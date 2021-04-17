package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.data.category.DefaultCategoryDataset;

import com.lowagie.text.pdf.PdfPublicKeySecurityHandler;

import Utils.MessageInfo;

import Interface.CircleFrame;
import Interface.GridFrame;
import Interface.InfoBox;
import Interface.KBReceiver;
import Interface.MidiDeviceInBox;
import Interface.DisplayReceiver;
import Model.Music.PitchClassSet;
import Model.Music.PitchSetStream;
import Model.Music.MusificationMovingCell;
import Model.Music.Parameters;
import Model.Music.PitchSet;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Model.Music.TriadPitchClassSet;
import Model.Music.Tonnetze.FoldedGraphTonnetz;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.TIChordComplex;
import Model.Music.Tonnetze.UTonnetzCoordList;
import Model.Music.Tonnetze.UTonnetzEdgeCoord;
import Model.Music.Tonnetze.UTonnetzVertexCoord;
import Model.Music.Tonnetze.Z12FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z7FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z7PlanarUnfoldedTonnetz;
import Path.PitchClassMappingTree;
import Path.SquareGridCoordPath;
import Path.TonnetzTranslationPath;
import Utils.BarDiagram;
import Utils.Binomial;
import Utils.DrawComplex;
import Utils.FileUtils;
import Utils.FramePicture;
import Utils.IntegerBag;
import Utils.MidiExtern;
import Utils.MidiFilePlayer;
import Utils.MidiParser;
import Utils.MidiPlayer;
import Utils.Table;
import Utils.TransformedSequence;
import Utils.Wiki;
//import jdk.nashorn.internal.ir.annotations.Ignore;



public class HexaChord implements KeyListener, ComponentListener, ActionListener, ChangeListener {
	
	private static Boolean export_in_out_dir = false; // mettre à true pour écrire les midi dans le dossier out. Non compatible avec HexaChord.app
	
	private static final int N_TRANSLATION = 0;
	private static final int NE_TRANSLATION = 0;
	private static final int ROTATION = 0;
	private static final int ORIGINE_TONNETZ = 10;
	private static final int DESTINATION_TONNETZ = 10;
	private static final boolean REAL_TIME_TRAJECTORY = true;
	private static final int _MIDI_FILE_ROUND_TICK=0;
	
	
	//private static int TRACE_LENGTH = -40;
	private static int TRACE_LENGTH = -160;
	
	public static boolean _external_KB_ON=false;
	public static boolean _is_recording = false;
	
	private String _midifile_name;
	
	private boolean _display_graph = false;
	private boolean _sustain_ON = true;
	
	private static HexaChord singleton = null;
	private static int RESIDUS_MAX_SIZE = 8;


	// Model
	private Parameters _parameters;
	
	private int _N;
	private HashSet<Integer> _window_pitchs = new HashSet<Integer>();
	private Z12PlanarUnfoldedTonnetz _chordTonnetz; // derived:
	private MidiFilePlayer _midi_file_player;

	// Control & view

	private InfoBox _infoBox;

//	private ArrayList<GridFrame> _frame_list;
	private HashSet<PlanarUnfoldedTonnetz> _tonnetz_list;
	private CircleFrame _circle_1_frame;

	private CircleFrame _circle_5_frame;
	private boolean _display_3D = false;
	
	private ArrayList<FoldedGraphTonnetz> _harmo_tonnetz_list;
	
	public static HexaChord getInstance(){
		if (singleton == null){
			singleton = new HexaChord();
		}
		return singleton;
	}

	private void playChord() {
		// if (_midi_extra_voice) {
		// _midiChordSeq.get(_pos).add_pitch_class_chord(n, N)
		// }
//		if (model_midi_on)
//			_modelChordSeq.play_current_chord(_midi_player);
			_parameters.get_colStream().play_current_chord(MidiPlayer.getInstance());
//			_parameters.get_colStream().play_current_chord(_parameters.get_midi_player());
//			_modelColStream.play_current_chord(_midi_player);
	}

	private HexaChord() {
				
//		System.out.println("HEAP d�but = "+Runtime.getRuntime().totalMemory());
//		_harmo_tonnetz_list.add(FoldedTonnetz.getZ12FoldedTonnetzList().get(3));
		Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList();
		
		_harmo_tonnetz_list = new ArrayList<FoldedGraphTonnetz>();
		_harmo_tonnetz_list.add(Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList().get(8));
		_harmo_tonnetz_list.add(Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList().get(12));
		_harmo_tonnetz_list.add(Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList().get(16));
		//_harmo_tonnetz_list.add(FoldedTonnetz.getZ12FoldedTonnetzList().get(20));
		_harmo_tonnetz_list.add(Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList().get(28));;
		
		
		
		Z12PlanarUnfoldedTonnetz t = Z12PlanarUnfoldedTonnetz.getTonnetz(3,4,5);
		_parameters = Parameters.getInstance();		
		//_parameters.set_midi_player(new MidiPlayer());
		_parameters.set_real_time_trajectory(REAL_TIME_TRAJECTORY);
		
		musification_process(t);
//		hamiltonian_process();
		
		
		
		//_frame_list = new ArrayList<GridFrame>();
		_tonnetz_list = new HashSet<PlanarUnfoldedTonnetz>();
		
		
		_N = t.get_N();
		
		_infoBox = InfoBox.getInstance();
		_infoBox.buttons_addActionListener(this);
		_midi_file_player = MidiFilePlayer.getInstance();
		
		open_file_choose_window(_infoBox._demo_file_chooser);
		
		
		
//		if (_midifile_name == null){
//			String pp = null;
//			try {
//				pp = (new File(".")).getCanonicalPath();
//			} catch (IOException e3) {
//				// TODO Auto-generated catch block
//				e3.printStackTrace();
//			}
//			File dir = new File("/"+pp+"/input_files/");
//			File file = dir.listFiles()[FILE_NUMBER];
//			_midifile_name = file.toString();
//			file_init(file);
//			//random_stream_init();
//		} else {
//			System.out.println("WTF !");
//		}
		
		
		// INFOBOX CONFIG POUR TEST
		_infoBox._n_translation_field.setText(String.valueOf(N_TRANSLATION));
		_infoBox._ne_translation_field.setText(String.valueOf(NE_TRANSLATION));
		_infoBox._rotation_field.setText(String.valueOf(ROTATION));
		//_infoBox._origin_tonnetz_box.setSelectedIndex(ORIGINE_TONNETZ);
		//_infoBox._destination_tonnetz_box.setSelectedIndex(DESTINATION_TONNETZ);
	}
	
