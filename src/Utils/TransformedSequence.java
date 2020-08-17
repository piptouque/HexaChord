package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import Model.Music.Interval;
import Path.PitchClassMappingTree;

public class TransformedSequence extends Sequence {

	private String _name;
	
	public TransformedSequence(float arg0, int arg1, String name) throws InvalidMidiDataException {
		super(arg0, arg1);
		_name = name;
		// TODO Auto-generated constructor stub
	}

//	public TransformedSequence(Sequence origine_seq,float arg0, int arg1) throws InvalidMidiDataException{
//		super(arg0, arg1);
//		Track origine_track;
//		MidiMessage origine_message;
//		for (int tr=0;tr<origine_seq.getTracks().length;tr++){
//			createTrack();
//			origine_track = origine_seq.getTracks()[tr];
//			for (int ev = 0;ev<origine_track.size();ev++){
//				origine_message = origine_track.get(ev).getMessage();
//				getTracks()[tr].add(new MidiEvent((MidiMessage) origine_message.clone(),origine_track.get(ev).getTick()));
//			}
//		}
//	}
	
	public static TransformedSequence new_sequence(Sequence sequence, String name){
		TransformedSequence new_seq = null;
		try {
			new_seq = new TransformedSequence(sequence.getDivisionType(), sequence.getResolution(), name);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		return new_seq;
	}

	
	private static int STRATEGY = 1;	// On prend le pitch le plus proche de l'ancien pitch
	
	public static TransformedSequence transforme_with_pc_mapping(Sequence origine_sequence, PitchClassMappingTree pitch_class_mapping,String name){
		//System.out.println("MAPPING : "+pitch_class_mapping);
		TransformedSequence new_sequence = new_sequence(origine_sequence,name);
    	Track original_track;
    	MidiEvent original_event;
    	MidiMessage original_message;
    	int ignored_NOTE_ON_events = 0;
    	for (int i = 0 ; i<origine_sequence.getTracks().length ; i++){
    		original_track = origine_sequence.getTracks()[i];
    		new_sequence.createTrack();
    		
    		// Tampon dans lequel demeurent les pitchs entre les dates NOTE_ON et NOTE_OFF [pitch initial|pitch transform�]
    		ArrayList<int[]> playing_pitchs = new ArrayList<int[]>();

    		for (int j = 0;j<original_track.size();j++){
    			original_event = original_track.get(j);
    			original_message = original_event.getMessage();
    			long tick = original_event.getTick();
    			if ( original_message instanceof ShortMessage && (((ShortMessage) original_message).getChannel() != 9)) {
    				//if (((ShortMessage) original_message).getCommand() == ShortMessage.NOTE_ON){
    				if (((ShortMessage) original_message).getCommand()==ShortMessage.NOTE_ON && ((ShortMessage) original_message).getData2() != 0){
    					int original_pitch = ((ShortMessage) original_message).getData1();
//    					System.out.println("original pitch : "+((ShortMessage) original_message).getData1());
    					int new_pitch_class = pitch_class_mapping.get_destination_pc(tick, original_pitch%12);
    					if (new_pitch_class != -1){
        					int new_pitch = pitch_class_to_pitch(new_pitch_class,original_pitch);
        					playing_pitchs.add(new int[]{original_pitch,new_pitch});
        					ShortMessage new_message = new ShortMessage();
        					try {
    							new_message.setMessage(ShortMessage.NOTE_ON, ((ShortMessage) original_message).getChannel(), new_pitch, ((ShortMessage) original_message).getData2());
    						} catch (InvalidMidiDataException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
        					new_sequence.getTracks()[i].add(new MidiEvent(new_message,tick));

    					} else {
    						// NOTE ON ignor�s soit car absentes de l'espace de d�part
    						ignored_NOTE_ON_events++;
    					}
    				}
    				
    				//if (((ShortMessage) original_message).getCommand() == ShortMessage.NOTE_OFF){
    				if (((ShortMessage) original_message).getCommand() == ShortMessage.NOTE_OFF || (((ShortMessage) original_message).getCommand()==ShortMessage.NOTE_ON && ((ShortMessage) original_message).getData2() == 0)){
    					int original_pitch = ((ShortMessage) original_message).getData1();
    					int pitch_to_turn_off=0;
    					int[] playing_pitch_to_remove = null;
    					for (int[] playing_pitch : playing_pitchs){
    						if(playing_pitch[0]==original_pitch){
    							playing_pitch_to_remove = playing_pitch;
    							pitch_to_turn_off = playing_pitch[1]; 
    						}
    					}
//    					assert playing_pitch_to_remove != null : "cant turn OFF original pitch "+original_pitch+" because it is not played";
    					if (playing_pitch_to_remove != null){
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
    				}
    				if (((ShortMessage) original_message).getCommand() != ShortMessage.NOTE_OFF && ((ShortMessage) original_message).getCommand() != ShortMessage.NOTE_ON){
    					new_sequence.getTracks()[i].add(original_event);
    				}

    			} else {
    				new_sequence.getTracks()[i].add(original_event);
    			}
    		}
    	}
    	if (ignored_NOTE_ON_events>0) System.err.println("failed to map "+ignored_NOTE_ON_events+" NOTE ON");
    	return new_sequence;
	}
	
	// strategie 1 : on prend le pitch le plus proche du pitch original
	//private static int strategy = 1;
	
	public static int pitch_class_to_pitch(int pitch_class, int original_pitch){
		int original_pitch_class = original_pitch%12;
		if (STRATEGY == 1){
			int mi_interval = Interval.MI(original_pitch_class, pitch_class, 12);
			if ((original_pitch+mi_interval)%12 == pitch_class){
				if (original_pitch+mi_interval <= 108){
					return original_pitch+mi_interval;					
				} else {
					return original_pitch+mi_interval-12;
				}
			} 
			if ((original_pitch-mi_interval)%12 == pitch_class){
				if (original_pitch-mi_interval >= 21){
					return original_pitch-mi_interval;
				} else {
					return original_pitch-mi_interval+12;
				}
			}			
		} 
		assert false : "ERROR ! pitch class : "+pitch_class+" original_pc : "+original_pitch_class;
		//System.err.println("ERROR ! pitch class : "+pitch_class+" original_pitch : "+original_pitch);
		return 0;			
	}
	
	public void export_as_midi(){
		try {
			MidiSystem.write(this, MidiSystem.getMidiFileTypes(this)[0], create_new_file(_name));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public File create_new_file(String file_name){
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		pp = pp+"/out/transformed_midi/"+file_name+"."+"midi";

		File file = new File(pp);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return file;
	}


}
