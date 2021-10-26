package Interface;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;

import Interface.MusicInterviewer1;
import Interface.MusicInterviewer2;
import Interface.MusicSubgrid;
import Main.HexaChord;
import Model.Music.Constant;
import Model.Music.Parameters;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Path.SquareGridCoordPath;
import Path.SquareGridCoordList;
import Utils.MidiFilePlayer;
import Utils.Table;
import Utils.TableSet;
import Utils.Vector;

public class MusicInterviewer3 extends MusicInterviewer2 implements Interviewer3 {

	private PosPitchSetStream _stream;
	private PitchSetWithDuration _current_col = new PitchSetWithDuration();
	private SquareGridCoordList _current_chord_coords = new SquareGridCoordList();
	private ArrayList<int[]> _last_model_chord_coords = new ArrayList<int[]>();
	private SquareGridCoordPath _past_coords_tree;

	private TreeMap<Long, ArrayList<ArrayList<int[]>>> _past_edges_tree;
	private TreeMap<Long, ArrayList<ArrayList<int[]>>> _past_triangles_tree;

	private TableSet _total_past_coords_set;
	private HashSet<Integer> _current_extra_pitches = new HashSet<Integer>();
	private PlanarUnfoldedTonnetz _tonnetz;
	private int i = 0;
	private SquareGridCoordPath _computed_coord_path;

	public MusicInterviewer3(PlanarUnfoldedTonnetz t, MusicSubgrid sg, MusicInterviewer1 m1, PosPitchSetStream stream) {
		super(t, sg, m1);

		_tonnetz = t;
		_stream = stream;
		_past_coords_tree = new SquareGridCoordPath();
		_past_triangles_tree = new TreeMap<Long, ArrayList<ArrayList<int[]>>>();
		_past_edges_tree = new TreeMap<Long, ArrayList<ArrayList<int[]>>>();
		_total_past_coords_set = new TableSet();
		if (!Parameters.isRealTimeTrajectory()) {
			_computed_coord_path = stream.compute_tonnetz_coord_path(t);
		}
		if (_tonnetz.get_connected_components_count() == 1) {
			coords_update();
		}
	}

	@Override
	public Color get_node_color(int X, int Y) {

		int note;

		if (_p.is_musification()) {
			if (_p.get_musificationMovingCell().is_at(X, Y)) {
				return new Color(255, 255, 0);
			} else
				return new Color(218, 218, 170);
		}

		if (_p.is_draw_subgrid()) {
			Vector p = new Vector(X, Y);
			_sg.subgridToGrid(p);
			note = _m1.get_note((int) p.x, (int) p.y);
		} else
			note = get_note(X, Y);

		if (_p.get_trace_length_to_draw() != 0) {
			// ligne à décommenter pour avoir le cas exceptionnel !draw_pitch_once && trace
			// if(_current_col.to_STChord().contains(note)) return new Color(255,255,0);
			for (int[] coord : _current_chord_coords) {
				if (Arrays.equals(coord, new int[] { X, Y }))
					return new Color(255, 255, 0);
			}

			return new Color(218, 218, 170);

		} else {

			// pas de trace
			if (_p.is_draw_extra_voice()) {
				// if (!_STSeq.get_current_chord().to_STChord(Constant.N).member(note)) {
				if (_current_extra_pitches.contains(note)) {
					if (_current_extra_pitches.size() == 1)
						return new Color(255, 175, 0);
					else {

						if (_current_extra_pitches.size() > 1)
							return new Color(137, 255, 141);
					}
				}
				if (!_stream.get_current_col().to_PitchClassSet(Constant.N).member(note)) {
					return new Color(255, 175, 0);
				}
			}
			return new Color(255, 255, 0);

		}

	}

	@Override
	public Color get_circle_color(int X, int Y) {

		if (_p.is_musification()) {
			if (_p.get_musificationMovingCell().is_at(X, Y)) {
				return Color.black;
			} else
				return Color.gray;
		}

		if (_p.get_trace_length_to_draw() != 0) {
			// ligne à décommenter pour avoir le cas exceptionnel !draw_pitch_once && trace
			// if(_current_col.to_STChord().contains(get_note(X, Y))) return Color.black;

			int note;
			if (_p.is_draw_subgrid()) {
				Vector p = new Vector(X, Y);
				_sg.subgridToGrid(p);
				note = _m1.get_note((int) p.x, (int) p.y);
			} else
				note = get_note(X, Y);

			for (int[] coord : _current_chord_coords) {
				if (Arrays.equals(coord, new int[] { X, Y }))
					return Color.black;
			}

			// if (_stream.get_current_col().to_STChord(Constant.N).member(note)) {
			// return Color.black;
			// }
			return Color.gray;

		} else {
			// sans trace
			return Color.black;
		}
	}

