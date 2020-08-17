package Interface;

import java.awt.Graphics;

public class JTransformGraphPanel extends JTransformPanel{
	
	private static final long serialVersionUID = 1L;

	public JTransformGraphPanel(){
		super();
	}

	public void paintComponent(Graphics g) {
        super.paintComponent(g);
    	layers.get(0).paint(_graph);
//    	layers.get(1).paint(_graph);
        g.drawImage(_image,0,0,this);

    }

}
