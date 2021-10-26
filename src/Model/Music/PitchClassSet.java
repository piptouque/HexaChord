package Model.Music;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Utils.Binomial;
import Utils.IntegerBag;
import Utils.Gcd;
import Utils.IntegerSet;
import Utils.MidiPlayer;
import Utils.Table;

public class PitchClassSet extends IntegerSet {

	private static final long serialVersionUID = 6140332455097484769L;
	private int _N;
	private int _M;
	private int _orbit;
	private List<Integer> _pitch_list;

	public PitchClassSet(int N) {
		super();
		_N = N;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
	}

	public PitchClassSet() {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
	}

	public PitchClassSet(ArrayList<Integer> list) {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
		for (Integer n : list)
			add(n % _N);
	}

	public PitchClassSet(HashSet<Integer> list) {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
		for (Integer n : list)
			add(n % _N);
	}

	public PitchClassSet(int n1, int n2) {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
		add(n1);
		add(n2);
	}

	public PitchClassSet(int n1, int n2, int n3) {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
		add(n1);
		add(n2);
		add(n3);
	}

	public PitchClassSet(int n1, int n2, int n3, int n4) {
		super();
		_N = 12;
		// _M = (_N/2)-(_N%2);
		_M = _N / 2;
		add(n1);
		add(n2);
		add(n3);
		add(n4);
	}

	public PitchClassSet(int pcs[]) {
		super();
		_N = 12;
		for (int pc : pcs)
			add(pc);
	}

	public PitchClassSet(String string) {
		super();
		assert string.charAt(0) == '(' && string.charAt(string.length() - 1) == ')'
				: "wrong pcs string format : " + string;
		String string2 = string.substring(1, string.length() - 1);
		// System.out.println("string2 :"+string2);
		Scanner scanner = new Scanner(string2);
		scanner.useDelimiter(",");
		HashSet<Integer> set = new HashSet<Integer>();
		while (scanner.hasNext()) {
			// scanner.next()
			int pc = scanner.nextInt();
			// System.out.println("pc : "+scanner.next());
			set.add(pc);
		}

		_N = 12;
		_M = _N / 2;
		for (Integer n : set)
			add(n % _N);

	}

	public void addElement(int n) {
		add(n);
	}

	public boolean member(Integer n) {
		return contains(n);
	}

	public boolean equals(PitchClassSet pcs_2) {
		// System.out.println("method PitchClassSet.equals");
		if (size() == pcs_2.size() && containsAll(pcs_2))
			return true;
		return false;
	}

	public HashSet<Integer> get_intersection(HashSet<Integer> set) {
		HashSet<Integer> intersection = new HashSet<Integer>();
		for (Integer i : set) {
			if (contains(i))
				intersection.add(i);
		}
		return intersection;
	}

	// public void playChord(MidiPlayer m) {
	// for(Integer n : this) m.playNote(0, n+60, 64);
	// }

	// public Z12PlanarUnfoldedTonnetz getZ12UnfoldedTonnetz() {
	// return (new STIntervallicStructure(this,12)).getZ12UnfoldedTonnetz();
	// }

	public Z12PlanarUnfoldedTonnetz get_corresponding_Tonnetz(ArrayList<Z12PlanarUnfoldedTonnetz> tonnetzs) {

		assert size() == 3 : "STChord.get_corresponding_Tonnetz : not yet implemented for more than 3-chords";
		for (Z12PlanarUnfoldedTonnetz t : tonnetzs) {
			if (t.get_IntervalVectorU().equals(get_MI_IntervalVector()))
				return t;
		}
		return null;
	}

	public int interval_count() {
		return ((this.size() - 1) * (this.size())) / 2;
	}

