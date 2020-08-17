package Interface;

import java.awt.Color;

import Interface.MusicInterviewer1;
import Interface.MusicSubgrid;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Utils.Vector;

public class MusicInterviewer2 extends MusicInterviewer1 {

	MusicSubgrid _sg;
	MusicInterviewer1 _m1;

	public MusicInterviewer2(PlanarUnfoldedTonnetz t, MusicSubgrid sg, MusicInterviewer1 m1) {
		super(t);
		_sg = sg;
		_m1 = m1;
	}

	@Override
	public boolean draw_edge() {
		return true;
	}

	@Override
	public String get_node_label(int X, int Y) {
		if (_p.is_draw_subgrid()) {
			Vector p = new Vector(X, Y);
			_sg.subgridToGrid(p);
			return _m1.get_node_label((int) p.x, (int) p.y);
		} else
			return super.get_node_label(X, Y);
	}

//	@Override
//	public Color get_edge_color() {
//		return Color.red;
//	}

}