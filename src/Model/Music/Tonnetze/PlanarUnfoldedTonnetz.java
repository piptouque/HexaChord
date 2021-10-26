package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.math.plex4.homology.chain_basis.Simplex;

import Model.Music.PitchSetStream;
import Model.Music.Constant;
import Model.Music.Interval;
import Model.Music.IntervallicVector;
import Model.Music.Note;
import Model.Music.PitchClassSetStream;
import Model.Music.PitchSet;
import Model.Music.PitchSetWithDuration;
import Model.Music.PitchClassSet;
import Model.Music.PitchClassSetSeq;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Path.CentralSymmetryInTonnetz;
import Path.SquareGridCoordPath;
import Path.SquareGridCoordList;
import Path.TranslationInTonnetz;
import Path.TonnetzTranslationPath;
import Utils.Gcd;
import Utils.TableSet;

public abstract class PlanarUnfoldedTonnetz {

	protected int _N;
	protected TonnetzGen _generators;
	protected TreeMap<Integer, TreeSet<UTonnetzVertexCoord>> _coords;
	protected HashSet<Integer> _pitch_class_set;
	protected FoldedGraphTonnetz _folded_graph_tonnetz;
	protected TIChordComplex _folded_complex;

	public static ArrayList<PlanarUnfoldedTonnetz> get_12_7_UTonnetze() {
		ArrayList<PlanarUnfoldedTonnetz> tonnetz_list = new ArrayList<PlanarUnfoldedTonnetz>();
		for (Z12PlanarUnfoldedTonnetz tonnetz : Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList())
			tonnetz_list.add(tonnetz);
		// for(Z7PlanarUnfoldedTonnetz tonnetz : Z7PlanarUnfoldedTonnetz.g
		return tonnetz_list;
	}

	public int get_N() {
		return _N;
	}

	public int get_generator(int n) {
		return _generators.get(n);
	}

	public TonnetzGen get_generators() {
		return _generators;
	}

	public int generators_count() {
		return _generators.size();
	}

	public UTonnetzVertexCoord toCoord(int n) {
		if ((n % (_folded_graph_tonnetz._orbit_count)) != 0)
			return new UTonnetzVertexCoord(); // si la note n'existe pas dans le rseau (pas la bonne orbite)
		return toCoords(n).get(0);
	}

	public FoldedGraphTonnetz get_folded_tonnetz() {
		return _folded_graph_tonnetz;
	}

	private static ArrayList<PlanarUnfoldedTonnetz> _Z7_and_Z12_HexaTonnetzList;

	public static ArrayList<PlanarUnfoldedTonnetz> getZ7AndZ12HexaTonnetzList() {
		if (_Z7_and_Z12_HexaTonnetzList == null) {
			_Z7_and_Z12_HexaTonnetzList = new ArrayList<PlanarUnfoldedTonnetz>();

			for (Z12PlanarUnfoldedTonnetz z12_unfolded_tonnetz : Z12PlanarUnfoldedTonnetz
					.getZ12TriangularUnfoldedTonnetzList()) {
				_Z7_and_Z12_HexaTonnetzList.add(z12_unfolded_tonnetz);
			}
			for (Z7PlanarUnfoldedTonnetz z7_unfolded_tonnetz : Z7PlanarUnfoldedTonnetz.getZ7HexaTonnetzList()) {
				_Z7_and_Z12_HexaTonnetzList.add(z7_unfolded_tonnetz);
			}
		}
		return _Z7_and_Z12_HexaTonnetzList;
	}

	public static ArrayList<String> getZ7_and_Z12_HexaTonnetzNameList() {
		ArrayList<String> name_list = new ArrayList<String>();
		for (PlanarUnfoldedTonnetz t : getZ7AndZ12HexaTonnetzList())
			name_list.add(t.toString());
		return name_list;
	}

	public static String[] getZ7_and_Z12_HexaTonnetzNameTable() {
		String[] name_table = new String[getZ7AndZ12HexaTonnetzList().size()];
		for (int i = 0; i < _Z7_and_Z12_HexaTonnetzList.size(); i++)
			name_table[i] = _Z7_and_Z12_HexaTonnetzList.get(i).toString();
		return name_table;
	}

	private void build_coords() {
		int i;
		_coords = new TreeMap<Integer, TreeSet<UTonnetzVertexCoord>>();

		UTonnetzVertexCoord zero = new UTonnetzVertexCoord();
		// for(i=0;i<_dim;i++) zero.add(0);
		for (i = 0; i < _generators.size(); i++)
			zero.add(0);

		TreeSet<UTonnetzVertexCoord> workList = new TreeSet<UTonnetzVertexCoord>();
		workList.add(zero);
		_coords.put(0, workList);

		workList = new TreeSet<UTonnetzVertexCoord>();
		workList.add(zero);

		while (true) {
			TreeSet<UTonnetzVertexCoord> neoWorkList = new TreeSet<UTonnetzVertexCoord>();

			for (UTonnetzVertexCoord c : workList) {
				// for(i=0;i<_dim;i++) {
				for (i = 0; i < _generators.size(); i++) {
					int x = c.get(i);
					if (x >= 0) {
						UTonnetzVertexCoord nc = new UTonnetzVertexCoord(c);
						nc.set(i, x + 1);
						if (!_coords.containsKey(get_PC(nc)))
							neoWorkList.add(nc);
					}
					if (x <= 0) {
						UTonnetzVertexCoord nc = new UTonnetzVertexCoord(c);
						nc.set(i, x - 1);
						if (!_coords.containsKey(get_PC(nc)))
							neoWorkList.add(nc);
					}
				}
			}

			if (neoWorkList.size() == 0)
				break;

			for (UTonnetzVertexCoord c : neoWorkList) {
				int n = get_PC(c);
				TreeSet<UTonnetzVertexCoord> l = _coords.get(n);
				if (l == null) {
					l = new TreeSet<UTonnetzVertexCoord>();
					_coords.put(n, l);
				}
				l.add(c);
			}

			workList = neoWorkList;
		}
		if (_N != Constant.N) {
			TreeSet<UTonnetzVertexCoord> l;
			TreeMap<Integer, TreeSet<UTonnetzVertexCoord>> _coords2 = new TreeMap<Integer, TreeSet<UTonnetzVertexCoord>>();
			for (Integer k : _coords.keySet()) {
				l = _coords.get(k);
				_coords2.put(get_scale().get_PC(k), l);
			}
			_coords = _coords2;
		}
	}