	public void random_stream_init(){
		//_parameters.set_colStream(new PosPitchSetStream(PitchSetStream.get_random_stream(10000, 4)));
		_parameters.colStream_no_listens_display_receiver_from_sequencer();
		_parameters.set_colStream(new PosPitchSetStream(PitchSetStream.get_random_stream(10000, 3, 12)));
		_parameters.colStream_listens_display_receiver_from_sequencer();
		_parameters.set_file(_parameters.get_colStream().get_name());
		_parameters.get_colStream().to_midi_file(true);
	}
	
	
	public void file_init(File file){
				
		_parameters.set_display_receiver_from_sequencer(new DisplayReceiver(this,false));
		//_parameters.get_note_receiver().add_midi_player_listener(_infoBox);
		
		_midi_file_player.init(file,_parameters.get_display_receiver_from_sequencer());
		
		_midifile_name = file.getName();
		_parameters.set_file(FileUtils.get_real_name(file));
		//_parameters.get_midi_player().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
		MidiPlayer.getInstance().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
		_infoBox.update_cursor(0);
		_infoBox.set_file_name_to_display(_midifile_name);
		
		MidiParser.stream_generator_thread(_midi_file_player.get_sequence(), _sustain_ON, _MIDI_FILE_ROUND_TICK);

	}
	
	public void stream_init(PitchSetStream brut_col_stream, int round){
		
		brut_col_stream.tail_reduction();
		_parameters.colStream_no_listens_display_receiver_from_sequencer();
		if (brut_col_stream.size()>0){
			_parameters.set_colStream(new PosPitchSetStream(brut_col_stream).rounded_PosColStream(round));			
		}
		_parameters.colStream_listens_display_receiver_from_sequencer();
		_parameters.get_colStream().set_current_key(_parameters.get_colStream().firstKey());
		_parameters.get_colStream().setLength_in_microseconds(_midi_file_player.get_sequence().getMicrosecondLength());
		_parameters.get_colStream().add_pos_pitch_set_stream_listener(_infoBox);
		System.out.println(_midifile_name+" loaded. Track duration : "+_parameters.get_colStream().get_duration_in_seconds()+" seconds");
		//System.out.println("STREAM : "+_parameters.get_colStream());
		//System.out.println("PC STREAM : "+_parameters.get_colStream().to_STColStream());
		//System.out.println("ROUNDED PCS STREAM : "+_parameters.get_colStream().rounded_PosColStream(10).to_STColStream());
		//PosPitchSetStream rounded = _parameters.get_colStream().rounded_PosColStream(10);
		
		frame_list_reinit();
		if (_parameters.get_graph_frame()!=null){
			_parameters.get_graph_frame().reinit();
		}
		if (_circle_1_frame != null) _parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_1_frame);
		if (_circle_5_frame != null) _parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_5_frame);
		if (_display_3D) {
			remove_3D_frame();
			display_3D_complex();
		}
	}

	
	public void musification_process(PlanarUnfoldedTonnetz t){
		TonnetzTranslationPath tonnetzTranslationPath = new TonnetzTranslationPath("JIM");

		
		
		// JIM
//		tonnetzTranslationPath.add_down(5);
//		tonnetzTranslationPath.add_down_left(2);
//		tonnetzTranslationPath.add_up_left(2);
//		tonnetzTranslationPath.add(3,6,0);
//		tonnetzTranslationPath.add_down_right(2);
//		tonnetzTranslationPath.add_up_right(2);
//		tonnetzTranslationPath.add_up(1);
//		tonnetzTranslationPath.add_up_left(4);
//		tonnetzTranslationPath.add_up(1);
//		tonnetzTranslationPath.add_up_right(2);
//		tonnetzTranslationPath.add_down_right(2);
//		tonnetzTranslationPath.add(-4,2,0);
		tonnetzTranslationPath.add_up(6);
		tonnetzTranslationPath.add_down_right(3);
		tonnetzTranslationPath.add_up_right(3);
		tonnetzTranslationPath.add_down(7);
		
		_parameters.create_musificationMovingCell(t, tonnetzTranslationPath);

	}
	
	public void hamiltonian_process(){
		
//		PitchSet initial_pitch_set = new PitchSet();
//		initial_pitch_set.add(60);
//		initial_pitch_set.add(64);
//		initial_pitch_set.add(67);
//		PitchSetStream path_stream = t.translation_path_to_col_stream(tonnetzTranslationPath, initial_pitch_set);
//		path_stream.set_name("test_path");
//		path_stream.to_midi_file();
		
		String motif = "RPRLPRPLPRLPLRLRPLRLPLRL";
		PitchSetStream hamiltonian = new PitchSetStream("ham_"+motif);
		int period = 24/motif.toCharArray().length;
		String transfo_word = "";
		for (int i=0;i<period;i++){
			transfo_word = transfo_word.concat(motif);
		}
		char[] transfo_list = transfo_word.toCharArray();
		System.out.println("et voila : "+transfo_word);
		 
		

		TriadPitchClassSet initial_triad = new TriadPitchClassSet(0, 4, 7);
		TriadPitchClassSet current_triad = initial_triad;
		Long current_time = (long)0;
		Long pas = (long)1000;
		
		hamiltonian.put(current_time, new PitchSetWithDuration(new PitchSet(),pas));
		current_time = current_time+pas;
		hamiltonian.put(current_time, new PitchSetWithDuration(current_triad.to_default_pitch_set(),pas));
		
		for (int i=0;i<transfo_list.length;i++){
			current_time = current_time+pas;
			
			switch (transfo_list[i]){
				case 'L' : current_triad = current_triad.get_L();break;
				case 'R' : current_triad = current_triad.get_R();break;
				case 'P' : current_triad = current_triad.get_P();break;
			}
				
			hamiltonian.put(current_time, new PitchSetWithDuration(current_triad.to_default_pitch_set(), pas));
		}
		
		System.out.println("hamiltonian : "+hamiltonian);
		System.out.println("hamiltonian size = "+hamiltonian.size());


	}

	// SPATIAL HARMONIZATION

	public void harmonization(){
		
		int voice_number = 2;
		String result = "";
		//File out = new File("out/chorales/results.txt");
		
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		System.out.println("pp : "+pp);
		File dir = new File("/"+pp+"/Midi/");

		//for (String file : dir.list()){
		for (File file : dir.listFiles()){
			if (file.toString().charAt(0)!='.'){
				file_init(file);	// Attention : file_init ne forme plus le stream depuis la maj SwingWorker pour la GUI
				PitchSetStream reduced_stream = new PosPitchSetStream(MidiParser.stream_generator_without_track(_midi_file_player.get_sequence(), voice_number,true));
				PitchSetStream target_voice = new PosPitchSetStream(MidiParser.stream_generator_with_one_track(_midi_file_player.get_sequence(), voice_number,true));
				reduced_stream.tail_adjustment();
				target_voice.tail_adjustment();
				reduced_stream.set_name(file.toString());
				
				result = result + file + reduced_stream.rounded_ColStream(RESIDUS_MAX_SIZE).print_voice_compliance(target_voice.rounded_ColStream(RESIDUS_MAX_SIZE))+"\n";				
			}
		}
		
		System.out.println("RESULT : \n"+result);

		
		
		
		
		//reduced_stream.rounded_ColStream(RESIDUS_MAX_SIZE).spatial_harmonization(target_voice.rounded_ColStream(RESIDUS_MAX_SIZE),voice_number);
		System.exit(0);

	}
	
	public void musification(){
		if (_parameters.is_musification()){
			_parameters.get_musificationMovingCell().stop();
		} else {
			_parameters.set_musification(true);
			MusificationMovingCell cell = _parameters.get_musificationMovingCell();
			cell.start(_parameters);
			frames_repaint();			
		}
	}
	
	
	
	public void sequence_transformation(PlanarUnfoldedTonnetz origine_tonnetz, PlanarUnfoldedTonnetz destination_tonnetz, int n_translation, int ne_translation, int rotation){
		System.out.println("STREAM T : "+_parameters.get_colStream());
		String transformed_sequence_name = _midifile_name+"_"+origine_tonnetz.toString()+"_N"+n_translation+"_NE"+ne_translation+"_R"+rotation+"_"+destination_tonnetz.toString();
		
		TreeMap<Long,ArrayList<Integer>> origine_pitch_list_stream = new TreeMap<Long,ArrayList<Integer>>();
		SquareGridCoordPath path = _parameters.get_colStream().compute_tonnetz_coord_path(origine_tonnetz,origine_pitch_list_stream);
		path.rotate(rotation);
		path.translate(n_translation,ne_translation);
		TreeMap<Long,ArrayList<Integer>> destination_pitch_list_stream = destination_tonnetz.get_pitch_stream_from_path(path);
		//System.out.println("destination pitch stream : "+destination_pitch_list_stream);
		
		PitchClassMappingTree pc_mapping_tree = new PitchClassMappingTree();
		
		//System.exit(0);
		pc_mapping_tree.build_mapping(origine_pitch_list_stream,destination_pitch_list_stream);
		
		TransformedSequence new_sequence = TransformedSequence.transforme_with_pc_mapping(_midi_file_player.get_sequence(),pc_mapping_tree,transformed_sequence_name);
		PitchSetStream transformed_col_stream = MidiParser.stream_generator(new_sequence, _sustain_ON);
		//System.out.println("transformed col stream : "+transformed_col_stream);
		transformed_col_stream.tail_reduction();
		_parameters.colStream_no_listens_display_receiver_from_sequencer();
		_parameters.set_colStream(new PosPitchSetStream(transformed_col_stream));
		_parameters.colStream_listens_display_receiver_from_sequencer();
		//_parameters.get_midi_player().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
		MidiPlayer.getInstance().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
		frame_list_reinit();

		_midi_file_player.set_sequence_in_sequencer(new_sequence,transformed_sequence_name);
		//new_sequence.export_as_midi();

	}
	
