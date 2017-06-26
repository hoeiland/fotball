package objects.markov;

public class State {
	private int stateID;
	private int zone;
	private String team;
	private int period;
	private int matchStatus;
	private int occurrence;
	private int reward;
	@Override
	public String toString() {
		return "State [stateID=" + stateID + ", occurrence=" + occurrence + ", reward=" + reward + ", value=" + value
				+ "]";
	}

	private double value;



	public State(int stateID) {
		super();
		this.stateID = stateID;
	}


	public State(int stateID,int zone, String team, int period, int matchStatus, int reward) {
		super();
		this.stateID = stateID;
		this.zone = zone;
		this.team = team;
		this.period = period;
		this.matchStatus = matchStatus;
		this.reward = reward;
		this.occurrence = 1;
	}


	public State(int stateID, int reward, double value, int occurrence) {
		super();
		this.stateID = stateID;
		this.reward = reward;
		this.value = value;
		this.occurrence = occurrence;
	}

	public State(int stateID, double value, int occurrence) {
		super();
		this.stateID = stateID;
		this.value = value;
		this.occurrence = occurrence;
	}


	public void setTeam(String team) {
		this.team = team;
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	public void setOccurrenceZero(){
		this.occurrence = 0;
	}

	public void setOccurrence(int occurrence){
		this.occurrence = occurrence;
	}

	public int getStateID(){
		return stateID;
	}

	public int getZone() {
		return zone;
	}
//	public boolean isHome() {
//		return home;
//	}

	public int getPeriod() {
		return period;
	}

	public String getTeam() {
		return team;
	}

	public int getMatchStatus() {
		return matchStatus;
	}
	public int getOccurrence() {
		return occurrence;
	}
	public int getReward() {
		return reward;
	}

	public void incrementOccurrence(){
		this.occurrence +=1;
	}

}
