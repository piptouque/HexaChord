package Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

//import com.sun.javafx.collections.MappingChange;

public class PitchClassMappingTree extends TreeMap<Long,PitchClassMappingList>{

	private static final long serialVersionUID = 1L;

	public PitchClassMappingTree(){
		super();
	}
	
	public PitchClassMappingTree(Set<Long> key_list,PitchClassMappingList unique_mapping){
		super();
		for (long key : key_list) put(key,unique_mapping);
	}

	public void build_mapping(TreeMap<Long, ArrayList<Integer>> origine_pitch_list_stream,TreeMap<Long, ArrayList<Integer>> destination_pitch_list_stream) {
		
		if (origine_pitch_list_stream.keySet().size() != destination_pitch_list_stream.keySet().size()){
			System.err.println("Trees building the mapping don't have the same number of keys");
		}
		for (long key : origine_pitch_list_stream.keySet()){
			if (destination_pitch_list_stream.containsKey(key)){
				if (destination_pitch_list_stream.get(key).size() == origine_pitch_list_stream.get(key).size()){
					PitchClassMappingList pitch_class_mapping_list = new PitchClassMappingList();
					for (int i=0;i<origine_pitch_list_stream.get(key).size();i++){
						pitch_class_mapping_list.add(new int[]{origine_pitch_list_stream.get(key).get(i),destination_pitch_list_stream.get(key).get(i)});
					}
					put(key,pitch_class_mapping_list);
				} else {
					assert false : "destination tree doesn't have the same pc list at the key "+key+" origine pc list : "+origine_pitch_list_stream.get(key)+" destination pc list : "+destination_pitch_list_stream.get(key);
				}
			} else {
				assert false : "destination tree doesn't have the key "+key;
			}
		}
		//System.out.println("mapping : "+this);
	}
	
	private static boolean KEEP_PC_NOT_IN_TONNETZ = false;
	
	public int get_destination_pc(long key, int origine_pc){
		try{
			for(int[] mapping : get(key)){
				//System.out.println("mapping0 = "+mapping[0]+" origine_pc = "+origine_pc);
				if (mapping[0] == origine_pc) {
					return mapping[1];
				}
			}
		} catch (NullPointerException e) {
			System.out.println("The mapping tree doesn't contain the key "+key+" Floor mapping considered for the pitch "+origine_pc);
			for(int[] mapping : floorEntry(key).getValue()){
				if (mapping[0] == origine_pc) {
					return mapping[1];
				}
			}
			return -1;
		}
		if (KEEP_PC_NOT_IN_TONNETZ) return origine_pc;
		else return -1;
//		System.out.println("ERROR ! key : "+key+" original_pc : "+origine_pc);
//		System.out.println("mapping tree : "+this);
//		assert false ;
//		return -1;
	}
	
	
}
