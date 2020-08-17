package Main;

import java.util.ArrayList;
import java.util.HashSet;

import Model.Music.PitchClassSet;
import Model.Music.STIntervallicStructure;
import Model.Music.Scale;
import Model.Music.Tonnetze.AffnChordComplex;
import Model.Music.Tonnetze.ChordComplex;
import Model.Music.Tonnetze.SComplex;
import Model.Music.Tonnetze.TChordComplex;
import Model.Music.Tonnetze.TComplex;
import Model.Music.Tonnetze.TIChordComplex;
import Utils.IntegerSet;
import Utils.Table;

public class Enumeration {
	
	public static ArrayList<ArrayList<TComplex>> T_enumeration(int N){
		
		ArrayList<ArrayList<TComplex>> list_list = new ArrayList<ArrayList<TComplex>>();
		
		ArrayList<TComplex> n_chord_complex_list;
		ArrayList<STIntervallicStructure> n_IS_list;
		
		for (int n = 1;n<=N;n++){
			n_chord_complex_list = new ArrayList<TComplex>();
			n_IS_list = STIntervallicStructure.enum_SI(n, N);
			
			for (STIntervallicStructure is : n_IS_list){
				n_chord_complex_list.add(new TChordComplex(is));
			}
			
			//System.out.println(n_chord_complex_list.size()+" complexes d'accords de taille "+n);
			list_list.add(n_chord_complex_list);
		}
		
		return list_list;
	}

	public static ArrayList<ArrayList<TComplex>> TI_enumeration(int N){
		
		ArrayList<ArrayList<TComplex>> list_list = new ArrayList<ArrayList<TComplex>>();
		
		ArrayList<TComplex> n_chord_complex_list;
		ArrayList<STIntervallicStructure> n_IS_list;
		
		for (int n = 1;n<=N;n++){
		//for (int n = 3;n<=3;n++){
			n_chord_complex_list = new ArrayList<TComplex>();
			n_IS_list = STIntervallicStructure.enum_SI_up_to_flip(n, N);
			
//			for (int i=0;i<=11;i++){
			for (STIntervallicStructure is : n_IS_list){
				TIChordComplex complex = new TIChordComplex(is);
//				if (i==0 || i==8){
//					TIChordComplex complex = new TIChordComplex(n_IS_list.get(i));
//					
//				}
				
				
				
//				System.out.println(complex.get_latex_name()+" "+complex.get_betti_numbers_list()+" "+complex.get_betty_numbers()+" "+complex.get_euler_characteristic());
				n_chord_complex_list.add(new TIChordComplex(is));
			}
			
			//System.out.println(n_chord_complex_list.size()+" complexes d'accords de taille "+n);
			list_list.add(n_chord_complex_list);
			
			
		}
		
		return list_list;
	}

	public static ArrayList<ArrayList<AffnChordComplex>> Aff_Enumeration(int N, int m){
		ArrayList<ArrayList<AffnChordComplex>> list_list = new ArrayList<ArrayList<AffnChordComplex>>();
		ArrayList<ArrayList<STIntervallicStructure>> is_set_list = STIntervallicStructure.enum_Aff_SI(N, m);
		ArrayList<AffnChordComplex> n_chord_complex_list;
		
		for (int n = 1;n<=N;n++){
			n_chord_complex_list = new ArrayList<AffnChordComplex>();
			for (ArrayList<STIntervallicStructure> is_list : is_set_list){
				if (is_list.get(0).size()==n){
					HashSet<PitchClassSet> chord_set = new HashSet<PitchClassSet>();
					for (STIntervallicStructure is : is_list){
						chord_set.addAll(is.get_transpositions_and_inversions());
					}
					AffnChordComplex complex = new AffnChordComplex(chord_set,is_list.get(0).get_prime_order_PCSet(0));
					n_chord_complex_list.add(complex);
				}
			}
			list_list.add(n_chord_complex_list);
		}
		return list_list;
	}
	
