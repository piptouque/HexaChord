package Model.Music;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.sound.midi.Sequence;

import Utils.MidiPlayer;

public class PosPitchSetStream extends PitchSetStream implements MidiPlayerListener {

	private static final long serialVersionUID = 1L;
	private long _current_key;
	
	private final Collection<PosPitchSetStreamListener> _pos_pitch_set_stream_listeners = new ArrayList<PosPitchSetStreamListener>();

	public PosPitchSetStream(PitchSetStream s){
		super(s.get_name());
		s.struct_verification();
		for (Map.Entry<Long, PitchSetWithDuration> e : s.entrySet()){
			put(e.getKey(),e.getValue());
		}
		build_keys();
		_name = s._name;
		_current_key = firstKey();
		fire_pos_moved();
	}
				
	public int get_pos() {
		return _keys.indexOf(_current_key);
	}

	public PitchSetWithDuration get_current_col(){
		return get(_current_key);
	}
	
	public PitchClassSet get_current_STChord(){
		return get(_current_key).to_PitchClassSet(12);
	}
	
//	// renvoie un accord passé ou futur de p positions
	public PitchSetWithDuration get_relative_Coll(int p){

		long tmp_key = _current_key;

		if (p<0){
			for (int i=-1;i==p;i--){
				if (lowerKey(tmp_key) != null){
					tmp_key = lowerKey(tmp_key);					
				}
			}
			return get(tmp_key);
		}
		
		
		if (p>0){
			for (int i=1;i==p;i++){
				if (higherKey(tmp_key) != null){
					tmp_key = higherKey(tmp_key);					
				}
			}
			return get(tmp_key);
		}
		
		return get(_current_key);
//		if (get_pos()+p<0) return get(firstKey());
//		if (get_pos()+p>size()) return get(lastKey());
//		return get_col_with_pos((get_pos()+p));
	}
	
	public PitchClassSet get_relative_STChord(int p){
		return get_relative_Coll(p).to_PitchClassSet(12);
	}
	
	public void play_current_chord(MidiPlayer m){
		get_current_col().playColl(m);
	}

	public ArrayList<Long> get_keys() {
		return _keys;
	}

	public long get_current_key() {
		return _current_key;
	}
	
	public boolean is_last_key(){
		return (get_current_key() == lastKey());
	}
	
	
	// Si la clé n'existe pas, on prend la clé inférieur la plus élevée ( la plus proche dans le passé)
	public void set_current_key(long key) {
		_current_key = floorKey(key);
		fire_pos_moved();
	}
	
	public void inc_current_key() {
		if (higherKey(_current_key) != null) _current_key = higherKey(_current_key);
		fire_pos_moved();
	}

	public void dec_current_key() {
		if (lowerKey(_current_key) != null) _current_key = lowerKey(_current_key);
		fire_pos_moved();
	}
	
	
	
	// Construction d'une PosColStream filtrant uniquement les collections de durée supérieure à r (par exemple r = 10 miditicks)
	// Les durées des collections conservées sont agrandies (on ajoute les durées des résidus) pour préserver la durée totale du fichier	
	
	public PosPitchSetStream rounded_PosColStream(int r){
		PosPitchSetStream stream = new PosPitchSetStream(rounded_ColStream(r));
		stream.set_current_key(_current_key);
		return stream;
	}


	public long get_duration_in_seconds() {
		long micro_seconds = get_duration_in_microseconds();
		System.out.println("microseconds : "+micro_seconds);
		return micro_seconds/1000000;
	}

	@Override
	public void tick_change(long new_tick) {
		_current_key = floorKey(new_tick);
		fire_pos_moved();
	}
	
	public void add_pos_pitch_set_stream_listener(PosPitchSetStreamListener listener){
		_pos_pitch_set_stream_listeners.add(listener);
	}
	
	public void remove_pos_pitch_set_stream_listener(PosPitchSetStreamListener listener){
		_pos_pitch_set_stream_listeners.remove(listener);
	}
	
	protected void fire_pos_moved(){
		for (PosPitchSetStreamListener listener : _pos_pitch_set_stream_listeners){
			listener.pos_change(_current_key);
		}
	}


}
