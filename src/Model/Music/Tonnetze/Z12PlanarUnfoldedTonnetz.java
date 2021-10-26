package Model.Music.Tonnetze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import Interface.GraphFrame;
import Model.Music.Interval;
import Model.Music.Parameters;
import Model.Music.PitchClassSet;
import Model.Music.STIntervallicStructure;
import Utils.GraphViz;

public class Z12PlanarUnfoldedTonnetz extends PlanarUnfoldedTonnetz {

	private Z12PlanarUnfoldedTonnetz(ArrayList<Integer> list) {
		STIntervallicStructure is = new STIntervallicStructure(list);
		for (TIChordComplex complex : TIChordComplex.getZ12Tonnetz_n_ChordComplexList(3)) {
			if (complex.get_STIntervallicStructure().equals(is)
					|| complex.get_STIntervallicStructure().get_filpped_ring().equals(is))
				_folded_complex = complex;
		}
		assert (_folded_complex != null) : "Folded complex not found " + list;

		_N = 12;
		_coords = null;
		_generators = new TonnetzGen(list);
		set_folded_tonnetz();
	}

	public PitchClassSet get_representative_chord() {
		PitchClassSet c = new PitchClassSet();
		c.add(0);
		int n = 0;
		for (int i = 0; i < _generators.size() - 1; i++) {
			c.add(n + _generators.get(i));
			n = _generators.get(i);
		}
		return c;
	}

	static private TreeMap<TonnetzGen, Z12PlanarUnfoldedTonnetz> _tonnetze = new TreeMap<TonnetzGen, Z12PlanarUnfoldedTonnetz>();

	static public Z12PlanarUnfoldedTonnetz getTonnetz(ArrayList<Integer> list) {
		TonnetzGen k = new TonnetzGen(list);
		Z12PlanarUnfoldedTonnetz t = _tonnetze.get(k);
		if (t == null) {
			t = new Z12PlanarUnfoldedTonnetz(list);
			_tonnetze.put(t.get_generators(), t);
		}
		return t;
	}

