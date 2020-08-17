package Model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

import Model.Music.Tonnetze.FoldedGraphTonnetz;
import Utils.IntegerSet;

public class PCCandidateStream extends TreeMap<Long,IntegerSet>{
	
	private static final long serialVersionUID = 1L;

	public PCCandidateStream(){
		super();
	}
	
	public long path_count(){
		long count = 1;
		for (long key : keySet()){
			count = count * get(key).size();
		}
		return count;
	}
	
	
	public HashSet<TreeMap<Long,Integer>> paths_in_tonnetz(ArrayList<FoldedGraphTonnetz> tonnetz_list){
		
		//FoldedTonnetz tonnetz = tonnetz_list.get(0);
		
		//boolean avoid_para_fifths = true;
		
		HashSet<TreeMap<Long,Integer>> path_set = new HashSet<TreeMap<Long,Integer>>();
		HashSet<TreeMap<Long,Integer>> tmp_path_set = new HashSet<TreeMap<Long,Integer>>();

		for (Integer first_candidate : this.firstEntry().getValue()){
			TreeMap<Long,Integer> path = new TreeMap<Long,Integer>();
			path.put(firstKey(), first_candidate);
			path_set.add(path);
		}
		
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		Collections.sort(key_list);
		for (int i=1;i<key_list.size();i++){
			long key = key_list.get(i);
			//System.out.println("key "+key);
			
			for (TreeMap<Long,Integer> path : path_set){
				int last_pitch = path.lastEntry().getValue();
				boolean bool = true;
				int tonnetz_index = 0;
				while(bool && tonnetz_index<tonnetz_list.size()){
					for (int candidate : get(key)){
						if(tonnetz_list.get(tonnetz_index).get_neighbors_pitch_class(last_pitch).contains(candidate) || last_pitch == candidate){
							TreeMap<Long,Integer> new_path = new TreeMap<Long,Integer>(path);
							new_path.put(key, candidate);
							//if (key == lastKey()) System.out.println("similarity : "+);
							tmp_path_set.add(new_path);
							bool = false;
						}
					}
					tonnetz_index++;
				}
			}
						
			path_set = new HashSet<TreeMap<Long,Integer>>(tmp_path_set);
			tmp_path_set.clear();
			
		}
		
		return path_set;
	}
	


	public HashSet<TreeMap<Long,Integer>> paths_in_tonnetz(FoldedGraphTonnetz tonnetz){
		ArrayList<FoldedGraphTonnetz> tonnetz_list = new ArrayList<FoldedGraphTonnetz>();
		tonnetz_list.add(tonnetz);
		return paths_in_tonnetz(tonnetz_list);
	}
}
