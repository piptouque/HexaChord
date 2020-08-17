package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Interface.GraphFrame;
import Model.Music.Tonnetze.TComplex;
import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;

public class UndirectedGraph {
	
	private ArrayList<TComplex> _nodes;
	private HashSet<HashSet<Integer>> _links;

	public UndirectedGraph(ArrayList<TComplex> nodes,HashSet<HashSet<Integer>> links){
		_nodes = new ArrayList<TComplex>(nodes);
		_links = new HashSet<HashSet<Integer>>(links);
	}
	
	public GraphViz get_graphviz_object(boolean latex) {
		
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph(latex));
		for (int i=0;i<_nodes.size();i++){
			if (latex){
				gv.addln(i+" [texlbl=\""+_nodes.get(i).get_latex_name()+"\""+",lblstyle=\"font=\\normalsize\""+"];");
			} else {
				gv.addln(i+" [label=\""+_nodes.get(i)+"\"];");	
			}
		}
		
		for (HashSet<Integer> link : _links){
			ArrayList<Integer> list_link = new ArrayList<Integer>(link);
			gv.addln(list_link.get(0)+" -- "+list_link.get(1)+";");
		}
		
		gv.addln(gv.end_graph());
		
		System.out.println(gv.getDotSource());
		return gv;
		//return gv.getDotSource();
		
//		String type = "plain";
//		File out = new File("tmp/out." + type);   // out.gif in this example
//		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
//		StringBuffer buffer = new StringBuffer();
//		try {
//			FileReader reader = new FileReader(out);
//			BufferedReader br = new BufferedReader(reader);
//			String line = br.readLine();
//			while (line != null){
//				buffer.append(line+" ");
//				line = br.readLine();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.println("Voici le buffer : "+Table.toString(buffer.toString().split(" ")));
//		GraphFrame graph_frame = new GraphFrame(buffer.toString().split(" "), param);
//		return graph_frame;
	}
	
	public void print_dot(boolean latex){
		System.out.println(get_graphviz_object(latex).getDotSource());
	}
	
	public void make_pdf(boolean latex){
		GraphViz gv = get_graphviz_object(latex);
		String type = "pdf";
		File out = new File("tmp/out." + type);
		gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
//		StringBuffer buffer = new StringBuffer();
//		try {
//			FileReader reader = new FileReader(out);
//			BufferedReader br = new BufferedReader(reader);
//			String line = br.readLine();
//			while (line != null){
//				buffer.append(line+" ");
//				line = br.readLine();
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.println("Voici le buffer : "+Table.toString(buffer.toString().split(" ")));
//		GraphFrame graph_frame = new GraphFrame(buffer.toString().split(" "), param);
//		return graph_frame;

	}


}
