package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.PitchClassSet;
import Model.Music.PitchSet;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Model.Music.Harmonization.PitchClassRanking;
import Utils.Binomial;

public class TIChordComplex extends TComplex{
		
	
	// Chromatic Tonnetze
	public TIChordComplex(STIntervallicStructure intervallic_structure){
		_intervallic_structure = intervallic_structure;
		_folded_graph_tonnetz = intervallic_structure.get_z12_folded_graph_tonnetz();
//		System.out.println(get_latex_name()+" "+get_betti_numbers_list()+" "+get_betty_numbers()+"\n"+get_dimension());
//		System.out.println(get_bar_code());
		//print_Plex_complex();
	}
	
	// Heptatonic Tonnetze
	public TIChordComplex(STIntervallicStructure intervallic_structure, Scale scale){
		_intervallic_structure = intervallic_structure;
		_folded_graph_tonnetz = intervallic_structure.get_z7_folded_graph_tonnetz(scale);
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
		ArrayList<PitchClassSet> higher_dimensional_pcset = _intervallic_structure.get_transpositions_and_inversions();
		for (PitchClassSet pcset : higher_dimensional_pcset){
			pcs_set.addAll(pcset.get_n_sub_pc_set(size));
		}
		return pcs_set;
	}
	
	
	
	/* ------ COMPLIANCE ------ */
	
	public float get_global_compliance(PitchSet pitch_set){
		
		if(pitch_set.to_PitchClassSet().size() == 0) return 0;
		if(pitch_set.to_PitchClassSet().size() == 1) return 1;	// Une pitch class seule a une compliance de 1 dans un espace qui la contient

		// Choix arbitraire de calcul pour la compliance globale. Ici, on choisit C = 3*(2_c) + 1_c
		// Calcul établit pour garder une hiérarchie cohérente de la compliance des accords
		// tonaux dans le tonnetz 345

		//return (3*get_2_compliance(pitch_set)+get_1_compliance(pitch_set))/4;
		
		// Autre méthode : méthode de Jean-Louis : 
		// 2_c + (1-1/(1+1_c)) On normalise en diviasant par 1,5 car c'est la valeur max que ca peut atteindre (1_comp et 2_comp = 1)
		
		//return (get_2_compliance(pitch_set)+(1-(1/(1+get_1_compliance(pitch_set)))))/(float)1.5;
		return (get_n_compliance(pitch_set,2)+(1-(1/(1+get_n_compliance(pitch_set,1)))))/(float)1.5;
		
		
	}

	public float get_1_compliance(PitchSet pitch_set){
		return _folded_graph_tonnetz.get_1_compliance(pitch_set);
	}
	
//	public float get_2_compliance(PitchSet pitch_set){
//		if (pitch_set.isEmpty()) return 0;
//		ArrayList<Integer> pc_list = new ArrayList<Integer>(pitch_set.to_PitchClassSet());
//		if (pc_list.size()<=2) return 0;
//		
//		int two_simplex_count = 0;
//		for (int i=0;i<pc_list.size()-2;i++){
//			for (int j=i+1;j<pc_list.size()-1;j++){
//				for (int k=j+1;k<pc_list.size();k++){
//					PitchClassSet three_pc_set = new PitchClassSet(pc_list.get(i),pc_list.get(j),pc_list.get(k));
//					if(includes_chord_simplex(three_pc_set)){
//						two_simplex_count++;
//					}
//				}
//			}
//		}
//		return two_simplex_count/(float)Binomial.binom(pc_list.size(), 3);
//	}
	
//	public float get_3_compliance(PitchSet pitch_set){
//		if (pitch_set.isEmpty()) return 0;
//		ArrayList<Integer> pc_list = new ArrayList<Integer>(pitch_set.to_PitchClassSet());
//		if (pc_list.size()<=3) return 0;
//		
//		int three_simplex_count = 0;
//		
//		for (int i=0;i<pc_list.size()-3;i++){
//			for (int j=i+1;j<pc_list.size()-2;j++){
//				for (int k=j+1;k<pc_list.size()-1;k++){
//					for (int m=k+1;m<pc_list.size();m++){
//						PitchClassSet four_pc_set = new PitchClassSet(pc_list.get(i),pc_list.get(j),pc_list.get(k),pc_list.get(m));
//						if(includes_chord_simplex(four_pc_set)){
//							three_simplex_count++;
//						}
//					}
//				}
//			}
//		}
//
//		return three_simplex_count/(float)Binomial.binom(pc_list.size(), 4);
//	}
	

