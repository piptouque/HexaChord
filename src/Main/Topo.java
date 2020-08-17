package Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.sound.midi.Sequence;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.ChordComplex;
import Path.PitchClassMappingList;
import Path.PitchClassMappingTree;
import Utils.DrawComplex;
import Utils.FileUtils;
import Utils.LineDiagram;
import Utils.MidiFilePlayer;
import Utils.MidiParser;
import Utils.TransformedSequence;

public class Topo {

	//private static String REPERTORY = "topo";
	private static String REPERTORY = "topo";
	private static boolean sustain_ON = true;
	private static long ABSOLUTE_WINDOW_SIZE = 2000; // en ms
	private static long ABSOLUTE_RAFFINEMMENT = 500; // en ms
	
	private static int RELATIVE_WINDOW_SIZE = 5; // en ms
	private static int RELATIVE_RAFFINEMMENT = 1; // en ms
	
	private static boolean ABSOLUTE_WINDOW = false;
	public static boolean MAX_DURATION = false;
	public static boolean MAX_WINDOW = true;
	
	private static MidiFilePlayer _midi_file_player;
	private static ArrayList<String> file_names;
	//private static File[] file_list;
	private static ArrayList<File> midi_file_list;
	
	
	
	/****** UTILS ******/
	
	public static long shorter_absolute_duration(ArrayList<PitchSetStream> stream_list){
		long shorter_duration = stream_list.get(0).get_duration_in_milliseconds();
		for (int i=1;i<stream_list.size();i++){
			long stream_duration = stream_list.get(i).get_duration_in_milliseconds();
			if (stream_duration<shorter_duration) shorter_duration = stream_duration; 
		}
		return shorter_duration/1000;
	}

	public static int shorter_relative_duration(ArrayList<PitchSetStream> stream_list){
		int shorter_duration = stream_list.get(0).keySet().size();
		for (int i=1;i<stream_list.size();i++){
			int stream_duration = stream_list.get(i).keySet().size();
			if (stream_duration<shorter_duration) shorter_duration = stream_duration; 
		}
		return shorter_duration;
	}

	public static void reduce_duration(XYSeries[] series_table, long duration_max){
		for (XYSeries serie : series_table){
			double tmp = 0;
			int index = 0;
			while(tmp<duration_max && tmp<serie.getMaxX()){
				tmp = serie.getDataItem(index).getXValue();
				index++;
			}
			System.out.println("max index : "+index);
			while(serie.getItemCount()>index+1){
				serie.remove(index+1);
			}
			System.out.println("Serie size : "+serie.getItemCount()+" index : "+index+" item count : "+serie.getItemCount());				
		}
	}
	
	public static void display_diagram(ArrayList<PitchSetStream> stream_list, XYSeries[] series_table,String x_axis, String y_axis){
		if (MAX_DURATION) {
			if (ABSOLUTE_WINDOW){
				reduce_duration(series_table,shorter_absolute_duration(stream_list));				
			} else {
				reduce_duration(series_table,shorter_relative_duration(stream_list));
			}
		}
		XYSeriesCollection dataset = new XYSeriesCollection();;
		for (XYSeries s : series_table) {
			System.out.println("s : "+s);
			dataset.addSeries(s);
		}
		LineDiagram diagram = new LineDiagram(dataset,(""));
		diagram.display_line_diagram(x_axis,y_axis,false);
	}
	
	
	/****** DIMENSION ******/
	

