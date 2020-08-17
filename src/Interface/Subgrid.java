package Interface;

import Utils.Vector;

public class Subgrid extends GridLayerEmbedding {

	/**
	 * @uml.property  name="_v1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Vector    _v1;
	/**
	 * @uml.property  name="_v2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Vector    _v2;
	/**
	 * @uml.property  name="_org"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Vector    _org;
	/**
	 * @uml.property  name="_l"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GridLayer _l;

	/**
	 * @uml.property  name="_det"
	 */
	private float _det;
	/**
	 * @uml.property  name="_detx"
	 */
	private float _detx;
	/**
	 * @uml.property  name="_dety"
	 */
	private float _dety;
	/**
	 * @uml.property  name="_col"
	 */
	private boolean _col;
	
	public Subgrid(GridLayer l, int X0, int Y0, int X1, int Y1, int X2, int Y2) {
		_l  = l;
		_org = new Vector(X0,Y0);
		_v1 = new Vector(X1,Y1);
		_v2 = new Vector(X2,Y2);
		update();
	}

	public Subgrid(GridLayer l, int X1, int Y1, int X2, int Y2) {
		this(l,0,0,X1,Y1,X2,Y2);
	}

	public Subgrid(GridLayer l) {
		this(l,0,0,1,0,0,1);
	}

	@Override
	public void gridToLocal(Vector p) {
		subgridToGrid(p);
		_l.gridToLocal(p);
	}

	public void subgridToGrid(Vector p) {
		float px = p.x * _v1.x + p.y * _v2.x;
		float py = p.x * _v1.y + p.y * _v2.y;
		p.x = px;
		p.y = py;
		p.translate(_org);
	}
	
	@Override
	public void localToGrid(Vector p) {
		_l.localToGrid(p);
		gridToSubgrid(p);
	}

	public void gridToSubgrid(Vector p) {
		p.untranslate(_org);
		float px, py;
		if (_col) {
			px = p.x * _detx + p.y * _dety;
			py = 0;
		} else {
			px = (_v2.x*p.y - _v2.y*p.x) / _det;
			py = (_v1.y*p.x - _v1.x*p.y) / _det;
		}
		p.x = Math.round(px);
		p.y = Math.round(py);
	}

	public void supergridToEuclidean(Vector p) {
		_l.gridToLocal(p);
	}
	
	@Override
	public float get_node_dist() {
		//TODO: change for a dependence with _l
		return 75.f;
	}

	public void set_org(int X, int Y) {
		_org = new Vector(X,Y);
		update();
	}

	public void set_v1(int X, int Y) {
		assert((X!=0)||(Y!=0));
		_v1 = new Vector(X,Y);
		update();
	}

	public void set_v2(int X, int Y) {
		assert((X!=0)||(Y!=0));
		_v2 = new Vector(X,Y);
		update();
	}

	private void update() {
		_det = _v2.x*_v1.y - _v2.y*_v1.x;
		_col = (Math.round(_det)==0);
		if (_col) {
			_detx = (Math.round(_v1.x)==0)?0:(1/_v1.x);
			_dety = (Math.round(_v1.x)==0)?(1/_v1.y):0;
		}
	}
}
