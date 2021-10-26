package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class UTonnetzEdgeCoord extends UTonnetzCoordList implements Comparable<UTonnetzEdgeCoord> {

	private static final long serialVersionUID = 1L;

	public UTonnetzEdgeCoord(Collection<UTonnetzVertexCoord> tonnetz_coord_list) {
		super();
		assert tonnetz_coord_list.size() == 2 : "not an edge because it has " + tonnetz_coord_list.size() + " vertices";
		for (UTonnetzVertexCoord tonnetz_coord : tonnetz_coord_list) {
			add(new UTonnetzVertexCoord(tonnetz_coord));
		}
	}

	public UTonnetzEdgeCoord(UTonnetzVertexCoord vertex1, UTonnetzVertexCoord vertex2) {
		super();
		add(vertex1);
		add(vertex2);
	}

	@Override
	public int compareTo(UTonnetzEdgeCoord edge) {
		if (containsAll(edge))
			return 0;
		// if ((get(0).compareTo(edge.get(0)) == 0 && get(1).compareTo(edge.get(1)) ==
		// 0) || (get(0).compareTo(edge.get(1)) == 0 && get(1).compareTo(edge.get(0)) ==
		// 0)) return 0;
		return 1;
	}

	// retourne les 2 sommets constituant les faces de l'arc
	public UTonnetzCoordList get_vertex_faces() {
		return new UTonnetzCoordList(this);
	}

	// retourne les arcs voisins par un sommet (y compris l'arc lui-même)
	public ArrayList<UTonnetzEdgeCoord> get_1_0_neighbors() {
		HashSet<UTonnetzEdgeCoord> neighbor_edges = new HashSet<UTonnetzEdgeCoord>();
		neighbor_edges.add(this);
		for (UTonnetzVertexCoord neighbor_vertex : get_vertex_faces()) {
			neighbor_edges.addAll(neighbor_vertex.get_edge_neighbors());
		}
		return new ArrayList<UTonnetzEdgeCoord>(neighbor_edges);
	}

	// retourne les triangles constituant les cofaces de l'arc
	public ArrayList<UTonnetzTriangleCoord> get_triangles_cofaces() {
		HashSet<UTonnetzTriangleCoord> cofaces = new HashSet<UTonnetzTriangleCoord>();

		UTonnetzVertexCoord vertex1 = get(0);
		UTonnetzVertexCoord vertex2 = get(1);

		for (UTonnetzVertexCoord v1 : vertex1.get_0_1_neighbors()) {
			for (UTonnetzVertexCoord v2 : vertex2.get_0_1_neighbors()) {
				if (v1.compareTo(v2) == 0 && v1.compareTo(vertex1) != 0 && v1.compareTo(vertex2) != 0)
					cofaces.add(new UTonnetzTriangleCoord(vertex1, vertex2, v1));
			}
		}
		return new ArrayList<UTonnetzTriangleCoord>(cofaces);
	}

}
