package Model.Music;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Music.Harmonization.DynamicPitchRanking;
import Model.Music.Harmonization.HarmonizationVoice;
import Model.Music.Harmonization.PitchRanking;
import Model.Music.Harmonization.TonnetzRanking;
import Model.Music.Tonnetze.ChordComplex;
import Model.Music.Tonnetze.HarmonizationTonnetz;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.TIChordComplex;
import Model.Music.Tonnetze.Z12FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z7FoldedGraphTonnetz;
import Path.SquareGridCoordPath;
import Path.SquareGridCoordList;
import Utils.BarDiagram;
import Utils.DotDiagram;
import Utils.IntegerSet;
import Utils.LineDiagram;
import Utils.MidiPlayer;
import Utils.PieDiagram;
import Utils.Table;
import Utils.TableSet;

public class PitchSetStream extends TreeMap<Long,PitchSetWithDuration>{
	
	private static final long serialVersionUID = 1L;
	protected String _name = "";
	protected ArrayList<Long> _keys;
	private long length_in_microseconds;
	//private TonnetzCoordPath nr_path;

	public PitchSetStream(){
		super();
	}
	
	public PitchSetStream(String name){
		super();
		set_name(name);
	}
	
	public PitchSetStream(TreeMap<Long,Integer> voice){
		for (long key : voice.keySet()){
			PitchSetWithDuration mc = new PitchSetWithDuration();
			mc.add(voice.get(key));
			put(key,mc);
		}
	}
	
	public PitchSetStream(PitchSetStream col_stream){
		super();
		for (long key : col_stream.keySet()){
			put(key,new PitchSetWithDuration(col_stream.get(key)));
		}
	}
	
	public static PitchSetStream get_random_stream(int size, int chord_size){
		long chord_duration = 100;
		PitchSetStream stream = new PitchSetStream("random_"+size+"_"+chord_size+"-chords");
		PitchClassSetSeq seq = PitchClassSetSeq.random_STChordSeq(size, chord_size, 12);
		for (int i = 0;i<seq.size();i++){
			stream.put(i*chord_duration, new PitchSetWithDuration(seq.get(i).to_default_pitch_set(),chord_duration));
		}
		return stream;
	}
	
	public static PitchSetStream get_random_stream(int size, int min_chord_size, int max_chord_size){
		long chord_duration = 100;
		PitchSetStream stream = new PitchSetStream("random_"+size+"_n-chords");
		PitchClassSetSeq seq = PitchClassSetSeq.random_STChordSeq(size, min_chord_size, max_chord_size, 12);
		for (int i = 0;i<seq.size();i++){
			stream.put(i*chord_duration, new PitchSetWithDuration(seq.get(i).to_default_pitch_set(),chord_duration));
		}
		return stream;
	}

	public TreeMap<Long,ArrayList<Integer>> to_pitch_list_stream(){
		TreeMap<Long,ArrayList<Integer>> pitch_list_stream = new TreeMap<Long,ArrayList<Integer>>();
		for (Long key : keySet()){
			pitch_list_stream.put(key, get(key).to_array_list());
		}
		return pitch_list_stream;
	}
	
	// pitch_list_stream est �quivalent au picthSetStream sauf que les pc sont ordonn�es.
	public SquareGridCoordPath compute_tonnetz_coord_path(PlanarUnfoldedTonnetz t,TreeMap<Long,ArrayList<Integer>> pitch_list_stream_in_t){
		
		
		SquareGridCoordPath tonnetz_coord_path = new SquareGridCoordPath();
		Long current_key = firstKey();
		PitchClassSet current_STChord = get(current_key).to_PitchClassSet();
		SquareGridCoordList current_coords;
		TableSet coords_set = new TableSet();
		
		if (current_STChord.isEmpty()){
			current_coords = new SquareGridCoordList();
			tonnetz_coord_path.put(current_key, current_coords);
			pitch_list_stream_in_t.put(current_key, current_STChord.to_array_list());
			current_key = higherKey(current_key);
			current_STChord = get(current_key).to_PitchClassSet();
		}
		current_coords = t.first_chord_XYcoords(current_STChord);
		tonnetz_coord_path.put(current_key, current_coords);
		//if(current_coords != null){
			pitch_list_stream_in_t.put(current_key, t.get_pc_list_from_coords(current_coords));
		//}
		//pitch_list_stream_in_t.put(current_key, current_STChord.to_array_list());
		coords_set.addAll(current_coords);
		
		while(current_key < lastKey()){
			current_key = higherKey(current_key);
			if (current_STChord.equals(get(current_key).to_PitchClassSet())){
				tonnetz_coord_path.put(current_key, current_coords.copy());
				pitch_list_stream_in_t.put(current_key, t.get_pc_list_from_coords(current_coords));
			} else {
				current_STChord = get(current_key).to_PitchClassSet();
				current_coords = t.n_chord_XYcoords(current_STChord, tonnetz_coord_path, coords_set);
				tonnetz_coord_path.put(current_key,current_coords);
				pitch_list_stream_in_t.put(current_key, t.get_pc_list_from_coords(current_coords));
				coords_set.addAll(current_coords);
			}
		}
		return tonnetz_coord_path;
	}
	
	public SquareGridCoordPath compute_tonnetz_coord_path(PlanarUnfoldedTonnetz t){
		return compute_tonnetz_coord_path(t,new TreeMap<Long,ArrayList<Integer>>());
	}
	
	
//	public int get_new_pitch(int old_pitch, long key, TonnetzCoordPath new_path, PlanarUnfoldedTonnetz t){
//		assert _keys.contains(key) : "key "+key+" not in the collection !!";
//		assert new_path.keySet().contains(key) : "key not in the path !!";
//		assert nr_path.keySet().contains(key) : "key not in the nr_path !!";
//		ArrayList<int[]> old_coord = nr_path.get(key);
////		System.out.println("new_path : "+Table.toString(new_path));
////		System.out.println("nr_path : "+Table.toString(nr_path));
////		System.out.println("KEY : "+key);
//		for (int i=0;i<old_coord.size();i++){
////			System.out.println("old_pitch : "+old_pitch+" old_path_pitch : "+t.xy_coords_to_pitch(old_coord.get(i)));
//			if (t.xy_coords_to_pitch(old_coord.get(i))==(old_pitch%12)){
////				System.out.println("old_pitch : "+old_pitch+" new pitch returned : "+(60+t.xy_coords_to_pitch(new_path.get(key).get(i))));
//				return 60+t.xy_coords_to_pitch(new_path.get(key).get(i));
//			}
//		}
//		assert false : "key "+key+" not supposed to be here (pitch not found)";
//		return old_pitch;
//	}
	
//	public TonnetzCoordPath get_nr_path() {
//		return nr_path;
//	}
	
	public long get_S_sized_pc_total_duration(int s){
		long duration = 0;
		for (long key : keySet()){
			if (get(key).to_PitchClassSet().size()>=s){
				duration=duration+get(key).get_duration();
			}
		}
		return duration;
	}

	public long get_duration(){
		return lastKey()+lastEntry().getValue().get_duration();
	}
	
	protected void build_keys(){
		_keys = new ArrayList<Long>(keySet());
		Collections.sort(_keys);
	}
		
	public PitchSetWithDuration get_col_with_pos(int pos){
		return get(_keys.get(pos));
	}
	
	// Si le tick demand� tombe entre 2 collections, c'est la derni�re collection (la plus proche mais dans le pass�) qui est retourn�e
	
	public PitchSetWithDuration get_last_col_with_tick(long tick){
//		if (containsKey(tick)){
//			return get(tick);
//		} else {
//			return headMap(tick).get(headMap(tick).lastKey());
//		}
		return headMap(tick,true).get(headMap(tick,true).lastKey());
	}
	
	public long get_last_tick_with_tick(long tick){
		return headMap(tick,true).lastKey();
	}
		
	public PitchSetStream to_STColStream(){
		PitchSetStream s  = new PitchSetStream();
		for (Long l : keySet()){
			s.put(l, get(l).to_STMusicCollection(12));
		}
		return s;
	}
	
	public static PitchSetStream mix_colStream_list(ArrayList<PitchSetStream> list){
		if (list.size()==0) return new PitchSetStream();
		if (list.size()==1) return list.get(0);
		PitchSetStream mixed_colStream = new PitchSetStream();
		mixed_colStream = list.get(0);
		for (int n = 1;n<list.size();n++){
			mixed_colStream = mix_2colStream(mixed_colStream,list.get(n));
		}
		return mixed_colStream;
	}
	
	
	public static PitchSetStream mix_2colStream(PitchSetStream s1, PitchSetStream s2){
		s1.struct_verification();
		s2.struct_verification();
//		System.out.println("voici s1 : "+s1);
//		System.out.println("voici s2 : "+s2);
		PitchSetStream mixed_colStream = new PitchSetStream(s1);
		for (Map.Entry<Long, PitchSetWithDuration> e : s2.entrySet()){
			if (!e.getValue().isEmpty()){
				mixed_colStream.insert_col(e);				
			}
		}
		return mixed_colStream;
	}
	
