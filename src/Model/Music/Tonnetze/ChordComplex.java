package Model.Music.Tonnetze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;

import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream;
import edu.stanford.math.plex4.streams.interfaces.AbstractFilteredStream;

import Model.Music.PitchClassSet;
import Model.Music.PitchClassSetSeq;
import Path.PitchClassMappingList;
import Utils.Binomial;
import Utils.Table;

public class ChordComplex {

	protected static int HOMOLOGY_COEF = 2;

	protected HashSet<PitchClassSet> _total_pcs_set;
	private ExplicitSimplexStream _Plex_complex;

	protected ChordComplex() {

	}

	public ChordComplex(HashSet<PitchClassSet> pcs_set) {
		_total_pcs_set = new HashSet<PitchClassSet>();
		for (PitchClassSet pcs : pcs_set) {
			_total_pcs_set.addAll(pcs.get_all_sub_pc_set());
		}
	}

	public ChordComplex(PitchClassSetSeq pcs_seq) {
		_total_pcs_set = new HashSet<PitchClassSet>();
		for (PitchClassSet pcs : pcs_seq) {
			_total_pcs_set.addAll(pcs.get_all_sub_pc_set());
		}
	}

	public HashSet<PitchClassSet> get_total_pcs_set() {
		return _total_pcs_set;
	}

	public int get_dimension() {
		int higher_dimension = 0;
		for (PitchClassSet pcs : _total_pcs_set) {
			if (pcs.size() - 1 > higher_dimension)
				higher_dimension = pcs.size() - 1;
		}
		return higher_dimension;
	}

	public boolean contains_chord(PitchClassSet pcs) {
		for (PitchClassSet pcs2 : get_sized_pitch_class_sets(pcs.size())) {
			if (pcs2.containsAll(pcs))
				return true;
		}
		return false;
	}

	public HashSet<PitchClassSet> get_sized_pitch_class_sets(int size) {
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		for (PitchClassSet pcs : _total_pcs_set) {
			if (pcs.size() == size)
				pcs_set.add(pcs);
		}
		return pcs_set;
	}

	public HashSet<PitchClassSet> get_higher_dim_pitch_class_sets() {
		return get_sized_pitch_class_sets(get_dimension() + 1);
	}

	public ChordComplex get_n_skeleton(int n) {
		if (n >= get_dimension())
			return this;
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		for (PitchClassSet pcs : get_total_pcs_set()) {
			if (pcs.size() <= n + 1)
				pcs_set.add(pcs);
		}
		ChordComplex skeleton = new ChordComplex(pcs_set);
		return skeleton;
	}

	public String toString() {
		return get_total_pcs_set().toString();
	}

	public boolean equals(ChordComplex complex) {
		return get_total_pcs_set().equals(complex.get_total_pcs_set());
	}

	public boolean contains(ChordComplex complex) {
		return get_total_pcs_set().containsAll(complex.get_total_pcs_set());
	}

	public ExplicitSimplexStream get_Plex_complex() {

		if (_Plex_complex != null)
			return _Plex_complex;

		ExplicitSimplexStream stream = new ExplicitSimplexStream();

		for (int dim = 0; dim <= get_dimension(); dim++) {
			HashSet<PitchClassSet> dim_pcset = get_sized_pitch_class_sets(dim + 1);

			for (PitchClassSet pc_set : dim_pcset) {
				// first parameter is the pc_set, second is the filtration index
				stream.addElement(pc_set.to_table(), pc_set.size());
			}
		}
		stream.finalizeStream();

		_Plex_complex = stream;

		return _Plex_complex;
	}

	public void print_Plex_complex() {
		for (Simplex simplex : _Plex_complex) {
			System.out.println(simplex);
			// System.out.println(simplex.getBoundaryCoefficients().length==0);
		}
	}

