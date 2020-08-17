package Interface;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import Interface.GridFrame;
import Main.HexaChord;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;

public class CloseWindowProcessing implements WindowListener{
	
	PlanarUnfoldedTonnetz _planar_unfolded_tonnetz;
	
	public CloseWindowProcessing(GridFrame f, PlanarUnfoldedTonnetz t) {
		_planar_unfolded_tonnetz = t;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		HexaChord.getInstance().remove_frame(_planar_unfolded_tonnetz);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
