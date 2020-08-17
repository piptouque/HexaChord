package Model.Music;

import java.util.ArrayList;
import java.util.List;

public class HAChordCandidate {

	private PitchClassSet _chord;
	private Scale _key;
	
	public HAChordCandidate(PitchClassSet chord, Scale key){
		_chord = chord;
		_key = key;
	}
	
	public void set_chord(PitchClassSet chord) {
		this._chord = chord;
	}

	public void set_tonality(Scale key) {
		this._key = key;
	}

	public PitchClassSet get_chord() {
		return _chord;
	}

	public Scale get_key() {
		return _key;
	}
	
	public String toString(){
		//return "{"+_key+","+_chord+"}--"+get_basic_space();
		return "{"+_key+","+_chord+"}";
	}
	
	// Lerdahl's basic space
	public List<Integer> get_basic_space(){
		assert _chord.has_estimated_fundamental() : "basic space can not be computed because "+_chord+" has no estimated fundamental";
		List<Integer> basic_space = new ArrayList<Integer>(12);
		for (int i=0;i<12;i++) {
			basic_space.add(1);
			if (_key.contains(i)) basic_space.set(i,basic_space.get(i)+1);
			if (_chord.contains(i)) basic_space.set(i,basic_space.get(i)+1);
			if (_chord.get_estimated_fundamental()==i || (_chord.get_estimated_fundamental()+7)%12==i) basic_space.set(i,basic_space.get(i)+1);
			if (_chord.get_estimated_fundamental()==i) basic_space.set(i,basic_space.get(i)+1);
		}
		return basic_space;
	}
	
	// Lerdahl tonal distance
	public static int transition_cost(HAChordCandidate first_cand, HAChordCandidate second_cand){
		
		int i=first_cand.get_key().get_distance_to(second_cand.get_key());
		int j=first_cand.get_chord().get_distance_in_fifth_circle(second_cand.get_chord());
		int k=0;
		for (int a=0;a<12;a++){
			if (second_cand.get_basic_space().get(a)>first_cand.get_basic_space().get(a)) k=k+second_cand.get_basic_space().get(a)-first_cand.get_basic_space().get(a);
		}
		
		return i+j+k;
	}
	
	public boolean equals(HAChordCandidate compared_candidate){
		if (_chord.equals(compared_candidate.get_chord()) && _key.equals(compared_candidate.get_key())) return true;
		return false;
	}
	
	
}
