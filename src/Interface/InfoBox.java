package Interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import Main.HexaChord;
import Model.Music.MidiPlayerListener;
import Model.Music.Parameters;
import Model.Music.PosPitchSetStreamListener;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Model.Music.Tonnetze.Z7FoldedGraphTonnetz;
import Model.Music.Tonnetze.Z7PlanarUnfoldedTonnetz;
import Utils.FileUtils;
import Utils.MidiFilePlayer;
import Utils.Table;




public class InfoBox extends JFrame implements KeyListener,PosPitchSetStreamListener {
	
	private static int Z12_TONNETZ_VIZU = 10;
	private static int Z7_TONNETZ_VIZU = 2;
	private static int TONALITY_VIZU = 0;

	private static final long serialVersionUID = 1L;
	private Parameters _parameters;
	private JPanel _jpanel;
	private JLabel _file_name_label;
	private JLabel _d_chord_jlabel;
	private JLabel _d_tonnetz_jlabel;
	public JFileChooser _file_chooser;
	public JComboBox _complex_box;
	public JComboBox _chroma_tonnetz_box;
	public JComboBox _hepta_tonnetz_box;
	public JComboBox _tonality_box;
	public JComboBox _tona_box;
	public JComboBox _origin_tonnetz_box;
	public JComboBox _destination_tonnetz_box;
	public JComboBox _origin_complex_box;
	public JComboBox _origin_tona_box;
	public JComboBox _destination_complex_box;
	public JComboBox _destination_tona_box;
	public JButton _play_but;
	public JButton _stop_but;
	public JButton _record_but;
	public JButton _load_file_but;
	
	public JRadioButton _midi_file_mode_but;
	public JRadioButton _KB_mode_but;
	
	public JButton _disp_complex_but;
	public JButton _hide_complexes_but;
	
	public JButton _trace_but;
	//public JButton _all_pitch_but;
	public JButton _extra_voice_but;
	//public JButton _subgrid_but;
	public JButton _1_hexaCompliance_but;
	public JButton _2_hexaCompliance_but;
	public JButton _1_Compliance_but;
	public JButton _2_Compliance_but;
	public JComboBox _compactness_degree_box;
	public JComboBox _dim_complex_box;
	public JButton _compute_comp_but;
	public JButton _abs_Compliance_but;
	public JButton _HLarge_Compliance_but;
	public JButton _HConstraint_Compliance_but;
	public JButton _graph_but;
	public JButton _circle_but_1;
	public JButton _circle_but_5;
	public JButton _3D_complex_but;
	public JButton _musification_but;
	public JButton _path_transformation_but;
	public JSlider _speedSlider;
	public JSlider _cursor;
	
	public JTextField _n_translation_field;
	public JTextField _ne_translation_field;
	public JTextField _rotation_field;
	
	private boolean _jumped = false;
	
	private static InfoBox singleton = null;
	
	public static InfoBox getInstance(){
		if (singleton == null) {
			singleton = new InfoBox();
		}
		return singleton;
	}
	
	public InfoBox() {
		super("HexaChord");

		//_h = h;
		//_h = HexaChord.getInstance();
		//_parameters = p;
		_parameters = Parameters.getInstance();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500,750));
        setResizable(true);
        setLocation(1000,50);
        setFocusable(true);
                
        // Insert a Panel
        
        _jpanel = new JPanel();
        _jpanel.setBorder(new LineBorder(Color.LIGHT_GRAY,3));
        GridBagConstraints c = new GridBagConstraints();
//        _jpanel.add(Box.createRigidArea(new Dimension(10,5)),c);
//        _jpanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //BoxLayout    bl=new BoxLayout(_jpanel,BoxLayout.PAGE_AXIS);
        //_jpanel.setLayout(bl);
        //FlowLayout    bl=new FlowLayout(FlowLayout.CENTER);
        //_jpanel.setLayout(bl);
        
        _jpanel.setLayout(new GridBagLayout());
        _file_name_label = new JLabel("");
        c.gridy=0;
        c.gridwidth=4;
        _jpanel.add(_file_name_label,c);
        
        c.gridy++;
        c.gridx=0;
        
        _speedSlider = new JSlider(0,20,10);
        _speedSlider.setMajorTickSpacing(10);
        _speedSlider.setMinorTickSpacing(1);
        _speedSlider.setPaintTicks(true);
        _speedSlider.setPaintLabels(true);
        _speedSlider.setSnapToTicks(true);
        _cursor = new JSlider();
        _cursor.setValue(0);
        _play_but = new JButton("play");
        _stop_but = new JButton("stop");
        _record_but = new JButton("record");
        _load_file_but = new JButton("select midi file");
        _midi_file_mode_but = new JRadioButton("midi file");
    	_KB_mode_but = new JRadioButton("ext keyboard");
    	ButtonGroup group = new ButtonGroup();
    	group.add(_midi_file_mode_but);
    	group.add(_KB_mode_but);

		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		//System.out.println("pp : "+pp);

		//File dir = new File("/"+pp+"/Midi/");
		//File dir = new File("/"+pp+"/input_midi_files/");
		//File dir = new File("/"+pp+"/test/");
		File dir = new File("/"+pp+"/input_files/");
		System.out.println("Midi file directory : "+dir);
		ArrayList<String> file_list = new ArrayList<String>();
		for (String name : dir.list()) {
			if (FileUtils.is_midi(name)) file_list.add(name);
		}
