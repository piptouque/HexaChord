package Utils;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Path.PathTransformation;
import Path.SquareGridCoordPath;

public abstract class SequenceTransformation {

	static public Sequence inversion(Sequence sequence){
		Sequence new_seq = new_sequence(sequence);
		
    	Track track;
    	MidiEvent event;
    	MidiMessage message;
    	for (int i = 0 ; i<sequence.getTracks().length ; i++){
    		track = sequence.getTracks()[i];
    		new_seq.createTrack();
    		for (int j = 0;j<track.size();j++){
    			event = track.get(j);
    			message = event.getMessage();
    			if ( message instanceof ShortMessage ) {
    				if (((ShortMessage) message).getCommand() == ShortMessage.NOTE_ON || ((ShortMessage) message).getCommand() == ShortMessage.NOTE_OFF){
    					ShortMessage new_mess = new ShortMessage();
    					try {
    						// diffŽrentes transfos possibles
							//new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), transfo_pitch(((ShortMessage) message).getData1()), ((ShortMessage) message).getData2());
    						//new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), transfo_pitch(((ShortMessage) message).getData1()), ((ShortMessage) message).getData2());
    						new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), inversion_pitch(((ShortMessage) message).getData1(),0), ((ShortMessage) message).getData2());
						} catch (InvalidMidiDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							new_mess = (ShortMessage) message;
						}
    					new_seq.getTracks()[i].add(new MidiEvent(new_mess,event.getTick()));
    				} else {
    					new_seq.getTracks()[i].add(event);
    				}
    			} else {
        			new_seq.getTracks()[i].add(event);
    			}
    		}
    	}
		
		return new_seq;
	}
	
	static public Sequence new_sequence(Sequence sequence){
		Sequence new_seq = null;
		try {
			new_seq = new Sequence(sequence.getDivisionType(), sequence.getResolution());
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return new_seq;
	}
	
    static public int inversion_pitch(int n, int i){
    	return 12*(n/12)+(12-(n%12));
    }
    
    static public Sequence inverted_path(Sequence sequence, PitchSetStream stream, SquareGridCoordPath new_path, PlanarUnfoldedTonnetz tonnetz){
		Sequence new_seq = new_sequence(sequence);
		
    	Track track;
    	MidiEvent event;
    	MidiMessage message;
    	for (int i = 0 ; i<sequence.getTracks().length ; i++){
    		track = sequence.getTracks()[i];
    		new_seq.createTrack();
    		
    		// Tampon dans lequel demeurent les pitchs entre les dates NOTE_ON et NOTE_OFF [pitch initial|pitch transformŽ]
		ArrayList<int[]> pitch_playing = new ArrayList<int[]>();
		int[] pitch_to_remove = null;
    		
    		for (int j = 0;j<track.size();j++){
    			event = track.get(j);
    			message = event.getMessage();
    			if ( message instanceof ShortMessage ) {
    				System.out.println(" T tick : "+event.getTick()+"message : "+MessageInfo.toString(message));
    				if (((ShortMessage) message).getCommand() == ShortMessage.NOTE_ON || ((ShortMessage) message).getCommand() == ShortMessage.NOTE_OFF){
    					ShortMessage new_mess = new ShortMessage();
    					if (((ShortMessage) message).getCommand() == ShortMessage.NOTE_OFF || (((ShortMessage) message).getData2() == 0)){
    						for (int[] p : pitch_playing){
    							//System.out.println("p[0]:"+p[0]+" pitch : "+((ShortMessage) message).getData1());
    							if (p[0]==((ShortMessage) message).getData1()){
    								pitch_to_remove = p;
    	        					try {
    	        						// diffŽrentes transfos possibles
    	        						//new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), inversion_pitch(((ShortMessage) message).getData1(),0), ((ShortMessage) message).getData2());
    	        						new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), p[1], ((ShortMessage) message).getData2());
    	    						} catch (InvalidMidiDataException e) {
    	    							// TODO Auto-generated catch block
    	    							e.printStackTrace();
    	    							new_mess = (ShortMessage) message;
    	    						}
    							}
    						}
    						assert (pitch_to_remove != null) : "NOTE_OFF pitch not found in the pitch_playing table";
    						pitch_playing.remove(pitch_to_remove);
    						pitch_to_remove = null;
    					} else {
    						int old_pitch = ((ShortMessage) message).getData1();
    						int new_pitch = 0;//stream.get_new_pitch(old_pitch, event.getTick(), new_path, tonnetz);
        					pitch_playing.add(new int[]{old_pitch,new_pitch});
        					try {
        						// diffŽrentes transfos possibles
        						//new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), inversion_pitch(((ShortMessage) message).getData1(),0), ((ShortMessage) message).getData2());
        						new_mess.setMessage(((ShortMessage) message).getCommand(), ((ShortMessage) message).getChannel(), new_pitch, ((ShortMessage) message).getData2());
    						} catch (InvalidMidiDataException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    							new_mess = (ShortMessage) message;
    						}

    					}
    					
    					
    					new_seq.getTracks()[i].add(new MidiEvent(new_mess,event.getTick()));
    				} else {
    					new_seq.getTracks()[i].add(event);
    				}
    			} else {
        			new_seq.getTracks()[i].add(event);
    			}
    		}
    	}
		System.out.println("FIN inverted path");
		return new_seq;

    }
    
    private static int strategy = 1;	// On prend le pitch le plus proche de l'ancien pitch
  
    public static Sequence test_modify(Sequence original_sequence,PathTransformation path_transformation){
    	Sequence new_sequence = new_sequence(original_sequence);
    	Track original_track;
    	MidiEvent original_event;
    	MidiMessage original_message;
    	
    	for (int i = 0 ; i<original_sequence.getTracks().length ; i++){
    		original_track = original_sequence.getTracks()[i];
    		new_sequence.createTrack();
    		
    		// Tampon dans lequel demeurent les pitchs entre les dates NOTE_ON et NOTE_OFF [pitch initial|pitch transformŽ]
    		ArrayList<int[]> playing_pitchs = new ArrayList<int[]>();

    		for (int j = 0;j<original_track.size();j++){
    			original_event = original_track.get(j);
    			original_message = original_event.getMessage();
    			long tick = original_event.getTick();
    			if ( original_message instanceof ShortMessage ) {
    				if (((ShortMessage) original_message).getCommand() == ShortMessage.NOTE_ON){
    					int original_pitch = ((ShortMessage) original_message).getData1();
//    					System.out.println("tick : "+tick);
//    					System.out.println("original pitch : "+((ShortMessage) original_message).getData1());
    					int new_pitch = path_transformation.get_new_pitch(original_pitch, tick);
    					if (new_pitch != 0){	// new_pitch = 0 si le pitch n'a pas ŽtŽ trouvŽ ˆ cette position (pas normal, erreur ˆ trouver)
        					playing_pitchs.add(new int[]{original_pitch,new_pitch});    						
    					}
    					
    					ShortMessage new_message = new ShortMessage();
    					try {
							new_message.setMessage(ShortMessage.NOTE_ON, ((ShortMessage) original_message).getChannel(), new_pitch, ((ShortMessage) original_message).getData2());
						} catch (InvalidMidiDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					new_sequence.getTracks()[i].add(new MidiEvent(new_message,tick));
    					
    				}
    				
    				if (((ShortMessage) original_message).getCommand() == ShortMessage.NOTE_OFF){
    					int original_pitch = ((ShortMessage) original_message).getData1();
    					int pitch_to_turn_off=0;
    					int[] playing_pitch_to_remove = null;
    					for (int[] playing_pitch : playing_pitchs){
    						if(playing_pitch[0]==original_pitch){
    							playing_pitch_to_remove = playing_pitch;
    							pitch_to_turn_off = playing_pitch[1]; 
    						}
    					}
    					assert playing_pitch_to_remove != null : "cant turn OFF original pitch "+original_pitch+" because it is not played";
    					playing_pitchs.remove(playing_pitch_to_remove);
    					
    					ShortMessage new_message = new ShortMessage();
    					try {
							new_message.setMessage(ShortMessage.NOTE_OFF, ((ShortMessage) original_message).getChannel(), pitch_to_turn_off, ((ShortMessage) original_message).getData2());
						} catch (InvalidMidiDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					new_sequence.getTracks()[i].add(new MidiEvent(new_message,tick));
    				}
    				
    				if (((ShortMessage) original_message).getCommand() != ShortMessage.NOTE_OFF && ((ShortMessage) original_message).getCommand() != ShortMessage.NOTE_ON){
    					new_sequence.getTracks()[i].add(original_event);
    				}

    			} else {
    				new_sequence.getTracks()[i].add(original_event);
    			}
    		}
    	}

    	return new_sequence;
    }
}
