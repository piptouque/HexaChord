package Model.Music.Tonnetze;

import java.util.ArrayList;
import java.util.Collections;

public class TonnetzGen extends UTonnetzVertexCoord {

	private static final long serialVersionUID = 2629006422639861313L;

	public TonnetzGen(ArrayList<Integer> l) {
		super(l);
		assert(l.size()!=0);
		Collections.sort(this);
		assert(this.get(0)>0);
	}
	
	public TonnetzGen(int[] l) {
		super(l);
		assert(l.length!=0);
		Collections.sort(this);
		assert(this.get(0)>0);		
	}

}