package Utils;

import java.util.ArrayList;
import java.util.HashSet;

public class TableSet extends HashSet<int[]>{

	private static final long serialVersionUID = 1L;

	public TableSet(){
		super();
	}
	
	public boolean add(int[] tab){
		for (int[] i : this){
			if (tab_equals(tab,i)) return false;
		}
		return super.add(tab);
	}
	
	public boolean addAll(ArrayList<int[]> coll){
		for (int[] i : coll) add(i);
		return true;
	}
	
	public boolean contains(int[] i){
		for (int[] j : this) if (tab_equals(i,j)) return true;
		return false;
	}
	
	public static boolean tab_equals(int[] a, int[] b){
		if (a.length != b.length) return false;
		for (int i = 0;i<a.length;i++){
			if (a[i] != b[i]) return false;
		}
		return true;
	}
	
	public ArrayList<int[]> get_intersection(ArrayList<int[]> list){
		ArrayList<int[]> intersection = new ArrayList<int[]>();
		for (int[] a : list) if (contains(a)) intersection.add(a);
		return intersection;
	}
	
	public String toString(){
		return Table.toString(this);
	}
	
}
