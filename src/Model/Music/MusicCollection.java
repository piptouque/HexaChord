package Model.Music;

public interface MusicCollection {

	public MusicCollection remove_pitch(int pitch);
	public PitchClassSet to_PitchClassSet();
	public PitchClassSet to_PitchClassSet(int N);
	
}
