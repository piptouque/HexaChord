package Model.Music.Harmonization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.HarmonizationTonnetz;

public class DynamicPitchRanking extends TreeMap<Long,PitchRanking>{

	private static final long serialVersionUID = 1L;
	
	private long _duration;

	public DynamicPitchRanking(long duration){
		super();
		_duration = duration;
	}
	
	private HashSet<PitchWithCompliance> select_best(HashSet<PitchWithCompliance> pitch_with_compliance_set,int count) {
		HashSet<PitchWithCompliance> new_set = new HashSet<PitchWithCompliance>();
//		System.out.println("SIZE : "+pitch_with_compliance_set.size());
		ArrayList<Float> quotient_list = new ArrayList<Float>();
		for (PitchWithCompliance pwc : pitch_with_compliance_set){
			float quotient;
			if (pwc.get_t_compliance()>=0){
				quotient = pwc.get_h_compliance()*pwc.get_v_compliance()*pwc.get_t_compliance(); 
			} else {
				quotient = pwc.get_h_compliance()*pwc.get_v_compliance();
			}
			
			quotient_list.add(quotient);
			//System.out.println("quotient : "+quotient);
		}
		Collections.sort(quotient_list);
//		System.out.println("quotient_list : "+quotient_list);
		float coef_mini=quotient_list.get(quotient_list.size()-count);
		for (PitchWithCompliance pwc : pitch_with_compliance_set){
			
			if (pwc.get_t_compliance()>=0){
				if (pwc.get_h_compliance()*pwc.get_v_compliance()*pwc.get_t_compliance()>=coef_mini){
					new_set.add(pwc);
				}	 
			} else {
				if (pwc.get_h_compliance()*pwc.get_v_compliance()>=coef_mini){
					new_set.add(pwc);
				}
			}
		}
//		System.out.println("SIZE post : "+new_set.size());
		return new_set;
	}

	
	private static boolean select_best_pitchs = true;
	
	public void generation(HarmonizationTonnetz harmonization_tonnetz,PitchSetStream other_voices,ArrayList<HarmonizationVoice> voice_list,HarmonizationVoice voice,long key, HarmonizationVoice target_voice){

		//if (key == higherKey(firstKey())){
		if (key == 1000){
			
			int div = firstEntry().getValue().get_total_element_count() * higherEntry(firstKey()).getValue().get_total_element_count();
			System.out.println("+ 1/"+div+" "+voice);
		}
		
		if (key==firstKey()){
			ArrayList<Float> first_vertical_scores = new ArrayList<Float>(firstEntry().getValue().keySet());
			for (int i=0;i<first_vertical_scores.size();i++){
				System.out.println((100*((float)i/(float)first_vertical_scores.size()))+"%");
				for (int pitch : firstEntry().getValue().get(first_vertical_scores.get(i))){
					PitchWithCompliance pitch_with_compliance = new PitchWithCompliance(pitch,first_vertical_scores.get(i),1,1);
					voice.put(key, pitch_with_compliance);
					generation(harmonization_tonnetz,other_voices,voice_list,voice,higherKey(key),target_voice);
				}				
			}
		} else {
			
			HashSet<PitchWithCompliance> pitch_with_compliance_set = harmonization_tonnetz.get_pitch_with_compliance_set(get(key),voice,key,other_voices);
			if(select_best_pitchs){
				pitch_with_compliance_set = select_best(pitch_with_compliance_set,1);
			}
			//PitchRanking vertical_pitch_ranking = harmonization_tonnetz.get_pitch_ranking(voice,other_voices,key,get(key));
			//System.out.println("key : "+key+" pitch_ranking : "+pitch_ranking); 
			
			for (PitchWithCompliance pitch_with_compliance : pitch_with_compliance_set){
				addPitchAndTestSize(harmonization_tonnetz, other_voices, pitch_with_compliance,voice_list, voice, key, target_voice);
			}
			
		}
	}
	

	static boolean ABANDONS = true;
	static long ABANDON_KEY = 10;
	static float ABANDON_H_COMP = (float) 0.4;
	static float ABANDON_V_COMP = (float) 0.4;
	static int MAX_VOICES = 45;
	
