package Interface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import Main.HexaChord;
import Model.Music.Parameters;
import Utils.Angle;
import Utils.Vector;

public class JTransformPanel extends JComponent
        implements MouseInputListener, MouseWheelListener, KeyListener, ComponentListener {
    private static final long serialVersionUID = 6406282664203074443L;

    private int _w;
    private int _h;
    private float _w_2;
    private float _h_2;
    protected BufferedImage _image;
    protected Graphics2D _graph;
    private boolean _antialiasing;
    private Vector _trans;
    private Angle _rot;
    public float _zoom;
    protected ArrayList<Layer> layers = new ArrayList<Layer>();
    // private HashSet<Integer> layers_to_draw = new HashSet<Integer>();
    protected Parameters _parameters;
    protected HexaChord _hexaChord;

    public JTransformPanel(HexaChord h, Parameters p) {
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.gray));

        // Initializing attributes
        _trans = new Vector(0.0f, 0.0f);
        _rot = new Angle(1.f * (float) Math.PI / 3);
        _zoom = 1.f;
        _antialiasing = true;
        set_size();

        // Adding listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        addComponentListener(this);
    }

    public JTransformPanel() {
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.gray));

        // Initializing attributes
        _trans = new Vector(0.0f, 0.0f);
        _rot = new Angle(0.0f);
        _zoom = 1.f;
        _antialiasing = true;
        set_size();

        // Adding listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        addComponentListener(this);
    }

    public void add_layer(Layer l) {
        layers.add(l);
    }

    // public Layer get_layer(int n) {
    // return layers.get(n);
    // }

    // Getters

    final float get_w_2() {
        return _w_2;
    }

    final float get_h_2() {
        return _h_2;
    }

    final float get_zoom(float x) {
        return x * _zoom;
    }

    final float get_unzoom(float x) {
        return x / _zoom;
    }

    // Setters
    final private void set_size() {
        _w = (int) getSize().getWidth();
        _h = (int) getSize().getHeight();
        _w_2 = .5f * (float) getSize().getWidth();
        _h_2 = .5f * (float) getSize().getHeight();
        _image = new BufferedImage(Math.max(1, _w), Math.max(1, _h), BufferedImage.TYPE_INT_RGB);
        _graph = _image.createGraphics();
        on_transformation();
    }

    final private void switch_antialiasing() {
        _antialiasing = !_antialiasing;
    }

    // Geometric transformations
    private void translate(float dx, float dy) {
        _trans.translate(dx, dy);
        on_transformation();
    }

    private void untranslate(Vector p) {
        translate(-p.x, -p.y);
    }

    private void rotate(float dtheta) {
        _rot.set_angle(_rot.theta + dtheta);
        _trans.rotate(dtheta);
        on_transformation();
    }

    private void scale(float dz) {
        _trans.unscale(_zoom);
        _zoom = Math.max(Math.min(_zoom * (1 + dz), 6f), 0.4f);
        _trans.scale(_zoom);
        on_transformation();
    }

    // private void update_notes() {
    // on_transformation();
    // }

    private void on_transformation() {
        Iterator<Layer> layer = layers.iterator();
        while (layer.hasNext())
            layer.next().on_transformation();
        repaint();
    }

    // Coordinates transformations
    final public void localToGlobal(Vector p) {
        p.scale(_zoom);
        p.rotate(_rot);
        p.translate(_trans.x, _trans.y);
    }

    final public Vector localToGlobal(float x, float y) {
        Vector p = new Vector(x, y);
        localToGlobal(p);
        return p;
    }

    final public void globalToLocal(Vector p) {
        p.untranslate(_trans.x, _trans.y);
        p.unrotate(_rot);
        p.unscale(_zoom);
    }

    final public Vector globalToLocal(float i, float j) {
        Vector p = new Vector(i, j);
        globalToLocal(p);
        return p;
    }

    final public void pixelToGlobal(Vector p) {
        p.x = p.x - _w_2;
        p.y = _h_2 - p.y;
    }

    final public Vector pixelToGlobal(int pi, int pj) {
        Vector p = new Vector(pi, pj);
        pixelToGlobal(p);
        return p;
    }

    final public void globalToPixel(Vector p) {
        p.x = p.x + _w_2;
        p.y = _h_2 - p.y;
    }

    final public Vector globalToPixel(float i, float j) {
        Vector p = new Vector(i, j);
        globalToPixel(p);
        return p;
    }

    final public void localToPixel(Vector p) {
        localToGlobal(p);
        globalToPixel(p);
    }

    final public void pixelToLocal(Vector p) {
        pixelToGlobal(p);
        globalToLocal(p);
    }

    final public Vector localToPixel(float x, float y) {
        Vector p = new Vector(x, y);
        localToPixel(p);
        return p;
    }

    final public Vector pixelToLocal(int pi, int pj) {
        Vector p = new Vector(pi, pj);
        pixelToLocal(p);
        return p;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (Parameters.getInstance().is_musification()) {
            on_transformation(); // Commente cara fait boucler le programme
        }
        _graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                _antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        _graph.setColor(getBackground());
        _graph.fillRect(0, 0, _w, _h);
    }

    // Mouse Events Listener
    private int _mouseX;
    private int _mouseY;

    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() >= 2) {
            translate(_w_2 - evt.getX(), evt.getY() - _h_2);
            repaint();
        }
    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
        _mouseX = evt.getX();
        _mouseY = evt.getY();
    }

    public void mouseReleased(MouseEvent evt) {
    }

    public void mouseDragged(MouseEvent evt) {
        int modifiers = evt.getModifiers();
        if (((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
                || ((modifiers & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK))
            rotate((float) Math.atan2(_mouseY - _h_2, _mouseX - _w_2)
                    - (float) Math.atan2(evt.getY() - _h_2, evt.getX() - _w_2));
        else
            translate(evt.getX() - _mouseX, _mouseY - evt.getY());
        _mouseX = evt.getX();
        _mouseY = evt.getY();
        repaint();
    }

    public void mouseMoved(MouseEvent evt) {
    }

    public void mouseWheelMoved(MouseWheelEvent evt) {
        scale(-0.1f * (float) evt.getWheelRotation());
        repaint();
    }

    // Key Events Listener
    public void keyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
        case KeyEvent.VK_DOWN:
            translate(0, 10);
            break;
        case KeyEvent.VK_UP:
            translate(0, -10);
            break;
        case KeyEvent.VK_RIGHT:
            translate(-10, 0);
            break;
        case KeyEvent.VK_LEFT:
            translate(10, 0);
            break;
        case KeyEvent.VK_SPACE:
            untranslate(_trans);
            break;
        case KeyEvent.VK_A:
            switch_antialiasing();
            break;
        }
        repaint();
    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

    // Component Events Listener
    public void componentShown(ComponentEvent evt) {
    }

    public void componentHidden(ComponentEvent evt) {
    }

    public void componentMoved(ComponentEvent evt) {
    }

    public void componentResized(ComponentEvent evt) {
        set_size();
    }

}