	public void insert_col(Map.Entry<Long, PitchSetWithDuration> entry){
		
		Long tick = entry.getKey();
		PitchSetWithDuration col = entry.getValue();
		
		// 1er cas : La collection d�bute et finit � des dates d�j� fronti�res dans le Stream.
		// A la fin du IF on sort de la fonction
		if (containsKey(tick) && containsKey(tick+col.get_duration())){
			for (Map.Entry<Long, PitchSetWithDuration> e : subMap(tick,tick+col.get_duration()).entrySet()){
				put(e.getKey(), e.getValue().union(col));
			}
		} else {
			// 2nd cas : La collection ne d�bute pas � une date fronti�re dans le Stream
			if (!containsKey(tick)){
				long end = lastKey()+get(lastKey()).get_duration();
				// Cas 2A : La collection d�bute apr�s la fin du stream. Il faut rallonger le stream. En principe �a n'est pas possible, toutes les pistes sont de m�me longueur. En fait si car quand �a finit par du vide ce n'est pas la peine de cr�er un long MusicCollection vide.
				if (tick>end){
					PitchSetWithDuration new_c = new PitchSetWithDuration(tick+col.get_duration()-end);
					put(end,new_c);
					insert_col(entry);
				}
				// Cas 2B : La collection d�bute avant la fin du stream
				else{
					// Cas 2B1 :  La collection d�bute avant le d�but du stream
					if (tick<firstKey()){
						PitchSetWithDuration new_c = new PitchSetWithDuration(firstKey()-tick);
						put(tick,new_c);
						insert_col(entry);
					// Cas 2B2 : La collection d�bute entre le d�but et la fin du stream
					}else {
						long prev_key = firstKey();
						for (Map.Entry<Long, PitchSetWithDuration> e : this.entrySet()){
							if ((e.getKey()<tick)&&(prev_key<e.getKey())){
								prev_key=e.getKey();
							}
						}
						TreeMap<Long, PitchSetWithDuration> tail = new TreeMap<Long, PitchSetWithDuration>(tailMap(prev_key));
						long next_key;
						if (tail.size()==1){
							next_key = end;
						}else {
							tail.remove(tail.firstKey());
							next_key = tail.firstKey();
						}
											
						// Cas 2B1 : La collection est � l'int�rieur des fronti�res d'une unique collection du stream
						if (next_key>tick+col.get_duration()){
							
							if (containsKey(tick+col.get_duration())) {
								System.err.println("Erreur dans l'algo d'insertion");
								System.exit(1);
							}
							PitchSetWithDuration new_c = new PitchSetWithDuration(get(prev_key));
							new_c.set_duration(next_key-(tick+col.get_duration()));
							put(tick+col.get_duration(),new_c);
							new_c = new PitchSetWithDuration(new_c);
							new_c.set_duration(tick+col.get_duration()-prev_key);
							put(prev_key,new_c);
							insert_col(entry);
							
						// CAS 2B2 : La collection ne d�bute pas � une date fronti�re dans le Stream mais elle arrive au moins � la fronti�re suivante
						// On raffine le stream de mani�re � se rediriger vers le cas 1
						} else {
							PitchSetWithDuration new_c = new PitchSetWithDuration(get(prev_key));
							new_c.set_duration(tick-prev_key);
							put(prev_key,new_c);
							new_c = new PitchSetWithDuration(new_c);
							new_c.set_duration(next_key-tick);
							put(tick,new_c);
							insert_col(entry);
						}

					}

				}
			}
			// 3eme cas : La collection d�bute � une date fronti�re mais ne finit pas � une date fronti�re
			// A la fin du if on re-rentre dans la fonction de mani�re r�cursive
			else {
				// Cas 3A : la collection finit avant la fin du stream
				long end = lastKey()+get(lastKey()).get_duration();
				if ((tick+col.get_duration())<=end){
					long prev_key = firstKey();
					// prev_key : date dans le stream la plus proche "avant" la fin de la collection
					for (Map.Entry<Long, PitchSetWithDuration> e : this.entrySet()){
						if (e.getKey()<tick+col.get_duration() && (prev_key<e.getKey())){
							prev_key=e.getKey();
						}
					}
					long next_key; // next_key : date dans le stream la plus proche "apr�s" la fin de la collection
					// Si la collection s'ach�ve au cours de la derni�re collection du stream
					if (prev_key==lastKey()){
						next_key = end;
					} else {
						TreeMap<Long, PitchSetWithDuration> tail = new TreeMap<Long, PitchSetWithDuration>(tailMap(prev_key));
						tail.remove(tail.firstKey());
						next_key = tail.firstKey();
					}
					PitchSetWithDuration new_c = new PitchSetWithDuration(get(prev_key));
					new_c.set_duration(next_key-(tick+col.get_duration()));
					put(tick+col.get_duration(),new_c);
					new_c = new PitchSetWithDuration(new_c);
					new_c.set_duration(tick+col.get_duration()-prev_key);
					put(prev_key,new_c);
					insert_col(entry);

				}else {
				// CAS 3B : la collection finit apr�s la fin du stream (Il faut rallonger le stream)
					PitchSetWithDuration new_c = new PitchSetWithDuration((tick+col.get_duration())-end);
					put(end,new_c);
					insert_col(entry);
				}
			}
		}
	}
	
	public void struct_verification(){
//		System.out.println("VERIFICATION : "+this);
		if (!this.isEmpty()){
			Set<Long> key_set = keySet();
			long last_key = 0;
			long last_duration = 0;
			for (Long key : key_set){
				//System.out.println("key : "+k+" last_duration : "+last_duration);
				assert key==last_key+last_duration || key==firstKey() : "ColStream Structure Verification Error on key "+key;
//				if (k!=last_key+last_duration && k!=firstKey()) {
//					System.err.println("ColStream Structure Verification Error on key "+k);
//				}
				last_key=key;
				last_duration=get(key).get_duration();
			}
		}
	}
	
//	public void defragmentation(){
//		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
//		Collections.sort(key_list);
//		boolean bool = true;
//		int n= 0;
//		while (n<key_list.size()-2 && bool){
////			System.out.println(key_list.get(n)+"   "+key_list.get(n+1)+"   "+get(key_list.get(n)));
//			if (!get(key_list.get(n)).isEmpty()){
//				
//				if (get(key_list.get(n)).equals(get(key_list.get(n+1)))){
//					PitchSetWithDuration new_c = new PitchSetWithDuration(get(key_list.get(n)));
//					new_c.set_duration(get(key_list.get(n)).get_duration()+get(key_list.get(n+1)).get_duration());
//					remove(key_list.get(n+1));
//					put(key_list.get(n),new_c);
//					//System.out.println("defragmentation : "+key_list.get(n));
//					bool=false;
//				}
//
//			} else if (get(key_list.get(n+1)).isEmpty()){
//				put(key_list.get(n),new PitchSetWithDuration(get(key_list.get(n)).get_duration()+get(key_list.get(n+1)).get_duration()));
//				remove(key_list.get(n+1));
//
//				bool=false;
//			}
//			n++;
//		}
//		if (!bool) {
//			System.out.println("defragmentation : "+n);
//			defragmentation();
//		}
//		struct_verification();
//	}

	// returns TRUE if a defragmentation has been done, FALSE otherwise
	public boolean try_defragmentation(){
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		Collections.sort(key_list);
		boolean bool = true;
		int n= 0;
		while (n<key_list.size()-2 && bool){
//			System.out.println(key_list.get(n)+"   "+key_list.get(n+1)+"   "+get(key_list.get(n)));
			if (!get(key_list.get(n)).isEmpty()){
				
				if (get(key_list.get(n)).equals(get(key_list.get(n+1)))){
					
					PitchSetWithDuration new_c = new PitchSetWithDuration(get(key_list.get(n)));
					new_c.set_duration(get(key_list.get(n)).get_duration()+get(key_list.get(n+1)).get_duration());
					remove(key_list.get(n+1));
					put(key_list.get(n),new_c);
					//System.out.println("defragmentation : "+key_list.get(n));
					return true;
				}

			} else {
				
				if (get(key_list.get(n+1)).isEmpty()){
					put(key_list.get(n),new PitchSetWithDuration(get(key_list.get(n)).get_duration()+get(key_list.get(n+1)).get_duration()));
					remove(key_list.get(n+1));
					return true;
				}
			}
			n++;
		}
		struct_verification();
		return false;
	}

	
	// Supress empty collections at the end of the stream
	public void tail_adjustment(){
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		Collections.sort(key_list);
		PitchSetWithDuration pitch_set = lastEntry().getValue();
		while(pitch_set.isEmpty() || pitch_set.get_duration()==0){
			remove(lastEntry().getKey());
			pitch_set = lastEntry().getValue();
		}
	}
	
	// Reduce an empty tail to 1 tick 
	public void tail_reduction(){
		
		if (lastEntry()!=null && lastEntry().getValue().isEmpty()){
			lastEntry().getValue().set_duration(1);
		}
	}
	
	// Supress empty collections at the beginning of the stream (Warning : Other collections onsets are all shifted).
	// This make a desynchronisation with the original midi file so don't use with midi player !
	public void head_adjustment(){
		assert firstKey() == 0 : "First Key is "+firstKey()+" instead of 0";
		if (get((long) 0).isEmpty()){
			long blank_duration = get((long) 0).get_duration();
			remove(firstKey());
			assert blank_duration == firstKey() : "First Key is "+firstKey()+" but should be "+blank_duration;
			ArrayList<Long> key_list = new ArrayList<Long>(keySet());
			Collections.sort(key_list);
			for (int n = 0;n<key_list.size();n++){
				long current_key = key_list.get(n);
				PitchSetWithDuration collection = get(current_key);
				remove(current_key);
				put(current_key-blank_duration,collection);
			}
			head_adjustment();
		}
		struct_verification();
	}
	
	public void adjustment(){
		tail_adjustment();
		head_adjustment();
	}
	
	// Add en empty collection in the end of the stream
	public void add_end_blank(long blank_duration){
		put(get_duration(),new PitchSetWithDuration(blank_duration));
	}
	
	// Construction d'une PosColStream filtrant uniquement les collections de dur�e sup�rieure � r (par exemple r = 10 miditicks)
	// Les dur�es des collections conserv�es sont agrandies (on ajoute les dur�es des r�sidus) pour pr�server la dur�e totale du fichier	

	public PitchSetStream rounded_ColStream(int r){
		
		List<Long> rounded_onset_list = get_rounded_onset_list(r);

		PitchSetStream stream = new PitchSetStream();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		
		long first_key = firstKey();
		stream.put(first_key, get(first_key));
		
		for (int i=1;i<key_list.size();i++){
			long key = key_list.get(i);
			PitchSetWithDuration collection = get(key);
			if (collection.get_duration() < r){ // si la collection est très courte
				PitchSetWithDuration last_col = stream.lowerEntry(key).getValue();
				//collection.
//				if (!last_col.containsAll(collection) && rounded_onset_list.contains(key)){ // si elle fait apparaitre au moins un nouveau pitch (ie elle est déclenchée par au moins un offset) ET que key appartient à la liste d'onsets approximatifs
//					stream.put(key, collection);
//				} else { // si elle ne fait apparaitre aucun nouveau pitch (ie elle est uniquement déclenchée par un (des) offset) ou si il y a un onset mais pas dans la liste des onsets approximatifs
					stream.lowerEntry(key).getValue().grow_duration(collection.get_duration());
//				}
				
			} else {
//				if (key-stream.lastKey()<r){
//					stream.put(stream.lastKey(), collection);
//				}
//				long prev_key = 
//				if ()
				stream.put(key, collection);
			}
		}		
		return stream;

	}
	