	public float get_n_compliance(PitchSet pitch_set, int n){
		
		if (pitch_set.to_PitchClassSet().size()<n+1) return 0;
		
		int n_simplex_count = 0;
		
		for (PitchClassSet pc_set : pitch_set.get_n_sub_pc_set(n+1)){
			if (includes_chord_simplex(pc_set)) n_simplex_count++;
		}
		
		return n_simplex_count/(float)Binomial.binom(pitch_set.to_PitchClassSet().size(), n+1);
		
	}
	
	public float get_absolute_compliance(PitchSet pitch_set){
		if (_intervallic_structure.get_N() == 12){
			if (pitch_set.get_intervallic_structure().equals(_intervallic_structure)) {
				return 1;
			}			
		} else {
			if (!_folded_graph_tonnetz.get_scale().containsAll(pitch_set.to_PitchClassSet())) return 0;
			if (pitch_set.to_N_pc_set(_folded_graph_tonnetz.get_scale()).get_intervallic_structure().equals(_intervallic_structure)) {
				return 1;
			}
		}
		return 0;
	}
	
	public PitchClassRanking get_compliance_ranking(PitchSet pitch_set){
		PitchClassRanking ranking = new PitchClassRanking();
		
		for (Integer i : _folded_graph_tonnetz.get_scale()){
			PitchSet pc2 = new PitchSet(pitch_set);
			pc2.add(i);
			ranking.add(i, get_global_compliance(pc2));
		}
		return ranking;
	}

	public PitchClassRanking get_compliance_ranking(PitchSet pitch_set,PitchClassSet forced_chord) {
		PitchClassRanking ranking = new PitchClassRanking();
		
		for (Integer i : _folded_graph_tonnetz.get_scale()){
			if (forced_chord.contains(i)){
				PitchSet pc2 = new PitchSet(pitch_set);
				pc2.add(i);
				ranking.add(i, get_global_compliance(pc2));
				//System.out.println("mc : "+mc+" pitch : "+i+" sum_compliance : "+get_sum_compliance(mc2));				
			}
		}
		return ranking;		
	}
	
	public String toString(){
		return "K"+_intervallic_structure.toString(false);
	}


	public boolean includes_chord_simplex(PitchClassSet pc_set){
		PitchClassSet N_pc_set;
		if (_intervallic_structure.get_N()!=12){
			if (!_folded_graph_tonnetz.get_scale().containsAll(pc_set)) return false;
			N_pc_set = pc_set.to_N_pc_set(_folded_graph_tonnetz.get_scale());
			assert false : "Chord inclusion in complex not yet implemented for N != 12";
		} else N_pc_set = pc_set;
		PitchClassSet complex_prime_chord = _intervallic_structure.get_prime_order_PCSet(0);
		int pc_set_cardinality = N_pc_set.size();
		for (int i : PitchClassSet.IFUNC_vector(complex_prime_chord, N_pc_set,_intervallic_structure.get_N())){
			if (i==pc_set_cardinality) return true;
		}
		for (int i : PitchClassSet.IFUNC_vector(complex_prime_chord, N_pc_set.inversion(0), _intervallic_structure.get_N())){
			if (i==pc_set_cardinality) return true;
		}
		return false;
	}
	
	
	/* ---------- STATIC METHODS ---------- */
	
	/* ------ Les 224 complexes chromatiques ------  */
	
