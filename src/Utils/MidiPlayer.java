package Utils;

import javax.sound.midi.*;

import Model.Music.PitchSetWithDuration;

public class MidiPlayer {
	
	private static MidiPlayer singleton = null;

    ChannelData _channels[];
    ChannelData _cc;    // current channel
    Instrument instruments[];
 	Synthesizer synthesizer;
    Sequencer sequencer;
    Sequence sequence;
	private CollectionPlayer _collection_player;
	private NotePlayer _note_player;
	private float _tick_length;

	public static MidiPlayer getInstance(){
		if (singleton == null){
			singleton = new MidiPlayer();
		}
		return singleton;
	}

	public MidiPlayer() {
    	midi_open();
    }
    
  	public void midi_open() {
 		try {
 	        if (synthesizer == null) {
 	            if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
 	                System.out.println("getSynthesizer() failed!");
 	                return;
 	            }
 	        } 
 	        synthesizer.open();
 	        sequencer = MidiSystem.getSequencer();
 	        sequence = new Sequence(Sequence.PPQ, 10);
 	    } catch (Exception ex) { ex.printStackTrace(); return; }
        Soundbank sb = synthesizer.getDefaultSoundbank();
		if (sb != null) {
	            instruments = synthesizer.getDefaultSoundbank().getInstruments();
	            synthesizer.loadInstrument(instruments[0]);
	        }
	        MidiChannel midiChannels[] = synthesizer.getChannels();
	        _channels = new ChannelData[midiChannels.length];
	        for (int i = 0; i < _channels.length; i++) {
	            _channels[i] = new ChannelData(midiChannels[i], i);
	        }
	        _cc = _channels[0];


	 	}
  	
    public void midi_close() {
        
    	if (synthesizer != null) {
            synthesizer.close();
        }
        if (sequencer != null) {
            sequencer.close();
        }

        sequencer = null;
        synthesizer = null;
        instruments = null;
        _channels = null;
    }
        

	public Synthesizer getSynthesizer() {
		return synthesizer;
	}

	public ChannelData get_cc() {
		return _cc;
	}
	
	public void playNote(int channel, int pitch, int velocity) {
        _cc = _channels[channel];
        _cc.channel.noteOn(pitch, velocity);
	}
	
	public void shutNote(int channel, int pitch){
        _cc = _channels[channel];
        _cc.channel.noteOff(pitch);		
	}
	
	public void playMusicCollection(PitchSetWithDuration mc){
		
		if (_collection_player != null && _collection_player.isAlive()){
			_collection_player.note_off();	
		}
		_collection_player = new CollectionPlayer(this,mc);
		_collection_player.start();
	}
	
	public void playNote(int channel, int pitch, int velocity, long duration) {

		if (_note_player != null && _note_player.isAlive()){
			_note_player.note_off();	
		}
		_note_player = new NotePlayer(this,pitch,duration);
		_note_player.start();

//        _cc = _channels[channel];
//        _cc.channel.noteOn(pitch, velocity);
	}

	// MetaEventListener role
	public void meta( MetaMessage event ) {
	  if ( event.getType() == 47 ) { // end of stream
	     sequencer.stop();
	     sequencer.close();
	     System.exit( 0 );
	}} // meta
	
    public float get_tick_length() {
		return _tick_length;
	}

	public void set_tick_length(float _tick_length) {
		this._tick_length = _tick_length;
	}



}