	public static ArrayList<ArrayList<TComplex>> S_enumeration(int N){
		
		ArrayList<ArrayList<TComplex>> list_list = new ArrayList<ArrayList<TComplex>>();
		
		ArrayList<TComplex> n_chord_complex_list;
		ArrayList<STIntervallicStructure> n_IS_list;
		n_IS_list = STIntervallicStructure.enum_SI_up_to_permutation(N);

		for (int n = 1;n<=N;n++){
		//for (int n = 4;n<=4;n++){
			System.out.println("n = "+n);
			n_chord_complex_list = new ArrayList<TComplex>();
			for (STIntervallicStructure is : n_IS_list){
				if (is.size()==n) n_chord_complex_list.add(new SComplex(is));
			}
			list_list.add(n_chord_complex_list);
		}
				
		return list_list;
	}

//	public static void latex_print(ArrayList<ArrayList<TComplex>> list_list){
//
//		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{4cm}|>{\\centering}m{1.5cm}|>{\\centering}m{4cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|}\n");
//		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_n$\n   & p-v\n   & $\\chi$ \\tabularnewline\n   \\hline");
//		System.out.println("\\multirow{1}*{-} & [] & 0 & 0 & 0 & 0\\tabularnewline\n\\hline\n\n");
//
//		for (ArrayList<TComplex> complex_list : list_list){
//			int complex_count = complex_list.size();
//			if (complex_count >= 1){
//
//				System.out.println("\\multirow{"+complex_count+"}*{"+(complex_list.get(0).get_dimension())+"}"		// Taille des accords (dimension +1)
//				//+" & "+complex_list.get(0).get_STIntervallicStructure()									// Structure intervallique
//				+" & "+complex_list.get(0).get_latex_name()									// Structure intervallique
//				+" & "+complex_list.get(0).get_higher_dim_pitch_class_sets().size()						// Nombre d'accords de taille N
//				+" & "+complex_list.get(0).get_betti_numbers_list()
//				//+" & "+complex_list.get(0).get_connected_componnent_count()
//				//+" & "+complex_list.get(0).get_circular_holes_count()
//				//+" & "+complex_list.get(0).get_holes_count()
//				+" & "+((complex_list.get(0).is_pseudo_variety()) ? "x" : "")
//				//+" & "+((complex_list.get(0).has_boundary()) ? "x" : "")
//				+" & "+complex_list.get(0).get_euler_characteristic()
//				+"\\tabularnewline");									
//				
//				for (int i=1;i<complex_count;i++){
//					System.out.println("\\cline{2-6}");
//					System.out.println(
//							 //"& "+complex_list.get(i).get_STIntervallicStructure()
//							  "& "+complex_list.get(i).get_latex_name()									// Structure intervallique
//							+" & "+complex_list.get(i).get_higher_dim_pitch_class_sets().size()
//							+" & "+complex_list.get(i).get_betti_numbers_list()
//							//+" & "+complex_list.get(i).get_connected_componnent_count()
//							//+" & "+complex_list.get(i).get_circular_holes_count()
//							//+" & "+complex_list.get(i).get_holes_count()
//							//+" & "+complex_list.get(i).get_betti_number(2)
//							+" & "+((complex_list.get(i).is_pseudo_variety()) ? "x" : "")
//							//+" & "+((complex_list.get(i).has_boundary()) ? "x" : "")
//							+" & "+complex_list.get(i).get_euler_characteristic()
//							+"\\tabularnewline");
//					//System.out.println("SC : "+complex_list.get(i).is_strongly_connected());
//				}
//				System.out.println("\\hline");
//			}
//		}
//		System.out.println("\\hline\n\\end{supertabular}");
//		int count = 0;
//		for (ArrayList<TComplex> complex_list : list_list){
//			count = count+complex_list.size();
//		}
//		System.out.println("\n"+count+" complexes en tout");
//
//	}