	public List<Long> get_onset_list(){
		List<Long> onset_list = new ArrayList<Long>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (int i=0;i<key_list.size();i++){
			long key = key_list.get(i);
			PitchSetWithDuration collection = get(key);
			if (key == firstKey()){
				if (!collection.isEmpty()) onset_list.add(key);
			} else {
				PitchSetWithDuration prev_collection = lowerEntry(key).getValue();
				if (!prev_collection.containsAll(collection)) onset_list.add(key);
			}
		}
		return onset_list;
	}
	
	public List<Long> get_rounded_onset_list(int r){
		List<Long> onset_list = get_onset_list();
		List<Long> rounded_onset_list = new ArrayList<Long>();
		
		rounded_onset_list.add(onset_list.get(0));
		for (int i=1;i<onset_list.size();i++){
			long last_round_onset = rounded_onset_list.get(rounded_onset_list.size()-1);
			long current_onset = onset_list.get(i); 
			if (current_onset-last_round_onset>=r) rounded_onset_list.add(current_onset);
		}
		return rounded_onset_list;
	}
	
	public PitchClassSetSeq to_pc_set_list(){
		PitchClassSetSeq pc_set_list = new PitchClassSetSeq();
		for (long key : keySet()){
			if (get(key).get_duration()>3){
				pc_set_list.add(get(key).to_PitchClassSet(12));				
			}
		}
		return pc_set_list;
	}
	

