package edu.stanford.math.plex_viewer.gl;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import Model.Music.Parameters;
import Model.Music.PitchClassSet;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.metric.interfaces.AbstractObjectMetricSpace;
import edu.stanford.math.plex_viewer.color.ColorScheme;

public class GLUtility {
	
	private final static boolean VERTICES_BLACK = true;
	private static boolean static_color = false;
	
	public static  void drawPoint(GL2 gl, double[] point, float[] color) {
		gl.glColor3fv(color, 0);

		if (point.length == 1) {
			gl.glVertex2d(point[0], 0);
		} else if (point.length == 2) {
			gl.glVertex2d(point[0], point[1]);
		} else if (point.length >= 3) {
			gl.glVertex3d(point[0], point[1], point[2]);
		}
	}
	
	public static  void drawPoint(GL2 gl, double[] point, ColorScheme<double[]> colorScheme) {
		drawPoint(gl, point, colorScheme.computeColor(point));
	}
	
	public static void drawSimplex(GL2 gl, Simplex simplex, ColorScheme<Simplex> colorScheme, AbstractObjectMetricSpace<double[]> metricSpace) {
		drawSimplex(gl, simplex, colorScheme.computeColor(simplex), metricSpace);
	}
	
	public static void drawSimplex(GL2 gl, Simplex simplex, float[] color, AbstractObjectMetricSpace<double[]> metricSpace) {
		
		int[] vertices = simplex.getVertices();
		ArrayList<Integer> vertex_list = new ArrayList<Integer>();
		for (int i : vertices) vertex_list.add(i);
		PitchClassSet current_chord = Parameters.getInstance().get_colStream().get_current_STChord();
		
		int glShapeCode = 0;
		if (simplex.getDimension() == 0) {
			glShapeCode = GL.GL_POINTS;
			if (VERTICES_BLACK) {
				if (static_color){
					gl.glColor3i(0,0,0);
				} else {
					if (current_chord.contains(vertex_list.get(0))) gl.glColor4b((byte)107, (byte)107, (byte)-127, (byte)127);
					else gl.glColor3i(0,0,0);
				}
			}
			else gl.glColor3fv(color,0);
			//gl.glColor3i(0,0,0);
			//gl.glColor3f((float)0.6,(float)1.0,(float)1.0);
		} else if (simplex.getDimension() == 1) {
			glShapeCode = GL.GL_LINES;
			
			if (static_color){
				gl.glColor3i(0,0,0);
			} else {
				if (current_chord.containsAll(vertex_list)){
					gl.glLineWidth((float) 6.0);
					gl.glColor3i(0,0,0);
					//gl.glColor4b((byte)107, (byte)107, (byte)-127, (byte)127);
				} else {
					gl.glLineWidth((float) 3.0);
					gl.glColor3i(0,0,0);
					//gl.glColor4b((byte)97, (byte)97, (byte)97, (byte)20);
				}
			}

		} else if (simplex.getDimension() == 2) {
			glShapeCode = GL.GL_TRIANGLES;
			if (static_color){
				gl.glColor3fv(color,0);
			} else {
				if (current_chord.containsAll(vertex_list)){
					gl.glColor3fv(color,0);
					//gl.glColor4b((byte)107, (byte)107, (byte)-127, (byte)127);	// yellow
				} else {
					gl.glColor4b((byte)97, (byte)97, (byte)97, (byte)20);
				}
			}
						
			
			// active
			//gl.glColor4b((byte)127, (byte)127, (byte)-127, (byte)127);
			//inactive
			
		}
		
		gl.glPointSize(15);
		
		
		
		
		gl.glBegin(glShapeCode);
		for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++) {
			double[] point = metricSpace.getPoint(vertices[vertexIndex]);
			//gl.glColor3fv(color,0);
			if (point.length == 1) {
				gl.glVertex2d(point[0], 0);
			} else if (point.length == 2) {
				gl.glVertex2d(point[0], point[1]);
			} else if (point.length >= 3) {
				gl.glVertex3d(point[0], point[1], point[2]);
				//gl.glRasterPos3d(point[0], point[1], point[2]);
//				if(simplex.getDimension()==1){
//					TextRenderer text_renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
//					text_renderer.begin3DRendering();
//				    text_renderer.setColor(Color.BLACK);
//				    text_renderer.draw3D("A",0,(float)0.5,(float)0.5,(float)0.05);
//				    text_renderer.end3DRendering();
//				}
			}
			
		}
		gl.glEnd();
//		TextRenderer text_renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
//		text_renderer.begin3DRendering();
//		text_renderer.setColor(Color.BLACK);
//		for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++) {
//			double[] point = metricSpace.getPoint(vertices[vertexIndex]);
//			if(simplex.getDimension()==1){
//				text_renderer.draw3D(String.valueOf(vertexIndex),(float)point[0], (float)point[1], (float)point[2],(float)0.01);
//			}
//		}
//		text_renderer.end3DRendering();
		
	}
	
	public static void drawText(GL gl,Simplex simplex){
		
	}
	
	public static void switch_static_color(){
		static_color = !static_color;
	}
	

}
