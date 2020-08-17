package Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.data.category.DefaultCategoryDataset;

import Model.Music.FileWithCompliance;
import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.TIChordComplex;
import Utils.BarDiagram;
import Utils.FileUtils;
import Utils.MidiFilePlayer;
import Utils.MidiParser;

public class Compactness {

	private static String REPERTORY = "compactness";
	private static boolean sustain_ON = true;
	private static int _COMPACTNESS_DIMENSION = 2;
	private static int _MINIMUM_PERCENT_SIZED_CHORDS = 50;
	private static boolean _ABSOLUTE_COMPLIANCE = false;

	private static ArrayList<TIChordComplex> _complex_list;
	
	public static void main(String[] args) {
		
		ArrayList<TIChordComplex> complex_list = TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3);
		ArrayList<Float> average_list = new ArrayList<Float>();
		for (int i=0;i<12;i++){
			average_list.add(complex_list.get(i).get_average_compactness(12, 2));
		}
		
		DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();
		for (int i=0;i<complex_list.size();i++){
			h_dataset.setValue(complex_list.get(i).get_average_compactness(12, 2), "", complex_list.get(i).toString());			
		}
		BarDiagram bar_diagram = new BarDiagram(h_dataset, "", false);
		bar_diagram.display_bar_diagram("2-compactness");
		
		System.exit(0);
		
		String pp = null;
		try {pp = (new File(".")).getCanonicalPath();}
		catch (IOException e3) {e3.printStackTrace();}
		