	public List<PitchClassSet> to_pcs_list(){
		List<PitchClassSet> pcs_list = new ArrayList<PitchClassSet>();
		for (long key : this.keySet()){
			pcs_list.add(this.get(key).to_PitchClassSet());
		}
		return pcs_list;
	}
	
	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}
	
	public void play(MidiPlayer p){
		
	}

	/* ------ Vertical Compliance ------ */

	public static void compliance_display(ArrayList<PitchSetStream> stream_list, ArrayList<TIChordComplex> complex_list, int compactness_dimension){
		
		ArrayList<float[]> average_table_list = new ArrayList<float[]>();
		for (PitchSetStream stream : stream_list) {
			average_table_list.add(stream.z12FoldedTonnetz_compliance_average_table(stream.Z12FoldedTonnetz_compliance_table(stream.get_name(),compactness_dimension,complex_list,false), compactness_dimension));
		}
		
		DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();
		
		
		for (int i = 0;i<average_table_list.get(0).length;i++){
			for (int j=0;j<average_table_list.size();j++){
			//for (float[] average_table : average_table_list){
				//if (average_table_list.get(j)[i]!=0){	// pour ne pas tracer les espaces dont la compliance = 0
				//dataset.setValue(complex_names.get(i), average_table[i]);
					h_dataset.setValue(average_table_list.get(j)[i], stream_list.get(j).get_name(),complex_list.get(i).toString());
				
				
//				h_dataset.setValue(average_table[i], name, complex_list.get(i).toString());
//				h_dataset.setValue(complex_list.get(i).get_average_compactness(12, compactness_dimension), "random chords", complex_list.get(i).toString());
				//}				
			}
		}
		BarDiagram bar_diagram = new BarDiagram(h_dataset,"", true);
		
		String compactness_type;
		if (compactness_dimension == -1) compactness_type = "abs"+"-compactness"; else compactness_type = compactness_dimension+"-compactness";
		bar_diagram.display_bar_diagram(compactness_type);
		
	}
	
	public void compliance_averages_compare_random_display(ArrayList<float[]> compliance_table, ArrayList<TIChordComplex> complex_list, String name, int compactness_dimension){
		float average_table[] = z12FoldedTonnetz_compliance_average_table(compliance_table, compactness_dimension);
		DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();

		for (int i = 0;i<average_table.length;i++){
			//if (average_table[i]!=0){	// pour ne pas tracer les espaces dont la compliance = 0
				//dataset.setValue(complex_names.get(i), average_table[i]);
				h_dataset.setValue(average_table[i], name, complex_list.get(i).toString());
				h_dataset.setValue(complex_list.get(i).get_average_compactness(12, compactness_dimension), "random chords", complex_list.get(i).toString());
			//}
		}
		BarDiagram bar_diagram = new BarDiagram(h_dataset,name, true);
		
		String compactness_type;
		if (compactness_dimension == -1) compactness_type = "abs"+"-compactness"; else compactness_type = compactness_dimension+"-compactness";
		bar_diagram.display_bar_diagram(compactness_type);

	}

	
	public void Z12FoldedTonnetz_compliance_display(String name,int compactness_dimension, ArrayList<TIChordComplex> complex_list, boolean compare_to_random){
		ArrayList<float[]> compliance_list = Z12FoldedTonnetz_compliance_table(name,compactness_dimension, complex_list, false);
		compliance_display(name,compliance_list,complex_list,compactness_dimension);
	}
	
	public void compliance_display(String name, ArrayList<float[]> compliance_list, ArrayList<TIChordComplex> complex_list, int compactness_dimension){
		
		XYSeries[] series_table = new XYSeries[complex_list.size()];
		for (int i =0;i<complex_list.size();i++) series_table[i] = new XYSeries(complex_list.get(i).toString());
		int j;
		for (int i=0;i<compliance_list.size();i++){
			float[] f = compliance_list.get(i);
			for (j=0;j<complex_list.size();j++){
				//series_table[j].add(f[1],f[3+complex_list.indexOf(complex_list.get(j))]);
				series_table[j].add(f[1],f[3+j]);
				//series_table[j].add(f[1]+f[2],f[3+complex_list.indexOf(complex_list.get(j))]);
				series_table[j].add(f[1]+f[2],f[3+j]);
			}
		}
		XYSeriesCollection dataset = new XYSeriesCollection();;
		for (XYSeries s : series_table) dataset.addSeries(s);
		LineDiagram diagram = new LineDiagram(dataset, (compactness_dimension+"-compactness : "+name));
		diagram.display_line_diagram("time","Complex compliance",false);

		//compliance_averages_display(compliance_list, TIChordComplex.get_complex_string_list(complex_list), name, compactness_dimension);
		compliance_averages_compare_random_display(compliance_list, complex_list, name, compactness_dimension);

	}


	// Compliance Table : Pour chaque collection du stream on d�finit le tableau || Index | Date_d�but | dur�e | c_T[1] | ... | c_T[1,2,3,4,5,6] ||	
	public ArrayList<float[]> Z12FoldedTonnetz_compliance_table(String name,int compactness_dimension, ArrayList<TIChordComplex> complex_list, boolean absolute_compliance){
		ArrayList<float[]> compliance_table = new ArrayList<float[]>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		
		for (int i=0;i<key_list.size();i++){
			// Pour chaque collection du stream on d�finit le tableau || Index | Date_d�but | dur�e | c_T[1,1,10] | ... | c_T[4,4,4] ||
			float[] collection_compliance = new float[complex_list.size()+3];
			collection_compliance[0] = i;											// Index
			collection_compliance[1] = key_list.get(i);								// Date_d�but
			collection_compliance[2] = get(key_list.get(i)).get_duration();			// dur�e
			for (int j=0;j<complex_list.size();j++){
				if (absolute_compliance)
					collection_compliance[j+3]=complex_list.get(j).get_absolute_compliance(get(key_list.get(i)));
				else {
					collection_compliance[j+3]=complex_list.get(j).get_n_compliance(get(key_list.get(i)),compactness_dimension);
				}
			}
			compliance_table.add(collection_compliance);
		}
		return compliance_table;
	}
	
	public void compliance_averages_display(ArrayList<float[]> compliance_table, ArrayList<String> complex_names, String name, int compactness_dimension){
		float average_table[] = z12FoldedTonnetz_compliance_average_table(compliance_table, compactness_dimension);
		DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();

		for (int i = 0;i<average_table.length;i++){
			//if (average_table[i]!=0){	// pour ne pas tracer les espaces dont la compliance = 0
				//dataset.setValue(complex_names.get(i), average_table[i]);
				h_dataset.setValue(average_table[i], "piece", complex_names.get(i));
			//}
		}
		BarDiagram bar_diagram = new BarDiagram(h_dataset, name, false);
		
		String compactness_type;
		if (compactness_dimension == -1) compactness_type = "abs"+"-compactness"; else compactness_type = compactness_dimension+"-compactness";
		bar_diagram.display_bar_diagram(compactness_type);
	}
	
	
	
	// Calcul de la compliance moyenne. Le fait de moyenner la somme en la divisant par la dur�e du morceau n'a de sens que si l'on veut comparer avec la compliance d'un autre morceau.
	// Si il s'agit uniquement de comparer la compliance dans les diff�rents espaces pour un morceau donn� �a ne sert � rien de diviser et on peut utiliser la fonction
	// Z12HexaTonnetz_compliance_sum_table (not yet implemented)


	public float[] z12FoldedTonnetz_compliance_average_table(ArrayList<float[]> compliance_table, int compactness_degree) {
		
		// N_AVERAGING = TRUE : on normalise seulement par rapport aux moments correspondant � au moins N pitch class simultan�es.
		boolean N_AVERAGING = true;
		
		int complex_count = compliance_table.get(0).length-3;
		
		float average_table[] = new float[complex_count];
				
		for (float[] f : compliance_table){
			//for (int i = 0;i<compliance_table.get(0).length-3;i++){
			for (int i = 0;i<complex_count;i++){
				// La moyenne prend compte des compliances pond�r�es par la dur�e de la collection concern�e
				average_table[i]=average_table[i]+(f[i+3]*f[2]);
				// La moyenne prend compte des compliances NON pond�r�es par la dur�e de la collection concern�e
				//average_table[i]=average_table[i]+(f[i+3]);
			}
		}

//		for (int i = 0;i<average_table.length;i++) {
//			average_table[i]=average_table[i]/get_duration();
//		}
		
		long duration_to_divide;

			if (N_AVERAGING){
				duration_to_divide = get_S_sized_pc_total_duration(compactness_degree+1);
				for (int i = 0;i<average_table.length;i++) {
					average_table[i]=average_table[i]/duration_to_divide;
				}
			} else {
				duration_to_divide = get_duration();
				for (int i = 0;i<average_table.length;i++) {
					average_table[i]=average_table[i]/duration_to_divide;
				}

		
		}

		return average_table;
	}

	
			/*   III 30 Tonnetze - Absolute compliance   */

	
	public void Z12FoldedTonnetz_abs_compliance_display(String name, ArrayList<TIChordComplex> tonnetz_list){
		ArrayList<float[]> compliance_list = Z12FoldedTonnetz_compliance_table(name,0, tonnetz_list, true);
		System.out.println("compliance_list : "+Table.toString(compliance_list));
		absolute_compliance_display(name,compliance_list,tonnetz_list);		
	}
	
	
	
	
	
	/* ------ Display with JFreeChart ------ */
		
	

	public void absolute_compliance_display(String name, ArrayList<float[]> compliance_list, ArrayList<TIChordComplex> tonnetz_list){
		XYSeries[] series_table = new XYSeries[tonnetz_list.size()];
		for (int i =0;i<tonnetz_list.size();i++) series_table[i] = new XYSeries(tonnetz_list.get(i).toString());
		int j;
		for (int i=0;i<compliance_list.size();i++){
			float[] f = compliance_list.get(i);
			for (j=0;j<tonnetz_list.size();j++){
				series_table[j].add(f[1],f[3+tonnetz_list.indexOf(tonnetz_list.get(j))]);
				series_table[j].add(f[1]+f[2],f[3+tonnetz_list.indexOf(tonnetz_list.get(j))]);
			}
		}
		
		
		// On cr�� une nouvelle liste avec seulement les tonnetz utiles pour a�rer le digrame
		ArrayList<XYSeries> new_list = new ArrayList<XYSeries>();	
		for (XYSeries xyserie : series_table) {
			boolean useless_tonnetz = true;
			double[][] array = xyserie.toArray();
			for (int i=0;i<array[0].length;i++){
				if (array[1][i] != 0) useless_tonnetz = false;
			}
			if (!useless_tonnetz)new_list.add(xyserie);
				
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();;
		for (XYSeries s : new_list) dataset.addSeries(s);				
		LineDiagram diagram = new LineDiagram(dataset, "Complex compliance : "+name);
		diagram.display_area_diagram();

		
		compliance_averages_display(compliance_list, TIChordComplex.get_complex_string_list(tonnetz_list), name,-1);		

	}
	
	

	
	
	
	/*---------- OLD : VERTICAL COMPLIANCE ----------*/

	
	/*  I Tonnetze h�xagonaux  */
	
	
//public void Z12HexaTonnetz_compliance_display(String name, int compactness_dimension) {
//ArrayList<TonnetzChordComplex> complex_list = TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList(3);
//ArrayList<float[]> compliance_list = Z12FoldedTonnetz_compliance_table(compactness_dimension,complex_list,false);
//
//
//XYSeries[] series_table = new XYSeries[complex_list.size()];
//for (int i =0;i<complex_list.size();i++) series_table[i] = new XYSeries(complex_list.get(i).toString());
//int j;
//for (int i=0;i<compliance_list.size();i++){
//	float[] f = compliance_list.get(i);
//	for (j=0;j<complex_list.size();j++){
//		series_table[j].add(f[1],f[3+TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList_index(complex_list.get(j),3)]);
//		series_table[j].add(f[1]+f[2],f[3+TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList_index(complex_list.get(j),3)]);
//		series_table[j].add(f[1],f[3+j]);
//		series_table[j].add(f[1]+f[2],f[3+j]);
//	}
//}
//XYSeriesCollection dataset = new XYSeriesCollection();
//for (XYSeries s : series_table) dataset.addSeries(s);
//LineDiagram diagram = new LineDiagram(dataset, "Tonnetz compliance : "+name);
//diagram.display_line_diagram("time","tonnetz compliance",false);
//// TODO : oon dessine encore la moyenne de l'ancienne compliance !!
//
////z12HexaTonnetz_compliance_averages_display(compliance_list, complex_list, name, compactness_dimension);
//compliance_averages_display(compliance_list, TonnetzChordComplex.get_complex_string_list(complex_list), name, compactness_dimension);
//
//}
//


	
//	public float[] z12HexaTonnetz_compliance_average_table(ArrayList<float[]> compliance_table) {
//	
//	// N_AVERAGING = TRUE : on normalise seulement par rapport aux moments correspondant � au moins N pitch class simultan�es.
//	boolean N_AVERAGING = true;
//
//	int complex_count = compliance_table.get(0).length-3;
//	
//	//float average_table[] = new float[12];
//	float average_table[] = new float[complex_count];
//	
//	
//	for (float[] f : compliance_table){
//		//for (int i = 0;i<12;i++){
//		for (int i = 0;i<complex_count;i++){
//			// La moyenne prend compte des compliances pond�r�es par la dur�e de la collection concern�e
//			average_table[i]=average_table[i]+(f[i+3]*f[2]);
//			// La moyenne prend compte des compliances NON pond�r�es par la dur�e de la collection concern�e
//			//average_table[i]=average_table[i]+(f[i+3]);
//		}
//	}
//
//	long duration_to_divide;
//	if (N_AVERAGING){
//		duration_to_divide = get_S_sized_pc_total_duration(3);
//		for (int i = 0;i<average_table.length;i++) {
//			average_table[i]=average_table[i]/duration_to_divide;
//		}
//	} else {
//		duration_to_divide = get_duration();
//		for (int i = 0;i<average_table.length;i++) {
//			average_table[i]=average_table[i]/duration_to_divide;
//		}
//	}
//	
//	return average_table;
//}

// Compliance Table : Pour chaque collection du stream on d�finit le tableau || Index | Date_d�but | dur�e | c_T[1,1,10] | ... | c_T[4,4,4] ||	
//public ArrayList<float[]> z12HexaTonnetz_compliance_table(int compactness_dimension, ArrayList<TonnetzChordComplex> complex_list, boolean absolute_compliance){
//	ArrayList<float[]> compliance_table = new ArrayList<float[]>();
//	ArrayList<Long> key_list = new ArrayList<Long>(keySet());
//	
//	for (int i=0;i<key_list.size();i++){
//		// Pour chaque collection du stream on d�finit le tableau || Index | Date_d�but | dur�e | c_T[1,1,10] | ... | c_T[4,4,4] ||
//		float[] collection_compliance = new float[complex_list.size()+3];
//		collection_compliance[0] = i;											// Index
//		collection_compliance[1] = key_list.get(i);								// Date_d�but
//		collection_compliance[2] = get(key_list.get(i)).get_duration();			// dur�e
//		//for (int j=0;j<TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList().size();j++){
//		//for (int j=0;j<TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList(3).size();j++){
//		for (int j=0;j<complex_list.size();j++){
//			//collection_compliance[j+3]=TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList().get(j).get_n_compliance(get(key_list.get(i)),compactness_dimension);
//			//collection_compliance[j+3]=TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList(3).get(j).get_n_compliance(get(key_list.get(i)),compactness_dimension);
//			collection_compliance[j+3]=complex_list.get(j).get_n_compliance(get(key_list.get(i)),compactness_dimension);
//		}
//		compliance_table.add(collection_compliance);
//	}
//	return compliance_table;
//}

//	public void z12HexaTonnetz_compliance_averages_display(ArrayList<float[]> compliance_table, ArrayList<TonnetzChordComplex> complex_list, String name, int compactness_dimension) {
//	//float average_table[] = z12HexaTonnetz_compliance_average_table(compliance_table);
//	float average_table[] = z12FoldedTonnetz_compliance_average_table(compliance_table,compactness_dimension);
//
////	System.out.println("Voici l' average table : "+Table.toString(average_table));
//	DefaultPieDataset dataset = new DefaultPieDataset();
//	DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();
//
//	for (int i = 0;i<average_table.length;i++){
////		dataset.setValue(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i).toString(), average_table[i]);
////		h_dataset.setValue(average_table[i], "", Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i).toString());
//		//dataset.setValue(TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList().get(i).toString(), average_table[i]);
//		dataset.setValue(TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList(3).get(i).toString(), average_table[i]);
//		//h_dataset.setValue(average_table[i], "", TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList().get(i).toString());
//		h_dataset.setValue(average_table[i], "", TonnetzChordComplex.getZ12Tonnetz_n_ChordComplexList(3).get(i).toString());
//	}
//	PieDiagram diagram = new PieDiagram(dataset, "Complex compliance averages : "+name);
//	diagram.display_pie_diagram();
//	//BarDiagram bar_diagram = new BarDiagram(h_dataset, _name+name);
//	BarDiagram bar_diagram = new BarDiagram(h_dataset, "File.mid");
//	
//	String compactness_type;
//	if (compactness_dimension == -1) compactness_type = "abs"+"-compactness"; else compactness_type = compactness_dimension+"-compactness";
//	bar_diagram.display_bar_diagram(compactness_type);
//	
//}

		
		/*   II All-sized Tonnetze   */


//public void Z12FoldedTonnetz_compliance_display(String name, int n, ArrayList<Z12FoldedChordGraphTonnetz> tonnetz_list){
//	ArrayList<float[]> compliance_list = Z12FoldedTonnetz_compliance_table(n, tonnetz_list, false);
//	compliance_display(name,compliance_list,tonnetz_list,n);
//}



	/*---------- HORIZONTAL COMPLIANCE ----------*/
	
	/*
	 * Enum�ration des intervales MI horizontaux entre une collection et celle qui pr�c�de
	 * M�thode "large" : on �limine tous les pc commun et on prend toutes les combinaisons parmis les pitchs restant
	 */
	public ArrayList<Integer> large_horizontal_intervals(long k){
		PitchClassSet c1 = lowerEntry(k).getValue().to_PitchClassSet(Constant.N);
		PitchClassSet c2 = get(k).to_PitchClassSet(Constant.N);
		System.out.println("c1:"+c1+" c2:"+c2	);
		ArrayList<Integer> list = new ArrayList<Integer>();
		// On �limine les pc communes
		for (int i:new PitchClassSet(c1)){
			if (c2.contains(i)){
				c1.remove(i);
				c2.remove(i);
			}
		}
		for (int i:c1){
			for (int j:c2) list.add(Interval.MI(i, j, 12));
		}
		System.out.println("on renvoit la liste :"+list);
		return list;
	}
	
	/*
	 * Enum�ration des intervales MI horizontaux entre une collection et celle qui pr�c�de
	 * M�thode "contrainte" : on �limine tous les pc commun. un pitch ne peut aller qu'� un unique endroit : la plus courte distance
	 */
	public ArrayList<Integer> constraint_horizontal_intervals(long k){
		PitchClassSet c1 = lowerEntry(k).getValue().to_PitchClassSet(Constant.N);
		PitchClassSet c2 = get(k).to_PitchClassSet(Constant.N);
		ArrayList<Integer> list = new ArrayList<Integer>();
		// On �limine les pc communes
		for (int i:new PitchClassSet(c1)){
			if (c2.contains(i)){
				c1.remove(i);
				c2.remove(i);
			}
		}
		
		int smaller=0;
		for (int i:c1){
			for (int j:c2) {
				int interval = Interval.MI(i, j, 12);
				if (smaller==0 || interval<smaller) smaller = interval;
			}
			list.add(smaller);
			smaller=0;
		}
		return list;
	}

	public ArrayList<float[]> z12HexaTonnetz_large_Hcompliance_table(boolean constraint){
		ArrayList<float[]> compliance_table = new ArrayList<float[]>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (int i=1;i<key_list.size();i++){
			// Pour chaque collection du stream on d�finit le tableau || Index | Date_d�but | dur�e | c_T[1,1,10] | ... | c_T[4,4,4] ||
			float[] collection_compliance = new float[15];
			collection_compliance[0] = i;											// Index
			collection_compliance[1] = key_list.get(i);								// Date_d�but
			collection_compliance[2] = get(key_list.get(i)).get_duration();			// dur�e
			ArrayList<Integer> horizontal_interval_list;
			if(constraint){
				horizontal_interval_list = constraint_horizontal_intervals(key_list.get(i));
			} else {
				horizontal_interval_list = large_horizontal_intervals(key_list.get(i));
			}
			for (int j=0;j<Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().size();j++){
				//collection_compliance[j+3]=get(key_list.get(i)).to_STChord(Constant.N).tonnetz_1_compliance(UnfoldedSimplicialTonnetz.getZ12HexaTonnetzList().get(j));
				//collection_compliance[j+3]=get(key_list.get(i)).to_STChord(Constant.N).tonnetz_large_Hcompliance(UnfoldedSimplicialTonnetz.getZ12HexaTonnetzList().get(j),get(key_list.get(i-1)).to_STChord(Constant.N));
				collection_compliance[j+3]=Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(j).Hcompliance(horizontal_interval_list);
				//System.out.println("tonnetz : "+UnfoldedSimplicialTonnetz.getZ12HexaTonnetzList().get(j));
			}
			compliance_table.add(collection_compliance);
		}
		return compliance_table;
	}

	
	public void Z12HexaTonnetz_Hcompliance_display(String name, boolean constraint){
		
		ArrayList<float[]> compliance_list = z12HexaTonnetz_large_Hcompliance_table(constraint);
		ArrayList<Z12PlanarUnfoldedTonnetz> tonnetz_list = Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList();
		
		XYSeries[] series_table = new XYSeries[tonnetz_list.size()];
		for (int i =0;i<tonnetz_list.size();i++) series_table[i] = new XYSeries(tonnetz_list.get(i).toString());
		int j;
		for (int i=0;i<compliance_list.size();i++){
			float[] f = compliance_list.get(i);
			for (j=0;j<tonnetz_list.size();j++){
				series_table[j].add(f[1],f[3+Z12PlanarUnfoldedTonnetz.getZ12HexaTonnetzIndex(tonnetz_list.get(j))]);
				series_table[j].add(f[1]+f[2],f[3+Z12PlanarUnfoldedTonnetz.getZ12HexaTonnetzIndex(tonnetz_list.get(j))]);
			}
		}
		XYSeriesCollection dataset = new XYSeriesCollection();;
		for (XYSeries s : series_table) dataset.addSeries(s);
		LineDiagram diagram = new LineDiagram(dataset, "Tonnetz compliance : "+name);
		diagram.display_line_diagram("time","tonnetz compliance",false);
		// TODO : oon dessine encore la moyenne de l'ancienne compliance !!
		//z12HexaTonnetz_compliance_averages_display(compliance_list, null, name,-2);
		compliance_averages_display(compliance_list, Z12PlanarUnfoldedTonnetz.get_complex_string_list(tonnetz_list), name, -2);

		
	}
	
	// HARMONIZATION

	public String print_voice_compliance(PitchSetStream target_voice){
		
		int voice_number = 3;
		
		HarmonizationTonnetz harmonization_tonnetz = get_harmonization_tonnetz();
		HarmonizationVoice target_harmo_voice = new HarmonizationVoice(target_voice,harmonization_tonnetz,this);
		
		float target_vertical_compliance = target_harmo_voice.get_final_vertical_compliance();
		float target_horizontal_compliance = target_harmo_voice.get_final_horizontal_compliance();
		float target_transversal_compliance = target_harmo_voice.get_final_transversal_compliance();
		return _name+" : target compliance : v = "+target_vertical_compliance+" h = "+target_horizontal_compliance+" t = "+target_transversal_compliance;

		
	}
	
//	public float st_similarity(ColStream st_target){
////		System.out.println("st_target : "+st_target);
////		System.out.println("gen_voice : "+this);
//		float size = 0;
//		float similarity = 0;
//		float tmp_similarity;
//		for (long key : keySet()){
//			size++;
//			if (st_target.containsKey(key)){
//				tmp_similarity = get(key).get_similarity(st_target.get(key));
//			}else {
//				tmp_similarity = get(key).get_similarity(st_target.floorEntry(key).getValue());
//			}
//			similarity = similarity +tmp_similarity;
//			//st_target.st_similarity(get(key));
//			//System.out.println("key : "+key+" similarity : "+tmp_similarity);
//		}
//		return (similarity/size);
//	}
	
	public ContinuePitchSpace get_choral_voice_space(int voice, PitchSetWithDuration pitch_set){
		
		//assert get(key).size()>=voice+1 : "not enough voices in "+get(key)+" to find the space of the voice "+voice;
		ArrayList<Integer> list = new ArrayList<Integer>(pitch_set);
		Collections.sort(list);
		return new ContinuePitchSpace(list.get(list.size()-voice)+1,list.get(list.size()-voice+1)-1);
	}
	
	public ContinuePitchSpace get_choral_voice_space(int voice, long key, boolean voices_can_meet){
				
		//assert get(key).size()>=voice+1 : "not enough voices in "+get(key)+" to find the space of the voice "+voice;
		ArrayList<Integer> list = new ArrayList<Integer>(get(key));
		Collections.sort(list);
		int inf = list.get(list.size()-voice);
		int sup;
		if (voice == 1){
			sup = inf+5;
		} else {
			sup = list.get(list.size()-voice+1);
		}

		if (voices_can_meet && ((sup-inf) < 5)){
			return new ContinuePitchSpace(inf,sup);
		} else {
			return new ContinuePitchSpace(inf+1,sup-1);
		}
		
	}

	public HarmonizationTonnetz get_tonal_harmonization_tonnetz(Scale scale){

		//FoldedChordTonnetz vertical_tonnetz;
		TIChordComplex vertical_tonnetz = null;
		TonnetzRanking horizontal_tonnetz_ranking = new TonnetzRanking();

		//vertical_tonnetz = Z7FoldedChordGraphTonnetz.get_triad_strip(scale);
		vertical_tonnetz = TIChordComplex.get_triad_strip(scale);
		horizontal_tonnetz_ranking.put((float)1, new Z7FoldedGraphTonnetz(scale,new IntegerSet(new int[]{0,1})));
		horizontal_tonnetz_ranking.put((float)0.8, new Z7FoldedGraphTonnetz(scale,new IntegerSet(new int[]{2,3})));
		return new HarmonizationTonnetz(vertical_tonnetz,horizontal_tonnetz_ranking);
	}

	public HarmonizationTonnetz get_harmonization_tonnetz(){
	
		//FoldedChordTonnetz vertical_tonnetz;
		TIChordComplex vertical_tonnetz;
		TonnetzRanking horizontal_tonnetz_ranking = new TonnetzRanking();

		
		//vertical_tonnetz = Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList().get(16);
		//vertical_tonnetz = TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList().get(10);
		vertical_tonnetz = TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3).get(10);
		
		System.out.println("vertical tonnetz : "+vertical_tonnetz);
//		horizontal_tonnetz_ranking.put((float) 1, new Z12FoldedGraphTonnetz(new int[]{0,1,2}));
//		horizontal_tonnetz_ranking.put((float) 0.6, new Z12FoldedGraphTonnetz(new int[]{3,4,5}));
		horizontal_tonnetz_ranking.put((float) 1, new Z12FoldedGraphTonnetz(new IntegerSet(new int[]{0,1,2})));
		horizontal_tonnetz_ranking.put((float) 0.6, new Z12FoldedGraphTonnetz(new IntegerSet(new int[]{3,4,5})));

		return new HarmonizationTonnetz(vertical_tonnetz,horizontal_tonnetz_ranking);
	}
	
	public void spatial_harmonization(PitchSetStream target_voice, int voice_number){
		
//		TonnetzRanking horizontal_tonnetz_ranking_test = new TonnetzRanking();
//		horizontal_tonnetz_ranking_test.put((float) 1, new Z12FoldedTonnetz(new int[]{1,2}));
//		horizontal_tonnetz_ranking_test.put((float) 0.8, new Z12FoldedTonnetz(new int[]{3,4,5}));
//		
//		System.out.println("beau tonnetz : "+Z7FoldedChordTonnetz.get_triad_strip(new Scale(0,"major")));
//		
//		HarmonizationTonnetz harmonization_tonnetz_test = new HarmonizationTonnetz(Z7FoldedChordTonnetz.get_triad_strip(new Scale(7,"major")),horizontal_tonnetz_ranking_test);
//		
//		ColStream stream_test = new ColStream();
////		PitchSetWithDuration set1 = new PitchSetWithDuration(new IntegerSet(new int[]{60,64}),1000);
////		PitchSetWithDuration set2 = new PitchSetWithDuration(new IntegerSet(new int[]{60,64,67,71}),1000);
////		PitchSetWithDuration set3 = new PitchSetWithDuration(new IntegerSet(new int[]{60,64,67,70}),1000);
//		PitchSetWithDuration set4 = new PitchSetWithDuration(new IntegerSet(new int[]{55,62,71}),1000);
//		
////		System.out.println(harmonization_tonnetz_test.get_vertical_compliance(set1));
////		System.out.println(harmonization_tonnetz_test.get_vertical_compliance(set2));
////		System.out.println(harmonization_tonnetz_test.get_vertical_compliance(set3));
////		System.out.println("haha : "+harmonization_tonnetz_test.get_vertical_compliance(set4));
//
//		PitchRanking pitch_ranking_test = set4.get_harmo_pitch_ranking(harmonization_tonnetz_test, get_choral_voice_space(voice_number,set4));
//		System.out.println("pitch ranking : "+pitch_ranking_test);
//		
//		
//		System.exit(0);
		
		boolean tonal = false;
		boolean first_chord_is_first_degree = false;
		boolean voice_can_meet_in_short_spaces = true;
		boolean only_best_vertical_coef = false;
		Scale scale = null;
		
		System.out.println("reduced stream : "+this);
		System.out.println("reduced ST stream : "+this.to_STColStream());
		System.out.println("ST target voice : "+target_voice.to_STColStream());
		System.out.println("target voice : "+target_voice);
		
//		FoldedChordTonnetz vertical_tonnetz;
//		TonnetzRanking horizontal_tonnetz_ranking = new TonnetzRanking();
//		
//		if(tonal){
//			scale = new Scale(0,"major");
//			vertical_tonnetz = Z7FoldedChordTonnetz.get_triad_strip(scale);
//			horizontal_tonnetz_ranking.put((float)1, new Z7FoldedTonnetz(scale,new IntegerSet(new int[]{0,1})));
//			horizontal_tonnetz_ranking.put((float)0.8, new Z7FoldedTonnetz(scale,new IntegerSet(new int[]{2,3})));
//		} else {
//			vertical_tonnetz = Z12FoldedChordTonnetz.getZ12FoldedChordTonnetzList().get(16);
//			System.out.println("vertical tonnetz : "+vertical_tonnetz);
//			horizontal_tonnetz_ranking.put((float) 1, new Z12FoldedTonnetz(new int[]{0,1,2}));
//			horizontal_tonnetz_ranking.put((float) 0.6, new Z12FoldedTonnetz(new int[]{3,4,5}));
//		}
		
		//TONAL
		//HarmonizationTonnetz harmonization_tonnetz = get_tonal_harmonization_tonnetz(scale);
		//ATONAL
		HarmonizationTonnetz harmonization_tonnetz = get_harmonization_tonnetz();

		HarmonizationVoice target_harmo_voice = new HarmonizationVoice(target_voice,harmonization_tonnetz,this);
		System.out.println("target harmo voice : "+target_harmo_voice);
		
		float target_vertical_compliance = target_harmo_voice.get_final_vertical_compliance();
		float target_horizontal_compliance = target_harmo_voice.get_final_horizontal_compliance();
		float target_transversal_compliance = target_harmo_voice.get_final_transversal_compliance();
		System.out.println("target compliance : v = "+target_vertical_compliance+" h = "+target_horizontal_compliance+" t = "+target_transversal_compliance);
		

		//PCCandidateStream all_generated_pc = new PCCandidateStream();
//		DynamicPitchClassSpace all_generated_pc = new DynamicPitchClassSpace();
		DynamicPitchRanking dynamic_pitch_ranking = new DynamicPitchRanking(get_duration());
//		MusicColSetTreeMap all_generated_collections = new MusicColSetTreeMap();
		for (long key : keySet()){
			if (get(key).get_duration()>5 && !get(key).isEmpty()){
				//PitchRanking pitch_ranking = get(key).get_harmo_pitch_ranking(vertical_Z12_tonnetz, vertical_Z7_tonnetz, get_choral_voice_space(voice_number,key));
				//PitchRanking pitch_ranking = get(key).get_harmo_pitch_ranking(harmonization_tonnetz, vertical_Z7_tonnetz, get_choral_voice_space(voice_number,key));
				PitchRanking pitch_ranking;
				if ((key == firstKey() && !firstEntry().getValue().isEmpty()) || (key == higherKey(firstKey()) && firstEntry().getValue().isEmpty()) && first_chord_is_first_degree){
					if (tonal && first_chord_is_first_degree){
						pitch_ranking = get(key).get_harmo_pitch_ranking(harmonization_tonnetz, get_choral_voice_space(voice_number,key,false),scale.get_degree(0));
					} else {
						pitch_ranking = get(key).get_harmo_pitch_ranking(harmonization_tonnetz, get_choral_voice_space(voice_number,key,false));
					}
					
				} else {
					if (voice_can_meet_in_short_spaces){
						pitch_ranking = get(key).get_harmo_pitch_ranking(harmonization_tonnetz, get_choral_voice_space(voice_number,key,true));
					} else {
						pitch_ranking = get(key).get_harmo_pitch_ranking(harmonization_tonnetz, get_choral_voice_space(voice_number,key,false));
					}	
				}
				if(only_best_vertical_coef){
					pitch_ranking.only_keep_the_best();
				} 
				dynamic_pitch_ranking.put(key, pitch_ranking);
			}
		}
		
		System.out.println("dynamic_pitch_ranking : "+dynamic_pitch_ranking);

		long begin = System.currentTimeMillis();
		ArrayList<HarmonizationVoice> voice_list = dynamic_pitch_ranking.get_voice_set(harmonization_tonnetz,this,target_harmo_voice);
		long end = System.currentTimeMillis();
		float time = ((float) (end-begin)) / 1000f;
		System.out.println("FUNCTION DURATION : "+time);
		draw_voice_table(voice_list,target_harmo_voice);
		
		//ArrayList<HarmonizationVoice> voice_list = new ArrayList<HarmonizationVoice>(voice_set);
		
		if (voice_list.size()<50 && voice_list.size()>0){
			for (int i=0;i<voice_list.size();i++){
				String name = "harmonization"+i;
				System.out.println(name+" cv = "+voice_list.get(i).get_final_vertical_compliance()+" ch = "+voice_list.get(i).get_final_horizontal_compliance()+" ct = "+voice_list.get(i).get_final_transversal_compliance());
				to_midi_file(voice_list.get(i).to_ColStream(),name);
			}
		}
				
//		System.out.println("1er voix : "+voice_list.get(0).to_ColStream());
//		System.out.println("test stream : "+test_stream.get((long)0));
//		System.out.println("test stream : "+test_stream.get((long)1000));
		
//		System.out.println("transversal comp : "+harmonization_tonnetz.get_compliance(test_stream.get((long)0), test_stream.get((long)1000)));
//		PitchSet dom = new PitchSet(new int[]{59,62,67,77});
//		PitchSet res = new PitchSet(new int[]{60,64,67,76});
//		PitchSet dom = new PitchSet(new int[]{60,64,67,72});
//		PitchSet res = new PitchSet(new int[]{62,65,69,72});

//		System.out.println("transversal comp : "+harmonization_tonnetz.get_compliance(dom,res));

//		for (HarmonizationVoice voice : voice_set){
//			ColStream reconstructed_stream = ColStream.mix_2colStream(this, voice.to_ColStream());
//			reconstructed_stream.tail_adjustment();
//			System.out.println("reconstructed stream : "+reconstructed_stream);
//			ArrayList<Float> new_hori_compli = new ArrayList<Float>();
//			ArrayList<Long> key_list = new ArrayList<Long>(reconstructed_stream.keySet());
//			for (int i = 0;i<key_list.size()-1;i++){
//				
//				new_hori_compli.add(harmonization_tonnetz.get_compliance(reconstructed_stream.get(key_list.get(i)),reconstructed_stream.get(key_list.get(i+1))));
//			}
//			System.out.println("new hori compli "+new_hori_compli.size()+" : "+new_hori_compli);
//		}
		
		
	}
	
	public void draw_voice_table(ArrayList<HarmonizationVoice> voice_list, HarmonizationVoice target){
		
		XYSeriesCollection xy_series_collection = new XYSeriesCollection();
		XYSeries voices_series = new XYSeries("generated_voices");
		XYSeries target_serie = new XYSeries("original voice");
		
		System.out.println("SIZE : "+voice_list.size());
		for (int i=0;i<voice_list.size();i++){
			voices_series.add(voice_list.get(i).get_final_horizontal_compliance(), voice_list.get(i).get_final_vertical_compliance());
		}
		target_serie.add(target.get_final_horizontal_compliance(), target.get_final_vertical_compliance());
		
		xy_series_collection.addSeries(target_serie);
		xy_series_collection.addSeries(voices_series);
		DotDiagram dot_diagram = new DotDiagram("Harmonization voices compliance",xy_series_collection);
		dot_diagram.display_dot_diagram();
	}
	
	public void to_midi_file(boolean forbid_repetition, String name){
		_name = name;
		to_midi_file(forbid_repetition);
	}
	
	public void to_midi_file(boolean forbid_repetition){
		
		
		Sequence sequence = get_midi_sequence(forbid_repetition);
		assert _name!=null : "ColStream has no name";
		write_midi(sequence,_name);
	}

	public void to_midi_file(PitchSetStream supp_voice,String name){
		Sequence sequence = get_midi_sequence(supp_voice);
		write_midi(sequence, name);
	}
	
	public void write_midi(Sequence sequence, String name){
		try {
			MidiSystem.write(sequence, MidiSystem.getMidiFileTypes(sequence)[0], create_new_file(name));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Sequence get_midi_sequence(PitchSetStream supp_voice){
		Sequence sequence = create_new_sequence();
		create_new_track(sequence,0,false);
		supp_voice.create_new_track(sequence,1,false);
		
		return sequence;
	}
	
	public Sequence get_midi_sequence(boolean forbid_repetition){
		
		Sequence sequence = create_new_sequence();
		create_new_track(sequence,0,forbid_repetition);		
		return sequence;
	}
	
	public static Sequence create_new_sequence(){
		Sequence sequence = null;
		try {
			//sequence = new Sequence(Sequence.PPQ, 240);
			sequence = new Sequence(Sequence.PPQ*(float)2, 1000);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sequence;
	}
	
	public void create_new_track(Sequence sequence,int channel, boolean forbid_repetition){
		
		sequence.createTrack();
		int track_count = sequence.getTracks().length;
		Track track = sequence.getTracks()[track_count-1];
		for (Long key : keySet()){
			PitchSetWithDuration col = get(key);
			for (Integer pitch : col){
				ShortMessage midi_message_ON = new ShortMessage();
				ShortMessage midi_message_OFF = new ShortMessage();
				try {
					midi_message_ON.setMessage(ShortMessage.NOTE_ON,channel, pitch, 127);
					midi_message_OFF.setMessage(ShortMessage.NOTE_OFF,channel, pitch, 127);
				} catch (InvalidMidiDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (forbid_repetition){
					if (key == firstKey()) track.add(new MidiEvent(midi_message_ON,key));
					else {
						if (key == lastKey()) track.add(new MidiEvent(midi_message_OFF,key+col.get_duration()));
						else {
							if (!get(lowerKey(key)).contains(pitch)){
								track.add(new MidiEvent(midi_message_ON,key));
							}
							if (!get(higherKey(key)).contains(pitch)){
								track.add(new MidiEvent(midi_message_OFF,key+col.get_duration()));
							}
						}
					}
				} else {
					track.add(new MidiEvent(midi_message_ON,key));
					track.add(new MidiEvent(midi_message_OFF,key+col.get_duration()));					
				}
			}
		}
	}
	
	
	
	/*****************  TOPOLOGY  ********************/
	
	
	/*** Window ***/
	
	public ChordComplex get_relative_window_complex(int start, int duration){
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		if (start>=keySet().size()) return null;
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		Long first_key = key_list.get(start); 
		Long last_key = key_list.get(start+duration);
		long key = first_key;
		while(key!=last_key){
			if (get(key).get_duration()>(get_tick_number_per_milli_second()*100)){
				pcs_set.add(get(key).to_PitchClassSet());				
			} 
			key=higherKey(key);
		}
		ChordComplex window_complex = new ChordComplex(pcs_set);
		return window_complex;
	}
	
	public ChordComplex get_absolute_window_complex(long start_in_ticks, long duration_in_ticks){
		HashSet<PitchClassSet> pcs_set = new HashSet<PitchClassSet>();
		if (start_in_ticks>get_duration()) return null;
		Long first_key = floorKey(start_in_ticks); 
		Long last_key = floorKey(start_in_ticks+duration_in_ticks);
		long key = first_key;
		while(key!=last_key){
			if (get(key).get_duration()>(get_tick_number_per_milli_second()*100)){
				pcs_set.add(get(key).to_PitchClassSet());				
			} 
			key=higherKey(key);
		}
		ChordComplex window_complex = new ChordComplex(pcs_set);
		return window_complex;
	}

	
	/****** BETTI NUMBERS ******/
	
	/*** Absolute Time ***/

	public XYSeries get_xy_serie_betti_absolute(long window_size_in_ms,long raffinnement_in_ms,String name, int betti_number){
		return get_XYSeries_from_TreeMap(get_betti_over_absolute_time(window_size_in_ms, raffinnement_in_ms, betti_number),name);
	}
	
	public TreeMap<Double,Integer> get_betti_over_absolute_time(long window_size_in_ms, long raffinnement_in_ms, int betti_number){
		long window_size_in_ticks = (long)(get_tick_number_per_milli_second()*window_size_in_ms);
		long raffinnement_in_ticks = (long)(get_tick_number_per_milli_second()*raffinnement_in_ms);
	
		TreeMap<Double,Integer> betti_over_time = new TreeMap<Double,Integer>();
		for (long key = window_size_in_ticks;key<=lastKey();key=key+(1*raffinnement_in_ticks)){
			ChordComplex complex = get_absolute_window_complex(key-window_size_in_ticks, window_size_in_ticks);
			betti_over_time.put((double)key/(1000*get_tick_number_per_milli_second()),complex.get_betti_number(betti_number));
		}
		return betti_over_time;
	}
		
		/*** Relative Time ***/
	
	public XYSeries get_xy_serie_betti_relative(int window_size,int raffinnement,String name, int betti_number){
		return get_XYSeries_from_TreeMap(get_betti_over_relative_time(window_size, raffinnement, betti_number),name);
	}
	
	public TreeMap<Long,Integer> get_betti_over_relative_time(int window_size, int raffinnement, int betti_number){
		TreeMap<Long,Integer> betti_over_time = new TreeMap<Long,Integer>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (int index = 0;index<=key_list.size()-window_size-1;index = index + raffinnement){
			ChordComplex complex = get_relative_window_complex(index, window_size);
			betti_over_time.put((long)index,complex.get_betti_number(betti_number));
		}
		return betti_over_time;
	}
	
	public XYSeries get_xy_serie_betti_realtive_average(String name, int betti_number) {
		return get_XYSeries_from_TreeMap(get_betti_average_over_relative_window(betti_number),name);
	}

	public XYSeries get_xy_serie_betti_realtive_average(String name, int window_max, int betti_number) {
		return get_XYSeries_from_TreeMap(get_betti_average_over_relative_window(window_max, betti_number),name);
	}

	public TreeMap<Integer,Float> get_betti_average_over_relative_window(int betti_number){

		TreeMap<Integer,Float> betti_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=keySet().size();window_in_index++){
			betti_over_window.put(window_in_index, get_average_betti_relative(window_in_index,betti_number));
		}
		return betti_over_window;
	}

	public TreeMap<Integer,Float> get_betti_average_over_relative_window(int window_max, int betti_number){

		TreeMap<Integer,Float> betti_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=window_max;window_in_index++){
			betti_over_window.put(window_in_index, get_average_betti_relative(window_in_index,betti_number));
		}
		return betti_over_window;
	}

	public Float get_average_betti_relative(int window_in_index, int betti_number){
		ArrayList<Float> betti_list = new ArrayList<Float>();
		for (int i=0;i<keySet().size()-window_in_index;i++){
			ChordComplex complex = get_relative_window_complex(i, window_in_index);
			betti_list.add((float)complex.get_betti_number(betti_number));
		}
		float average = 0;
		for (float dim : betti_list) average = average+dim;
		return average/betti_list.size();
	}


	
	/****** DIMENSION ******/
	
		/*** Absolute Time ***/
	
	public XYSeries get_xy_serie_dimension_absolute(long window_size_in_ms,long raffinnement_in_ms,String name){
		return get_XYSeries_from_TreeMap(get_dimension_over_absolute_time(window_size_in_ms, raffinnement_in_ms),name);
	}

	public TreeMap<Double,Integer> get_dimension_over_absolute_time(long window_size_in_ms, long raffinnement_in_ms){
		long window_size_in_ticks = (long)(get_tick_number_per_milli_second()*window_size_in_ms);
		long raffinnement_in_ticks = (long)(get_tick_number_per_milli_second()*raffinnement_in_ms);

		TreeMap<Double,Integer> dimension_over_time = new TreeMap<Double,Integer>();
		for (long key = window_size_in_ticks;key<=lastKey();key=key+(1*raffinnement_in_ticks)){
			ChordComplex complex = get_absolute_window_complex(key-window_size_in_ticks, window_size_in_ticks);
			dimension_over_time.put((double)key/(1000*get_tick_number_per_milli_second()),complex.get_dimension());
		}
		return dimension_over_time;
	}
		
		/*** Relative Time ***/
	
	public XYSeries get_xy_serie_dimension_relative(int window_size,int raffinnement,String name){
		return get_XYSeries_from_TreeMap(get_dimension_over_relative_time(window_size, raffinnement),name);
	}

	public TreeMap<Long,Integer> get_dimension_over_relative_time(int window_size, int raffinnement){
		TreeMap<Long,Integer> dimension_over_time = new TreeMap<Long,Integer>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (int index = 0;index<=key_list.size()-window_size-1;index = index + raffinnement){
			ChordComplex complex = get_relative_window_complex(index, window_size);
			dimension_over_time.put((long)index,complex.get_dimension());
		}
		return dimension_over_time;
	}
	
	public XYSeries get_xy_serie_dimension_realtive_average(String name) {
		return get_XYSeries_from_TreeMap(get_dimension_average_over_relative_window(),name);
	}

	public XYSeries get_xy_serie_dimension_realtive_average(String name, int window_max) {
		return get_XYSeries_from_TreeMap(get_dimension_average_over_relative_window(window_max),name);
	}

	public TreeMap<Integer,Float> get_dimension_average_over_relative_window(){

		TreeMap<Integer,Float> dimension_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=keySet().size();window_in_index++){
			dimension_over_window.put(window_in_index, get_average_dimension_relative(window_in_index));
		}
		return dimension_over_window;
	}

	public TreeMap<Integer,Float> get_dimension_average_over_relative_window(int window_max){

		TreeMap<Integer,Float> dimension_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=window_max;window_in_index++){
			dimension_over_window.put(window_in_index, get_average_dimension_relative(window_in_index));
		}
		return dimension_over_window;
	}

	public Float get_average_dimension_relative(int window_in_index){
		ArrayList<Float> dimensions = new ArrayList<Float>();
		for (int i=0;i<keySet().size()-window_in_index;i++){
			ChordComplex complex = get_relative_window_complex(i, window_in_index);
			dimensions.add((float)complex.get_dimension());
		}
		float average = 0;
		for (float dim : dimensions) average = average+dim;
		return average/dimensions.size();
	}

	
	/****** SIZE ******/
	
		/*** Absolute Time ***/
	
	public XYSeries get_xy_serie_size_absolute(long window_size_in_ms, long raffinnement_in_ms,String name, int dimension) {
		//return get_xy_serie_double_key(get_size_over_absolute_time(window_size_in_ms, raffinnement_in_ms, dimension),name);
		return get_XYSeries_from_TreeMap(get_size_over_absolute_time(window_size_in_ms, raffinnement_in_ms, dimension),name);
	}
	
	public TreeMap<Double,Integer> get_size_over_absolute_time(long window_size_in_ms, long raffinnement_in_ms, int dimension){
		long window_size_in_ticks = (long)(get_tick_number_per_milli_second()*window_size_in_ms);
		long raffinnement_in_ticks = (long)(get_tick_number_per_milli_second()*raffinnement_in_ms);

		TreeMap<Double,Integer> size_over_time = new TreeMap<Double,Integer>();
		for (long key = window_size_in_ticks;key<=lastKey();key=key+(1*raffinnement_in_ticks)){
			ChordComplex complex = get_absolute_window_complex(key-window_size_in_ticks, window_size_in_ticks);
			size_over_time.put((double)key/(1000*get_tick_number_per_milli_second()),complex.get_d_simplex_count(dimension));
		}
		return size_over_time;
	}

		/*** Relative Time ***/
		
	public XYSeries get_xy_serie_size_relative(int window_size,int raffinnement,String name, int dimension){
		//return get_xy_serie_long_key(get_size_over_relative_time(window_size, raffinnement, dimension),name);
		return get_XYSeries_from_TreeMap(get_size_over_relative_time(window_size, raffinnement, dimension),name);
	}
	
	public TreeMap<Long,Integer> get_size_over_relative_time(int window_size, int raffinnement, int dimension){
		TreeMap<Long,Integer> size_over_time = new TreeMap<Long,Integer>();
		ArrayList<Long> key_list = new ArrayList<Long>(keySet());
		for (int index = 0;index<=key_list.size()-window_size;index = index + raffinnement){
			ChordComplex complex = get_relative_window_complex(index, window_size);
			size_over_time.put((long)index,complex.get_d_simplex_count(dimension));
		}
		return size_over_time;
	}
	
	public XYSeries get_xy_serie_size_relative_average(String name, int dimension) {
		return get_XYSeries_from_TreeMap(get_size_average_over_relative_window(dimension),name);
	}

	public XYSeries get_xy_serie_size_relative_average(String name, int dimension, int window_max) {
		return get_XYSeries_from_TreeMap(get_size_average_over_relative_window(dimension,window_max),name);
	}

	

	public TreeMap<Integer,Float> get_size_average_over_relative_window(int dimension){

		TreeMap<Integer,Float> size_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=keySet().size();window_in_index++){
			size_over_window.put(window_in_index, get_average_size_relative(window_in_index,dimension));
		}
		return size_over_window;
	}

	public TreeMap<Integer,Float> get_size_average_over_relative_window(int dimension, int window_max){

		TreeMap<Integer,Float> size_over_window = new TreeMap<Integer,Float>();
		for (int window_in_index = 1;window_in_index<=window_max;window_in_index++){
			size_over_window.put(window_in_index, get_average_size_relative(window_in_index,dimension));
		}
		return size_over_window;
	}

	public Float get_average_size_relative(int window_in_index, int dimension){
		ArrayList<Float> sizes = new ArrayList<Float>();
		for (int i=0;i<keySet().size()-window_in_index;i++){
			ChordComplex complex = get_relative_window_complex(i, window_in_index);
			sizes.add((float)complex.get_d_simplex_count(dimension));
		}
		float average = 0;
		for (float dim : sizes) average = average+dim;
		return average/sizes.size();
	}

	
	/*** JFreeChart ***/
	
