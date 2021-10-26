package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import Model.Music.FileWithCompliance;
import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.TIChordComplex;
import Utils.FileUtils;
import Utils.MidiFilePlayer;
import Utils.MidiParser;
import Utils.Table;

public class Classification {

	private static boolean _sustain_ON = true;
	private static int _COMPACTNESS_DIMENSION = 1;
	private static boolean _ABSOLUTE_COMPLIANCE = false;
	private static int _COMPLIANCE_NUMBER_SIZE = 10;
	private static int _MINIMUM_PERCENT_SIZED_CHORDS = 50;

	public static void main(String[] args) {

		// MidiFilePlayer _midi_file_player = new MidiFilePlayer();
		MidiFilePlayer _midi_file_player = MidiFilePlayer.getInstance();

		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		System.out.println("pp : " + pp);
		File dir = new File(pp + "/dendro/");

		// for (File file : dir.listFiles()){
		// if (FileUtils.is_midi(file.toString())){
		// System.out.println("file : "+file);
		// PitchSetStream col_stream =
		// MidiParser.stream_generator(_midi_file_player.getSequenceFromFile(file),
		// _sustain_ON);
		// //col_stream.print_pcs_size_info();
		// System.out.println("proportion d'accords à moins 3 PC :
		// "+col_stream.get_minmum_sized_proportion_pcs(3));
		// System.out.println("proportion d'accords à au moins 4 PC :
		// "+col_stream.get_minmum_sized_proportion_pcs(4));
		// }
		// }
		//
		// System.exit(0);

		ArrayList<FileWithCompliance> file_table = new ArrayList<FileWithCompliance>();

		for (File file : dir.listFiles()) {
			if (FileUtils.is_midi(file.toString())) {
				System.out.println("file : " + file);
				PitchSetStream col_stream = MidiParser.stream_generator(_midi_file_player.getSequenceFromFile(file),
						_sustain_ON);
				if (col_stream
						.get_minmum_sized_proportion_pcs(_COMPACTNESS_DIMENSION + 1) >= _MINIMUM_PERCENT_SIZED_CHORDS) {
					float[] compactness_vector;
					ArrayList<TIChordComplex> complex_list = TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3);
					// ArrayList<float[]> compliance_table =
					// col_stream.z12HexaTonnetz_compliance_table(_COMPACTNESS_DIMENSION,complex_list,_ABSOLUTE_COMPLIANCE);
					ArrayList<float[]> compliance_table = col_stream.Z12FoldedTonnetz_compliance_table(
							FileUtils.get_real_name(file), _COMPACTNESS_DIMENSION, complex_list, _ABSOLUTE_COMPLIANCE);
					// compactness_vector =
					// col_stream.z12HexaTonnetz_compliance_average_table(compliance_table);
					compactness_vector = col_stream.z12FoldedTonnetz_compliance_average_table(compliance_table,
							_COMPACTNESS_DIMENSION);
					FileWithCompliance file_with_compliance = new FileWithCompliance(FileUtils.get_real_name(file),
							compactness_vector);
					file_table.add(file_with_compliance);
					System.out.println("compactness vector : " + Table.toString(compactness_vector));
				} else {
					System.out.println("not enough " + (_COMPACTNESS_DIMENSION + 1) + "chords to compute "
							+ _COMPACTNESS_DIMENSION + "-compactness");
				}
			}
		}

		// for (String file : dir.list()){
		// if (is_midi(file)){
		// System.out.println("file : "+file);
		// float[] compactness_vector;
		// PitchSetStream col_stream =
		// MidiParser.stream_generator(_midi_file_player.getSequenceFromPath("/"+pp+"/dendro/"+file),
		// _sustain_ON);
		// ArrayList<float[]> compliance_table =
		// col_stream.z12HexaTonnetz_compliance_table(_COMPACTNESS_DIMENSION);
		// compactness_vector =
		// col_stream.z12HexaTonnetz_compliance_average_table(compliance_table);
		// FileWithCompliance file_with_compliance = new
		// FileWithCompliance(file,compactness_vector);
		// file_table.add(file_with_compliance);
		// System.out.println("compactness vector :
		// "+Table.toString(compactness_vector));
		// }
		// }

		// TreeMap<Double,String> tree_map = new TreeMap<Double,String>();
		//
		// System.out.println("Table de distance par rapport à
		// "+file_table.get(0)._name);
		// for (FileWithCompliance file_with_compliance : file_table){
		// tree_map.put(get_file_distance(file_table.get(0),file_with_compliance),
		// file_with_compliance._name);
		// }
		// for (double key : tree_map.keySet()){
		// System.out.println(tree_map.get(key)+" : "+key);
		// }

		generate_output_file(get_distance_matrix_string_buffer(file_table), "distance_matrix");
		// generate_output_file(get_newick_string_buffer(file_table),"distance_matrix.new");

		// Runtime runtime = Runtime.getRuntime();
		// String[] commande = new String[2];
		// commande[0]="./out/newick/fitch";
		// commande[1]="out/newick/distance_matrix";
		// try {
		// runtime.exec(commande);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.exit(0);

	}

	public static double[][] get_distance_matrix(ArrayList<FileWithCompliance> file_table) {
		int n = file_table.size();
		double[][] distance_matrix = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				distance_matrix[i][j] = FileWithCompliance.get_file_distance(file_table.get(i), file_table.get(j));
			}
		}

		for (int i = 0; i < n; i++) {
			distance_matrix[i][i] = 0;
		}

		for (int j = 0; j < n; j++) {
			for (int i = j + 1; i < n; i++) {
				distance_matrix[i][j] = distance_matrix[j][i];
			}
		}

		return distance_matrix;
	}

	public static String to_sized_string(String str, int size) {
		String string = new String(str);
		for (int i = 0; i < size - str.length(); i++) {
			string = string + " ";
		}
		return string;
	}

	public static String to_sized_string(double l, int size) {
		String string = "";
		if (l == 0) {
			string = string + l;
			for (int i = 0; i < size - 3; i++) {
				string = string + "0";
			}
		} else {
			string = string + ((Math.floor(l * Math.pow(10, size - 2))) / Math.pow(10, size - 2));
		}
		return string;
	}

	public static StringBuffer get_distance_matrix_string_buffer(ArrayList<FileWithCompliance> file_table) {

		double[][] distance_matrix = get_distance_matrix(file_table);

		StringBuffer buffer = new StringBuffer();
		buffer.append(file_table.size() + "\n");

		for (int i = 0; i < file_table.size(); i++) {
			buffer.append(to_sized_string(file_table.get(i)._name, 100) + " ");
			for (int j = 0; j < file_table.size(); j++) {
				// buffer.append(" "+((Math.floor(distance_matrix[i][j]*1000))/1000));
				buffer.append(" " + to_sized_string(distance_matrix[i][j], _COMPLIANCE_NUMBER_SIZE));
			}
			buffer.append("\n");
		}
		return buffer;
	}

	public static StringBuffer get_newick_string_buffer(ArrayList<FileWithCompliance> file_table) {
		StringBuffer buffer = new StringBuffer();
		double[][] distance_matrix = get_distance_matrix(file_table);

		return buffer;
	}

	public static void generate_output_file(StringBuffer buffer, String name) {
		File out = new File("out/newick/" + name);
		FileWriter writer = null;
		try {
			writer = new FileWriter(out);
			writer.write(new String(buffer));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
