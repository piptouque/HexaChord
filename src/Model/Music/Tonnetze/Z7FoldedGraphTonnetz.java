package Model.Music.Tonnetze;

import java.util.ArrayList;

import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Utils.IntegerSet;


public class Z7FoldedGraphTonnetz extends FoldedGraphTonnetz{

	private static final long serialVersionUID = 1L;
	//protected static final int _N = 7;
	
	public Z7FoldedGraphTonnetz(Scale scale,IntegerSet generators){
		
		_scale = scale;
		_N = 7;
		_orbit_count = 1;
		for (int z7_interval : generators){
			add(z7_interval);
		}
	}
	
	private static ArrayList<STIntervallicStructure> _z7Folded_all_GraphTonnetzList;
	
	public static ArrayList<STIntervallicStructure> getZ7Folded_all_GraphTonnetzList() {
		if (_z7Folded_all_GraphTonnetzList == null) {
			_z7Folded_all_GraphTonnetzList = STIntervallicStructure.enum_SI_up_to_flip(3, 7);
		}
		return _z7Folded_all_GraphTonnetzList;
	}

	
	private static ArrayList<Z7FoldedGraphTonnetz> _z7Folded_124_GraphTonnetzList;
	
	public static ArrayList<Z7FoldedGraphTonnetz> getZ7Folded_124_GraphTonnetzList() {
		if (_z7Folded_124_GraphTonnetzList == null) {
			_z7Folded_124_GraphTonnetzList = new ArrayList<Z7FoldedGraphTonnetz>();
			IntegerSet z7_intervals = new IntegerSet();
			z7_intervals.add(1);
			z7_intervals.add(2);
			z7_intervals.add(4);
//			z7_intervals.add(2);
//			z7_intervals.add(2);
//			z7_intervals.add(3);
			for (int i=0;i<12;i++){
				_z7Folded_124_GraphTonnetzList.add(new Z7FoldedGraphTonnetz(new Scale(i,"major"),z7_intervals));
				_z7Folded_124_GraphTonnetzList.add(new Z7FoldedGraphTonnetz(new Scale(i,"minor"),z7_intervals));
			}
		}
		return _z7Folded_124_GraphTonnetzList;
	}
	
	public static Z7FoldedGraphTonnetz getZ7Folded_124_GraphTonnetz(int n, String mode){
		int m = 0;
		if (mode.compareTo("major") == 0){
			m=0;
		} else {
			if (mode.compareTo("minor") == 0){
				m=1;
			} else {
				assert false : "mode must be minor or major";
			}
		}
		return getZ7Folded_124_GraphTonnetzList().get((2*n)+m);
	}

	public boolean contains_pitch(int n){
		return _scale.contains(n);
	}

	@Override
	public boolean are_neighbor(int p1, int p2) {
		if (!(_scale.contains(p1%12)&&_scale.contains(p2%12))) {
			return false;
		}
		if(contains(_scale.get_MIstep_in_scale(p1%12, p2%12))){
			return true;
		}
		return false;
	}
	
	public String toString(){
		return to_string()+"-"+_scale.toString();
	}

}