//	public XYSeries get_xy_serie_long_key(TreeMap<Long,Integer> tree_map, String name){
//		XYSeries serie = new XYSeries(name);
//		System.out.println("name : "+name);
//		System.out.println("treemap : "+tree_map);
//		for(long key : tree_map.keySet()){
//			//serie.add(key/((long)1000*get_tick_number_per_milli_second()), tree_map.get(key));
//			serie.add(key,tree_map.get(key));
//		}
//		return serie;		
//	}
//
//	public XYSeries get_xy_serie_double_key(TreeMap<Double,Integer> tree_map, String name){
//		XYSeries serie = new XYSeries(name);
//		System.out.println("name : "+name);
//		System.out.println("treemap : "+tree_map);
//		for(double key : tree_map.keySet()){
//			//serie.add(key/((long)1000*get_tick_number_per_milli_second()), tree_map.get(key));
//			serie.add(key,tree_map.get(key));
//		}
//		return serie;		
//	}
//	
//	public XYSeries get_xy_serie_double_key(TreeMap<Double,Integer> tree_map, String name){
//		XYSeries serie = new XYSeries(name);
//		System.out.println("name : "+name);
//		System.out.println("treemap : "+tree_map);
//		for(double key : tree_map.keySet()){
//			//serie.add(key/((long)1000*get_tick_number_per_milli_second()), tree_map.get(key));
//			serie.add(key,tree_map.get(key));
//		}
//		return serie;		
//	}

	public XYSeries get_XYSeries_from_TreeMap(TreeMap<? extends Number,? extends Number> tree_map, String name){
		XYSeries serie = new XYSeries(name);
		System.out.println("name : "+name);
		System.out.println("treemap : "+tree_map);
		for(Number key : tree_map.keySet()){
			serie.add(key.doubleValue(),tree_map.get(key).doubleValue());
		}
		return serie;
	}
	
	public ChordComplex get_nieme_complex(int n, long window_size_in_ms, long raffinnement_in_ms){
		long window_size_in_ticks = (long)(get_tick_number_per_milli_second()*window_size_in_ms);
		long raffinnement_in_ticks = (long)(get_tick_number_per_milli_second()*raffinnement_in_ms);
		return get_absolute_window_complex(n*raffinnement_in_ticks, window_size_in_ticks);
	}

	
