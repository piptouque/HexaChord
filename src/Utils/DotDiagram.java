package Utils;


import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;

public class DotDiagram extends Diagram{

	private XYDataset _dataset;
	
	public DotDiagram(String name, XYDataset dataset){
		_name = name;
		_dataset = dataset;
	}
	
	public void display_dot_diagram() {
		
		JFreeChart chart = ChartFactory.createScatterPlot(_name, "horizontal compliance", "vertical compliance",
				_dataset, PlotOrientation.VERTICAL, true, true, false);
		
		//XYPlot xyPlot = (XYPlot) chart.getPlot();
		//renderer.setSeriesPaint(0, Color.red);
		display_diagram(chart);
	
	}
	
	public void display_diagram(JFreeChart chart) {
    	ChartPanel crepart = new ChartPanel(chart);
        _chartFrame = new JFrame("Chart");
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
