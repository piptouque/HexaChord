package Interface;

import java.util.ArrayList;

import Model.Music.Constant;
import Model.Music.Parameters;
import Model.Music.PitchClassSet;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;
import Utils.Vector;

public class MusicSubgrid extends Subgrid {

	private Parameters _p;
	private PlanarUnfoldedTonnetz _t;
	// private PosChordSeq _seq;
	// private PosColStream _stream;

	// public MusicSubgrid(GridLayer l, HexaChord h, Tonnetz t, PosChordSeq seq) {
	// super(l);
	// _seq = seq;
	// _h = h;
	// _t = t;
	// update();
	// }

	// public MusicSubgrid(GridLayer l, HexaChord h, Tonnetz t, PosColStream s) {
	public MusicSubgrid(GridLayer l, PlanarUnfoldedTonnetz t) {
		super(l);
		_p = Parameters.getInstance();
		_t = t;
		if (_p.get_colStream().get_current_col().to_PitchClassSet(Constant.N).size() == 3) {
			update();
		}
	}

	public void update() {
		// STChord ch = _seq.get_current_chord().to_STChord(Constant.N);
		PitchClassSet ch = _p.get_colStream().get_current_col().to_PitchClassSet(Constant.N);
		// System.out.println("Voici ch : "+ch);

		assert (ch.size() == 3) : "Erreur de dtermination de la sous-grille : pas 3 sons";
		// SimplicialTonnetz t = _h.getCurrentTonnetz();

		int o = _t.getOrbit(ch);

		int m[] = new int[3];
		int i;
		for (i = 0; i < 3; i++)
			m[i] = (Integer) ch.toArray()[i];
		int n[] = new int[3];

		int nbNode = 0;
		if (_t.getOrbit(m[0]) == o)
			n[nbNode++] = m[0];
		if (_t.getOrbit(m[1]) == o)
			n[nbNode++] = m[1];
		if (_t.getOrbit(m[2]) == o)
			n[nbNode++] = m[2];

		ArrayList<Integer> org = null;
		ArrayList<Integer> i1 = null;
		ArrayList<Integer> i2 = null;

		// System.out.println("Compute coordinates of chord " + ch
		// + " in tonnetz " + _t);
		// System.out.println(" - Choosen orbit: " + o);
		// System.out.println(" - Number of note in the orbit: " + nbNode);

		switch (nbNode) {
		case 1:
			org = _t.toCoord(n[0] - o);
			assert (org.size() != 0);
			set_org(org.get(0) - org.get(1), org.get(1) - org.get(2));
			set_v1(1, 0);
			set_v2(0, 1);
			return;
		case 2:
			org = _t.toCoord(n[0] - o);
			assert (org.size() != 0);
			i1 = _t.toCoord(n[1] - n[0]);
			assert (i1.size() != 0);
			set_org(org.get(0) - org.get(1), org.get(1) - org.get(2));
			set_v1(i1.get(0) - i1.get(1), i1.get(1) - i1.get(2));
			set_v2(i1.get(2) - i1.get(1), i1.get(0) - i1.get(1));
			return;
		case 3:
			float radius = Float.POSITIVE_INFINITY;
			for (i = 0; i < 3; i++) {
				org = _t.toCoord(n[i] - o);
				assert (org.size() != 0);
				i1 = _t.toCoord(n[(i + 1) % 3] - n[i]);
				assert (i1.size() != 0);
				i2 = _t.toCoord(n[(i + 2) % 3] - n[i]);
				assert (i2.size() != 0);
				Vector v1 = new Vector(i1.get(0) - i1.get(1), i1.get(1) - i1.get(2));
				Vector v2 = new Vector(i2.get(0) - i2.get(1), i2.get(1) - i2.get(2));

				// System.out.println(i1 + " " + i2);
				// System.out.println(v1 + " " + v2);
				supergridToEuclidean(v1);
				supergridToEuclidean(v2);
				// System.out.println(v1 + " " + v2);
				float rad = v1.distance2(v2);
				if (rad <= radius) {
					set_org(org.get(0) - org.get(1), org.get(1) - org.get(2));
					set_v1(i1.get(0) - i1.get(1), i1.get(1) - i1.get(2));
					set_v2(i2.get(0) - i2.get(1), i2.get(1) - i2.get(2));
					// System.out.println("0K " + n[i]);
					radius = rad;
				}
				System.out.println();
			}
			return;
		default:
			set_org(0, 0);
			set_v1(1, 0);
			set_v2(0, 1);
		}
	}
}
