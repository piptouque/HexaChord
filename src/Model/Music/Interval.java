package Model.Music;

import java.util.ArrayList;

public abstract class Interval {

	public static String get_name(int i){
		switch (i%12) {
		case 0:  return "P1";
		case 1:  return "m2";
		case 2:  return "M2";
		case 3:  return "m3";
		case 4:  return "M3";
		case 5:  return "P4";
		case 6:  return "TT";
		case 7:  return "P5";
		case 8:  return "m6";
		case 9:  return "M6";
		case 10: return "m7";
		case 11: return "M7";
		default: return "??";
		}
	}
	
	public static String get_name(int i, char language) {
			
		if (language == 'c') {
			switch (i%12) {
			case 0:  return "0";
			case 1:  return "2-";
			case 2:  return "2+";
			case 3:  return "3-";
			case 4:  return "3+";
			case 5:  return "4";
			case 6:  return "4+";
			case 7:  return "5";
			case 8:  return "6-";
			case 9:  return "6+";
			case 10: return "7-";
			case 11: return "7+";
			default: return "??";
			}
		}
		if (language == 'E') {
			switch (i%12) {
			case 0:  return "Perfect Unison";
			case 1:  return "Minor second";
			case 2:  return "Major second";
			case 3:  return "Minor third";
			case 4:  return "Major third";
			case 5:  return "Perfect fourth";
			case 6:  return "Augumented fourth";
			case 7:  return "Perfect fifth";
			case 8:  return "Minor sixth";
			case 9:  return "Major sixth";
			case 10: return "Minor seventh";
			case 11: return "Major seventh";
			default: return "??";
			}
		}
		if (language == 'F') {
			switch (i%12) {
			case 0:  return "Unison";
			case 1:  return "Seconde mineure";
			case 2:  return "Seconde majeure";
			case 3:  return "Tierce mineure";
			case 4:  return "Tierce majeure";
			case 5:  return "Quarte juste";
			case 6:  return "Quarte augument�e";
			case 7:  return "Quinte juste";
			case 8:  return "Sixte mineure";
			case 9:  return "Sixte majeure";
			case 10: return "Septi�me mineure";
			case 11: return "Septi�me majeure";
			default: return "??";
			}
		}
		
		else {
			return get_name(i);
		}
	}
	
	public static int MI(int n1, int n2, int N){
		return MI((n1-n2),N);
	}
	
	public static int MI(int i, int N){
		int a = Math.abs(i)%N;
		if (a<=N/2) return a;
		else return N-a;
	}
	
	// Ex : 5 -> 5 ; 7 -> -5
	public static int smaller_distance_interval(int i, int N){
		int interval = i%12;
		if (interval>=0){
			if (interval <= N/2){
				return interval;
			} else {
				return interval-N;
			}
		} else {
			if (interval > -N/2){
				return interval;
			} else {
				return interval+N;
			}
		}
	}
	
	public static int distance_in_fith_circle(int n1, int n2){
		ArrayList<Integer> fifth_list = Scale.get_fifth_list();
		int index1 = fifth_list.indexOf(n1);
		for (int i=0;i<fifth_list.size();i++){
			if (fifth_list.get((index1+i)%fifth_list.size())==n2 || fifth_list.get((index1-i+fifth_list.size())%fifth_list.size())==n2) return i; 
		}
		System.err.println("fifth distance not found");
		return -1;
	}

//	public static int MI(int i, int N){
//		//int M = (N/2)-(N%2);
//		int M = (N/2);
//		return -Math.abs(i-M)+M;
//	}
}
