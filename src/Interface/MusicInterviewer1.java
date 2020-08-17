package Interface;

import java.awt.Color;

import Main.HexaChord;
import Model.Music.Note;
import Model.Music.Parameters;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Utils.Gcd;

public class MusicInterviewer1 extends GridLayerInterviewer {
	
	private final Color LIGHT_GRAY = Color.lightGray;

	protected HexaChord _h;
	protected Parameters _p;
	protected PlanarUnfoldedTonnetz _t;

	public MusicInterviewer1(PlanarUnfoldedTonnetz t) {
		_h = HexaChord.getInstance();
		_t = t;
		_p = Parameters.getInstance();
	}

	@Override
	public String get_node_label(int X, int Y) {
		return Note.get_name(get_note(X, Y));	
	}

	@Override
	public Color get_node_color(int X, int Y) {
		return LIGHT_GRAY;
	}

//	//@Override
//	public Color get_edge_color() {
//		return LIGHT_GRAY;
//	}

	@Override
	public Color get_label_color() {
		return Color.white;
	}

	@Override
	public boolean draw_edge() {
		return false;
	}

	protected int get_note(int X, int Y) {

		//return Gcd.reduce(X * _t.get_generator(0) - Y * _t.get_generator(2),_t.get_N());
		return _t.get_scale().get(Gcd.reduce(X * _t.get_generator(0) - Y * _t.get_generator(2),_t.get_N()));

	}
}