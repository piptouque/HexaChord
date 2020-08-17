package Model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class DynamicContinuePitchSpace extends TreeMap<Long,ContinuePitchSpace> {

	private static final long serialVersionUID = 1L;

	public DynamicContinuePitchSpace(){
		super();
	}
	
	public void set_passage(PitchSetStream stream, int voice){
		
		for (long key : stream.keySet()){
			if(stream.get(key).get_duration()>5){
				if (stream.get(key).size()>=voice+1){
					ArrayList<Integer> list = new ArrayList<Integer>(stream.get(key));
					Collections.sort(list);
					//put(key,new int[]{list.get(list.size()-voice)+1,list.get(list.size()-voice+1)-1});
					put(key,new ContinuePitchSpace(list.get(list.size()-voice)+1,list.get(list.size()-voice+1)-1));
				}
			}
		}
		
	}
}
