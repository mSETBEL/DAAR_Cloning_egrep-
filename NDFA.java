import java.util.ArrayList;
import java.util.List;


public class NDFA {
	
	private NDFAstate start;
    private NDFAstate end;
    private List<NDFAstate> states;

    private static int stateCount=0;

    public NDFA(NDFAstate start, NDFAstate end, List<NDFAstate> states) {
        this.states = states;
        this.start = start;
        this.end = end;
    }


    public void newState() {
        states.add(new NDFAstate(stateCount++));
    }

	//step 1 : for each leaf, we construct an instance of the automaton
	public static NDFA makeLeaf(int character) {

        int startId = stateCount++;
        int endId = stateCount++;

        NDFAstate start = new NDFAstate(startId);
        NDFAstate end = new NDFAstate(endId);

        start.setTransition(character, endId);
        List<NDFAstate> states = new ArrayList<>();
            states.add(start);
            states.add(end);

        return new NDFA(start, end, states);
    }

    public List<NDFAstate> getStates() {
        return states;
    }

    public static NDFA makeConcatenation(NDFA ndfa1, NDFA ndfa2) {
        ndfa1.end.addEpsilon(ndfa2.start.getStateId());
        List<NDFAstate> newStates = new ArrayList<>(ndfa1.states);
        newStates.addAll(ndfa2.states);
        return new NDFA(ndfa1.start, ndfa2.end, newStates);
    }


    public static NDFA makeClosure(NDFA ndfa) {
        int startId = stateCount++;
        int endId = stateCount++;

        NDFAstate newStart = new NDFAstate(startId);
        NDFAstate newEnd = new NDFAstate(endId);

        newStart.addEpsilon(ndfa.start.getStateId());
        newStart.addEpsilon(newEnd.getStateId());
        ndfa.end.addEpsilon(ndfa.start.getStateId());
        ndfa.end.addEpsilon(newEnd.getStateId());

        List<NDFAstate> newStates = new ArrayList<>(ndfa.states);
        newStates.add(newStart);
        newStates.add(newEnd);

        return new NDFA(newStart, newEnd, newStates);
    }

    public static NDFA makeAlternation(NDFA ndfa1, NDFA ndfa2) {
        int startId = stateCount++;
        int endId = stateCount++;

        NDFAstate newStart = new NDFAstate(startId);
        NDFAstate newEnd = new NDFAstate(endId);

        newStart.addEpsilon(ndfa1.start.getStateId());
        newStart.addEpsilon(ndfa2.start.getStateId());
        ndfa1.end.addEpsilon(newEnd.getStateId());
        ndfa2.end.addEpsilon(newEnd.getStateId());

        List<NDFAstate> newStates = new ArrayList<>(ndfa1.states);
        newStates.addAll(ndfa2.states);
        newStates.add(newStart);
        newStates.add(newEnd);

        return new NDFA(newStart, newEnd, newStates);
    }

    public static NDFA fromRegExTree(RegExTree tree) {

        // Leaf
        if (tree.subTrees.isEmpty()) {
            return makeLeaf(tree.root);
        }

        // Concatenation: A.B
        if (tree.root == RegEx.CONCAT) {
            NDFA left = fromRegExTree(tree.subTrees.get(0));
            NDFA right = fromRegExTree(tree.subTrees.get(1));

            return makeConcatenation(left, right);
        }

        // Alternation: A|B
        if (tree.root == RegEx.ALTERN) {
            NDFA left = fromRegExTree(tree.subTrees.get(0));
            NDFA right = fromRegExTree(tree.subTrees.get(1));

            return makeAlternation(left, right);
        }

        // Kleene star: A*
        if (tree.root == RegEx.ETOILE) {
            NDFA child = fromRegExTree(tree.subTrees.get(0));

            return makeClosure(child);
        }

        throw new IllegalArgumentException(
            "Unknown node: " + tree.root
        );
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("State | Transition | Epsilon\n");
        result.append("----------------------------\n");

        for (NDFAstate state : states) {

            result.append("q")
                .append(state.getStateId())
                .append("    | ");

            boolean hasTransition = false;

            for (int c = 0; c < state.transitions.size(); c++) {
                if (state.transitions.get(c) != -1) {
                    result.append((char)c)
                        .append(" -> q")
                        .append(state.transitions.get(c));
                    hasTransition = true;
                }
            }

            if (!hasTransition) {
                result.append("-");
            }

            result.append("       | ");

            if (state.epsilon.isEmpty()) {
                result.append("-");
            } else {
                for (int i = 0; i < state.epsilon.size(); i++) {
                    if (i > 0) result.append(", ");

                    result.append("q")
                        .append(state.epsilon.get(i));
                }
            }

            result.append("\n");
        }

        return result.toString();
    }

}