//		String[] file_table = new String[file_list.size()+1];
//		file_table[0] = "<select a midi file in the list>";
//		for (int i=0;i<file_list.size();i++) file_table[i+1] = file_list.get(i);
//        _file_box = new JComboBox(file_table);
//        _file_box.setSelectedIndex(0);
        _file_chooser = new JFileChooser(dir);
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridwidth=4;
        _jpanel.add(new JLabel("Tempo"),c);
        c.gridy++;
        _jpanel.add(_speedSlider,c);
        
        c.gridy++;
        _jpanel.add(_cursor,c);
        
        c.gridy++;
        c.gridx=0;
        c.gridwidth=1;
        _jpanel.add(_play_but,c);

        c.gridx+=1;
        _jpanel.add(_stop_but,c);

        c.gridx+=1;
        c.gridwidth=2;
        _jpanel.add(_record_but,c);
        _record_but.setEnabled(false);
        c.gridx=0;
        c.gridy++;
        
        _jpanel.add(_load_file_but,c);
        c.gridx+=2;
        c.gridwidth=1;
        _jpanel.add(_midi_file_mode_but,c);
        _midi_file_mode_but.setSelected(true);
        c.gridx+=1;
        
        _jpanel.add(_KB_mode_but,c);
        c.gridy++;
        c.gridx=0;
        c.gridwidth=4;
        _jpanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
        c.gridy++;
        
        c.gridwidth=2;
        c.gridx=0;
        _jpanel.add(new JLabel("Intervallic structure"),c);
        c.gridx=2;
        _jpanel.add(new JLabel("Scale"),c);
        
        c.gridy++;
        
        c.gridx=0;
        
        _complex_box  = new JComboBox(STIntervallicStructure.get_SI_3_12_7_UTF().toArray());
        _complex_box.setSelectedIndex(10);
        _tona_box = new JComboBox(Scale.get_chromatic_scale_list().toArray());
        _tona_box.setSelectedIndex(0);
        _jpanel.add(_complex_box,c);
        c.gridx=2;
        _jpanel.add(_tona_box,c);
        
        c.gridx=0;
        c.gridy+=2;

        _disp_complex_but = new JButton("display/hide complex");
        _hide_complexes_but = new JButton("hide all");
        c.gridwidth=3;
        _jpanel.add(_disp_complex_but,c);
        c.gridx=3;
        c.gridwidth=1;
        _jpanel.add(_hide_complexes_but,c);
        c.gridwidth=2;
        c.gridx=0;
        c.gridy+=2;

        c.gridwidth=4;
        //_jpanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
        c.gridy++;
        c.gridx=0;
        c.gridwidth=2;
        c.gridheight=2;
        c.ipadx=0;
        c.fill=GridBagConstraints.VERTICAL;
                
        _trace_but = new JButton((_parameters.get_trace_length_to_draw()==0) ? "Trace mode":"Trace off");
        _extra_voice_but = new JButton((_parameters.is_draw_extra_voice()) ? "Harmonization OFF":"Harmonization ON");
        //_subgrid_but = new JButton((_parameters.is_draw_subgrid()) ? "Normal Grid":"Subgrid");
        _graph_but = new JButton((_parameters.is_display_graph()) ? "VL space OFF":"VL space ON");
        _circle_but_1 = new JButton("circle 1");
        _circle_but_5 = new JButton("circle 5");
        _3D_complex_but = new JButton("3D complex");
                
        _compactness_degree_box = new JComboBox(get_compactness_string_table(11));
        _compactness_degree_box.setSelectedIndex(1);
        
        _dim_complex_box = new JComboBox(get_dim_complex_int_table(11));
        _dim_complex_box.setSelectedIndex(1);
        
        _abs_Compliance_but = new JButton("absolute compactness");
        _compute_comp_but = new JButton("compute compactness");
        _HLarge_Compliance_but = new JButton("Large Compliance");
        _HConstraint_Compliance_but = new JButton("Constraint Compliance");
        _musification_but = new JButton("Musification");
        _path_transformation_but = new JButton("Compute transformation");
        
        c.fill=GridBagConstraints.HORIZONTAL;
        
        c.gridx=0;
        c.gridy++;
        c.gridwidth=2;
        
        _jpanel.add(_trace_but,c);

        c.gridx=2;
        _jpanel.add(_extra_voice_but,c);
        
        c.gridy++;
