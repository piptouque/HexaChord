package Model.Music;

import java.util.ArrayList;

import Interface.GraphFrame;
import Interface.GridFrame;
import Interface.DisplayReceiver;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Path.TonnetzTranslationPath;
import Utils.FileUtils;

public class Parameters {
	
	// Parameters
	private String _file_name="";
//	private boolean _model = true;
	private int _trace_length_to_draw = 0;
	private boolean _draw_extra_voice = false;
	private boolean _draw_pitch_once = false;
	private boolean _draw_subgrid = false;
	private boolean _display_graph = false;
	private MusificationMovingCell _musificationMovingCell;
	private boolean _musification = false;
	//private MidiPlayer _midi_player;
	
	private ArrayList<GridFrame> _frame_list = new ArrayList<GridFrame>();
	private static boolean _real_time_trajectory;



	// Model
	private PosPitchSetStream _colStream;
	private ArrayList<Z12PlanarUnfoldedTonnetz> _Z12_triangular_unfolded_tonnetz_list;
	
	// Interface
	private GraphFrame _graph_frame = null;
	private DisplayReceiver _display_receiver_from_sequencer;
	private DisplayReceiver _display_receiver_from_KB;
	
	private static Parameters singleton = null;
	

	private Parameters(){
		_Z12_triangular_unfolded_tonnetz_list = Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList();
	}
	
	public static Parameters getInstance(){
		if (singleton == null) {
			singleton = new Parameters();
		}
		return singleton;
	}

	public static boolean isRealTimeTrajectory() {
		return _real_time_trajectory;
	}
	
	public void set_real_time_trajectory(boolean real_time_trajectory){
		_real_time_trajectory = real_time_trajectory;
	}
	
	public ArrayList<GridFrame> get_frame_list() {
		return _frame_list;
	}

	public int get_trace_length_to_draw() {
		return _trace_length_to_draw;
	}

	public void set_trace_length_to_draw(int _draw_previous_chord) {
		this._trace_length_to_draw = _draw_previous_chord;
	}

	public boolean is_draw_pitch_once() {
		return _draw_pitch_once;
	}

	public void set_draw_pitch_once(boolean _draw_pitch_once) {
		this._draw_pitch_once = _draw_pitch_once;
	}

	public boolean is_draw_extra_voice() {
		return _draw_extra_voice;
	}

	public void set_draw_extra_voice(boolean _draw_extra_voice) {
		this._draw_extra_voice = _draw_extra_voice;
	}

	public void switch_draw_extra_voice(){
	if (_draw_extra_voice)
		_draw_extra_voice =false;
	else _draw_extra_voice = true;
}

	public boolean is_draw_subgrid() {
		return _draw_subgrid;
	}


	public void set_draw_subgrid(boolean _draw_subgrid) {
		this._draw_subgrid = _draw_subgrid;
	}
	
	public void switch_draw_subgrid() {
		_draw_subgrid = !_draw_subgrid;
	}

	public boolean is_display_graph() {
		return _display_graph;
	}

	public void set_display_graph(boolean _display_graph) {
		this._display_graph = _display_graph;
	}

	public PosPitchSetStream get_colStream() {
		return _colStream;
	}

	public void set_colStream(PosPitchSetStream colStream) {
		_colStream = colStream;
	}
	
	public void colStream_listens_display_receiver_from_sequencer(){
		_display_receiver_from_sequencer.add_midi_player_listener(_colStream);
	}
	
	public void colStream_no_listens_display_receiver_from_sequencer(){
		_display_receiver_from_sequencer.remove_midi_player_listener(_colStream);
	}

	public ArrayList<Z12PlanarUnfoldedTonnetz> get_Z12_triangular_unfolded_tonnetz_list() {
		return _Z12_triangular_unfolded_tonnetz_list;
	}

	public void set_Z12_triangular_unfolded_tonnetz_list(ArrayList<Z12PlanarUnfoldedTonnetz> _tonnetz_list) {
		this._Z12_triangular_unfolded_tonnetz_list = _tonnetz_list;
	}
	
	public DisplayReceiver get_display_receiver_from_sequencer() {
		return _display_receiver_from_sequencer;
	}

	public void set_display_receiver_from_sequencer(DisplayReceiver _note_receiver) {
		this._display_receiver_from_sequencer = _note_receiver;
	}

	public GraphFrame get_graph_frame() {
		return _graph_frame;
	}

	public void set_graph_frame(GraphFrame _graph_frame) {
		this._graph_frame = _graph_frame;
	}


	public String get_file_name() {
		return _file_name;
	}

	public String get_song_name() {
		return FileUtils.get_real_name(_file_name);
	}

	public void set_file(String file_name) {
		this._file_name = file_name;
	}

	public MusificationMovingCell get_musificationMovingCell() {
		return _musificationMovingCell;
	}

	public void create_musificationMovingCell(PlanarUnfoldedTonnetz tonnetz, TonnetzTranslationPath path) {
		this._musificationMovingCell = new MusificationMovingCell(tonnetz, path);
	}

	public boolean is_musification() {
		return _musification;
	}

	public void set_musification(boolean _musification) {
		this._musification = _musification;
	}

	public DisplayReceiver get_display_receiver_from_KB() {
		return _display_receiver_from_KB;
	}

	public void set_display_receiver_from_KB(DisplayReceiver _display_receiver_from_KB) {
		this._display_receiver_from_KB = _display_receiver_from_KB;
	}

//	public MidiPlayer get_midi_player() {
//		return MidiPlayer.getInstance();
//		//return _midi_player;
//	}

//	public void set_midi_player(MidiPlayer _midi_player) {
//		this._midi_player = _midi_player;
//	}
	
//	public void update_pos_from_sequencer() {
//		_colStream.set_current_key(MidiFilePlayer.getInstance().get_sequencer().getTickPosition());
//		if (!_real_time_trajectory)
//			for (GridFrame frame : _frame_list) frame.path_pos_update(MidiFilePlayer.getInstance().get_sequencer().getTickPosition());
//	}

}
