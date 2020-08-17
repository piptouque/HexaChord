package Path;

import java.util.ArrayList;

import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;

public class CentralSymmetryInTonnetz implements TransformationInTonnetz{
	
	private ArrayList<Float> _center_coords;
	
	public CentralSymmetryInTonnetz(ArrayList<Float> center_coords){
		assert verification(center_coords) : "non conform symmetry center : "+center_coords;
		_center_coords = center_coords;
	}
	
	public boolean verification(ArrayList<Float> center_coords){
		
		int float_count = 0;
		for (float coord : center_coords){
			if (coord%1 != 0){
				float_count++;
			}
		}
		if (float_count > 1) return false;
		else return true;
	}
	
	public int get_center_coord_size(){
		return _center_coords.size();
	}	
	
	public float get_float_center_symetry_pitch_class(PlanarUnfoldedTonnetz planar_unfolded_tonnetz){
		System.out.println("tonnetz : "+planar_unfolded_tonnetz);
		assert _center_coords.size() == planar_unfolded_tonnetz.generators_count() : "unfolding vector and symmetry center coords must be of same dimension";
		float pitch_class_center=0;
		for (int i=0;i<_center_coords.size();i++){
			pitch_class_center = pitch_class_center + _center_coords.get(i)*planar_unfolded_tonnetz.get_generator(i);
		}
		return pitch_class_center%12;
	}

	

}
