package objects.markov;


public class StateTransition {
	private int stateTransitionID;
	private State startState;
	private State endState;
	private String action;
	private int occurrence;

	public StateTransition(int stateTransitionID, State startState, State endState, String action) {
		super();
		this.startState = startState;
		this.endState = endState;
		this.action = action;
		this.occurrence = 1;
	}

	

	public StateTransition(State startState, State endState, String action) {
		super();
		this.startState = startState;
		this.endState = endState;
		this.action = action;
		this.occurrence = 1;
	}
	
	public void setStateTransitionID(int stateTransitionID) {
		this.stateTransitionID = stateTransitionID;
	}

	@Override
	public String toString() {
		return "StateTransition [stateTransitionID=" + stateTransitionID + ", startState=" + startState.getStateID() + ", endState="
				+ endState.getStateID() + ", action=" + action + ", occurrence=" + occurrence + "]";
	}



	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

	public int getStateTransitionID() {
		return stateTransitionID;
	}

	public State getStartState() {
		return startState;
	}

	public State getEndState() {
		return endState;
	}

	public String getAction() {
		return action;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void incrementOccurence(){
		this.occurrence+=1;
	}

}
