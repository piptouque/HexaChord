package Interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import Utils.Vector;

abstract class GridLayerEmbedding {
	
	abstract public void  gridToLocal(Vector p);

	abstract public void  localToGrid(Vector p);
	
	abstract public float get_node_dist();
	
}

public class GridLayer extends Layer { 

    private ArrayList<Vector> _edges  = new ArrayList<Vector>();
    private ArrayList<Vector> _dedges = new ArrayList<Vector>();
	private Vector _orig;
	private Vector _dir1;
	private Vector _dir2;
    protected int _grid_radius;
    
    protected GridLayerEmbedding _embedding;
	
	private GridLayerInterviewer _m;

	public GridLayer(JTransformPanel p, GridLayerEmbedding e, GridLayerInterviewer m) {
		super(p);
		_m = m;
		_embedding = e;
		on_transformation();
	}

    final public void compute_dir_vectors() {
    	_orig = gridToPixel(0,0);
    	_dir1 = gridToPixel(1,0);
    	_dir2 = gridToPixel(0,1);
    	_dir1.untranslate(_orig);
    	_dir2.untranslate(_orig);
    }

	public void compute_grid_radius() {
		Vector p = new Vector(_p.get_unzoom(_p.get_w_2()),_p.get_unzoom(_p.get_h_2()));
		_grid_radius = (int)Math.ceil(Math.sqrt(2) * p.norm() / _embedding.get_node_dist());
		//System.out.println(_grid_radius);
	}

	@Override
	public void on_transformation() {
		compute_dir_vectors();
		compute_grid_radius();
	}
	
	public void add_edge(int X, int Y) {
//		assert((X!=0)&&(Y!=0));
		_edges.add(new Vector(X,Y));
		compute_dedges();
	}
	
	private void compute_dedges() {
		int i, N=_edges.size();
		Vector v1, v2;
		_dedges = new ArrayList<Vector>();

		for(i=0;i<N-1;i++) {
			v1 = _edges.get(i);
			v2 = _edges.get(i+1);
			_dedges.add(new Vector(v2.x-v1.x,v2.y-v1.y));
		}

		v1 = _edges.get(N-1);
		v2 = _edges.get(0);
		_dedges.add(new Vector(-v2.x-v1.x,-v2.y-v1.y));
		
		for(i=0;i<N-1;i++) {
			v1 = _edges.get(i);
			v2 = _edges.get(i+1);
			_dedges.add(new Vector(v1.x-v2.x,v1.y-v2.y));
		}

		v1 = _edges.get(N-1);
		v2 = _edges.get(0);
		_dedges.add(new Vector(v2.x+v1.x,v2.y+v1.y));
		
		/*
		System.out.println("Edge basis:");
		Iterator<Vector> edge = _edges.iterator();
        while(edge.hasNext()) {
        	Vector e = edge.next();
        	System.out.println("  - "+e.x+" x "+e.y);
        }
		System.out.println("DEdge basis:");
		Iterator<Vector> dedge = _dedges.iterator();
        while(dedge.hasNext()) {
        	Vector e = dedge.next();
        	System.out.println("  - "+e.x+" x "+e.y);
        }*/
	}
	
	public void gridToLocal(Vector p) {
		_embedding.gridToLocal(p);
	}

    public void localToGrid(Vector p) {
    	_embedding.localToGrid(p);
    }

    public void gridToGlobal(Vector p) {
    	gridToLocal(p);
    	_p.localToGlobal(p);
    }

    public void gridToPixel(Vector p) {
    	gridToLocal(p);
    	_p.localToPixel(p);
    }

    public void globalToGrid(Vector p) {
    	_p.globalToLocal(p);
    	localToGrid(p);
    }

    public void pixelToGrid(Vector p) {
    	_p.pixelToGlobal(p);
    	globalToGrid(p);
    }
    
    public Vector gridToLocal(int X, int Y) {
        Vector p = new Vector((float)X,(float)Y);
        gridToLocal(p);
        return p;
    }
    