	// f0 = 1 ; f1 = nombre de sommets ; f2 = nombres d'arcs ; f3 = nombre de
	// triangles ; etc.
	public int get_f_vector_value(int d) {
		if (d == 0)
			return 1;
		return get_sized_pitch_class_sets(d).size();
	}

	public int get_d_simplex_count(int d) {
		return get_f_vector_value(d + 1);
	}

	/* d-compacit d'un complexe seul, dfinit dans MCM13 */
	//
	// f_(d+1)
	// d-compacit = --------
	// (f_1)
	// (d+1)

	// d-compacit moyenne pour un accord quelconque (formule dans la thse)
	public float get_average_compactness(int N, int d) {
		int d_simplex_count = get_d_simplex_count(d);
		int total_chords_count = PitchClassSet.get_size_pcset_count(N, d + 1);
		return (float) (d_simplex_count) / (float) (total_chords_count);
	}

	public float get_complex_d_compactness(int d) {
		int f1 = get_f_vector_value(1);
		int fdim = get_d_simplex_count(d);
		int max_d_simplex = Binomial.binom(f1, d + 1);
		return (float) fdim / max_d_simplex;
	}

	// Proposition 0 de compacit globale. Approche par dcimale : la nime dcimale
	// corresond la n-compacit

	public float get_complex_global_compactness() {
		int dim_max = get_f_vector_value(1) - 1;
		float compacity = 0;
		for (int n = 1; n <= dim_max; n++) {
			compacity = (float) (compacity + get_complex_d_compactness(n) * Math.pow(10, n - dim_max));
		}
		return compacity;
	}

	// Proposition 1 de compacit globale
	// public float get_complex_global_compactness(){
	// int complex_dimension = get_dimension();
	// int vertex_count = get_f_vector_value(1);
	// int dim_simplex_count;
	// int dim_simplex_max;
	// float compliance =0;
	// if (complex_dimension == vertex_count-1) {
	// compliance = 1;
	// return compliance;
	// }
	// compliance = compliance/2;
	//
	//
	// int dec_dim = vertex_count-2;
	// dim_simplex_count = get_d_simplex_count(dec_dim);
	// dim_simplex_max = Binomial.binom(vertex_count, dec_dim+1);
	// if (dim_simplex_count == dim_simplex_max) return compliance;
	//
	//
	// for (int )
	//
	// }

	/* ************** TOPOLOGY ************** */

	public HashSet<PitchClassMappingList> get_inclusion_pc_mapping_set(ChordComplex complex) { // 254.contains(321)
		HashSet<PitchClassMappingList> mapping_set = new HashSet<PitchClassMappingList>();

		if (complex.get_dimension() > get_dimension())
			return mapping_set;
		for (int d = 0; d <= complex.get_dimension(); d++) {
			if (get_d_simplex_count(d) < complex.get_d_simplex_count(d)) {
				return mapping_set;
			}
		}
		int complex_vertices_count = complex.get_d_simplex_count(0);
		ArrayList<ArrayList<Simplex>> solution_list = new ArrayList<ArrayList<Simplex>>();
		ArrayList<Simplex> complex_vertices = complex.get_dim_Plex_simplices(0);
		System.out.println("sous complexe cherch : " + complex_vertices);

		for (ArrayList<Simplex> solution : get_solutions(complex_vertices_count, complex)) {
			// if (is_available_injection(candidat, complex.get_dim_Plex_simplices(0),
			// complex)){
			solution_list.add(solution);
			System.out.println("candidat trouv : " + solution);
			PitchClassMappingList mapping = new PitchClassMappingList();
			for (int i = 0; i < complex_vertices.size(); i++) {
				mapping.add(new int[] { complex_vertices.get(i).getVertices()[0], solution.get(i).getVertices()[0] });
			}
			System.out.println("mapping : " + Table.toString(mapping));
			mapping_set.add(mapping);
			// }
		}

		return mapping_set;
	}

