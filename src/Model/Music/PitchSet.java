package Model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Utils.IntegerSet;

public class PitchSet extends IntegerSet implements MusicCollection{

	private static final long serialVersionUID = 1L;
	
	public PitchSet(){
		super();
	}
	
	public PitchSet(ArrayList<Integer> pitch_list){
		super(pitch_list);
	}
	
	public PitchSet(IntegerSet set){
		super(set);
	}
	
	public PitchSet(int[] pitches){
		super();
		for (int p : pitches){
			add(p);
		}
	}


	@Override
	public PitchSet remove_pitch(int pitch) {
		PitchSet new_pitch_set = new PitchSet();
		if (size()>0){ 
			for (int n : this){
				if (n!=pitch){
					new_pitch_set.add(n);
				}
			}			
		}
		return new_pitch_set;
	}

	@Override
	public PitchClassSet to_PitchClassSet() {
		return to_PitchClassSet(12);
	}
	
	@Override
	public PitchClassSet to_PitchClassSet(int N){
		PitchClassSet set = new PitchClassSet();
		for(int n : this) set.add(n%N);
		return set;
	}
	
	public PitchClassSet to_N_pc_set(Scale scale) {
		return to_PitchClassSet().to_N_pc_set(scale);
	}
	
	public STIntervallicStructure get_intervallic_structure(){
		return to_PitchClassSet().get_intervallic_structure();
	}
	
	public HashSet<PitchClassSet> get_n_sub_pc_set(int n){
		return to_PitchClassSet().get_n_sub_pc_set(n);
	}
	
	public static String print_pitch_list(ArrayList<Integer> pitch_list){
		String str = "[";
		Collections.sort(pitch_list);
		for (int i=0;i<pitch_list.size();i++){
			str=str+Note.get_name(pitch_list.get(i))+(pitch_list.get(i)/12-1);
			if(i!=pitch_list.size()-1){
				str=str+",";
			}
		}		
		return str+"]";
	}

	public String print_pitch_set(){
		String str = "[";
		ArrayList<Integer> pitch_list = new ArrayList<Integer>();
		for (int pitch : this) pitch_list.add(pitch);
		Collections.sort(pitch_list);
		for (int i=0;i<pitch_list.size();i++){
			str=str+Note.get_name(pitch_list.get(i))+(pitch_list.get(i)/12-1);
			if(i!=pitch_list.size()-1){
				str=str+",";
			}
		}		
		return str+"]";
	}
	
//	public List<Scale> get_HA_compatible_keys(){
//		List<Scale> compatible_keys = new ArrayList<Scale>();
//		for (Scale major_scale : Scale.get_major_scales()){
//			if (major_scale.containsAll(this.to_PitchClassSet())) compatible_keys.add(major_scale);
//		}
//		for (Scale minor_scale : Scale.get_minor_scales()){
//			if (minor_scale.containsAll(this.to_PitchClassSet())) compatible_keys.add(minor_scale);
//		}
//		return compatible_keys;
//	}
//	
//	public Set<PitchClassSet> get_HA_compatible_chords(){
//		return this.to_PitchClassSet().get_HA_compatible_chords();
//	}
//	
//	public List<HAChordCandidate> get_HA_candidates(){
//		List<HAChordCandidate> cand_list = new ArrayList<HAChordCandidate>();
//		for (Scale compatible_scale : get_HA_compatible_keys()){
//			for (PitchClassSet compatible_triade : get_HA_compatible_chords()){
//				cand_list.add(new HAChordCandidate(compatible_triade, compatible_scale));
//			}
//		}
//		return cand_list;
//	}

}
