package Path;

import java.util.ArrayList;

import Model.Music.PitchClassSet;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;
import Utils.MidiPlayer;

public class PosSquareGridCoordPath extends SquareGridCoordPath{
	
	private static final long serialVersionUID = 1L;
	private long _current_key;
	
	public PosSquareGridCoordPath(SquareGridCoordPath tonnetz_coord_path){
		super(tonnetz_coord_path);
		build_keys();
		_current_key = firstKey();
	}
	
	public int get_pos() {
		return _keys.indexOf(_current_key);
	}

	public SquareGridCoordList get_current_CoordSet(){
		return get(_current_key);
	}
	
	
//	// renvoie un CoordSet passé ou futur de p positions
	public SquareGridCoordList get_relative_CoordSet(int p){

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
	
	
//	// Si la clé n'existe pas, on impose la clé inférieur la plus élevée ( la plus proche dans le passé)
	public void set_current_key(long key) {
		_current_key = floorKey(key);
	}
	
	public void inc_current_key() {
		if (higherKey(_current_key) != null) _current_key = higherKey(_current_key);
	}

	public void dec_current_key() {
		if (lowerKey(_current_key) != null) _current_key = lowerKey(_current_key);
	}
	
	// Construction d'une PosColStream filtrant uniquement les collections de durée supérieure à r (par exemple r = 10 miditicks)
	// Les durées des collections conservées sont agrandies (on ajoute les durées des résidus) pour préserver la durée totale du fichier	
	// NOT YET IMPLEMENTED BECAUSE CoordSet has no duration (contrary to PitchSetWithDuration)
	
//	public PosTonnetzCoordPath rounded_PosTonnetzCoordPath(int r){
//		PosTonnetzCoordPath tonnetz_coord_path = new PosTonnetzCoordPath(rounded_ColStream(r));
//		tonnetz_coord_path.set_current_key(_current_key);
//		return tonnetz_coord_path;
//	}

	
	
}
