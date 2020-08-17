package Model.Music.Harmonization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

import Model.Music.PitchSetStream;
import Model.Music.PitchSetWithDuration;
import Model.Music.Tonnetze.HarmonizationTonnetz;

public class HarmonizationVoice extends TreeMap<Long,PitchWithCompliance>{

	private static final long serialVersionUID = 1L;

	private long _duration;
	

	private float _final_vertical_compliance;
	private float _final_horizontal_compliance;
	private float _final_transversal_compliance;
	private HarmonizationTonnetz _harmonization_tonnetz;

	public HarmonizationVoice(long duration, HarmonizationTonnetz harmonization_tonnetz){
		super();
		_duration = duration;
		_harmonization_tonnetz = harmonization_tonnetz;
	}

	public HarmonizationVoice(HarmonizationVoice voice){
		super(voice);
		_duration = voice.get_duration();
		_harmonization_tonnetz = voice._harmonization_tonnetz;
		_final_vertical_compliance = voice._final_vertical_compliance;
		_final_horizontal_compliance = voice._final_horizontal_compliance;
		_final_transversal_compliance = voice._final_transversal_compliance;
	}
	
	public HarmonizationVoice(PitchSetStream voice, HarmonizationTonnetz harmonization_tonnetz,PitchSetStream other_voices){
		super();
		//_vertical_tonnetz = harmonization_tonnetz.g vertical_tonnetz;
		//_horizontal_tonnetz_ranking = horizontal_tonnetz_ranking;
		HashSet<Long> key_set = new HashSet<Long>(other_voices.keySet());
		key_set.addAll(voice.keySet());
		ArrayList<Long> key_list = new ArrayList<Long>(key_set);
		Collections.sort(key_list);
		
		for (int key_index = 0;key_index<key_list.size();key_index++){

			long key = key_list.get(key_index);
			if(voice.floorEntry(key) != null && !voice.floorEntry(key).getValue().isEmpty()){
				float vertical_compliance;
				float horizontal_compliance;
				float transversal_compliance = 0;
				PitchSetWithDuration current_voice = voice.floorEntry(key).getValue();
				assert current_voice.size()==1 : "collection "+current_voice+" must have 1 element to be in HarmonizationVoice";
				int current_voice_pitch = current_voice.get(0);
				// horizontal compliance
				if(voice.lowerEntry(key)!=null && !voice.lowerEntry(key).getValue().isEmpty()){
					int last_pitch = voice.lowerEntry(key).getValue().get(0);
					horizontal_compliance = harmonization_tonnetz.get_horizontal_compliance(last_pitch, current_voice_pitch);
					if(other_voices.lowerEntry(key) != null && !other_voices.lowerEntry(key).getValue().isEmpty()){
						System.out.println("mesure : "+key/4000+" "+other_voices.floorEntry(key).getValue().print_pitch_set());
						
						// Une collection ne peut avoir une compliance transversale que si elle est associŽe ˆ une transition impliquant des collections de taille Žgales 
						ArrayList<Integer> first_list = new ArrayList<Integer>(other_voices.lowerEntry(key).getValue());
						ArrayList<Integer> second_list = new ArrayList<Integer>(other_voices.floorEntry(key).getValue());

						if (first_list.size() == second_list.size()){
							transversal_compliance = harmonization_tonnetz.get_transversal_compliance(other_voices.lowerEntry(key).getValue(), last_pitch, other_voices.floorEntry(key).getValue(), current_voice_pitch);
						} else {
							transversal_compliance = -1;
						}
											
					} else {
						transversal_compliance = 1;
					}

				} else {
					horizontal_compliance = 1;
					transversal_compliance = 1;
				}
				// vertical compliance
				
				if(other_voices.floorEntry(key) != null && !other_voices.floorEntry(key).getValue().isEmpty()){
					PitchSetWithDuration mixed_current_pitch_set = new PitchSetWithDuration(other_voices.floorEntry(key).getValue());
					mixed_current_pitch_set.add(current_voice_pitch);
					vertical_compliance = harmonization_tonnetz.get_vertical_compliance(mixed_current_pitch_set);
				} else {
					vertical_compliance = harmonization_tonnetz.get_vertical_compliance(current_voice);
				}
				put(key,new PitchWithCompliance(current_voice_pitch,vertical_compliance,horizontal_compliance,transversal_compliance));
					
				
			}
		}
		_duration = voice.get_duration();
		_harmonization_tonnetz = harmonization_tonnetz;
		compute_final_compliances();
	}
	

