package Model.Music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// Distance entre deux ensembles de classes de hauteurs
// Ex : distance identique entre (0,3,7) -> (0,2,5) et (0,4,9) et (2,9,11)
// Correspond (sžrement) ˆ un vecteur dans les voice leading spaces

public class PitchClassSetInterval{

	public STIntervallicStructure _is1;
	public STIntervallicStructure _is2;
	public int _interval;
	public int _N=12;
	
	public PitchClassSetInterval(PitchClassSet pcs1, PitchClassSet pcs2){
		
		_is1 = pcs1.get_intervallic_structure();
		_is2 = pcs2.get_intervallic_structure();
		System.out.println("prime 1 : "+_is1.get_prime_PCSet(true));
		System.out.println("prime 2 : "+_is2.get_prime_PCSet(true));
		_interval = pcs2.get_interval_from_prime_chord() - pcs1.get_interval_from_prime_chord();
		if (_interval<0) {
			_interval = _N + _interval;
		}
	}
	
	private PitchClassSetInterval(STIntervallicStructure is1, STIntervallicStructure is2, int interval){
		_is1 = new STIntervallicStructure(is1.get_list());
		_is2 = new STIntervallicStructure(is2.get_list());
		_interval = interval;
	}

	public boolean equals(PitchClassSetInterval pcsi) {
		if (_is1.equals(pcsi._is1) && _is2.equals(pcsi._is2) && _interval == pcsi._interval) return true;
		return false;
	}
	
	public String toString(){
		return _is1.toString()+"--("+_interval+")-->"+_is2.toString();
	}
	
	public PitchClassSet get_second_pcs(PitchClassSet pcs1){
		PitchClassSet second_pcs = _is2.get_prime_PCSet(true);
		int i = (pcs1.get_interval_from_prime_chord()+_interval)%_N;
		return second_pcs.transposition(i);
	}

	public static HashSet<PitchClassSetInterval> get_total_transitions_under_sized_pcs(int size, int N) {
		HashSet<PitchClassSetInterval> pcsi_set = new HashSet<PitchClassSetInterval>();
		ArrayList<STIntervallicStructure> is_list_1 = new ArrayList<STIntervallicStructure>();
		ArrayList<STIntervallicStructure> is_list_2 = new ArrayList<STIntervallicStructure>();
		for (int i = 0;i<=size;i++){
			is_list_1.addAll(STIntervallicStructure.enum_SI(i, N));
			is_list_2.addAll(is_list_1);
		}
		for (STIntervallicStructure is_1 : is_list_1){
			for (STIntervallicStructure is_2 : is_list_2){
				for (int i=0;i<N;i++){
					pcsi_set.add(new PitchClassSetInterval(is_1, is_2, i));
				}
			}
		}
		
		return pcsi_set;
	}
	
	public static List<PitchClassSet> generate_pcs_list(List<PitchClassSetInterval> list){
		List<PitchClassSet> pcs_list = new ArrayList<PitchClassSet>();
		PitchClassSet tmp_pcs = list.get(0)._is1.get_prime_PCSet(true);
		pcs_list.add(tmp_pcs);
		for (PitchClassSetInterval pcsi : list){
			pcs_list.add(pcsi.get_second_pcs(tmp_pcs));
			tmp_pcs=pcs_list.get(pcs_list.size()-1);
		}
		
		return pcs_list;
	}
	
}
