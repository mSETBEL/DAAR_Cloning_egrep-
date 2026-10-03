import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DFAstate {

    private int stateId;

    // The NDFA states represented by this DFA state
    private Set<Integer> ndfaStates;

    protected List<Integer> transitions;

    public DFAstate(int id, Set<Integer> ndfaStates) {
        this.stateId = id;
        this.ndfaStates = ndfaStates;

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
}