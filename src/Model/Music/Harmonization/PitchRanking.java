package Model.Music.Harmonization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import Model.Music.ContinuePitchSpace;
import Utils.IntegerSet;

public class PitchRanking extends TreeMap<Float,IntegerSet>{

	private static final long serialVersionUID = 1L;

	public PitchRanking(){
		super();
	}
	
	public PitchRanking(PitchClassRanking pc_ranking, ContinuePitchSpace continue_space){
		super();
		for (Float score : pc_ranking.keySet()){
			IntegerSet pitch_set = continue_space.get_pitch_set(pc_ranking.get(score));
			if (!pitch_set.isEmpty()){
				put(score,pitch_set);	
			}

		}
	}
	
	public Map.Entry<Float, IntegerSet> get_best_pitch(){
		return lastEntry();
	}
	
	public void add(int pitch, float score){
		if (containsKey(score)){
			get(score).add(pitch);
		} else {
			IntegerSet set = new IntegerSet();
			set.add(pitch);
			put(score,set);
		}
	}
	
	public int get_total_element_count(){
		int n = 0;
		for (float key : keySet()){
			n = n + get(key).size();
		}
		return n;
	}
	
	public void only_keep_the_best(){
		ArrayList<Float> coef_list = new ArrayList<Float>(keySet());
		Collections.sort(coef_list);
		for (float coef : coef_list){
			if (coef != coef_list.get(coef_list.size()-1)){
				remove(coef);
			}
		}
	}

}
