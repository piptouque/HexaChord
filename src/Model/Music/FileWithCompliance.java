package Model.Music;

public class FileWithCompliance {
	
	public String _name;
	public float[] _vector;
	
	public FileWithCompliance(String name, float[] vector){
		_name = new String(name);
		_vector = new float[vector.length];
		for (int i=0;i<vector.length;i++){
			_vector[i]=vector[i];
		}
	}

	public static double get_file_distance(FileWithCompliance file1, FileWithCompliance file2){
		return get_euclidean_distance(file1._vector, file2._vector);
	}

	public static double get_euclidean_distance(float[] vector1, float[] vector2){
		assert vector1.length == vector2.length : "vectors must be the same size";
		int dim = vector1.length;
		double distance=0;
		
		for (int i=0;i<dim;i++){
			distance=distance+Math.pow(vector1[i]-vector2[i], 2);
		}
		
		return Math.sqrt(distance);
	}

}
