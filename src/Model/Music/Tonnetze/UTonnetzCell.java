package Model.Music.Tonnetze;

import java.util.ArrayList;

public interface UTonnetzCell {
	
	public ArrayList<UTonnetzCell> get_faces();
	public ArrayList<UTonnetzCell> get_cofaces();
	public ArrayList<UTonnetzCell> get_icells();
	public ArrayList<UTonnetzCell> get_pcells(int p);
	

}
