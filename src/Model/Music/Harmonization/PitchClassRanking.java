package Model.Music.Harmonization;

import java.util.Map;

import Utils.IntegerSet;

public class PitchClassRanking extends PitchRanking{

	private static final long serialVersionUID = 1L;
	
	public PitchClassRanking(){
		super();
	}

	public Map.Entry<Float, IntegerSet> get_best_pc(){
		return get_best_pitch();
	}

}
