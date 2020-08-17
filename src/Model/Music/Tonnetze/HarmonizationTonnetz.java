package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import Model.Music.PitchSetStream;
import Model.Music.Interval;
import Model.Music.PitchSet;
import Model.Music.PitchSetWithDuration;
import Model.Music.PitchClassSet;
import Model.Music.Harmonization.HarmonizationVoice;
import Model.Music.Harmonization.PitchClassRanking;
import Model.Music.Harmonization.PitchRanking;
import Model.Music.Harmonization.PitchWithCompliance;
import Model.Music.Harmonization.TonnetzRanking;

// p1 ------> p2

class PitchTransition {
	
	int _p1;
	int _p2;

	public PitchTransition(int p1, int p2){
		_p1 = p1;
		_p2 = p2;
	}
		
	public int get_interval(){
		return _p2-_p1;
	}
		
	public String toString(){
		String str = "("+_p1+"->"+_p2+")";
		return str;
	}
}

class NVoiceTransition extends ArrayList<PitchTransition>{
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<Integer> _list1;
	ArrayList<Integer> _list2;
		
	//public NVoiceTransition(PitchSet set1, PitchSet set2){
	public NVoiceTransition(ArrayList<Integer> pitch_list1, ArrayList<Integer> pitch_list2){
		super();
//		ArrayList<Integer> list1 = new ArrayList<Integer>(set1);
//		ArrayList<Integer> list2 = new ArrayList<Integer>(set2);
		Collections.sort(pitch_list1);
		Collections.sort(pitch_list2);

		if (pitch_list1.size()!=pitch_list2.size()){
			System.out.println("sets are not the same size : "+PitchSet.print_pitch_list(pitch_list1)+" "+PitchSet.print_pitch_list(pitch_list2));
			
			//System.out.println("sets are not the same size : "+pitch_list1+" "+pitch_list2+". Complete the shortest one with an extra lower pitch");
//			assert false;
//			if (list2.size()<list1.size()){
//				list2.add(list2.size()-1);
//				Collections.sort(list2);
//			} else {
//				list1.add(list1.size()-1);
//				Collections.sort(list1);				
//			}			
		} 
		assert pitch_list1.size()==pitch_list2.size() : "sets should now be the same size : "+pitch_list1+" "+pitch_list2;
		_list1 = pitch_list1;
		_list2 = pitch_list2;

		for (int i=0;i<pitch_list1.size();i++){
			add(new PitchTransition(pitch_list1.get(i),pitch_list2.get(i)));
		}
	}
	
	private static boolean avoid_parallel_fifths = true;
	private static boolean avoid_parallel_octaves = true;
	private static boolean avoid_direct_fifths = true;
	private static boolean avoid_direct_octaves = true;
	
	
	public float get_compliance(HarmonizationTonnetz harmo_tonnetz){
		
		assert size()>0 : "NVoiceTransition must have at least one voice";
		if (size()==1) return harmo_tonnetz.get_horizontal_compliance(get(0)._p1, get(0)._p2);
		
		boolean forbidden_mvmt = false;
		boolean bad_mvmt = false;
		
		//System.out.println("TOC");
		for (int i=0;i<size()-1;i++){
			for (int j=i+1;j<size();j++){
				TwoVoiceTransition two_voice_transition = new TwoVoiceTransition(get(i),get(j));
				//System.out.println("TVT : "+two_voice_transition);
				if((avoid_parallel_fifths && two_voice_transition.parallel_motion(7)) ||
				   (avoid_parallel_octaves && two_voice_transition.parallel_motion(0))){
					System.out.println("Forbidden mouvment ! "+_list1+" -> "+_list2);
					forbidden_mvmt=true;
				}
				if((avoid_direct_fifths && two_voice_transition.direct_motion(7)) ||
				   (avoid_direct_octaves && two_voice_transition.direct_motion(0))){
					System.out.println("Bad mouvment ! "+_list1+" -> "+_list2);
					bad_mvmt = true;
				}
			}
		}

		if (forbidden_mvmt) return 0;
		if (bad_mvmt) return (float)0.2;
				
		// Si aucune plaque n'est ˆ 0, alors on dŽfinit la compliance comme le produit VC1*HC*VC2
		// Chaque VC est pris 2 fois en compte car on considre un accord une fois dans le contexte de son passŽ
		// et une fois dans le contexte de son futur
		
//		System.out.println(" ... horizontal compliance : "+get_horizontal_compliance(harmo_tonnetz));
//		System.out.println(vc1);
//		System.out.println(vc2);
		
		// Finalement on ne prend pas en compte les compl verticales car elles seront prises en compte lors du produit final
		//return vc1*get_horizontal_compliance(harmo_tonnetz)*vc2;
		//return get_horizontal_compliance(harmo_tonnetz);
		return 1;
		
	}
	
