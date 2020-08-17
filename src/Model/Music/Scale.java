package Model.Music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class Scale extends ArrayList<Integer>{
	
	private static final long serialVersionUID = 1L;
	private int _N;
	//private int _pitch;
	private String _mode_name;
	private ArrayList<Integer> _mode;
	
	private static List<Scale> _major_scales;
	private static List<Scale> _minor_scales;
	private static List<Scale> _Hminor_scales;
	private static List<Scale> _diatonic_plus_mH_sensible_scales;
	
//	private static enum Root {Cb,C,Cs,Db,D,Ds,Eb,E,Es,Fb,F,Fs,Gb,G,Gs,Ab,A,As,Bb,B,Bs,N;}
//	private static enum Mode {min,maj};

	
	public Scale(int tonic, String mode_name){
		_N=0;
		_mode_name=mode_name;
		assert (mode_name.equals("major") || mode_name.equals("M") || mode_name.equals("minor") || mode_name.equals("m") || mode_name.equals("Hminor") || mode_name.equals("chromatic")) : "Unknown mode";
		if (mode_name.equals("major")) _mode = get_major_mode();
		if (mode_name.equals("M")) _mode = get_major_mode();
		if (mode_name.equals("minor")) _mode = get_minor_mode();
		if (mode_name.equals("m")) _mode = get_minor_mode();
		if (mode_name.equals("Hminor")) _mode = get_Hminor_mode();
		if (mode_name.equals("chromatic")) _mode = get_chromatic_mode();
		for (int i : _mode){
			_N+=i;
		}
		//_pitch = tonic;
		add(tonic);
		for (int i=0;i<_mode.size()-1;i++){
			add((get(this.size()-1)+_mode.get(i))%_N);
		}
	}
	
	public Scale(String name){
		String mode="", root="";
		if (name.indexOf(":")!=-1){
			int index_dots = name.indexOf(":");
			root = name.substring(0, index_dots);
			mode = name.substring(index_dots+1, name.length());			
		} else {
			System.err.println("wrong key format : "+name);
			System.exit(0);
			//root = name.substring(0, 1);
			//if (!name.equals("N")) mode = "maj";
		}
		
		_N=0;
		_mode_name=mode;
		assert (mode.equals("major") || mode.equals("maj") || mode.equals("M") || mode.equals("minor") || mode.equals("m") || mode.equals("min") || mode.equals("Hminor") || mode.equals("chromatic")) : "Unknown mode";
		if (mode.equals("major")) _mode = get_major_mode();
		if (mode.equals("maj")) _mode = get_major_mode();
		if (mode.equals("M")) _mode = get_major_mode();
		if (mode.equals("minor")) _mode = get_minor_mode();
		if (mode.equals("min")) _mode = get_minor_mode();
		if (mode.equals("m")) _mode = get_minor_mode();
		if (mode.equals("Hminor")) _mode = get_Hminor_mode();
		if (mode.equals("chromatic")) _mode = get_chromatic_mode();
		for (int i : _mode){
			_N+=i;
		}
		//_pitch = tonic;
		add(Note.get_pitch_class(root));
		for (int i=0;i<_mode.size()-1;i++){
			add((get(this.size()-1)+_mode.get(i))%_N);
		}

		
	}
	
	public String get_mode_name(){
		return _mode_name;
	}
	
	public static ArrayList<Integer> get_major_mode(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(2);
		list.add(2);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(2);
		list.add(1);
		return list;
	}
	
	public static ArrayList<Integer> get_minor_mode(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(2);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(1);
		list.add(2);
		list.add(2);
		return list;
	}

	public static ArrayList<Integer> get_Hminor_mode(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(2);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(1);
		list.add(3);
		list.add(1);
		return list;
	}
	
	public static ArrayList<Integer> get_chromatic_mode(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i=0;i<12;i++) list.add(1);
		return list;
	}

	public static Scale get_chromatic_scale(){
		return new Scale(0,"chromatic");
	}
	
	public static ArrayList<Integer> get_fifth_list(){
		ArrayList<Integer> fifth_list = new ArrayList<Integer>();
		for (int i=0;i<12;i++){
			fifth_list.add((i*7)%12);
		}
		return fifth_list;
	}	
	
	public static ArrayList<Scale> get_chromatic_scale_list(){
		ArrayList<Scale> list = new ArrayList<Scale>();
		list.add(get_chromatic_scale());
		return list;
	}
	
	public int get_PC(int n){
		int m = n;
		while(m<0) m=m+size();
		m = m%size();
		return get(m);
	}
	
	public String toString(){
		if (_mode_name.equals("chromatic")) return "chromatic";
		if (_mode_name.equals("major")) return Note.get_name(get(0))+":M";
		if (_mode_name.equals("minor")) return Note.get_name(get(0))+":m";
		if (_mode_name.equals("Hminor")) return Note.get_name(get(0))+":mh";
		return Note.get_name(get(0))+":"+_mode_name;		
	}
	
	public HashSet<Integer> to_set(){
		return new HashSet<Integer>(this);
	}
	
	// exemple : (5,2) in CMajor returns 2 (MIsteps between F and D in Cmajor)
	public int get_MIstep_in_scale(int p1, int p2){ 
		assert contains(p1) && contains(p1) : p1+" and "+p2+" must be in "+toString()+ " scale";
		int dif = Math.abs(indexOf(p1) - indexOf(p2));
		if (dif>size()/2) return size()-dif;
		return dif;
	}
	
	public PitchClassSet get_degree(int n){
		PitchClassSet st_chord = new PitchClassSet();
		st_chord.add(get(0));
		st_chord.add(get(2));
		st_chord.add(get(4));
		return st_chord;
	}
	
