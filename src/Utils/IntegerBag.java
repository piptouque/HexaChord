package Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class IntegerBag extends ArrayList<Integer>{
	
	private static final long serialVersionUID = 1L;
	
	public IntegerBag(){
		super();
	}

	public IntegerBag(Collection<Integer> c){
		super(c);
		sort();
	}
	
	public IntegerBag(OrientedRing r){
		super(r._list);
		sort();
	}
	
	public IntegerBag(int... integers){
		super();
		for (int i : integers){
			add(i);
		}
	}

	public void add(int i){
		super.add(i);
		sort();
	}

	private void sort() {
		Collections.sort(this);
	}

	public void take_out(int a){
		boolean bool = false;
		int i=0;
		while(!bool){
			if(get(i)==a){
				bool = true;
				remove(i);
			} else {
				if (i==size()-1){
					bool = true;
				} else {
					i++;
				}
			}
		
		}
	}
	
	public boolean includes(IntegerBag included_bag){
		
		IntegerBag tmp_bag = new IntegerBag(this);
		for (int i : included_bag){
			if(tmp_bag.contains(i)){
				tmp_bag.take_out(i);
			} else {
				return false;
			}
		}
		return true;
	}
	
	public IntegerSet setify(){
		IntegerSet set = new IntegerSet();
		for (int i : this) set.add(i);
		return set;
	}
}
