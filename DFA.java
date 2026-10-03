import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFA {
	
	private DFAstate start;
	private DFAstate end;
	private List<DFAstate> states;

	private static int stateCount=0;

	public DFA(DFAstate start, DFAstate end, List<DFAstate> states) {
		this.states = states;
		this.start = start;
		this.end = end;
	}

	public void newState() {
		states.add(new DFAstate(stateCount++));
	}

	public DFA fromNDFA(NDFA ndfa) {
		Set<Integer> visited = new HashSet<>();

		for (NDFAstate state : ndfa.getStates()) {
			if (!visited.contains(state.getStateId())) {
				visited.add(state.getStateId());
				for (int epsilonStateId : state.epsilon) {
					visited.add(epsilonStateId);
				}

				
				DFAstate dfaState = new DFAstate(state.getStateId());
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