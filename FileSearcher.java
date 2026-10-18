import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSearcher {
    
    private static final String YELLOW = "\u001B[1;33m"; // Couleur pour highlight (jaune)
    private static final String RESET = "\u001B[0m"; // pour annuler le jaune et revenir à la couleur par défaut

    // Table de transitions : next[état][caractère ASCII] = état suivant, ou -1
    private final int[][] next;
    private final boolean[] accepting;
    private final int start;

    // Convertir le DFA en tableaux
    public FileSearcher(DFA dfa) {
        List<DFAstate> states = dfa.getStates();
        int n = states.size();

        // les id des states sont static on doit les mapper (0 n-1)
        Map<Integer, Integer> index = new HashMap<>(); //(id de state , index dans notre liste)
        for (int i = 0; i < n; i++) {
            index.put(states.get(i).getStateId(), i);
        }

        next = new int[n][128];
        accepting = new boolean[n];
        for (int i = 0; i < n; i++) {
            DFAstate s = states.get(i);
            accepting[i] = s.isAccepting();
            for (int c = 0; c < 128; c++) {
                int t = s.getTransition(c);
                next[i][c] = (t == -1) ? -1 : index.get(t);
            }
        }
        start = index.get(dfa.getStart().getStateId());
    }

    // parcourir la ligne à partir de i 
    public int[] findMatch(String line, int from) {
        for (int i = from; i <= line.length(); i++) {
            int state = start;
            int lastAccept = accepting[state] ? i : -1; // correspondance vide possible

            for (int j = i; j < line.length(); j++) {
                char ch = line.charAt(j);
                if (ch >= 128) break;            // hors ASCII : pas de transition
                state = next[state][ch];
                if (state == -1) break;          // blocage : inutile de continuer depuis i
                if (accepting[state]) lastAccept = j + 1;
            }
            if (lastAccept != -1) return new int[] { i, lastAccept };
        }
        return null;
    }

    //  booléen pour une ligne
    public boolean matches(String line) {
        return findMatch(line, 0) != null;
    }

    //  booléen pour tout un fichier
    public boolean fileContainsMatch(String path) throws IOException {
        try (BufferedReader br = open(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (matches(line)) return true;
            }
        }
        return false;
    }

    // Renvoie la ligne avec les occurrences colorées
    public String highlight(String line) {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (pos <= line.length()) {
            int[] m = findMatch(line, pos);
            if (m == null) break;
            if (m[1] == m[0]) {                  // correspondance vide : on ne surligne pas
                if (m[0] >= line.length()) break;
                sb.append(line, pos, m[0] + 1);
                pos = m[0] + 1;
                continue;
            }
            sb.append(line, pos, m[0]).append(YELLOW).append(line, m[0], m[1]).append(RESET);
            pos = m[1];
        }
        if (pos < line.length()) sb.append(line, pos, line.length());
        return sb.toString();
    }

    //  lignes qui correspondent, avec numéro de ligne 
    public int search(String path) throws IOException {
        int count = 0;
        try (BufferedReader br = open(path)) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (matches(line)) {
                    count++;
                    System.out.println(lineNumber + ":" + highlight(line));
                }
            }
        }
        return count;
    }

    private static BufferedReader open(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        List<String> params = new ArrayList<>();
        for (String a : args) {
            params.add(a);
        }
        if (params.size() != 2) {
            System.err.println("Usage : java FileSearcher  \"regex\" fichier.txt");
            System.exit(1);
        }

        String pattern = params.get(0);
        String path = params.get(1);

        try {
            DFA dfa = RegEx.buildDFA(pattern);
            FileSearcher searcher = new FileSearcher(dfa);
            int count = searcher.search(path);
            System.err.println(count + " ligne(s) trouvée(s).");
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Erreur de syntaxe dans la regex \"" + pattern + "\".");
            System.exit(3);
        }
    }
}