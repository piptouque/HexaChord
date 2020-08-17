package Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Model.Music.PitchSetStream;
import Model.Music.Tonnetze.TIChordComplex;
import Utils.FileUtils;
import Utils.MidiFilePlayer;
import Utils.MidiParser;

public class Comparison {
	
	private static String REPERTORY = "compa";
	private static ArrayList<String> file_names;
	private static boolean sustain_ON = true;


	public static void main(String[] args) {
		
		file_names = new ArrayList<String>();
		//file_names.add("Anton Webern - Op. 28 2nd movement");
		file_names.add("J.S Bach - Choral BWV 257");
		file_names.add("P. Glass - Metamorphosis 1");
		file_names.add("A Schoenberg - OP. 33 a");
				
		String pp = null;
		try {pp = (new File(".")).getCanonicalPath();}
		catch (IOException e3) {e3.printStackTrace();}

		File dir = new File(pp+"/"+REPERTORY+"/");
		
		ArrayList<PitchSetStream> stream_list = new ArrayList<PitchSetStream>();
		
		File[] files = dir.listFiles();
		for (int i=0;i<files.length;i++){
			if (FileUtils.is_midi(files[i].toString())){
				System.out.println("file : "+files[i]);
				PitchSetStream stream = MidiParser.stream_generator(MidiFilePlayer.getSequenceFromFile(files[i]), sustain_ON).rounded_ColStream(6);
				System.out.println("stream : "+stream);
				System.out.println("Relative size = "+stream.keySet().size());
				//stream.set_name(file_names.get(i));
				stream.set_name(FileUtils.get_real_name(files[i]));
				stream.setLength_in_microseconds(MidiFilePlayer.getSequenceFromFile(files[i]).getMicrosecondLength());
				stream_list.add(stream);
			}			
		}

		ArrayList<TIChordComplex> complex_list;
		//complex_list = new ArrayList<TIChordComplex>();
		complex_list = TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3); 
		//complex_list.addAll(TIChordComplex.getZ12Tonnetz_n_ChordComplexList(4));
		
		PitchSetStream.compliance_display(stream_list, complex_list, 2);
		

	}

}
