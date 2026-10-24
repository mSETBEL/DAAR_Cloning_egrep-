public class BoyerMoore {
	
	static void badCharHeuristic(char[] str, int size, int badchar[]) {
		int i;
		//initialization
		for (i = 0; i < 256; i++)
			badchar[i] = -1;
		//fill the actual value of last occurrence of a character
		for (i = 0; i < size; i++)
			badchar[(int) str[i]] = i;
	}

	static void preprocess_strong_suffix(int []shift, int []bpos, char []pat, int m){
    	//m taille du pattern
		int i = m, j = m + 1;
		bpos[i] = j;

		while(i > 0){
			while(j <= m && pat[i - 1] != pat[j - 1]){
				if(shift[j] == 0)
					shift[j] = j - i;
				j = bpos[j];
			}
			i--; j--;
			bpos[i] = j;
		}
	}
	static void preprocess_case2(int []shift, int []bpos, char []pat, int m){
		int j = bpos[0];
		for(int i = 0; i <= m; i++){
			if(shift[i] == 0)
				shift[i] = j;
			if(i == j)
				j = bpos[j];
		}
	}

	static void search(char []pat, char []txt){
		int m = pat.length;
		int n = txt.length;

		int []badchar = new int[256];
		badCharHeuristic(pat, m, badchar);

		int []shift = new int[m + 1];
		int []bpos = new int[m + 1];

		preprocess_strong_suffix(shift, bpos, pat, m);
		preprocess_case2(shift, bpos, pat, m);

		int s = 0;
		while(s <= (n - m)){
			int j = m - 1;

			while(j >= 0 && pat[j] == txt[s + j])
				j--;
			if(j < 0){
				System.out.println("Pattern found at index: " + s);
				s += (s + m < n) ? m - badchar[txt[s + m]] : 1;
			}
			else
				s += Math.max(shift[j + 1], j - badchar[txt[s + j]]);
		}
	}

	static public void main(String []args){
		char []text = "ABAAAABAACD".toCharArray();
    	char []pat = "ABA".toCharArray();
		search(pat, text);
	}
}
