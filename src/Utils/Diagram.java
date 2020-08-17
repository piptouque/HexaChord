package Utils;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

import javax.swing.JFrame;

import org.jfree.chart.JFreeChart;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public abstract class Diagram implements KeyListener{
	
	protected boolean LEGEND = false;
	private static int PDF_WIDTH = 800;
	private static int PDF_HEIGHT = 400; 

	//private static String FORMAT = "pdf";  // Ca marche pas ...
	private static String FORMAT = "png";
	
	protected String _name;
	protected JFrame _chartFrame;
	protected JFreeChart _chart;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
        case KeyEvent.VK_I:
        	//String file_name = _name.replaceAll(" ", "_");
//        	String file_name = "histogram";
//        	FramePicture.save(_chartFrame, file_name,"stat", FORMAT);
//        	System.out.println("Diagram saved as "+file_name+"."+FORMAT);
        	save_pdf();
        	break;
        case KeyEvent.VK_ESCAPE:
        	_chartFrame.dispose();
        	break;
		default:
		}
	}
	
	public void save_pdf(){
		
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		pp = pp+"/out/stat/histo.pdf";

		File file = new File(pp);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("file "+file+" does not exist");
			e1.printStackTrace();
		}

		try {
			saveChartAsPDF(file, PDF_WIDTH, PDF_HEIGHT, new DefaultFontMapper());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save_pdf(String file_path){
		
//		String pp = null;
//		try {
//			pp = (new File(".")).getCanonicalPath();
//		} catch (IOException e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//		}
//		pp = pp+"/out/stat/histo.pdf";

		File file = new File(file_path);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("file "+file+" does not exist");
			e1.printStackTrace();
		}

		try {
			saveChartAsPDF(file, PDF_WIDTH, PDF_HEIGHT, new DefaultFontMapper());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void writeChartAsPDF(OutputStream out, int width, int height, FontMapper mapper) throws IOException {
		Rectangle pagesize = new Rectangle(width, height);
		Document document = new Document(pagesize, 50, 50, 50, 50);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.addAuthor("JFreeChart");
			document.addSubject("Demonstration");
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2 = tp.createGraphics(width, height, mapper);
			Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
			_chart.draw(g2, r2D);
			g2.dispose();
			cb.addTemplate(tp, 0, 0);
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		}
		document.close();
	}
	
	public void saveChartAsPDF(File file,int width, int height, FontMapper mapper) throws IOException {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeChartAsPDF(out, width, height, mapper);
		out.close();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