    public Vector localToGrid(float x, float y) {
    	Vector p = new Vector(x,y);
    	localToGrid(p);
        return p;    	
    }
    
    public Vector gridToGlobal(int X, int Y) {
        Vector p = new Vector((float)X,(float)Y);
        gridToGlobal(p);
        return p;
    }
    
    public Vector globalToGrid(float i, float j) {
    	Vector p = new Vector(i,j);
    	globalToGrid(p);
        return p;    	
    }
    
    public Vector gridToPixel(int X, int Y) {
        Vector p = new Vector((float)X,(float)Y);
        gridToPixel(p);
        return p;
    }
    
    public Vector pixelToGrid(int pi, int pj) {
    	Vector p = new Vector((float)pi,(float)pj);
    	pixelToGrid(p);
        return p;    	
    }
    
    public void drawNode(Graphics g, int X, int Y) {
    	if (!_m.node_to_draw(X, Y)) return; 
    	
    	float  pi    = _orig.x + X*_dir1.x + Y*_dir2.x; 
    	float  pj    = _orig.y + X*_dir1.y + Y*_dir2.y;
    	g.setColor(_m.get_node_color(X,Y));
        g.fillOval((int)(pi-_p.get_zoom(25.f)),(int)(pj-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));
//        g.setColor(_m.get_edge_color());
        g.setColor(_m.get_circle_color(X,Y));
//        g.drawOval((int)(pi-_p.get_zoom(25.f)),(int)(pj-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));
        g.drawOval((int)(pi-_p.get_zoom(25.f)),(int)(pj-_p.get_zoom(25.f)),(int)(_p.get_zoom(50.f)),(int)(_p.get_zoom(50.f)));
        //System.out.println(pi+" "+pj);
        //System.out.println(_dir1.x+" "+_dir1.y+" "+_dir2.x+" "+_dir2.y+" ");
        //g.drawPolygon(new Polygon());

        String label = _m.get_node_label(X,Y);
        if (label != "") {
        	float sx = 0.5f *  g.getFontMetrics().stringWidth(label);
        	float sy = 0.25f * g.getFontMetrics().getHeight();
        	g.setColor(_m.get_label_color());
        	g.drawString(label,(int)(pi-sx),(int)(pj+sy));
        }        
        
    }
    
    public void drawTriangle(Graphics g, int X, int Y){
    	
    	Color[] draw = _m.triangle_to_draw(X, Y);
//    	boolean[] draw = _m.triangle_to_draw(X, Y);    	
    	if (draw[0]==null && draw[1]==null) return;

    	float  pi    = _orig.x + X*_dir1.x + Y*_dir2.x; 
    	float  pj    = _orig.y + X*_dir1.y + Y*_dir2.y;
    	float  pi2    = _orig.x + X*_dir1.x + (Y+1)*_dir2.x; 
    	float  pj2    = _orig.y + X*_dir1.y + (Y+1)*_dir2.y;
    	float pi3 = 0;
    	float pj3 = 0;
    	
    	if (draw[0]!=null) {
        	pi3    = _orig.x + (X+1)*_dir1.x + Y*_dir2.x; 
        	pj3    = _orig.y + (X+1)*_dir1.y + Y*_dir2.y;
        	g.setColor(draw[0]);
        	g.fillPolygon(new int[]{(int)(pi),(int)(pi2),(int)(pi3)},
  				  new int[]{(int)(pj),(int)(pj2),(int)(pj3)}, 3);
    	}
    
    	if (draw[1]!=null) {
        	pi3    = _orig.x + (X-1)*_dir1.x + (Y+1)*_dir2.x; 
        	pj3    = _orig.y + (X-1)*_dir1.y + (Y+1)*_dir2.y;
        	g.setColor(draw[1]);
        	g.fillPolygon(new int[]{(int)(pi),(int)(pi2),(int)(pi3)},
  				  new int[]{(int)(pj),(int)(pj2),(int)(pj3)}, 3);
    	}
    }
    
