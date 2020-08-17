package Utils;

import java.util.ArrayList;

public class Binomial {

	public static int[][] _matrix = null;
	
//	public static int binom(int n, int p) {
//
//		assert (n>=0 && p>=0 && p<=n && p<12 && n<12) : "Binomial: binom: Error "+n+" "+p; 
//		if (_matrix==null){
//		    int i, j;
//			_matrix =  new int[12][12];
//			for(i=0;i<12;i++) _matrix[i][0] = 1;
//			for(i=1;i<12;i++) _matrix[0][i] = 0;
//			for(j=1;j<12;j++) for(i=1;i<12;i++) 
//				_matrix[i][j] = _matrix[i-1][j-1] + _matrix[i-1][j];
////			System.out.println(Table.toString(_matrix));
//		}
//		return _matrix[n][p];
//	}

	// p parmis n
	public static int binom(int n, int p) {

		assert (n>=0 && p>=0 && p<=n && p<=12 && n<=12) : "Binomial: binom: Error "+n+" "+p; 
		if (_matrix==null){
		    int i, j;
			_matrix =  new int[13][13];
			for(i=0;i<13;i++) _matrix[i][0] = 1;
			for(i=1;i<13;i++) _matrix[0][i] = 0;
			for(j=1;j<13;j++) for(i=1;i<13;i++) 
				_matrix[i][j] = _matrix[i-1][j-1] + _matrix[i-1][j];
//			System.out.println(Table.toString(_matrix));
		}
		return _matrix[n][p];
	}
	
	public static ArrayList<ArrayList<Integer>> generate_all_n_parmis_p(int n, int p){
		
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		if (n==0 || p==0) return list;
		
		int[] t = new int[n];
		int i=0;
		recur(list,i,n,p,t,0);
		//System.out.println("n parmis p resultats :\n"+list);		
		return list;
		
	}
	
	public static void recur(ArrayList<ArrayList<Integer>> list, int i, int n, int p, int[] t, int d){
		
		for (int j=d;j<p;j++){
			
			t[i]=j;
			if (i<n-1){
				recur(list,i+1,n,p,t,t[i]+1);
			} else {
				ArrayList<Integer> new_list = new ArrayList<Integer>();
				for(int k=0;k<t.length;k++) new_list.add(t[k]);
				list.add(new_list);
			}
		}
	}

}
