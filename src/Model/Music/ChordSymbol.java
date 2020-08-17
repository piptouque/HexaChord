package Model.Music;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.SysexMessage;

public class ChordSymbol{
	
	private static enum Root {Cb,C,Cs,Db,D,Ds,Eb,E,Es,Fb,F,Fs,Gb,G,Gs,Ab,A,As,Bb,B,Bs,N;}
	private static enum Mode {min,maj,aug,dim,sus};

	private Root root;
	private Mode mode;
	
	// string format so far : root:mode
	public ChordSymbol(String str){
		String string = str;
		if (string.indexOf(":")!=-1){
			int index_dots = string.indexOf(":");
			root = Root.valueOf(string.substring(0, index_dots).replace("#", "s"));
			mode = Mode.valueOf(string.substring(index_dots+1, string.length()));			
		} else {
			if (string.indexOf("/")!=-1){
				string = string.substring(0, string.indexOf("/"));
			} 
			int size = string.length();
			if (string.substring(size-1, size).equals("m")) {
				root = Root.valueOf(string.substring(0, size-1).replace("#", "s"));
				mode = Mode.min;
			} else {
				if (!string.equals("N")){
					root = Root.valueOf(string.substring(0, size).replace("#", "s"));
					mode = Mode.maj;					
				}
			}
		}
	}
	
	private ChordSymbol(Root r,Mode m){
		root=r;
		mode=m;
	}
	
	public String toString(){
		if (Root.valueOf("N")==root) return "N";
		return root.toString()+":"+mode.toString();
	}
	
	public PitchClassSet get_pcs(){
		PitchClassSet pcs = new PitchClassSet();
		int root_pc;
		switch(root.toString().charAt(0)){
			case 'N':
				return pcs;
			case 'C':
				root_pc=0;
				break;
			case 'D':
				root_pc=2;
				break;
			case 'E':
				root_pc=4;
				break;
			case 'F':
				root_pc=5;
				break;
			case 'G':
				root_pc=7;
				break;
			case 'A':
				root_pc=9;
				break;
			case 'B':
				root_pc=11;
				break;
			default :
				root_pc=0;
				System.err.println("Root pitch class not found");
				System.exit(0);
				break;
		}
		if (root.toString().length()>1){
			if (root.toString().charAt(1)=='b') root_pc=(root_pc-1+12)%12;
			if (root.toString().charAt(1)=='s') root_pc=(root_pc+1)%12;
		}
		pcs.add(root_pc);
		if (mode.toString().equals("min")){
			pcs.add((root_pc+3)%12);
			pcs.add((root_pc+7)%12);
		}
		if (mode.toString().equals("maj")){
			pcs.add((root_pc+4)%12);
			pcs.add((root_pc+7)%12);
		}
		if (mode.toString().equals("dim")){
			pcs.add((root_pc+3)%12);
			pcs.add((root_pc+6)%12);
		}
		if (mode.toString().equals("aug")){
			pcs.add((root_pc+4)%12);
			pcs.add((root_pc+8)%12);
		}
		if (mode.toString().equals("sus")){
			pcs.add((root_pc+5)%12);
			pcs.add((root_pc+7)%12);
		}
		return pcs;
	}
	
	public static List<ChordSymbol> get_chord_space(){
		List<ChordSymbol> chord_space = new ArrayList<ChordSymbol>();
		for (Root r : Root.values()){
			for (Mode m : Mode.values()){
				chord_space.add(new ChordSymbol(r,m));
			}
		}
		return chord_space;
	}
	
	public boolean is_major(){
		//System.out.println("pouet "+mode.toString());
		return (mode.toString().equals("maj"));
	}

	public boolean is_minor(){
		//System.out.println("bom "+mode.toString());
		return (mode.toString().equals("min"));
	}
	
	public boolean equals(ChordSymbol other_chord_symbol){
		if (mode.toString().equals(other_chord_symbol.mode.toString()) && root.toString().equals(other_chord_symbol.root.toString()))
			return true;
		
		
		return false;
	}

}
