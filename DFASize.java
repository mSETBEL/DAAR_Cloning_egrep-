public class DFASize {
    public static void main(String[] a) throws Exception {
        for (int k = 0; k <= 10; k++) {
            StringBuilder sb = new StringBuilder("(a|b)*a");
            for (int i = 0; i < k; i++) sb.append("(a|b)");
            System.out.println(k + " " + RegEx.buildDFA(sb.toString()).getStates().size());
        }
    }
}