	private static ArrayList<TIChordComplex> _z12TonnetzChordComplexList;
	
	public static ArrayList<TIChordComplex> getZ12TonnetzChromaticChordComplexList() {
		if (_z12TonnetzChordComplexList == null) {
			ArrayList<STIntervallicStructure> si_list = STIntervallicStructure.enum_SI_up_to_flip(12);
			_z12TonnetzChordComplexList = new ArrayList<TIChordComplex>();
			for (STIntervallicStructure si : si_list) _z12TonnetzChordComplexList.add(new TIChordComplex(si));
		}
		return _z12TonnetzChordComplexList;
	}
	
	/* ------ Les 12 complexes chromatiques constitués d'accords de taille 3------  */
	
	//private static ArrayList<TonnetzChordComplex> _z12Tonnetz_3_ChordComplexList;
	
//	public static ArrayList<TonnetzChordComplex> getZ12Tonnetz_3_ChordComplexList() {
//		if (_z12Tonnetz_3_ChordComplexList == null) {
//			ArrayList<STIntervallicStructure> si_list = STIntervallicStructure.enum_SI_up_to_flip(3,12);
//			_z12Tonnetz_3_ChordComplexList = new ArrayList<TonnetzChordComplex>();
//			for (STIntervallicStructure si : si_list) _z12Tonnetz_3_ChordComplexList.add(new TonnetzChordComplex(si));
//			System.out.println("Et voici les Triangular complexes : "+_z12Tonnetz_3_ChordComplexList);
//		}
//		return _z12Tonnetz_3_ChordComplexList;
//	}
//	
//	public static int getZ12Tonnetz_3_ChordComplexList_index(TonnetzChordComplex h) {
//		assert getZ12Tonnetz_3_ChordComplexList().indexOf(h) != -1 : "3_ChordComplex not found";
//		return getZ12Tonnetz_3_ChordComplexList().indexOf(h);
//	}

	/* ------ Les complexes chromatiques constitués d'accords de taille n------  */
	/* ------ Optimisation : les accords les plus utilisés (de taille 3 et 4) sont sauvegardés ------ */

	private static ArrayList<TIChordComplex> _z12Tonnetz_3_ChordComplexList;
	private static ArrayList<TIChordComplex> _z12Tonnetz_4_ChordComplexList;
	
	public static ArrayList<TIChordComplex> getZ12Tonnetz_n_ChordComplexList(int chord_size) {
		
		if (chord_size==3){
			if (_z12Tonnetz_3_ChordComplexList == null) {
				_z12Tonnetz_3_ChordComplexList = new ArrayList<TIChordComplex>();
				for (STIntervallicStructure si : STIntervallicStructure.enum_SI_up_to_flip(3,12)) _z12Tonnetz_3_ChordComplexList.add(new TIChordComplex(si));
			}
			return _z12Tonnetz_3_ChordComplexList;
		}

		if (chord_size==4){
			if (_z12Tonnetz_4_ChordComplexList == null) {
				_z12Tonnetz_4_ChordComplexList = new ArrayList<TIChordComplex>();
				for (STIntervallicStructure si : STIntervallicStructure.enum_SI_up_to_flip(4,12)) _z12Tonnetz_4_ChordComplexList.add(new TIChordComplex(si));
			}
			return _z12Tonnetz_4_ChordComplexList;
		}

		ArrayList<TIChordComplex> _z12Tonnetz_n_ChordComplexList = new ArrayList<TIChordComplex>();
		for (STIntervallicStructure si : STIntervallicStructure.enum_SI_up_to_flip(chord_size,12)) _z12Tonnetz_n_ChordComplexList.add(new TIChordComplex(si));
		return _z12Tonnetz_n_ChordComplexList;
	}
	
	public static int getZ12Tonnetz_n_ChordComplexList_index(TIChordComplex h, int chord_size) {
		assert getZ12Tonnetz_n_ChordComplexList(chord_size).indexOf(h) != -1 : chord_size+"_ChordComplex not found";
		return getZ12Tonnetz_n_ChordComplexList(chord_size).indexOf(h);
	}

