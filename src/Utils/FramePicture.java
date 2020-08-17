package Utils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import Interface.GridFrame;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public abstract class FramePicture {

	public static void save (Frame j, String name, String dir, String format) {
		
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		pp = pp+"/out/"+dir+"/"+name+"."+format;

		File file = new File(pp);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("file "+file+" does not exist");
			e1.printStackTrace();
		}
		
		  BufferedImage image = new BufferedImage(j.getWidth(),
		                                          j.getHeight(),
		                                          BufferedImage.TYPE_INT_ARGB);
		  Graphics2D g2 = image.createGraphics();
		  j.paint(g2);
		  g2.dispose();

		  try {
			    ImageIO.write(image, format.toUpperCase(), file);
			  } catch (Exception e) { }

	}
	
	public static void save_as_pdf (Frame j, String name, String dir) {
		System.out.println("Hello");
		String pp = null;
		try {
			pp = (new File(".")).getCanonicalPath();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		pp = pp+"/out/"+dir+"/"+name+".pdf";

		File file = new File(pp);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("file "+file+" does not exist");
			e1.printStackTrace();
		}
		
		//PrintFrameToPDF((JFrame) j, file);
		createPdf(false, (GridFrame)j, file);
	}

	public static void PrintFrameToPDF(JFrame j,File file) {
	    try {
	        Document d = new Document();
	        PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(file));
	        d.open();

	        PdfContentByte cb = writer.getDirectContent();
	        PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(),PageSize.A4.getHeight());
	        cb.addTemplate(template, 0, 0);

	        Graphics2D g2d = template.createGraphics(PageSize.A4.getWidth(),PageSize.A4.getHeight());
	        g2d.scale(0.4, 0.4);

	        for(int i=0; i< j.getContentPane().getComponents().length; i++){
	            Component c = j.getContentPane().getComponent(i);
	            //if(c instanceof JLabel || c instanceof JScrollPane){
	                g2d.translate(c.getBounds().x,c.getBounds().y);
	                //if(c instanceof JScrollPane){c.setBounds(0,0,(int)PageSize.A4.getWidth()*2,(int)PageSize.A4.getHeight()*2);}
	                c.paintAll(g2d);
	                c.addNotify();
	            //}
	        }


	        g2d.dispose();

	        d.close();
	    } catch (Exception e) {
	        System.out.println("ERROR: " + e.toString());
	    }
	}
	
	public static void createPdf(boolean shapes,GridFrame j, File file) {
		   Document document = new Document();
		   try {
		      PdfWriter writer;
		      if (shapes)
		         writer = PdfWriter.getInstance(document,
		            new FileOutputStream(file));
		      else
		         writer = PdfWriter.getInstance(document,
		            new FileOutputStream(file));
		      document.open();
		      PdfContentByte cb = writer.getDirectContent();
		      PdfTemplate tp = cb.createTemplate(500, 500);
		      Graphics2D g2;
		      if (shapes)
		         g2 = tp.createGraphicsShapes(500, 500);
		      else
		         g2 = tp.createGraphics(500, 500);
		      //j.print(g2);
		      j.get_p().print(g2);
		      g2.dispose();
		      cb.addTemplate(tp, 30, 300);
		      } catch (Exception e) {
		      System.err.println(e.getMessage());
		   }
		   document.close();
		}
}