	@Override
	public Color get_edge_color() {
		return Color.black;
	}

	@Override
	public Color get_label_color() {
		return Color.black;
	}

	@Override
	public boolean node_to_draw(int X, int Y) {
		int note;

		if (_p.is_musification()) {
			if (_p.get_musificationMovingCell().has_passed_by(X, Y)) {
				return true;
			} else
				return false;
		}

		if (_p.is_draw_subgrid()) {
			Vector p = new Vector(X, Y);
			_sg.subgridToGrid(p);
			note = _m1.get_note((int) p.x, (int) p.y);
		} else
			note = get_note(X, Y);

		if (_p.is_draw_pitch_once()) {

			if (_p.get_trace_length_to_draw() == 0) {
				if (_current_chord_coords.size() != 0) {
					for (int[] c : _current_chord_coords)
						if (c[0] == X && c[1] == Y)
							return true;
					return false;
				} else
					return false;
			} else {
				// if (_current_model_chord_coords.size()!=0) {
				Long tmp_key = _stream.get_current_key();
				for (int n = 0; n > _p.get_trace_length_to_draw(); n--) {
					if (tmp_key != null && _past_coords_tree.get(tmp_key) != null) {

						try {
							for (int[] c : _past_coords_tree.get(tmp_key)) {
								if (c[0] == X && c[1] == Y)
									return true;
							}
						} catch (java.lang.NullPointerException e) {
							// TODO Auto-generated catch block
							System.out.println("draw node : Midi events to frequent ");
							// e.printStackTrace();
						}
						tmp_key = _past_coords_tree.lowerKey(tmp_key);
					}
				}
				return false;
			}
		} else {
			if (_p.get_trace_length_to_draw() != 0)
				return _h.get_window_pitchs().contains(note);

			else {
				if (_p.is_draw_extra_voice()) {
					// return _stream.get_current_col().to_STChord(Constant.N)
					// .member(note)
					// || (_t.closer_pitch_class(
					// _stream.get_current_col().to_STChord(
					// Constant.N)).size() == 1 && (Integer) _t
					// .closer_pitch_class(
					// _stream.get_current_col()
					// .to_STChord(Constant.N))
					// .toArray()[0] == note);
					return (_stream.get_current_col().to_PitchClassSet(Constant.N).member(note)
							|| _current_extra_pitches.contains(note));
				} else
					return _stream.get_current_col().to_PitchClassSet(Constant.N).member(note);
			}
		}
	}

	// [0,0] : pas de triangle. [Color,0] : tirnagle qui pointe vers la droite.
	// [0,Color] : triangle qui pointe vers la gauche
	// À chaque sommet on peut associer 2 triangles (ex : triade m et M)
	@Override
	public Color[] triangle_to_draw(int X, int Y) {

		Color[] colors = new Color[] { null, null };
		if (_p.is_musification())
			return colors;
		if (!_p.is_draw_pitch_once() && _current_col.to_PitchClassSet(12).size() < 3)
			return colors;
		if (!node_to_draw(X, Y))
			return colors;
		// int note;
		if (_p.is_draw_subgrid()) {
			return colors;
			// Vector p = new Vector(X, Y);
			// _sg.subgridToGrid(p);
			// note = _m1.get_note((int) p.x, (int) p.y);
		} // else note = get_note(X, Y);

		if (_p.get_trace_length_to_draw() != 0) {
			Long tmp_key = _stream.get_current_key();
			boolean bool1 = true;
			boolean bool2 = true;
			for (int n = 0; n > _p.get_trace_length_to_draw(); n--) {
				if (tmp_key != null && _past_triangles_tree.get(tmp_key) != null) {
					try {
						for (ArrayList<int[]> t : _past_triangles_tree.get(tmp_key)) {
							if (t.get(0)[0] == X && t.get(0)[1] == Y) {
								if (t.get(2)[0] == X + 1 && t.get(2)[1] == Y && bool1) {
									bool1 = false;
									if (n == 0) {
										colors[0] = Color.yellow;
									} else {
										colors[0] = new Color(218, 218, 170);
									}
								}
								if (t.get(2)[0] == X - 1 && t.get(2)[1] == Y + 1 && bool2) {
									bool2 = false;
									if (n == 0) {
										colors[1] = Color.yellow;
									} else {
										colors[1] = new Color(218, 218, 170);
									}
								}
							}
						}
					} catch (java.lang.NullPointerException e) {
						// TODO Auto-generated catch block
						System.out.println("draw triangles : Midi events to frequent");
						// e.printStackTrace();
					}
					// tmp_key=_past_triangles_tree.lowerKey(tmp_key);
					tmp_key = _past_coords_tree.lowerKey(tmp_key);
				}
			}
		} else {
			if (_current_col.to_PitchClassSet(12).contains(_t.xy_coord_to_pitch_class(new int[] { X, Y + 1 }))) {
				if (_current_col.to_PitchClassSet(12).contains(_t.xy_coord_to_pitch_class(new int[] { X + 1, Y }))) {
					colors[0] = Color.yellow;
				}
				if (_current_col.to_PitchClassSet(12)
						.contains(_t.xy_coord_to_pitch_class(new int[] { X - 1, Y + 1 }))) {
					colors[1] = Color.yellow;
				}
			}
		}
		return colors;
	}

