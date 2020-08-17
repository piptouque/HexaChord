package Model.Music;

import java.util.ArrayList;
import java.util.Collections;

import Model.Music.Harmonization.PitchClassRanking;
import Model.Music.Harmonization.PitchRanking;
import Model.Music.Tonnetze.FoldedGraphTonnetz;
import Model.Music.Tonnetze.HarmonizationTonnetz;
import Model.Music.Tonnetze.TIChordComplex;
import Model.Music.Tonnetze.Z12FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z7FoldedGraphTonnetz;
import Utils.IntegerSet;
import Utils.MidiPlayer;

public class PitchSetWithDuration extends PitchSet{
	
	private static final long serialVersionUID = 1L;
	private long _duration;
	//private int _channel;
	
	public PitchSetWithDuration(long d){
		super();
		_duration=d;
	}

	public PitchSetWithDuration(){
		super();
	}

//	public PitchSetWithDuration(Chord c, long d){
//		super();
//		_duration = d;
//		for (Note n : c){
//			add(n.get_pitch());
//		}
//	}

	public PitchSetWithDuration(PitchClassSet st_chord,long duration) {
		super(st_chord);
		_duration = duration;
	}

	public PitchSetWithDuration(PitchSet set,long duration) {
		super(set);
		_duration = duration;
	}

	public PitchSetWithDuration(PitchSetWithDuration pitch_set) {
		super(pitch_set);
		_duration = pitch_set.get_duration();
	}

	public long get_duration() {
		return _duration;
	}

	public void set_duration(long _duration) {
		this._duration = _duration;
	}
	
	public void grow_duration(long n){
		this._duration = this._duration+n; 
	}
	
	public PitchSetWithDuration remove_pitch(int pitch){
		PitchSetWithDuration new_coll = new PitchSetWithDuration(_duration);
		if (size()>0){ 
			for (int n : this){
				if (n!=pitch){
					new_coll.add(n);
				}
			}			
		}
		return new_coll;
	}
	
	public PitchSetWithDuration union(PitchSetWithDuration c){
		PitchSetWithDuration col = this;
		for (int n : c){
			col.add(n);
		}
		return col;
	}
		
	public boolean is_equal(PitchSetWithDuration c){
		return equals(c);
	}
	
	public String toString(){
		String str = "[";
		ArrayList<Integer> list = new ArrayList<Integer>(this);
		Collections.sort(list);
		for (int n=0;n<list.size();n++){
			if (n==0) str = str+list.get(n);
			else str = str+","+list.get(n);
		}
		str=str+"]"+"~"+_duration;
		
		return str;
	}
	
	public void playColl(MidiPlayer m) {
//		for(Integer i : this) m.playNote(0, i, 64);
		m.playMusicCollection(this);
	}
	
	public PitchSetWithDuration to_STMusicCollection(int N){
		PitchSetWithDuration c = new PitchSetWithDuration(to_PitchClassSet(N),_duration);
		return c;
	}
	
	public PitchClassSet to_PitchClassSet(){
		return to_PitchClassSet(12);
	}
	
	public PitchClassSet to_PitchClassSet(int N){
		PitchClassSet set = new PitchClassSet();
		for(int n : this) set.add(n%N);
		return set;
	}
	
//	public ArrayList<Z12PlanarUnfoldedTonnetz> most_compliant_tonnetz(ArrayList<Z12PlanarUnfoldedTonnetz> tonnetze){
//		ArrayList<Z12PlanarUnfoldedTonnetz> best_tonnetz = new ArrayList<Z12PlanarUnfoldedTonnetz>();
//		float comp_max = 0;
//		for (Z12PlanarUnfoldedTonnetz t : tonnetze){
//			if (to_STChord(Constant.N).tonnetz_2_compliance(t)>comp_max){
//				best_tonnetz.clear();
//				best_tonnetz.add(t);
//				comp_max = to_STChord(Constant.N).tonnetz_2_compliance(t);
//			} else if (to_STChord(Constant.N).tonnetz_2_compliance(t) == comp_max){
//				best_tonnetz.add(t);
//			}
//		}
//		System.out.println("Voici les meilleurs tonnetzs de "+this+" : "+best_tonnetz);
//		if (best_tonnetz.size()!=tonnetze.size())
//			return best_tonnetz;
//		else return new ArrayList<Z12PlanarUnfoldedTonnetz>();
//	}

	public ArrayList<TIChordComplex> most_n_compliant_tonnetz(ArrayList<TIChordComplex> tonnetze,int n){
		ArrayList<TIChordComplex> best_tonnetz = new ArrayList<TIChordComplex>();
		float comp_max = 0;
		for (TIChordComplex t : tonnetze){
			System.out.println("tested tonnetz : "+t);
			//if (to_STChord(Constant.N).tonnetz_2_compliance(t)>comp_max){
			if (t.get_n_compliance(this, n)>comp_max){
				best_tonnetz.clear();
				best_tonnetz.add(t);
				comp_max = t.get_n_compliance(this, n);
			} else if (t.get_n_compliance(this, n) == comp_max){
				best_tonnetz.add(t);
			}
		}
		System.out.println("Voici les meilleurs tonnetzs de "+this.to_PitchClassSet()+" : "+best_tonnetz);
		if (best_tonnetz.size()!=tonnetze.size())
			return best_tonnetz;
		else return new ArrayList<TIChordComplex>();
	}