//	public void listen_to_path(){
//		for (GridFrame f : _frame_list) {
//			if (f != null) {
//				
//			}
//		}
//	}
	
	
	public void path_inversion(){
		System.out.println("calcul path debut");
		//TonnetzCoordPath tonnetz_coord_path = _parameters.get_colStream().calcul_path(Z12PlanarUnfoldedTonnetz.getTonnetz(3,4,5));
		//_parameters.get_colStream().get_nr_path().get_inverted_path();
//		System.out.println("normal path : "+Table.toString(_parameters.get_colStream().get_nr_path()));
//		TonnetzCoordPath invert_path = new TonnetzCoordPath(_parameters.get_colStream().get_nr_path());
//		invert_path.inversion();
//		System.out.println("invert path : "+Table.toString(invert_path));
//		System.out.println("renorm path : "+Table.toString(_parameters.get_colStream().get_nr_path()));
//		System.out.println("calcul path fin");
		
		/*----Test zone - transformation----*/
		
//		_midi_file_player.edit_sequence(_parameters.get_colStream(),invert_path,Z12PlanarUnfoldedTonnetz.getTonnetz(3,4,5));
		_infoBox.update_cursor(0);
		_parameters.colStream_no_listens_display_receiver_from_sequencer();
		_parameters.set_colStream(new PosPitchSetStream(MidiParser.stream_generator(_midi_file_player.get_sequence(), _sustain_ON)));
		_parameters.colStream_listens_display_receiver_from_sequencer();
		//_parameters.get_midi_player().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
		MidiPlayer.getInstance().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));

		/*----END Test zone - transformation----*/

	}
	
	public Parameters get_parameters() {
		return _parameters;
	}
	
	public void set_infoBox(InfoBox _infoBox) {
		this._infoBox = _infoBox;
	}

//	public void re_init_file(){
//		file_init(_parameters.get_file());
//	}
    
    public void frames_repaint(){

    	for (GridFrame f : _parameters.get_frame_list()) if (f != null) f.coords_update();
    	for (GridFrame f : _parameters.get_frame_list()) if (f != null) f.repaint();
    	if (_parameters.get_graph_frame()!=null) _parameters.get_graph_frame().update();
    }
    
    
    public void frame_list_reinit(){
    	for (GridFrame f : _parameters.get_frame_list()) if (f != null) f.init(_parameters.get_colStream());
    	frames_repaint();
    }
    
    public void update_derived_model() {
    	
    	update_window_pitchs();
    	for (GridFrame f : _parameters.get_frame_list()) f.coords_update();
    	
    	if (_parameters.get_graph_frame()!=null) _parameters.get_graph_frame().update();
    	frames_repaint();
    	
//    	if (_parameters.get_colStream().get_current_col().to_PitchClassSet(_N).size()==3){
//    		for (GridFrame f : _parameters.get_frame_list()) f.subgrid_update();
//    	}    	
    	
    	
    }
    	
	private void inc_pos() {
		_parameters.get_colStream().inc_current_key();
		_infoBox.update_cursor((float)_parameters.get_colStream().get_current_key()/_parameters.get_colStream().get_duration());
		//System.out.println("Voici le current col : "+_parameters.get_colStream().get_current_col()+" "+_parameters.get_colStream().get_current_key());
		frames_repaint();
		//update_derived_model();
	}

	private void dec_pos() {
		_parameters.get_colStream().dec_current_key();
		_infoBox.update_cursor((float)_parameters.get_colStream().get_current_key()/_parameters.get_colStream().get_duration());
//		_modelChordSeq.dec_pos();
		//System.out.println("Voici le current col : "+_modelColStream.get_current_col()+" "+_modelColStream.get_current_key());
		frames_repaint();
		//update_derived_model();
	}