	public static void display_dimension(ArrayList<PitchSetStream> stream_list){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		for (int i =0;i<stream_list.size();i++) {
			if (ABSOLUTE_WINDOW){
				//series_table[i] = stream_list.get(i).get_xy_serie_dimension_absolute(WINDOW_SIZE,RAFFINEMMENT,file_names.get(i));
				series_table[i] = stream_list.get(i).get_xy_serie_dimension_absolute(ABSOLUTE_WINDOW_SIZE,ABSOLUTE_RAFFINEMMENT,stream_list.get(i).get_name());				
			} else {
				//series_table[i] = stream_list.get(i).get_xy_serie_dimension_absolute(WINDOW_SIZE,RAFFINEMMENT,file_names.get(i));
				series_table[i] = stream_list.get(i).get_xy_serie_dimension_relative(RELATIVE_WINDOW_SIZE,RELATIVE_RAFFINEMMENT,stream_list.get(i).get_name());								
			}
		}
		if (ABSOLUTE_WINDOW){
			display_diagram(stream_list,series_table,"time (seconds)","Complex dimension");			
		} else {
			display_diagram(stream_list,series_table,"time (events)","Complex dimension");
		}
	}
	
	public static void display_dimension_averages(ArrayList<PitchSetStream> stream_list){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		if (MAX_WINDOW){
			int window_max = shorter_relative_duration(stream_list);
			for (int i =0;i<stream_list.size();i++) {
				series_table[i] = stream_list.get(i).get_xy_serie_dimension_realtive_average(stream_list.get(i).get_name(),window_max);
			}
		} else {
			for (int i =0;i<stream_list.size();i++) {
				series_table[i] = stream_list.get(i).get_xy_serie_dimension_realtive_average(stream_list.get(i).get_name());
			}			
		}
		display_diagram(stream_list,series_table,"window size (events)","Complex dimension");
	}

	/****** BETTI ******/
	

	public static void display_betti(ArrayList<PitchSetStream> stream_list, int betti){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		for (int i =0;i<stream_list.size();i++) {
			if (ABSOLUTE_WINDOW){
				series_table[i] = stream_list.get(i).get_xy_serie_betti_absolute(ABSOLUTE_WINDOW_SIZE,ABSOLUTE_RAFFINEMMENT,file_names.get(i), betti);
				//series_table[i] = stream_list.get(i).get_xy_serie_connexity_absolute(ABSOLUTE_WINDOW_SIZE,ABSOLUTE_RAFFINEMMENT,stream_list.get(i).get_name());				
			} else {
				series_table[i] = stream_list.get(i).get_xy_serie_betti_absolute(RELATIVE_WINDOW_SIZE,RELATIVE_RAFFINEMMENT,file_names.get(i), betti);
				//series_table[i] = stream_list.get(i).get_xy_serie_connexity_relative(RELATIVE_WINDOW_SIZE,RELATIVE_RAFFINEMMENT,stream_list.get(i).get_name());								
			}
		}
		if (ABSOLUTE_WINDOW){
			display_diagram(stream_list,series_table,"time (seconds)","Betti "+betti);			
		} else {
			display_diagram(stream_list,series_table,"time (events)","Betti "+betti);
		}
	}
	
	public static void display_betti_averages(ArrayList<PitchSetStream> stream_list, int betti){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		if (MAX_WINDOW){
			int window_max = shorter_relative_duration(stream_list);
			for (int i =0;i<stream_list.size();i++) {
				series_table[i] = stream_list.get(i).get_xy_serie_betti_realtive_average(stream_list.get(i).get_name(),window_max, betti);
				//series_table[i] = stream_list.get(i).get_xy_serie_betti_realtive_average(file_names.get(i),window_max, betti);
			}
		} else {
			for (int i =0;i<stream_list.size();i++) {
				series_table[i] = stream_list.get(i).get_xy_serie_betti_realtive_average(stream_list.get(i).get_name(), betti);
			}			
		}
		display_diagram(stream_list,series_table,"window size (events)","Betti "+betti);
	}

	

	
	/****** SIZE ******/
	
