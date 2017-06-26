package objects;

public class Game {
	private int game_id;
	private int home_team_id;
	private int away_team_id;
	private int matchday;
	private int season;
	private int score;
	
	
	public Game(int game_id, int score) {
		super();
		this.game_id = game_id;
		this.score = score;
	}
	public int getGame_id() {
		return game_id;
	}
	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}
	public int getHome_team_id() {
		return home_team_id;
	}
	public void setHome_team_id(int home_team_id) {
		this.home_team_id = home_team_id;
	}
	public int getAway_team_id() {
		return away_team_id;
	}
	public void setAway_team_id(int away_team_id) {
		this.away_team_id = away_team_id;
	}
	public int getMatchday() {
		return matchday;
	}
	public void setMatchday(int matchday) {
		this.matchday = matchday;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	public Game(int game_id, int home_team_id, int away_team_id, int matchday, int season) {
		super();
		this.game_id = game_id;
		this.home_team_id = home_team_id;
		this.away_team_id = away_team_id;
		this.matchday = matchday;
		this.season = season;
	}
	public int getOtherTeam (int team){
		if (team == this.getHome_team_id()) return this.away_team_id;
		else return this.getHome_team_id();
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

}
