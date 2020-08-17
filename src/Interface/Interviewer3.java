package Interface;

import java.awt.Color;

import Model.Music.PosPitchSetStream;

public interface Interviewer3 {
	
	public Color get_node_color(int X, int Y);
	public Color get_circle_color(int X, int Y);
	public Color get_edge_color();
	public Color get_label_color();
	public boolean node_to_draw(int X, int Y);
	public void coords_update();

}
