package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import Model.Music.PitchClassSet;

public class UTonnetzCoordList extends ArrayList<UTonnetzVertexCoord>{
	
	private static final long serialVersionUID = 1L;

	public UTonnetzCoordList(){
		super();
	}
	
	public UTonnetzCoordList(Collection<UTonnetzVertexCoord> tonnetz_coord_list){
		super();
		for (UTonnetzVertexCoord tonnetz_coord : tonnetz_coord_list){
			add(new UTonnetzVertexCoord(tonnetz_coord));
		}
	}
	
	
	// return coords neighbor from the coords of the list
	public UTonnetzCoordList neighbor_coords(){
		UTonnetzCoordList neighbor_coord_list = new UTonnetzCoordList();
		for (UTonnetzVertexCoord coord : this){
			neighbor_coord_list.addAll(coord.get_0_1_neighbors());
		}
		return neighbor_coord_list;
	}
	
	// Same as neighbor_coords but don't take 2 times the same coord (anciennement : enlarge_circle) -> non déterministe
	public UTonnetzCoordList neighbor_coords_set(){
		UTonnetzCoordList neighbor_coord_list = new UTonnetzCoordList();
		for (UTonnetzVertexCoord coord : this){
			//for (TonnetzCoord neighbor_coord : neighbor_coords(coord)){
			for (UTonnetzVertexCoord neighbor_coord : coord.get_0_1_neighbors()){
				if (!neighbor_coord_list.contains_coord(neighbor_coord)){
					neighbor_coord_list.add(neighbor_coord);
				}
			}
		}
		return neighbor_coord_list;
	}

	public static int neighborhood_count(UTonnetzCoordList coord_list, UTonnetzVertexCoord coord){
		int count = 0;
		for (UTonnetzVertexCoord c : coord_list) {
			if (coord.is_neighbor(c)) count++;
		}
		return count;
	}
	
	// return the list of the coords of coord_list which have the more neighborhood relationships with coords.
	public UTonnetzCoordList closest_coords(UTonnetzCoordList coord_list){
		UTonnetzCoordList closest_coords_list = new UTonnetzCoordList();
		int max=0;
		int neighbors;
		//TonnetzCoord closest_coord = coord_list.get(0);
		for (UTonnetzVertexCoord coord : coord_list){
			neighbors = neighborhood_count(this,coord);
			if (neighbors>=max){
				if (neighbors==max){
					closest_coords_list.add(coord);
				} else {
					closest_coords_list.clear();
					closest_coords_list.add(coord);
					max = neighbors;
				}
			} 
		}
		return closest_coords_list;		
	}
	
	public int pitch_size(PlanarUnfoldedTonnetz t){
		HashSet<Integer> set = new HashSet<Integer>();
		for (UTonnetzVertexCoord c : this) set.add(c.t_coords_to_pitch(t));
		return set.size();
	}
		
	public ArrayList<Integer> t_coords_to_pitch(PlanarUnfoldedTonnetz t){
		ArrayList<Integer> pitch_list = new ArrayList<Integer>();
		for (UTonnetzVertexCoord c : this){
			pitch_list.add(c.t_coords_to_pitch(t));
		}
		return pitch_list;
	}
	
