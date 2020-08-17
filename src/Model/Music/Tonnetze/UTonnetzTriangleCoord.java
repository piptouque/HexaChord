package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class UTonnetzTriangleCoord extends UTonnetzCoordList implements Comparable<UTonnetzTriangleCoord>{

	private static final long serialVersionUID = 1L;

	public UTonnetzTriangleCoord(Collection<UTonnetzVertexCoord> tonnetz_coord_list){
		super();
		assert tonnetz_coord_list.size() == 3 : "not a triangle because it has "+tonnetz_coord_list.size()+" vertices";
		for (UTonnetzVertexCoord tonnetz_coord : tonnetz_coord_list){
			add(new UTonnetzVertexCoord(tonnetz_coord));
		}
	}

	public UTonnetzTriangleCoord(UTonnetzVertexCoord vertex1, UTonnetzVertexCoord vertex2, UTonnetzVertexCoord vertex3){
		super();
		add(vertex1);
		add(vertex2);
		add(vertex3);
	}
	
	@Override
	public int compareTo(UTonnetzTriangleCoord triangle) {
		if (containsAll(triangle)) return 0;
		return 1;
	}
	
	// retourne les 3 sommets du triangle
	public ArrayList<UTonnetzVertexCoord> get_vertices(){
		return new ArrayList<UTonnetzVertexCoord>(this);
	}

	//retourne les 3 arcs constituant les faces du triangle
	public ArrayList<UTonnetzEdgeCoord> get_edge_faces(){
		ArrayList<UTonnetzEdgeCoord> faces = new ArrayList<UTonnetzEdgeCoord>();
		faces.add(new UTonnetzEdgeCoord(get(0),get(1)));
		faces.add(new UTonnetzEdgeCoord(get(0),get(2)));
		faces.add(new UTonnetzEdgeCoord(get(1),get(2)));
		return faces;
	}
		
	// retourne les triangles voisins par un arc y compris le triangle lui même
	public ArrayList<UTonnetzTriangleCoord> get_2_1_voisins(){
		HashSet<UTonnetzTriangleCoord> neighbor_triangles = new HashSet<UTonnetzTriangleCoord>();
		for (UTonnetzEdgeCoord neighbor_edge : get_edge_faces()){
			neighbor_triangles.addAll(neighbor_edge.get_triangles_cofaces());
		}
		return new ArrayList<UTonnetzTriangleCoord>(neighbor_triangles);
	}
	
	// retourne les triangles voisins par un sommet y compris le triangle lui même
	public ArrayList<UTonnetzTriangleCoord> get_2_0_voisins(){
		HashSet<UTonnetzTriangleCoord> neighbor_triangles = new HashSet<UTonnetzTriangleCoord>();
		for (UTonnetzVertexCoord neighbor_vertex : get_vertices()){
			neighbor_triangles.addAll(neighbor_vertex.get_triangle_neighbors());
		}
		return new ArrayList<UTonnetzTriangleCoord>(neighbor_triangles);
	}

}
