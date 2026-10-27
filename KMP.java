import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class KMP {
	


	public static int[] makeCarryOver(String pattern){
		int n = pattern.length();
		int[] carryOver = new int[n];

		carryOver[0] = -1;
		for (int i=1; i<n; i++){
			carryOver[i] = biggestPrefixSuffix(pattern.substring(0, i)).length();
		}

		for (int i=1; i<n; i++){
			if (pattern.charAt(i) == pattern.charAt(carryOver[i])){
				carryOver[i] = carryOver[carryOver[i]];
			}
		}


		return carryOver;


	}

	public static String biggestPrefixSuffix(String pattern){

		int n = pattern.length();
		String max = "";

		for (int i=1; i<n; i++){
			if (pattern.substring(0, i).equals(pattern.substring(n-i, n))){
				max = pattern.substring(0, i);
			}
		}
		return max;
	}

	public static boolean KmpMatching(String text, String pattern, int[] carryOver){

		
		int n = text.length();
		int k = pattern.length();
		int indexText = 0;
		int indexPattern = 0;
		if (indexPattern >= k){
			return true;
		}


		while (indexText<n){
			
			while (indexPattern < k && indexText < n && text.charAt(indexText)==(pattern.charAt(indexPattern))){
				indexPattern++;
				indexText++;
			}
			if (indexPattern >= k){
				return true;
			} 
			if (indexText >= n){
				return false;
			}
			indexText -= carryOver[indexPattern];
			indexPattern = 0;
		}


		return false;
	}

	
	
	private static List<String> listOccurences(List<String> textLines, String pattern, int[] carryOver) {
		
		List<String> acceptedLines = new ArrayList<>();

		int i = 1;
		for (String t : textLines){
			if (KmpMatching(t, pattern, carryOver)){
				acceptedLines.add(i + "\t" + highlightMatches(t, pattern));
			}
			i++;
		}
		return acceptedLines;
	}

	static String highlightMatches(String line, String pattern) {
		return line.replaceAll(
			"(?i)" + java.util.regex.Pattern.quote(pattern),
			"\u001B[1m$0\u001B[0m"
		);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java KMP <pattern> <file>");
			System.exit(1);
		}

		String pattern = args[0];
		Path textFile = Path.of(args[1]);

		List<String> textLines = Files.readAllLines(
			textFile, StandardCharsets.UTF_8
		);
		// preprocessing of the pattern, which is done once and reused for all lines
		long preprocessStart = System.nanoTime();

		int[] carryOver = makeCarryOver(pattern);
		
		long preprocessElapsed = System.nanoTime() - preprocessStart;
		System.err.println("PREPROCESS_NS=" + preprocessElapsed);

		long start = System.nanoTime();

		List<String> acceptedLines =
			listOccurences(textLines, pattern, carryOver);

		long elapsed = System.nanoTime() - start;

		// Send timing to stderr
		System.err.println("BENCHMARK_NS=" + elapsed);
		System.err.println("MATCH_COUNT=" + acceptedLines.size());
		System.out.println("MATCHES:");
		for (String line : acceptedLines) {
			System.out.println(line);
		}
	}

}
