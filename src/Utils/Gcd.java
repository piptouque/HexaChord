package Utils;

import java.util.ArrayList;


// found at http://www.liafa.jussieu.fr/~carton/Enseignement/CalculFormel/Style/ModArith/

//Time-stamp: <Gcd.java  17 Nov 2000 10:31:40>

/**
* Computation of integer GCD 
* @author O. Carton
* @version 1.0
*/
public class Gcd {
 /**
  * Computes the canonical class of n modulo mod
  * @param n the integer to reduce modulo mod
  * @param mod the modulo
  * @return the integer k such that k = n [mod] et 0 <= k < mod
  */
 public static int reduce(int n, int mod)
 {
	int m = n % mod;	// -mod < m < mod

	if (m >= 0 )
	    return m;
	else 
	    return m + mod;
 }
 /**
  * Computes the GCD of the two integers.
  * @param m the first integer
  * @param n the second integer 
  * @return the GCD of m and n
  */
 public static int gcd(int m, int n) 
 {
	int r;

	// Exchange m and n if m < n
	if (m < n) {
	    r = n;  n = m; m = r;
	}
	// It can be assumed that m >= n
	while (n > 0) {
	    r = m % n;
	    m = n;
	    n = r;
	}
	return m;
 }
 /*
  * Return the GCD of a List of Integer
  * 
  */
 public static int gcd(ArrayList<Integer> list) {
	 int gcd = list.get(0);
	 for(Integer n : list) gcd = gcd(gcd,n);
	 return gcd;
 }
 /**
  * Computes the GCD and the coefficients of the Bezout equality.
  * @param m the first integer
  * @param n the second integer 
  * @return an array g of 3 integers.  g[0] is the GCD of m and n.
  *  g[1] and g[2] are two integers such that g[0] = m g[1] + n g[2].
  */
 public static int[] extgcd(int m, int n) 
 {
	// Both arrays ma and na are arrays of 3 integers such that
	// ma[0] = m ma[1] + n ma[2] and na[0] = m na[1] + n na[2]
	int[] ma = new int[]  {m, 1, 0};
	int[] na = new int[]  {n, 0, 1};
	int[] ta;		// Temporary variable 
	int i;			// Loop index
	int q;			// Quotient
	int r;			// Rest

	// Exchange ma and na if m < n
	if (m < n) {
	    ta = na;  na = ma; ma = ta;
	}
	
	// It can be assumed that m >= n
	while (na[0] > 0) {
	    q = ma[0] / na[0];	// Quotient
	    for (i = 0; i < 3; i++) {
		r = ma[i] - q * na[i];
		ma[i] = na[i];
		na[i] = r;
	    }
	}
	return ma;
 }
 /**
  * Computes the modular inverse
  * @param n the integer to inverse
  * @param mod the modula
  * @return the integer 0 <= m < mod such that nm = 1 [mod] or
  *         -1 if if n and mod are not coprime
  */
 public static int modInverse(int n, int mod)
 {
	int[] g = extgcd(mod, n);
	if (g[0] != 1)
	    return -1;		// n and mod not coprime
	else 
	    return reduce(g[2], mod);
 }
}