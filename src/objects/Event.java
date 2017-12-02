package objects;

public class Event {
	private long event_id;
	private String action_type;
	private int outcome;
	private int team_id;
	private int player_id;
	private float xstart;
	private float ystart;
	private float xend;
	private float yend;
	private int number;
	private int sequence;
	private int game_id;
	private int period;
	private int goaldifference;
	private int minute;
	private int second;

	public long getEvent_id() {
		return event_id;
	}

	public String getAction_type() {
		return action_type;
	}

	public int getTeam_id() {
		return team_id;
	}

	public int getPlayer_id() {
		return player_id;
	}

	public float getXstart() {
		return xstart;
	}

	public float getYstart() {
		return ystart;
	}

	public int getNumber() {
		return number;
	}

	public int getSequence() {
		return sequence;
	}

	public int getGame_id() {
		return game_id;
	}

	public int getPeriod() {
		return period;
	}

	public int getGoaldifference() {
		return goaldifference;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}


	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Event [event_id=" + event_id + ", action_type=" + action_type + ", outcome=" + outcome + ", team_id="
				+ team_id + ", player_id=" + player_id + ", xstart=" + xstart + ", ystart=" + ystart + ", xend=" + xend
				+ ", yend=" + yend + ", number=" + number + ", sequence=" + sequence + ", game_id=" + game_id
				+ ", period=" + period + ", goaldifference="
				+ goaldifference + ", minute=" + minute + ", second=" + second + "]";
	}

	public Event(long event_id, String action_type, int outcome, int team_id, int player_id, float xstart, float ystart, float xend, float yend, int number,
			int sequence, int game_id, int period, int minute, int second, int goaldifference) {
		super();
		this.event_id = event_id;
		this.action_type = action_type;
		this.outcome = outcome;
		this.team_id = team_id;
		this.player_id = player_id;
		this.xstart = xstart;
		this.ystart = ystart;
		this.xend = xend;
		this.yend = yend;
		this.number = number;
		this.sequence = sequence;
		this.game_id = game_id;
		this.period = period;
		this.minute = minute;
		this.second = second;
		this.goaldifference = goaldifference;
	}

	public int getOutcome() {
		return outcome;
	}

	public float getXend() {
		return xend;
	}

	public float getYend() {
		return yend;
	}

	public void setEvent_id(Long eventid) {
		this.event_id = eventid;
		
	}
}
