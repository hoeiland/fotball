package objects;

public class PlayerGameTime {
	private int playerID;
	private int teamID;
	private int season2014;
	private int season2015;
	private int season2016;
	private int season2017;

	public PlayerGameTime(int playerID, int teamID) {
		super();
		this.playerID = playerID;
		this.teamID = teamID;
		this.season2014 = 0;
		this.season2015 = 0;
		this.season2016 = 0;
		this.season2017 = 0;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getTeamID(){
		return playerID;
	}

	public int getSeason2014() {
		return season2014;
	}

	public void incrementGameTime2014(int time) {
		this.season2014 += time;
	}

	public int getSeason2015() {
		return season2015;
	}

	public void incrementGameTime2015(int time) {
		this.season2015 += time;
	}

	public int getSeason2016() {
		return season2016;
	}

	public void incrementGameTime2016(int time) {
		this.season2016 += time;
	}

	public int getSeason2017() {
		return season2017;
	}

	public void incrementGameTime2017(int time) {
		this.season2017 += time;
	}


	public void incrementGameTime(int time, int season) {
		switch (season){
			case 2014: this.season2014 += time;
			break;
			case 2015: this.season2015 += time;
			break;
			case 2016: this.season2016 += time;
			break;
			case 2017: this.season2017 += time;
		}
	}

}