//	public void draw_graph(long window_size_in_ms, long raffinnement_in_ms){
//		//System.out.println("nombre de ticks en 1/10 sec : "+(get_tick_number_per_second()/10));
//		long window_size_in_ticks = (long)(get_tick_number_per_milli_second()*window_size_in_ms);
//		long raffinnement_in_ticks = (long)(get_tick_number_per_milli_second()*raffinnement_in_ms);
//		
//		TreeMap<Long,Integer> dimension_over_time = get_dimension_over_absolute_time(window_size_in_ticks, raffinnement_in_ticks);
//		XYSeries serie = new XYSeries("dimension over time");
//		for (long key : dimension_over_time.keySet()){
//			serie.add(key/((long)1000*get_tick_number_per_milli_second()), dimension_over_time.get(key));
//		}
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		dataset.addSeries(serie);
//		LineDiagram diagram = new LineDiagram(dataset, "Dimension");
//		diagram.display_line_diagram("time (seconds)","dimension",false);
//	}
	
	
	public File create_new_file(String file_name){
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		pp = pp+"/out/midi/"+file_name+"."+"midi";
		System.out.println("file name = "+pp);
		File file = new File(pp);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return file;
	}
	

	public float get_tick_number_per_milli_second(){
		return (float)((float)get_duration()/((float)get_duration_in_milliseconds()));
	}

	public long get_duration_in_microseconds() {
		return length_in_microseconds;
	}

	public long get_duration_in_milliseconds() {
		return length_in_microseconds/1000;
	}

	public void setLength_in_microseconds(long length_in_microseconds) {
		this.length_in_microseconds = length_in_microseconds;
	}

	public ArrayList<Long> sized_pcs_duration_sum_in_ticks(){
		ArrayList<Long> duration_list = new ArrayList<Long>();
		for (int i=0;i<=12;i++) duration_list.add((long)0);
		PitchSetWithDuration ps;
		for (long key : keySet()) {
			ps = get(key);
			int pcs_size = ps.to_PitchClassSet().size();
			duration_list.set(pcs_size, duration_list.get(pcs_size)+ps.get_duration());
		}
		return duration_list;
	}
	
	public void print_pcs_size_info(){
		ArrayList<Long> list = sized_pcs_duration_sum_in_ticks();
		long duration = get_duration();
		float incr = 0;
		for (int i=12;i>=0;i--){
			if (list.get(i)>0){
				incr = incr + (100*(float)list.get(i)/(float)duration);
				System.out.println(i+" : "+incr);				
			}
		}
	}

	// percents of chords whose size is greater or equal to size 
	public float get_minmum_sized_proportion_pcs(int minimum_size){
		ArrayList<Long> list = sized_pcs_duration_sum_in_ticks();
		long duration = get_duration();
		float incr = 0;
		for (int i=12;i>=minimum_size;i--){
			incr = incr + (100*(float)list.get(i)/(float)duration);
		}
		return incr;
	}
	
	public long get_closest_key(long key){
		
		if (keySet().contains(key)) return key;
		if (key < firstKey()) return firstKey();
		if (key > lastKey()) return lastKey();
		long lower_key = lowerKey(key);
		long higher_key = higherKey(key);
		long dist_to_lower_key = key - lower_key;
		long dist_to_higher_key = higher_key - key;
		if (dist_to_lower_key < dist_to_higher_key) return lower_key;
		else return higher_key;
	}
	
	public long get_approximated_key(long key, int round){
		if (keySet().contains(key)) return key;
		if (key < firstKey()) return firstKey();
		if (key > lastKey()) return lastKey();
		long lower_key = lowerKey(key);
		long higher_key = higherKey(key);
		long dist_to_higher_key = higher_key - key;
		if (dist_to_higher_key <= round) return higher_key;
		return lower_key;
	}
	
	public PitchSetWithDuration get_closest_ps(long key){
		return get(get_closest_key(key));
	}
	