//        c.gridx=0;
//        _jpanel.add(_extra_voice_but,c);
        
//        c.gridx++;
//        _jpanel.add(_subgrid_but,c);

        c.gridy++;
        c.gridx=0;
        c.gridwidth=1;
        _jpanel.add(_circle_but_1,c);
        c.gridx=1;
        _jpanel.add(_circle_but_5,c);
        c.gridx=2;
        _jpanel.add(_3D_complex_but,c);
        c.gridx=3;
        //c.gridwidth=2;
        _jpanel.add(_graph_but,c);
        c.gridy++;

        c.gridx=0;
        c.gridy++;
        c.gridwidth=4;
        c.ipady++;
        c.ipady++;
        _jpanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
        c.gridy+=2;
        JLabel vertComp = new JLabel("Vertical compactness",JLabel.CENTER);
        vertComp.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(vertComp,c);

        c.gridy+=2;
        c.gridwidth=2;
        
        JLabel compactness_dimension_label = new JLabel("compactness dimension",JLabel.CENTER);
        vertComp.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(compactness_dimension_label,c);

        c.gridx+=2;
        
        JLabel complex_dimension_label = new JLabel("complexes dimension",JLabel.CENTER);
        vertComp.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(complex_dimension_label,c);
        
        c.gridx=0;
        c.gridy+=2;

        _jpanel.add(_compactness_degree_box,c);
                
        c.gridx+=2;
        _jpanel.add(_dim_complex_box,c);
        
        c.gridx=0;
        
        c.gridy+=2;
        
        c.gridwidth=4;
        _jpanel.add(_compute_comp_but,c);
        c.gridy+=2;
        _jpanel.add(_abs_Compliance_but,c);
        c.gridy+=2;
        
        
        _jpanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
        c.gridy+=2;
        JLabel path_trans_label = new JLabel("Trajectory Transformation",JLabel.CENTER);
        path_trans_label.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(path_trans_label,c);

        c.gridy+=2;

        c.gridwidth=2;
        JLabel origin_tonnetz_label = new JLabel("Origin complex",JLabel.CENTER);
        origin_tonnetz_label.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(origin_tonnetz_label,c);
        
        c.gridx+=2;
        JLabel destination_tonnetz_label = new JLabel("Destination complex",JLabel.CENTER);
        destination_tonnetz_label.setFont(new Font("Arial", Font.PLAIN, 14));
        _jpanel.add(destination_tonnetz_label,c);

        c.gridx=0;
        c.gridy+=2;
        c.gridwidth=1;
        
    	_origin_complex_box = new JComboBox(STIntervallicStructure.get_SI_3_12_7_UTF().toArray());
    	_origin_tona_box = new JComboBox(Scale.get_chromatic_scale_list().toArray());
    	_destination_complex_box = new JComboBox(STIntervallicStructure.get_SI_3_12_7_UTF().toArray());
    	_destination_tona_box = new JComboBox(Scale.get_chromatic_scale_list().toArray());

    	_origin_complex_box.setSelectedIndex(10);
    	_destination_complex_box.setSelectedIndex(0);
    	
    	_jpanel.add(_origin_complex_box,c);
    	c.gridx++;
    	_jpanel.add(_origin_tona_box,c);
    	c.gridx++;
    	_jpanel.add(_destination_complex_box,c);
    	c.gridx++;
    	_jpanel.add(_destination_tona_box,c);
    	
