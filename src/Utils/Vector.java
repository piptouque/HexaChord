package Utils;

public class Vector {
    /**
	 * @uml.property  name="x"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="Utils.Vector"
	 */
    public float x;
    /**
	 * @uml.property  name="y"
	 */
    public float y;

    public Vector(float x_, float y_) {
        x = x_;
        y = y_;
    }

    public void rotate(float cos_rot, float sin_rot) {
        float x_ = x*cos_rot - y*sin_rot;
        float y_ = y*cos_rot + x*sin_rot;
        x = x_;
        y = y_;
    }

    public void rotate(float rot) {
        rotate((float)Math.cos(rot),(float)Math.sin(rot));
    }

    public void rotate(Angle a) {
        rotate(a.ctheta, a.stheta);
    }

    public void unrotate(float cos_rot, float sin_rot) {
        float x_ = x*cos_rot + y*sin_rot;
        float y_ = y*cos_rot - x*sin_rot;
        x = x_;
        y = y_;
    }

    public void unrotate(float rot) {
        unrotate((float)Math.cos(rot),(float)Math.sin(rot));
    }

    public void unrotate(Angle a) {
        unrotate(a.ctheta, a.stheta);
    }

    public void translate(float tx, float ty) {
        x += tx;
        y += ty;
    }

    public void translate(Vector v) {
        translate(v.x, v.y);
    }

    public void untranslate(float tx, float ty) {
        x -= tx;
        y -= ty;
    }
    
    public void untranslate(Vector v) {
        untranslate(v.x, v.y);
    }

    public void scale(float z) {
        x *= z;
        y *= z;
    }

    public void unscale(float z) {
        x /= z;
        y /= z;
    }

    public float distance2(float vx, float vy) {
        return (vx-x)*(vx-x) + (vy-y)*(vy-y);
    }

    public float distance2(Vector v) {
        return distance2(v.x, v.y);
    }

    public float distance(float vx, float vy) {
        return (float)Math.sqrt(distance2(vx, vy));
    }

    public float distance(Vector v) {
        return (float)Math.sqrt(distance2(v));
    }
    
    public float norm() {
    	return distance(0,0);
    }

    public float norm2() {
    	return distance2(0,0);
    }
    
    public String toString() {
    	return ("["+x+", "+y+"]");
    }
}


