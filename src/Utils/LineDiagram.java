package Utils;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

public class LineDiagram extends Diagram {


	private XYSeriesCollection _dataset;
	
	public LineDiagram(XYSeriesCollection dataset, String name) {
		_name = name;
		_dataset = dataset;
//        DefaultCategoryDataset union = new DefaultCategoryDataset();
	}
		
	public void display_line_diagram(String x_axis,String y_axis, boolean draw_shapes) {
		
		build_chart(x_axis,y_axis,draw_shapes);
		display_diagram(_chart);
	}
	
	public void build_chart(String x_axis,String y_axis, boolean draw_shapes){
		_chart = ChartFactory.createXYLineChart(
				_name, x_axis, y_axis, _dataset, PlotOrientation.VERTICAL, true,
				true, false );
		
		XYPlot plot = (XYPlot) _chart.getPlot();
		// change the auto tick unit selection to integer units only... 
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		plot.setBackgroundPaint(Color.white);
		//plot.setRangeGridlinePaint(Color.black);
		
		if (draw_shapes){
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setSeriesLinesVisible(0, true);
			renderer.setSeriesShapesVisible(0, true);
			_chart.getXYPlot().setRenderer(renderer);			
		}
//    	ChartPanel crepart = new ChartPanel(chart);
//      _chartFrame = new JFrame("Chart");
//      _chartFrame.add(crepart);
//		_chartFrame.setLocation(0,600);
//		_chartFrame.setPreferredSize(new Dimension(600,400));	
//		_chartFrame.setResizable(true);
//		_chartFrame.addKeyListener(this);
//      // Sizing and displaying the window
//      _chartFrame.pack();
//      _chartFrame.setVisible(true);

	}
	
	public void display_area_diagram() {
		JFreeChart chart = ChartFactory.createXYAreaChart(
				_name, "time", "tonnetz compliance", _dataset, PlotOrientation.VERTICAL, true,
				true, false );
		display_diagram(chart);
//    	ChartPanel crepart = new ChartPanel(chart);
//        _chartFrame = new JFrame("Chart");
//        _chartFrame.add(crepart);
//		_chartFrame.setLocation(0,600);
//		_chartFrame.setPreferredSize(new Dimension(600,400));	
//		_chartFrame.setResizable(true);
//		_chartFrame.addKeyListener(this);
//        // Sizing and displaying the window
//        _chartFrame.pack();
//        _chartFrame.setVisible(true);

	}

	public void display_diagram(JFreeChart chart) {
    	ChartPanel crepart = new ChartPanel(chart);
        _chartFrame = new JFrame("Chart");
        _chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // libère la mémoire lorsque la fenêtre est fermée
        _chartFrame.add(crepart);
		_chartFrame.setLocation(0,600);
		_chartFrame.setPreferredSize(new Dimension(600,400));	
		_chartFrame.setResizable(true);
		_chartFrame.addKeyListener(this);
        // Sizing and displaying the window
        _chartFrame.pack();
        _chartFrame.setVisible(true);
	}
}
