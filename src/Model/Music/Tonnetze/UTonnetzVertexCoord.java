package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.HashSet;

public class UTonnetzVertexCoord extends ArrayList<Integer> implements Comparable<UTonnetzVertexCoord>{

	private static final long serialVersionUID = 1576471135047215860L;

	public UTonnetzVertexCoord(ArrayList<Integer> l) {
		super(l);
	}
	
	public UTonnetzVertexCoord() {
		super();
	}

	public UTonnetzVertexCoord(int[] l) {
		super();
		for (int i : l){
			this.add(i);
		}
	}

	@Override
	public int compareTo(UTonnetzVertexCoord l) {
		int cmp = ((Integer)(l.size())).compareTo(size());
		if (cmp != 0) return cmp;
		int i;
		for(i=0;i<size();i++) {
			cmp = get(i).compareTo(l.get(i));
			if (cmp != 0) return cmp;
		}
		return 0;
	}
		
	public UTonnetzVertexCoord plus(UTonnetzVertexCoord coord){
		if (this.size() != coord.size()) {
			System.err.println("Error : try to add different-sized coords");
			return null;
		}
		UTonnetzVertexCoord tc = new UTonnetzVertexCoord();
		for (int i=0;i<size();i++) tc.add(get(i)+coord.get(i));
		return tc;	
	}
	
	public UTonnetzVertexCoord less(UTonnetzVertexCoord coord){
		if (this.size() != coord.size()) {
			System.err.println("Error : try to add different-sized coords");
			return null;
		}
		UTonnetzVertexCoord tc = new UTonnetzVertexCoord();
		for (int i=0;i<size();i++) tc.add(get(i)-coord.get(i));
		return tc;	
	}
	
	
	public void move_one_gen_more(int n) {
		set(n, this.get(n)+1);
		
	}
	public void move_one_gen_less(int n) {
		set(n, this.get(n)-1);
	}

	// renvoi pitch associŽ ˆ des TcoordonnŽes
	public int t_coords_to_pitch(PlanarUnfoldedTonnetz t){
		assert (t._generators.size() == size()) : "Error : Coordinates don't match with the Tonnetz" ;
		if (t._generators.size() != size()) {
			System.err.println("Error : Coordinates don't match with the Tonnetz");
		}
		int pitch = 0;
		for (int i = 0;i < size();i++){
			pitch = (pitch+(get(i)*t._generators.get(i)));
		}
		pitch = t.get_scale().get_PC(pitch);
		while (pitch < 0) pitch += 12;
		return pitch%12;
	}
	
	// retourne les sommets voisins par un arc, y compris le sommet lui mme
	public UTonnetzCoordList get_0_1_neighbors(){
		assert(size()==3) : "Error : only 3-coord implementation";
		UTonnetzCoordList list = new UTonnetzCoordList();
		list.add(this);
		for (int i=0;i<size();i++){
			int[] t = {0,0,0};
			t[i]=1;
			UTonnetzVertexCoord c = new UTonnetzVertexCoord(t);
			list.add(plus(c));
			list.add(less(c));
		}
		return list;
	}
	
	// retourne les arcs constituant les cofaces du sommet.
	public ArrayList<UTonnetzEdgeCoord> get_edge_neighbors(){
		ArrayList<UTonnetzEdgeCoord> neighbor_edges = new ArrayList<UTonnetzEdgeCoord>();
		for (UTonnetzVertexCoord neighbor_vertex : get_0_1_neighbors()){
			neighbor_edges.add(new UTonnetzEdgeCoord(this,neighbor_vertex));
		}
		return neighbor_edges;
	}
	
	// retourne les triangles qui comprennent le sommet
	public ArrayList<UTonnetzTriangleCoord> get_triangle_neighbors(){
		HashSet<UTonnetzTriangleCoord> neighbor_triangles = new HashSet<UTonnetzTriangleCoord>();
		for (UTonnetzEdgeCoord neighbor_edge : get_edge_neighbors()){
			neighbor_triangles.addAll(neighbor_edge.get_triangles_cofaces());
		}
		return new ArrayList<UTonnetzTriangleCoord>(neighbor_triangles);
	}
	
	public boolean is_neighbor(UTonnetzVertexCoord c){
		if (get_0_1_neighbors().contains_coord(c)) return true;
		return false;
	}

}
