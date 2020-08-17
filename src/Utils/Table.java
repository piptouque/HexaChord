package Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public abstract class Table {

	public static String toString(Object[] t){
		if (t.length == 0) return "[]";
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}
	
	public static String toString(int[] t) {
		//if (t==null) return "null";
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}
	
	public static String toString(float[] t) {
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}

	public static String toString(double[] t) {
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}
	
	public static String toString(byte[] t) {
		//if (t==null) return "null";
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}

	public static String toString(String[] t) {
		String s = "["+t[0];
		for (int i = 1; i < t.length; i++){
			s = s + "," + t[i];
		}
		s = s+"]";
		return s;
	}

	public static String toString(float[][] m){
		String s = toString(m[0]);
		for (int i = 1; i < m.length; i++){
			s = s + "\n" + toString(m[i]);
		}
		s = s+"\n";
		return s;
	}
	
	public static String toString(int[][] m){
		String s = toString(m[0]);
		for (int i = 1; i < m.length; i++){
			s = s + "\n" + toString(m[i]);
		}
		s = s+"\n";
		return s;
	}
	
	public static String toString(double[][] m){
		String s = toString(m[0]);
		for (int i = 1; i < m.length; i++){
			s = s + "\n" + toString(m[i]);
		}
		s = s+"\n";
		return s;
	}

//	public static String toString(ArrayList<int[]> list){
//		String s = "";
//		for (int[] i : list){
//			s = s+toString(i)+"|";
//		}
//		return s;
//	}
	
	public static String toString(Collection<int[]> list){
		String s = "";
		for (int[] i : list){
			s = s+toString(i)+"|";
		}
		return s;
	}

	public static String toString(ArrayList<float[]> list){
		String s = "";
		for (float[] i : list){
			s = s+toString(i)+"|";
		}
		return s;
	}
	
	public static String toString2(ArrayList<ArrayList<int[]>> list){
		String s = "";
		for (ArrayList<int[]> i : list){
		//for (int i=0;i<40;i++){
			if (i == null){
			//if (list.get(i) == null){
				s = s+"null::";
			} else {
				//s = s+toString(list.get(i))+"\n";
				s = s+toString(i)+"\n";
			}
		}
		return s;
	}
	
	public static String toString(TreeMap<Long,ArrayList<int[]>> tree){
		String s = "";
		for (Map.Entry<Long, ArrayList<int[]>> e : tree.entrySet()){
		    //System.out.println(e.getKey() + " : " + e.getValue());
		    s = s+","+e.getKey()+":"+toString(e.getValue());
		}
		return s;
	}

}
