package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.PitchClassSet;
import Model.Music.STIntervallicStructure;

public class TChordComplex extends TComplex{
	
	
	
	public TChordComplex(STIntervallicStructure intervallic_structure){
		_intervallic_structure = intervallic_structure;
	}
	

	@Override
	public int get_dimension(){
		return _intervallic_structure.size()-1;
	}

	@Override
	public HashSet<PitchClassSet> get_total_pcs_set(){
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		for (int dim = 0;dim<=get_dimension();dim++){
			pcs_set.addAll(get_sized_pitch_class_sets(dim+1));
		}
		return pcs_set;
	}

	
	@Override
	public HashSet<PitchClassSet> get_sized_pitch_class_sets(int size){
		
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		ArrayList<PitchClassSet> higher_dimensional_pcset = _intervallic_structure.get_transpositions();
		
		for (PitchClassSet pcset : higher_dimensional_pcset){
			pcs_set.addAll(pcset.get_n_sub_pc_set(size));
		}
		return pcs_set;
	}
	
	public String toString(){
		return "CT"+_intervallic_structure.toString(true);
	}
	
	public STIntervallicStructure get_STIntervallicStructure(){
		return _intervallic_structure;
	}
	
	public String get_latex_name(){
		return "\\ct{"+_intervallic_structure.elements_to_string(true)+"}";
	}


	@Override
	public HashSet<TComplex> get_vpl_neighbor_complexes(HashSet<TComplex> complex_set){
		
		HashSet<TComplex> neighbors_set = new HashSet<TComplex>();
		ArrayList<STIntervallicStructure> neighbor_is_list = _intervallic_structure.get_neighbors_Tis();
		for (TComplex complex : complex_set){
			if (complex.get_STIntervallicStructure().is_contained(neighbor_is_list)) neighbors_set.add(complex);
		}
		return neighbors_set;
	}



}
