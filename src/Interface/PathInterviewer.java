package Interface;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;

import Model.Music.Constant;
import Model.Music.Parameters;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Path.PosSquareGridCoordPath;
import Path.SquareGridCoordPath;
import Path.SquareGridCoordList;
import Utils.Table;
import Utils.TableSet;
import Utils.Vector;

public class PathInterviewer extends MusicInterviewer1 implements Interviewer3{
	
	private final static Color jaune_clair = new Color(255,255,0);
	private final static Color jaune_fonce = new Color(218, 218, 170);
	private final static Color rouge = new Color(255,175,0);
	private final static Color vert = new Color(137,255,141);
	private final static Color NOIR = Color.black;
	private final static Color gris = Color.gray;
	private final static Color gris_fonce = Color.DARK_GRAY;
	
	private PosSquareGridCoordPath _tonnetz_coord_path;
	private SquareGridCoordList _current_coords;
	private ArrayList<ArrayList<int[]>> _current_edges;
	private ArrayList<ArrayList<int[]>> _current_triangles;
	
	private TableSet _coords_set;
	private ArrayList<ArrayList<int[]>> _past_edges;
	private ArrayList<ArrayList<int[]>> _past_triangles;
	
//	private TreeMap<Long,ArrayList<ArrayList<int[]>>> _past_edges_tree;
	private TreeMap<Long,ArrayList<ArrayList<int[]>>> _past_triangles_tree;
	
	public PathInterviewer(PosSquareGridCoordPath tonnetz_coord_path, PlanarUnfoldedTonnetz tonnetz){
		super(tonnetz);
		_tonnetz_coord_path = tonnetz_coord_path;
		_current_coords = new SquareGridCoordList();
		_past_triangles_tree = new TreeMap<Long,ArrayList<ArrayList<int[]>>>();
		//_past_edges_tree = new TreeMap<Long,ArrayList<ArrayList<int[]>>>();
		_past_edges = new ArrayList<ArrayList<int[]>>();
		_past_triangles = new ArrayList<ArrayList<int[]>>();
		_coords_set = new TableSet();
	}

	@Override
	public Color get_node_color(int X, int Y) {
		
		for (int[] c : _current_coords){
			if (c[0] == X && c[1] == Y) return jaune_clair;			
		}
		return jaune_fonce;
	}

	@Override
	public Color get_circle_color(int X, int Y) {		
		for (int[] coord : _current_coords){
			if (Arrays.equals(coord,new int[]{X,Y})) return NOIR;
		}
		return gris;
	}

	@Override
	public Color get_edge_color() {
		return NOIR;
	}

	@Override
	public Color get_label_color() {
		return NOIR;
	}

	@Override
	public boolean node_to_draw(int X, int Y) {
		
		for (int[] c : _coords_set){
			if (c[0] == X && c[1] == Y) return true;			
		}
		return false;
	}
	
	@Override
	public boolean draw_edge() {
		return true;
	}

	@Override
	public Color edge_to_draw(int X1, int Y1,int X2, int Y2){
		
		ArrayList<ArrayList<int[]>> tmp_current_edges = new ArrayList<ArrayList<int[]>>(_current_edges);
		for (ArrayList<int[]> edge : tmp_current_edges){
			if (edge.get(0)[0] == X1 && edge.get(0)[1] == Y1 && edge.get(1)[0] == X2 && edge.get(1)[1] == Y2) return NOIR;
		}
		
		ArrayList<ArrayList<int[]>> tmp_past_edges = new ArrayList<ArrayList<int[]>>(_past_edges);
		for (ArrayList<int[]> edge : tmp_past_edges){
			if (edge.get(0)[0] == X1 && edge.get(0)[1] == Y1 && edge.get(1)[0] == X2 && edge.get(1)[1] == Y2) return gris_fonce;
		}		
		return null;
	}

	@Override
	public boolean draw_triangle(){
		return true;
	}
	
	// [0,0] : pas de triangle. [Color,0] : tirnagle qui pointe vers la droite. [0,Color] : triangle qui pointe vers la gauche
	// ˆ chaque sommet on peut associer 2 triangles (ex : triade m et M)
	@Override
	public Color[] triangle_to_draw(int X, int Y){
		boolean bool1 = true;
		boolean bool2 = true;
		Color[] colors = new Color[]{null,null};
		ArrayList<int[]> triangle_d = new ArrayList<int[]>();
		triangle_d.add(new int[]{X,Y});
		triangle_d.add(new int[]{X,Y+1});
		triangle_d.add(new int[]{X+1,Y});
		ArrayList<int[]> triangle_g = new ArrayList<int[]>();
		triangle_g.add(new int[]{X,Y});
		triangle_g.add(new int[]{X,Y+1});
		triangle_g.add(new int[]{X-1,Y+1});
		
		ArrayList<ArrayList<int[]>> tmp_past_triangle = new ArrayList<ArrayList<int[]>>(_past_triangles);
		for(ArrayList<int[]> triangle : tmp_past_triangle){
			if (triangle_equality(triangle, triangle_d)){
				colors[0] = jaune_fonce;
			}
			if (triangle_equality(triangle, triangle_g)){
				colors[1] = jaune_fonce;
			}
		}

		ArrayList<ArrayList<int[]>> tmp_current_triangle = new ArrayList<ArrayList<int[]>>(_current_triangles);
		for(ArrayList<int[]> triangle : tmp_current_triangle){
			if (triangle_equality(triangle, triangle_d)){
				colors[0] = jaune_clair;
			}
			if (triangle_equality(triangle, triangle_g)){
				colors[1] = jaune_clair;
			}
		}		
		return colors;

	}

	public static boolean triangle_equality(ArrayList<int[]> triangle1, ArrayList<int[]> triangle2){
		int n=0;
		for (int[] v1 : triangle1){
			for (int[] v2 : triangle2){
				if (Arrays.equals(v1,v2)) n++;
			}
		}
		if (n==3) return true;
		return false;
	}
	

	@Override
	public void coords_update() {
		
		_current_coords = _tonnetz_coord_path.get_current_CoordSet();
		_current_edges = _tonnetz_coord_path.get_current_CoordSet().get_edges();
		_current_triangles = _tonnetz_coord_path.get_current_CoordSet().get_triangles();
 		_coords_set.addAll(_current_coords);
 		_past_edges.addAll(_current_coords.get_edges());
 		_past_triangles.addAll(_current_coords.get_triangles());

// 		if (_stream.get_current_col().size()>0){
// 	 		_current_extra_pitches = _t.closer_pitch_class(_stream.get_current_col()); 			
// 		} else _current_extra_pitches.clear();
	}


	public void re_init(PosSquareGridCoordPath path) {
		_tonnetz_coord_path = path;
		_coords_set.clear();
		_past_triangles_tree = new TreeMap<Long,ArrayList<ArrayList<int[]>>>();
		//_past_edges_tree = new TreeMap<Long,ArrayList<ArrayList<int[]>>>();
		_past_edges = new ArrayList<ArrayList<int[]>>();
		_past_triangles = new ArrayList<ArrayList<int[]>>();
		coords_update();
	}
	

	
	

}
