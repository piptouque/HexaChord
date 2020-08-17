package Interface;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Main.HexaChord;
import Model.Music.Note;
import Model.Music.Parameters;
import Model.Music.PitchClassSet;
import Model.Music.PosPitchSetStreamListener;
import Model.Music.Scale;

class CirclePannel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	private ArrayList<Integer> _scale;
	
    private String _font_name  = "Times New Roman";
    private int    _font_style = Font.PLAIN;
    
    public CirclePannel(ArrayList<Integer> scale){
    	_scale = scale;
    }

	public void paintComponent(Graphics g){

		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(2.f));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font(_font_name, _font_style, get_font_size()));
		
		g2d.drawOval(get_left_blank(), get_up_blank(), get_circle_diameter(), get_circle_diameter());
		
		ArrayList<int[]> circle_to_draw_center_list = get_circle_to_draw_center_list();
		PitchClassSet pcs = Parameters.getInstance().get_colStream().get_current_STChord();
		
		int[] xs = new int[pcs.size()];
		int[] ys = new int[pcs.size()];
		int[] note_names = new int[pcs.size()];
		int z=0;

		for (int i=0;i<_scale.size();i++){
			int[] c = circle_to_draw_center_list.get(i);
			
			g2d.setColor(Color.white);
			g2d.fillOval(c[0]-get_node_radius(), c[1]-get_node_radius(), get_node_radius()*2, get_node_radius()*2);
			g2d.setColor(Color.black);
			g2d.drawOval(c[0]-get_node_radius(), c[1]-get_node_radius(), get_node_radius()*2, get_node_radius()*2);

	        //String label = _scale.get(i).toString();
			String label = Note.get_name(_scale.get(i));
	        if (label != "") {
	        	float sx = 0.5f *  g.getFontMetrics().stringWidth(label);
	        	float sy = 0.25f * g.getFontMetrics().getHeight();
	        	g.drawString(label,(int)(c[0]-sx),(int)(c[1]+sy));
	        }
	        //if (pcs.contains(i)){
	        if (pcs.contains(_scale.get(i))){
				xs[z]=c[0];
				ys[z]=c[1];
				note_names[z]=i;
				//centers.add(c);
				z++;
	        }
		}
		
		if (pcs.size()>0){
			g2d.setStroke(new BasicStroke(4.f));
			g2d.drawPolygon(xs, ys, pcs.size());
			g2d.setColor(Color.yellow);
			g2d.fillPolygon(xs, ys, pcs.size());
			
			for (int i=0;i<xs.length;i++){
				g2d.setColor(Color.yellow);
				g2d.fillOval(xs[i]-get_node_radius(), ys[i]-get_node_radius(), get_node_radius()*2, get_node_radius()*2);
				g2d.setColor(Color.black);
				g2d.drawOval(xs[i]-get_node_radius(), ys[i]-get_node_radius(), get_node_radius()*2, get_node_radius()*2);

				//String label = _scale.get(note_names[i]).toString();
				String label = Note.get_name(_scale.get(note_names[i]));
		        if (label != "") {
		        	float sx = 0.5f *  g.getFontMetrics().stringWidth(label);
		        	float sy = 0.25f * g.getFontMetrics().getHeight();
		        	g.drawString(label,(int)(xs[i]-sx),(int)(ys[i]+sy));
		        }
			}
		}
	}
	
	private int get_circle_diameter(){
		return Math.min(getWidth(), getHeight()) - getWidth()/4;
	}
	
	private int get_node_radius(){
		return get_circle_diameter()/16;
	}
	
	private int get_font_size(){	
		return getHeight()/25;
	}
	
	private ArrayList<int[]> get_circle_to_draw_center_list(){
		ArrayList<int[]> list = new ArrayList<int[]>();
		double a;
		int[] center;

		for (int i=0;i<_scale.size();i++){
			center = new int[2];
			a =  (Math.PI/2)-i*2*Math.PI/_scale.size();
			center[0]=(int) (get_left_blank()+get_circle_diameter()/2+Math.cos(a)*get_circle_diameter()/2);
			center[1]=(int) (get_up_blank()+get_circle_diameter()/2-Math.sin(a)*get_circle_diameter()/2);
			list.add(center);
		}
		return list;
	}
	
	private int get_left_blank(){
		return getWidth()/8+getWidth()/64;
	}
	
	private int get_up_blank(){
		return getHeight()/8;
	}

	public ArrayList<Integer> get_scale() {
		return _scale;
	}

	public void set_scale(Scale _scale) {
		this._scale = _scale;
	}
	
}

class CloseCircleWindowProcessing implements WindowListener{

	private CircleFrame _circle_frame;
	
	public CloseCircleWindowProcessing(CircleFrame circle_frame){
		_circle_frame = circle_frame;
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		_circle_frame.setVisible(false);
		if (_circle_frame==HexaChord.getInstance().get_circle_1_frame()) HexaChord.getInstance().set_circle_1_frame(null);
		if (_circle_frame==HexaChord.getInstance().get_circle_5_frame()) HexaChord.getInstance().set_circle_5_frame(null);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

public class CircleFrame extends JFrame implements PosPitchSetStreamListener{

	private static final long serialVersionUID = 1L;
	
	public CircleFrame(ArrayList<Integer> scale, String name){
		super(name);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(300,300));
        setResizable(true);
        if (scale.get(1)==1) setLocation(500,50);
        else setLocation(500,350);
        setFocusable(true);
        setContentPane(new CirclePannel(scale));
        
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                setSize(new Dimension(get_size(), get_size()));
                super.componentResized(e);
            }
        });
        addWindowListener(new CloseCircleWindowProcessing(this));
        addKeyListener(HexaChord.getInstance());
        pack();
        setVisible(true);

	}
		
	private int get_size(){
		return Math.min(getWidth(), getHeight());
	}

	@Override
	public void pos_change(long new_pos) {
		repaint();
	}
}
