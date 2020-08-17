package Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import Model.Music.PitchSetStream;
import Model.Music.PitchSetWithDuration;
import Utils.Table;


public class SquareGridCoordPath extends TreeMap<Long,SquareGridCoordList>{
//public class SquareGridCoordPath extends HashMap<Long,SquareGridCoordList>{
	
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<Long> _keys;

	public SquareGridCoordPath(){
		super();
	}
	
	public SquareGridCoordPath(SquareGridCoordPath path){
		super();
		for (long l : path.keySet()){
			SquareGridCoordList coord_set = new SquareGridCoordList();
			for (int[] i : path.get(l)){
				int[] t = new int[i.length];
				for (int j = 0;j<i.length;j++) {
					t[j]=i[j];
				}
				coord_set.add(t);
			}
			this.put(l, coord_set);
		}
	}
	
//	public SquareGridCoordList get_last_non_empty_coord(){
//		System.out.println("SquareGridCoordList : "+this);
//		long tmp_key = lastKey();
//		while(get(tmp_key).isEmpty()){
//			tmp_key = lowerKey(tmp_key);
//		}
//		return get(tmp_key);
//	}
	
	public void inversion(){
		for (long l : keySet()){
			if (get(l).size()>0){
				for (int[] c : get(l)){
					for (int i = 0;i<c.length;i++) c[i]=c[i]*(-1);
				}
			}
		}
	}

	public SquareGridCoordPath get_translated_path(TranslationInTonnetz translation_vector) {
		SquareGridCoordPath new_coord_path = new SquareGridCoordPath();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (long key : key_list){
			new_coord_path.put(key, get(key).get_translated_coord_set(translation_vector));
		}
		return new_coord_path;
	}
		
	protected void build_keys(){
		_keys = new ArrayList<Long>(keySet());
		Collections.sort(_keys);
	}
	
	// the parameter rotation = {0,1,2,3,4,5}. Angle[rotation] = rotation*(-Pi/3). => rotation dans le sens des éguilles d'une montre
	// Rotation par rapport au centre [0,0]
	public void rotate(int rotation){
		CoordMappingTree coord_mapping_tree = new CoordMappingTree();
		for (Long key : keySet()) {
			coord_mapping_tree.put(key, get(key).rotate(rotation));
		}
	}
	
	// translation parameters : north and north-east
	public void translate(int n, int ne){
		for (Long key : keySet()) {
			get(key).translate(n,ne);
		}
	}
	
	// Construction d'un TonnetzCoordPath filtrant uniquement les CoordSet de durée supérieure à r (par exemple r = 10 miditicks)
	// Les durées des CoordPath conservées sont agrandies (on ajoute les durées des résidus) pour préserver la durée totale du fichier	
	// NOT YET IMPLEMENTED BECAUSE CoordSet has no duration (contrary to PitchSetWithDuration)

//	public TonnetzCoordPath rounded_TonnetzCoordPath(int r){
//	
//		TonnetzCoordPath tonnetz_coord_path = new TonnetzCoordPath();
//		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
//		for (int i=0;i<key_list.size();i++){
//			long key = key_list.get(i);
//			TonnetzCoordSet coord_set = get(key);
//			if (coord_set.get_duration() < r && i!=0){
//				tonnetz_coord_path.lowerEntry(key).getValue().grow_duration(coord_set.get_duration());
//			} else {
//				tonnetz_coord_path.put(key, coord_set);
//			}
//		}		
//		return tonnetz_coord_path;
//
//	}
	

//	public TonnetzPath get_inverted_path(){
//		TonnetzPath new_path = new TonnetzPath(this);
//		new_path.path_inversion();
//		return new_path;
//	}
	
}
