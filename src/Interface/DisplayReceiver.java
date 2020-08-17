package Interface;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import Main.HexaChord;
//import Utils.MessageInfo;
import Model.Music.MidiPlayerListener;
import Model.Music.Parameters;
import Model.Music.PitchSet;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;
import Utils.MessageInfo;
import Utils.MidiFilePlayer;

public class DisplayReceiver implements Receiver {

	private HexaChord _h;
	private Parameters _p;
	private MidiFilePlayer _midi_file_player;
	private long _last_time_millis;
	private long _birth_date_millis;
	private boolean _listens_KB;
	private boolean _sustain_activated=false;
	private PitchSet _current_pushed_keys;	// for KB listening. to take sustain into account
	
	private final Collection<MidiPlayerListener> _midi_player_listeners = new ArrayList<MidiPlayerListener>();
	
	public DisplayReceiver(HexaChord hexachord, boolean listens_KB) {
		_h = hexachord;
		_p = Parameters.getInstance();
		_midi_file_player = MidiFilePlayer.getInstance();

		_listens_KB=listens_KB;
		if (_listens_KB){
			_birth_date_millis=System.currentTimeMillis();
			_current_pushed_keys=new PitchSet();
		}
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		//System.out.println("HELLO "+this+" "+MessageInfo.toString(message));
		if(_midi_file_player.get_sequencer().isRunning() && !_midi_file_player.get_sequencer().isRecording() && !_listens_KB){
			fire_midi_player_moved(MidiFilePlayer.getInstance().get_sequencer().getTickPosition());
		} else {
			// MIDI messages coming from keyboard
			if (HexaChord.getInstance()._external_KB_ON && _listens_KB){
				long current_millis = System.currentTimeMillis();
				//System.out.println("\nage: "+get_age()+" time since last message = "+(current_millis-_last_time_millis));
				_last_time_millis = current_millis;
				
				if ( message instanceof ShortMessage ) {
					
					ShortMessage short_message = (ShortMessage) message;
					if (short_message.getCommand()==ShortMessage.CONTROL_CHANGE && short_message.getData1() == 64){
						if (short_message.getData2()>63) _sustain_activated=true;
						else {
							_sustain_activated=false;
							PosPitchSetStream stream = Parameters.getInstance().get_colStream();
							long new_tick = get_age();
							PitchSetWithDuration new_pswd = new PitchSetWithDuration(new PitchSet(_current_pushed_keys),stream.lastEntry().getValue().get_duration());
							stream.put(new_tick, new_pswd);
							stream.set_current_key(new_tick);
							// + désactiver toutes les notes qui ont eu leur note off
						}
					} else {
						if (short_message.getChannel()+1 != 10) {
							PosPitchSetStream stream = Parameters.getInstance().get_colStream();
							//long new_tick = stream.get_current_key()+1;
							long new_tick = get_age();
							PitchSetWithDuration new_pswd = new PitchSetWithDuration(stream.lastEntry().getValue());
							if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
								_current_pushed_keys.add(short_message.getData1());
								new_pswd.add(short_message.getData1());
								stream.put(new_tick, new_pswd);
								stream.set_current_key(new_tick);
							}
							if ((short_message.getCommand() == ShortMessage.NOTE_OFF || short_message.getData2() == 0)) {
								_current_pushed_keys.remove(short_message.getData1());
								//System.out.println("_current_pushed_keys : "+_current_pushed_keys);
								if (!_sustain_activated){
									new_pswd.remove(short_message.getData1());	
									stream.put(new_tick, new_pswd);
									stream.set_current_key(new_tick);
								} 
							}
							//fire_midi_player_moved(new_tick);
						}						
					}
				}
			}
		}
		
		_h.frames_repaint();
	}
	
	public boolean is_sustain(){
		return _sustain_activated;
	}
	
	public long get_age(){
		return System.currentTimeMillis()-_birth_date_millis;
	}
	
	public void add_midi_player_listener(MidiPlayerListener listener){
		_midi_player_listeners.add(listener);
	}
	
	public void remove_midi_player_listener(MidiPlayerListener listener){
		_midi_player_listeners.remove(listener);
	}
	
	protected void fire_midi_player_moved(long new_tick){
		for (MidiPlayerListener listener : _midi_player_listeners){
			listener.tick_change(new_tick);
		}
	}
	
}
