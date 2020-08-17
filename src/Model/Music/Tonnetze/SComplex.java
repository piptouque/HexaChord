package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.PitchClassSet;
import Model.Music.STIntervallicStructure;

public class SComplex extends TComplex{

	public SComplex(STIntervallicStructure is){
		_total_pcs_set = new HashSet<PitchClassSet>();
		for (PitchClassSet pcs : is.get_chords_up_to_is_perm()){
			_total_pcs_set.addAll(pcs.get_all_sub_pc_set());
		}
		_intervallic_structure = is;
	}
	
	@Override
	public String get_latex_name(){
		return "\\ctp{"+_intervallic_structure.elements_to_string(false)+"}";
	}

	@Override
	public HashSet<TComplex> get_vpl_neighbor_complexes(HashSet<TComplex> complex_set) {

		HashSet<TComplex> neighbors_set = new HashSet<TComplex>();
		HashSet<PitchClassSet> neighbors_pc_list;
		
		
		for (PitchClassSet pcs : get_sized_pitch_class_sets(get_dimension()+1)){
			for (PitchClassSet neighbor : pcs.semitone_close_chords_set()){
				for (TComplex complex : complex_set){
					if (complex.contains_chord(neighbor)) neighbors_set.add(complex);
				}
			}
		}
		return neighbors_set;
	}


}
