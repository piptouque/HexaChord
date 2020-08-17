package Model.Music;

import java.util.ArrayList;

import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Path.TonnetzTranslationPath;
import Utils.MidiPlayer;

public class MusificationMovingCell {

	
//	public int _x;
//	public int _y;
	public ArrayList<Integer> _cell_coords;
	private TonnetzTranslationPath _path;
	private ArrayList<ArrayList<Integer>> _past_pos;
	private MusificationThread _musification_thread;
	//private MidiPlayer _midi_player;
	private PlanarUnfoldedTonnetz _tonnetz;
	private int _last_pitch;
	
	public TonnetzTranslationPath get_path() {
		return _path;
	}

	public MusificationMovingCell(PlanarUnfoldedTonnetz tonnetz, TonnetzTranslationPath path){
		_path = path;
		_past_pos = new ArrayList<ArrayList<Integer>>();
		//_midi_player = midi_player;
		_tonnetz = tonnetz;
		init();
	}
	
	public int pitch_class_to_pitch(int pitch_class){
		if (_past_pos.size() == 1) {
			_last_pitch = 60+pitch_class;
			return (60+pitch_class);
		}
		int last_pc = _last_pitch%12;
		int distance = Math.abs(pitch_class - last_pc);		
		int mi_interval = Interval.MI(distance, 12);
		if ((_last_pitch+mi_interval)%12 == pitch_class){
			return _last_pitch+mi_interval;
		} else {
			assert ((_last_pitch-mi_interval)%12 == pitch_class) : "Erreur pc to pitch";
			return _last_pitch-mi_interval;
		}
	}
	
	public void play(long duration){
		//int pitch_class = _tonnetz.xy_coord_to_pitch_class(new int[]{_x,_y});
		int pitch_class = _tonnetz.get_PC(_cell_coords);
		int pitch_to_play = pitch_class_to_pitch(pitch_class);
		//_midi_player.playNote(0, pitch_to_play, 0, duration);
		MidiPlayer.getInstance().playNote(0, pitch_to_play, 0, duration);
		_last_pitch = pitch_to_play;
	}
	
	public void update(int i){
		_past_pos.add(new ArrayList<Integer>(_cell_coords));
		for (int j=0;j<_cell_coords.size();j++){
			_cell_coords.set(j, _cell_coords.get(j)+_path.get(i).get(j));
		}
//		_x=_x+_path.get(i).get(0);
//		_y=_y+_path.get(i).get(1);
	}
	
	public void init(){
		_past_pos.clear();
		_cell_coords = new ArrayList<Integer>();
		for (int i=0;i<3;i++){
			_cell_coords.add(0);
		}
//		_x = 0;
//		_y = 0;
		//_past_pos.add(new int[]{_x,_y});
	}
	
	public void start(Parameters p){
		init();
		_musification_thread = new MusificationThread(this,p);
		_musification_thread.start();
	}
	
	public void stop(){
		_musification_thread.end();
		_past_pos.clear();
	}
	
	public boolean is_at(int x, int y){
		int[] coords = PlanarUnfoldedTonnetz.get_2gen_coords(_cell_coords);
		assert coords.length == 2 : "three dimensional unfolding";
		
		if (x==coords[0] && y==coords[1]) return true;
		return false;
	}
	
	public boolean has_passed_by(int x, int y){
		
		for (ArrayList<Integer> past_pos : _past_pos){
			int[] past_coords = PlanarUnfoldedTonnetz.get_2gen_coords(past_pos);
			if (past_coords[0] == x && past_coords[1] == y){
				return true;
			}
		}
		return false;
	}
}
