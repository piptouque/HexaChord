package Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarDiagram extends Diagram {

	private boolean _black_and_white = true;
	private DefaultCategoryDataset _dataset;

	public BarDiagram(DefaultCategoryDataset dataset, String name, boolean display_legend) {
		System.out.println("bar diagram name : " + name);
		_name = name;
		_dataset = dataset;
		LEGEND = display_legend;
	}

	public void display_bar_diagram(String compactness) {

		_chart = ChartFactory.createBarChart(_name, "", compactness, _dataset, PlotOrientation.VERTICAL, LEGEND, true,
				false);
		//
		// CategoryPlot plot = chart.getCategoryPlot();
		// NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setUpperMargin(0.15);
		//
		// CategoryItemRenderer renderer = plot.getRenderer();
		// ItemLabelPosition position = new ItemLabelPosition();
		// position = new ItemLabelPosition(position.getItemLabelAnchor(),
		// position.getTextAnchor(), position.getRotationAnchor(), 1);
		// renderer.setBasePositiveItemLabelPosition(position);
		// renderer.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 80));

		// create the chart...
		// JFreeChart chart = ChartFactory.createBarChart("Item Label Demo 1",
		// "Category", "Value", _dataset, PlotOrientation.VERTICAL, false,true, false);
		// chart title // domain axis label // range axis label // data // orientation
		// // include legend // tooltips? // URLs?
		// chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = _chart.getCategoryPlot();
		// plot.setBackgroundPaint(Color.lightGray);
		// plot.setDomainGridlinePaint(Color.white);
		// plot.setRangeGridlinePaint(Color.white);
		// NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setUpperMargin(0.15);
		// CategoryItemRenderer renderer = plot.getRenderer();
		// renderer.setItemLabelGenerator(new LabelGenerator(50.0));
		// renderer.setItemLabelFont(new Font("Serif", Font.PLAIN, 10));
		// renderer.setItemLabelsVisible(true);
		// ItemLabelPosition position = new ItemLabelPosition();
		// position = new ItemLabelPosition(position.getItemLabelAnchor(),
		// position.getTextAnchor(), position.getRotationAnchor(), 1);
		// renderer.setPositiveItemLabelPosition(position);
		// return chart;
		// XYPlot xy_plot = chart.getXYPlot();
		// ValueAxis axis = plot.getRangeAxis();
		// axis.setLabelAngle(1);

		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.setBackgroundPaint(Color.white);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

		TickUnits units = new TickUnits();
		units.add(new NumberTickUnit(0.25));

		// TickUnitSource units = new StandardTickUnitSource();
		// rangeAxis.setStandardTickUnits(units);

		// NumberAxis.createStandardTickUnits(locale)
		rangeAxis.setStandardTickUnits(units);
		rangeAxis.setRange(0, 0.3);
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //
		// N'affiche que les entier sur l'axs des ordonnes
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter()); // remove gradiant (remove reflection)

		// renderer.setSeriesPaint(0, ChartColor.LIGHT_BLUE);
		if (_black_and_white)
			set_renderer_BW(renderer);
		// renderer.setItemMargin(-6);
		renderer.setGradientPaintTransformer(null);

		if (LEGEND)
			renderer.setItemMargin(0.0);

		renderer.setShadowVisible(false);

		ChartPanel crepart = new ChartPanel(_chart);
		_chartFrame = new JFrame("Chart");
		_chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // libre la mmoire lorsque la fentre est
																				// ferme
		_chartFrame.add(crepart);
		_chartFrame.setLocation(100, 700);
		_chartFrame.setPreferredSize(new Dimension(1000, 600));
		_chartFrame.setResizable(true);
		_chartFrame.addKeyListener(this);
		// Sizing and displaying the window
		_chartFrame.pack();
		_chartFrame.setVisible(true);
	}

	private static void set_renderer_BW(BarRenderer renderer) {

		int i = 0;

		// dark gray
		BufferedImage bufferedImageDG = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D bigDG = bufferedImageDG.createGraphics();
		bigDG.setColor(Color.DARK_GRAY);
		bigDG.fillRect(0, 0, 5, 5);
		Rectangle imageRectDG = new Rectangle(0, 0, 5, 5);
		TexturePaint tpDG = new TexturePaint(bufferedImageDG, imageRectDG);
		renderer.setSeriesPaint(i, tpDG);
		i++;

		// light gray
		BufferedImage bufferedImageLG = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D bigLG = bufferedImageLG.createGraphics();
		bigLG.setColor(Color.LIGHT_GRAY);
		bigLG.fillRect(0, 0, 5, 5);
		Rectangle imageRectLG = new Rectangle(0, 0, 5, 5);
		TexturePaint tpLG = new TexturePaint(bufferedImageLG, imageRectLG);
		renderer.setSeriesPaint(i, tpLG);
		i++;

		// diag 1
		// BufferedImage bufferedImage0 = new
		// BufferedImage(5,5,BufferedImage.TYPE_INT_RGB);
		// Graphics2D big0 = bufferedImage0.createGraphics();
		// big0.setColor(Color.LIGHT_GRAY);
		// big0.fillRect(0, 0, 5, 5);
		// big0.setColor(Color.DARK_GRAY);
		// big0.drawLine(0, 0, 5, 5);
		// Rectangle imageRect0 = new Rectangle(0, 0, 5, 5 );
		// TexturePaint tp0 = new TexturePaint(bufferedImage0, imageRect0);
		// renderer.setSeriesPaint(i, tp0);
		// i++;

		// // diag 2
		// BufferedImage bufferedImage1 = new
		// BufferedImage(5,5,BufferedImage.TYPE_INT_RGB);
		// Graphics2D big1 = bufferedImage1.createGraphics();
		// big1.setColor(Color.LIGHT_GRAY);
		// big1.fillRect(0, 0, 5, 5);
		// big1.setColor(Color.DARK_GRAY);
		// big1.drawLine(0, 5, 5, 0);
		// Rectangle imageRect1 = new Rectangle(0, 0, 5, 5 );
		// TexturePaint tp1 = new TexturePaint(bufferedImage1, imageRect1);
		// renderer.setSeriesPaint(i, tp1);
		// i++;
		//
		// // horizontal
		// BufferedImage bufferedImage2 = new
		// BufferedImage(5,5,BufferedImage.TYPE_INT_RGB);
		// Graphics2D big2 = bufferedImage2.createGraphics();
		// big2.setColor(Color.LIGHT_GRAY);
		// big2.fillRect(0, 0, 5, 5);
		// big2.setColor(Color.DARK_GRAY);
		// big2.drawLine(0, 0, 5, 0);
		// Rectangle imageRect2 = new Rectangle(0, 0, 5, 5 );
		// TexturePaint tp2 = new TexturePaint(bufferedImage2, imageRect2);
		// renderer.setSeriesPaint(i, tp2);
		// i++;
		//
		// // vertical
		// BufferedImage bufferedImage3 = new
		// BufferedImage(5,5,BufferedImage.TYPE_INT_RGB);
		// Graphics2D big3 = bufferedImage3.createGraphics();
		// big3.setColor(Color.LIGHT_GRAY);
		// big3.fillRect(0, 0, 5, 5);
		// big3.setColor(Color.DARK_GRAY);
		// big3.drawLine(0, 0, 0, 5);
		// Rectangle imageRect3 = new Rectangle(0, 0, 5, 5 );
		// TexturePaint tp3 = new TexturePaint(bufferedImage3, imageRect3);
		// renderer.setSeriesPaint(i, tp3);
		// i++;

		// diag3
		BufferedImage bufferedImage4 = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D big4 = bufferedImage4.createGraphics();
		big4.setColor(Color.DARK_GRAY);
		big4.fillRect(0, 0, 5, 5);
		big4.setColor(Color.WHITE);
		big4.drawLine(0, 0, 5, 5);
		Rectangle imageRect4 = new Rectangle(0, 0, 5, 5);
		TexturePaint tp4 = new TexturePaint(bufferedImage4, imageRect4);
		renderer.setSeriesPaint(i, tp4);
		i++;

		// croix
		BufferedImage bufferedImage5 = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		Graphics2D big5 = bufferedImage5.createGraphics();
		big5.setColor(Color.DARK_GRAY);
		big5.fillRect(0, 0, 5, 5);
		big5.setColor(Color.WHITE);
		big5.drawLine(0, 0, 5, 5);
		big5.drawLine(0, 5, 5, 0);
		Rectangle imageRect5 = new Rectangle(0, 0, 5, 5);
		TexturePaint tp5 = new TexturePaint(bufferedImage5, imageRect5);
		renderer.setSeriesPaint(i, tp5);
		i++;

	}
}
