package Interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import Model.Music.Parameters;
import Model.Music.Tonnetze.TIChordComplex;
import Utils.FramePicture;



class GraphLayer extends Layer{
	
	
    private ArrayList<Edge> _edges;
    private ArrayList<Node> _nodes;
    private float  _stroke = 2.f;
    private float  _font_size  = 12.f;
    private String _font_name  = "Times New Roman";
    private int    _font_style = Font.PLAIN;
    private GraphInterviewer _m;

	public GraphLayer(JTransformPanel p, GraphInterviewer m, ArrayList<Node> nodes, ArrayList<Edge> edges) {
		super(p);
		_edges = edges;
		_nodes = nodes;
		_m=m;
		// TODO Auto-generated constructor stub
	}
	
    public void drawEdge(Graphics g, Edge edge) {
        g.setColor(_m.edge_to_draw());
        g.drawLine((int)x_convert(edge._x1),(int)y_convert(edge._y1),(int)x_convert(edge._x2),(int)y_convert(edge._y2));
    }
    
    public void drawNode(Graphics g, Node node) {
//    	if (!_m.node_to_draw(X, Y)) return; 
    	g.setColor(_m.get_node_color(node));
//        g.fillOval((int)(pi-_p.get_zoom(25.f)),(int)(pj-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));
        g.fillOval((int)(x_convert(node._x)-_p.get_zoom(25.f)),(int)(x_convert(node._y)-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));
//        g.setColor(_m.get_edge_color());
        g.setColor(_m.get_circle_color(node));
        g.drawOval((int)(x_convert(node._x)-_p.get_zoom(25.f)),(int)(y_convert(node._y)-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));

        String label = _m.get_node_label(node);
        if (label != "") {
        	float sx = 0.5f *  g.getFontMetrics().stringWidth(label);
        	float sy = 0.25f * g.getFontMetrics().getHeight();
        	g.setColor(_m.get_label_color());
        	g.drawString(label,(int)(x_convert(node._x)-sx),(int)(x_convert(node._y)+sy));
        }
    }


	@Override
	public void paint(Graphics2D g) {
		
        g.setStroke(new BasicStroke(_p.get_zoom(_stroke)));
        g.setFont(new Font(_font_name, _font_style, (int)_p.get_zoom(_font_size)));

//		int r, radius;
//        Vector p  = globalToGrid(0,0);
//        Vector e0 = _edges.get(0);

        if (_m.draw_edge()) {
        	for (Edge edge : _edges){
        		drawEdge(g, edge);        		
        	}
        }

        if (_m.draw_node()) {
        	for (Node node : _nodes){
        		drawNode(g, node);
        	}	
        }
	}

	@Override
	public void on_transformation() {
		// TODO Auto-generated method stub
		
	}

	public int x_convert(float x){
		return (int)(80*x+10);
	}

	public int y_convert(float y){
		return (int)(80*y+10);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
	}

}


class Node{
	
	int _id;
	String _label;
	float _x;
	float _y;
	
	public Node(String label, float x, float y, int id){
		_id = id;
		_label = label.substring(1, label.length()-1);
		_x = x;
		_y = y;
	}
	
	public int get_id(){
		return _id;
	}
}

class Edge{

	float _x1;
	float _y1;
	float _x2;
	float _y2;

	public Edge(float x1, float y1, float x2, float y2){
		_x1 = x1;
		_y1 = y1;
		_x2 = x2;
		_y2 = y2;
		
	}	
}

class GraphInterviewer extends GridLayerInterviewer{
	
	private static int COMPLIANCE_DEGREE = 2;
	
	private Parameters _parameters=Parameters.getInstance();
	
	public GraphInterviewer(){
		System.out.println(" graph col stream : "+_parameters.get_colStream().to_STColStream());
	}
	
	public Color edge_to_draw() {
		// TODO Auto-generated method stub
		return Color.black;
	}

	public Color get_node_color(Node node){
		ArrayList<TIChordComplex> most_n_compliant_tonnetz = _parameters.get_colStream().get_current_col().most_n_compliant_tonnetz(TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3),COMPLIANCE_DEGREE);
		if (most_n_compliant_tonnetz.contains(TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3).get(node.get_id()))) {
			return Color.yellow;				
		} else return Color.LIGHT_GRAY;
	}

	public Color get_circle_color(Node node){
		return Color.black;
	}

	public String get_node_label(Node node){
		return node._label;
	}
}


public class GraphFrame extends JFrame implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	private JTransformPanel _jpanel;
	private ArrayList<Node> _nodes;
	private ArrayList<Edge> _edges;
	private GraphInterviewer _gi;
	
	public GraphFrame(String[] list){
		super("voice leading space");
		
		build_elements(list);
		
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(400,500));
        setResizable(true);
        setLocation(500,50);
        setFocusable(true);

        _jpanel = new JTransformGraphPanel();
        _gi = new GraphInterviewer();
        GraphLayer layer = new GraphLayer(_jpanel, _gi, _nodes, _edges);
        _jpanel.add_layer(layer);
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
		add(_jpanel, c);

		// Adding listeners
		addKeyListener(this);

        pack();
        setVisible(true);

	}
	
	public void build_elements(String[] list){
		_nodes = new ArrayList<Node>();
		_edges = new ArrayList<Edge>();
		for (int i=0;i<list.length;i++){
			if (list[i].equals("node")){
				_nodes.add(new Node(list[i+6], Float.parseFloat(list[i+2]),Float.parseFloat(list[i+3]),Integer.parseInt(list[i+1])));
			}
			if (list[i].equals("edge")){
				_edges.add(new Edge(Float.parseFloat(list[i+4]),Float.parseFloat(list[i+5]),Float.parseFloat(list[i+10]),Float.parseFloat(list[i+11])));
			}
		}
	}
	
	public void repaint() {
		_jpanel.repaint();
	}
	
	public void reinit(){
		repaint();
	}
	
	public void update(){
		//System.out.println("most compliant tonnetzs : "+_parameters.get_colStream().get_current_col().most_compliant_tonnetz(_parameters.get_Z12tonnetz_list()));
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_I){
			FramePicture.save(this, "pvl","graph", "png");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