	public static void display_size(ArrayList<PitchSetStream> stream_list, int dimension){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		for (int i =0;i<stream_list.size();i++) {
			if (ABSOLUTE_WINDOW){
				//series_table[i] = stream_list.get(i).get_xy_serie_size_absolute(WINDOW_SIZE,RAFFINEMMENT,file_names.get(i),dimension);
				series_table[i] = stream_list.get(i).get_xy_serie_size_absolute(ABSOLUTE_WINDOW_SIZE,ABSOLUTE_RAFFINEMMENT,stream_list.get(i).get_name(),dimension);				
			} else {
				//series_table[i] = stream_list.get(i).get_xy_serie_size_relative(WINDOW_SIZE,RAFFINEMMENT,file_names.get(i),dimension);
				series_table[i] = stream_list.get(i).get_xy_serie_size_relative(RELATIVE_WINDOW_SIZE,RELATIVE_RAFFINEMMENT,stream_list.get(i).get_name(),dimension);
			}
		}
		if (ABSOLUTE_WINDOW){
			display_diagram(stream_list,series_table,"time (seconds)","Complex size (d="+dimension+")");			
		} else {
			display_diagram(stream_list,series_table,"time (events)","Complex size (d="+dimension+")");
		}
	}
	
	public static void display_size_averages(ArrayList<PitchSetStream> stream_list, int dimension){
		
		XYSeries[] series_table = new XYSeries[stream_list.size()];
		if (MAX_WINDOW){
			int window_max = shorter_relative_duration(stream_list);
			for (int i =0;i<stream_list.size();i++) {
				//series_table[i] = stream_list.get(i).get_xy_serie_size_relative_average(stream_list.get(i).get_name(),dimension, window_max);
				series_table[i] = stream_list.get(i).get_xy_serie_size_relative_average(file_names.get(i),dimension, window_max);
			}
		} else {
			for (int i =0;i<stream_list.size();i++) {
				series_table[i] = stream_list.get(i).get_xy_serie_size_relative_average(stream_list.get(i).get_name(),dimension);
			}			
		}
		display_diagram(stream_list,series_table,"window size (events)","Complex size (d="+dimension+")");
	}

	public static void display_size_averages(ArrayList<PitchSetStream> stream_list){
		for (int d=0;d<=3;d++) display_size_averages(stream_list,d);
	}
	
	
	
	/****** STRUCTURE ******/
	
	// Trouve les complexes structurellements �gaux
	private static void compare_topology(ArrayList<PitchSetStream> stream_list) {
	
		ChordComplex complex1, complex2;
		for (int i = 0;i<stream_list.size();i++){
			System.out.println(i+"/"+stream_list.size());
			complex1 = new ChordComplex(stream_list.get(i).to_pc_set_list());
			for (int j=i+1;j<stream_list.size();j++){
				complex2 = new ChordComplex(stream_list.get(j).to_pc_set_list());
				if (complex1.is_same_complex_structure(complex2)) {
				//if (complex1.contains_complex_structure(complex2)) {
					System.out.println(stream_list.get(i).get_name()+" et "+stream_list.get(j).get_name()+" = MEME COMPLEXES");
				} else {
					//System.out.println(stream_list.get(i).get_name()+" et "+stream_list.get(j).get_name()+" = COMPLEXES DIFFERENTS");
				}
			}
		}
	}
	
	// Trouve les complexes qui se contiennent d'un point de vue structurel
	private static void test_complex_inclusions(ArrayList<PitchSetStream> stream_list) {
	
		ChordComplex complex1, complex2;
		for (int i = 0;i<stream_list.size();i++){
			System.out.println("on cherche des inclusions dans "+stream_list.get(i).get_name());
			System.out.println(i+"/"+stream_list.size());
			complex1 = new ChordComplex(stream_list.get(i).to_pc_set_list());
			for (int j=0;j<stream_list.size();j++){
				if (i!=j){
					//System.out.println(i+"/"+stream_list.size()+" - "+j+"/"+stream_list.size());
					complex2 = new ChordComplex(stream_list.get(j).to_pc_set_list());
					HashSet<PitchClassMappingList> mapping_set = complex1.get_inclusion_pc_mapping_set(complex2);
					if (mapping_set.size()>0) {
						System.out.println(stream_list.get(i).get_name()+" contient "+stream_list.get(j).get_name());
						construct_mid_files(j,i,stream_list.get(j),mapping_set);
					} else {
						//System.out.println(stream_list.get(i).get_name()+" ne contient pas "+stream_list.get(j).get_name());
					}					
				}
			}
		}
	}