    public void drawEdge(Graphics g, int X1, int Y1, int X2, int Y2) {
    	
    	Color edge_color = _m.edge_to_draw(X1,Y1,X2,Y2);
    	if (edge_color == null) return;
    	float  pi1   = _orig.x + X1*_dir1.x + Y1*_dir2.x; 
    	float  pj1   = _orig.y + X1*_dir1.y + Y1*_dir2.y;
    	float  pi2   = _orig.x + X2*_dir1.x + Y2*_dir2.x; 
    	float  pj2   = _orig.y + X2*_dir1.y + Y2*_dir2.y;
        g.setColor(edge_color);
        g.drawLine((int)pi1,(int)pj1,(int)pi2,(int)pj2);
    }

    
    private float  _stroke = 2.f;
    private float  _font_size  = 18.f;
    private String _font_name  = "Times New Roman";
    private int    _font_style = Font.PLAIN;
    
    public void set_stroke(float stroke) {
    	_stroke = stroke;
    }
    
    public void set_font_size(float psize) {
    	_font_size = psize;
    }
    
    public void set_font_name(String pname) {
    	_font_name = pname;
    }
    
    public void set_font_style(int pstyle) {
    	_font_style = pstyle;
    }
    
    public void paint(Graphics2D g) {
 
        g.setStroke(new BasicStroke(_p.get_zoom(_stroke)));
        g.setFont(new Font(_font_name, _font_style, (int)_p.get_zoom(_font_size)));

		int r, radius;
        Vector p  = globalToGrid(0,0);
        Vector e0 = _edges.get(0);

        if (_m.draw_triangle()){
        	drawTriangle(g, (int)p.x, (int)p.y);
        	for (radius=0; radius<=_grid_radius; radius++) {
        		float nx = p.x + radius*e0.x;
        		float ny = p.y + radius*e0.y;

        		Iterator<Vector> dedge = _dedges.iterator();
        		while(dedge.hasNext()) {
        			Vector de = dedge.next();
        			for(r=0; r<radius; r++) {
        				drawTriangle(g, (int)nx, (int)ny);
        				nx+=de.x;
        				ny+=de.y;
        			}            
        		}
        	}        
        }
        
        if (_m.draw_edge()) {
        	Iterator<Vector> edge = _edges.iterator();
        	while(edge.hasNext()) {
        		Vector e = edge.next();
        		drawEdge(g, (int)p.x, (int)p.y, (int)(p.x+e.x), (int)(p.y+e.y));
        	}
        	for (radius=0; radius<=_grid_radius; radius++) {
        		float nx = p.x + radius*e0.x;
        		float ny = p.y + radius*e0.y;

        		Iterator<Vector> dedge = _dedges.iterator();
        		while(dedge.hasNext()) {
        			Vector de = dedge.next();
        			for(r=0; r<radius; r++) {
        				edge = _edges.iterator();
        				while(edge.hasNext()) {
        					Vector e = edge.next();
        					drawEdge(g, (int)nx, (int)ny, (int)(nx+e.x), (int)(ny+e.y));
        				}
        				nx+=de.x;
        				ny+=de.y;
        			}            
        		}
        	}
        }
                
        if (_m.draw_node()) {
        	drawNode(g, (int)p.x, (int)p.y);
        	for (radius=0; radius<=_grid_radius; radius++) {
        		float nx = p.x + radius*e0.x;
        		float ny = p.y + radius*e0.y;

        		Iterator<Vector> dedge = _dedges.iterator();
        		while(dedge.hasNext()) {
        			Vector de = dedge.next();
        			for(r=0; r<radius; r++) {
        				drawNode(g, (int)nx, (int)ny);
        				nx+=de.x;
        				ny+=de.y;
        			}            
        		}
        	}        
        }

    }

    protected void set_embedding(GridLayerEmbedding _embedding) {
		this._embedding = _embedding;
		this.on_transformation();
	}

	public void set_embedding(Subgrid sg) {
		set_embedding((GridLayerEmbedding)sg);
	}

}
