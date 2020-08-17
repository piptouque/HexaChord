package Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import Model.Music.STIntervallicStructure;

public class OrientedRing implements Collection<Integer>{
	
	protected ArrayList<Integer> _list;
	
	public OrientedRing(){
	}
	
	public OrientedRing(ArrayList<Integer> list){
		_list = new ArrayList<Integer>(list);
	}
	
	public OrientedRing(int[] t){
		_list = new ArrayList<Integer>();
		for (int i=0;i<t.length;i++) _list.add(t[i]);
	}
	
	public boolean equals(OrientedRing r2){
		//System.out.println("OrientedRing.equals(OrientedRing r)");
		if (this.size()!=r2.size()) return false;
		if (get_list().equals(r2.get_list())) return true;
		if (!(new HashSet<Integer>(this._list)).equals(new HashSet<Integer>(r2.get_list()))) return false;
		
		int first = _list.get(0);
		
		ArrayList<Integer> first_elem_pos_list = new ArrayList<Integer>();
		for (int i = 0;i<r2.size();i++){
			if (r2.get(i)==first) first_elem_pos_list.add(i);
		}

		for (int i = 0;i<first_elem_pos_list.size();i++){
			if (_list.equals(get_turned_ring(r2,first_elem_pos_list.get(i)).get_list())) return true;
		}
		
		return false;
		
	}
	
	// SEMBLE FAUSSE ! !
//	public boolean equals_up_to_flip(OrientedRing r2){
//		if (this.size()!=r2.size()) return false;
//		if (get_list().equals(r2.get_list())) return true;
//		if (!(new HashSet<Integer>(this._list)).equals(new HashSet<Integer>(r2.get_list()))) return false;
//		
//		int first = _list.get(0);
//		
//		ArrayList<Integer> first_elem_pos_list = new ArrayList<Integer>();
//		for (int i = 0;i<r2.size();i++){
//			if (r2.get(i)==first) first_elem_pos_list.add(i);
//		}
//
//		for (int i = 0;i<first_elem_pos_list.size();i++){
//			if (_list.equals(get_turned_ring(r2,first_elem_pos_list.get(i)).get_list())) return true;
//			if (_list.equals(get_turned_ring(r2,first_elem_pos_list.get(i)).get_filpped_ring().get_list())) return true;
//		}
//		
//		return false;
//		
//	}

	public int size(){
		return _list.size();
	}
	
	private int get(int i){
		return _list.get(i);
	}
	
	public int next(int i){
		return _list.get((i+1)%(size()));
	}
	
	public int prev(int i){
		return _list.get((i-1+size())%size());
	}
	
	public ArrayList<Integer> get_list() {
		return _list;
	}
	
	public int[] get_list_table() {
		int[] list_table = new int[_list.size()];
		for (int i = 0;i<_list.size();i++){
			list_table[i]=_list.get(i);
		}
		return list_table;
	}

	public Boolean is_contained(ArrayList<OrientedRing> ring_list){
		
		for (OrientedRing r : ring_list){
			if (this.equals(r)) return true;
		}
		return false;
	}

	public Boolean is_containedSI(ArrayList<STIntervallicStructure> ring_list){
		
		for (OrientedRing r : ring_list){
			if (this.equals(r)) return true;
		}
		return false;
	}
	
	public Boolean is_contained_up_to_flipSI(ArrayList<STIntervallicStructure> ring_list){

		for (OrientedRing r : ring_list){
			if (this.equals(r) || this.equals(r.get_filpped_ring())) return true;
		}
		return false;

	}

	// Turn circularly the ring. (It does not flip it !)
	protected static OrientedRing get_turned_ring(OrientedRing r,int n){
		ArrayList<Integer> new_list = new ArrayList<Integer>();
		for (int i=0;i<r.get_list().size();i++){
			new_list.add(r.get((i+n)%r.size()));
		}
		return new OrientedRing(new_list);
	}
	
	private void rotate(int n){
		ArrayList<Integer> new_list = new ArrayList<Integer>();
		for (int i=0;i<_list.size();i++){
			new_list.add(get((i+n)%size()));
		}
		_list = new_list;		
	}
	
	public OrientedRing get_filpped_ring(){
		ArrayList<Integer> new_list = new ArrayList<Integer>();
		for (int i=_list.size()-1;i>=0;i--){
			new_list.add(_list.get(i));
		}
		return new OrientedRing(new_list);
	}
	
	public void flip(){
		ArrayList<Integer> new_list = new ArrayList<Integer>();
		for (int i=_list.size()-1;i>=0;i--){
			new_list.add(_list.get(i));
		}
		_list = new_list;		
	}
	