//	public SimplicialTonnetz getCurrentTonnetz() {
//		if (_dynamicTonnetz)
//			return _chordTonnetz;
//		else
//			return _tonnetz;
//	}

	private void update_window_pitchs() {
		if (_parameters.get_trace_length_to_draw() != 0) {
			_window_pitchs.clear();
			if (_parameters.get_trace_length_to_draw() < 0) {
				for (int i = _parameters.get_trace_length_to_draw(); i <= 0; i++) {
					_window_pitchs.addAll(_parameters.get_colStream().get_relative_STChord(i));
				}
			} else {
				for (int i = 0; i <= _parameters.get_trace_length_to_draw(); i++)
					_window_pitchs.addAll(_parameters.get_colStream().get_relative_STChord(i));
			}
		}
	}

	public int getCurrentChordOrbit(PlanarUnfoldedTonnetz t) {
		//return t.getOrbit(_modelChordSeq.get_current_chord().to_STChord(_N));
		return t.getOrbit(_parameters.get_colStream().get_current_col().to_PitchClassSet(_N));
	}

	public Z12PlanarUnfoldedTonnetz getChordTonnetz() {
		return _chordTonnetz;
	}

	public void switch_draw_subgrid() {
		for (GridFrame f : _parameters.get_frame_list()) {
			f.switch_draw_subgrid();
			f.repaint();
		}
	}

	public void new_frame(){
		new_frame(Z12PlanarUnfoldedTonnetz.getTonnetz(3,4,5));
	}

	public void new_frame(PlanarUnfoldedTonnetz t) {
		GridFrame grid_frame;
		if (!_tonnetz_list.contains(t)) {
			grid_frame = new GridFrame(t,_parameters.isRealTimeTrajectory());
			_tonnetz_list.add(t);
			_parameters.get_frame_list().add(grid_frame);
		} else
			System.err.println("Tonnetz deja affich�");

		update_derived_model();
		System.out.println("Display KTI"+t.get_generators());
	}

	public void remove_frame(PlanarUnfoldedTonnetz t) {
		//_infoBox.check_box_set_selected(t, false);
		if (_tonnetz_list.contains(t)) {
			Iterator<GridFrame> iter = _parameters.get_frame_list().iterator();
			boolean b = true;
			while (iter.hasNext() && b) {
				GridFrame f = iter.next();
				if (f.get_tonnetz() == t) {
					f.dispose();
					_parameters.get_frame_list().remove(f);
					b = false;
				}
			}
			_tonnetz_list.remove(t);
		}
	}
	
	public void open_or_close_frame(PlanarUnfoldedTonnetz t){
		if (_tonnetz_list.contains(t)){
			remove_frame(t);
		} else {
			new_frame(t);
		}
	}

	public ArrayList<GridFrame> get_frame_list() {
		return _parameters.get_frame_list();
	}

	public DisplayReceiver get_note_receiver() {
		return _parameters.get_display_receiver_from_sequencer();
	}

	public MidiFilePlayer get_midi_file_player() {
		return _midi_file_player;
	}

	public InfoBox get_infoBox() {
		return _infoBox;
	}

	public String get_midifile() {
		return _midifile_name;
	}

	public HashSet<Integer> get_window_pitchs() {
		return _window_pitchs;
	}

	public boolean is_display_graph() {
		return _display_graph;
	}

	public void switch_display_graph() {
		if (!_display_graph){
			_display_graph = true;
			if (_parameters.get_graph_frame() == null){
				_parameters.set_graph_frame(Z12PlanarUnfoldedTonnetz.dot_builder());
			}else {
				_parameters.get_graph_frame().setVisible(true);
			}
		}else {
			_display_graph = false;
			_parameters.get_graph_frame().setVisible(false);
		}
	}
		
	public void display_abs_compliance(){
		ArrayList<TIChordComplex> tonnetz_list = new ArrayList<TIChordComplex>();
		//tonnetz_list.addAll(TonnetzChordComplex.getZ12Tonnetz_3_ChordComplexList());
		tonnetz_list.addAll(TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3));
		tonnetz_list.addAll(TIChordComplex.getZ12Tonnetz_n_ChordComplexList(4));

		_parameters.get_colStream().Z12FoldedTonnetz_abs_compliance_display("", tonnetz_list);
	}

	public void display_large_compliance(){
		//_parameters.get_colStream().Z12HexaTonnetz_Hcompliance_display("",false);
		_parameters.get_colStream().rounded_PosColStream(10).Z12HexaTonnetz_Hcompliance_display("",false);
	}
	
	public void display_constraint_compliance(){
		//_parameters.get_colStream().Z12HexaTonnetz_Hcompliance_display("",true);
		_parameters.get_colStream().rounded_PosColStream(10).Z12HexaTonnetz_Hcompliance_display("",true);
	}

	// Key Events Listeners
	@Override
	public void keyPressed(KeyEvent evt) {
		switch (evt.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			exit();
			break;
		case KeyEvent.VK_K:
		case KeyEvent.VK_ENTER:
			inc_pos();
			playChord();
			// System.out.println("Voici les candidats : "+_tonnetz.closer_pitch_class(_midiChordSeq.get(_pos).to_STChord(_N)));
			break;
		case KeyEvent.VK_J:
			dec_pos();
			playChord();
			break;
		case KeyEvent.VK_P:
			switch_KB_listener_mode();
			// playChord(); uncomment to play current chord - disables external keyboard function 
			break;
		case KeyEvent.VK_SPACE:
			play_pause_processing();
//			get_midi_file_player().play_file(_parameters.get_colStream().get_current_key());
//			_infoBox.requestFocus();
//			_infoBox.switch_play_button_label();
			break;
		case KeyEvent.VK_W:

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

			
			break;
//		case KeyEvent.VK_T:
//			display_hexa_compliance(2);
//			break;
		case KeyEvent.VK_F:
			System.out.println("Frames : "+_parameters.get_frame_list());
			break;
		case KeyEvent.VK_R:
			_midi_file_player.get_sequencer().setTickPosition(1000);
			break;
		case KeyEvent.VK_I:
			for (GridFrame f : _parameters.get_frame_list())
				f.saveFrameAsJepg();
			//FramePicture.save(_infoBox, "info_box", "infobox", "gif");
				//f.saveFrameAsPdf(); //ne fais pas du vectoriel et imprime sur une page A4 :s
			break;
		case KeyEvent.VK_C:
			switch_3D_display();
			break;
//		case KeyEvent.VK_Z: // PANIC
//			stop_processing();
//			MidiFilePlayer.getInstance().reset_sequencer();
//			file_init(_hard_file);
//			break;
//		case KeyEvent.VK_O:
//			if (_circle_1_frame == null) display_circle_1();
//			else {
//				remove_circle_1_frame();
//			}
//			break;
		case KeyEvent.VK_S:
			_midi_file_player.export_sequence_as_midi(export_in_out_dir);
			break;
		case KeyEvent.VK_N:
			_parameters.switch_draw_subgrid();
			break;
//		case KeyEvent.VK_D:
//			set_dynamicTonnetz(true);
//			break;
		case KeyEvent.VK_M:
			//System.out.println("Memory : Used heap size = "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(long)100000);
			musification();
			break;
		case KeyEvent.VK_RIGHT:
			if (evt.isControlDown()) {
				_midi_file_player.get_sequencer()
						.setTickPosition(
								_midi_file_player.get_sequencer()
										.getTickPosition() + 1000);
			} else
				for (GridFrame f : _parameters.get_frame_list())
					f.get_p().keyPressed(evt);
			break;
		case KeyEvent.VK_LEFT:
			if (evt.isControlDown()) {
				_midi_file_player.get_sequencer()
						.setTickPosition(
								_midi_file_player.get_sequencer()
										.getTickPosition() - 1000);
			} else
				for (GridFrame f : _parameters.get_frame_list())
					f.get_p().keyPressed(evt);
			break;
		case KeyEvent.VK_DOWN:
			if (evt.isControlDown()) {
				_midi_file_player.get_sequencer().setTempoInBPM(
						_midi_file_player.get_sequencer().getTempoInBPM() / 2);
			} else
				for (GridFrame f : _parameters.get_frame_list())
					f.get_p().keyPressed(evt);
			break;
		case KeyEvent.VK_UP:
			if (evt.isControlDown()) {
				_midi_file_player.get_sequencer().setTempoInBPM(
						_midi_file_player.get_sequencer().getTempoInBPM() * 2);
			} else
				for (GridFrame f : _parameters.get_frame_list())
					f.get_p().keyPressed(evt);
			break;
		default:
			for (GridFrame f : _parameters.get_frame_list())
				f.get_p().keyPressed(evt);
		}
	}

	public void remove_circle_1_frame() {
		_circle_1_frame.setVisible(false);
		_circle_1_frame = null;
	}

	public void remove_circle_5_frame() {
		_circle_5_frame.setVisible(false);
		_circle_5_frame = null;
	}

	public void switch_3D_display(){
		if (_display_3D){
			remove_3D_frame();
		} else {
			display_3D_complex();
		}
	}

	public void remove_3D_frame() {
		DrawComplex.close();
		_display_3D = false;
	}

	private void display_3D_complex() {
		DrawComplex.draw_sequence_CS(_parameters.get_colStream().to_pc_set_list());
		//DrawComplex.draw_sequence_CS(STIntervallicStructure.get_major_IS().get_transpositions());
		//DrawComplex.draw_sequence_CS(STIntervallicStructure.get_major_IS().get_transpositions_and_inversions());
		//DrawComplex.draw_sequence_CS(STIntervallicStructure.get_diminished_IS().get_transpositions());
		//DrawComplex.draw_sequence_CS(STIntervallicStructure.get_augmented_IS().get_transpositions());
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{6,6});
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{2,5,5});
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{3,4,5});
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{1,1,10});
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{4,3,3,2});
		//STIntervallicStructure is = new STIntervallicStructure(new int[]{2,3,4,3});
		//DrawComplex.draw_sequence_CS(is.get_transpositions_and_inversions());
		_display_3D = true;
	}

	private void display_circle_1() {
		_circle_1_frame = new CircleFrame(Scale.get_chromatic_scale(),"circle of semitones");
		_parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_1_frame);		
	}
	private void display_circle_5() {
		_circle_5_frame = new CircleFrame(Scale.get_fifth_list(),"circle of fitfhs");
		_parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_5_frame);		
	}

	
	@Override
	public void keyReleased(KeyEvent evt) {
	}

	@Override
	public void keyTyped(KeyEvent evt) {
	}

	@Override
	public void componentShown(ComponentEvent evt) {
	}

	@Override
	public void componentHidden(ComponentEvent evt) {
	}

	@Override
	public void componentMoved(ComponentEvent evt) {
	}

	@Override
	public void componentResized(ComponentEvent evt) {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		
		if (e.getSource() == _infoBox._play_but){
			if (_external_KB_ON){
				System.out.println("number of tracks:"+get_midi_file_player().get_sequence().getTracks().length
						  +" - size of track 0 : "+get_midi_file_player().get_sequence().getTracks()[0].size());
				//System.out.println("size of track 0 : "+get_midi_file_player().get_sequence().getTracks()[0].size());
			}
			System.out.println("STREAM P : "+_parameters.get_colStream());
			play_pause_processing();
		}
		
		if (e.getSource() == _infoBox._stop_but){
			if (_is_recording){
				
				get_midi_file_player().get_sequencer().stopRecording();
				MidiExtern.close_record_transmitter();
				_midi_file_player.close_record_receiver();
				
				_is_recording=false;
				_infoBox._play_but.setEnabled(true);
				Sequence tmp = _midi_file_player.get_sequence();
				post_record_sequence_scaling(tmp);
				MidiParser.stream_generator_thread(_midi_file_player.get_sequence(), _sustain_ON,0);
				_midi_file_player.export_sequence_as_midi(export_in_out_dir);
				_midi_file_player.connect_playback_transmitter(get_note_receiver());
				
			} else {
				stop_processing();
			}
		}

		if (e.getSource() == _infoBox._record_but){
			
			set_empty_colStream_KB_mode();
			Sequence sequence=null;
			MidiExtern.set_record_transmitter();
			_midi_file_player.set_record_receiver();
			MidiExtern.connect_record_transmitter(_midi_file_player.get_record_receiver());
			frame_list_reinit();
			//MidiExtern.set_display_transmitter();
			//MidiExtern.connect_KB_display_transmitter(_parameters.get_display_receiver_from_sequencer());
			//Parameters.getInstance().set_display_receiver_from_KB(new DisplayReceiver(HexaChord.getInstance()));
			//MidiExtern.connect_KB_display_transmitter(_parameters.get_display_receiver_from_KB());
			
		    try{
		    	  sequence = new Sequence(Sequence.PPQ, 960);
		      } catch (Exception ex) { 
		          ex.printStackTrace(); 
		      }
		    
		    
		      Track track0 = sequence.createTrack();
		      try {
		    	byte[] byte_table = new byte[]{7,-95,32};
				track0.add(new MidiEvent(new MetaMessage(81, byte_table, byte_table.length), 0));
				byte_table = new byte[]{0,0};
				track0.add(new MidiEvent(new MetaMessage(89, byte_table, byte_table.length), 0));
				byte_table = new byte[]{4,2,24,8};
				track0.add(new MidiEvent(new MetaMessage(88, byte_table, byte_table.length), 0));
				byte_table = new byte[]{};
				track0.add(new MidiEvent(new MetaMessage(47, byte_table, byte_table.length), 0));
		      } catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
		    	  e1.printStackTrace();				
		      }
		      
		      Track track1 = sequence.createTrack();
		      
		      try {
				track1.add(new MidiEvent(new ShortMessage(176, 0, 0, 0), 0));
				track1.add(new MidiEvent(new ShortMessage(176, 0, 32, 0), 0));
				track1.add(new MidiEvent(new ShortMessage(192, 0, 0, 0), 0));
		    	byte[] byte_table = new byte[]{83,97,110,115,32,116,105,116,114,101};
				track1.add(new MidiEvent(new MetaMessage(3, byte_table, byte_table.length), 0));
				track1.add(new MidiEvent(new ShortMessage(176, 0, 7, 127), 0));
		      } catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		      }
	    	  get_midi_file_player().set_sequence_in_sequencer(sequence,"record");
	    	  get_midi_file_player().get_sequencer().setTickPosition(0);
	    	  //get_midi_file_player().get_sequencer().setMicrosecondPosition(0);
	    	  get_midi_file_player().get_sequencer().recordEnable(track1, -1);
	    	  get_midi_file_player().get_sequencer().startRecording();
		      _is_recording=true;
		      _infoBox._stop_but.setEnabled(true);
		      
		}

		if (e.getSource() == _infoBox._load_demo_file_but){
			open_file_choose_window(_infoBox._demo_file_chooser);
		}

		if (e.getSource() == _infoBox._load_user_file_but){
			open_file_choose_window(_infoBox._user_file_chooser);
		}
		
		if (e.getSource() == _infoBox._KB_mode_but && !_external_KB_ON){
			switch_KB_listener_mode();
		}
	
		if (e.getSource() == _infoBox._midi_file_mode_but && _external_KB_ON){
				switch_KB_listener_mode();
		}
		
		if (e.getSource() == _infoBox._trace_but){
							
			if (_parameters.get_trace_length_to_draw()!=0){
				//System.out.println("->no trace");
				_parameters.set_trace_length_to_draw(0);
				_infoBox._trace_but.setText("Trace on");
				_parameters.set_draw_pitch_once(false);
				//_infoBox._all_pitch_but.setText("one pitch");
			} else {
				//System.out.println("-> trace");
				if (_parameters.is_draw_pitch_once())
					_parameters.set_trace_length_to_draw(TRACE_LENGTH);
				else {
					// 2 lignes suivantes � commenter dans le rare cas ou on veut !draw_pitch_once && trace (il y en a 2 autres � d�commenter dans MusicInterviewer3)
					_parameters.set_draw_pitch_once(true);
					//_infoBox._all_pitch_but.setText("All pitch");
					_infoBox._trace_but.setText("Trace off");	
					_parameters.set_trace_length_to_draw(TRACE_LENGTH);
				}
			}
		}
		
		if (e.getSource() == _infoBox._extra_voice_but){
			_parameters.switch_draw_extra_voice();
			if (_parameters.is_draw_extra_voice()) _infoBox._extra_voice_but.setText("No extra voice");
			else _infoBox._extra_voice_but.setText("Extra voice");		
		}
		
