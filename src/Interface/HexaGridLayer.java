package Interface;

import Utils.Vector;

class HexaGridLayerEmbedding extends GridLayerEmbedding {
	private float _node_dist;
	
	HexaGridLayerEmbedding() {
		this(100.f);
	}

	HexaGridLayerEmbedding(float nd) {
		_node_dist = nd;
	}
	
	@Override
	public float get_node_dist() {
		return _node_dist;
	}

	@Override
	public void gridToLocal(Vector p) {
        float i = (1.5f/(float)Math.sqrt(3))*p.y;
        float j = (p.x + 0.5f*p.y);
        p.x = _node_dist*i;
        p.y = _node_dist*j;
	}

	@Override
	public void localToGrid(Vector p) {
		float c = (2.f/(float)Math.sqrt(3)) * (p.x/_node_dist);
		float a = p.y / _node_dist - 0.5f * c ;

		int na = Math.round(a);
		int nc = Math.round(c);

		Vector p0 = new Vector(na,   nc);
		Vector p1 = new Vector(na+1, nc);
		Vector p2 = new Vector(na-1, nc);
		Vector p3 = new Vector(na,   nc+1);
		Vector p4 = new Vector(na,   nc-1);
		gridToLocal(p0);
		gridToLocal(p1);
		gridToLocal(p2);
		gridToLocal(p3);
		gridToLocal(p4);
	        
		float d0 = p.distance2(p0);
		float d1 = p.distance2(p1);
		float d2 = p.distance2(p2);
		float d3 = p.distance2(p3);
		float d4 = p.distance2(p4);
	        
		float d = Math.min(d0,Math.min(d1,Math.min(d2,Math.min(d3,d4))));
		if      (d==d0)  { p.x = na;   p.y = nc;   }
		else if (d==d1)  { p.x = na+1; p.y = nc;   }
		else if (d==d2)  { p.x = na-1; p.y = nc;   }
		else if (d==d3)  { p.x = na;   p.y = nc+1; }
		else   /*d==d4*/ { p.x = na;   p.y = nc-1; }
	}
}

public class HexaGridLayer extends GridLayer {

	public HexaGridLayer(JTransformPanel p, GridLayerInterviewer m, float node_dist) {
		super(p, new HexaGridLayerEmbedding(node_dist), m);
		add_edge(1,0);
		add_edge(0,1);
		add_edge(-1,1);
	}
	
	public HexaGridLayer(JTransformPanel p, GridLayerInterviewer m) {
		super(p, new HexaGridLayerEmbedding(), m);
		add_edge(1,0);
		add_edge(0,1);
		add_edge(-1,1);
	}

	public HexaGridLayer(JTransformPanel p, GridLayerInterviewer m, Subgrid g) {
		super(p, g, m);
		add_edge(1,0);
		add_edge(0,1);
		add_edge(-1,1);
	}

	public void set_embedding(float node_dist) {
		set_embedding(new HexaGridLayerEmbedding(node_dist));
	}
}