//	public TreeMap<Long,List<HAChordCandidate>> get_HAcandidate_stream(){
//		TreeMap<Long,List<HAChordCandidate>> cand_stream = new TreeMap<Long,List<HAChordCandidate>>();
//		for (long key : keySet()){
//			if (!get(key).isEmpty()){
//				cand_stream.put(key,get(key).get_HA_candidates());
//			}
//		}
//		return cand_stream;
//	}
	
	
	public List<Scale> get_HA_compatible_keys(long key){
		List<Scale> compatible_keys = new ArrayList<Scale>();
		PitchClassSet current_pcs = get(key).to_PitchClassSet();
		for (Scale major_scale : Scale.get_major_scales()){
			if (major_scale.containsAll(current_pcs)) compatible_keys.add(major_scale);
		}
		for (Scale minor_scale : Scale.get_minor_scales()){
			if (minor_scale.containsAll(current_pcs)) compatible_keys.add(minor_scale);
		}
		if (compatible_keys.isEmpty()) compatible_keys = get_HA_compatible_keys(lowerKey(key));
		return compatible_keys;
	}
	
	public Set<PitchClassSet> get_HA_compatible_chords(long key){
		PitchClassSet current_pcs = get(key).to_PitchClassSet();
		Set<PitchClassSet> compatible_chords = current_pcs.get_HA_compatible_chords();
		if (compatible_chords.isEmpty()) compatible_chords = get_HA_compatible_chords(lowerKey(key));
		return compatible_chords;
	}
	
	public List<HAChordCandidate> get_HA_candidates(long key){
		List<HAChordCandidate> cand_list = new ArrayList<HAChordCandidate>();
		for (Scale compatible_scale : get_HA_compatible_keys(key)){
			for (PitchClassSet compatible_triade : get_HA_compatible_chords(key)){
				cand_list.add(new HAChordCandidate(compatible_triade, compatible_scale));
			}
		}
		return cand_list;
	}

	public TreeMap<Long,Scale> get_inferred_key_stream(){
		HAChordCandidateGraph graph = new HAChordCandidateGraph(this);
		return graph.get_HA_best_path().get_defragmented_path().get_key_stream();
	}
	
	public PitchSetStream get_transformed_stream(List<ChordSymbol> permutation_list){
		PitchSetStream new_stream = new PitchSetStream();
		assert permutation_list.size()==size() : "cannot permute chords with a different sized list";
		int n = 0;
		for (long key : keySet()){
			new_stream.put(key, new PitchSetWithDuration(permutation_list.get(n).get_pcs(), get(key).get_duration()));
			n++;
		}
		System.out.println("BUG 1 "+this);
		System.out.println("BUG 2 "+new_stream);
		return new_stream;
	}
		
}
