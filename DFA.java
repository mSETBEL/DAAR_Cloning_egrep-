import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DFA {
	
	private DFAstate start;
	private List<DFAstate> states;

	private static int stateCount=0;

	public DFA(DFAstate start, List<DFAstate> acceptingStates,
		List<DFAstate> states) {
		this.start = start;
		this.states = states;
	}

	// Epsilon closure 
	public static Set<Integer> epsilonClosure (Set<Integer> initial, List<NDFAstate> ndfaStates) {

		Set<Integer> result = new HashSet<>();
		result.addAll(initial);

		boolean changed = true;
		while (changed) {
			changed = false;

			for (NDFAstate state : ndfaStates){

				if (!result.contains(state.getStateId())) {
					continue;
				}

				for (int epsilonStateId : state.epsilon) {
					if (!result.contains(epsilonStateId)) {
						result.add(epsilonStateId);
						changed = true;
					}
				}
			}
		}
		return result;
	}

	// Retourne les destinations possibles à partir d'un ensemble d'états NDFA pour un caractère donné	
	public static Set<Integer> move(int character, Set<Integer> states, List<NDFAstate> ndfaStates) {
		Set<Integer> result = new HashSet<>();

		for (NDFAstate state : ndfaStates) {
			if (states.contains(state.getStateId())) {
				int transition = state.transitions.get(character);
				if (transition != -1) {
					result.add(transition);
				}
			}
		}
		return result;
	}



	// Search or create a DFA state
	public static DFAstate getOrCreateDFAState(Set<Integer> ndfaStates, List<DFAstate> dfaStates, int finalStateId) {

		for (DFAstate dfaState : dfaStates) {
			if (dfaState.getNDFAStates().equals(ndfaStates)) {
				return dfaState;
			}
		}

		boolean accepting = ndfaStates.contains(finalStateId);

		DFAstate newState = new DFAstate(stateCount++,ndfaStates,accepting);

		dfaStates.add(newState);
		return newState;
	}



	
	public static DFA fromNDFA(NDFA ndfa) {

		List<DFAstate> dfaStates = new ArrayList<>(); //resulting DFA states

		// Map each subset of NDFA states to its DFA state
        Map<Set<Integer>, DFAstate> subsets = new HashMap<>();

		// Queue of DFA states that still need processing
        Queue<DFAstate> queue = new ArrayDeque<>();

		// Initial DFA state is the epsilon closure of the NDFA start state
		Set<Integer> initialSet = new HashSet<>();
		initialSet.add(ndfa.getStart().getStateId());
		initialSet = epsilonClosure(initialSet, ndfa.getStates());
		
		boolean accepting = initialSet.contains(ndfa.getEnd().getStateId());

		DFAstate dfaStart = new DFAstate(stateCount++, initialSet, accepting);	

        dfaStates.add(dfaStart);
        subsets.put(initialSet, dfaStart);
        queue.add(dfaStart);

		while (!queue.isEmpty()) {
			DFAstate currentDFAState = queue.poll();

			for (int c= 0; c<128; c++) {
				Set<Integer> destination = move(c, currentDFAState.getNDFAStates(), ndfa.getStates());

                // No transition on this character
                if (destination.isEmpty()) {
                    continue;
                }

                // Include epsilon transitions
                destination = epsilonClosure(destination, ndfa.getStates());

				// if the destination set is not already represented by a DFA state, create a new DFA state
                DFAstate destinationState = subsets.get(destination);
                if (destinationState == null) {
                    boolean destAccepting = destination.contains(ndfa.getEnd().getStateId());
                    destinationState = new DFAstate(stateCount++, destination, destAccepting);
                    subsets.put(destination, destinationState);
                    dfaStates.add(destinationState); 
                    queue.add(destinationState);
                }
                currentDFAState.setTransition(c, destinationState.getStateId());

				// Set the transition for the current DFA state
				currentDFAState.setTransition(c, destinationState.getStateId());

			}
		}
		
		
		// collecting accepting states
		List<DFAstate> acceptingStates = new ArrayList<>();

		for (DFAstate state : dfaStates) {
			if (state.getNDFAStates().contains(ndfa.getEnd().getStateId())) {
				acceptingStates.add(state);
			}
		}

		return new DFA(dfaStart, acceptingStates, dfaStates);
	}
	
	@Override
	public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("State | Transition\n");
        result.append("----------------------------\n");

        for (DFAstate state : states) {

            result.append("q")
                .append(state.getStateId())
                .append(state.getNDFAStates())
                .append("    | ");

            boolean hasTransition = false;
            for (int c = 0; c < state.transitions.size(); c++) {
                if (state.transitions.get(c) != -1) {
                    if (hasTransition) result.append(", ");
                    result.append((char)c).append(" -> q").append(state.transitions.get(c));
                    hasTransition = true;
                }
            }

            if (!hasTransition) {
                result.append("-");
            }
                        
            if (state.isAccepting()) result.append("(accepting)");

            result.append("\n");
        }

        return result.toString();
    }
	

}