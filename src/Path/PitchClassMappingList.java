package Path;

import java.util.ArrayList;

import Utils.Table;

public class PitchClassMappingList extends ArrayList<int[]> {

	private static final long serialVersionUID = 1L;

	public PitchClassMappingList(){
		super();
	}
	
	public String toString(){
		return Table.toString(this);
	}
	
}