	static public Z12PlanarUnfoldedTonnetz getTonnetz(int i1, int i2, int i3) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		l.add(i1);
		l.add(i2);
		l.add(i3);
		return getTonnetz(l);
	}

	static private ArrayList<Z12PlanarUnfoldedTonnetz> _z12TriangularUnfoldedTonnetzList;

	static public ArrayList<Z12PlanarUnfoldedTonnetz> getZ12TriangularUnfoldedTonnetzList() {
		if (_z12TriangularUnfoldedTonnetzList == null) {
			_z12TriangularUnfoldedTonnetzList = new ArrayList<Z12PlanarUnfoldedTonnetz>();
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(1, 1, 10));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(1, 2, 9));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(1, 3, 8));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(1, 4, 7));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(1, 5, 6));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(2, 2, 8));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(2, 3, 7));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(2, 4, 6));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(2, 5, 5));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(3, 3, 6));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(3, 4, 5));
			_z12TriangularUnfoldedTonnetzList.add(getTonnetz(4, 4, 4));
		}
		return _z12TriangularUnfoldedTonnetzList;
	}

	// static public Z12PlanarUnfoldedTonnetz
	// getZ12UnfoldedTonnetz(STIntervallicStructure is){
	// // TODO : envisager les tonnetz dplis non planaires
	// assert is.size() == 3 : "Can not build a PlanarUnfoldedTonnetz from a
	// Interval Structure of size "+is.size();
	//
	// return new Z12PlanarUnfoldedTonnetz(is.get_list());
	// }

	public void set_folded_tonnetz() {
		ArrayList<Integer> mi_generators = new ArrayList<Integer>();
		for (int i : _generators) {
			mi_generators.add(Interval.MI(i, _N));
		}

		for (FoldedGraphTonnetz t : Z12FoldedGraphTonnetz.getZ12FoldedChordGraphTonnetzList()) {
			if (t.equals(new HashSet<Integer>(mi_generators))) {
				_folded_graph_tonnetz = t;
			}
		}
		assert _folded_graph_tonnetz != null : "Can't find corresponding folded tonnetz to " + this;
	}

	public static ArrayList<String> getZ12HexaTonnetzNameList() {
		ArrayList<String> name_list = new ArrayList<String>();
		for (Z12PlanarUnfoldedTonnetz t : getZ12TriangularUnfoldedTonnetzList())
			name_list.add(t.toString());
		return name_list;
	}

	public static String[] getZ12HexaTonnetzNameTable() {
		String[] name_table = new String[getZ12TriangularUnfoldedTonnetzList().size()];
		for (int i = 0; i < _z12TriangularUnfoldedTonnetzList.size(); i++)
			name_table[i] = _z12TriangularUnfoldedTonnetzList.get(i).toString();
		return name_table;
	}

	public static float getZ12HexaTonnetz_compliance_coef(Z12PlanarUnfoldedTonnetz t) {
		float coef = 0;
		HashSet<Integer> G = new HashSet<Integer>(t._generators);
		// System.out.println("voici le set : "+G);

		// si N est paire
		if (t._N % 2 == 0) {
			for (int g : G) {
				if (g != t._N / 2) {
					coef = coef + 2 / (float) (t._N - 1);
				} else
					coef = coef + 1 / (float) (t._N - 1);
			}
		} else { // si N est impaire
			for (int g : G) {
				coef = coef + 2 / (float) (t._N - 1);
			}
		}
		return 1 / coef;
	}

	public static int getZ12HexaTonnetzIndex(Z12PlanarUnfoldedTonnetz h) {
		// TODO: relies on object references !!!
		return getZ12TriangularUnfoldedTonnetzList().indexOf(h);
	}

	// Fonction retournant la liste des Tonnetz voisins d'un Tonnetz selon le critre
	// :
	// 2 Tonnetz sont voisins si un mouvement minimal permet de passer de l'un
	// l'autre
	public HashSet<Z12PlanarUnfoldedTonnetz> neighbor_tonnetzs() {
		HashSet<Z12PlanarUnfoldedTonnetz> list = new HashSet<Z12PlanarUnfoldedTonnetz>();
		PitchClassSet c = this.get_representative_chord();
		ArrayList<PitchClassSet> neighbors = c.semitone_close_chords();
		for (PitchClassSet n : neighbors) {
			Z12PlanarUnfoldedTonnetz t = n
					.get_corresponding_Tonnetz(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList());
			if (t != null && t != this)
				list.add(t);
		}
		return list;
	}

	// Fonction retournant la liste des Tonnetz voisins dans le graphe des Tonnetz
	// selon le critre :
	// 2 Tonnetz sont voisins si un mouvement minimal permet de passer de l'un
	// l'autre
	public static HashSet<HashSet<Z12PlanarUnfoldedTonnetz>> neighbor_list(
			ArrayList<Z12PlanarUnfoldedTonnetz> tonnetzs) {
		HashSet<HashSet<Z12PlanarUnfoldedTonnetz>> neighbor_list = new HashSet<HashSet<Z12PlanarUnfoldedTonnetz>>();

		for (Z12PlanarUnfoldedTonnetz t : Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList()) {
			HashSet<Z12PlanarUnfoldedTonnetz> set = t.neighbor_tonnetzs();
			for (Z12PlanarUnfoldedTonnetz n : set) {
				HashSet<Z12PlanarUnfoldedTonnetz> neighborhood = new HashSet<Z12PlanarUnfoldedTonnetz>();
				neighborhood.add(n);
				neighborhood.add(t);
				neighbor_list.add(neighborhood);
			}
		}
		return neighbor_list;
	}

	public static GraphFrame dot_builder() {

		String type = "plain";
		File out = new File("tmp/out." + type);

		// La section suivante est dcommenter pour reconstruire le fichier .plain avec
		// neato
		// ncssite /usr/local/bin/neato
		// Lorsqu'elle est commente, on va chercher les donnes dans tmp/out.plain

		// ArrayList<Z12PlanarUnfoldedTonnetz> tonnetzs =
		// param.get_Z12_triangular_unfolded_tonnetz_list();
		// HashSet<HashSet<Z12PlanarUnfoldedTonnetz>> neighbor_list =
		// neighbor_list(tonnetzs);
		// GraphViz gv = new GraphViz();
		// gv.addln(gv.start_graph());
		// for (int i=0;i<tonnetzs.size();i++){
		// gv.addln(i+" [label=\""+tonnetzs.get(i)+"\"];");
		// }
		// for (HashSet<Z12PlanarUnfoldedTonnetz> n : neighbor_list){
		// ArrayList<Z12PlanarUnfoldedTonnetz> l = new
		// ArrayList<Z12PlanarUnfoldedTonnetz>(n);
		// //gv.addln("\""+l.get(0)._generators+"\" -- \""+l.get(1)._generators+"\";");
		// //gv.addln(SimplicialTonnetz.getZ12HexaTonnetzIndex(l.get(0))+" --
		// "+SimplicialTonnetz.getZ12HexaTonnetzIndex(l.get(1)));
		// gv.addln(tonnetzs.indexOf(l.get(0))+" -- "+tonnetzs.indexOf(l.get(1))+";");
		// }
		// gv.addln(gv.end_graph());
		// //System.out.println(gv.getDotSource());
		// gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );

		StringBuffer buffer = new StringBuffer();
		try {
			FileReader reader = new FileReader(out);
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				buffer.append(line + " ");
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("Voici le buffer :
		// "+Table.toString(buffer.toString().split(" ")));
		GraphFrame graph_frame = new GraphFrame(buffer.toString().split(" "));
		return graph_frame;
	}

	/* ------ Compliance ------ */

	/* -- Horizontal Compliance -- */

	public int Hcompliance(ArrayList<Integer> list) {
		int compliance = 0;
		for (int i : list) {
			if (_generators.contains(i))
				compliance++;
		}
		// System.out.println(list+" gen : "+_generators+" compliance : "+compliance);
		return compliance;
	}

	public static ArrayList<String> get_complex_string_list(ArrayList<Z12PlanarUnfoldedTonnetz> tonnetz_list) {
		ArrayList<String> string_list = new ArrayList<String>();
		for (Z12PlanarUnfoldedTonnetz tonnetz : tonnetz_list)
			string_list.add(tonnetz.toString());
		return string_list;
	}

}
