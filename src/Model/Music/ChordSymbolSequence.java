package Model.Music;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ChordSymbolSequence extends ArrayList<ChordSymbol>{

	private static final long serialVersionUID = 1L;
	
	public ChordSymbolSequence(ChordSymbol[] chord_list){
		super();
		for (int i=0;i<chord_list.length;i++) add(chord_list[i]);
	}

	public ChordSymbolSequence(List<ChordSymbol> chord_list){
		super();
		for (int i=0;i<chord_list.size();i++) add(chord_list.get(i));
	}
	
	public ChordSymbolSequence(int size){
		super(size);
	}

	public ChordSymbolSequence(String[] chord_list){
		super();
		for (int i=0;i<chord_list.length;i++) add(new ChordSymbol(chord_list[i]));
	}
	
	public ChordSymbolSequence(String chord_seq_string){
		super();
		Scanner scanner = new Scanner(chord_seq_string);
		scanner.useDelimiter(" ");
		while(scanner.hasNext()){
			add(new ChordSymbol(scanner.next()));
		}
	}
	
	public List<Integer> get_coherence_template(){
		List<Integer> int_list = new ArrayList<Integer>(size());
		int current_int=0;
		
		for (int i=0;i<size();i++){
			boolean bool=true;
			for (int j=0;j<i;j++){
				if (bool){
					if (get(i).get_pcs().equals(get(j).get_pcs())) {
						int_list.add(i, int_list.get(j)); 
						bool=false;
					}					
				}
			}
			if (bool) {
				int_list.add(current_int);
				current_int++;
			}
		}
		return int_list;
	}
	
	public String get_coherence_template_string(){
		String str="";
		for (int i : get_coherence_template()) str=str+i;
		return str;
	}
	
	public int get_coherence_value(){
		Set<Integer> int_set = new HashSet<Integer>(get_coherence_template());
		return size()-int_set.size();
	}

	public float get_chord_intersection_mean(){
		float mean = 0;
		List<Integer> intersection_list = new ArrayList<Integer>();
		for (int i=0;i<size();i++){
			if (i==size()-1){
				intersection_list.add(get(i).get_pcs().get_intersection(get(0).get_pcs()).size());
			} else {
				intersection_list.add(get(i).get_pcs().get_intersection(get(i+1).get_pcs()).size());
			}
		}
		for (int i=0;i<intersection_list.size();i++) mean = mean+intersection_list.get(i);
		return mean/(float)size();
	}
	
	public String toString(){
		String str = "";
		for (int i=0;i<size();i++){
			if (i==size()-1){
				str=str+get(i);
			} else {
				str=str+get(i)+" ";
			}
		}
		return str;
	}
	
	public List<PitchClassSet> get_pcs_list(){
		List<PitchClassSet> pcs_list = new ArrayList<PitchClassSet>();
		for (int i=0;i<size();i++) pcs_list.add(get(i).get_pcs());
		return pcs_list;
	}
}