	public ArrayList<ArrayList<Simplex>> get_solutions(int size, ChordComplex complex) {
		ArrayList<ArrayList<Simplex>> seqs = new ArrayList<ArrayList<Simplex>>();
		ArrayList<Simplex> vertices = get_dim_Plex_simplices(0);
		ArrayList<Simplex> new_list = new ArrayList<Simplex>();
		for (int i = 0; i < size; i++)
			new_list.add(null);
		recur2(new_list, 0, vertices, seqs, complex);
		return seqs;
	}

	public void recur2(ArrayList<Simplex> list, int index, ArrayList<Simplex> vertices,
			ArrayList<ArrayList<Simplex>> seqs, ChordComplex complex) {
		for (Simplex vertex : vertices) {
			if (!(list.contains(vertex) && list.indexOf(vertex) < index)) {
				list.set(index, vertex);
				if (index == list.size() - 1) {
					if (is_available_injection(list, complex.get_dim_Plex_simplices(0), complex)) {
						seqs.add(new ArrayList<Simplex>(list));
					}
				} else {
					recur2(list, index + 1, vertices, seqs, complex);
				}
			}
		}
	}

	public boolean is_same_complex_structure(ChordComplex complex) {

		if (get_dimension() != complex.get_dimension())
			return false;
		int dim = get_dimension();
		for (int d = 0; d <= dim; d++) {
			if (get_d_simplex_count(d) != complex.get_d_simplex_count(d))
				return false;
		}
		for (int d = 0; d <= get_dimension(); d++)
			if (get_betti_number(d) != complex.get_betti_number(d))
				return false;

		ArrayList<Simplex> vertices_a = get_dim_Plex_simplices(0);
		// System.out.println("vertices A = "+vertices_a);
		ArrayList<Simplex> vertices_b = complex.get_dim_Plex_simplices(0);

		ArrayList<Simplex> new_list = new ArrayList<Simplex>(vertices_b);
		ArrayList<ArrayList<Simplex>> sk1_solution_list = new ArrayList<ArrayList<Simplex>>();

		recur(complex, vertices_a, vertices_b, new_list, sk1_solution_list, 0);

		// System.out.println("1SK RESULTATS : \n"+sk1_solution_list);

		ArrayList<ArrayList<Simplex>> real_solution_list = new ArrayList<ArrayList<Simplex>>();

		for (ArrayList<Simplex> candidate : sk1_solution_list) {
			if (is_available_bijection(vertices_a, candidate, complex))
				real_solution_list.add(candidate);
		}

		if (real_solution_list.size() != 0) {
			// System.out.println("REAL SOLUTIONS : ");
			// for (ArrayList<Simplex> solution : real_solution_list)
			// System.out.println(solution);
			return true;
		}

		return false;
	}

	public void recur(ChordComplex complex, ArrayList<Simplex> vertices_a, ArrayList<Simplex> vertices_b,
			ArrayList<Simplex> new_list, ArrayList<ArrayList<Simplex>> sk1_solution_list, int index) {
		int cofaces_count = get_cofaces(vertices_a.get(index)).size();
		for (Simplex vertex : vertices_b) {
			if (!(new_list.contains(vertex) && (new_list.indexOf(vertex) < index))) {
				if (complex.get_cofaces(vertex).size() == cofaces_count) {
					new_list.set(index, vertex);
					if (index == vertices_a.size() - 1) {
						sk1_solution_list.add(new ArrayList<Simplex>(new_list));
					} else {
						recur(complex, vertices_a, vertices_b, new_list, sk1_solution_list, index + 1);
					}
				}
			}
		}
	}

