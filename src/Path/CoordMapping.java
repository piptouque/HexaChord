package Path;

import Utils.Table;

public class CoordMapping {

	public int[] _old_coord;
	public int[] _new_coord;
	
	public CoordMapping(int[] old_coord, int[] new_coord){
		_old_coord = new int[]{old_coord[0],old_coord[1]};
		_new_coord = new int[]{new_coord[0],new_coord[1]};
//		_old_coord = old_coord.clone();
//		_new_coord = new_coord.clone();
	}
	
	public String toString(){
		return Table.toString(_old_coord)+"->"+Table.toString(_new_coord);
	}
}
