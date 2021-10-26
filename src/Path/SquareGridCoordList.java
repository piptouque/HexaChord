package Path;

import java.util.ArrayList;
import java.util.Arrays;

import Utils.Table;

public class SquareGridCoordList extends ArrayList<int[]> {

	private static final long serialVersionUID = 1L;

	public SquareGridCoordList() {
		super();
	}

	public String toString() {
		return Table.toString(this);
	}

	public SquareGridCoordList copy() {
		SquareGridCoordList square_grid_coord_list = new SquareGridCoordList();
		for (int[] coord : this) {
			square_grid_coord_list.add(new int[] { coord[0], coord[1] });
		}
		return square_grid_coord_list;
	}

	public SquareGridCoordList get_translated_coord_set(TranslationInTonnetz translation_vector) {
		SquareGridCoordList new_coord_set = new SquareGridCoordList();
		for (int i = 0; i < size(); i++) {
			assert get(i).length == translation_vector.size() : "coord and translation vector are not the same size";
			int[] new_coord = new int[get(i).length];
			for (int j = 0; j < translation_vector.size(); j++) {
				new_coord[j] = get(i)[j] + translation_vector.get(j);
			}
			new_coord_set.add(new_coord);
		}
		return new_coord_set;
	}

	// the parameter rotation = {0,1,2,3,4,5}. Angle[rotation] = rotation*(-Pi/3).
	// => rotation dans le sens des guilles d'une montre
	// Rotation par rapport au centre [0,0]
	// La structure retourne est l'ensemble rsultant de mappings sur les coordonnes
	public ArrayList<CoordMapping> rotate(int rotation) {

		ArrayList<CoordMapping> coord_mapping_list = new ArrayList<CoordMapping>();
		int[] old_coord;

		switch (rotation) {
		case 1:
			for (int[] coord : this) {
				old_coord = coord.clone();
				coord[0] = -old_coord[1];
				coord[1] = old_coord[0] + old_coord[1];
				coord_mapping_list.add(new CoordMapping(old_coord, coord));
			}
			break;

		case 2:
			for (int[] coord : this) {
				old_coord = coord.clone();
				coord[0] = -1 * old_coord[1] - 1 * old_coord[0];
				coord[1] = old_coord[0];
				coord_mapping_list.add(new CoordMapping(old_coord, coord));
			}
			break;

		case 3:
			for (int[] coord : this) {
				old_coord = coord.clone();
				coord[0] = (-1) * old_coord[0];
				coord[1] = (-1) * old_coord[1];
				coord_mapping_list.add(new CoordMapping(old_coord, coord));
			}
			break;

		case 4:
			for (int[] coord : this) {
				old_coord = coord.clone();
				coord[0] = old_coord[1];
				coord[1] = (-1) * old_coord[0] + (-1) * old_coord[1];
				coord_mapping_list.add(new CoordMapping(old_coord, coord));
			}
			break;

		case 5:
			for (int[] coord : this) {
				old_coord = coord.clone();
				coord[0] = old_coord[0] + old_coord[1];
				coord[1] = (-1) * old_coord[0];
				coord_mapping_list.add(new CoordMapping(old_coord, coord));
			}
			break;

		}
		return coord_mapping_list;
	}

	public void translate(int n, int ne) {

		// Anciennes orientations des axes
		// for (int i=0;i<size();i++){
		// get(i)[0] = get(i)[0] + n;
		// get(i)[1] = get(i)[1] + ne;
		// }

		// Nouvelle orientation des axes (pour JMC -> quintes vers le haut)
		for (int i = 0; i < size(); i++) {
			get(i)[0] = get(i)[0] - ne;
			get(i)[1] = get(i)[1] + n + ne;
		}

	}

	public ArrayList<ArrayList<int[]>> get_edges() {
		ArrayList<ArrayList<int[]>> list = new ArrayList<ArrayList<int[]>>();

		for (int[] i : this) {
			int[] note_coord2 = { i[0], i[1] + 1 };
			int[] note_coord3 = { i[0] + 1, i[1] };
			int[] note_coord4 = { i[0] - 1, i[1] + 1 };
			for (int[] i2 : this) {
				if (Arrays.equals(note_coord2, i2) || Arrays.equals(note_coord3, i2)
						|| Arrays.equals(note_coord4, i2)) {
					ArrayList<int[]> edge = new ArrayList<int[]>();
					edge.add(i);
					edge.add(i2);
					list.add(edge);
				}
			}
		}
		return list;
	}

	public ArrayList<ArrayList<int[]>> get_triangles() {
		ArrayList<ArrayList<int[]>> list = new ArrayList<ArrayList<int[]>>();
		for (int[] i : this) {
			int[] note_coord2 = { i[0], i[1] + 1 };
			for (int[] i2 : this) {
				if (Arrays.equals(note_coord2, i2)) {
					int[] note_coord3 = { i[0] + 1, i[1] };
					for (int[] i3 : this) {
						if (Arrays.equals(note_coord3, i3)) {
							ArrayList<int[]> triangle = new ArrayList<int[]>();
							triangle.add(i);
							triangle.add(i2);
							triangle.add(i3);
							list.add(triangle);
						}
					}
					int[] note_coord4 = { i[0] - 1, i[1] + 1 };
					for (int[] i4 : this) {
						if (Arrays.equals(note_coord4, i4)) {
							ArrayList<int[]> triangle = new ArrayList<int[]>();
							triangle.add(i);
							triangle.add(i2);
							triangle.add(i4);
							list.add(triangle);
						}
					}
				}
			}
		}
		return list;
	}

}
