package Model.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.management.openmbean.KeyAlreadyExistsException;


public class HAChordCandidateGraph extends TreeMap<Long,List<HAChordCandidate>>{
	
	private static final long serialVersionUID = 1L;

	public HAChordCandidateGraph(PitchSetStream stream){
		super();
		for (long key : stream.keySet()){
			if (!stream.get(key).isEmpty()){
				List<HAChordCandidate> HA_candidates = stream.get_HA_candidates(key);
				put(key,HA_candidates);
			}
		}
		System.out.println("candidate stream : "+this);
	}

	public HAChordCandidateGraphPath get_HA_best_path(){
		HAChordCandidateGraphPath best_path = new HAChordCandidateGraphPath();
		List<HAChordCandidateGraphPath> path_list = new ArrayList<HAChordCandidateGraphPath>();
		List<HAChordCandidateGraphPath> tmp_path_list;

		// Pour chaque slice du graph
		for (long key : keySet()){
			System.out.println("research : "+(100*(float)key/(float)lastKey())+"%");
			tmp_path_list = new ArrayList<HAChordCandidateGraphPath>();
			if (key==firstKey()){
				for (int i=0;i<firstEntry().getValue().size();i++){
					HAChordCandidateGraphPath new_path = new HAChordCandidateGraphPath(); 
					new_path.put(key, firstEntry().getValue().get(i));
					path_list.add(new_path);
				}
				//System.out.println("path_list 1 : "+path_list);
			} else {
				// Pour chaque candidat de la slice
				for (int i=0;i<get(key).size();i++){
					int min_cost = 100;
					HAChordCandidateGraphPath best_cand_path = new HAChordCandidateGraphPath();
					// Pour chaque chemin arrivant à la slice précédente
					
					for (HAChordCandidateGraphPath path : path_list){
						//System.out.println("path : "+path);
						HAChordCandidate tail_candidate = path.lastEntry().getValue();
						int trans_cost = HAChordCandidate.transition_cost(tail_candidate, get(key).get(i));
						if (trans_cost<min_cost){
							min_cost = trans_cost;
							best_cand_path = new HAChordCandidateGraphPath(path);
							best_cand_path.put(key, get(key).get(i));
						}
					}
					tmp_path_list.add(best_cand_path);
				}
				path_list = tmp_path_list;
				//System.out.println("path_list 2 : "+path_list);
			}
//			if (key==16320){
//				System.out.println("WOA");
//				for (int i=0;i<path_list.size();i++){
//					System.out.println("path "+i+" : "+path_list.get(i));
//				}
//				
//			}
			
		}
		
		int min_cost = path_list.get(0).get_path_cost();
		best_path = path_list.get(0);
		
		for (HAChordCandidateGraphPath path : path_list){
			int cost = path.get_path_cost();
			//if (cost<40)
			//System.out.println("cost : "+cost+" path : "+path.get_defragmented_path());
			if (cost<min_cost){
				best_path = path;
				min_cost = cost;
			}
		}
		System.out.println("best path : "+best_path.get_defragmented_path());
		return best_path;
	}
}
