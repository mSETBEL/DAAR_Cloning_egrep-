import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BoyerMoore {
	
	static void badCharHeuristic(char[] str, int size, int badchar[]) {
		int i;
		//initialization
		for (i = 0; i < badchar.length; i++)
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

	static int search(char []pat, char []txt){
		int matches = 0;
		
		int m = pat.length;
		int n = txt.length;

		int []badchar = new int[Character.MAX_VALUE + 1];
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
				//System.out.println("Pattern found at index: " + s);
				matches++;
				s += (s + m < n) ? m - badchar[txt[s + m]] : 1;
			}
			else
				s += Math.max(shift[j + 1], j - badchar[txt[s + j]]);
		}
		return matches;
	}

	static String highlightMatches(String line, String pattern) {
		return line.replaceAll(
			"(?i)" + java.util.regex.Pattern.quote(pattern),
			"\u001B[1m$0\u001B[0m"
		);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java BoyerMoore <pattern> <file>");
			System.exit(1);
		}

		char[] pat = args[0].toCharArray();
		Path file = Path.of(args[1]);

		List<String> lines = Files.readAllLines(
			file, StandardCharsets.UTF_8
		);

		long start = System.nanoTime();
		int totalMatches = 0;
		List<Integer> matchingLines = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			char[] chars = line.toCharArray();

			int count = search(pat, chars);

			if (count > 0) {
				matchingLines.add(i + 1);
				totalMatches += count;
			}
		}

		long elapsed = System.nanoTime() - start;

		System.err.println("BENCHMARK_NS=" + elapsed);
		System.err.println("MATCH_COUNT=" + totalMatches);
		for (int lineNumber : matchingLines) {
			String line = lines.get(lineNumber - 1);
			System.out.println(lineNumber + "\t" + highlightMatches(line, args[0]));
		}
	}



}