	public void normalization_rotate(boolean oriented){
		if (size() == 0) return;
		if (size() == 1) return;

		ArrayList<ArrayList<Integer>> all_rotations = new ArrayList<ArrayList<Integer>>();
//		int[][] all_permutations;
				
		if (!oriented) {
			all_rotations.add(get_list());
			all_rotations.add(get_filpped_ring().get_list());
			for (int i=1;i<_list.size();i++){
				all_rotations.add(get_turned_ring(this, i).get_list());
				all_rotations.add(get_turned_ring(this, i).get_filpped_ring().get_list());
			}
			
			
//			all_permutations = new int[_list.size()*2][];
//			all_permutations[0]=get_list_table();
//			all_permutations[1]=get_filpped_ring().get_list_table();
//			for (int i=1;i<_list.size();i++){
//				all_permutations[2*i]=get_turned_ring(this,i).get_list_table();
//				all_permutations[2*i+1]=get_turned_ring(this,i).get_filpped_ring().get_list_table();
//			}
		} else {
			all_rotations.add(get_list());
			for (int i=1;i<_list.size();i++){
				all_rotations.add(get_turned_ring(this, i).get_list());
			}
//			all_permutations = new int[_list.size()][];
//			all_permutations[0]=get_list_table();
//			for (int i=1;i<_list.size();i++){
//				all_permutations[i]=get_turned_ring(this,i).get_list_table();
//			}
		}
		
		ArrayList<ArrayList<Integer>> all_rotations_tmp = new ArrayList<ArrayList<Integer>>();
		int interval_max = Collections.max(this);
		for (int i=0;i<all_rotations.size();i++){
			if (all_rotations.get(i).get(size()-1)==interval_max){
				all_rotations_tmp.add(all_rotations.get(i));
			}
		}
		all_rotations = new ArrayList<ArrayList<Integer>>(all_rotations_tmp);
		
		int min;
		
		int tmp_index = 0;
		ArrayList<ArrayList<Integer>> tmp_list = new ArrayList<ArrayList<Integer>>(all_rotations);
		ArrayList<ArrayList<Integer>> tmp_list_2 = new ArrayList<ArrayList<Integer>>();
		
		while(tmp_list.size()>1 && tmp_index<_list.size()){
			 min = tmp_list.get(0).get(tmp_index);
			for (ArrayList<Integer> permutation : tmp_list) if (permutation.get(tmp_index) < min) min = permutation.get(tmp_index);
			for (ArrayList<Integer> permutation : tmp_list) {
				if (permutation.get(tmp_index) == min) tmp_list_2.add(permutation);
			}
			tmp_list = new ArrayList<ArrayList<Integer>>(tmp_list_2);
			tmp_list_2.clear();
			tmp_index++;
		}
		
		_list = new ArrayList<Integer>(tmp_list.get(0));
		
	}
		
		
		
		
		
//		int min = _list.get(0);
//		for (int i=1;i < _list.size();i++){
//			if (_list.get(i)<min) min = _list.get(i);
//		}
//		ArrayList<Integer> min_index_set = new ArrayList<Integer>();
//		for (int i=0;i < _list.size();i++){
//			if (_list.get(i) == min) min_index_set.add(i);
//		}
//		if (min_index_set.size()==1){
//			rotate(min_index_set.get(0));
//			return;
//		}
//		
//
//	}
//	
//	public void normalization_rotate_up_to_flip(){
//		
//	}
	// affiche sous la forme la plus "croissante" AVEC flip 
//	public String toString_up_to_flip(){
//		if (size() == 0) return "[]";
//		if (size() == 1) return this.toString();
//		int index_min = 0;
//		for (int i=1;i < _list.size();i++){
//			if (_list.get(i)<_list.get(index_min)) index_min = i;
//		}
//		String str;
//		if(_list.get((index_min+1)%size()) <= (_list.get((index_min-1+size())%size()))){
//			str = get_turned_ring(this,index_min).toString();
//		} else {
//			str = get_turned_ring(get_turned_ring(this,index_min).get_filpped_ring(), size()-1).toString();
//		}
//		return str;
//	}


	// affiche sous la forme la plus "croissante" SANS flip
//	public String toString_without_flip(){
//		if (size() == 0) return "[]";
//		if (size() == 1) return this.toString();
//		int min = _list.get(0);
//		for (int i=1;i < _list.size();i++){
//			if (_list.get(i)<min) min = _list.get(i);
//		}
//		HashSet<Integer> min_index_set = new HashSet<Integer>();
//		for (int i=0;i < _list.size();i++){
//			if (_list.get(i) == min) min_index_set.add(i);
//		}
//		
//		
//		
//		int index_min = 0;
//		for (int i=1;i < _list.size();i++){
//			if (_list.get(i)<_list.get(index_min)) index_min = i;
//		}
//		
//		String str;
//		if(_list.get((index_min+1)%size()) <= (_list.get((index_min-1+size())%size()))){
//			str = get_turned_ring(this,index_min).toString();
//		} else {
//			str = get_turned_ring(get_turned_ring(this,index_min).get_filpped_ring(), size()-1).toString();
//		}
//		return str;
//	}

	// [3,4,5]
	public String toString(boolean oriented){
		normalization_rotate(oriented);
		return _list.toString().replaceAll(" ", "");
	}

	public String toString(){
		return toString(true);
	}

	// 3,4,5
	public String elements_to_string(boolean oriented){
		String string = toString(oriented);
		return string.substring(1, string.length()-1);
	}

	public String elements_to_string_without_space(boolean oriented){
		String string = toString(oriented);
		return string.substring(1, string.length()-1).replaceAll(" ", "");
	}

	public int sum(){
		int sum = 0;
		for (int i : _list){
			sum = sum+i;
		}
		return sum;
	}

	@Override
	public boolean add(Integer arg0) {
		System.err.println("no add to ring");
		System.exit(0);
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> arg0) {
		System.err.println("no add to ring");
		System.exit(0);
		return false;
	}

	@Override
	public void clear() {
		System.err.println("no clear ring");
		System.exit(0);
	}

	@Override
	public boolean contains(Object arg0) {
		return _list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return _list.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return _list.isEmpty();
	}

	@Override
	public Iterator<Integer> iterator() {
		return _list.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		System.err.println("no remove in ring");
		System.exit(0);
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		System.err.println("no remove all in ring");
		System.exit(0);
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		System.err.println("no retainsAll in ring");
		System.exit(0);
		return false;
	}

	@Override
	public Object[] toArray() {
		return _list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return _list.toArray(arg0);
	}

}
