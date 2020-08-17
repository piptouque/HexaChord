package Model.Music.Tonnetze;

import java.util.HashSet;

import Model.Music.PitchClassSet;

public class AffnChordComplex extends ChordComplex{

	public PitchClassSet _prime_pcset;
	
	public AffnChordComplex(HashSet<PitchClassSet> chord_set, PitchClassSet pc_set){
		super(chord_set);
		_prime_pcset = pc_set;
	}

	public String get_latex_name() {		
		return "\\cm{"+_prime_pcset.toString()+"}";
	}
	
	
}
