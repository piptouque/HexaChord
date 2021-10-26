package Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class IntegerSet extends HashSet<Integer> {

	private static final long serialVersionUID = 1L;

	public IntegerSet() {
		super();
	}

	public IntegerSet(HashSet<Integer> set) {
		super(set);
	}

	public IntegerSet(IntegerSet set) {
		super(set);
	}

	public IntegerSet(int a, int b, int c) {
		super();
		add(a);
		add(b);
		add(c);
	}

	public IntegerSet(int[] table) {
		super();
		for (int i : table)
			add(i);
	}

	public IntegerSet(ArrayList<Integer> pitch_list) {
		super(pitch_list);
	}

	public IntegerSet intersection(IntegerSet other_set) {
		IntegerSet intersection = new IntegerSet();
		for (int i : this) {
			if (other_set.contains(i)) {
				intersection.add(i);
			}
		}
		return intersection;
	}

	public int get(int n) {
		assert (size() > n) : "set " + this + " doesn't contain " + n + " elements";
		ArrayList<Integer> list = new ArrayList<Integer>(this);
		Collections.sort(list);
		return list.get(n);
	}

	// orders arbitrarily the elements
	public ArrayList<Integer> to_array_list() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : this)
			list.add(i);
		return list;
	}

	// sorts the elements
	public ArrayList<Integer> to_sorted_list() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.addAll(this);
		Collections.sort(list);
		return list;
	}

	// retourne toutes les paires possibles. TODO : fonction rcursive pour rcuprer
	// les subsets de taille n
	public HashSet<IntegerSet> get_2_subsets() {
		HashSet<IntegerSet> subsets = new HashSet<IntegerSet>();
		for (int i : this) {
			for (int j : this) {
				if (i != j) {
					subsets.add(new IntegerSet(new int[] { i, j }));
				}
			}
		}
		return subsets;
	}

}
