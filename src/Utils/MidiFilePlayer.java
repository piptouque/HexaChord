package Utils;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.swing.JFileChooser;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;

import Interface.DisplayReceiver;
import Main.HexaChord;
import Model.Music.Parameters;

public class MidiFilePlayer {

	Sequencer _sequencer = null;
	Sequence _sequence = null;
	String _sequence_name;
	Track [] _tracks;
    Transmitter _playback_transmitter;
    javax.sound.midi.Receiver _record_receiver;
    //NoteReceiver _display_receiver;
    
    private static MidiFilePlayer singleton = null;

    public MidiFilePlayer(){
    }
    
	public static MidiFilePlayer getInstance(){
		if (singleton == null){
			singleton = new MidiFilePlayer();
		}
		return singleton;
	}


  	public void init(File file, DisplayReceiver r){
  		
  		reset_sequencer();
  		midi_close();
  		setSequenceAndSequencer(file);
  		open_sequencer();
  		//Parameters.getInstance().set_sequencer(_sequencer);
  		set_playback_transmitter();
  		connect_playback_transmitter(r);
  	}

  	// init without changing the sequence
  	public void init(DisplayReceiver r){
  		reset_sequencer();
  		midi_close();
  		_sequencer = get_system_sequencer();
  		set_sequence_in_sequencer(_sequencer, _sequence);
  		open_sequencer();
  		//Parameters.getInstance().set_sequencer(_sequencer);
  		set_playback_transmitter();
  		connect_playback_transmitter(r);
  	}

  	public static URL getURLfromFileName(String file){
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		pp = pp+"/input_files/"+file;
		
		return getURLfromPath(pp);
  	}
  	
  	public static URL getURLfromPath(String path){
		String pp = null;
		pp = "file://"+path;
        URL url = null;
		try {
			url = new URL(pp);
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return url;
  	}
  	
  	public static Sequence getSequenceFromFile(File file){
  		Sequence seq = null;
  		try {
        	
        	System.out.println("file : "+file);
        	seq = MidiSystem.getSequence(file);
        	} catch (InvalidMidiDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
        	} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error : MIDI file "+file+" doesn't exists");
			System.exit(1);
			e.printStackTrace();
		} 
  		return seq;

  	}
  	
  	public void set_playback_transmitter(){
  		_playback_transmitter = get_sequencer_new_transmitter();
  	}
  	
  	public void connect_playback_transmitter(javax.sound.midi.Receiver receiver){
  		_playback_transmitter.setReceiver(receiver);
  	}
  	
  	public void set_record_receiver(){
  		_record_receiver = get_sequencer_new_receiver();
  	}
  	
  	public javax.sound.midi.Receiver get_record_receiver(){
  		return _record_receiver;
  	}
  	
  	public void close_record_receiver(){
  		_record_receiver.close();
  	}
  	