	@Override
	public Color edge_to_draw(int X1, int Y1, int X2, int Y2) {

		if (_p.is_musification())
			return null;
		if (!_p.is_draw_pitch_once() && _current_col.to_PitchClassSet(12).size() < 2)
			return null;
		if (!node_to_draw(X1, Y1) || !node_to_draw(X2, Y2))
			return null;
		int[] note1 = new int[] { X1, Y1 };
		int[] note2 = new int[] { X2, Y2 };
		Color color = null;

		Long tmp_key = _stream.get_current_key();
		if (_p.is_draw_pitch_once()) {
			if (_p.get_trace_length_to_draw() != 0) {
				for (int n = 0; n > _p.get_trace_length_to_draw(); n--) {
					if (tmp_key != null && _past_edges_tree.get(tmp_key) != null) {
						try {
							for (ArrayList<int[]> t : _past_edges_tree.get(tmp_key)) {
								if (t.get(0)[0] == X1 && t.get(0)[1] == Y1 && t.get(1)[0] == X2 && t.get(1)[1] == Y2) {
									if (n == 0) {
										return BLACK;
									} else {
										return DARK_GRAY;
									}
								}
							}
						} catch (java.lang.NullPointerException e) {
							// TODO Auto-generated catch block
							System.out.println("draw edges : Midi events to frequent");
							// e.printStackTrace();
						}
						tmp_key = _past_coords_tree.lowerKey(tmp_key);
					}
				}
			} else {
				if (tmp_key != null && _past_edges_tree.get(tmp_key) != null) {
					for (ArrayList<int[]> t : _past_edges_tree.get(tmp_key)) {
						if (t.get(0)[0] == X1 && t.get(0)[1] == Y1 && t.get(1)[0] == X2 && t.get(1)[1] == Y2) {
							return BLACK;
						}
					}
				}
			}
		} else {
			if (_p.get_trace_length_to_draw() != 0) {
				if (_current_col.to_PitchClassSet(12).contains(_t.xy_coord_to_pitch_class(new int[] { X1, Y1 }))
						&& _current_col.to_PitchClassSet(12)
								.contains(_t.xy_coord_to_pitch_class(new int[] { X2, Y2 }))) {
					return BLACK;
				}
				for (int n = -1; n > _p.get_trace_length_to_draw(); n--) {
					if (tmp_key != null && _stream.get(tmp_key) != null) {
						if (_stream.get(tmp_key).to_PitchClassSet()
								.contains(_t.xy_coord_to_pitch_class(new int[] { X1, Y1 }))
								&& _stream.get(tmp_key).to_PitchClassSet()
										.contains(_t.xy_coord_to_pitch_class(new int[] { X2, Y2 }))) {
							return DARK_GRAY;
						}
						tmp_key = _stream.lowerKey(tmp_key);
					}
				}
			} else {
				if (_current_col.to_PitchClassSet(12).contains(_t.xy_coord_to_pitch_class(new int[] { X1, Y1 }))
						&& _current_col.to_PitchClassSet(12)
								.contains(_t.xy_coord_to_pitch_class(new int[] { X2, Y2 }))) {
					return BLACK;
				}
			}
		}

		return null;
	}

	@Override
	public boolean draw_edge() {
		return true;
	}

	@Override
	public boolean draw_triangle() {
		return true;
	}