//        _origin_tonnetz_box = new JComboBox(PlanarUnfoldedTonnetz.getZ7_and_Z12_HexaTonnetzNameTable());
//        _origin_tonnetz_box.setSelectedIndex(10);
//        _jpanel.add(_origin_tonnetz_box,c);
//
//        c.gridx+=2;
//        _destination_tonnetz_box = new JComboBox(PlanarUnfoldedTonnetz.getZ7_and_Z12_HexaTonnetzNameTable());
//        _destination_tonnetz_box.setSelectedIndex(10);
//        _jpanel.add(_destination_tonnetz_box,c);
        
        c.gridx=0;
        
        c.gridwidth=2;
        c.gridy+=2;
        
        _jpanel.add(new JLabel("Rotation",JLabel.CENTER),c);
        c.gridx+=2;
        _rotation_field = new JTextField("0");
        _jpanel.add(_rotation_field,c);
        
        c.gridx=0;
        c.gridy+=2;
        
        _jpanel.add(new JLabel("North translation",JLabel.CENTER),c);
        c.gridx+=2;
        _n_translation_field = new JTextField("0");
        _jpanel.add(_n_translation_field,c);
        
        c.gridx=0;
        c.gridy+=2;
        
        _jpanel.add(new JLabel("North-east translation",JLabel.CENTER),c);
        c.gridx+=2;
        _ne_translation_field = new JTextField("0");
        _jpanel.add(_ne_translation_field,c);
        
        c.gridx=0;
        c.gridy+=2;
        

        c.gridwidth=4;
        _jpanel.add(_path_transformation_but,c);

        // Sizing and displaying the window
        add(_jpanel);
        //setContentPane(_jpanel);
        
        addKeyListener(this);
        pack();
        setVisible(true);

        }
	
	public void set_chordToDisplay(String _toDisplay) {
		_d_chord_jlabel.setText(_toDisplay);
	}
	
	public void set_tonnetzToDisplay(String _toDisplay) {
		_d_tonnetz_jlabel.setText(_toDisplay);
	}

	public void set_file_name_to_display(String _toDisplay) {
		_file_name_label.setText(_toDisplay);
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		HexaChord.getInstance().keyPressed(evt);
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		HexaChord.getInstance().keyReleased(evt);
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		HexaChord.getInstance().keyTyped(evt);
	}
	
	public synchronized void update_cursor(float f) {
		_jumped = false;
		_cursor.setValue((int)(f*100));
		_jumped = true;
	}

	public boolean is_jumped() {
		return _jumped;
	}
	
	public void set_jumped(boolean j){
		_jumped = j;
	}
	
	public String[] get_compactness_string_table(int n){
		String[] string_table = new String[n];
		for (int i =1;i<=n;i++){
			string_table[i-1]=i+"-compactness";
		}
		return string_table;
	}
	
	public Integer[] get_dim_complex_int_table(int n){
		Integer[] integer_table = new Integer[n];
		for (int i =1;i<=n;i++){
			integer_table[i-1]=i;
		}
		return integer_table;
	}
	
	
	public Parameters get_parameters() {
		return _parameters;
	}
	
	public void buttons_addActionListener(HexaChord h){
		_play_but.addActionListener(h);
        _stop_but.addActionListener(h);
        _record_but.addActionListener(h);
        _load_file_but.addActionListener(h);
        _midi_file_mode_but.addActionListener(h);
        _KB_mode_but.addActionListener(h);
        _trace_but.addActionListener(h);
        //_all_pitch_but.addActionListener(h);
        _extra_voice_but.addActionListener(h);
        //_subgrid_but.addActionListener(h);
        _compactness_degree_box.addActionListener(h);
        _dim_complex_box.addActionListener(h);
        _graph_but.addActionListener(h);
        _circle_but_1.addActionListener(h);
        _circle_but_5.addActionListener(h);
        _3D_complex_but.addActionListener(h);
        _abs_Compliance_but.addActionListener(h);
        _compute_comp_but.addActionListener(h);
        _HLarge_Compliance_but.addActionListener(h);
        _HConstraint_Compliance_but.addActionListener(h);
        _musification_but.addActionListener(h);
        _speedSlider.addChangeListener(h);
        _cursor.addChangeListener(h);
        _complex_box.addActionListener(h);
        _disp_complex_but.addActionListener(h);
        _hide_complexes_but.addActionListener(h);
        _origin_complex_box.addActionListener(h);
        _destination_complex_box.addActionListener(h);
        _path_transformation_but.addActionListener(h);
	}

	public void check_tona_box(JComboBox tona_box, int N){
		if (N==7){
			tona_box.removeAllItems();
			for (Scale s : Scale.get_diatonic_scales()) tona_box.addItem(s);
		}
		if (N==12){
			tona_box.removeAllItems();
			tona_box.addItem(Scale.get_chromatic_scale());
		}
	}

	@Override
	public void pos_change(long new_pos) {
		update_cursor((float)new_pos/(float)Parameters.getInstance().get_colStream().get_duration());
	}
	
	
}
