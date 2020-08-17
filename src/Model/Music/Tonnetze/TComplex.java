package Model.Music.Tonnetze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Interface.GraphFrame;
import Model.Music.Parameters;
import Model.Music.PitchClassSet;
import Model.Music.STIntervallicStructure;
import Utils.GraphViz;
import Utils.UndirectedGraph;

public abstract class TComplex extends ChordComplex  {

	protected STIntervallicStructure _intervallic_structure;
	protected FoldedGraphTonnetz _folded_graph_tonnetz;
	
	public STIntervallicStructure get_STIntervallicStructure() {
		return _intervallic_structure;
	}
	
	public FoldedGraphTonnetz get_folded_graph_tonnetz() {
		return _folded_graph_tonnetz;
	}
	
	@Override
	public int get_dimension(){
		return _intervallic_structure.size()-1;
	}

	public abstract String get_latex_name();
	
	public abstract HashSet<TComplex> get_vpl_neighbor_complexes(HashSet<TComplex> complex_set);
	
	public static UndirectedGraph get_pvl_graph(HashSet<TComplex> complex_set){

		ArrayList<TComplex> nodes = new ArrayList<TComplex>(complex_set);
		HashSet<HashSet<Integer>> links = new HashSet<HashSet<Integer>>();
		
		for (int i=0;i<nodes.size();i++){
			TComplex complex = nodes.get(i);
			for (TComplex neighbor : complex.get_vpl_neighbor_complexes(complex_set)){
				HashSet<Integer> link = new HashSet<Integer>();
				link.add(i); link.add(nodes.indexOf(neighbor));
				if (link.size()>1) links.add(link);
			}
		}
		return new UndirectedGraph(nodes, links);
	}
	
	public static void make_pvl_graph(HashSet<TComplex> complex_set){
		
		boolean latex = true;
		get_pvl_graph(complex_set).make_pdf(latex);
		//get_pvl_graph(complex_set).print_dot(latex);
	}

}