	// SANS LA CARACTERISTIQUE D EULER
	public static void latex_print(ArrayList<ArrayList<TComplex>> list_list){

		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{4.9cm}|>{\\centering}m{1.5cm}|>{\\centering}m{4cm}|>{\\centering}m{0.6cm}|>{\\centering}m{0.6cm}|}\n");
		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_n$\n   & p-v\n   & $\\chi$ \\tabularnewline\n   \\hline");
		System.out.println("\\multirow{1}*{-} & $\\K_{\\emptyset}$ & 0 & 0 & & 0\\tabularnewline\n\\hline\n\n");

		for (ArrayList<TComplex> complex_list : list_list){
			int complex_count = complex_list.size();
			if (complex_count >= 1){

				System.out.println("\\multirow{"+complex_count+"}*{"+(complex_list.get(0).get_dimension())+"}"		// Taille des accords (dimension +1)
				//+" & "+complex_list.get(0).get_STIntervallicStructure()									// Structure intervallique
				+" & "+complex_list.get(0).get_latex_name()									// Structure intervallique
				+" & "+complex_list.get(0).get_higher_dim_pitch_class_sets().size()						// Nombre d'accords de taille N
				+" & "+complex_list.get(0).get_betti_numbers_list()
				//+" & "+complex_list.get(0).get_connected_componnent_count()
				//+" & "+complex_list.get(0).get_circular_holes_count()
				//+" & "+complex_list.get(0).get_holes_count()
				+" & "+((complex_list.get(0).is_pseudo_variety()) ? "x" : "")
				//+" & "+((complex_list.get(0).has_boundary()) ? "x" : "")
				//+" & "+complex_list.get(0).get_euler_characteristic()
				+"\\tabularnewline");									
				
				for (int i=1;i<complex_count;i++){
					System.out.println("\\cline{2-5}");
					System.out.println(
							 //"& "+complex_list.get(i).get_STIntervallicStructure()
							  "& "+complex_list.get(i).get_latex_name()									// Structure intervallique
							+" & "+complex_list.get(i).get_higher_dim_pitch_class_sets().size()
							+" & "+complex_list.get(i).get_betti_numbers_list()
							//+" & "+complex_list.get(i).get_connected_componnent_count()
							//+" & "+complex_list.get(i).get_circular_holes_count()
							//+" & "+complex_list.get(i).get_holes_count()
							//+" & "+complex_list.get(i).get_betti_number(2)
							+" & "+((complex_list.get(i).is_pseudo_variety()) ? "x" : "")
							//+" & "+((complex_list.get(i).has_boundary()) ? "x" : "")
							//+" & "+complex_list.get(i).get_euler_characteristic()
							+"\\tabularnewline");
					//System.out.println("SC : "+complex_list.get(i).is_strongly_connected());
				}
				System.out.println("\\hline");
			}
		}
		System.out.println("\n\\end{supertabular}");
		int count = 0;
		for (ArrayList<TComplex> complex_list : list_list){
			count = count+complex_list.size();
		}
		System.out.println("\n"+count+" complexes en tout");

	}

	