	public void coords_update() {
		// if (_current_model_chord_coords.isEmpty()){

		// 1. update _current_col
		if (_current_col.is_equal(_stream.get_current_col()) && !(HexaChord.getInstance()._external_KB_ON
				&& !MidiFilePlayer.getInstance().get_sequencer().isRunning())) {
			// if (_current_col.is_equal(_stream.get_current_col())) {
			// System.out.println("current col does not change: "+_current_col);
			return;
		} else {
			_current_col = new PitchSetWithDuration(_stream.get_current_col());
		}
		// System.out.println("CURRENT COL "+_current_col);

		// 2. update _current_chord_coords
		if (_last_model_chord_coords.isEmpty()) {
			// System.out.println("_last_model_chord_coords IS EMPTY -
			// _current_col:"+_current_col);
			if (!_stream.get_current_col().isEmpty())
				_current_chord_coords = _t.first_chord_XYcoords(_stream.get_current_STChord());
		} else {
			// System.out.println("_last_model_chord_coords IS NOT EMPTY -
			// _current_col:"+_current_col);
			if (!_stream.get_current_col().isEmpty()) { // si le nouvel accord n'est pas vide
				// System.out.println("PAS VIDE");
				// _current_model_chord_coords =
				// _t.n_chord_XYcoords(_stream.get_current_col().to_STChord(Constant.N),_current_model_chord_coords);
				// _current_model_chord_coords =
				// _t.n_chord_XYcoords(_stream.get_current_STChord(),_last_model_chord_coords);
				if (!HexaChord.getInstance()._external_KB_ON) {
					if (_stream.get_current_col().get_duration() > 5) {
						_current_chord_coords = _t.n_chord_XYcoords(_stream.get_current_STChord(), _past_coords_tree,
								_total_past_coords_set);
					}
				} else {
					if (_past_coords_tree.size() > 2
							&& (_stream.get_current_key() - _stream.lowerKey(_stream.get_current_key()) < 20)) {

						// _past_coords_tree.remove(_stream.lowerKey(_stream.get_current_key()));
						// System.out.println("BBAAAMM "+HexaChord.getInstance()._external_KB_ON);
						// _stream.remove(_stream.lowerKey(_stream.get_current_key()));

					}
					// System.out.println("avant : "+_current_chord_coords);
					_current_chord_coords = _t.n_chord_XYcoords(_stream.get_current_STChord(), _past_coords_tree,
							_total_past_coords_set);
					// System.out.println("après : "+_current_chord_coords);
				}
			} else { // si le nouvel accord est vide
				// System.out.println("VIDE");
				if (HexaChord.getInstance()._external_KB_ON && _past_coords_tree.size() > 2
						&& (_stream.get_current_key() - _stream.lowerKey(_stream.get_current_key()) < 20)) {
					// _past_coords_tree.remove(_stream.lowerKey(_stream.get_current_key()));
					// System.out.println("BBOOOMM "+HexaChord.getInstance()._external_KB_ON);
					// _stream.remove(_stream.lowerKey(_stream.get_current_key()));
					// System.out.println("stream tail:
					// "+_stream.subMap(_stream.lowerKey(_stream.lowerKey(_stream.lowerKey(_stream.lastKey()))),
					// _stream.lastKey()+1));

				}
				_current_chord_coords = new SquareGridCoordList();
				_last_model_chord_coords = _past_coords_tree.lowerEntry(_stream.get_current_key()).getValue();
			}
		}

		// 3.
		_past_coords_tree.put(_stream.get_current_key(), _current_chord_coords);
		_past_edges_tree.put(_stream.get_current_key(), _current_chord_coords.get_edges());
		_past_triangles_tree.put(_stream.get_current_key(), _current_chord_coords.get_triangles());
		_total_past_coords_set.addAll(_current_chord_coords);

		// System.out.println("_past_coords : "+Table.toString2(_past_model_coords));
		if (_current_chord_coords != null && !_current_chord_coords.isEmpty()) {
			_last_model_chord_coords = _current_chord_coords;
		}

		if (_stream.get_current_col().size() > 0) {
			_current_extra_pitches = _t.closer_pitch_class(_stream.get_current_col());
		} else
			_current_extra_pitches.clear();
		// System.out.println("triangles : "+_past_triangles_tree);
		// System.out.println("vertices : "+_past_coords_tree);

	}

	public void re_init(PosPitchSetStream stream) {
		_stream = stream;
		_past_coords_tree = new SquareGridCoordPath();
		_past_edges_tree = new TreeMap<Long, ArrayList<ArrayList<int[]>>>();
		_past_triangles_tree = new TreeMap<Long, ArrayList<ArrayList<int[]>>>();
		_last_model_chord_coords.clear();
		_total_past_coords_set.clear();
		_current_chord_coords = new SquareGridCoordList();
		_current_extra_pitches = new HashSet<Integer>();
		if (_tonnetz.get_connected_components_count() == 1) {
			coords_update();
		}
		if (!Parameters.isRealTimeTrajectory()) {
			_computed_coord_path = stream.compute_tonnetz_coord_path(_tonnetz, new TreeMap<Long, ArrayList<Integer>>());
		}
	}

	// public void set_tonnetz_coord_path(SquareGridCoordPath _tonnetz_coord_path) {
	// this._tonnetz_coord_path = _tonnetz_coord_path;
	// }

}
