package Interface;

import java.awt.Color;
import java.awt.Graphics2D;

import Utils.Vector;

class BasicLayer extends Layer {

    public BasicLayer(JTransformPanel p) {
		super(p);}

	public void paint(Graphics2D g) {
		Vector p1 = _p.localToPixel(-75,-75);
		Vector p2 = _p.localToPixel(75,75);
        g.setColor(Color.green);
        g.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
        Vector c = _p.localToPixel(0,0);
        g.setColor(Color.blue);
        g.fillOval((int)(c.x-50.f*_p._zoom),(int)(c.y-50.f*_p._zoom),(int)(100.f*_p._zoom),(int)(100.f*_p._zoom));
    }

	@Override
	public void on_transformation() {}
}



