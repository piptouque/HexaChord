package Utils;

import java.io.IOException;
import Model.Music.PitchClassSetSeq;
import Model.Music.Tonnetze.ChordComplex;

import edu.stanford.math.plex4.io.DoubleArrayReaderWriter;
import edu.stanford.math.plex_viewer.gl.OpenGLManager;
import edu.stanford.math.plex_viewer.rendering.SimplexStreamRenderer;

public class DrawComplex {
	
	//private static String VERTICES_COORDINATES_FILE = "tmp/plex/pc_points.txt";
	//private static String VERTICES_COORDINATES_FILE = "tmp/plex/pc_torus.txt";
	//private static String VERTICES_COORDINATES_FILE = "tmp/plex/pc_points_CM.txt";
	//private static String VERTICES_COORDINATES_FILE = "tmp/plex/pc_strip.txt";
	//private static String VERTICES_COORDINATES_FILE = "tmp/plex/2345.txt";
	private static String VERTICES_COORDINATES_FILE = "tmp/plex/23456.txt";
	private static OpenGLManager openGLManager;

	public static void draw_sequence_CS(PitchClassSetSeq pc_set_seq){
		
		double[][] points;		
		
		try {
			points = DoubleArrayReaderWriter.getInstance().importFromFile(VERTICES_COORDINATES_FILE);
			openGLManager = new OpenGLManager(new SimplexStreamRenderer(pc_set_seq.get_simplex_stream(), points));
			openGLManager.initialize();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void draw_sequence_CS(ChordComplex complex){
		
		double[][] points;		
		OpenGLManager openGLManager;
		try {
			points = DoubleArrayReaderWriter.getInstance().importFromFile(VERTICES_COORDINATES_FILE);
			openGLManager = new OpenGLManager(new SimplexStreamRenderer(complex.get_Plex_complex(), points));
			openGLManager.initialize();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void close(){
		openGLManager.exit();
	}
}
