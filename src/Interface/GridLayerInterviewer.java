package Interface;

import java.awt.Color;

public class GridLayerInterviewer {

	protected static final Color YELLOW = Color.yellow;
	protected static final Color DARK_YELLOW = new Color(218,218,170);
	protected static final Color BLACK = Color.black;
	protected static final Color DARK_GRAY = Color.DARK_GRAY;
	protected static final Color GRAY = Color.gray;
	protected static final Color LIGHT_GRAY = new Color(181,181,181);

	
	// NODES
	
	public boolean draw_node() {
		return true;
	}
	
	public boolean node_to_draw(int X, int Y) {
		return true;
	}

	public Color get_node_color(int X, int Y) {
		return (((X==0)&&(Y==0))?Color.gray:Color.lightGray);
	}
	
	public Color get_circle_color(int X, int Y){
		return Color.gray;
	}

	public String get_node_label(int X, int Y) {
		return "";
	}
	
	public Color  get_label_color() {
		return Color.black;
	}

	// EDGES
	
	public Color edge_to_draw(int X1, int Y1,int X2, int Y2){
		//return Color.GRAY;
		return LIGHT_GRAY;
	}
	
	public boolean draw_edge() {
		return true;
	}
	
//	public Color  get_edge_color() {
//		return Color.black;
//	}

	// TRIANGLES
	
	public boolean draw_triangle(){
		return false;
	}
	
	public Color[] triangle_to_draw(int X, int Y){
		return new Color[]{null,null};
	}
	
//	public Color get_triangle_color(boolean b) {
//		return Color.yellow;
//	}
	
	
}
