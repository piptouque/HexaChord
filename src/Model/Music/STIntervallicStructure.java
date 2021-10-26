package Model.Music;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import Model.Music.Tonnetze.FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z12FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z7FoldedGraphTonnetz;
import Utils.GraphViz;
import Utils.IntegerBag;
import Utils.IntegerSet;
import Utils.OrientedRing;
import Utils.Table;

public class STIntervallicStructure extends OrientedRing {

	public STIntervallicStructure(PitchClassSet ch, int N) {
		super();

		// Listify the chord pitches
		ArrayList<Integer> notes = new ArrayList<Integer>();
		for (Integer n : ch)
			notes.add(n);

		// Sort list of pitches
		Collections.sort(notes);

		// Pairwise differences
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		int i;
		for (i = 0; i < notes.size(); i++)
			intervals.add(((notes.get((i + 1) % notes.size()) - notes.get(i)) + N) % N);

		_list = intervals;
		// normalization_rotate();
	}

	public STIntervallicStructure(ArrayList<Integer> list) {
		super(list);
	}

	public STIntervallicStructure(int[] list) {
		super(list);
	}

	public int get_N() {
		int sum = sum();
		if (sum == 0)
			return 12;
		return sum;
	}

	public FoldedGraphTonnetz get_z12_folded_graph_tonnetz() {

		assert get_N() == 12 : "Intervallic Structure " + this + " is not of congruence 12.";
		return new Z12FoldedGraphTonnetz(get_MI_IntervalContent_set());
	}

	public FoldedGraphTonnetz get_z7_folded_graph_tonnetz(Scale scale) {
		assert get_N() == 7 : "Not yet implemented for Z dif to 7";
		return new Z7FoldedGraphTonnetz(scale, get_MI_IntervalContent_set());
	}

	// public Z12PlanarUnfoldedTonnetz getZ12UnfoldedTonnetz() {
	// return Z12PlanarUnfoldedTonnetz.getZ12UnfoldedTonnetz(this);
	// }

	// // retourne l'accord primaire (commencant par C)
	// public PitchClassSet get_prime_PCSet(){
	// PitchClassSet pc_set = new PitchClassSet();
	// pc_set.add(0);
	// int n=0;
	// for (int i=0;i<_list.size()-1;i++){
	// pc_set.add(n+_list.get(i));
	// n=n+_list.get(i);
	// }
	// return pc_set;
	// }

	// retourne prime chord : intervales croissant, premier pitch = 0
	public PitchClassSet get_prime_PCSet(boolean oriented) {
		int N = get_N();
		// System.out.println("abbbbbbbbb "+N);
		PitchClassSet pc_set = new PitchClassSet(N);
		normalization_rotate(oriented);
		// System.out.println("after norma : "+this);
		pc_set.add(0);
		int n = 0;
		for (int i = 0; i < _list.size() - 1; i++) {
			pc_set.add((n + _list.get(i)) % N);
			n = (n + _list.get(i)) % N;
		}
		return pc_set;
	}

	// retourne l'accord dans l'ordre des intervalles primaires (i.e. pas son
	// inverse) commencant par pitch.
	public PitchClassSet get_prime_order_PCSet(int pitch) {

		int N = get_N();
		PitchClassSet pc_set = new PitchClassSet(N);
		pc_set.add(pitch);
		int n = pitch;
		for (int i = 0; i < _list.size() - 1; i++) {
			pc_set.add((n + _list.get(i)) % N);
			n = (n + _list.get(i)) % N;
		}
		return pc_set;
	}

	// retourne l'ensemble des PC Sets instance de la SI (ie transposes uniquement)

	public PitchClassSetSeq get_transpositions() {
		PitchClassSetSeq instances_set = new PitchClassSetSeq();

		int N = get_N();
		for (int i = 0; i < N; i++) {
			PitchClassSet pcs = get_prime_order_PCSet(i);
			instances_set.add(pcs);
		}
		return instances_set;
	}

	// retourne l'ensemble des PC Sets instance de la SI up to flip (ie transposes
	// et inversions)