	public long get_pitch_duration(long key){
		if(key != lastKey()){
			return higherKey(key)-key;
		} else {
			return _duration-key;
		}
	}

//	public float getVertical_compliance() {
//		if (_vertical_compliance == 0){
//			_vertical_compliance = compute_vertical_compliance();
//		}
//		return _vertical_compliance;
//	}

//	public float getHorizontal_compliance() {
//		if (_horizontal_compliance == 0){
//			_horizontal_compliance = compute_horizontal_compliance();
//		}
//		return _horizontal_compliance;
//	}
//	public void update_horizontal_compliance(float compliance) {
//		_horizontal_compliance = _horizontal_compliance+compliance;
//	}
	
//	public void reset_horizontal_compliance(){
//		_horizontal_compliance = 0;
//	}

//	public void set_horizontal_compliance(float _horizontal_compliance) {
//		this._horizontal_compliance = _horizontal_compliance;
//	}
	
	public float compute_horizontal_compliance(long last_key){
		float horizontal_compliance = 0;
		float moves_count = 0;
		long key = higherKey(firstKey());
		while (key < last_key){
			if (get(key)!=null){
				horizontal_compliance = horizontal_compliance+get(key).get_h_compliance();
				moves_count = moves_count+1;
			}
			key = higherKey(key);
		}
		horizontal_compliance = horizontal_compliance+get(lastKey()).get_h_compliance();
		moves_count = moves_count+1;

		if (moves_count == 0) return 1;
		return horizontal_compliance/moves_count;
	}
	
	public float compute_vertical_compliance(long last_key){
		float sum_vertical_compliance = 0;
		long key=firstKey();
		while (key<last_key){
			PitchWithCompliance pitch_with_compliance = get(key);
			if (pitch_with_compliance!=null){
				sum_vertical_compliance = sum_vertical_compliance + pitch_with_compliance.get_v_compliance()*get_pitch_duration(key);
			}
			key=higherKey(key);				
		}
		sum_vertical_compliance = sum_vertical_compliance + get(lastKey()).get_v_compliance()*get_pitch_duration(lastKey());
		return sum_vertical_compliance/(float)_duration;
	}
	
	public float compute_transversal_compliance(long last_key) {
		float transversal_compliance = 0;
		float moves_count = 0;
		long key = higherKey(firstKey());
		while (key < last_key){
			if (get(key)!=null){
				if (get(key).get_t_compliance()>=0){
					transversal_compliance = transversal_compliance+get(key).get_t_compliance();
					moves_count = moves_count+1;
				}
			}
			key = higherKey(key);
		}
		if (get(lastKey()).get_t_compliance()>=0){
			transversal_compliance = transversal_compliance+get(lastKey()).get_t_compliance();
			moves_count = moves_count+1;
		}

//		if (moves_count == 0) return 1;
//		if((transversal_compliance/moves_count) > (float)0.95){
//			System.out.println("tcsum : "+transversal_compliance+" moves_count : "+moves_count);
//			key = higherKey(firstKey());
//			while (key < last_key){
//				if (get(key)!=null){
//					System.out.println("key : "+key+" tc : "+get(key).get_t_compliance());
//				}
//				key = higherKey(key);
//			}
//			System.out.println("key : "+lastKey()+" tc : "+get(lastKey()).get_t_compliance());
//
//		}
		return transversal_compliance/moves_count;
	}
	
	public void compute_final_compliances(){
		_final_vertical_compliance = compute_vertical_compliance(lastKey());
		_final_horizontal_compliance = compute_horizontal_compliance(lastKey());
		_final_transversal_compliance = compute_transversal_compliance(lastKey());
	}

	public long get_duration() {
		return _duration;
	}

	public PitchSetStream to_ColStream(){
		PitchSetStream stream = new PitchSetStream();
		
		for (long key : keySet()){
			PitchSetWithDuration pitch_set = new PitchSetWithDuration();
			pitch_set.add(get(key).get_pitch());
			if(key!=lastKey()){
				pitch_set.set_duration(higherKey(key)-key);
			} else {
				pitch_set.set_duration(_duration-key);
			}
			stream.put(key, pitch_set);
		}
		
		return stream;
	}

	public float get_final_vertical_compliance() {
		return _final_vertical_compliance;
	}

	public float get_final_horizontal_compliance() {
		return _final_horizontal_compliance;
	}
	
	public float get_final_transversal_compliance() {
		return _final_transversal_compliance;
	}
	
	public float get_mixed_compliance(){
		float c = _final_transversal_compliance*(_final_horizontal_compliance+(float)2*_final_vertical_compliance)/(float)3;
		return c;
	}

}