	public boolean is_available_bijection(ArrayList<Simplex> vertices_a, ArrayList<Simplex> vertices_b,
			ChordComplex complex) {

		int dimension = complex.get_dimension();
		for (int d = 1; d <= dimension; d++) {
			for (Simplex simplex : get_dim_Plex_simplices(d)) {
				ArrayList<Simplex> incident_vertices = get_p_neighbors(simplex, 0);
				ArrayList<Integer> index_a_list = new ArrayList<Integer>();
				for (Simplex vertex : incident_vertices)
					index_a_list.add(vertices_a.indexOf(vertex));
				ArrayList<Simplex> corresponding_b_vertices = new ArrayList<Simplex>();
				for (int index : index_a_list)
					corresponding_b_vertices.add(vertices_b.get(index));
				if (!complex.contains_simplex_exactly_surrounded_by_vertices(corresponding_b_vertices))
					return false;
			}
		}
		return true;
	}

	public boolean is_available_injection(ArrayList<Simplex> vertices_a, ArrayList<Simplex> vertices_b,
			ChordComplex complex) {

		int dimension = complex.get_dimension();
		for (int d = 1; d <= dimension; d++) {
			for (Simplex simplex : complex.get_dim_Plex_simplices(d)) {
				ArrayList<Simplex> incident_vertices = complex.get_p_neighbors(simplex, 0);
				ArrayList<Integer> index_b_list = new ArrayList<Integer>();
				for (Simplex vertex : incident_vertices)
					index_b_list.add(vertices_b.indexOf(vertex));
				ArrayList<Simplex> corresponding_a_vertices = new ArrayList<Simplex>();
				for (int index : index_b_list)
					corresponding_a_vertices.add(vertices_a.get(index));
				if (!contains_simplex_exactly_surrounded_by_vertices(corresponding_a_vertices))
					return false;
			}
		}
		return true;
	}

	public boolean contains_simplex_exactly_surrounded_by_vertices(ArrayList<Simplex> vertices) {

		int dim = vertices.size() - 1;
		for (Simplex d_simplex : get_dim_Plex_simplices(dim)) {
			ArrayList<Simplex> d_simplex_vertices = get_p_neighbors(d_simplex, 0);
			if (vertices.containsAll(d_simplex_vertices)) {
				return true;
			}
		}
		return false;
	}

	public int get_euler_characteristic() {

		int e_char = 0;
		for (int dim = 0; dim <= get_dimension(); dim++) {
			e_char = e_char + ((get_sized_pitch_class_sets(dim + 1).size()) * (int) Math.pow(-1, dim));
		}
		return e_char;
	}

	/* PLEX */

	public BarcodeCollection<java.lang.Integer> get_bar_code() {
		AbstractFilteredStream<Simplex> complex = get_Plex_complex();
		AbstractPersistenceAlgorithm<Simplex> persistence = Plex4.getModularSimplicialAlgorithm(get_dimension() + 1,
				HOMOLOGY_COEF);
		// BarcodeCollection<java.lang.Double> bar_code =
		// persistence.computeIntervals(complex);
		BarcodeCollection<java.lang.Integer> bar_code = persistence.computeIndexIntervals(complex);
		return bar_code;
	}

	// Nombre de composantes connexes. = Nombre de Betty b0
	public int get_connected_componnent_count() {
		Map<Integer, Integer> betty_numbers = null;
		betty_numbers = get_betty_numbers();
		if (betty_numbers.containsKey(0)) {
			return get_bar_code().getBettiNumbersMap(get_dimension() + 1).get(0);
		}
		return 0;
	}

	// Nombre de trous circulaires. = Nombre de Betty b1
	public int get_circular_holes_count() {
		Map<Integer, Integer> betty_numbers = get_betty_numbers();
		if (betty_numbers.containsKey(1)) {
			return get_bar_code().getBettiNumbersMap(get_dimension() + 1).get(1);
		}
		return 0;
	}

	// Nombre de vides. = Nombre de Betty b2
	public int get_holes_count() {
		Map<Integer, Integer> betty_numbers = get_betty_numbers();
		if (betty_numbers.containsKey(2)) {
			return get_bar_code().getBettiNumbersMap(get_dimension() + 1).get(2);
		}
		return 0;
	}