	public PitchClassSetSeq get_transpositions_and_inversions() {
		PitchClassSetSeq instances_set = new PitchClassSetSeq();

		int N = get_N();
		for (int i = 0; i < N; i++) {
			PitchClassSet pcs = get_prime_order_PCSet(i);
			if (!instances_set.contains(pcs))
				instances_set.add(pcs);
			if (!instances_set.contains(pcs.inversion(0)))
				instances_set.add(pcs.inversion(0));
		}
		return instances_set;
	}

	public PitchClassSetSeq get_chords_up_to_is_perm() {
		HashSet<STIntervallicStructure> is_permutations = get_is_permutations();
		PitchClassSetSeq pcs_seq = new PitchClassSetSeq();
		for (STIntervallicStructure is : is_permutations) {
			pcs_seq.addAll(is.get_transpositions_and_inversions());
		}
		return pcs_seq;
	}

	public IntervallicVector get_IntervalVector() { // Allen Forte. Ex : (O47) -> [001110]

		return get_prime_order_PCSet(0).get_MI_IntervalVector();
	}

	public IntegerSet get_MI_IntervalContent_set() {
		// System.out.println("is : "+this);
		// System.out.println("return :
		// "+get_default_STChord().get_MI_IntervalContent_set());
		// return get_default_STChord().get_MI_IntervalContent_set();
		return get_MI_IntervalContent_multiset().setify();
	}

	public IntegerBag get_MI_IntervalContent_multiset() {

		IntegerBag bag = new IntegerBag();
		int N = get_N();
		for (int i = 0; i < _list.size() - 1; i++) {
			for (int j = i; j < _list.size() - 1; j++) {
				int sum = 0;
				for (int k = i; k <= j; k++) {
					sum = sum + _list.get(k);
				}
				bag.add(Interval.MI(sum, N));
			}
		}
		return bag;
	}

	public IntegerBag get_bag() {
		return new IntegerBag(_list);
	}

	/* ---------- STATIC METHODS ---------- */

