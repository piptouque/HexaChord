package Utils;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class PieDiagram extends Diagram {

	/**
	 * @uml.property  name="_dataset"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private DefaultPieDataset _dataset;
	
	public PieDiagram(DefaultPieDataset dataset, String name) {
		_name = name;
		_dataset = dataset;
	}

	public void display_pie_diagram() {
		JFreeChart chart = ChartFactory.createPieChart(
				_name, _dataset, true, true,false );
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