//		if (e.getSource() == _infoBox._subgrid_but){
//			_parameters.switch_draw_subgrid();
//			switch_draw_subgrid();
//			if (_parameters.is_draw_subgrid()) _infoBox._subgrid_but.setText("Normal Grid");
//			else _infoBox._subgrid_but.setText("Subgrid");
//		}
				
		if (e.getSource() == _infoBox._compute_comp_but){
			_parameters.get_colStream().Z12FoldedTonnetz_compliance_display(_parameters.get_song_name(),_infoBox._compactness_degree_box.getSelectedIndex()+1,TIChordComplex.getZ12Tonnetz_n_ChordComplexList(_infoBox._dim_complex_box.getSelectedIndex()+2),true);
		}

		if (e.getSource() == _infoBox._abs_Compliance_but){
			display_abs_compliance();
		}

		if (e.getSource() == _infoBox._HLarge_Compliance_but){
			display_large_compliance();
		}

		if (e.getSource() == _infoBox._HConstraint_Compliance_but){
			display_constraint_compliance();
		}

		if (e.getSource() == _infoBox._graph_but){
			switch_display_graph();
			if (_parameters.is_display_graph()) _infoBox._graph_but.setText("No graph");
			else _infoBox._graph_but.setText("Display graph");
		}

		if (e.getSource() == _infoBox._circle_but_1){
			if (_circle_1_frame == null) display_circle_1();
			else remove_circle_1_frame();
		}

		if (e.getSource() == _infoBox._circle_but_5){
			if (_circle_5_frame == null) display_circle_5();
			else remove_circle_5_frame();
		}

		if (e.getSource() == _infoBox._3D_complex_but){
			switch_3D_display();
		}

		if (e.getSource() == _infoBox._musification_but){
			musification();
		}

		if (e.getSource() == _infoBox._chroma_tonnetz_box){
			JComboBox cb = (JComboBox)e.getSource();
	        int tonnetz_index = cb.getSelectedIndex(); 
			System.out.println("display Tonnetz "+tonnetz_index);
			open_or_close_frame(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(tonnetz_index));
		}

		if (e.getSource() == _infoBox._hepta_tonnetz_box){
			Scale scale = (Scale) _infoBox._tonality_box.getSelectedItem();
			JComboBox cb = (JComboBox)e.getSource();
			STIntervallicStructure is = (STIntervallicStructure) cb.getSelectedItem();
	        //int tonnetz_index = cb.getSelectedIndex(); 
			System.out.println("display Tonnetz hepta "+is+" "+scale);
			//open_or_close_frame(Z7PlanarUnfoldedTonnetz.getZ7HexaTonnetzList().get(tonnetz_index));
			open_or_close_frame(new Z7PlanarUnfoldedTonnetz(is,scale));
		}
		
		if (e.getSource() == _infoBox._complex_box){
			JComboBox cb = (JComboBox)e.getSource();
			STIntervallicStructure is = (STIntervallicStructure) cb.getSelectedItem();
			if(is.get_N()==12){
				_infoBox.check_tona_box(_infoBox._tona_box, 12);
//				System.out.println("display Tonnetz "+tonnetz_index);
				open_or_close_frame(Z12PlanarUnfoldedTonnetz.getTonnetz(is.get_list()));
			}
			if(is.get_N()==7){
				_infoBox.check_tona_box(_infoBox._tona_box, 7);
//				System.out.println("display Tonnetz "+tonnetz_index);
//				open_or_close_frame(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(tonnetz_index));
			}
		}
		
		if (e.getSource() == _infoBox._disp_complex_but){
			STIntervallicStructure is = (STIntervallicStructure) _infoBox._complex_box.getSelectedItem();
			if(is.get_N()==12){
				open_or_close_frame(Z12PlanarUnfoldedTonnetz.getTonnetz(is.get_list()));
			}
			if(is.get_N()==7){
				open_or_close_frame(new Z7PlanarUnfoldedTonnetz(is,(Scale) _infoBox._tona_box.getSelectedItem()));
			}
		}
		
		if (e.getSource() == _infoBox._hide_complexes_but){
			System.out.println("list : "+_tonnetz_list);
			HashSet<PlanarUnfoldedTonnetz> _tonnetz_list_tmp = new HashSet<PlanarUnfoldedTonnetz>(_tonnetz_list);
			for (PlanarUnfoldedTonnetz t : _tonnetz_list_tmp) remove_frame(t);
		}
		
		if (e.getSource() == _infoBox._origin_complex_box){
			JComboBox cb = (JComboBox)e.getSource();
			STIntervallicStructure is = (STIntervallicStructure) cb.getSelectedItem();
			if(is.get_N()==12){
				_infoBox.check_tona_box(_infoBox._origin_tona_box, 12);
			}
			if(is.get_N()==7){
				_infoBox.check_tona_box(_infoBox._origin_tona_box, 7);
			}
		}

		if (e.getSource() == _infoBox._destination_complex_box){
			JComboBox cb = (JComboBox)e.getSource();
			STIntervallicStructure is = (STIntervallicStructure) cb.getSelectedItem();
			if(is.get_N()==12){
				_infoBox.check_tona_box(_infoBox._destination_tona_box, 12);
			}
			if(is.get_N()==7){
				_infoBox.check_tona_box(_infoBox._destination_tona_box, 7);
			}
		}
		
		if (e.getSource() == _infoBox._path_transformation_but){
			transformation_processing();
		}

		if (e.getSource() != _infoBox._3D_complex_but){
			HexaChord.getInstance().frames_repaint();
			_infoBox.requestFocus();			
		}
		
	}
	
	private static void post_record_sequence_scaling(Sequence sequence){
		long offset_desired_for_first_note = 50;
		long first_tick=0;
		for (Track track : sequence.getTracks()){
			for (int i=0;i<track.size();i++){
				long tick = track.get(i).getTick();
				if (first_tick==0){
					if (tick>0) first_tick = tick;
				} else {
					if (tick>0 && tick<first_tick) first_tick = tick;
				}
			}
		}
		for (Track track : sequence.getTracks()){
			for (int i=0;i<track.size();i++){
				long original_tick = track.get(i).getTick();
				if (original_tick>0){
					track.get(i).setTick(original_tick-first_tick+offset_desired_for_first_note);
				}
			}
		}
		//Sequence scaled_sequence = new Sequence
	}
	
	private byte[] build_byte_table(int[] t){
		byte[] byte_table = new byte[]{};
		for (int i=0;i<t.length;i++){
			byte_table[i]=(byte) t[i];
		}
		return byte_table;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == _infoBox._speedSlider){
			JSlider slider = (JSlider)e.getSource();
			_midi_file_player.get_sequencer().setTempoFactor(2*(float)slider.getValue()/20);
			HexaChord.getInstance().frames_repaint();
			_infoBox.requestFocus();
		}

		if (e.getSource() == _infoBox._cursor){
			if (_infoBox.is_jumped()) {
				JSlider slider = (JSlider)e.getSource();
				if (!slider.getValueIsAdjusting()){
					_midi_file_player.jump(slider.getValue());
					_parameters.get_colStream().set_current_key((int)(_parameters.get_colStream().get_duration()*((float)slider.getValue()/100)));
				}
				HexaChord.getInstance().frames_repaint();
				_infoBox.requestFocus();
			}
		}
	}
	
	public File get_file_from_name(String name){
		
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		pp = pp+"/input_files/"+name;
		File file = new File(pp);
		return file;
	}
	
	File _hard_file;
	public void open_file_choose_window(JFileChooser file_chooser){
		stop_processing();
		if (file_chooser.showOpenDialog(_infoBox) == JFileChooser.APPROVE_OPTION) {
            File file = file_chooser.getSelectedFile();
            _hard_file = file;
			System.out.println("Loading "+file.getName()+".\n");
			file_init(file);
			frame_list_reinit();
            //This is where a real application would open the file.
            //log.append("Opening: " + file.getName() + "." + newline);
        } else {
        	
            if(get_midi_file_player().get_sequencer() == null){
            	exit();
            }
        }
	}
	
	public File open_file_save_window(JFileChooser file_chooser){
		stop_processing();
		File file = null;
		if (file_chooser.showSaveDialog(_infoBox) == JFileChooser.APPROVE_OPTION) {
            file = file_chooser.getSelectedFile();
			System.out.println("Save as file: "+file.getName()+".\n");
        } else {        	
            if(get_midi_file_player().get_sequencer() == null){
            	exit();
            }
        }
		return file;
	}
	
	public void play_pause_processing(){
		Sequencer sequencer = get_midi_file_player().get_sequencer();
		if (sequencer != null){
			InfoBox.getInstance().requestFocus();
			if (sequencer.isRunning()) {
				System.out.println("pause at tick : "+_parameters.get_colStream().get_current_key());
				InfoBox.getInstance()._play_but.setText("play") ;
				sequencer.stop();
			}
			else {
				if (_parameters.get_colStream().is_last_key()){
					InfoBox.getInstance().update_cursor(0);
					_parameters.get_colStream().set_current_key(_parameters.get_colStream().firstKey());
					sequencer.setTickPosition(0);
				}
				InfoBox.getInstance()._play_but.setText("pause");
				sequencer.start();
			}			
		}
	}
	
	public void stop_processing(){
		if (get_midi_file_player().get_sequencer() != null){
			if (get_midi_file_player().get_sequencer().isRunning()){
				get_midi_file_player().stop_file();
			}
			
			_infoBox._play_but.setText("play");
			
			//_parameters.set_note_receiver(new NoteReceiver(this));
			_midi_file_player.init(_parameters.get_display_receiver_from_sequencer());
			_infoBox.update_cursor(0);
			_parameters.get_colStream().set_current_key(_parameters.get_colStream().firstKey());
			frame_list_reinit();
			if (_parameters.get_graph_frame()!=null){
				_parameters.get_graph_frame().reinit();
			}
		} 
	}
	
	public void transformation_processing(){
		
		PlanarUnfoldedTonnetz origin_tonnetz = null,destination_tonnetz = null;
		
		STIntervallicStructure origin_is = (STIntervallicStructure) _infoBox._origin_complex_box.getSelectedItem();
		STIntervallicStructure destination_is = (STIntervallicStructure) _infoBox._destination_complex_box.getSelectedItem();
		if(origin_is.get_N()==12){
			origin_tonnetz = Z12PlanarUnfoldedTonnetz.getTonnetz(origin_is.get_list());
		}
		if(origin_is.get_N()==7){
			origin_tonnetz = new Z7PlanarUnfoldedTonnetz(origin_is,(Scale) _infoBox._origin_tona_box.getSelectedItem());
		}
		if(destination_is.get_N()==12){
			destination_tonnetz = Z12PlanarUnfoldedTonnetz.getTonnetz(destination_is.get_list());
		}
		if(destination_is.get_N()==7){
			destination_tonnetz = new Z7PlanarUnfoldedTonnetz(destination_is,(Scale) _infoBox._destination_tona_box.getSelectedItem());
		}

//		PlanarUnfoldedTonnetz origin_tonnetz = PlanarUnfoldedTonnetz.getZ7AndZ12HexaTonnetzList().get(_infoBox._origin_tonnetz_box.getSelectedIndex());
//		PlanarUnfoldedTonnetz destination_tonnetz = PlanarUnfoldedTonnetz.getZ7AndZ12HexaTonnetzList().get(_infoBox._destination_tonnetz_box.getSelectedIndex());
		int n_translation = Integer.parseInt(_infoBox._n_translation_field.getText());
		int ne_translation = Integer.parseInt(_infoBox._ne_translation_field.getText());
		int rotation = Integer.parseInt(_infoBox._rotation_field.getText());
		sequence_transformation(origin_tonnetz, destination_tonnetz, n_translation, ne_translation, rotation);
	}
	
	public void exit(){
		//_parameters.get_midi_player().midi_close();
		MidiPlayer.getInstance().midi_close();
		if (_parameters.get_display_receiver_from_sequencer() != null) _parameters.get_display_receiver_from_sequencer().close();
		_midi_file_player.midi_close();
		_infoBox.dispose();
		for (GridFrame f : _parameters.get_frame_list())
			f.dispose();
	}
	
	public void switch_KB_listener_mode(){
		if (_external_KB_ON){
			switch_KB_listener_OFF();
		} else {
			switch_KB_listener_ON();
		}
	}
	
	public void switch_KB_listener_ON(){
		
		if (!_infoBox._KB_mode_but.isSelected()) _infoBox._KB_mode_but.setSelected(true);
		stop_processing();
		InfoBox.getInstance()._play_but.setEnabled(false);
		InfoBox.getInstance()._stop_but.setEnabled(false);
		InfoBox.getInstance()._load_demo_file_but.setEnabled(false);
		InfoBox.getInstance()._load_user_file_but.setEnabled(false);
		InfoBox.getInstance()._record_but.setEnabled(true);
		_external_KB_ON=true;
		set_empty_colStream_KB_mode();

		if (_parameters.get_graph_frame()!=null){
			_parameters.get_graph_frame().reinit();
		}
		if (_circle_1_frame != null) _parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_1_frame);
		if (_circle_5_frame != null) _parameters.get_colStream().add_pos_pitch_set_stream_listener(_circle_5_frame);
		if (_display_3D) {
			remove_3D_frame();
			display_3D_complex();
		}

		frame_list_reinit();
		if(!MidiDeviceInBox.getInstance().isVisible()) MidiDeviceInBox.getInstance().setVisible(true);
	}
	
	public void set_empty_colStream_KB_mode(){
		PitchSetStream stream = new PitchSetStream();
		stream.put((long)0, new PitchSetWithDuration(new PitchClassSet(),1));
		PosPitchSetStream pos_pcs_stream = new PosPitchSetStream(stream);
		_parameters.colStream_no_listens_display_receiver_from_sequencer();
		_parameters.set_colStream(pos_pcs_stream);
		//_parameters.colStream_listens_note_receiver();
		_parameters.get_colStream().set_current_key(1);
	}
	
	public void switch_KB_listener_OFF(){
		if (!_infoBox._midi_file_mode_but.isSelected()) _infoBox._midi_file_mode_but.setSelected(true);
		MidiExtern.close_display_transmitter();
		_external_KB_ON=false;
		if(MidiDeviceInBox.getInstance().isVisible()) MidiDeviceInBox.getInstance().setVisible(false);
		InfoBox.getInstance()._play_but.setEnabled(true);
		InfoBox.getInstance()._stop_but.setEnabled(true);
		InfoBox.getInstance()._load_demo_file_but.setEnabled(true);
		InfoBox.getInstance()._load_user_file_but.setEnabled(true);
		InfoBox.getInstance()._record_but.setEnabled(false);
		open_file_choose_window(_infoBox._demo_file_chooser);
		
	}
	

	// Program entry
	public static void main(String[] args) {
		
//		String pp = null;
//		try {
//			pp = (new File(".")).getCanonicalPath();
//		} catch (IOException e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//		}
//		File dir = new File("/"+pp+"/input_files/");
//		File file = dir.listFiles()[7];
//		System.out.println(file.getAbsolutePath());

//		_parameters.set_note_receiver(new NoteReceiver(this));
//		_parameters.get_note_receiver().add_midi_player_listener(_infoBox);
//		MidiFilePlayer p = MidiFilePlayer.getInstance();
//  		p.reset_sequencer();
//  		p.midi_close();
//  		p.setSequenceAndSequencer(file);
//  		p.open_sequencer();
//  		p.play_file();

		
//		_midifile_name = file.getName();
//		_parameters.set_file(FileUtils.get_real_name(file));
		//_parameters.get_midi_player().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
//		MidiPlayer.getInstance().set_tick_length(60/(_midi_file_player.get_sequencer().getTempoInBPM()*_midi_file_player.get_sequence().getResolution()));
//		_infoBox.update_cursor(0);
//		_infoBox.set_file_name_to_display(_midifile_name);
		
//		MidiParser.stream_generator_thread(_midi_file_player.get_sequence(), _sustain_ON);


		InfoBox.getInstance();
		HexaChord.getInstance();
    }

	public int get_N() {
		return _N;
	}
	
	public CircleFrame get_circle_1_frame() {
		return _circle_1_frame;
	}

	public CircleFrame get_circle_5_frame() {
		return _circle_5_frame;
	}

	public void set_circle_1_frame(CircleFrame circle_frame) {
		_circle_1_frame = circle_frame;
	}

	public void set_circle_5_frame(CircleFrame circle_frame) {
		_circle_5_frame = circle_frame;
	}


}