//	public static void latex_print_affn(ArrayList<ArrayList<AffnChordComplex>> list_list){
//
////		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{2.5cm}|>{\\centering}m{1.5cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|}\n");
////		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_0$\n   & $b_1$\n   & $b_2$\n   & s-var\n   & $\\chi$ \\tabularnewline\n   \\hline");
////		System.out.println("\\multirow{1}*{-} & [] & 0 & 0 & 0 & 0 & 0 & 0\\tabularnewline\n\\hline\n\n");
//		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{4cm}|>{\\centering}m{1.5cm}|>{\\centering}m{4cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|}\n");
//		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_n$\n   & p-v\n   & $\\chi$ \\tabularnewline\n   \\hline");
//		System.out.println("\\multirow{1}*{-} & [] & 0 & 0 & 0 & 0\\tabularnewline\n\\hline\n\n");
//
//		for (ArrayList<AffnChordComplex> complex_list : list_list){
//			int complex_count = complex_list.size();
//			if (complex_count >= 1){
//
//				System.out.println("\\multirow{"+complex_count+"}*{"+(complex_list.get(0).get_dimension())+"}"		// Taille des accords (dimension +1)
//				//+" & "+complex_list.get(0).get_STIntervallicStructure()									// Structure intervallique
//				+" & "+complex_list.get(0).get_latex_name()									// Structure intervallique
//				+" & "+complex_list.get(0).get_higher_dim_pitch_class_sets().size()						// Nombre d'accords de taille N
//				+" & "+complex_list.get(0).get_betti_numbers_list()
//				//+" & "+complex_list.get(0).get_connected_componnent_count()
//				//+" & "+complex_list.get(0).get_circular_holes_count()
//				//+" & "+complex_list.get(0).get_holes_count()
//				+" & "+((complex_list.get(0).is_pseudo_variety()) ? "x" : "")
//				//+" & "+((complex_list.get(0).has_boundary()) ? "x" : "")
//				+" & "+complex_list.get(0).get_euler_characteristic()
//				+"\\tabularnewline");									
//				
//				for (int i=1;i<complex_count;i++){
//					System.out.println("\\cline{2-6}");
//					System.out.println(
//							 //"& "+complex_list.get(i).get_STIntervallicStructure()
//							  "& "+complex_list.get(i).get_latex_name()									// Structure intervallique
//							+" & "+complex_list.get(i).get_higher_dim_pitch_class_sets().size()
//							+" & "+complex_list.get(i).get_betti_numbers_list()
//							//+" & "+complex_list.get(i).get_connected_componnent_count()
//							//+" & "+complex_list.get(i).get_circular_holes_count()
//							//+" & "+complex_list.get(i).get_holes_count()
//							+" & "+((complex_list.get(i).is_pseudo_variety()) ? "x" : "")
//							//+" & "+((complex_list.get(i).has_boundary()) ? "x" : "")
//							+" & "+complex_list.get(i).get_euler_characteristic()
//							+"\\tabularnewline");
//					//System.out.println("Surface : "+complex_list.get(i).is_a_n_surface()+"  Bord : "+complex_list.get(i).has_boundary());
//				}
//				System.out.println("\\hline");
//			}
//		}
//		System.out.println("\\hline\n\\end{supertabular}");
//		int count = 0;
//		for (ArrayList<AffnChordComplex> complex_list : list_list){
//			count = count+complex_list.size();
//		}
//		System.out.println("\n"+count+" complexes en tout");
//
//	}

	// SANS LA CARACTERISTIQUE D EULER
	
	public static void latex_print_affn(ArrayList<ArrayList<AffnChordComplex>> list_list){

//		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{2.5cm}|>{\\centering}m{1.5cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|}\n");
//		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_0$\n   & $b_1$\n   & $b_2$\n   & s-var\n   & $\\chi$ \\tabularnewline\n   \\hline");
//		System.out.println("\\multirow{1}*{-} & [] & 0 & 0 & 0 & 0 & 0 & 0\\tabularnewline\n\\hline\n\n");
		System.out.println("\\begin{supertabular}{|c|>{\\centering}m{4cm}|>{\\centering}m{1.5cm}|>{\\centering}m{4cm}|>{\\centering}m{1cm}|>{\\centering}m{1cm}|}\n");
		System.out.println("\\hline\n  $d$\n   & complexe\n   & taille\n   & $b_n$\n   & p-v\n   & $\\chi$ \\tabularnewline\n   \\hline");
		System.out.println("\\multirow{1}*{-} & $\\K_{\\emptyset}$ & 0 & 0 &  & 0\\tabularnewline\n\\hline\n\n");

		for (ArrayList<AffnChordComplex> complex_list : list_list){
			int complex_count = complex_list.size();
			if (complex_count >= 1){

				System.out.println("\\multirow{"+complex_count+"}*{"+(complex_list.get(0).get_dimension())+"}"		// Taille des accords (dimension +1)
				//+" & "+complex_list.get(0).get_STIntervallicStructure()									// Structure intervallique
				+" & "+complex_list.get(0).get_latex_name()									// Structure intervallique
				+" & "+complex_list.get(0).get_higher_dim_pitch_class_sets().size()						// Nombre d'accords de taille N
				+" & "+complex_list.get(0).get_betti_numbers_list()
				//+" & "+complex_list.get(0).get_connected_componnent_count()
				//+" & "+complex_list.get(0).get_circular_holes_count()
				//+" & "+complex_list.get(0).get_holes_count()
				+" & "+((complex_list.get(0).is_pseudo_variety()) ? "x" : "")
				//+" & "+((complex_list.get(0).has_boundary()) ? "x" : "")
				//+" & "+complex_list.get(0).get_euler_characteristic()
				+"\\tabularnewline");									
				
				for (int i=1;i<complex_count;i++){
					System.out.println("\\cline{2-5}");
					System.out.println(
							 //"& "+complex_list.get(i).get_STIntervallicStructure()
							  "& "+complex_list.get(i).get_latex_name()									// Structure intervallique
							+" & "+complex_list.get(i).get_higher_dim_pitch_class_sets().size()
							+" & "+complex_list.get(i).get_betti_numbers_list()
							//+" & "+complex_list.get(i).get_connected_componnent_count()
							//+" & "+complex_list.get(i).get_circular_holes_count()
							//+" & "+complex_list.get(i).get_holes_count()
							+" & "+((complex_list.get(i).is_pseudo_variety()) ? "x" : "")
							//+" & "+((complex_list.get(i).has_boundary()) ? "x" : "")
							//+" & "+complex_list.get(i).get_euler_characteristic()
							+"\\tabularnewline");
					//System.out.println("Surface : "+complex_list.get(i).is_a_n_surface()+"  Bord : "+complex_list.get(i).has_boundary());
				}
				System.out.println("\\hline");
			}
		}
		System.out.println("\n\\end{supertabular}");
		int count = 0;
		for (ArrayList<AffnChordComplex> complex_list : list_list){
			count = count+complex_list.size();
		}
		System.out.println("\n"+count+" complexes en tout");

	}

	
	public static IntegerSet get_primes(int N){
		IntegerSet primes = new IntegerSet();
		for (int i=2;i<N/2;i++){
			if (N%i!=0) {
				primes.add(i);
				primes.add(N-i);
			}
		}
		return primes;
	}
	
	
	public static void main(String[] args) {
		
		//HashSet<TComplex> c_set = new HashSet<TComplex>(TI_enumeration(12).get(2));
		//HashSet<TComplex> c_set = new HashSet<TComplex>(S_enumeration(12).get(0));
		
		//TComplex.make_pvl_graph(c_set);
		
		latex_print(TI_enumeration(12));
		//latex_print(T_enumeration(7));
		//latex_print(S_enumeration(7));
		//latex_print_affn(Aff_Enumeration(12, 5));
		
		//STIntervallicStructure.enum_SI_up_to_permutation(12);
		//latex_print(S_enumeration(7));
//		HashSet<STIntervallicStructure> set = new HashSet<STIntervallicStructure>();
//		ArrayList<Integer> list1 = new ArrayList<Integer>(); list1.add(2); list1.add(5); list1.add(3);
//		ArrayList<Integer> list2 = new ArrayList<Integer>(); list2.add(5); list2.add(3); list2.add(2);
//		STIntervallicStructure is1 = new STIntervallicStructure(list1);
//		STIntervallicStructure is2 = new STIntervallicStructure(list2);
//		set.add(is1);//set.add(is2);
//		System.out.println("set : "+is2.is_contained(set));
		
	}
	


}
