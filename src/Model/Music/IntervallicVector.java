package Model.Music;

import java.util.ArrayList;

import Utils.IntegerSet;

public class IntervallicVector extends ArrayList<Integer>{
	
	private static final long serialVersionUID = 1L;

	public IntervallicVector(int[] vect){
		for (int i=0;i<vect.length;i++){
			add(vect[i]);
		}
	}
	
	public int dot_product(IntervallicVector v2){
		int sum = 0;
		assert(size()==v2.size()) : "IntervallicVector: dot_product : different size vectors";
		for (int i = 0 ; i<size() ; i++){
			sum = sum + (get(i)*v2.get(i));
		}
		return sum;
	}
	
	public IntegerSet get_interval_content_set(){
		IntegerSet set = new IntegerSet();
		for (int i=0;i<size();i++){
			if (get(i)!=0){
				set.add(i+1);
			}
		}
		return set;		
	}
}