	private void addPitchAndTestSize(HarmonizationTonnetz harmonization_tonnetz,PitchSetStream other_voices,PitchWithCompliance pitch_with_compliance, ArrayList<HarmonizationVoice> voice_list,HarmonizationVoice voice,long key, HarmonizationVoice target_voice){
		
		//float horizontal_compliance = harmonization_tonnetz.get_horizontal_compliance(voice.lowerEntry(key).getValue().get_pitch(), pitch);
		//float vertical_compliance = vertical_score;
		//PitchWithCompliance pitch_with_compliance = new PitchWithCompliance(vertical_pitch,vertical_score);
		voice.put(key, pitch_with_compliance);
		if(key == lastKey()){
			
			voice.compute_final_compliances();
//			float v_compliance = voice.compute_vertical_compliance(key);
//			float h_compliance = voice.compute_horizontal_compliance(key);
//			float t_compliance = voice.compute_transversal_compliance(key);
			//if(v_compliance>=(float)0.87359565 && v_compliance<(float)0.87359566 && h_compliance>=(float)0.8636364 && h_compliance< (float)0.8636365){
			//if(v_compliance>=(float)0.96 && h_compliance==(float)1){
				//System.out.println(voice_set.size()+" v= "+voice.getVertical_compliance()+" h= "+voice.getHorizontal_compliance()+" voice : "+voice);
				//if(voice.get_final_horizontal_compliance() >= target_voice.get_final_horizontal_compliance()){
				if(voice.get_final_horizontal_compliance() >= (float)0.8){
				//if(voice.get_mixed_compliance() >= (float)0.84){
				//&& voice.get_final_vertical_compliance()>=target_voice.get_final_vertical_compliance()
				//&& voice.get_final_vertical_compliance()>=(float)0.85
				//&& voice.get_final_transversal_compliance()>=target_voice.get_final_transversal_compliance()){
				//&& voice.get_final_transversal_compliance()>=(float)0.4){
				//if(voice.get_final_horizontal_compliance() > (float)0.6 && voice.get_final_vertical_compliance()>0.7){
				//if(voice.get_final_transversal_compliance()>0.95){
				//if((h_compliance > (float)1 && v_compliance>0.9 ) || (v_compliance>0.95 && h_compliance > 0.9)){
					//System.out.println(voice_set.size()+" v= "+voice.getVertical_compliance()+" h= "+voice.getHorizontal_compliance()+" voice : "+voice);
					voice_list.add(new HarmonizationVoice(voice));
					System.out.println("fouded voices : "+voice_list.size()+"vc = "+voice.get_final_vertical_compliance()+" hc = "+voice.get_final_horizontal_compliance()+" tc = "+voice.get_final_transversal_compliance()+" MC : "+voice.get_mixed_compliance());
				} else {
					//System.out.println("abort : "+voice.get_final_vertical_compliance()+" "+voice.get_final_horizontal_compliance()+" "+voice.get_final_transversal_compliance());
				}
			//} 
		} else {
			
			if (voice_list.size()<=MAX_VOICES){
				if (ABANDONS){
					ArrayList<Long> key_list = new ArrayList<Long>(keySet());
					if (!(key_list.indexOf(key) > ABANDON_KEY && voice.compute_horizontal_compliance(key)<ABANDON_H_COMP && voice.compute_vertical_compliance(key)<ABANDON_V_COMP)){
						generation(harmonization_tonnetz,other_voices,voice_list,voice,higherKey(key),target_voice);									
					} else {
						//System.out.println("Abandon : current hori comp : "+voice.compute_horizontal_compliance(key)+" current verti comp : "+voice.compute_vertical_compliance(key)+" key : "+key);					
					}					
				} else {
					generation(harmonization_tonnetz,other_voices,voice_list,voice,higherKey(key),target_voice);
				}

			}
		}
	}


	public ArrayList<HarmonizationVoice> get_voice_set(HarmonizationTonnetz harmonization_tonnetz, PitchSetStream col_stream, HarmonizationVoice target_harmo_voice) {
		ArrayList<HarmonizationVoice> voice_list = new ArrayList<HarmonizationVoice>(); 
		generation(harmonization_tonnetz,col_stream,voice_list,new HarmonizationVoice(_duration,harmonization_tonnetz),firstKey(),target_harmo_voice);
		return voice_list;
	}

	public long get_duration() {
		return _duration;
	}

	public void set_duration(long _duration) {
		this._duration = _duration;
	}
	
	
}
