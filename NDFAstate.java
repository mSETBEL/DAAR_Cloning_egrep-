import java.util.ArrayList;
import java.util.List;
public class NDFAstate {
	
	protected List<Integer> transitions;
	protected List<Integer> epsilon;

	private final int stateId;

	public NDFAstate(int id) {
		this.transitions = new ArrayList<>(java.util.Collections.nCopies(128, -1));
		this.epsilon = new ArrayList<>();
		stateId = id;
	}

	public void setTransition(int state, int character){
		transitions.set(state,character);
	}

	public void addEpsilon(int state){
		epsilon.add(state);
	}

	public int getStateId() {
		return stateId;
	}
}