	// SPATIAL HARMONIZATION
	
	public float get_similarity(PitchSetWithDuration collection){

		float similarity=0;
		
		if (isEmpty() && collection.isEmpty()) return 1;
		
		for (int i : this){
			if (collection.contains(i)){
				similarity++;
			}
		}
		return similarity/Math.max(size(), collection.size());		
	}
	
	public PitchRanking get_harmo_pitch_ranking(HarmonizationTonnetz harmonization_tonnetz, Z7FoldedGraphTonnetz z7_tonnetz,ContinuePitchSpace continue_pitch_space){
		return new PitchRanking(get_harmo_pc_ranking(harmonization_tonnetz,z7_tonnetz),continue_pitch_space);
	}
		
	public PitchRanking get_harmo_pitch_ranking(HarmonizationTonnetz harmonization_tonnetz, ContinuePitchSpace continue_pitch_space){
		return new PitchRanking(harmonization_tonnetz.get_vertical_compliance_ranking(this),continue_pitch_space);
	}

	public PitchRanking get_harmo_pitch_ranking(HarmonizationTonnetz harmonization_tonnetz, ContinuePitchSpace continue_pitch_space,PitchClassSet forced_chord){
		return new PitchRanking(harmonization_tonnetz.get_vertical_compliance_ranking(this,forced_chord),continue_pitch_space);
	}

	public PitchClassRanking get_harmo_pc_ranking(HarmonizationTonnetz harmonization_tonnetz, Z7FoldedGraphTonnetz z7_tonnetz){
		PitchClassRanking ranking = new PitchClassRanking();
		
		boolean tonal;

		if (z7_tonnetz == null){
			tonal = false;
		} else {
			tonal = true;
		}

		ranking = harmonization_tonnetz.get_vertical_compliance_ranking(this);
		//ranking = z12_tonnetz.get_compliance_ranking(this);
		if(tonal){
			PitchClassRanking tonal_ranking = new PitchClassRanking();
			for (float score : ranking.keySet()){
				IntegerSet new_set = new IntegerSet();
				for (int pitch : ranking.get(score)){
					if (z7_tonnetz.contains_pitch(pitch)) {
						new_set.add(pitch);
					}
				}
				if (!new_set.isEmpty()) {
					tonal_ranking.put(score, new_set);
				}
			}
		ranking = tonal_ranking;
		}
		
		return ranking;
	}
	
	public IntegerSet harmo_pc_set_generation(Z12FoldedGraphTonnetz z12_tonnetz, Z7FoldedGraphTonnetz z7_tonnetz){
		
		boolean tonal;
		boolean triad = true;
		int compliance_degree = 2;
		boolean doubl = true;
		if (z7_tonnetz == null){
			tonal = false;
		} else {
			tonal = true;
		}
		PitchClassSet st_chord = this.to_PitchClassSet();
		IntegerSet vertical_candidates = new IntegerSet();
		
//		ArrayList<FoldedTonnetz> priority_vertical_harmo_tonnetz_list = new ArrayList<FoldedTonnetz>();
//		priority_vertical_harmo_tonnetz_list.add(Z12FoldedChordTonnetz.getZ12FoldedChordTonnetzList().get(16));
		
		//System.out.println("chord : "+this.to_STChord());
		
		IntegerSet neighbors_candidates = new IntegerSet();
		
		if (compliance_degree == 1){
			
		}
		
		
		
		if (triad){
			neighbors_candidates = z12_tonnetz.complete_chord_triad_pc_set(st_chord);
			if (neighbors_candidates.isEmpty()){
				neighbors_candidates = z12_tonnetz.closer_pitch_class_set(st_chord);
			}
		} else {
			neighbors_candidates = z12_tonnetz.closer_pitch_class_set(st_chord);
		}
		if (tonal){
			for (int i : neighbors_candidates){
				if (z7_tonnetz.contains_pitch(i)){
					vertical_candidates.add(i);
				}
			}				
		}
		if (doubl && st_chord.size()>2){
			vertical_candidates.addAll(st_chord);
		}
			//vertical_candidates.addAll(z12_tonnetz.closer_pitch_class_set(this.to_STChord()));
			//vertical_candidates.addAll(z12_tonnetz.complete_chord_triad_pc_set(this.to_STChord()));
		//System.out.println("candidates : "+vertical_candidates);
		return vertical_candidates;
	}

//	public HashSet<MusicCollection> harmo_collection_set_generation(){
//		HashSet<MusicCollection> set = new HashSet<MusicCollection>();
//		for (int pc : harmo_pc_set_generation()){
//			MusicCollection col = new MusicCollection();
//			col.add(pc);
//			set.add(col);
//		}
//		return set;
//	}

	public void print_harmo(ArrayList<FoldedGraphTonnetz> tonnetz_list) {
		System.out.println("chord : "+this.to_PitchClassSet());
		for (FoldedGraphTonnetz t : tonnetz_list){
			System.out.println(t.to_string()+" : "+t.closer_pitch_class_set(this.to_PitchClassSet()));
		}
	}

//	@Override
//	public PitchSetWithDuration remove_pitch() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