	public ArrayList<UTonnetzVertexCoord> toCoords(int n) { // n = pitch class
		// assert(n>0);
		n = ((n % Constant.N) + Constant.N) % Constant.N;
		if ((n % (_folded_graph_tonnetz._orbit_count)) != 0)
			return new ArrayList<UTonnetzVertexCoord>();
		if (_coords == null) {
			build_coords();
		}
		return new ArrayList<UTonnetzVertexCoord>(_coords.get(n));
	}

	public int get_PC(ArrayList<Integer> coord) {

		assert (coord.size() == _generators.size());
		int n = 0;
		for (int i = 0; i < _generators.size(); i++)
			n += coord.get(i) * _generators.get(i);
		return Gcd.reduce(n, _N);
	}

	public int get_PC(int[] square_grid_coord) {

		assert (square_grid_coord.length == _generators.size() - 1);
		int n = 0;
		n = n + (square_grid_coord[0] + square_grid_coord[1]) * _generators.get(0);
		n = n + (square_grid_coord[1]) * _generators.get(1);

		return Gcd.reduce(n, _N);
	}

	// // renvoi pitch associ des Tcoordonnes
	// public int t_coords_to_pitch(TonnetzCoord t_coord){
	// assert (_generators.size() == t_coord.size()) : "Error : Coordinates don't
	// match with the Tonnetz" ;
	// if (_generators.size() != t_coord.size()) {
	// System.err.println("Error : Coordinates don't match with the Tonnetz");
	// }
	// int pitch = 0;
	// for (int i = 0;i < t_coord.size();i++){
	// pitch = (pitch+(t_coord.get(i)*_generators.get(i)));
	// }
	// pitch = get_scale().get_PC(pitch);
	// while (pitch < 0) pitch += 12;
	// return pitch%12;
	// }

	private static int[] to_2gen_coords(UTonnetzVertexCoord c) { // Ex Si dans [3,4,5] : [0,1,-1] => [-1,2] (puisque X =
																	// g0 et Y = -g2 dans les rseaux hexagonaux)
		int[] coord = new int[2];
		assert (c.size() == 3)
				: "Error : method to_2gen_coords is only implemented for 3-generators simplicial Tonnetz for 2D-hexagonal representation";
		coord[0] = c.get(0) - c.get(1);
		coord[1] = -c.get(2) + c.get(1);
		return coord;
	}

	public static int[] get_2gen_coords(ArrayList<Integer> coord_list) {
		return to_2gen_coords(new UTonnetzVertexCoord(coord_list));
	}

	public static UTonnetzVertexCoord get_normalized_coord(UTonnetzVertexCoord c) {
		if (c.size() != 3)
			System.err.println("norm Not yet implemented for non-hexagonal representations");
		UTonnetzVertexCoord coord = new UTonnetzVertexCoord();
		coord.add(c.get(0) - c.get(2));
		coord.add(c.get(1) - c.get(2));
		coord.add(0);
		return coord;
	}

	public static SquareGridCoordList get_2gen_coords_list(ArrayList<UTonnetzVertexCoord> list) {
		SquareGridCoordList coord_list = new SquareGridCoordList();
		for (UTonnetzVertexCoord c : list)
			coord_list.add(to_2gen_coords(c));
		return coord_list;
	}

	// TODO : peut tre optimis
	public static UTonnetzVertexCoord to_3gen_coords(int[] square_grid_coord) {
		if (square_grid_coord.length != 2)
			System.err.println(
					"Not yet implemented for more than 2-dimensional representations (i.e more than 2 coordinates");
		UTonnetzVertexCoord ton_coord = new UTonnetzVertexCoord();
		ton_coord.add(square_grid_coord[0] + square_grid_coord[1]);
		ton_coord.add(square_grid_coord[1]);
		ton_coord.add(0);
		return ton_coord;
	}

	// public static ArrayList<TonnetzCoord> to_3gen_coords(SquareGridCoordList
	// list){
	// ArrayList<TonnetzCoord> new_list = new ArrayList<TonnetzCoord>();
	// for (int[] square_grid_coord : list) {
	// new_list.add(to_3gen_coords(square_grid_coord));
	// }
	// return new_list;
	// }

	public static UTonnetzCoordList to_3gen_CoordList(SquareGridCoordList list) {
		UTonnetzCoordList new_list = new UTonnetzCoordList();
		for (int[] square_grid_coord : list) {
			new_list.add(to_3gen_coords(square_grid_coord));
		}
		return new_list;
	}

	public static UTonnetzCoordList to_3gen_coords(TableSet set) {
		UTonnetzCoordList new_list = new UTonnetzCoordList();
		for (int[] square_grid_coord : set) {
			new_list.add(to_3gen_coords(square_grid_coord));
		}
		return new_list;
	}

	public static UTonnetzCoordList intersection(UTonnetzCoordList l1, UTonnetzCoordList l2) {
		UTonnetzCoordList intersection = new UTonnetzCoordList();
		for (UTonnetzVertexCoord i1 : l1) {
			for (UTonnetzVertexCoord i2 : l2) {
				if (i1.compareTo(i2) == 0)
					intersection.add(i1);
			}
		}
		return intersection;
	}

	public int get_orbitCount() {
		// return _gcd;
		return _folded_graph_tonnetz._orbit_count;
	}

	public int getOrbit(int note) {
		note = Gcd.reduce(note, _N);
		return note % (_folded_graph_tonnetz._orbit_count);
	}

	public int getOrbit(PitchClassSet ch) {
		int t[] = new int[_folded_graph_tonnetz._orbit_count];
		int i;
		for (i = 0; i < _folded_graph_tonnetz._orbit_count; i++)
			t[i] = 0;
		for (Integer n : ch)
			t[getOrbit(n)]++;
		int o = 0;
		for (i = 1; i < _folded_graph_tonnetz._orbit_count; i++)
			if (t[o] < t[i])
				o = i;
		return o;
	}

	// renvoi les premires coord trouves associs au pitch n dans une List de
	// TonnetzCoord. Si aucun lment ne correspond, null est renvoy
	public UTonnetzVertexCoord pitch_in_list(ArrayList<UTonnetzVertexCoord> list, int pitch) {
		for (UTonnetzVertexCoord c : list) {
			if (c.t_coords_to_pitch(this) == pitch) {
				return c;
			}
		}
		return null;
	}

	public boolean is_pitch_in_CoordList(int pitch, UTonnetzCoordList coord_list) {

		for (UTonnetzVertexCoord c : coord_list) {
			if (c.t_coords_to_pitch(this) == pitch) {
				return true;
			}
		}
		return false;
	}

