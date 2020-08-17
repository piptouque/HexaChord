package Model.Music;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

public class HAChordCandidateGraphPath extends TreeMap<Long,HAChordCandidate>{
	
	public HAChordCandidateGraphPath(){
		super();
	}

	public HAChordCandidateGraphPath(HAChordCandidateGraphPath path){
		super(path);
	}
	
	public int get_path_cost(){
		int cost = 0;
		for (long key : keySet()){
			if (key != firstKey()){
				cost = cost + HAChordCandidate.transition_cost(get(lowerKey(key)), get(key));
			}
		}
		return cost;
	}
	
	public HAChordCandidateGraphPath get_defragmented_path(){
		HAChordCandidateGraphPath defragmented_path = new HAChordCandidateGraphPath();
		defragmented_path.put(firstEntry().getKey(),firstEntry().getValue());
		for (long key : keySet()){
			if (key != firstKey()){
				HAChordCandidate current_candidate = get(key);
				HAChordCandidate lower_candidate = lowerEntry(key).getValue();
				if (!current_candidate.equals(lower_candidate)){
					defragmented_path.put(key,get(key));
				}
			}
		}
		return defragmented_path;
	}
	
	public TreeMap<Long,Scale> get_key_stream(){
		TreeMap<Long,Scale> key_stream = new TreeMap<Long,Scale>();
		for (long k : keySet()) key_stream.put(k, get(k).get_key());
		TreeMap<Long,Scale> defrag_key_stream = new TreeMap<Long,Scale>();
		defrag_key_stream.put(key_stream.firstKey(), key_stream.firstEntry().getValue());
		for (long key : key_stream.keySet()){
			if (key != key_stream.firstKey() && !key_stream.get(key).equals(defrag_key_stream.get(defrag_key_stream.lowerKey(key)))){
				//System.out.println("inegal ! "+key_stream.get(key)+" -- "+defrag_key_stream.get(defrag_key_stream.lowerKey(key)));
				defrag_key_stream.put(key,key_stream.get(key));
			}
		}
		System.out.println("defrag key stream : "+defrag_key_stream);
		return defrag_key_stream;
	}
	
	public void output_path_lab_files(String output_chord_file_path,String output_key_file_path, long end_of_last_chord_key,int time_sign_numerator,int resolution) throws FileNotFoundException{
		
		output_chord_analysis_file(output_chord_file_path,end_of_last_chord_key,time_sign_numerator,resolution);
		output_key_analysis_file(output_key_file_path,end_of_last_chord_key,time_sign_numerator,resolution);
		
	}
	
	public void output_chord_analysis_file(String output_chord_file_path,long end_of_last_chord_key,int time_sign_numerator,int resolution) throws FileNotFoundException{
		
		File output_chord_file = new File(output_chord_file_path);
		PrintWriter writer = null;
		if (!output_chord_file.exists()){
			try {
				output_chord_file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		writer = new PrintWriter(output_chord_file);
		
		
		if (firstKey()==0){
			long higher_key = higherKey(firstKey());
			PitchClassSet pcs = firstEntry().getValue().get_chord();
			writer.println(0+" "+higher_key+" "+pcs.get_chord_symbol()+" 0-0");
		} else {
			writer.println("0 "+firstKey()+" N");
			writer.println(firstKey()+" "+higherKey(firstKey())+" "+firstEntry().getValue().get_chord().get_chord_symbol()+" "+(firstKey()/(resolution*time_sign_numerator)+1)+"-"+((firstKey()%(resolution*time_sign_numerator))/resolution));
		}
		
		for (long key : keySet()){
			if (key != firstKey() && key != lastKey()){
				writer.println(key+" "+higherKey(key)+" "+get(key).get_chord().get_chord_symbol()+" "+(key/(resolution*time_sign_numerator))+"-"+((key%(resolution*time_sign_numerator)+1)/resolution));
			}
			if (key == lastKey()){
				writer.println(key+" "+end_of_last_chord_key+" "+get(key).get_chord().get_chord_symbol()+" "+(key/(resolution*time_sign_numerator))+"-"+((key%(resolution*time_sign_numerator)+1)/resolution));
			}
		}
		writer.close();		
	}
	
	public void output_key_analysis_file(String output_key_file_path,long end_of_last_chord_key,int time_sign_numerator,int resolution){
		File output_key_file = new File(output_key_file_path);
		PrintWriter writer = null;
		// key stream defragmentation
		TreeMap<Long,Scale> defrag_key_stream = get_key_stream();
//		for (long k : keySet()) key_stream.put(k, get(k).get_key());
//		TreeMap<Long,Scale> defrag_key_stream = new TreeMap<Long,Scale>();
//		defrag_key_stream.put(key_stream.firstKey(), key_stream.firstEntry().getValue());
//		for (long key : key_stream.keySet()){
//			if (key != key_stream.firstKey() && !key_stream.get(key).equals(defrag_key_stream.get(defrag_key_stream.lowerKey(key)))){
//				//System.out.println("inegal ! "+key_stream.get(key)+" -- "+defrag_key_stream.get(defrag_key_stream.lowerKey(key)));
//				defrag_key_stream.put(key,key_stream.get(key));
//			}
//		}
//		System.out.println("defrag key stream : "+defrag_key_stream);
		writer = null;
		try {
			writer = new PrintWriter(output_key_file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (defrag_key_stream.firstKey()==0){
			//writer.println(0+" "+defrag_key_stream.higherKey(defrag_key_stream.firstKey())+" "+defrag_key_stream.get(0));
			writer.println(0+" "+defrag_key_stream.higherKey(defrag_key_stream.firstKey())+" "+defrag_key_stream.firstEntry().getValue());
		} else {
			writer.println("0 "+defrag_key_stream.firstKey()+" N");
			if (defrag_key_stream.keySet().size()>1){
				writer.println(defrag_key_stream.firstKey()+" "+defrag_key_stream.higherKey(defrag_key_stream.firstKey())+" "+defrag_key_stream.firstEntry().getValue());				
			}
		}
		
		for (long key : defrag_key_stream.keySet()){
			if (key != defrag_key_stream.firstKey() && key != defrag_key_stream.lastKey()){
				writer.println(key+" "+defrag_key_stream.higherKey(key)+" "+defrag_key_stream.get(key));
			}
			if (key == defrag_key_stream.lastKey()){
				writer.println(key+" "+end_of_last_chord_key+" "+defrag_key_stream.get(key));
			}
		}
		writer.close();

	}

}