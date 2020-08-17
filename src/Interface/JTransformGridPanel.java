package Interface;

import java.awt.Graphics;

import Main.HexaChord;
import Model.Music.Parameters;

public class JTransformGridPanel extends JTransformPanel {

	private static final long serialVersionUID = 1L;
	private static boolean DRAW_ONLY_PLAYED = false;

	public JTransformGridPanel(HexaChord h, Parameters p){
		super(h,p);
	}
	
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!DRAW_ONLY_PLAYED){
            layers.get(0).paint(_graph);
        	layers.get(1).paint(_graph);
        }

//        if (_parameters.is_model()){
//            Iterator<Layer> layer = layers.iterator();
//            while(layer.hasNext()) {
//            	layer.next().paint(_graph);
        	layers.get(2).paint(_graph);        	
//        } else {
//            Iterator<Layer> layer = layers.iterator();
//            while(layer.hasNext()) {
//            	layer.next().paint(_graph);
//            }
//        	layers.get(3).paint(_graph);
//        }
        g.drawImage(_image,0,0,this);

    }
    
//    public void repaint(){
//    	super.repaint();
//       	System.out.println("repere 1");
//    }

}