	public boolean contains_coord(UTonnetzVertexCoord c){
		for (UTonnetzVertexCoord tc : this){
			if (c.compareTo(tc) == 0){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<UTonnetzEdgeCoord> get_0_1_neighbors_in_list(){
		ArrayList<UTonnetzEdgeCoord> z_1_neighbors = new ArrayList<UTonnetzEdgeCoord>();
		for (int i=0;i<size();i++){
			UTonnetzVertexCoord vertex1 = get(i);
			for (int j=i+1;j<size();j++){
				UTonnetzVertexCoord vertex2 = get(j);
				if (vertex1.is_neighbor(vertex2)) {
					z_1_neighbors.add(new UTonnetzEdgeCoord(vertex1, vertex2));
				}
			}
		}
		return z_1_neighbors;
	}

	public ArrayList<UTonnetzTriangleCoord> get_0_2_neighbors_in_list(){
		ArrayList<UTonnetzTriangleCoord> z_2_neighbors = new ArrayList<UTonnetzTriangleCoord>();
		for (int i=0;i<size();i++){
			UTonnetzVertexCoord vertex1 = get(i);
			for (int j=i+1;j<size();j++){
				UTonnetzVertexCoord vertex2 = get(j);
				for (int k=j+1;k<size();k++){
					UTonnetzVertexCoord vertex3 = get(k);
					if (vertex1.is_neighbor(vertex2) && vertex1.is_neighbor(vertex3) && vertex2.is_neighbor(vertex3)) {
						z_2_neighbors.add(new UTonnetzTriangleCoord(vertex1, vertex2, vertex3));
					}					
				}
			}
		}
		return z_2_neighbors;
	}

	public ArrayList<UTonnetzCoordList> get_Int(){
		ArrayList<UTonnetzCoordList> interior = new ArrayList<UTonnetzCoordList>();
		for (UTonnetzVertexCoord vertex : this) {
			UTonnetzCoordList list = new UTonnetzCoordList();  
			list.add(vertex); interior.add(list);
		}
		interior.addAll(get_0_1_neighbors_in_list());
		interior.addAll(get_0_2_neighbors_in_list());
		return interior;
	}
	
//	public ArrayList<UTonnetzCoordList> get_closure(){
//		ArrayList<UTonnetzCoordList> interior = new ArrayList<UTonnetzCoordList>();
//	}
	
	
	
	
	// fonction recursive stockant dans set tous les sous ensembles de taille n de list. 
	// le paramètre index est utilisé pour la récursion
	
//	public void recur(int index, int n,ArrayList<UTonnetzVertexCoord> list,HashSet<UTonnetzCoordList> set){
//		if (index >= n) {
//			set.add(new UTonnetzCoordList(list));
//			set.add(new UTonnetzCoordList(list));
//			return;
//		}
//		
//		int start = 0;
//		if (index > 0) start = to_sorted_list().indexOf(list.get(index-1))+1;
//		
//		for (int i = start;i<size();i++){
//			list.set(index, to_sorted_list().get(i));	
//			recur(index+1,n,list,set);
//		}		
//	}
//	
	// renvoie une liste contenant tous les sous-ensembles de n sommets
	
//	public HashSet<UTonnetzCoordList> get_n_vertex_subset(int n){
//		HashSet<UTonnetzCoordList> set = new HashSet<UTonnetzCoordList>();
//		if (n>size() || n==0) return set;
//		if (n == size()) {
//			set.add(this); return set;
//		}
//		if (n==1){
//			for (UTonnetzVertexCoord vertex : this) {
//				UTonnetzCoordList new_vertex_set = new UTonnetzCoordList();
//				new_vertex_set.add(vertex);
//				set.add(new_vertex_set);
//			}
//			return set;
//		}
//		
//		ArrayList<UTonnetzVertexCoord> list = new ArrayList<UTonnetzVertexCoord>();
//		for (int i=0;i<n;i++) list.add(null);
//		recur(0,n,list,set);
//		
//		return set;
//	}
	
	//renvoie l'ensemble des sous ensembles (comprenant l'ensemble lui-même)
	
//	public HashSet<UTonnetzCoordList> get_all_vertex_subset(){
//		HashSet<UTonnetzCoordList> subsets = new HashSet<UTonnetzCoordList>();
//		for (int i=1;i<=size();i++){
//			subsets.addAll(get_n_vertex_subset(i));
//		}
//		return subsets;
//	}
	
	// sorts the elements
	public ArrayList<UTonnetzVertexCoord> to_sorted_list(){
		ArrayList<UTonnetzVertexCoord> list = new ArrayList<UTonnetzVertexCoord>();
		list.addAll(this);
		Collections.sort(list);
		return list;
	}

}