	// horizontal compliance = moyenne des horizontal compliances de chaque voice transition 
	public float get_horizontal_compliance(HarmonizationTonnetz harmo_tonnetz){
		float horizontal_compliance = 0;
		for (PitchTransition pt : this){
			horizontal_compliance = horizontal_compliance+harmo_tonnetz.get_horizontal_compliance(pt);
		}
		return horizontal_compliance/(float)size();
	}
		
}

//
//			 hi1
//		p1 --------->p3
//		|	 t1		 |
//  vi1	|			 | vi2
//		|	 t2		 |
//		p2 --------->p4
//			 hi2
//


class TwoVoiceTransition{

	private PitchTransition _t1;
	private PitchTransition _t2;
	
	public TwoVoiceTransition(PitchTransition pt1, PitchTransition pt2){
		_t1=pt1;
		_t2=pt2;
	}
	
//	public float get_compliance(HarmonizationTonnetz harmo_tonnetz){
//		float vc1 = harmo_tonnetz.get_vertical_compliance(_t1._p1, _t2._p1);
//		float vc2 = harmo_tonnetz.get_vertical_compliance(_t1._p2, _t2._p2);
////		float hc1 = harmo_tonnetz.get_horizontal_compliance(_t1._p1, _t1._p2);
////		float hc2 = harmo_tonnetz.get_horizontal_compliance(_t2._p1, _t2._p2);
//		// compliance de la plaque comme produit des 4 compliances : inconvŽnient : si une compliance 
//		// verticale est nulle la compliance de la plaque est nulle
//		//return vc1*vc2*hc1*hc2;
//		// 2me mŽthode : on fait la moyenne des 4 compliances : inconvŽnient : on ne prend plus en compte
//		// les 2-compliances verticales >0 (seules les arrtes verticales comptent)
//		// 3me mŽthode : produit vc1*hc*vc2
//		float hc = get_horizontal_compliance(harmo_tonnetz);
//		return vc1*hc*vc2;
//		
//	}
//
//	// Moyenne des 2 compliances horizontales
//	public float get_horizontal_compliance(HarmonizationTonnetz harmo_tonnetz){
//
//		float hc1 = harmo_tonnetz.get_horizontal_compliance(_t1._p1, _t1._p2);
//		float hc2 = harmo_tonnetz.get_horizontal_compliance(_t2._p1, _t2._p2);
//		if (parallel_motion(7)){
//			return 0;
//		}
//		return (hc1+hc2)/(float)2;
//	}
	
	public boolean parallel_motion(int interval){
		
		if (get_hi1() == get_hi2() && get_hi1()!=0){
			assert get_vi1() == get_vi2();
			if (get_vi1()%12 == interval){
				return true;
			}
		}
		return false;
	}
	
	public boolean direct_motion(int interval){
		
		if((get_hi1()>0 && get_hi2()>0) || (get_hi1()<0 && get_hi2()<0)){
			if(get_vi2()%12 == interval){
				//System.out.println("direct motion "+interval+" ! : "+this);
				return true;
			}
		}		
		return false;
	}
	
	public int get_hi1(){
		return _t1.get_interval();
	}

	public int get_hi2(){
		return _t2.get_interval();
	}

	public int get_vi1(){
		return Math.abs(_t1._p1-_t2._p1);
	}

	public int get_vi2(){
		return Math.abs(_t1._p2-_t2._p2);
	}
	
	public String toString(){
		String str = "\n"+_t1+"\n"+_t2;
		return str;
	}
	
}

public class HarmonizationTonnetz {
	
	private TIChordComplex _v_tonnetz;
	private TonnetzRanking _h_tonnetz_ranking;
	