	// idem avec les x_y coordonnes
	public int[] get_coord_corresponding_to_pitch(ArrayList<int[]> coord_list, int pitch) {
		for (int[] coord : coord_list) {
			// System.out.println("coord : "+Table.toString(coord)+" pitch :
			// "+xy_coord_to_pitch_class(coord));
			if (xy_coord_to_pitch_class(coord) == pitch % _N) {
				return coord;
			}
		}
		return null;
	}

	// renvoi la classe de hauteur associ des XYcoordonnes
	public int xy_coord_to_pitch_class(int[] coords) {
		assert (coords.length == 2) : "method coords_to_pitch implemented for XY coords only";
		return Gcd.reduce(coords[0] * get_generator(0) - coords[1] * get_generator(2), _N);
	}

	// renvoi les pitchs (et leurs Tcoordonnees) voisins d'une position
	public Hashtable neighbor_pitchs(UTonnetzVertexCoord coord) {
		Hashtable table = new Hashtable();
		UTonnetzVertexCoord tc;
		for (int i = 0; i < _generators.size(); i++) {
			tc = new UTonnetzVertexCoord(coord);
			tc.move_one_gen_more(i);
			table.put(tc, tc.t_coords_to_pitch(this));
			tc = new UTonnetzVertexCoord(coord);
			tc.move_one_gen_less(i);
			table.put(tc, tc.t_coords_to_pitch(this));
			// table.put(coord.move_one_gen_more(i),
			// t_coords_to_pitch(coord.move_one_gen_more(i)));
			// table.put(coord.move_one_gen_less(i),
			// t_coords_to_pitch(coord.move_one_gen_less(i)));
		}
		return table;
	}

	// renvoi la liste des coordonnes des plus proches pitchs d'une certaine
	// position. Renvoi vide si le pitch n'est pas dans le rseau
	public ArrayList<UTonnetzVertexCoord> closest_pitches_Tcoords(int pitch, UTonnetzVertexCoord coords) {
		ArrayList<UTonnetzVertexCoord> pitches_coords = new ArrayList<UTonnetzVertexCoord>();
		Hashtable table = neighbor_pitchs(coords);
		return pitches_coords;
	}

	public HashSet<Integer> get_pitch_class_set(int orbit) {
		HashSet<Integer> set = new HashSet<Integer>();
		if (_folded_graph_tonnetz._orbit_count == 1) {
			set = _folded_graph_tonnetz._scale.to_set();
		} else {
			System.err.println("not yet implemented for >1 orbits");
		}
		return set;
	}

	public ArrayList<UTonnetzVertexCoord> first_chord_coords(PitchClassSet c, int orbit) {

		UTonnetzCoordList coords_list = new UTonnetzCoordList();
		PitchClassSet to_draw = new PitchClassSet();
		for (int i : c) {
			if (get_pitch_class_set(orbit).contains(i))
				to_draw.add(i);
		}
		if (to_draw.isEmpty())
			return null;
		UTonnetzVertexCoord searched_note_coords = toCoord(to_draw.get_smallest_pitch());
		coords_list.add(searched_note_coords);
		if (to_draw.size() == 1)
			return coords_list;
		to_draw.remove(to_draw.get_smallest_pitch());
		UTonnetzCoordList circle = coords_list;

		int n = 5;
		while (to_draw.size() != 0 && n > 0) {
			circle = circle.neighbor_coords_set();
			int searched_pitch = to_draw.get_smallest_pitch();
			searched_note_coords = pitch_in_list(circle, searched_pitch);
			if (searched_note_coords != null) {
				coords_list.add(searched_note_coords);
				to_draw.remove(searched_pitch);
				circle = coords_list;
			}
			n--;
		}

		return coords_list;

	}

	public ArrayList<UTonnetzVertexCoord> first_chord_coords(PitchClassSet c) {
		return first_chord_coords(c, 0);
	}

	// renvoi le sous-ensemble de coords_set contenant seulement les coordonnes qui
	// sont associes des pitchs contenus dans l'accord
	public UTonnetzCoordList coords_associated_with_pitches(PitchClassSet c,
			ArrayList<UTonnetzVertexCoord> coords_set) {
		UTonnetzCoordList subset = new UTonnetzCoordList();
		for (UTonnetzVertexCoord coord : coords_set) {
			if (c.contains(coord.t_coords_to_pitch(this))) {
				subset.add(coord);
			}
		}
		return subset;
	}

	// public UTonnetzCoordList get_chord_coords_strategy_vertical(PitchClassSet
	// current_pc_set, SquareGridCoordPath past_coords_tree, TableSet
	// past_coords_set){
	//
	// UTonnetzCoordList new_coords_set = new UTonnetzCoordList();
	//
	// // trouve les dernires coordonnes (prev_coords)
	// UTonnetzCoordList prev_coords =
	// to_3gen_CoordList(past_coords_tree.get_last_non_empty_coord());
	//
	// // dtermine les pitchs dont il faut trouver une position (filtre les pitchs
	// dj illumins juste avant)
	// PitchClassSet pc_to_locate_set = new PitchClassSet();
	// for (int current_pc : current_pc_set){
	// if (get_pitch_class_set(0).contains(current_pc)){
	// UTonnetzVertexCoord tonnetz_coord = pitch_in_list(prev_coords,current_pc);
	// if(tonnetz_coord != null){
	// new_coords_set.add(tonnetz_coord);
	// } else {
	// pc_to_locate_set.add(current_pc);
	// }
	// }
	// }
	//
	// if (pc_to_locate_set.isEmpty()) return new_coords_set;
	//
	// UTonnetzCoordList coord_circle;
	//
	// if (new_coords_set.isEmpty()){
	// // Le nouvel accord n'a aucune note en commun avec le prcdent
	// coord_circle = new UTonnetzCoordList(prev_coords);
	// } else {
	// // Le nouvel accord a des notes en commun avec le prcdent
	// coord_circle = new UTonnetzCoordList(new_coords_set);
	// }
	// coord_circle = coord_circle.neighbor_coords_set();
	//
	// while (pc_to_locate_set.size()!=0){
	//
	// UTonnetzCoordList candidats_coords =
	// coords_associated_with_pitches(pc_to_locate_set,coord_circle);
	// if (candidats_coords.size() != 0){
	// UTonnetzVertexCoord new_coord;
	// new_coord = prev_coords.closest_coords(candidats_coords).get(0); // tape non
	// dterministe
	// new_coords_set.add(new_coord);
	// pc_to_locate_set.remove(new_coord.t_coords_to_pitch(this));
	// coord_circle = new_coords_set;
	// } else {
	// coord_circle = coord_circle.neighbor_coords_set();
	// }
	// }
	//
	// return new_coords_set;
	// }

