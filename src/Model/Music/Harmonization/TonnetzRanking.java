package Model.Music.Harmonization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import Model.Music.Tonnetze.FoldedGraphTonnetz;

public class TonnetzRanking extends TreeMap<Float,FoldedGraphTonnetz>{

	private static final long serialVersionUID = 1L;
	
	public TonnetzRanking(){
		super();
	}
	
	//	forbid_big_steps interdit les sauts trop grands
	
	//private static 
	private static boolean forbid_big_steps = true;
	private static int bigger_setp = 7;
	
	public float get_compliance(int p1, int p2){
		//if(p1 == p2) return 1;
		float compliance = 0;
		ArrayList<Float> score_list = new ArrayList<Float>(keySet());
		Collections.sort(score_list);
		for (int coef_index=score_list.size()-1;coef_index>=0;coef_index--){
			float coef = score_list.get(coef_index);
			//System.out.println("Htonnetz : "+get(score_list.get(coef_index)));
//			if (p1==p2 && coef == (float)1){
//				System.out.println("ttttooocc : "+p1+" - "+p2+" - "+get(score_list.get(coef_index)).are_neighbor(p1, p2));
//			}
			if(get(score_list.get(coef_index)).are_neighbor(p1, p2)){
				if (forbid_big_steps){
					if (Math.abs(p1-p2)<=bigger_setp){
						return coef;
					} 
				} else {
					return coef;
				}
			}
		}
		return compliance;
	}

}
