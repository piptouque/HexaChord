package edu.stanford.math.plex_viewer.color;

import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.metric.interfaces.AbstractObjectMetricSpace;
import edu.stanford.math.primitivelib.autogen.array.FloatArrayMath;

/**
 * This class computes the color of a geometric realization of a simplex but averaging
 * the colors of its vertices. The colors of its vertices are determined by a color scheme
 * on the underlying metric space.
 * 
 * @author Andrew Tausz
 *
 * @param <T> the type of the underlying metric space (e.g. double[])
 */
public class AveragedSimplicialColorScheme<T> extends ColorScheme<Simplex> {
	private final AbstractObjectMetricSpace<T> metricSpace;
	private final ColorScheme<T>  geometricColorScheme;

	public AveragedSimplicialColorScheme(AbstractObjectMetricSpace<T> metricSpace, ColorScheme<T>  geometricColorScheme) {
		this.metricSpace = metricSpace;
		this.geometricColorScheme = geometricColorScheme;
	}
	
	public float[] computeColor(Simplex simplex) {
		float[] rgb = new float[3];

		int[] vertices = simplex.getVertices();
		for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++) {
			T vertexPoint = this.metricSpace.getPoint(vertices[vertexIndex]);
			FloatArrayMath.accumulate(rgb, this.geometricColorScheme.computeColor(vertexPoint));
		}

		if (simplex.getDimension()==0){
			switch(simplex.getVertices()[0])
			{
			// Pour le mapping pitch class -> color : http://simple.wikipedia.org/wiki/Color_wheel
			case 0 : rgb =  new float[]{1,0,0};break;
			case 1 : rgb = new float[]{1,(float)0.5,0};break;
			case 2 : rgb = new float[]{1,1,0};break;
			case 3 : rgb = new float[]{(float)0.5,1,0};break;
			case 4 : rgb = new float[]{0,1,0};break;
			case 5 : rgb = new float[]{0,1,(float)0.5};break;
			case 6 : rgb = new float[]{0,1,1};break;
			case 7 : rgb = new float[]{0,(float)0.5,1};break;
			case 8 : rgb = new float[]{0,0,1};break;
			case 9 : rgb = new float[]{(float)0.5,0,1};break;
			case 10 : rgb = new float[]{1,0,1};break;
			case 11 : rgb = new float[]{1,0,(float)0.5};break;
			
			}

		}
		
		//System.out.println("computeColor "+simplex.getVertices()[0]+" rgb : "+Table.toString(rgb));
//		System.err.println("AIE");
//		System.exit(0);
		FloatArrayMath.inPlaceMultiply(rgb, 1.0f / (float) vertices.length);
		return rgb;
	}
}
