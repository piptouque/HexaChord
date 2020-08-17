package Interface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import Interface.CloseWindowProcessing;
import Main.HexaChord;
import Interface.MusicInterviewer1;
import Interface.MusicInterviewer2;
import Interface.MusicInterviewer3;
import Interface.MusicSubgrid;
import Model.Music.Parameters;
import Model.Music.PosPitchSetStream;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Path.PosSquareGridCoordPath;
import Path.SquareGridCoordPath;
import Utils.FramePicture;

public class GridFrame extends JFrame implements KeyListener{

	private static final long serialVersionUID = 1L;
	// Control & view
	private JTransformPanel _p;
	private Parameters _parameters;
	private HexaChord _h;
	private PlanarUnfoldedTonnetz _tonnetz;
	
	PosSquareGridCoordPath _pos_path;
//	private boolean _model_seq = false;
//	private boolean _midi_seq = false;

	// Control & view
	private MusicInterviewer1 _m1;
	private MusicInterviewer2 _m2;
	private Interviewer3 _m3;
	
	private boolean _draw_subgrid;
	private MusicSubgrid _sg;
	private HexaGridLayer _l2;
	private HexaGridLayer _l3;
//	private HexaGridLayer _l4;
//	private ArrayList<int[]> _current_model_chord_coords;
	
	private boolean _real_time_path;

	public GridFrame(PlanarUnfoldedTonnetz t, boolean real_time_path) {

		super("Tonnetz : " + t.toString());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // libère la mémoire lorsque la fenêtre est fermée
//		_model_seq = true;
		_tonnetz = t;
		_parameters = Parameters.getInstance();
		_h = HexaChord.getInstance();
		_draw_subgrid = _parameters.is_draw_subgrid();
		JTransformPanel _panel = panel_factory();
		_panel.add_layer(vertices_layer_factory()); // display vertices
		_panel.add_layer(edges_layer_factory()); // display edges
		if (real_time_path){
			_panel.add_layer(real_time_traj_layer_factory(_parameters.get_colStream())); // display model
		} else {
			_pos_path = new PosSquareGridCoordPath(_parameters.get_colStream().compute_tonnetz_coord_path(_tonnetz));
			_panel.add_layer(computed_traj_layer_factory(_pos_path)); // display model
		}
		_real_time_path = real_time_path;
		pack();
		setVisible(true);
	}

	private GridLayer vertices_layer_factory() {
		// First layer: all notes, no edge
		_m1 = new MusicInterviewer1(_tonnetz);
		GridLayer l1 = new HexaGridLayer(_p, _m1, 75.f);
		if (_tonnetz.get_N()-1>(2*_tonnetz.get_directions_count())){
			_sg = new MusicSubgrid(l1, _tonnetz);
		}
		return l1;
	}

	private HexaGridLayer edges_layer_factory() {
		// Second layer: notes of the current chord tonnetz with edges
		_m2 = new MusicInterviewer2(_tonnetz, _sg, _m1);
		if (_draw_subgrid)
			_l2 = new HexaGridLayer(_p, _m2, _sg);
		else
			_l2 = new HexaGridLayer(_p, _m2, 75.f);

		return _l2;
	}

	private HexaGridLayer real_time_traj_layer_factory(PosPitchSetStream stream) {
		_m3 = new MusicInterviewer3(_tonnetz, _sg, _m1, stream);
		if (_draw_subgrid)
			_l3 = new HexaGridLayer(_p, (MusicInterviewer3)_m3, _sg);
		else
			_l3 = new HexaGridLayer(_p, (MusicInterviewer3)_m3, 75.f);
		return _l3;
	}

	private HexaGridLayer computed_traj_layer_factory(PosSquareGridCoordPath path) {
		
		_m3 = new PathInterviewer(path,_tonnetz);
		_l3 = new HexaGridLayer(_p, (PathInterviewer)_m3, 75.f);
		return _l3;
	}

//	private HexaGridLayer midiseq_layer_factory() {
//		_m4 = new MusicInterviewer4(_h, _tonnetz, _sg, _m1, _parameters);
//		if (_draw_subgrid)
//			_l4 = new HexaGridLayer(_p, _m4, _sg);
//		else
//			_l4 = new HexaGridLayer(_p, _m4, 75.f);
//		return _l4;
//	}

	private JTransformPanel panel_factory() {
		addWindowListener(new CloseWindowProcessing(this, _tonnetz));

		if (!_h.get_frame_list().isEmpty())
			setLocation(0, 600);
		setPreferredSize(new Dimension(400, 400));
		setResizable(true);
		// setFocusable(true);

		_p = new JTransformGridPanel(_h, _parameters);
		// Building the GUI
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();

		// External padding (here, all margins are of 10 pixels)
		c.insets = new Insets(10, 10, 10, 10);

		// Insertion of a panel

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH; // for the component to be resized in
											// vertical and horizontal direction
		c.weightx = 1.0; // for the component to take all the horizontal space
		c.weighty = 1.0; // for the component to take all the vertical space
		add(_p, c);

		// Adding listeners
		addKeyListener(this);
		return _p;
	}

	public void switch_draw_subgrid() {
		_draw_subgrid = !_draw_subgrid;
		if (_draw_subgrid) {
			_l2.set_embedding(_sg);
			_l3.set_embedding(_sg);
		} else {
			_l2.set_embedding(75.f);
			_l3.set_embedding(75.f);
		}
	}

	public void repaint() {
		_p.repaint();
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		_h.keyPressed(evt);
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		_h.keyReleased(evt);
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		_h.keyTyped(evt);
	}

	public JTransformPanel get_p() {
		return _p;
	}

	public PlanarUnfoldedTonnetz get_tonnetz() {
		return _tonnetz;
	}

	public void saveFrameAsJepg() {
		FramePicture.save(this, _tonnetz.toString(),"grid", "png");
	}

	// Ne marche pas
	public void saveFrameAsPdf() {
		FramePicture.save_as_pdf(this, _tonnetz.toString(),"grid");
	}

	public void coords_update() {
		if (_tonnetz.get_connected_components_count()==1) {
			_m3.coords_update();
		}
	}
	
	public void subgrid_update(){
		if (_sg != null)
			_sg.update();
	}
	
	// met à jour la position dans path lorsqu'un évènement midi est détecté.
	public void path_pos_update(long key){
		_pos_path.set_current_key(key);
	}

//	public ArrayList<int[]> get_current_model_chord_coords() {
//		return _current_model_chord_coords;
//	}
	
	public void init(PosPitchSetStream stream){
		if (_real_time_path){
			((MusicInterviewer3) _m3).re_init(stream);
		} else {
			_pos_path = new PosSquareGridCoordPath(stream.compute_tonnetz_coord_path(_tonnetz));
			((PathInterviewer) _m3).re_init(_pos_path);
		}
	}

	public void init(SquareGridCoordPath path){
		_pos_path = new PosSquareGridCoordPath(path);
		//((PathInterviewer) _m3).re_init(_pos_path);
	}

//	public void refer_to_path(SquareGridCoordPath tonnetz_coord_path){
//		_m3.set_tonnetz_coord_path(tonnetz_coord_path);
//	}
	
	public String toString(){
		return "F"+_tonnetz.toString();
	}


}

	

