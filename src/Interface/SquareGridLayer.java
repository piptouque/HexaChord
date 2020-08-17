package Interface;

import Utils.Vector;


class SquareGridLayerEmbedding extends GridLayerEmbedding {
	/**
	 * @uml.property  name="_node_dist"
	 */
	private float _node_dist;
	
	SquareGridLayerEmbedding() {
		this(100.f);
	}

	SquareGridLayerEmbedding(float nd) {
		_node_dist = nd;
	}
	
	/**
	 * @return
	 * @uml.property  name="_node_dist"
	 */
	@Override
	public float get_node_dist() {
		return _node_dist;
	}

	@Override
	public void gridToLocal(Vector p) {
		p.x = _node_dist * p.x;
        p.y = _node_dist * p.y;
	}

	@Override
	public void localToGrid(Vector p) {		
		p.x = Math.round(p.x / _node_dist);
		p.y = Math.round(p.y / _node_dist);
	}
}

public class SquareGridLayer extends GridLayer {

	public SquareGridLayer(JTransformPanel p, GridLayerInterviewer m, float node_dist) {
		super(p, new SquareGridLayerEmbedding(node_dist), m);
		add_edge(0,1);
		add_edge(1,0);
	}
	
	public SquareGridLayer(JTransformPanel p, GridLayerInterviewer m) {
		super(p, new SquareGridLayerEmbedding(), m);
		add_edge(0,1);
		add_edge(1,0);
	}
	
}