		_complex_list = TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3);
		
		File dir = new File(pp+"/"+REPERTORY+"/");
		//File random_chords_file = new File(pp+"/10000 random n-chords.midi");
		//FileWithCompliance random_chords_file_with_compliance = get_file_with_compliance(random_chords_file);
		//display_sorted_map(get_distance_to_a_piece(dir, random_chords_file_with_compliance));
		//String dir_name = "/Users/bigo/_SHARED/Midi/Bach/chorales.all.midi/";
		String dir_name = "/Users/louisbigo/_SHARED/Midi/Bach/chorales.all.midi/";
		display_sorted_map(get_distance_to_a_piece(new File(dir_name), get_corpus_mean_compliance(new File(dir_name),"Bach chorales")));
		
		display_corpus_compliance(get_corpus_mean_compliance(new File(dir_name),"Bach Chorales")._vector,"Bach Chorales");
		
		
		
	}
	
	public static Map<String,Double> get_distance_to_a_piece(File file_set_dir, FileWithCompliance compared_file){
		
		
//		PitchSetStream compared_piece_chords_stream = MidiParser.stream_generator(MidiFilePlayer.getSequenceFromFile(compared_file), sustain_ON).rounded_ColStream(6);
//		compared_piece_chords_stream.setLength_in_microseconds(MidiFilePlayer.getSequenceFromFile(compared_file).getMicrosecondLength());
//		ArrayList<float[]> compared_piece_chords_compliance_table = compared_piece_chords_stream.Z12FoldedTonnetz_compliance_table(FileUtils.get_real_name(compared_file),_COMPACTNESS_DIMENSION,complex_list,_ABSOLUTE_COMPLIANCE);
//		float[] random_chords_compactness_vector = compared_piece_chords_stream.z12FoldedTonnetz_compliance_average_table(compared_piece_chords_compliance_table,_COMPACTNESS_DIMENSION);
//		FileWithCompliance compared_piece_chords_file_with_compliance = new FileWithCompliance(FileUtils.get_real_name(compared_file),random_chords_compactness_vector);
		
		ArrayList<PitchSetStream> stream_list = new ArrayList<PitchSetStream>();
		
		File[] files = file_set_dir.listFiles();
		PitchSetStream stream;
		float[] compactness_vector;
		FileWithCompliance file_with_compliance;
		File file;
		Map<String,Double> distance_map = new HashMap<String,Double>();
		for (int i=0;i<files.length;i++){
			file = files[i];
			if (FileUtils.is_midi(file.toString())){
				stream = MidiParser.stream_generator(MidiFilePlayer.getSequenceFromFile(file), sustain_ON).rounded_ColStream(6);
				stream.setLength_in_microseconds(MidiFilePlayer.getSequenceFromFile(file).getMicrosecondLength());
				if (stream.get_minmum_sized_proportion_pcs(_COMPACTNESS_DIMENSION+1) >= _MINIMUM_PERCENT_SIZED_CHORDS){
					ArrayList<float[]> compliance_table = stream.Z12FoldedTonnetz_compliance_table(FileUtils.get_real_name(file),_COMPACTNESS_DIMENSION,_complex_list,_ABSOLUTE_COMPLIANCE);
					compactness_vector = stream.z12FoldedTonnetz_compliance_average_table(compliance_table,_COMPACTNESS_DIMENSION);
					file_with_compliance = new FileWithCompliance(FileUtils.get_real_name(file),compactness_vector);
					double distance_with_random = FileWithCompliance.get_file_distance(file_with_compliance,compared_file);
					distance_map.put(FileUtils.get_real_name(file),distance_with_random);
					//System.out.println("distance : "+distance_with_random);
				}
			}
		}
		return distance_map;
	}
	
	public static void display_sorted_map(Map<String,Double> distance_map){

		Set<Double> distance_set = new HashSet<Double>();
		for (Map.Entry<String, Double> entry : distance_map.entrySet()) distance_set.add(entry.getValue());
		List<Double> distance_list = new ArrayList<Double>(distance_set);
		Collections.sort(distance_list);
		for (double distance : distance_list){
			for (Map.Entry<String, Double> entry : distance_map.entrySet()){
				if (entry.getValue()==distance){
					System.out.println("distance : "+distance+" - "+entry.getKey());
				}
			}
		}
	}
	
	public static FileWithCompliance get_file_with_compliance(File file){
		
		FileWithCompliance file_with_compliance = null;
		if (FileUtils.is_midi(file.toString())){
			PitchSetStream stream = MidiParser.stream_generator(MidiFilePlayer.getSequenceFromFile(file), sustain_ON).rounded_ColStream(6);
			stream.setLength_in_microseconds(MidiFilePlayer.getSequenceFromFile(file).getMicrosecondLength());
			if (stream.get_minmum_sized_proportion_pcs(_COMPACTNESS_DIMENSION+1) >= _MINIMUM_PERCENT_SIZED_CHORDS){
				ArrayList<float[]> compliance_table = stream.Z12FoldedTonnetz_compliance_table(FileUtils.get_real_name(file),_COMPACTNESS_DIMENSION,_complex_list,_ABSOLUTE_COMPLIANCE);
				float[] compactness_vector = stream.z12FoldedTonnetz_compliance_average_table(compliance_table,_COMPACTNESS_DIMENSION);
				file_with_compliance = new FileWithCompliance(FileUtils.get_real_name(file),compactness_vector); 
			} 
		}
		return file_with_compliance;
	}
	
	public static FileWithCompliance get_corpus_mean_compliance(File dir, String corpus_name){
		
		List<float[]> vector_list = new ArrayList<float[]>();
		File[] files = dir.listFiles();
		File file;
		
		for (int i=0;i<files.length;i++){
			file = files[i];
			if (FileUtils.is_midi(file.toString())){
				vector_list.add(get_file_with_compliance(file)._vector);
			}
		}
		return new FileWithCompliance(corpus_name,get_average_vector(vector_list));
	}
	
	public static float[] get_average_vector(List<float[]> vector_list){
		int vector_size = vector_list.get(0).length;
		System.out.println("vector_size : "+vector_size);
		System.out.println("vector list size : "+vector_list.size());
		float[] average_vector = new float[vector_size];
		for (int i=0;i<vector_size;i++){
			
			float sum = 0;
			for (int j = 0;j<vector_list.size();j++) {
				if (i==1) System.out.println("j = "+j+" "+vector_list.get(j)[i]);
				sum = sum+vector_list.get(j)[i];
			}
			System.out.println(_complex_list.get(i)+" : "+(sum/(float)vector_list.size()));
			average_vector[i]=sum/(float)vector_list.size();	
		}
		
		return average_vector;
	}
	
	public static void display_corpus_compliance(float[] average_table, String name){

		DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();

		for (int i = 0;i<average_table.length;i++){
			//if (average_table[i]!=0){	// pour ne pas tracer les espaces dont la compliance = 0
				//dataset.setValue(complex_names.get(i), average_table[i]);
				h_dataset.setValue(average_table[i], name, _complex_list.get(i).toString());
				h_dataset.setValue(_complex_list.get(i).get_average_compactness(12, _COMPACTNESS_DIMENSION), "random chords", _complex_list.get(i).toString());
			//}
		}
		BarDiagram bar_diagram = new BarDiagram(h_dataset,name, true);
		
		String compactness_type;
		if (_COMPACTNESS_DIMENSION == -1) compactness_type = "abs"+"-compactness"; else compactness_type = _COMPACTNESS_DIMENSION+"-compactness";
		bar_diagram.display_bar_diagram(compactness_type);

	}
	
}