	// public UTonnetzCoordList get_chord_coords_strategy_horizontal(PitchClassSet
	// current_pc_set, SquareGridCoordPath past_coords_tree, TableSet
	// past_coords_set){
	//
	// UTonnetzCoordList new_coords_set = new UTonnetzCoordList();
	//
	// // trouve les dernires coordonnes (prev_coords)
	// UTonnetzCoordList prev_coords =
	// to_3gen_CoordList(past_coords_tree.get_last_non_empty_coord());
	//
	// // dtermine les pitchs dont il faut trouver une position (filtre les pitchs
	// dj illumins juste avant)
	// PitchClassSet pc_to_locate_set = new PitchClassSet();
	// for (int current_pc : current_pc_set){
	// if (get_pitch_class_set(0).contains(current_pc)){
	// UTonnetzVertexCoord tonnetz_coord = pitch_in_list(prev_coords,current_pc);
	// if(tonnetz_coord != null){
	// new_coords_set.add(tonnetz_coord);
	// } else {
	// pc_to_locate_set.add(current_pc);
	// }
	// }
	// }
	//
	// if (pc_to_locate_set.isEmpty()) return new_coords_set;
	//
	// UTonnetzCoordList coord_circle;
	//
	// // On part quoi qu'il arrive des coordonnes prcdentes
	// coord_circle = new UTonnetzCoordList(prev_coords);
	//
	//
	//
	// while (pc_to_locate_set.size()!=0){
	//
	// coord_circle = coord_circle.neighbor_coords_set();
	//
	// UTonnetzCoordList candidats_coords =
	// coords_associated_with_pitches(pc_to_locate_set,coord_circle);
	// if (candidats_coords.size() != 0){
	// UTonnetzVertexCoord new_coord;
	// for (UTonnetzVertexCoord candidat : candidats_coords){ // tape non
	// dterministe
	// int candidat_pitch = candidat.t_coords_to_pitch(this);
	// if (pc_to_locate_set.contains(candidat_pitch)){
	// new_coords_set.add(candidat);
	// pc_to_locate_set.remove(candidat_pitch);
	// }
	// }
	// }
	// }
	//
	// return new_coords_set;
	//
	// }

	public ArrayList<UTonnetzVertexCoord> n_chord_coords(PitchClassSet current_played_pcset,
			SquareGridCoordPath past_coords_tree, TableSet total_past_coords_set) {

		UTonnetzCoordList predecessor_chord_coords;
		// SquareGridCoordList prev_2_coords;

		long tmp_key = past_coords_tree.lastKey();
		while (past_coords_tree.get(tmp_key).isEmpty()) {
			tmp_key = past_coords_tree.lowerKey(tmp_key);
		}
		// prev_coords = to_3gen_coords(past_coords_tree.get(tmp_key));
		predecessor_chord_coords = to_3gen_CoordList(past_coords_tree.get(tmp_key));
		// prev_2_coords = past_coords_tree.get(tmp_key);

		// if (past_coords_tree.lastEntry().getValue().isEmpty()){
		// prev_coords =
		// to_3gen_coords(past_coords_tree.lowerEntry(past_coords_tree.lastKey()).getValue());
		// } else {
		// prev_coords = to_3gen_coords(past_coords_tree.lastEntry().getValue());
		// }
		// ArrayList<TonnetzCoord> prev_coords =
		// to_3gen_coords(past_coords_tree.lastEntry().getValue());
		// prev_coords = to_3gen_coords(previous_coords_set);
		// System.out.println("prev cord : "+prev_coords);
		// System.out.println("previous_coords_set : "+previous_coords_set);
		UTonnetzCoordList current_played_pcset_coords = new UTonnetzCoordList();
		SquareGridCoordList coords_2_set = new SquareGridCoordList();

		PitchClassSet pc_to_draw_set = new PitchClassSet();
		for (int i : current_played_pcset) {
			// if (_pitch_class_set.contains(i)) to_draw.add(i);
			// TODO To be changed when other orbit vizualisation will be implemented
			if (get_pitch_class_set(0).contains(i))
				pc_to_draw_set.add(i);
		}

		// for (int[] square_grid_coord : prev_2_coords){
		// if (pc_set.contains(get_PC(square_grid_coord))) {
		// coords_2_set.add(square_grid_coord);
		// to_draw.remove(get_PC(square_grid_coord));
		// }
		// }

		// Clause de déplacement minimum stricte : on conserve toutes les pc déjà
		// affichées - risque de perte de compacité statique ("explosion des accords")
		// for (UTonnetzVertexCoord tc : predecessor_chord_coords){
		// if (current_played_pcset.contains(tc.t_coords_to_pitch(this))) {
		// current_played_pcset_coords.add(tc);
		// pc_to_draw_set.remove(tc.t_coords_to_pitch(this));
		// }
		// }

		// Clause de déplacement minimum plus souple : on conserve au max une pc déjà
		// affichée
		UTonnetzVertexCoord choosen_remaining_coord = null;
		for (UTonnetzVertexCoord tc : predecessor_chord_coords) {
			if (current_played_pcset.contains(tc.t_coords_to_pitch(this))) {
				choosen_remaining_coord = tc;
			}
		}
		if (choosen_remaining_coord != null) {
			current_played_pcset_coords.add(choosen_remaining_coord);
			pc_to_draw_set.remove(choosen_remaining_coord.t_coords_to_pitch(this));
		}

		// System.out.println("to_draw : "+pc_to_draw_set);
		// Premier cas : le nouvel accord est un sous-accord du prcdent -> pas de
		// recherche
		if (pc_to_draw_set.isEmpty())
			return current_played_pcset_coords;

		UTonnetzCoordList circle = current_played_pcset_coords;

		// Second cas : le nouvel accord n'a aucune note en commun avec le prcdent.
		// Le cercle de recherche grossit partir de l'accord prcdent
		if (current_played_pcset_coords.isEmpty()) {
			circle = predecessor_chord_coords;
			while (pc_to_draw_set.size() != 0) {
				// System.out.println("avant enlarge circle : "+circle);
				circle = circle.neighbor_coords_set();
				// System.out.println("apres enlarge circle : "+circle);
				UTonnetzCoordList candidats = coords_associated_with_pitches(pc_to_draw_set, circle);
				if (candidats.size() != 0) {
					if (candidats.size() == 1) {
						current_played_pcset_coords.add(candidats.get(0));
						pc_to_draw_set.remove(candidats.get(0).t_coords_to_pitch(this));
					} else {
						UTonnetzCoordList candidats_in_past = intersection(candidats,
								to_3gen_coords(total_past_coords_set)); // privilégier candidats ayant été activé
																		// (n'importe quand) dans le passé
						if (candidats_in_past.size() > 0) {
							if (candidats_in_past.size() == 1) {
								current_played_pcset_coords.add(candidats_in_past.get(0));
								pc_to_draw_set.remove(candidats_in_past.get(0).t_coords_to_pitch(this));
							} else {
								boolean fund = false;
								// ArrayList<TonnetzCoord> candidats_in_past_neighb =
								// intersection(candidats,to_3gen_coords(previous_coords_set));
								for (UTonnetzVertexCoord tcp : candidats_in_past) {

									for (UTonnetzVertexCoord tc : candidats) {
										if (tcp.is_neighbor(tc)) {
											current_played_pcset_coords.add(tcp);
											pc_to_draw_set.remove(tcp.t_coords_to_pitch(this));
											fund = true;
										}
									}
								}
								if (!fund) {
									current_played_pcset_coords.add(candidats_in_past.get(0));
									pc_to_draw_set.remove(candidats_in_past.get(0).t_coords_to_pitch(this));
								}
							}
						} else {
							current_played_pcset_coords.add(candidats.get(0));
							pc_to_draw_set.remove(candidats.get(0).t_coords_to_pitch(this));
						}
					}
					circle = current_played_pcset_coords;
				}
			}
		} else {
			// Troisime cas : les deux accords ont un sous-ensemble en commun OU l'ancien
			// est un sous ensemble du nouveau. Le cercle de recherche grossit partir de
			// l'intersection des 2.
			UTonnetzCoordList erased_notes = new UTonnetzCoordList();
			for (UTonnetzVertexCoord tc : predecessor_chord_coords) {
				if (!current_played_pcset_coords.contains(tc))
					erased_notes.add(tc);
			}
			circle = current_played_pcset_coords;
			while (pc_to_draw_set.size() != 0) {
				boolean found = false;
				circle = circle.neighbor_coords_set();
				UTonnetzVertexCoord new_choosen_coord = null;
				UTonnetzCoordList candidats = coords_associated_with_pitches(pc_to_draw_set, circle);
				if (candidats.size() > 1) {
					for (UTonnetzVertexCoord cand : candidats) {
						if (erased_notes.neighbor_coords_set().contains(cand)) {
							new_choosen_coord = cand;
							found = true;
						}
					}
					if (!found) {
						UTonnetzCoordList candidats_in_past = intersection(candidats,
								to_3gen_coords(total_past_coords_set));
						if (candidats_in_past.size() > 0) {
							new_choosen_coord = candidats_in_past.get(0);
						} else {
							new_choosen_coord = candidats.get(0);
						}
					}
				} else {
					if (candidats.size() == 1)
						new_choosen_coord = candidats.get(0);
				}
				if (new_choosen_coord != null) {
					current_played_pcset_coords.add(new_choosen_coord);
					pc_to_draw_set.remove(new_choosen_coord.t_coords_to_pitch(this));
					circle = current_played_pcset_coords;
				}
			}

		}
		return current_played_pcset_coords;
	}

