package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.Interval;
import Model.Music.IntervallicVector;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Utils.Gcd;
import Utils.IntegerSet;


public class Z12FoldedGraphTonnetz extends FoldedGraphTonnetz {

	private static final long serialVersionUID = 1L;
	
	protected int _gcd;

	public Z12FoldedGraphTonnetz(IntegerSet generators){
		
		addAll(generators);
		_N = 12;
		_scale = Scale.get_chromatic_scale();
		for(Integer n : this) {
			if (_gcd==0) _gcd = n; else _gcd = Gcd.gcd(_gcd, n);
		}
		_orbit_count = _gcd;
	}
	
	public IntervallicVector get_IntervalVector() {
		int M = (_N/2)-(_N%2);
		int iv[] = new int[M];
		for (int i : iv) iv[i]=0;
		for (int g : this) iv[Interval.MI(g, _N)-1]=1;
		return new IntervallicVector(iv);
	}
	
	private static ArrayList<Z12FoldedGraphTonnetz> _z12FoldedChordGraphTonnetzList;
	
	public static ArrayList<Z12FoldedGraphTonnetz> getZ12FoldedChordGraphTonnetzList() {
			
		
		if (_z12FoldedChordGraphTonnetzList == null) {
			ArrayList<STIntervallicStructure> si_list = new ArrayList<STIntervallicStructure>();
			for (int i=2;i<13;i++){
				si_list.addAll(STIntervallicStructure.enum_SI_up_to_flip(i, 12));			
			}
			HashSet<Z12FoldedGraphTonnetz> possible_tonnetzs = new HashSet<Z12FoldedGraphTonnetz>();
			for (STIntervallicStructure si : si_list){
				//System.out.println("SI : "+si+" Tonnetz : "+si.getTonnetz()+" Chord : "+si.get_default_STChord()+" VI : "+si.get_default_STChord().get_IntervalVector()+" IC : "+si.get_default_STChord().get_IntervalContent_set());
				TIChordComplex tonnetz_chord_complex = new TIChordComplex(si);
				//possible_tonnetzs.add((Z12FoldedGraphTonnetz)tonnetz_chord_complex.get_folded_tonnetz());
				possible_tonnetzs.add((Z12FoldedGraphTonnetz)si.get_z12_folded_graph_tonnetz());
			}
			
			_z12FoldedChordGraphTonnetzList = new ArrayList<Z12FoldedGraphTonnetz>();
			for (int i=1;i<7;i++){
				for (Z12FoldedGraphTonnetz t : possible_tonnetzs){
					if (t.size()==i) _z12FoldedChordGraphTonnetzList.add(t);
				}
			}
		}	
		return _z12FoldedChordGraphTonnetzList;
	}

	@Override
	public boolean are_neighbor(int p1, int p2) {
		for (Integer g : this){
			if (p2%_N == (p1+g)%_N || p2%_N == (_N+p1-g)%_N) return true;
		}
		return false;
	}

}