	// Nombre de Betty n
	public int get_betti_number(int n) {
		Map<Integer, Integer> betty_numbers = get_betty_numbers();
		if (betty_numbers.containsKey(n)) {
			return get_bar_code().getBettiNumbersMap(get_dimension() + 1).get(n);
		}
		return 0;
	}

	// Nombres de Betty
	public Map<Integer, Integer> get_betty_numbers() {
		return get_bar_code().getBettiNumbersMap(get_dimension() + 1);
	}

	public ArrayList<Integer> get_betti_numbers_list() {

		ArrayList<Integer> betti_numbers_list = new ArrayList<Integer>();
		Map<Integer, Integer> betty_numbers = get_betty_numbers();
		for (int n = 0; n <= get_dimension(); n++) {
			if (betty_numbers.containsKey(n)) {
				betti_numbers_list.add(get_bar_code().getBettiNumbersMap(get_dimension() + 1).get(n));

			} else {
				betti_numbers_list.add(0);
			}
		}

		verif(betti_numbers_list);
		return betti_numbers_list;
	}

	public void verif(ArrayList<Integer> betti_list) {
		double alt_sum_betti = 0;
		for (int i = 0; i < betti_list.size(); i++) {
			alt_sum_betti = alt_sum_betti + (Math.pow(-1, i) * betti_list.get(i));
		}
		if (alt_sum_betti != get_euler_characteristic()) {
			System.err.println("Incohrence Betti : " + betti_list + " et Euler : " + get_euler_characteristic());
		}
	}

	// returns all simplices of the complex
	public ArrayList<Simplex> get_Plex_simplices() {
		ArrayList<Simplex> simplices = new ArrayList<Simplex>();
		Iterator<Simplex> i = get_Plex_complex().iterator();
		while (i.hasNext())
			simplices.add(i.next());
		return simplices;
	}

	public ArrayList<Simplex> get_dim_Plex_simplices(int dim) {
		ArrayList<Simplex> dim_simplices = new ArrayList<Simplex>();
		Simplex simplex;
		Iterator<Simplex> i = get_Plex_complex().iterator();
		while (i.hasNext()) {
			simplex = i.next();
			if (simplex.getDimension() == dim)
				dim_simplices.add(simplex);
		}
		return dim_simplices;
	}

	// only returns incident cells of dim d-1
	public ArrayList<Simplex> get_faces(Simplex simplex) {
		ArrayList<Simplex> faces = new ArrayList<Simplex>();
		for (Simplex s : simplex.getBoundaryArray()) {
			faces.add(s);
		}
		return faces;
	}

	// returns neighbors of higher dimension (cofaces, cofaces of the cofaces, etc.)
	public ArrayList<Simplex> get_higher_neighbors(Simplex simplex) {
		ArrayList<Simplex> higher_neighbors = new ArrayList<Simplex>();
		ArrayList<Integer> simplex_vertices = new ArrayList<Integer>();
		for (int v : simplex.getVertices())
			simplex_vertices.add(v);
		for (Simplex s : get_Plex_simplices()) {
			ArrayList<Integer> s_vertices = new ArrayList<Integer>();
			for (int v : s.getVertices())
				s_vertices.add(v);
			if (s_vertices.containsAll(simplex_vertices) && simplex != s)
				higher_neighbors.add(s);
		}
		return higher_neighbors;
	}

	// returns neighbors of lower dimension (faces, faces of the faces, etc.)
	public ArrayList<Simplex> get_lower_neighbors(Simplex simplex) {
		ArrayList<Simplex> lower_neighbors = new ArrayList<Simplex>();
		ArrayList<Integer> simplex_vertices = new ArrayList<Integer>();
		for (int v : simplex.getVertices())
			simplex_vertices.add(v);
		for (Simplex s : get_Plex_simplices()) {
			ArrayList<Integer> s_vertices = new ArrayList<Integer>();
			for (int v : s.getVertices())
				s_vertices.add(v);
			if (simplex_vertices.containsAll(s_vertices) && simplex != s)
				lower_neighbors.add(s);
		}
		return lower_neighbors;
	}

