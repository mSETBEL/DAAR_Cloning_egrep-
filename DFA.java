import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFA {
	
	private DFAstate start;
	private List<DFAstate> states;

	private static int stateCount=0;

	public DFA(DFAstate start, List<DFAstate> states) {
		this.start = start;
		this.states = states;
	}

	// Epsilon closure 
	public Set<Integer> epsilonClosure (Set<Integer> initial, List<NDFAstate> ndfaStates) {

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
	public Set<Integer> move(int character, Set<Integer> states, List<NDFAstate> ndfaStates) {
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
	public DFAstate getOrCreateDFAState(Set<Integer> ndfaStates, List<DFAstate> dfaStates, int finalStateId) {

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



    // j'en suis à là, il manque la modification de fromNDFA ! 







	
	public DFA fromNDFA(NDFA ndfa) {
		Set<Integer> visited = new HashSet<>();

		for (NDFAstate state : ndfa.getStates()) {
			if (!visited.contains(state.getStateId())) {
				visited.add(state.getStateId());
				for (int epsilonStateId : state.epsilon) {
					visited.add(epsilonStateId);
				}

				
				DFAstate dfaState = new DFAstate(state.getStateId(), visited, false);
				for (int i = 0; i < state.transitions.size(); i++) {
					int transition = state.transitions.get(i);
					if (transition != -1) {
						dfaState.setTransition(i, transition);
					}
				}
				states.add(dfaState);
			}
		}


		return null;
	
	}

}