	public SquareGridCoordList first_chord_XYcoords(PitchClassSet c) {
		ArrayList<UTonnetzVertexCoord> pop = first_chord_coords(c);
		if (pop != null)
			return get_2gen_coords_list(pop);
		else
			return null;
	}

	public SquareGridCoordList n_chord_XYcoords(PitchClassSet c, SquareGridCoordPath past_coords_tree,
			TableSet total_past_coords_set) {
		if (past_coords_tree.lastEntry().getValue() == null)
			return first_chord_XYcoords(c);
		return get_2gen_coords_list(n_chord_coords(c, past_coords_tree, total_past_coords_set));
		// return to_2gen_coords(get_chord_coords_strategy_vertical(c, past_coords_tree,
		// previous_coords_set));
		// return to_2gen_coords(get_chord_coords_strategy_horizontal(c,
		// past_coords_tree, previous_coords_set));
	}

	public String toString() {

		// StringBuffer s = new StringBuffer("K["+_generators.get(0));
		// for (int i = 1;i < _generators.size(); i++) {
		// //s.append(","+Interval.get_name(_generators.get(i)));
		// s.append(","+_generators.get(i));
		// }
		// s.append("]");
		// return s.toString();
		return _folded_complex.toString();
	}

	public String toHTMLString() {
		StringBuffer s = new StringBuffer("<html>C<sub>[" + _generators.get(0));
		for (int i = 1; i < _generators.size(); i++) {
			s.append("," + _generators.get(i));
		}
		s.append("]</sub></html>");
		return s.toString();
	}

	// abstract public ArrayList<Integer> toCoord(int pitch);
	// abstract public TonnetzCoord toCoord(int pitch);

	// public HashSet<Integer> get_pitch_class_set() {
	//
	// return _folded_tonnetz._pitch_class_set;
	// }

	// Fonction retournant les PC voisins d'une PC dans le tonnetz
	public HashSet<Integer> get_neighbors_pitch_class(int n) {
		// HashSet<Integer> neighbors = new HashSet<Integer>();
		// for (Integer g : _generators) {
		// neighbors.add((n+g)%_N);
		// neighbors.add((_N+n-g)%_N);
		// }
		return _folded_graph_tonnetz.get_neighbors_pitch_class(n);
	}

