import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;

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

	
	
	private static List<String> listOccurences(List<String> textLines, String pattern) {
		
		int[] carryOver = makeCarryOver(pattern);
		List<String> acceptedLines = new ArrayList<>();

		int i = 1;
		for (String t : textLines){
			if (KmpMatching(t, pattern, carryOver)){
				acceptedLines.add(i + "\t" + t);
			}
			i++;
		}
		return acceptedLines;
	}

	public static void main(String[] args) {

		File textFile = new File("41011-0.txt");

		String pattern = "Chicago";

		List<String> textLines = new ArrayList<>();

	
		try (Scanner myReader = new Scanner(textFile)) {
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				textLines.add(line);
			}
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			
		List<String> acceptedLines = listOccurences(textLines, pattern);

		for (String line : acceptedLines) {
			System.out.println(line);
		}
		return  ;
	}

}
