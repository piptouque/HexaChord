package Utils;

import Model.Music.PitchClassSet;
import Model.Music.PitchSet;

public class MusicUtil {
	
	public static int get_pitch_from_frequency(float f){
		return (int)(69+12*(Math.log((f/(float)440))/Math.log(2)));
	}

	public static double get_frequency_from_pitch(int n){
		return Math.exp(((float)(n-69)/(float)12)*Math.log(2)+Math.log(440));
	}
	
	public static void test_funda(PitchClassSet pcs){
		int pitch_min=0;
		
		for (int pc : pcs){
			System.out.println("tmp_pc_fund : "+pc);
			PitchSet ps = new PitchSet();
			int tmp_funda_pitch = pc+pitch_min;
			ps.add(tmp_funda_pitch);
			for (int pc2 : pcs){
				if (pc2!=pc){
					int pitch_to_add;
					if (pc2+pitch_min>tmp_funda_pitch) pitch_to_add=pc2+pitch_min;
					else pitch_to_add=pc2+pitch_min+12;
					ps.add(pitch_to_add);
					double freq_ratio = MusicUtil.get_frequency_from_pitch(pitch_to_add)/MusicUtil.get_frequency_from_pitch(tmp_funda_pitch);
					System.out.println("ratio avec "+pitch_to_add+" : "+freq_ratio);
				}
			}
		}

	}

	public static void test_funda2(){
		
	}
}