//	public ArrayList<Scale> get_diatonic_scales(){
//		ArrayList<Scale> diatonic_scale_list = new ArrayList<Scale>();
//		for (int i=0;i<12;i++) diatonic_scale_list.add(new Scale(i,"major"));
//		return diatonic_scale_list;
//	}
	
	public static Scale[] get_diatonic_scales(){
		Scale[] scales = new Scale[12];
		for (int i=0;i<12;i++) {
			scales[i]=new Scale(i,"major");	
		}
		return scales;
	}
	
//	public static Scale[] get_diatonic_scales_plus_mH_sensible(){
//		Scale[] scales = new Scale[12];
//		for (int i=0;i<12;i++) {
//			Scale scale = new Scale(i,"major"); 
//			scale.add(5, scale.get(4)+1);
//			scales[i]= scale;	
//		}
//		return scales;
//	}

//	public ArrayList<String> get_diatonic_scale_names(){
//		ArrayList<String> diatonic_scale_list = new ArrayList<String>();
//		for (Scale s : get_diatonic_scales()) diatonic_scale_list.add(s.toString());
//		return diatonic_scale_list;
//	}
	
	public static List<Scale> get_major_scales(){
		if (_major_scales==null){
			_major_scales = new ArrayList<Scale>();
			for (int i=0;i<12;i++) _major_scales.add(new Scale(i,"major"));
		}
		return _major_scales;
	}

	public static List<Scale> get_minor_scales(){
		if (_minor_scales==null){
			_minor_scales = new ArrayList<Scale>();
			for (int i=0;i<12;i++) _minor_scales.add(new Scale(i,"minor"));
		}
		return _minor_scales;
	}

	public static List<Scale> get_Hminor_scales(){
		if (_Hminor_scales==null){
			_Hminor_scales = new ArrayList<Scale>();
			for (int i=0;i<12;i++) _Hminor_scales.add(new Scale(i,"Hminor"));
		}
		return _Hminor_scales;
	}
	
	public static List<Scale> get_diatonic_scales_plus_mH_sensible(){
		if (_diatonic_plus_mH_sensible_scales==null){
			_diatonic_plus_mH_sensible_scales = new ArrayList<Scale>();
			//System.out.println("_diatonic_plus_mH_sensible_scales : ");
			for (int i=0;i<12;i++) {
				Scale scale = new Scale(i,"major"); 
				scale.add(5, scale.get(4)+1);
				_diatonic_plus_mH_sensible_scales.add(scale);
				//System.out.println(new ArrayList<Integer>(scale));
			}
		}
		return _diatonic_plus_mH_sensible_scales;
	}

	public Scale get_relative_scale(){
		if (get_mode_name().equals("minor")) return new Scale((get(0)+3)%12,"major");
		if (get_mode_name().equals("major")) return new Scale((get(0)+9)%12,"minor");
		System.err.println("only major and minor scales have a relative scale");
		return null;
	}

	// distance computed in fifth cricle, according to Ledahl's tonal distance
	public int get_distance_to(Scale other_scale){
		
		if (get_mode_name().equals("minor") && other_scale.get_mode_name().equals("major")){
			return 1+get_distance_to(other_scale.get_relative_scale());
		}
		if (get_mode_name().equals("major") && other_scale.get_mode_name().equals("minor")){
			return 1+get_distance_to(other_scale.get_relative_scale());
		}
		return Interval.distance_in_fith_circle(get(0), other_scale.get(0));
	}
	
	public boolean equals(Scale scale){
		if (size()==scale.size()){
			for (int i=0;i<size();i++){
				if (get(i)!=scale.get(i)) return false;
			}
			return true;
		}
		return false;
	}
	
	public List<PitchClassSet> get_3_notes_degrees(){
		List<PitchClassSet> pcs_list = new ArrayList<PitchClassSet>();
		
		if (_mode_name.equals("Hminor")){
			pcs_list.add(new PitchClassSet(get(0),get(2),get(4)));
			pcs_list.add(new PitchClassSet(get(1),get(3),get(5)));
			pcs_list.add(new PitchClassSet(get(2),get(4),(get(6)+11)%12));
			pcs_list.add(new PitchClassSet(get(3),get(5),get(0)));
			pcs_list.add(new PitchClassSet(get(4),get(6),get(1)));
			pcs_list.add(new PitchClassSet(get(5),get(0),get(2)));
			pcs_list.add(new PitchClassSet((get(6)+11)%12,get(1),get(3)));
			return pcs_list;
		}
		if (_mode_name.equals("major") || _mode_name.equals("minor")){
			int mod = size();
			for (int i=0;i<size();i++){
				pcs_list.add(new PitchClassSet(get(i),get((i+2)%mod),get((i+4)%mod)));
			}
			return pcs_list;
		}
		System.err.println("mode not recognized : "+_mode_name);
		return null;
	}
	
	public static boolean test_diatonic_consistency(Set<Integer> pcs){
		//System.out.println("test dc "+pcs);
		for (Scale scale : get_diatonic_scales()){
			if (scale.containsAll(pcs)) return true;
		}
		return false;
	}

	public static boolean test_mH_consistency(Set<Integer> pcs){
//		for (Scale scale : get_diatonic_scales()){
//			if (scale.containsAll(pcs)) return true;
//		}
		return false;
	}
	
	public static boolean test_diatonic_or_mH_consistency(Set<Integer> pcs){
		for (Scale scale : get_diatonic_scales_plus_mH_sensible()){
			if (scale.containsAll(pcs)) return true;
		}
		return false;
	}

	public static List<Scale> get_consistent_scales(List<ChordSymbol> chord_list, List<Scale> candidate_scales){
		List<Scale> consistent_scales = new ArrayList<Scale>();
		
		for (Scale scale : candidate_scales){
			boolean bool = true;
			for (ChordSymbol chord : chord_list){
				//if (!scale.get_3_notes_degrees().contains(chord.get_pcs())) bool = false;
				if (!scale.containsAll(chord.get_pcs())) bool = false;
			}
			if (bool) consistent_scales.add(scale);
		}
		return consistent_scales;
	}
	
	public static List<Scale> get_consistent_scales(List<ChordSymbol> chord_list){
		List<Scale> consistent_scales = new ArrayList<Scale>();
		consistent_scales.addAll(get_consistent_scales(chord_list,get_Hminor_scales()));
		consistent_scales.addAll(get_consistent_scales(chord_list,get_major_scales()));
		consistent_scales.addAll(get_consistent_scales(chord_list,get_diatonic_scales_plus_mH_sensible()));
		return consistent_scales;
	}
	
}