	/* ------ Les 24 complexes heptatoniques de la forme [1,2,4] (12 majeurs et 12 mineurs) ------ */
	
	private static ArrayList<TIChordComplex> _z7Tonnetz_124_chordComplexList;
	
	public static ArrayList<TIChordComplex> getZ7Tonnetz_124_ChordComplexList(){
		if (_z7Tonnetz_124_chordComplexList == null) {
			STIntervallicStructure _z7_intervallic_structure = new STIntervallicStructure(new int[]{1,2,4});
			_z7Tonnetz_124_chordComplexList = new ArrayList<TIChordComplex>();
			for (int i=0;i<12;i++){
				_z7Tonnetz_124_chordComplexList.add(new TIChordComplex(_z7_intervallic_structure, new Scale(i,"major")));
				_z7Tonnetz_124_chordComplexList.add(new TIChordComplex(_z7_intervallic_structure, new Scale(i,"minor")));
			}
		}
		return _z7Tonnetz_124_chordComplexList;
	}
	
	/* ------ Les 24 complexes heptatoniques de la forme [2,2,3] (möbius strips) (12 majeurs et 12 mineurs) ------ */
	
	private static ArrayList<TIChordComplex> _z7Tonnetz_223_chordComplexList;
	
	public static ArrayList<TIChordComplex> getZ7Tonnetz_223_ChordComplexList(){
		if (_z7Tonnetz_223_chordComplexList == null) {
			STIntervallicStructure _z7_intervallic_structure = new STIntervallicStructure(new int[]{2,2,3});
			_z7Tonnetz_223_chordComplexList = new ArrayList<TIChordComplex>();
			for (int i=0;i<12;i++){
				_z7Tonnetz_223_chordComplexList.add(new TIChordComplex(_z7_intervallic_structure, new Scale(i,"major")));
				_z7Tonnetz_223_chordComplexList.add(new TIChordComplex(_z7_intervallic_structure, new Scale(i,"minor")));
			}
		}
		return _z7Tonnetz_223_chordComplexList;
	}
	
	public static TIChordComplex get_triad_strip(Scale scale){
		return new TIChordComplex(new STIntervallicStructure(new int[]{2,2,3}),scale);
	}
	
	public static ArrayList<String> get_complex_string_list(ArrayList<TIChordComplex> complex_list){
		ArrayList<String> string_list = new ArrayList<String>();
		for (TIChordComplex complex : complex_list) string_list.add(complex.toString());
		return string_list;
	}
	
	public String get_latex_name(){
		//return "\\cti{"+_intervallic_structure.elements_to_string_without_space(false)+"}";
		return "\\cti{"+_intervallic_structure.elements_to_string(false)+"}";
	}

//	@Override
//	public HashSet<TComplex> get_neighbor_pvl_complexes() {
//		
//		HashSet<TComplex> neighbors = new HashSet<TComplex>();
//		for (PitchClassSet pcs : get_higher_dim_pitch_class_sets()){
//			ArrayList<PitchClassSet> neighbor_chords = pcs.semitone_close_chords();
//			for (PitchClassSet neighbor_chord : neighbor_chords){
//				
//			}
//
//			
//			return neighbors;
//		}
//
//
//		return null;
//	}


	@Override
	public HashSet<TComplex> get_vpl_neighbor_complexes(HashSet<TComplex> complex_set){
		
		HashSet<TComplex> neighbors_set = new HashSet<TComplex>();
		ArrayList<STIntervallicStructure> neighbor_is_list = _intervallic_structure.get_neighbors_TIis();
		for (TComplex complex : complex_set){
			if (complex.get_STIntervallicStructure().is_contained_up_to_flipSI(neighbor_is_list)) neighbors_set.add(complex);
		}
		return neighbors_set;
	}


}
