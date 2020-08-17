package Model.Music;

import Utils.IntegerSet;

public class ContinuePitchSpace {

	private int _inf;
	private int _sup;
	
	public ContinuePitchSpace(int inf,int sup){
		assert sup>inf : inf+" must be smaller than "+sup;
		_inf = inf;
		_sup = sup;
	}
	
	public IntegerSet get_pitch_set(IntegerSet pc_set){
		IntegerSet pitch_set = new IntegerSet();
		
		for (int pc : pc_set){
			int p = _inf;
			while (p%12 != pc){
				p++;
			}
			while (p<=_sup){
				pitch_set.add(p);
				p = p+12;
			}
		}
		return pitch_set;
	}
}
