import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DFAstate {

    private int stateId;

    // The NDFA states represented by this DFA state
    private Set<Integer> ndfaStates;
    protected List<Integer> transitions;
    private final boolean accepting; // Indicates if this state is an accepting state

    public DFAstate(int id, Set<Integer> ndfaStates, boolean accepting) {
        this.stateId = id;
        this.ndfaStates = ndfaStates;
        this.accepting = accepting;

        // ASCII 0-127
        this.transitions = new ArrayList<>(
            java.util.Collections.nCopies(128, -1)
        );
    }

    public int getStateId() {
        return stateId;
    }

    public Set<Integer> getNDFAStates() {
        return ndfaStates;
    }

    public void setTransition(int character, int state) {
        transitions.set(character, state);
    }

    public boolean isAccepting() {
        return accepting;
    }
    public int getTransition(int character) {
        return transitions.get(character);
    }
}