package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.PitchSet;
import Model.Music.PitchClassSet;
import Model.Music.Scale;
import Utils.Binomial;
import Utils.IntegerSet;

public abstract class FoldedGraphTonnetz extends HashSet<Integer>{
	
	private static final long serialVersionUID = -5450209308326861577L;

	
	protected int _N;
	protected Scale _scale;
	protected int _orbit_count;
	
	public  Scale get_scale(){
		return _scale;
	}

	public abstract boolean are_neighbor(int p1, int p2);
	
	public boolean is_clique(IntegerSet pitch_set){
		
		if (pitch_set.size() == 0) return false;
		boolean is_clique = true;
		ArrayList<Integer> pitch_list = new ArrayList<Integer>(pitch_set);
		
		for (int i=0;i<pitch_list.size()-1;i++){
			for (int j=i+1;j<pitch_list.size();j++){
				if (!are_neighbor(pitch_list.get(i),pitch_list.get(j))) is_clique = false;
			}
		}
		return is_clique;
	}
	
	// Fonction retournant les PC voisins d'une PC dans le tonnetz
	public IntegerSet get_neighbors_pitch_class(int pitch_class) {
		IntegerSet neighbors = new IntegerSet();
		for (Integer g : this) {
			neighbors.add((pitch_class+g)%_N);
			neighbors.add((_N+pitch_class-g)%_N);
		}
		return neighbors;
	}

	// Retourne les pitchs voisins (pas les pitch class !)
	public PitchSet get_neighbors_pitch(int pitch) {
		PitchSet neighbors = new PitchSet();
		for (Integer g : this) {
			neighbors.add(pitch+g);
			neighbors.add(pitch-g);
		}
		return neighbors;
	}

	
	// Fonction retournant les PC les plus en contact avec un accord dans le tonnetz
	public IntegerSet closer_pitch_class_set(PitchClassSet c) {
		IntegerSet s = new IntegerSet();
		int max_neighbors = 0;
		for (Integer n : _scale.to_set()) {
			if (!c.member(n)) {
				int neighbors_in_chord = c.get_intersection(get_neighbors_pitch_class(n)).size();
				if (neighbors_in_chord >= max_neighbors) {
					if (neighbors_in_chord > max_neighbors) {
						max_neighbors = neighbors_in_chord;
						s.clear();
					}
					s.add(n);
				}
			}
		}
		return s;
	}
	
	public ArrayList<int[]> get_2_cliques(PitchClassSet c){
		ArrayList<int[]> cliques = new ArrayList<int[]>();
		for (int n : c){
			for (int m : c){
				if (n!=m){
					if(get_neighbors_pitch_class(n).contains(m)){
						cliques.add(new int[]{n,m});
					}
				}
			}
		}
		return cliques;
	}
	
	public float get_1_compliance(PitchSet pitch_set){
		if (pitch_set.isEmpty()) return 0;
		ArrayList<Integer> pc_list = new ArrayList<Integer>(pitch_set.to_PitchClassSet());
		if (pc_list.size() == 1) return 0;
		int voisinages = 0;
		for (int i=0;i<pc_list.size()-1;i++){
			for (int j=i+1;j<pc_list.size();j++){
				if (are_neighbor(pc_list.get(i),pc_list.get(j))){
					voisinages++;
				}
			}
		}
		return voisinages/(float)Binomial.binom(pc_list.size(), 2);
	}
	
	public IntegerSet complete_chord_triad_pc_set(PitchClassSet c){
		IntegerSet set = new IntegerSet();
		for(int[] cliques : get_2_cliques(c)){
			for (int i : _scale){
				if (!c.contains(i) && get_neighbors_pitch_class(i).contains(cliques[0]) && get_neighbors_pitch_class(i).contains(cliques[1])){
					set.add(i);
				}
			}
		}
		//System.out.println("complete_chord_triad_pc_set : "+set);
		return set;
	}
	
//	// Fonction retournant la liste des Tonnetz voisins d'un Tonnetz selon le critère : 
//	// 2 Tonnetz sont voisins si un mouvement minimal permet de passer de l'un à l'autre
//	public HashSet<FoldedGraphTonnetz> neighbor_tonnetzs(){
//		HashSet<FoldedGraphTonnetz> list = new HashSet<FoldedGraphTonnetz>();
//		PitchClassSet c = this.get_representative_chord();
//		ArrayList<PitchClassSet> neighbors = c.semitone_close_chords();
//		for (PitchClassSet n : neighbors){
//			Z12PlanarUnfoldedTonnetz t = n.get_corresponding_Tonnetz(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList());
//			if (t!=null && t!=this) list.add(t);
//		}
//		return list;
//	}
//	
//	// Fonction retournant la liste des Tonnetz voisins dans le graphe des Tonnetz selon le critère  :
//	// 2 Tonnetz sont voisins si un mouvement minimal permet de passer de l'un à l'autre
//	public static HashSet<HashSet<Z12PlanarUnfoldedTonnetz>> neighbor_list(ArrayList<Z12PlanarUnfoldedTonnetz> tonnetzs){
//		HashSet<HashSet<Z12PlanarUnfoldedTonnetz>> neighbor_list = new HashSet<HashSet<Z12PlanarUnfoldedTonnetz>>();
//		for (Z12PlanarUnfoldedTonnetz t : Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList()){
//			HashSet<Z12PlanarUnfoldedTonnetz> set = t.neighbor_tonnetzs();
//			for (Z12PlanarUnfoldedTonnetz n : set){
//				HashSet<Z12PlanarUnfoldedTonnetz> neighborhood = new HashSet<Z12PlanarUnfoldedTonnetz>();
//				neighborhood.add(n);
//				neighborhood.add(t);
//				neighbor_list.add(neighborhood);
//			}
//		}
//		return neighbor_list;
//	}

	
	public String to_string(){
		String str = "T"+super.toString();
		return str;
	}
	
}