	public static void construct_mid_files(int index_transformed, int index_support, PitchSetStream stream, HashSet<PitchClassMappingList> mapping_set){
		
		int i = 0;
		for (PitchClassMappingList mapping : mapping_set){
			String trans_name = midi_file_list.get(index_transformed).getName()+"_dans_"+midi_file_list.get(index_support).getName()+i;
			PitchClassMappingTree pc_mapping_tree = new PitchClassMappingTree(stream.keySet(),mapping);
			TransformedSequence new_sequence = TransformedSequence.transforme_with_pc_mapping(get_sequence(index_transformed),pc_mapping_tree,trans_name);
			_midi_file_player.set_sequence_in_sequencer(new_sequence,trans_name);
			_midi_file_player.export_sequence_as_midi();
			i++;
		}
	}
	
	public static Sequence get_sequence(int index){
		return _midi_file_player.getSequenceFromFile(midi_file_list.get(index));
	}
	
	/****** MAIN ******/
	
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("java.runtime.version"));
		
		System.exit(0);
		
		file_names = new ArrayList<String>();
		file_names.add("Anton Webern - Op. 28 2nd movement");
		file_names.add("J.S Bach - Choral BWV 292");
		file_names.add("P. Glass - Metamorphosis 1");
		file_names.add("A Schoenberg - OP. 33 a");
		
		//file_names.add("Chick Corea - Eternal Child");
		//file_names.add("J.S Bach - Variations Goldberg 1");
//		file_names.add("J.S Bach - Choral BWV 325");
//		file_names.add("Philip Glass - Metamorphosis I");
//		file_names.add("A Schoenberg - Pierrot Lunaire 21");

		
		//_midi_file_player = new MidiFilePlayer();
		_midi_file_player = MidiFilePlayer.getInstance();
		
		String pp = null;
		try {pp = (new File(".")).getCanonicalPath();}
		catch (IOException e3) {e3.printStackTrace();}

		File dir = new File(pp+"/"+REPERTORY+"/");
		File[] brut_file_list = dir.listFiles();
		midi_file_list = new ArrayList<File>();
		for (File file : brut_file_list){
			if (FileUtils.is_midi(file.toString())){
				midi_file_list.add(file);
			}
		}
//		file_list = dir.listFiles();
		
		ArrayList<PitchSetStream> stream_list = new ArrayList<PitchSetStream>();
		for (File file : midi_file_list){
			//if (FileUtils.is_midi(file.toString())){
				System.out.println("file : "+file);
				PitchSetStream stream = MidiParser.stream_generator(_midi_file_player.getSequenceFromFile(file), sustain_ON).rounded_ColStream(6);
				System.out.println("stream : "+stream);
				System.out.println("Relative size = "+stream.keySet().size());
				stream.set_name(FileUtils.get_real_name(file));
				stream.setLength_in_microseconds(_midi_file_player.getSequenceFromFile(file).getMicrosecondLength());
				stream_list.add(stream);
			//}
		}
		
		//display_dimension(stream_list);
		//display_size(stream_list,2);
		//display_dimension_averages(stream_list);
		//display_size_averages(stream_list);
		//display_connexity(stream_list);
		//display_betti_averages(stream_list,0);
		//display_betti_averages(stream_list,1);
		
//		ChordComplex complex1 = new ChordComplex(stream_list.get(0).to_pc_set_list());
//		ChordComplex complex2 = new ChordComplex(stream_list.get(1).to_pc_set_list());
//		
//		complex1.contains_complex_structure(complex2);
		
//		DrawComplex.draw_sequence_CS(complex1);
//		DrawComplex.draw_sequence_CS(complex2);
		
		//compare_topology(stream_list);
		
		
		
		test_complex_inclusions(stream_list);
		
	}


}
