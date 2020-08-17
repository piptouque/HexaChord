package Utils;



public class Angle {

	final static private float C2PI = 2.f*(float)Math.PI;

    public float theta;
    public float ctheta;
    public float stheta;

    public Angle(float t) {
        set_angle(t);
    }

    public void set_angle(float t) {
        while (t >= C2PI) { t -= C2PI; }
        while (t < 0.f)   { t += C2PI; }
        theta = t;
        ctheta = (float)Math.cos(theta);
        stheta = (float)Math.sin(theta);
    }

    public void rotate(Vector v) {
        v.rotate(ctheta,stheta);
    }

    public void unrotate(Vector v) {
        v.rotate(ctheta,-stheta);
    }
}

