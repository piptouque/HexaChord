package Path;

import Model.Music.Interval;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;

public class PathTransformation {

	private PlanarUnfoldedTonnetz _original_planar_unfolded_tonnetz;
	private PlanarUnfoldedTonnetz _destination_planar_unfolded_tonnetz;
	private int[] _translation_vector;
	private SquareGridCoordPath _original_path;
	
	public PathTransformation(SquareGridCoordPath original_path,PlanarUnfoldedTonnetz original_planar_unfolded_tonnetz,PlanarUnfoldedTonnetz destination_planar_unfolded_tonnetz,int[] translation_vector){
		_original_path = original_path;
		_original_planar_unfolded_tonnetz = original_planar_unfolded_tonnetz;
		_destination_planar_unfolded_tonnetz = destination_planar_unfolded_tonnetz;
		_translation_vector = translation_vector;
	}

	public PathTransformation(SquareGridCoordPath original_path,PlanarUnfoldedTonnetz original_planar_unfolded_tonnetz,PlanarUnfoldedTonnetz destination_planar_unfolded_tonnetz){
		_original_path = original_path;
		_original_planar_unfolded_tonnetz = original_planar_unfolded_tonnetz;
		_destination_planar_unfolded_tonnetz = destination_planar_unfolded_tonnetz;
		_translation_vector = new int[]{0,0};
	}

	public int get_new_pitch(int original_pitch, long tick){
		
		int[] original_coord = _original_planar_unfolded_tonnetz.get_coord_corresponding_to_pitch(_original_path.floorEntry(tick).getValue(), original_pitch);
		//int[] original_coord = _original_planar_unfolded_tonnetz.get_coord_corresponding_to_pitch(_original_path.get(tick), original_pitch);
		if (original_coord == null){
			System.err.println("original_coord not found");
			return 0;
		}
		//assert original_coord != null : "original_coord not found";
		int[] new_coord = get_new_coord(original_coord);
		int new_pitch_class = _destination_planar_unfolded_tonnetz.xy_coord_to_pitch_class(new_coord);
		
		int new_pitch = pitch_class_to_pitch(new_pitch_class,original_pitch);
		
		return new_pitch;
	}
	
	public int[] get_new_coord(int[] original_coord){
		
		assert original_coord.length == _translation_vector.length : "original coord and translation vector are not the same size";
		int[] new_coord = new int[original_coord.length];
		for (int i=0;i<original_coord.length;i++){
			new_coord[i] = original_coord[i]+_translation_vector[i]; 
		}
		return new_coord;
	}
	
	// strategie 1 : on prend le pitch le plus proche du pitch original
	//private static int strategy = 1;
	
	public int pitch_class_to_pitch(int pitch_class, int original_pitch){
		int original_pitch_class = original_pitch%12;
		int mi_interval = Interval.MI(original_pitch_class, pitch_class, 12);
		if ((original_pitch+mi_interval)%12 == pitch_class){
			return original_pitch+mi_interval;
		} 
		if ((original_pitch-mi_interval)%12 == pitch_class){
			return original_pitch-mi_interval;
		}
		System.err.println("ERROR ! !");
		return 0;
	}
}