	public ArrayList<Simplex> get_all_neighbors(Simplex simplex) {
		ArrayList<Simplex> neighbors = new ArrayList<Simplex>();
		neighbors.addAll(get_higher_neighbors(simplex));
		neighbors.addAll(get_lower_neighbors(simplex));
		return neighbors;
	}

	// returns neighbors (faces, faces of faces, cofaces, cofaces of cofaces, etc.)
	// of dimension p
	public ArrayList<Simplex> get_p_neighbors(Simplex simplex, int p) {
		ArrayList<Simplex> p_neighbors = new ArrayList<Simplex>();
		if (simplex.getDimension() == p)
			return p_neighbors;
		ArrayList<Integer> simplex_vertices = new ArrayList<Integer>();
		for (int v : simplex.getVertices())
			simplex_vertices.add(v);
		for (Simplex s : get_Plex_simplices()) {
			if (s.getDimension() == p) {
				ArrayList<Integer> s_vertices = new ArrayList<Integer>();
				for (int v : s.getVertices())
					s_vertices.add(v);
				if (p > simplex.getDimension()) {
					if (s_vertices.containsAll(simplex_vertices))
						p_neighbors.add(s);
				} else {
					if (simplex_vertices.containsAll(s_vertices))
						p_neighbors.add(s);
				}
			}
		}
		return p_neighbors;

	}

	// Two d-cells are (d,p)-neighbor if they have a common border of dimension p
	// when p n or if they are in the boundary of a p-cell of higher dimension.
	public ArrayList<Simplex> get_d_p_neighbors(Simplex simplex, int p) {
		HashSet<Simplex> d_p_neighbors = new HashSet<Simplex>();
		int d = simplex.getDimension();
		ArrayList<Simplex> p_neighbors = get_p_neighbors(simplex, p);
		for (Simplex p_neighbor : p_neighbors) {
			for (Simplex d_p_neighbor : get_p_neighbors(p_neighbor, d)) {
				if (d_p_neighbor != simplex)
					d_p_neighbors.add(d_p_neighbor);
			}
		}
		return new ArrayList<Simplex>(d_p_neighbors);
	}

	// for a d-simplex : neighbor simplices of dimension d+1
	public ArrayList<Simplex> get_cofaces(Simplex simplex) {

		assert get_Plex_complex().containsElement(simplex) : "The complex does not contains the simplex " + simplex;
		int simplex_dim = simplex.getDimension();
		ArrayList<Simplex> cofaces = new ArrayList<Simplex>();
		ArrayList<Simplex> just_higher_simplices = get_dim_Plex_simplices(simplex_dim + 1);
		for (Simplex higher_simplex : just_higher_simplices) {
			if (get_faces(higher_simplex).contains(simplex))
				cofaces.add(higher_simplex);
		}
		return cofaces;
	}

	// public boolean is_a_surface(){
	// if ((get_dimension() !=2) || (get_connected_componnent_count() != 1)) return
	// false;
	// ArrayList<Simplex> one_simplices = get_dim_Plex_simplices(1);
	// for (Simplex edge : one_simplices){
	// ArrayList<Simplex> cofaces = get_cofaces(edge);
	// if(cofaces.size()>2 || cofaces.size()<1) return false;
	// }
	// return true;
	// }
	//
	// public boolean is_a_volume(){
	// if ((get_dimension() !=3) || (get_connected_componnent_count() != 1)) return
	// false;
	// ArrayList<Simplex> two_simplices = get_dim_Plex_simplices(2);
	// for (Simplex surface : two_simplices){
	// ArrayList<Simplex> cofaces = get_cofaces(surface);
	// if(cofaces.size()>2 || cofaces.size()<1) return false;
	// }
	// return true;
	// }