  	public Transmitter get_sequencer_new_transmitter(){
  		Transmitter transmitter = null;
  		try {
			transmitter = _sequencer.getTransmitter();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return transmitter;
  	}
  	  	
  	public javax.sound.midi.Receiver get_sequencer_new_receiver(){
  		javax.sound.midi.Receiver receiver = null;
  		try {
			receiver = _sequencer.getReceiver();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return receiver;
  	}
  	
  	public static Sequencer get_system_sequencer(){
  		try {
			return MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
  	}
  	
  	public void set_sequence_in_sequencer(Sequencer sequencer, Sequence sequence){
  		try {
			sequencer.setSequence(sequence);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
  		_sequence = sequence;
  	}
  	
  	public void set_sequence_in_sequencer(Sequence sequence,String sequence_name){
  		if (_sequencer == null) set_sequencer();
  		_sequence_name = sequence_name;
  		set_sequence_in_sequencer(_sequencer,sequence);
  		_sequencer.setTickPosition(0);
  	}
  	
//  	public void setSequenceAndSequencer(String s){
//  		URL url = getURLfromFileName(s);
//  		_sequence = getSequenceFromURL(url);        
//  		_sequencer = get_system_sequencer();
//  		set_sequence_in_sequencer(_sequencer, _sequence);        
//        //System.out.println("tempo : "+_sequencer.getTempoInBPM()+" BPM"+" - "+_sequencer.getTempoInMPQ()+" MPQ");
//  	}

  	
  	public void setSequenceAndSequencer(File file){
  		_sequence = getSequenceFromFile(file);
  		set_sequencer();
  		set_sequence_in_sequencer(_sequencer, _sequence);        
        //System.out.println("tempo : "+_sequencer.getTempoInBPM()+" BPM"+" - "+_sequencer.getTempoInMPQ()+" MPQ");
  	}
  	
  	public void set_sequencer(){
  		_sequencer = get_system_sequencer();
  	}

  	public void open_sequencer(){
        try {
			_sequencer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}

  	}
  	  	
  	
//  	public void edit_sequence(ColStream stream, TonnetzCoordPath new_path, PlanarUnfoldedTonnetz tonnetz){
//  		sequence = SequenceTransformation.inverted_path(sequence, stream, new_path, tonnetz);
//        try {
//			sequencer.setSequence(sequence);
//		} catch (InvalidMidiDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
//        try {
//			t = sequencer.getTransmitter();
//			t.setReceiver(_receiver);
//			_receiver.set_seq(sequencer);
//		} catch (MidiUnavailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//  	}

    public void midi_close() {
        
        if (_sequencer != null) {
            _sequencer.stop();
        	_sequencer.close();
        }
        _sequencer = null;
    }
  
//	public void play_file(long tick) {
//		
//		if (_sequencer.isRunning()) {
//			_sequencer.stop();	
//		}
//		else {
//			_sequencer.start();
//			_sequencer.setTickPosition(tick);
//		}
//	}

	
	public void stop_file() {
		if (_sequencer != null) {
			if (_sequencer.isRunning()) {
				_sequencer.stop();
			}
		}
	}
	
	public boolean is_running() {
		return _sequencer.isRunning();
	}
	public Sequencer get_sequencer() {
		return _sequencer;
	}
	public void reset_sequencer() {
		if (_sequencer != null){
			_sequencer.stop();
			_sequencer.setTickPosition(0);			
		}
	}

	public void jump(int n) {
		_sequencer.setTickPosition((int)(_sequence.getTickLength()*((float)n/100)));
		System.out.println("sequencer jumped");
	}

//	public NoteReceiver get_receiver() {
//		return _display_receiver;
//	}
	
    public Sequence get_sequence() {
		return _sequence;
	}
    
	public void export_sequence_as_midi(Boolean export_in_out_dir){
		export_sequence_as_midi(_sequence_name,export_in_out_dir);
    }

	public void export_sequence_as_midi(String file_name, Boolean export_in_out_dir){
		export_sequence_as_midi(file_name,_sequence,export_in_out_dir);
    }
	
	public static void export_sequence_as_midi(String file_name, Sequence sequence, Boolean export_in_out_dir){
		File file = create_new_file(file_name,export_in_out_dir);
		export_sequence_as_midi(file,sequence);
	}

	public static void export_sequence_as_midi(File file, Sequence sequence){
		try {
			MidiSystem.write(sequence, MidiSystem.getMidiFileTypes(sequence)[0],file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Midi file exported : "+file.getName());
	}

	public static File create_new_file(String file_name, Boolean out_dir){
		String pp = null;
		File file = null;
		if (out_dir){
			try {
				pp = (new File(".")).getCanonicalPath();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			pp = pp+"/out/midi/"+file_name+"."+"mid";
			file = new File(pp);
		} else {
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setSelectedFile(new File(file_name+".mid"));
			file = HexaChord.getInstance().open_file_save_window(jFileChooser);
		}
		
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return file;
	}
	
        
}