	// Fonction retournant les PC les plus en contact avec un accord dans le tonnetz
	public HashSet<Integer> closer_pitch_class_set(PitchClassSet c, int orbit) {
		HashSet<Integer> s = new HashSet<Integer>();
		int max_neighbors = 0;
		for (Integer n : get_pitch_class_set(orbit)) {
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

	public HashSet<Integer> closer_pitch_class_set(PitchClassSet c) {
		return closer_pitch_class_set(c, 0);
	}

	public HashSet<Integer> closer_pitch_class(PitchSetWithDuration c) {
		return closer_pitch_class_set(c.to_PitchClassSet());
	}

	// Fonction retournant une STChordSeq dont certains STChod ont t "augument"
	// d'une note
	// lorsque celle ci tait unique note la plus proche
	public PitchClassSetSeq get_extra_STvoice(PitchClassSetSeq seq) {
		PitchClassSetSeq new_seq = new PitchClassSetSeq();
		for (PitchClassSet c : seq) {
			if (closer_pitch_class_set(c).size() == 1) {
				PitchClassSet new_chord = c;
				new_chord.add((Integer) closer_pitch_class_set(c).toArray()[0]);
				new_seq.add(new_chord);
			} else
				new_seq.add(c);
		}
		return new_seq;
	}

	// public ChordSeq get_extra_voice(ChordSeq seq) {
	// ChordSeq new_seq = new ChordSeq();
	// for (Chord chord : seq) {
	// if (closer_pitch_class_set(chord.to_STChord(_N)).size() == 1) {
	// Chord c = chord;
	// c.add_pitch_class((Integer)
	// closer_pitch_class_set(c.to_STChord(_N)).toArray()[0], _N);
	// new_seq.add(c);
	// } else new_seq.add(chord);
	// }
	// return new_seq;
	// }

	// public Chord get_extra_voice_chord(Chord chord) {
	//// Chord c = chord;
	//// if
	// return chord;
	// }

	public TonnetzGen get_semi_generators() {
		int demiN = (_N / 2) - (_N % 2);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer i : _generators)
			list.add(-Math.abs((i) - demiN) + demiN);
		return new TonnetzGen(list);
	}

	public IntervallicVector get_IntervalVectorU() {
		int M = (_N / 2) - (_N % 2);
		int iv[] = new int[M];
		for (int i : iv)
			iv[i] = 0;
		for (int g : _generators)
			iv[Interval.MI(g, _N) - 1]++;
		return new IntervallicVector(iv);
	}

	public IntervallicVector get_IntervalVectorF() {
		int M = (_N / 2) - (_N % 2);
		int iv[] = new int[M];
		for (int i : iv)
			iv[i] = 0;
		for (int g : _generators)
			iv[Interval.MI(g, _N) - 1] = 1;
		return new IntervallicVector(iv);
	}

	public int get_connected_components_count() {
		return Gcd.gcd(_generators);
	}

	public int get_directions_count() {
		return _generators.size();
	}

	public static ArrayList<STIntervallicStructure> emerging_IS(HashSet<Integer> intervals) {

		ArrayList<STIntervallicStructure> list = new ArrayList<STIntervallicStructure>();

		return list;
	}

	public Scale get_scale() {
		return _folded_graph_tonnetz._scale;
	}

	public static String latex_name(Collection<Integer> list) {
		return "$T_{" + list + "}$";
	}

	public PitchClassSetStream get_pc_set_stream_from_path(SquareGridCoordPath tonnetz_coord_path) {
		PitchClassSetStream pitch_class_set_stream = new PitchClassSetStream();
		for (long key : tonnetz_coord_path.keySet()) {
			PitchClassSet pitch_class_set = new PitchClassSet();
			// HashSet<Integer> pitch_class_set = new HashSet<Integer>();
			for (int[] coord : tonnetz_coord_path.get(key)) {
				pitch_class_set.add(xy_coord_to_pitch_class(coord));
			}
			pitch_class_set_stream.put(key, pitch_class_set);
		}
		return pitch_class_set_stream;
	}

	private static long default_col_duration = 200;

	public PitchSetStream translation_path_to_col_stream(TonnetzTranslationPath tonnetz_translation_path,
			PitchSet initial_pitch_set) {
		PitchSetStream col_stream = new PitchSetStream(tonnetz_translation_path.get_name());
		PitchSetWithDuration pitch_set_with_duration = new PitchSetWithDuration(initial_pitch_set,
				default_col_duration);
		long time_position = 0;
		col_stream.put(time_position, pitch_set_with_duration);
		// TonnetzCoordSet tonnetz_coord_set =
		// first_chord_XYcoords(initial_pitch_set.to_STChord());
		// PitchSet pitch_set = initial_pitch_set;
		for (TranslationInTonnetz tonnetz_translation : tonnetz_translation_path) {
			time_position = time_position + default_col_duration;
			// tonnetz_coord_set =
			// get_translated_coord_set(tonnetz_coord_set,tonnetz_translation);
			PitchSet pitch_set = get_translated_pitch_set(pitch_set_with_duration, tonnetz_translation);
			pitch_set_with_duration = new PitchSetWithDuration(pitch_set, default_col_duration);
			col_stream.put(time_position, pitch_set_with_duration);
		}

		return col_stream;
	}

	// public TonnetzCoordSet get_translated_coord_set(TonnetzCoordSet
	// tonnetz_coord_set,TonnetzTranslation tonnetz_translation){
	// TonnetzCoordSet translated_tonnetz_coord_set = new TonnetzCoordSet();
	// for (int[] coord : tonnetz_coord_set){
	// assert coord.length == tonnetz_translation.size() : "coord and tonnetz
	// translation are not the same size";
	// int[] translated_coord = new int[coord.length];
	// for (int i=0;i<tonnetz_translation.size();i++){
	// translated_coord[i] = coord[i]+tonnetz_translation.get(i);
	// }
	// translated_tonnetz_coord_set.add(translated_coord);
	// }
	// return translated_tonnetz_coord_set;
	// }

	public PitchSet get_translated_pitch_set(PitchSet pitch_set, TranslationInTonnetz tonnetz_translation) {

		PitchSet new_pitch_set = new PitchSet();
		for (int pitch : pitch_set) {
			new_pitch_set.add(get_translated_pitch(pitch, tonnetz_translation));
		}
		return new_pitch_set;
	}

	private static boolean closer_pitch = true; // on choisit le pitch au dessus ou en dessous formant le + petit
												// interval musical

	public int get_translated_pitch(int pitch, TranslationInTonnetz tonnetz_translation) {
		assert tonnetz_translation.size() == _generators.size()
				: "unfolding vector and translation vector must be of same dimension";
		int translated_pitch = 0;
		if (closer_pitch) {
			translated_pitch = pitch + get_smaller_interval(tonnetz_translation);
		}
		return translated_pitch;
	}

	// Ex : 7 -> -5 ; -1 -> -1 ; -11 -> 1
	public int get_smaller_interval(TranslationInTonnetz tonnetz_translation) {
		assert tonnetz_translation.size() == _generators.size()
				: "unfolding vector and translation vector must be of same dimension";
		int interval = 0;
		for (int i = 0; i < _generators.size(); i++) {
			interval += tonnetz_translation.get(i) * _generators.get(i);
		}
		return Interval.smaller_distance_interval(interval, 12);
	}

	public int get_pitch_central_symmetry(int pitch, CentralSymmetryInTonnetz central_symmetry_in_tonnetz) {
		assert central_symmetry_in_tonnetz.get_center_coord_size() == _generators.size()
				: "unfolding vector and symmetry center coords must be of same dimension";
		int reflected_pitch = 0;
		if (closer_pitch) {
			reflected_pitch = get_closest_pitch_by_central_symetry(pitch, central_symmetry_in_tonnetz);
		}
		return reflected_pitch;

	}

	// Ex : 7 -> -5 ; -1 -> -1 ; -11 -> 1
	private int get_closest_pitch_by_central_symetry(int pitch, CentralSymmetryInTonnetz central_symmetry_in_tonnetz) {
		int pitch_class = pitch % 12;
		float symmetry_center_pitch_class = central_symmetry_in_tonnetz.get_float_center_symetry_pitch_class(this);
		int reflected_pitch_class = Note.pitch_class_symmetry(pitch_class, symmetry_center_pitch_class);
		int interval = reflected_pitch_class - pitch_class;
		int closest_pitch_by_symetry = pitch + Interval.smaller_distance_interval(interval, 12);
		return closest_pitch_by_symetry;
	}

	public PitchSet get_pitch_set_central_symetry(PitchSet pitch_set,
			CentralSymmetryInTonnetz central_symmetry_in_tonnetz) {
		PitchSet new_pitch_set = new PitchSet();
		for (int pitch : pitch_set) {
			new_pitch_set.add(get_pitch_central_symmetry(pitch, central_symmetry_in_tonnetz));
		}
		return new_pitch_set;

	}

	public ArrayList<Integer> get_pc_list_from_coords(SquareGridCoordList coord_list) {
		ArrayList<Integer> pc_list = new ArrayList<Integer>();
		for (int[] coord : coord_list)
			pc_list.add(xy_coord_to_pitch_class(coord));
		return pc_list;
	}

	public TreeMap<Long, ArrayList<Integer>> get_pitch_stream_from_path(SquareGridCoordPath path) {
		TreeMap<Long, ArrayList<Integer>> pc_stream = new TreeMap<Long, ArrayList<Integer>>();
		for (long key : path.keySet()) {
			// if (key == 25000) System.out.println(" key 25000 path : "+path.get(key));
			pc_stream.put(key, get_pc_list_from_coords(path.get(key)));
		}
		return pc_stream;
	}

	/********* TRAJECTORY ALGORITHMS *********/

	// public ArrayList<UTonnetzCoordList> closure(ArrayList<UTonnetzCoordList>
	// complex){
	// ArrayList<UTonnetzCoordList> closure = new ArrayList<UTonnetzCoordList>();
	// HashSet<UTonnetzVertexCoord> vertices = new HashSet<UTonnetzVertexCoord>();
	// for (UTonnetzCoordList simplex : complex){
	// vertices.addAll(simplex);
	// }
	//
	// }

	// public ArrayList<UTonnetzCoordList> star(ArrayList<UTonnetzCoordList>
	// complex){
	//
	// }

	public UTonnetzCoordList closed_star(UTonnetzCoordList vertices) {
		HashSet<UTonnetzVertexCoord> vertex_set = new HashSet<UTonnetzVertexCoord>();
		vertex_set.addAll(vertices);
		for (UTonnetzVertexCoord vertex : vertices)
			vertex_set.addAll(vertex.get_0_1_neighbors());
		UTonnetzCoordList closed_star = new UTonnetzCoordList(vertex_set);
		return closed_star;
	}

	public int Label(UTonnetzVertexCoord v) {
		assert (_generators.size() == v.size()) : "Error : Coordinates don't match with the Tonnetz";
		if (_generators.size() != v.size()) {
			System.err.println("Error : Coordinates don't match with the Tonnetz");
		}
		int pitch = 0;
		for (int i = 0; i < v.size(); i++) {
			pitch = (pitch + (v.get(i) * _generators.get(i)));
		}
		pitch = get_scale().get_PC(pitch);
		while (pitch < 0)
			pitch += 12;
		return pitch % 12;
	}

	public PitchClassSet Label(UTonnetzCoordList complex) {
		PitchClassSet pc_set = new PitchClassSet();
		for (UTonnetzVertexCoord c : complex) {
			pc_set.add(Label(c));
		}
		return pc_set;
	}

	public HashSet<PitchClassSet> presence(PitchClassSet pcset) {
		HashSet<PitchClassSet> set_set = new HashSet<PitchClassSet>();
		HashSet<PitchClassSet> sub_chords = pcset.get_all_sub_pc_set();
		for (PitchClassSet sub_chord : sub_chords) {
			if (contains_simplex_that_represents(sub_chord))
				set_set.add(sub_chord);
		}
		return set_set;
	}

	public ArrayList<UTonnetzCoordList> candidats_R_filtrage(HashSet<PitchClassSet> R, UTonnetzCoordList Kr) {
		ArrayList<UTonnetzCoordList> candidats = new ArrayList<UTonnetzCoordList>();
		ArrayList<PitchClassSet> pcs_list = new ArrayList<PitchClassSet>(R);
		// int size =

		return candidats;
	}

	public void search(HashSet<PitchClassSet> R, UTonnetzCoordList Ki, UTonnetzCoordList Kr) {
		ArrayList<UTonnetzCoordList> candidats = new ArrayList<UTonnetzCoordList>();
		// for (UTonnetzCoordList simplex : Kr){
		// if (Label(simplex).is_contained_in_set(R)){
		// candidats.add(simplex);
		// }
		// }
		if (candidats.isEmpty()) {
			search(R, Ki, closed_star(Kr));
		}
	}

	/********* PLEX *********/

	public ArrayList<Simplex> get_dim_Plex_simplices(int dim) {
		return _folded_complex.get_dim_Plex_simplices(dim);
	}

	public ArrayList<Simplex> get_simplices_representing_n_subchords(PitchClassSet pcs, int n) {

		if (n > pcs.size())
			return null;
		HashSet<PitchClassSet> n_subchords = pcs.get_n_sub_pc_set(n);
		ArrayList<Simplex> simplex_list = new ArrayList<Simplex>();
		ArrayList<Integer> list, list2;
		for (Simplex simplex : get_dim_Plex_simplices(n - 1)) {
			list = new ArrayList<Integer>();
			for (int i : simplex.getVertices())
				list.add(i);
			Collections.sort(list);
			for (PitchClassSet set : n_subchords) {
				list2 = new ArrayList<Integer>(set);
				Collections.sort(list2);
				if (list.equals(list2)) {
					simplex_list.add(simplex);
				}
			}
		}
		return null;
	}

	public boolean contains_simplex_that_represents(PitchClassSet pcs) {
		int size = pcs.size();
		ArrayList<Integer> list;
		for (Simplex simplex : get_dim_Plex_simplices(size - 1)) {
			list = new ArrayList<Integer>();
			for (int i : simplex.getVertices())
				list.add(i);
			if (pcs.containsAll(list))
				return true;
		}
		return false;
	}

}

// class TonnetzCoordList extends ArrayList<TonnetzCoord> {
//
// private static final long serialVersionUID = 1L;
//
// public TonnetzCoordList(){
// super();
// }
//
// public TonnetzCoordList(Collection<TonnetzCoord> tonnetz_coord_list){
// super();
// for (TonnetzCoord tonnetz_coord : tonnetz_coord_list){
// add(new TonnetzCoord(tonnetz_coord));
// }
// }
//
//// return coords neighbor from the coords of the list
// public TonnetzCoordList neighbor_coords(){
// TonnetzCoordList neighbor_coord_list = new TonnetzCoordList();
// for (TonnetzCoord coord : this){
// neighbor_coord_list.addAll(coord.get_neighbors());
// }
// return neighbor_coord_list;
// }
//
//// Same as neighbor_coords but don't take 2 times the same coord (anciennement
// : enlarge_circle) -> non dterministe
// public TonnetzCoordList neighbor_coords_set(){
// TonnetzCoordList neighbor_coord_list = new TonnetzCoordList();
// for (TonnetzCoord coord : this){
// //for (TonnetzCoord neighbor_coord : neighbor_coords(coord)){
// for (TonnetzCoord neighbor_coord : coord.get_neighbors()){
// if (!neighbor_coord_list.contains_coord(neighbor_coord)){
// neighbor_coord_list.add(neighbor_coord);
// }
// }
// }
// return neighbor_coord_list;
// }
//
// public static int neighborhood_count(TonnetzCoordList coord_list,
// TonnetzCoord coord){
// int count = 0;
// for (TonnetzCoord c : coord_list) {
// if (coord.is_neighbor(c)) count++;
// }
// return count;
// }
//
//// return the list of the coords of coord_list which have the more
// neighborhood relationships with coords.
// public TonnetzCoordList closest_coords(TonnetzCoordList coord_list){
// TonnetzCoordList closest_coords_list = new TonnetzCoordList();
// int max=0;
// int neighbors;
// //TonnetzCoord closest_coord = coord_list.get(0);
// for (TonnetzCoord coord : coord_list){
// neighbors = neighborhood_count(this,coord);
// if (neighbors>=max){
// if (neighbors==max){
// closest_coords_list.add(coord);
// } else {
// closest_coords_list.clear();
// closest_coords_list.add(coord);
// max = neighbors;
// }
// }
// }
// return closest_coords_list;
// }

//// return the (one of the) coord of coord_list which has the more neighborhood
//// relationships with coords.
// public TonnetzCoord closest_coord(TonnetzCoordList coord_list){
// int max=0;
// int neighbors;
// TonnetzCoord closest_coord = coord_list.get(0);
// for (TonnetzCoord coord : coord_list){
// neighbors = neighborhood_count(this,coord);
// if (neighbors>max){
// closest_coord = coord;
// max = neighbors;
// }
// }
// return closest_coord;
// }

// public int pitch_size(PlanarUnfoldedTonnetz t){
// HashSet<Integer> set = new HashSet<Integer>();
// for (TonnetzCoord c : this) set.add(c.t_coords_to_pitch(t));
// return set.size();
// }
//
// public ArrayList<Integer> t_coords_to_pitch(PlanarUnfoldedTonnetz t){
// ArrayList<Integer> pitch_list = new ArrayList<Integer>();
// for (TonnetzCoord c : this){
// pitch_list.add(c.t_coords_to_pitch(t));
// }
// return pitch_list;
// }
//
// public boolean contains_coord(TonnetzCoord c){
// for (TonnetzCoord tc : this){
// if (c.compareTo(tc) == 0){
// return true;
// }
// }
// return false;
// }
//
// }

// class TonnetzCoord extends ArrayList<Integer> implements
// Comparable<TonnetzCoord> {
//
// private static final long serialVersionUID = 1576471135047215860L;
//
// public TonnetzCoord(ArrayList<Integer> l) {
// super(l);
// }
//
// public TonnetzCoord() {
// super();
// }
//
// public TonnetzCoord(int[] l) {
// super();
// for (int i : l){
// this.add(i);
// }
// }
//
// @Override
// public int compareTo(TonnetzCoord l) {
// int cmp = ((Integer)(l.size())).compareTo(size());
// if (cmp != 0) return cmp;
// int i;
// for(i=0;i<size();i++) {
// cmp = get(i).compareTo(l.get(i));
// if (cmp != 0) return cmp;
// }
// return 0;
// }
//
// public TonnetzCoord plus(TonnetzCoord coord){
// if (this.size() != coord.size()) {
// System.err.println("Error : try to add different-sized coords");
// return null;
// }
// TonnetzCoord tc = new TonnetzCoord();
// for (int i=0;i<size();i++) tc.add(get(i)+coord.get(i));
// return tc;
// }
//
// public TonnetzCoord less(TonnetzCoord coord){
// if (this.size() != coord.size()) {
// System.err.println("Error : try to add different-sized coords");
// return null;
// }
// TonnetzCoord tc = new TonnetzCoord();
// for (int i=0;i<size();i++) tc.add(get(i)-coord.get(i));
// return tc;
// }
//
//
// public void move_one_gen_more(int n) {
// set(n, this.get(n)+1);
//
// }
// public void move_one_gen_less(int n) {
// set(n, this.get(n)-1);
// }
//
//// renvoi pitch associ des Tcoordonnes
// public int t_coords_to_pitch(PlanarUnfoldedTonnetz t){
// assert (t._generators.size() == size()) : "Error : Coordinates don't match
// with the Tonnetz" ;
// if (t._generators.size() != size()) {
// System.err.println("Error : Coordinates don't match with the Tonnetz");
// }
// int pitch = 0;
// for (int i = 0;i < size();i++){
// pitch = (pitch+(get(i)*t._generators.get(i)));
// }
// pitch = t.get_scale().get_pitch(pitch);
// while (pitch < 0) pitch += 12;
// return pitch%12;
// }
//
// public TonnetzCoordList get_neighbors(){
// assert(size()==3) : "Error : only 3-coord implementation";
// TonnetzCoordList list = new TonnetzCoordList();
// for (int i=0;i<size();i++){
// int[] t = {0,0,0};
// t[i]=1;
// TonnetzCoord c = new TonnetzCoord(t);
// list.add(plus(c));
// list.add(less(c));
// }
// return list;
// }
//
// public boolean is_neighbor(TonnetzCoord c){
// if (get_neighbors().contains_coord(c)) return true;
// return false;
// }
//
// }

// class TonnetzGen extends TonnetzCoord {
//
// private static final long serialVersionUID = 2629006422639861313L;
//
// public TonnetzGen(ArrayList<Integer> l) {
// super(l);
// assert(l.size()!=0);
// Collections.sort(this);
// assert(this.get(0)>0);
// }
//
// public TonnetzGen(int[] l) {
// super(l);
// assert(l.length!=0);
// Collections.sort(this);
// assert(this.get(0)>0);
// }
//
// }
