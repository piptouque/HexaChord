package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.TreeMap;

import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Utils.Gcd;
import Utils.IntegerSet;

public class Z7PlanarUnfoldedTonnetz extends PlanarUnfoldedTonnetz {

	private Scale _scale;
	
	
	private Z7PlanarUnfoldedTonnetz(Z7FoldedGraphTonnetz folded_tonnetz, STIntervallicStructure is){
		assert (folded_tonnetz.get_scale().size()==7) : "The scale is not heptatonic";
		
		//_scale = folded_tonnetz.get_scale();
		_N=7;
		
		int[] generators = new int[]{1,2,4};
		//int[] generators = new int[]{2,2,3};
		//_generators = new TonnetzGen(generators);
		_generators = new TonnetzGen(is.get_list());
		
		//STIntervallicStructure is = new STIntervallicStructure(generators);
		for (TIChordComplex complex : TIChordComplex.getZ7Tonnetz_124_ChordComplexList()){
			if (folded_tonnetz.get_scale().toString().equals(complex.get_folded_graph_tonnetz().get_scale().toString())) _folded_complex = complex;
		}
		assert (_folded_complex != null) : "Folded complex not found ";

		_folded_graph_tonnetz = folded_tonnetz;
	}

	public Z7PlanarUnfoldedTonnetz(STIntervallicStructure is, Scale scale){
		assert (scale.size()==7) : "The scale is not heptatonic";
		_N=7;
		_scale = scale;
		_generators = new TonnetzGen(is.get_list());
		_folded_complex = null;
		_folded_graph_tonnetz = new Z7FoldedGraphTonnetz(scale,new IntegerSet(is.get_list()));
	}

	@Override
	public String toHTMLString(){
		StringBuffer s = new StringBuffer("<html>T<sub>["+_generators.get(0));
			s.append("hept");
		s.append("]</sub></html>");
		return s.toString();		
	}
	
	public Scale get_scale(){
		return _folded_graph_tonnetz._scale;
	}
	
	public String toString(){
		//if (_scale!=null) return _generators.toString()+" "+_scale;
		return _folded_graph_tonnetz._scale.toString(); 
	}
	
	static private TreeMap<TonnetzGen,Z7PlanarUnfoldedTonnetz> _tonnetze = new TreeMap<TonnetzGen,Z7PlanarUnfoldedTonnetz>();
	
//	static public Z7PlanarUnfoldedTonnetz getTonnetz(ArrayList<Integer> list) {
//		TonnetzGen k = new TonnetzGen(list);
//		Z7PlanarUnfoldedTonnetz t = _tonnetze.get(k);
//		if (t==null) {
//			t = new Z7PlanarUnfoldedTonnetz(list);
//			_tonnetze.put(t.get_generators(),t);
//		}
//		return t;
//	}

//	static public Z7PlanarUnfoldedTonnetz getTonnetz(int i1, int i2, int i3) {
//		ArrayList<Integer> l = new ArrayList<Integer>();
//		l.add(i1);
//		l.add(i2);
//		l.add(i3);
//		return getTonnetz(l);
//	}

//	static private ArrayList<Z7PlanarUnfoldedTonnetz> _z7TriangularUnfoldedTonnetzList;
//	
//	static public ArrayList<Z7PlanarUnfoldedTonnetz> getZ7TriangularUnfoldedTonnetzList() {
//		if (_z7TriangularUnfoldedTonnetzList == null) {
//			_z7TriangularUnfoldedTonnetzList = new ArrayList<Z7PlanarUnfoldedTonnetz>();
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(1,1,10));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(1,2,9));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(1,3,8));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(1,4,7));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(1,5,6));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(2,2,8));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(2,3,7));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(2,4,6));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(2,5,5));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(3,3,6));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(3,4,5));
//			_z7TriangularUnfoldedTonnetzList.add(getTonnetz(4,4,4));
//		}		
//		return _z7TriangularUnfoldedTonnetzList;
//	}

	
	private static ArrayList<Z7PlanarUnfoldedTonnetz> _Z7HexaTonnetzList;
	
	public static ArrayList<Z7PlanarUnfoldedTonnetz> getZ7HexaTonnetzList() {
		if (_Z7HexaTonnetzList == null) {
			_Z7HexaTonnetzList = new ArrayList<Z7PlanarUnfoldedTonnetz>();
			for (Z7FoldedGraphTonnetz folded_tonnetz : Z7FoldedGraphTonnetz.getZ7Folded_124_GraphTonnetzList()){
				_Z7HexaTonnetzList.add(new Z7PlanarUnfoldedTonnetz(folded_tonnetz,STIntervallicStructure.enum_SI_up_to_flip(3, 7).get(0)));
			}
		}		
		return _Z7HexaTonnetzList;
	}
	
	public static ArrayList<String> getZ7HexaTonnetzNameList() {
		ArrayList<String> name_list = new ArrayList<String>();
		for (Z7PlanarUnfoldedTonnetz t : getZ7HexaTonnetzList()) name_list.add(t.toString());
		return name_list;
	}

	public static String[] getZ7HexaTonnetzNameTable() {
		String [] name_table = new String[getZ7HexaTonnetzList().size()];
		for (int i=0;i<_Z7HexaTonnetzList.size();i++) name_table[i]=_Z7HexaTonnetzList.get(i).toString();
		return name_table;
	}

	public int xy_coord_to_pitch_class(int[] coords){
		assert (coords.length == 2) : "method coords_to_pitch implemented for XY coords only";
		return _folded_graph_tonnetz._scale.get_PC(Gcd.reduce(coords[0] * get_generator(0) - coords[1] * get_generator(2), _N));
	}

}