	// Ne veut rien dire pour l'instant (Il n'existe pas de terme pour qualifier
	// cette proprit)
	// public boolean is_a_n_surface(){
	// int dimension = get_dimension();
	// if (get_connected_componnent_count() != 1) return false;
	// ArrayList<Simplex> one_simplices = get_dim_Plex_simplices(dimension-1);
	// for (Simplex edge : one_simplices){
	// ArrayList<Simplex> cofaces = get_cofaces(edge);
	// if(cofaces.size()>2 || cofaces.size()<1) return false;
	// }
	// return true;
	// }

	// The d-complex D is strongly connected if D satisfies the following condition
	// : for any d-simplex I and I', there is a sequence of d-simplex I = I1, I2,
	// ... , Is = I' tel que dim(Ii inter Ii+1)=d-1 pour tout 1<=i<=s-1
	public boolean is_strongly_connected() {

		int dim = get_dimension();
		ArrayList<Simplex> simplices = get_dim_Plex_simplices(dim);
		if (simplices.isEmpty())
			return false;
		int size = simplices.size();

		HashSet<Simplex> d_simplices = new HashSet<Simplex>();
		d_simplices.add(simplices.get(0));
		HashSet<Simplex> tmp_set = new HashSet<Simplex>();
		int count;
		while (true) {
			tmp_set.clear();
			for (Simplex s : d_simplices) {
				tmp_set.addAll(get_d_p_neighbors(s, dim - 1));
			}
			count = d_simplices.size();
			d_simplices.addAll(tmp_set);
			if (d_simplices.size() == size)
				return true;
			if (d_simplices.size() == count) {
				return false;
			}
		}
	}

	// A d-complex is a pseudo-variety if
	// - it is strongly connected,
	// - if each simplex of the d-complex is the face of at least one d-simplex,
	// - each (d-1)-simplex is the face of maximum two simplexes
	public boolean is_pseudo_variety() {
		if (!is_strongly_connected())
			return false;

		// checks if each simplex of the d-complex is the face of at least one d-simplex
		int dim = get_dimension();
		ArrayList<Simplex> d_simplices = get_dim_Plex_simplices(dim);
		HashSet<Simplex> lower_faces = new HashSet<Simplex>();
		for (Simplex d_simplex : d_simplices)
			lower_faces.addAll(get_lower_neighbors(d_simplex));
		if (lower_faces.size() != (get_Plex_simplices().size() - d_simplices.size()))
			return false;

		// checks if each (d-1)-simplex is the face of maximum two simplexes
		ArrayList<Simplex> d_m1_simplices = get_dim_Plex_simplices(dim - 1);
		for (Simplex d_m1_simplex : d_m1_simplices) {
			if (get_cofaces(d_m1_simplex).size() > 2)
				return false;
		}

		return true;
	}

	public boolean has_boundary() {
		int dimension = get_dimension();
		ArrayList<Simplex> one_less_dim_simpleces = get_dim_Plex_simplices(dimension - 1);
		for (Simplex simplex : one_less_dim_simpleces) {
			if (get_cofaces(simplex).size() < 2)
				return true;
		}
		return false;
	}

	public void print_all_simplex() {
		Simplex simplex;
		Iterator<Simplex> i = get_Plex_complex().iterator();
		while (i.hasNext()) {
			simplex = i.next();
			// System.out.println("simplex : "+simplex+" boundary :
			// "+Table.toString(simplex.getBoundaryArray()));
			// System.out.println("simplex : "+simplex+" boundary :
			// "+Table.toString(simplex.getBoundaryArray()));
			// System.out.println("simplex : "+simplex+" d,1-neighbors :
			// "+get_d_p_neighbors(simplex,1));
		}
	}

}