	public HarmonizationTonnetz(TIChordComplex v_tonnetz, TonnetzRanking h_tonnetz_ranking){
		_v_tonnetz = v_tonnetz;
		_h_tonnetz_ranking = h_tonnetz_ranking;
	}
	
	public float get_vertical_compliance(int p1, int p2){
		return _v_tonnetz.get_global_compliance(new PitchSet(new int[]{p1,p2}));
	}
		
	public float get_vertical_compliance(PitchSet pitch_set){
		return _v_tonnetz.get_global_compliance(pitch_set);
	}
	
	public PitchClassRanking get_vertical_compliance_ranking(PitchSet pitch_set){
		return _v_tonnetz.get_compliance_ranking(pitch_set);
	}
	
	public PitchClassRanking get_vertical_compliance_ranking(PitchSet pitch_set, PitchClassSet forced_chord){
		return _v_tonnetz.get_compliance_ranking(pitch_set,forced_chord);
	}

	private static boolean AVOID_TRITON = true;
	
	public float get_horizontal_compliance(int p1, int p2){
		if (AVOID_TRITON && Interval.MI(p1, p2, 12) == 6){
			return 0;
		}
		return _h_tonnetz_ranking.get_compliance(p1, p2);
	}

	public float get_horizontal_compliance(PitchTransition pt){
		return _h_tonnetz_ranking.get_compliance(pt._p1, pt._p2);
	}
	
	public float get_transversal_compliance(ArrayList<Integer> list1, ArrayList<Integer> list2){
		NVoiceTransition transition = new NVoiceTransition(list1, list2);		
		if (list1.size()!=list2.size()){
			
		}
		return transition.get_compliance(this);
	}
	
	public float get_transversal_compliance(PitchSet set1, int n1, PitchSet set2, int n2){
		ArrayList<Integer> list1 = new ArrayList<Integer>(set1);
		list1.add(n1);
		ArrayList<Integer> list2 = new ArrayList<Integer>(set2);
		list2.add(n2);
		return get_transversal_compliance(list1,list2);
	}

	
//	public PitchRanking get_pitch_ranking(HarmonizationVoice voice,ColStream other_voices,long key,PitchRanking vertical_ranking){
//		
//		PitchRanking pitch_ranking = new PitchRanking();
//		
//		for (float vertical_score : vertical_ranking.keySet()){
//			for (int pitch : vertical_ranking.get(vertical_score)){
//				for(Float coef : _h_tonnetz_ranking.keySet()){
//					int last_pitch = voice.lowerEntry(key).getValue().get_pitch();
//					if(_h_tonnetz_ranking.get(coef).get_neighbors_pitch(last_pitch).contains(pitch) || pitch==last_pitch){
//						float plate_comp = get_transversal_compliance(other_voices.get(voice.lowerKey(key)),voice.lowerEntry(key).getValue().get_pitch(), other_voices.get(key),pitch);
////						//System.out.println("key : "+key+" pitch : "+pitch);
//						if(plate_comp>0){
//							pitch_ranking.add(pitch, vertical_score);
//						}					
//					}
//				}
//			}
//		}
//		return pitch_ranking;
//		
//	}
	
	
	public HashSet<PitchWithCompliance> get_pitch_with_compliance_set(PitchRanking vertical_ranking,HarmonizationVoice voice,long key,PitchSetStream other_voices){
		HashSet<PitchWithCompliance> pitch_with_compliance_set = new HashSet<PitchWithCompliance>();
		for (float vertical_score : vertical_ranking.keySet()){
			for (int pitch : vertical_ranking.get(vertical_score)){
				int last_pitch = voice.lowerEntry(key).getValue().get_pitch();
				float transversal_compliance;
				if(other_voices.lowerEntry(key) != null && !other_voices.lowerEntry(key).getValue().isEmpty()){
					PitchSetWithDuration last_other_voices_col = new PitchSetWithDuration(other_voices.lowerEntry(key).getValue());
					transversal_compliance = get_transversal_compliance(last_other_voices_col, last_pitch, other_voices.floorEntry(key).getValue(), pitch);
				} else {
					transversal_compliance = 1;
				}
				pitch_with_compliance_set.add(new PitchWithCompliance(pitch,vertical_score,get_horizontal_compliance(last_pitch, pitch),transversal_compliance));
			}
		}
		return pitch_with_compliance_set;
	}
		
}