	// Pour N = 12, retourne les Forte classes de taille size (ie. distingue 3,4,5
	// de 4,3,5
	public static ArrayList<STIntervallicStructure> enum_SI(int size, int N) {

		ArrayList<STIntervallicStructure> is_list = new ArrayList<STIntervallicStructure>();
		if (size == 0)
			return is_list;
		if (size == 1) {
			is_list.add(new STIntervallicStructure(new int[] { N }));
			return is_list;
		}
		if (size == N) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < N; i++) {
				list.add(1);
			}
			is_list.add(new STIntervallicStructure(list));
			return is_list;
		}

		int max = N + 1 - size;

		int[] si = new int[size];
		for (int i = 0; i < size; i++)
			si[i] = 1;

		while (!(si[size - 1] == max)) {
			int sum = 0;
			for (int in : si)
				sum = sum + in;
			if (sum == N) {
				STIntervallicStructure ring_si = new STIntervallicStructure(si);
				if (!ring_si.is_containedSI(is_list))
					is_list.add(ring_si);
			}

			int i = 0;
			boolean b = true;

			while (b) {
				if (si[i] == max) {
					si[i] = 1;
					i++;
				} else {
					si[i]++;
					i = 0;
					b = false;
				}
			}
		}
		return is_list;
	}

	public static List<STIntervallicStructure> enum_SI_up_to_size(int size, int N) {
		List<STIntervallicStructure> is_list = new ArrayList<STIntervallicStructure>();
		for (int i = 1; i <= size; i++)
			is_list.addAll(enum_SI(i, N));
		return is_list;
	}

	// Liste les SI de toute taille pour un systme de congruence N donn.
	// Pour N = 12, renvoie les 350 Forte Classes (351 avec la "classe vide")

	static public ArrayList<STIntervallicStructure> SI_12;

	public static ArrayList<STIntervallicStructure> enum_SI(int N) {
		if (N == 12) {
			if (SI_12 == null) {
				SI_12 = new ArrayList<STIntervallicStructure>();
				for (int i = 2; i <= N; i++) {
					SI_12.addAll(enum_SI(i, 12));
				}
			}
			System.out.println("et voic SI_12 : " + SI_12);
			return SI_12;
		}
		// System.err.println("Tiens, on a une congruence diffrente de 12 ... ?");
		ArrayList<STIntervallicStructure> si_list = new ArrayList<STIntervallicStructure>();
		for (int i = 2; i <= N; i++) {
			si_list.addAll(enum_SI(i, N));
		}
		return si_list;
	}

	static public ArrayList<STIntervallicStructure> SI_3_Z12_UTF;
	static public ArrayList<STIntervallicStructure> SI_3_Z7_UTF;

	public static ArrayList<STIntervallicStructure> SI_3_12_7_UTF;

	public static ArrayList<STIntervallicStructure> get_SI_3_12_7_UTF() {

		if (SI_3_12_7_UTF == null) {
			SI_3_12_7_UTF = new ArrayList<STIntervallicStructure>();
			SI_3_12_7_UTF.addAll(enum_SI_up_to_flip(3, 12));
			SI_3_12_7_UTF.addAll(enum_SI_up_to_flip(3, 7));
		}
		return SI_3_12_7_UTF;
	}

	// Pour N = 12, retourne les SI UP TO FLIP de taille size (ie. on ne distingue
	// pas 3,4,5 de 4,3,5
	public static ArrayList<STIntervallicStructure> enum_SI_up_to_flip(int size, int N) {

		if (size == 3) {
			if (N == 12) {
				if (SI_3_Z12_UTF != null) {
					return SI_3_Z12_UTF;
				}
			}
			if (N == 7) {
				if (SI_3_Z7_UTF != null) {
					return SI_3_Z7_UTF;
				}
			}
		}

		ArrayList<STIntervallicStructure> is_list = new ArrayList<STIntervallicStructure>();
		if (size == 0)
			return is_list;
		if (size == 1) {
			is_list.add(new STIntervallicStructure(new int[] { 0 }));
			return is_list;
		}
		if (size == N) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < N; i++) {
				list.add(1);
			}
			is_list.add(new STIntervallicStructure(list));
			return is_list;
		}

		int max = N + 1 - size;

		int[] si = new int[size];
		for (int i = 0; i < size; i++)
			si[i] = 1;

		while (!(si[size - 1] == max)) {
			int sum = 0;
			for (int in : si)
				sum = sum + in;
			if (sum == N) {
				STIntervallicStructure ring_si = new STIntervallicStructure(si);
				if (!ring_si.is_contained_up_to_flipSI(is_list)) {
					ring_si.normalization_rotate(false); // car c'est UTF
					is_list.add(ring_si);
				}
			}

			int i = 0;
			boolean b = true;

			while (b) {
				if (si[i] == max) {
					si[i] = 1;
					i++;
				} else {
					si[i]++;
					i = 0;
					b = false;
				}
			}
		}

		if (size == 3) {
			if (N == 12)
				SI_3_Z12_UTF = new ArrayList<STIntervallicStructure>(is_list);
			if (N == 7)
				SI_3_Z7_UTF = new ArrayList<STIntervallicStructure>(is_list);
		}
		return is_list;
	}

	// Liste les SI UP TO FLIP de toute taille pour un systme de congruence N donn.
	// Pour N = 12, renvoie les 224 orbites de l'action de Dn sur Z12

	static public ArrayList<STIntervallicStructure> SI_12_UTF;

	static public ArrayList<STIntervallicStructure> enum_SI_up_to_flip(int N) {
		if (N == 12) {
			if (SI_12_UTF == null) {
				SI_12_UTF = new ArrayList<STIntervallicStructure>();
				for (int i = 2; i <= N; i++) {
					SI_12_UTF.addAll(enum_SI_up_to_flip(i, 12));
				}
			}
			System.out.println("et voic SI_12_UTF : " + SI_12_UTF);
			return SI_12_UTF;
		}
		// System.err.println("Tiens, on a une congruence diffrente de 12 ... ?");
		ArrayList<STIntervallicStructure> si_list = new ArrayList<STIntervallicStructure>();
		for (int i = 2; i <= N; i++) {
			si_list.addAll(enum_SI_up_to_flip(i, N));
		}
		return si_list;
	}

	static public STIntervallicStructure get_major_IS() {
		return new STIntervallicStructure(new int[] { 3, 5, 4 });
	}

	boolean is_major_IS() {
		return this.equals(get_major_IS());
	}

	static public STIntervallicStructure get_minor_IS() {
		return new STIntervallicStructure(new int[] { 3, 4, 5 });
	}

	boolean is_minor_IS() {
		return this.equals(get_minor_IS());
	}

	static public STIntervallicStructure get_diminished_IS() {
		return new STIntervallicStructure(new int[] { 3, 3, 6 });
	}

	static public STIntervallicStructure get_augmented_IS() {
		return new STIntervallicStructure(new int[] { 4, 4, 4 });
	}

	static public STIntervallicStructure get_dominant_seventh_IS() {
		return new STIntervallicStructure(new int[] { 4, 3, 3, 2 });
	}

	static public STIntervallicStructure get_minor_seventh_IS() {
		return new STIntervallicStructure(new int[] { 3, 4, 3, 2 });
	}

	static public STIntervallicStructure get_major_seventh_IS() {
		return new STIntervallicStructure(new int[] { 4, 3, 4, 1 });
	}

	static public STIntervallicStructure get_half_dim_seventh_IS() {
		return new STIntervallicStructure(new int[] { 3, 3, 4, 2 });
	}

	public static void partition(int n, int max, String temp, ArrayList<ArrayList<Integer>> master,
			ArrayList<Integer> holder) {
		if (n == 0) {
			ArrayList<Integer> temp1 = new ArrayList<Integer>();
			for (int i = 0; i < holder.size(); i++) {
				temp1.add(holder.get(i));
			}
			master.add(temp1);
			// System.out.println(temp);
		}

		for (int i = Math.min(max, n); i >= 1; i--) {
			holder.add(i);
			partition(n - i, i, temp + " " + i, master, holder);
			holder.remove(holder.size() - 1);
		}
	}

	static public ArrayList<STIntervallicStructure> enum_SI_up_to_permutation(int N) {

		ArrayList<ArrayList<Integer>> part_list = new ArrayList<ArrayList<Integer>>();

		partition(N, N, "", part_list, new ArrayList<Integer>());

		ArrayList<STIntervallicStructure> is_list = new ArrayList<STIntervallicStructure>();
		for (int n = 1; n <= N; n++) {
			for (ArrayList<Integer> part : part_list) {
				if (part.size() == n) {
					STIntervallicStructure is = new STIntervallicStructure(part);
					is.flip();
					is_list.add(is);
				}
			}
		}
		return is_list;
	}

	public boolean is_contained(Collection<STIntervallicStructure> is_collection) {
		for (STIntervallicStructure is : is_collection) {
			if (equals(is))
				return true;
		}
		return false;
	}

	public HashSet<STIntervallicStructure> get_is_permutations() {
		HashSet<STIntervallicStructure> is_set = new HashSet<STIntervallicStructure>();
		HashSet<ArrayList<Integer>> permutations = new HashSet<ArrayList<Integer>>();
		perm2(this._list, this.size(), permutations);
		for (ArrayList<Integer> list : permutations) {
			STIntervallicStructure is = new STIntervallicStructure(list);
			if (!is.is_contained(is_set))
				is_set.add(is);
		}
		// System.out.println("is_permutations : "+is_set);

		return is_set;
	}

	public static void perm2(ArrayList<Integer> a, int n, HashSet<ArrayList<Integer>> permutations) {
		if (n == 1) {
			permutations.add(new ArrayList<Integer>(a));
			// System.out.println("add : "+a);
			// System.out.println(Table.toString(a));
			return;
		}
		for (int i = 0; i < n; i++) {
			swap(a, i, n - 1);
			perm2(a, n - 1, permutations);
			swap(a, i, n - 1);
		}
	}

	// swap the characters at indices i and j
	private static void swap(ArrayList<Integer> a, int i, int j) {
		int c;
		c = a.get(i);
		a.set(i, a.get(j));
		a.set(j, c);
		// c = a[i]; a[i] = a[j]; a[j] = c;
	}

	static public ArrayList<ArrayList<STIntervallicStructure>> enum_Aff_SI(int N, int m) {

		ArrayList<ArrayList<STIntervallicStructure>> aff_list = new ArrayList<ArrayList<STIntervallicStructure>>();
		ArrayList<STIntervallicStructure> T_classes = new ArrayList<STIntervallicStructure>(enum_SI_up_to_flip(N));

		for (int i = 0; i < T_classes.size(); i++) {
			if (T_classes.get(i) != null) {

				ArrayList<STIntervallicStructure> aff_class = new ArrayList<STIntervallicStructure>();
				PitchClassSet prime = T_classes.get(i).get_prime_order_PCSet(0);
				STIntervallicStructure is = T_classes.get(i);
				STIntervallicStructure is_flip = get_reverse_is(is);
				PitchClassSet prime_m = T_classes.get(i).get_prime_order_PCSet(0).multiplication(m);
				STIntervallicStructure m_is = prime_m.get_intervallic_structure();
				STIntervallicStructure m_is_flip = get_reverse_is(m_is);

				aff_class.add(T_classes.get(i));
				if (!T_classes.get(i).equals(m_is))
					aff_class.add(m_is);

				T_classes.set(i, null);
				for (int j = 0; j < T_classes.size(); j++) {
					if (T_classes.get(j) != null) {
						if (T_classes.get(j).equals(is) || T_classes.get(j).equals(m_is)
								|| T_classes.get(j).equals(is_flip) || T_classes.get(j).equals(m_is_flip))
							T_classes.set(j, null);
					}
				}
				aff_list.add(aff_class);
			}
		}
		System.out.println("size : " + aff_list.size());
		// for (HashSet<STIntervallicStructure> list : aff_list) {
		// //System.out.println("taille : "+list.size());
		// for (STIntervallicStructure is : list){
		// System.out.println(is.get_prime_order_PCSet(0));
		// }
		// //System.out.println("list : "+list);
		// System.out.println("---");
		// }
		return aff_list;
	}

	public static STIntervallicStructure get_reverse_is(STIntervallicStructure is) {
		STIntervallicStructure new_is = new STIntervallicStructure(is.get_filpped_ring().get_list());
		return new_is;
	}

	public static GraphViz get_graphviz_parsimonious(ArrayList<STIntervallicStructure> is_list) {
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());

		return gv;
	}

	// Fonction retournant la liste des SI voisines d'une SI selon le critre :
	// 2 SI sont voisines si un mouvement minimal d'un accord de l'une permet de
	// passer un accord de l'autre

	public ArrayList<STIntervallicStructure> get_neighbors_Tis() {

		ArrayList<STIntervallicStructure> neighbors_Tis = new ArrayList<STIntervallicStructure>();
		PitchClassSet pcs1 = get_prime_order_PCSet(0);
		ArrayList<PitchClassSet> pcs_set = pcs1.semitone_close_chords();
		for (PitchClassSet pcs : pcs_set) {
			STIntervallicStructure is = pcs.get_intervallic_structure();
			if (!is.is_contained(neighbors_Tis))
				neighbors_Tis.add(pcs.get_intervallic_structure());
		}
		System.out.println("neighbors_Tis : " + neighbors_Tis);
		return neighbors_Tis;
	}

	public ArrayList<STIntervallicStructure> get_neighbors_TIis() {

		ArrayList<STIntervallicStructure> neighbors_TIis = new ArrayList<STIntervallicStructure>();
		PitchClassSet pcs1 = get_prime_order_PCSet(0);
		ArrayList<PitchClassSet> pcs_set = pcs1.semitone_close_chords();
		for (PitchClassSet pcs : pcs_set) {
			STIntervallicStructure is = pcs.get_intervallic_structure();
			if (!is.is_contained_up_to_flipSI(neighbors_TIis))
				neighbors_TIis.add(pcs.get_intervallic_structure());
		}
		System.out.println("neighbors_TIis : " + neighbors_TIis);
		return neighbors_TIis;
	}

	public boolean equals(STIntervallicStructure is) {
		// System.out.println("STIntervallicStructure.equals(STIntervallicStructure
		// is)");
		return super.equals((OrientedRing) is);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof STIntervallicStructure))
			return false;
		// System.out.println("STIntervallicStructure.equals(Object object)");
		return super.equals((OrientedRing) object);
	}

}
