package objects.markov;

public class StateAction {
	private int stateID;
	private String action;
	private int occurrence;
	private double value;

	public StateAction(int stateID, String action, int occurrence) {
		super();
		this.stateID = stateID;
		this.action = action;
		this.occurrence = occurrence;
		this.value = 0;
	}

	@Override
	public String toString() {
		return "StateAction [stateID=" + stateID + ", action=" + action + ", occurrence=" + occurrence + ", value="
				+ value + "]";
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getStateID() {
		return stateID;
	}

	public String getAction() {
		return action;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public double getValue() {
		return value;
	}
	public void incrementOccurrence(){
		this.occurrence++;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

}