	public String toString() {
		String s = "(";
		ArrayList<Integer> list = to_array_list();
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				s = s.concat(list.get(i) + ",");
			} else {
				s = s.concat(list.get(i).toString());
			}
		}
		// for(Integer n : this) s = s.concat(n+" ");
		return s + ")";
	}

	public PitchClassSet transposition(int t) {
		PitchClassSet c = new PitchClassSet();
		for (Integer n : this)
			c.add((n + t) % _N);
		return c;
	}

	public PitchClassSet inversion(int i) {
		PitchClassSet c = new PitchClassSet();
		for (Integer n : this)
			c.add((i - n + _N) % _N);
		return c;
	}

	public PitchClassSet multiplication(int m) {
		PitchClassSet c = new PitchClassSet();
		for (int pc : this)
			c.add((pc * m) % _N);
		return c;
	}

	public int get_smallest_pitch() {
		ArrayList<Integer> list = new ArrayList<Integer>(this);
		return list.get(0);
	}

	public int get_orbit(Z12PlanarUnfoldedTonnetz t) {
		_orbit = (get_smallest_pitch()) % (Gcd.gcd(t.get_generators()));
		return _orbit;
	}

	public int get_interval_from_prime_chord() {
		if (this.isEmpty())
			return 0;
		PitchClassSet prime_chord = get_intervallic_structure().get_prime_PCSet(true);
		// PitchClassSet tmp_chord = new PitchClassSet(this);
		// System.out.println("chord : "+this);
		// System.out.println("prime : "+prime_chord);

		for (int i = 0; i < _N; i++) {
			if (this.equals(prime_chord.transposition(i)))
				return i;
		}
		System.err.println("error : did not find the prime chord of " + this);
		return 0;
	}

	// return the position of pc in the context of the pcs intervallic structure
	public int get_pc_position_in_context_of_pcs(int pc) {
		int dist_interval = get_interval_from_prime_chord();
		return (pc - dist_interval + 12) % 12;
	}

	// returns the pitch class at pc_position in the context of the pcs intervallic
	// structure
	public int get_pc_in_context_of_pcs(int pc_position) {
		int dist_interval = get_interval_from_prime_chord();
		return (pc_position + dist_interval) % 12;
	}

	public STIntervallicStructure get_intervallic_structure() {
		return new STIntervallicStructure(this, 12);
	}

	// retourne l'intervalle entre la root note de 2 pitch class set. Chaque root
	// note est dtermine l'aide de l'accord primaire de la structure intervallique
	// de l'accord.
	public static int get_PCS_interval(PitchClassSet pcs1, PitchClassSet pcs2) {
		if (pcs1.isEmpty() || pcs2.isEmpty())
			return 0;
		int N = pcs1._N;
		// System.out.println("pcs1 : "+pcs1);
		// System.out.println("pcs2 : "+pcs2);
		// STIntervallicStructure _is1 = pcs1.get_intervallic_structure();
		// STIntervallicStructure _is2 = pcs2.get_intervallic_structure();
		// System.out.println("prime 1 : "+_is1.get_prime_PCSet(true));
		// System.out.println("prime 2 : "+_is2.get_prime_PCSet(true));
		int interval = pcs2.get_interval_from_prime_chord() - pcs1.get_interval_from_prime_chord();
		if (interval < 0) {
			interval = N + interval;
		}
		return interval;
	}

	public IntervallicVector get_MI_IntervalVector() { // Allen Forte. Ex : (O,4,7) -> [001110] (0,4,7,10) -> [012111]]

		ArrayList<Integer> list = new ArrayList<Integer>(this);
		Collections.sort(list);
		int iv[] = new int[_M];
		for (int i : iv)
			iv[i] = 0;
		for (int i = 0; i < this.size() - 1; i++) {
			for (int j = i + 1; j < this.size(); j++) {
				// iv[-Math.abs((list.get(j)-list.get(i))-M)+M-1]++;
				iv[Interval.MI((list.get(j) - list.get(i)), _N) - 1]++;
			}
		}
		return new IntervallicVector(iv);
	}

	public IntegerSet get_MI_IntervalContent_set() {
		return get_MI_IntervalVector().get_interval_content_set();
	}

	public IntegerBag get_MI_IntervalContent_multiset() {
		IntegerBag multi_set = new IntegerBag();
		IntervallicVector vector = get_MI_IntervalVector();
		for (int i = 0; i < vector.size(); i++) {
			if (vector.get(i) != 0) {
				for (int j = 1; j <= vector.get(i); j++) {
					multi_set.add(i + 1);
				}
			}
		}
		return multi_set;
	}

	public IntegerBag get_StepContent_multiset(Scale scale) {
		IntegerBag multi_set = new IntegerBag();
		int[] MI_step_vector = new int[scale.size() / 2];
		for (int i = 0; i < MI_step_vector.length; i++)
			MI_step_vector[i] = 0;
		for (int i = 0; i < size() - 1; i++) {
			for (int j = i + 1; j < size(); j++) {
				MI_step_vector[scale.get_MIstep_in_scale(get(i), get(j)) - 1]++;
			}
		}
		IntervallicVector iv = new IntervallicVector(MI_step_vector);
		for (int i = 0; i < iv.size(); i++) {
			if (iv.get(i) != 0) {
				for (int j = 1; j <= iv.get(i); j++) {
					multi_set.add(i + 1);
				}
			}
		}

		return multi_set;
	}

	public static PitchClassSet random_STChord(int size, int N) {
		PitchClassSet chord = new PitchClassSet();
		while (chord.size() != size) {
			chord.add((int) (1000 * Math.random()) % N);
		}
		return chord;
	}

	public static PitchClassSet random_STChord(int min_size, int max_size, int N) {
		int interval = max_size - min_size + 1;
		PitchClassSet chord = new PitchClassSet();
		int size = ((int) (1000 * Math.random()) % interval) + min_size;
		while (chord.size() != size) {
			chord.add((int) (1000 * Math.random()) % N);
		}
		return chord;
	}

	// instanciate a pitch set with each pitch included in 60 -> 71
	public PitchSet to_default_pitch_set() {
		PitchSet pitch_set = new PitchSet();
		for (int i : this) {
			pitch_set.add(i + 60);
		}
		return pitch_set;
	}

	public PitchSet to_default_pitch_set_vl(PitchSet previous_ps) {
		if (previous_ps.isEmpty())
			return to_default_pitch_set();
		PitchSet pitch_set = new PitchSet();

		for (int pc : this) {
			int chosen_pitch = 0;
			int smaller_distance = 6;
			for (int prev_p : previous_ps) {
				// int interval = Interval.smaller_distance_interval(prev_p%12-pc, 12);
				int distance = Interval.MI(prev_p % 12, pc, 12);
				if (distance < smaller_distance) {
					smaller_distance = distance;
					chosen_pitch = Note.get_closer_pitch_having_pitch_class(prev_p, pc);
				}

				//
				//
				// if (Math.abs(interval) < smaller_distance){
				// smaller_distance = Math.abs(interval);
				// chosen_pitch = prev_p+interval;
				// }
			}
			pitch_set.add(chosen_pitch);
		}
		return pitch_set;
	}

	// renvoie le nombre de couple (a,b) dans pc_set1 x pc_set2 qui sont spar de
	// interval. Plus de dtails :
	// http://repmus.ircam.fr/_media/moreno/support_cours_mmim2011-2012c.pdf
	// public static int IFUNC(PitchClassSet pc_set1, PitchClassSet pc_set2,int
	// interval){
	// for (int pc1 : pc_set1){
	//
	// }
	// }

	// fonction recursive stockant dans set tous les sous ensembles de taille n de
	// list.
	// le paramtre index est utilis pour la rcursion

	public void recur(int index, int n, ArrayList<Integer> list, HashSet<PitchClassSet> set) {
		if (index >= n) {
			set.add(new PitchClassSet(list));
			return;
		}

		int start = 0;
		if (index > 0)
			start = to_sorted_list().indexOf(list.get(index - 1)) + 1;

		for (int i = start; i < size(); i++) {
			list.set(index, to_sorted_list().get(i));
			recur(index + 1, n, list, set);
		}
	}

	// renvoie une liste contenant tous les sous accords n sons contenus dans
	// l'accord

	public HashSet<PitchClassSet> get_n_sub_pc_set(int n) {
		HashSet<PitchClassSet> set = new HashSet<PitchClassSet>();
		if (n > size() || n == 0)
			return set;
		if (n == size()) {
			set.add(this);
			return set;
		}
		if (n == 1) {
			for (int pc : this) {
				PitchClassSet new_pc = new PitchClassSet(_N);
				new_pc.add(pc);
				set.add(new_pc);
			}
			return set;
		}

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < n; i++)
			list.add(0);
		recur(0, n, list, set);

		return set;
	}

	// renvoie l'ensemble des sous ensembles (comprenant l'ensemble lui-mme)

	public HashSet<PitchClassSet> get_all_sub_pc_set() {
		HashSet<PitchClassSet> subsets = new HashSet<PitchClassSet>();
		for (int i = 1; i <= size(); i++) {
			subsets.addAll(get_n_sub_pc_set(i));
		}
		return subsets;
	}

	// renvoie le vecteur IFUNC
	public static int[] IFUNC_vector(PitchClassSet pc_set1, PitchClassSet pc_set2, int N) {

		int[] vector = new int[N];
		for (int i = 0; i < vector.length; i++)
			vector[i] = 0;
		for (int pc1 : pc_set1) {
			for (int pc2 : pc_set2) {
				vector[(pc2 - pc1 + N) % N]++;
			}
		}

		return vector;
	}

	public PitchClassSet to_N_pc_set(Scale scale) {

		assert scale.containsAll(this) : "pc_set not contained in the scale";
		PitchClassSet N_pc_set = new PitchClassSet(scale.size());
		for (int p : this)
			N_pc_set.add(scale.indexOf(p));
		return N_pc_set;
	}

	public int[] to_table() {
		int[] pc_set = new int[size()];
		ArrayList<Integer> pc_list = to_array_list();
		for (int i = 0; i < pc_list.size(); i++) {
			pc_set[i] = pc_list.get(i);
		}
		return pc_set;
	}

	// Fonction retournant l'ensemble des accords "proches" d'un accord ie. on ne
	// change qu'une note et d'un demi-ton.

	public ArrayList<PitchClassSet> semitone_close_chords() {
		ArrayList<PitchClassSet> list = new ArrayList<PitchClassSet>();
		ArrayList<Integer> chord_l = new ArrayList<Integer>(this);
		ArrayList<Integer> c;
		for (int i = 0; i < chord_l.size(); i++) {
			c = new ArrayList<Integer>(this);
			c.set(i, (c.get(i) + 1) % 12);
			// System.out.println("1 : "+c);
			PitchClassSet stChord = new PitchClassSet(c);
			if (stChord.size() == this.size())
				list.add(stChord);
			c = new ArrayList<Integer>(this);
			c.set(i, (c.get(i) - 1 + 12) % 12);
			// System.out.println("2 : "+c);
			stChord = new PitchClassSet(c);
			if (stChord.size() == this.size())
				list.add(stChord);

		}
		return list;
	}

	public HashSet<PitchClassSet> semitone_close_chords_set() {
		HashSet<PitchClassSet> set = new HashSet<PitchClassSet>();
		ArrayList<Integer> chord_l = new ArrayList<Integer>(this);
		ArrayList<Integer> c;
		for (int i = 0; i < chord_l.size(); i++) {
			c = new ArrayList<Integer>(this);
			c.set(i, (c.get(i) + 1) % 12);
			System.out.println("1 : " + c);
			PitchClassSet stChord = new PitchClassSet(c);
			if (stChord.size() == this.size())
				set.add(stChord);
			c = new ArrayList<Integer>(this);
			c.set(i, (c.get(i) - 1 + 12) % 12);
			System.out.println("2 : " + c);
			stChord = new PitchClassSet(c);
			if (stChord.size() == this.size())
				set.add(stChord);

		}
		return set;
	}

	// retourne le nombre de PCSet existant de taille size
	public static int get_size_pcset_count(int N, int size) {
		int p = 1;
		for (int i = 0; i <= size - 1; i++) {
			p = p * (N - i);
		}
		int q = 1;
		for (int i = 2; i <= size; i++)
			q = q * i;
		return p / q;
	}

	public static HashSet<PitchClassSet> get_total_sized_pcs(int size, int N) {
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		for (ArrayList<Integer> list : Binomial.generate_all_n_parmis_p(size, N))
			pcs_set.add(new PitchClassSet(list));
		return pcs_set;
	}

	public static HashSet<PitchClassSet> get_total_under_sized_pcs(int size, int N) {
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		pcs_set.add(new PitchClassSet()); // comprend l'accord vide
		for (int i = 1; i <= size; i++)
			pcs_set.addAll(get_total_sized_pcs(i, N));
		return pcs_set;
	}

	public boolean is_contained_in_set(Collection<PitchClassSet> pcs_collection) {
		for (PitchClassSet pcs : pcs_collection) {
			if (equals(pcs))
				return true;
		}
		return false;
	}

	public static PitchClassSet get_next_pcs(PitchClassSet prev_pcs, int interval, STIntervallicStructure is) {
		int i = prev_pcs.get_interval_from_prime_chord() + interval;
		return is.get_prime_PCSet(true).transposition(i);
	}

	@Override
	public boolean equals(Object object) {

		if (object instanceof PitchClassSet) {
			return this.equals((PitchClassSet) object);
		}
		return false;
	}

	public boolean has_estimated_fundamental() {
		if (get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_diminished_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_augmented_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_dominant_seventh_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_minor_seventh_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_major_seventh_IS()))
			return true;
		return false;
	}

	public int get_estimated_fundamental() {
		assert has_estimated_fundamental() : "can not estimate the fundamental of pcset " + this;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_major_IS()))
			for (int pc : this)
				if (contains((pc + 7) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS()))
			for (int pc : this)
				if (contains((pc + 7) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_diminished_IS()))
			for (int pc : this)
				if (contains((pc + 6) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_augmented_IS()))
			for (int pc : this)
				if (contains((pc + 8) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_dominant_seventh_IS()))
			for (int pc : this)
				if (contains((pc + 7) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_minor_seventh_IS()))
			for (int pc : this)
				if (contains((pc + 10) % 12))
					return pc;
		if (get_intervallic_structure().equals(STIntervallicStructure.get_major_seventh_IS()))
			for (int pc : this)
				if (contains((pc + 11) % 12))
					return pc;
		System.err.println("error - should not be here - fundamental not found");
		return 0;
	}

	public Set<PitchClassSet> get_HA_compatible_chords() {
		return get_compatible_major_and_minor_chords();
	}

	public Set<PitchClassSet> get_compatible_major_and_minor_chords() {
		Set<PitchClassSet> compatible_triades = new HashSet<PitchClassSet>();
		PitchClassSet tested_chord;
		for (int i = 0; i < 12; i++) {
			// major chords
			tested_chord = new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this))
				compatible_triades.add(new PitchClassSet(tested_chord));
			// minor chords
			tested_chord = new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this))
				compatible_triades.add(new PitchClassSet(tested_chord));
			// M7 chords
			tested_chord = new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12, (i + 11) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 11) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12));
			// 7 chords
			tested_chord = new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12, (i + 10) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 10) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12));
			// m7 chords
			tested_chord = new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12, (i + 10) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 10) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12));
			// M9 chords
			tested_chord = new PitchClassSet(i, (i + 2) % 12, (i + 4) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 2) % 12)
					&& this.contains((i + 7) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12));
			// m9 chords
			tested_chord = new PitchClassSet(i, (i + 2) % 12, (i + 3) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 2) % 12)
					&& this.contains((i + 7) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12));
			// M11 chords
			tested_chord = new PitchClassSet(i, (i + 4) % 12, (i + 5) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 5) % 12)
					&& this.contains((i + 7) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12));
			// m11 chords
			tested_chord = new PitchClassSet(i, (i + 3) % 12, (i + 5) % 12, (i + 7) % 12);
			if (tested_chord.containsAll(this) && this.contains(i) && this.contains((i + 5) % 12)
					&& this.contains((i + 7) % 12))
				compatible_triades.add(new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12));
		}
		return compatible_triades;
	}

	public static List<PitchClassSet> get_major_and_minor_chords() {
		List<PitchClassSet> major_and_minor_chords = new ArrayList<PitchClassSet>();
		major_and_minor_chords.addAll(get_major_chords());
		major_and_minor_chords.addAll(get_minor_chords());
		return major_and_minor_chords;
	}

	public static List<PitchClassSet> get_major_chords() {
		List<PitchClassSet> major_chords = new ArrayList<PitchClassSet>();
		for (int i = 0; i < 12; i++)
			major_chords.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12));
		return major_chords;
	}

	public static List<PitchClassSet> get_minor_chords() {
		List<PitchClassSet> minor_chords = new ArrayList<PitchClassSet>();
		for (int i = 0; i < 12; i++)
			minor_chords.add(new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12));
		return minor_chords;
	}

	public static List<PitchClassSet> get_major_seventh_chords() {
		List<PitchClassSet> major_seventh_chords = new ArrayList<PitchClassSet>();
		for (int i = 0; i < 12; i++)
			major_seventh_chords.add(new PitchClassSet(i, (i + 4) % 12, (i + 7) % 12, (i + 11) % 12));
		return major_seventh_chords;
	}

	public static List<PitchClassSet> get_minor_seventh_chords() {
		List<PitchClassSet> minor_seventh_chords = new ArrayList<PitchClassSet>();
		for (int i = 0; i < 12; i++)
			minor_seventh_chords.add(new PitchClassSet(i, (i + 3) % 12, (i + 7) % 12, (i + 10) % 12));
		return minor_seventh_chords;
	}

	public PitchClassSet get_relative_chord() {
		assert (get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())
				|| get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS()))
				: "only major and minor chords have relative";

		if (get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())) {
			return new PitchClassSet((get_estimated_fundamental() + 9) % 12, get_estimated_fundamental(),
					(get_estimated_fundamental() + 4) % 12);
		}
		if (get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS())) {
			return new PitchClassSet((get_estimated_fundamental() + 3) % 12, (get_estimated_fundamental() + 7) % 12,
					(get_estimated_fundamental() + 10) % 12);
		}
		System.err.println("should not be here");
		return null;
	}

	// according to Lerdahl's tonal distance
	public int get_distance_in_fifth_circle(PitchClassSet other_pcs) {
		assert (this.get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())
				|| this.get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS())
						&& other_pcs.get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())
				|| other_pcs.get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS()))
				: "Lerdahl's tonal distance can only be calculated between maj/min chords";

		if (this.get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS())
				&& other_pcs.get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())) {
			return 1 + get_distance_in_fifth_circle(other_pcs.get_relative_chord());
		}
		if (this.get_intervallic_structure().equals(STIntervallicStructure.get_major_IS())
				&& other_pcs.get_intervallic_structure().equals(STIntervallicStructure.get_minor_IS())) {
			return 1 + get_distance_in_fifth_circle(other_pcs.get_relative_chord());
		}

		return Interval.distance_in_fith_circle(get_estimated_fundamental(), other_pcs.get_estimated_fundamental());

	}

	// Warning : no pitch spelling possible !
	public String get_chord_symbol() {
		STIntervallicStructure is = get_intervallic_structure();
		assert (is.is_major_IS() || is.is_minor_IS()) : "can only return chord symbol for major and minor chord";
		int fund = get_estimated_fundamental();
		if (is.is_major_IS())
			return Note.get_name(fund);
		if (is.is_minor_IS())
			return Note.get_name(fund) + "m";
		System.err.println("chord symbol not found : " + this);

		return null;
	}

	// returns the closest pitch to ref_pitch, which belongs (%12) to this
	// PitchClassSet - with priority to greater pitch
	public int get_closest_pitch_in_pcs(int ref_pitch) {
		assert !isEmpty() : "pitch class set is empty";
		int i = 0;
		while (i < 12) {
			if (contains((ref_pitch + i) % 12))
				return ref_pitch + i;
			if (contains((ref_pitch - i) % 12))
				return ref_pitch - i;
			i++;
		}
		System.err.println("did not find any closest pitch");
		return 0;
	}

	// returns the list of pitch (in the whole piano roll) that fit with the PCS
	public List<Integer> get_pcs_pitches() {
		if (_pitch_list == null) {
			_pitch_list = new ArrayList<Integer>();
			for (int p = 21; p <= 108; p++) {
				if (contains(p % _N))
					_pitch_list.add(p);
			}
		}
		return _pitch_list;
	}

	public int get_next_pitch_following_chordic_interval(int pitch, int chordic_interval) {
		assert get_pcs_pitches().contains(pitch) : "pitch not in pcs";
		return get_pcs_pitches().get(get_pcs_pitches().indexOf(pitch) + chordic_interval);
	}

}
