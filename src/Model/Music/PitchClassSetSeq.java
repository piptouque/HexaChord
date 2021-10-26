package Model.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream;
import edu.stanford.math.plex4.streams.interfaces.AbstractFilteredStream;

import Model.Music.Tonnetze.Z12PlanarUnfoldedTonnetz;
import Utils.BarDiagram;
import Utils.PieDiagram;

public class PitchClassSetSeq extends ArrayList<PitchClassSet> {

	private static final long serialVersionUID = 1L;
	private String _name = "Default STChord Sequence";

	public PitchClassSetSeq() {
	}

	public PitchClassSetSeq(String name) {
		_name = name;
	}

	public PitchClassSetSeq(ArrayList<PitchClassSet> list) {
		addAll(list);
	}

	public float[] Z12HexaTonnetz_compliance_average_table(ArrayList<ArrayList<float[]>> compliance_table) {
		float average_table[] = new float[12];
		for (int n = 0; n < average_table.length; n++) {
			float sum = 0;
			for (float[] f : compliance_table.get(n)) {
				// System.out.println("f[1] : "+f[1]);
				sum = sum + f[1];
			}
			average_table[n] = sum / (this.size());
		}
		return average_table;
	}

	public float[] Z12HexaTonnetz_compliance_corrected_average_table(ArrayList<ArrayList<float[]>> compliance_table) {
		float average_table[] = Z12HexaTonnetz_compliance_average_table(compliance_table);
		float corrected_average_table[] = new float[12];
		for (int i = 0; i < average_table.length; i++) {
			// System.out.println("Voici le coef :
			// "+1/SimplicialTonnetz.getZ12HexaTonnetz_compliance_coef(SimplicialTonnetz.getZ12HexaTonnetzList().get(i)));
			// Compliance corrige, on multiplie toutes les moyennes par le coef qui fait que
			// l'alatoire tend vers1. Puis on retire 1 pour avoir la diff par rapport
			// l'alatoire.
			corrected_average_table[i] = average_table[i] * Z12PlanarUnfoldedTonnetz.getZ12HexaTonnetz_compliance_coef(
					Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i));
			// Compliance corrige, on se contente de retirer sa valeur moyenne caque myenne
			// de compliance. (On ne normalise pas pour les rendre tous quitables)
			corrected_average_table[i] = average_table[i]
					- (1 / Z12PlanarUnfoldedTonnetz.getZ12HexaTonnetz_compliance_coef(
							Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i)));

		}
		return average_table;
		// return corrected_average_table;
	}

	// public void
	// Z12HexaTonnetz_compliance_averages_display(ArrayList<ArrayList<float[]>>
	// compliance_table, String name, int compactness_dimension) {
	// //float average_table[] =
	// Z12HexaTonnetz_compliance_average_table(compliance_table);
	// float average_table[] =
	// Z12HexaTonnetz_compliance_corrected_average_table(compliance_table);
	//// System.out.println("Voici l' average table :
	// "+Table.toString(average_table));
	// DefaultPieDataset dataset = new DefaultPieDataset();
	// DefaultCategoryDataset h_dataset = new DefaultCategoryDataset();
	//
	// for (int i = 0;i<average_table.length;i++){
	// dataset.setValue(Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i).toString(),
	// average_table[i]);
	// h_dataset.setValue(average_table[i], "",
	// Z12PlanarUnfoldedTonnetz.getZ12TriangularUnfoldedTonnetzList().get(i).toString());
	// }
	// PieDiagram diagram = new PieDiagram(dataset, "Complex compactness averages :
	// "+name);
	// diagram.display_pie_diagram();
	// BarDiagram bar_diagram = new BarDiagram(h_dataset, name);
	//
	// String compactness_type;
	// if (compactness_dimension == -1) compactness_type = "abs"+"-compactness";
	// else compactness_type = compactness_dimension+"-compactness";
	// bar_diagram.display_bar_diagram(compactness_type);
	// bar_diagram.display_bar_diagram(compactness_type);
	//
	// }

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public static PitchClassSetSeq random_STChordSeq(int seq_size, int chord_size, int N) {
		PitchClassSetSeq chord_seq = new PitchClassSetSeq("random_chord_seq");
		for (int n = 0; n < seq_size; n++) {
			chord_seq.add(PitchClassSet.random_STChord(chord_size, N));
		}
		return chord_seq;
	}

	public static PitchClassSetSeq random_STChordSeq(int seq_size, int min_chord_size, int max_chord_size, int N) {
		PitchClassSetSeq chord_seq = new PitchClassSetSeq("random_chord_seq");
		for (int n = 0; n < seq_size; n++) {
			chord_seq.add(PitchClassSet.random_STChord(min_chord_size, max_chord_size, N));
		}
		return chord_seq;
	}

	public AbstractFilteredStream<Simplex> get_simplex_stream() throws IOException {
		ExplicitSimplexStream stream = new ExplicitSimplexStream();
		for (PitchClassSet pc_set : list_all_subsets_in_seq()) {
			// first parameter is the pc_set, second is the filtration index
			stream.addElement(pc_set.to_table(), pc_set.size());
		}
		return stream;
	}

	public HashSet<PitchClassSet> get_all_subsets_in_seq() {
		HashSet<PitchClassSet> all_subsets_in_seq = new HashSet<PitchClassSet>();
		for (PitchClassSet pc_set : this) {
			all_subsets_in_seq.addAll(pc_set.get_all_sub_pc_set());
		}
		return all_subsets_in_seq;
	}

	// Get all subsets sorted by size
	public ArrayList<PitchClassSet> list_all_subsets_in_seq() {
		ArrayList<PitchClassSet> all_subsets_in_seq_list = new ArrayList<PitchClassSet>();
		HashSet<PitchClassSet> all_subsets_in_seq_set = get_all_subsets_in_seq();
		int size_max = 0;
		for (PitchClassSet set : this)
			if (set.size() > size_max)
				size_max = set.size();
		for (int i = 1; i <= size_max; i++) {
			for (PitchClassSet pc_set : all_subsets_in_seq_set) {
				if (pc_set.size() == i) {
					all_subsets_in_seq_list.add(pc_set);
				}
			}
		}
		return all_subsets_in_seq_list;
	}

}