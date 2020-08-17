package edu.stanford.math.plex_viewer.gl;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
//import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import Main.HexaChord;

import com.sun.opengl.util.Animator;
//import com.sun.opengl.util.Screenshot;

import com.sun.opengl.util.awt.Screenshot;

import edu.stanford.math.plex4.io.FileManager;
import edu.stanford.math.plex_viewer.rendering.ObjectRenderer;

/**
 * This class sets up the OpenGL classes and renders a given object. 
 * 
 * @author Andrew Tausz
 *
 */
public class OpenGLManager implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private float x_angle = 0.0f;
	private float y_angle = 0.0f;
	private float z_angle = 0.0f;

	private float zoom = 63.0f;

	private final ObjectRenderer object_renderer;

	private final GLU glu = new GLU();
	private final GLCanvas canvas = new GLCanvas();
	private final Frame frame = new Frame("3D complex");
	private final Animator animator = new Animator(canvas);

	private boolean capture = false;

	public OpenGLManager(ObjectRenderer renderer) {
		this.object_renderer = renderer;
	}

	public void display(GLAutoDrawable gLDrawable) {
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -zoom);

		gl.glRotatef(x_angle, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(y_angle, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(z_angle, 0.0f, 0.0f, 1.0f);

		this.renderShape(gl);

		if (this.capture) {
			this.capture();
		}
	}
	
	public void renderShape(GL2 gl) {
		this.object_renderer.renderShape(gl);
	}

	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {}

	public void init(GLAutoDrawable gLDrawable) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(GLSettings.defaultBackgroundIntensity, GLSettings.defaultBackgroundIntensity, GLSettings.defaultBackgroundIntensity, GLSettings.defaultBackgroundIntensity);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		//text_renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
		this.object_renderer.init(gl);

		//gLDrawable.addKeyListener(this);
		((Component) gLDrawable).addKeyListener(this);
	}

	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		if(height <= 0) {
			height = 1;
		}
		float h = (float)width / (float)height;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50.0f, h, 1.0, 1000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}


	public void exit(){
		animator.stop();
		frame.dispose();
	}

	public void initialize() {
		
		int width = GLSettings.getScreenWidth()/2;
		int height = GLSettings.getScreenHeight()/2;
		
		canvas.addGLEventListener(this);
		frame.add(canvas);
		frame.setSize(width, height);
		frame.setUndecorated(false);
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setExtendedState(Frame.NORMAL);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("bye bye 3D");
				exit();
			}
		});
		frame.setVisible(true);
		animator.start();
		canvas.requestFocus();
	}
	
	public void hide(){
		frame.setVisible(false);
	}
	
	public void justCreateCapture() {
		this.capture = true;
		this.initialize();
		this.exit();
	}

	public void capture() {
		int width = GLSettings.getScreenWidth();
		int height = GLSettings.getScreenHeight();

		String tFileName = "capture-" + FileManager.generateUniqueFileName() + ".png";

		BufferedImage screenShotBuffer = Screenshot.readToBufferedImage(width, height);
		File file = new File("out/CS/"+tFileName);
		//File file = new File(tFileName);

		try {
			ImageIO.write(screenShotBuffer, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.capture = false;
	}

	public void keyPressed(KeyEvent e) {
		this.processDefaultKeys(e);
		this.processSpecializedKeys(e);
	}

	private void processDefaultKeys(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			exit();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.x_angle += 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.x_angle -= 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.y_angle += 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.y_angle -= 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_O) {
			this.z_angle += 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			this.z_angle -= 4.0f;
		} else if (e.getKeyCode() == KeyEvent.VK_M) {
			this.zoom -= 5f;
		} else if (e.getKeyCode() == KeyEvent.VK_L) {
			this.zoom += 0.5f;
		} else if (e.getKeyCode() == KeyEvent.VK_K) {
			this.zoom += 5f;
		} else if (e.getKeyCode() == KeyEvent.VK_I) {
			//FramePicture.save(frame,"simplicial_complex","CS", "png"); // semble marcher pour les JFrame mais pas les Frame utilisant OpenGL
			this.capture = true;
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			GLUtility.switch_static_color();
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			HexaChord.getInstance().play_pause_processing();
		} 
	}

	protected void processSpecializedKeys(KeyEvent e) {
		this.object_renderer.processSpecializedKeys(e);
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void dispose(GLAutoDrawable arg0) {}
}
