package Utils;

import java.util.Collection;

import Model.Music.STIntervallicStructure;
import Model.Music.Tonnetze.PlanarUnfoldedTonnetz;

public abstract class Wiki {

	public static String latex(Collection<Integer> col){
		return "$"+col+"$";
	}
	
	public static String latex(OrientedRing ring){
		return "$"+ring.get_list()+"$";
	}
	
	public static String latex(PlanarUnfoldedTonnetz t){
		return "";
	}
	
	public static String latex_tonnetz(STIntervallicStructure is){
		return "$T_{"+is.get_list()+"}$";
	}
	
	public static String latex_tonnetz(Collection<Integer> list){
		return "$T_{"+list+"}$";
	}
	
}
