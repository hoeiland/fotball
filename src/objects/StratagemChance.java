package objects;

public class StratagemChance {

	int StrataGameID;
	String Competition;
	String GameDate;
	String PlayerName;
	int Icon; 
	int Rating; 
	int StrataTeamID; 
	int StrataOppTeamID; 
	
	public StratagemChance() {
		super();
	}
	
	@Override
	public String toString() {
		return "StratagemChance [StrataGameID=" + StrataGameID + ", Competition=" + Competition + ", GameDate="
				+ GameDate + ", PlayerName=" + PlayerName + ", Icon=" + Icon + ", Rating=" + Rating + ", StrataTeamID="
				+ StrataTeamID + ", StrataOppTeamID=" + StrataOppTeamID + ", ChanceType=" + ChanceType + ", ChanceTime="
				+ ChanceTime + ", LocationX=" + LocationX + ", LocationY=" + LocationY + ", Bodypart=" + Bodypart
				+ ", ShotQuality=" + ShotQuality + ", DefPressure=" + DefPressure + ", NumDefPlayers=" + NumDefPlayers
				+ ", NumAttPlayers=" + NumAttPlayers + ", Outcome=" + Outcome + ", AssistPlayerName=" + AssistPlayerName
				+ ", AssistType=" + AssistType + ", AssistLocationX=" + AssistLocationX + ", AssistLocationY="
				+ AssistLocationY + ", SecondAssistName=" + SecondAssistName + ", SecondAssistType=" + SecondAssistType
				+ ", StrataPID=" + StrataPID + ", StrataAssistPID=" + StrataAssistPID + ", StrataSecondAssistPID="
				+ StrataSecondAssistPID + "]";
	}

	public int getStrataGameID() {
		return StrataGameID;
	}
	public void setStrataGameID(int strataGameID) {
		StrataGameID = strataGameID;
	}
	public String getCompetition() {
		return Competition;
	}
	public void setCompetition(String competition) {
		Competition = competition;
	}
	public String getGameDate() {
		return GameDate;
	}
	public void setGameDate(String gameDate) {
		GameDate = gameDate;
	}
	public String getPlayerName() {
		return PlayerName;
	}
	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}
	public int getIcon() {
		return Icon;
	}
	public void setIcon(int icon) {
		Icon = icon;
	}
	public int getRating() {
		return Rating;
	}
	public void setRating(int rating) {
		Rating = rating;
	}
	public int getStrataTeamID() {
		return StrataTeamID;
	}
	public void setStrataTeamID(int strataTeamID) {
		StrataTeamID = strataTeamID;
	}
	public int getStrataOppTeamID() {
		return StrataOppTeamID;
	}
	public void setStrataOppTeamID(int strataOppTeamID) {
		StrataOppTeamID = strataOppTeamID;
	}
	public String getChanceType() {
		return ChanceType;
	}
	public void setChanceType(String chanceType) {
		ChanceType = chanceType;
	}
	public String getChanceTime() {
		return ChanceTime;
	}
	public void setChanceTime(String chanceTime) {
		ChanceTime = chanceTime;
	}
	public int getLocationX() {
		return LocationX;
	}
	public void setLocationX(int locationX) {
		LocationX = locationX;
	}
	public int getLocationY() {
		return LocationY;
	}
	public void setLocationY(int locationY) {
		LocationY = locationY;
	}
	public String getBodypart() {
		return Bodypart;
	}
	public void setBodypart(String bodypart) {
		Bodypart = bodypart;
	}
	public int getShotQuality() {
		return ShotQuality;
	}
	public void setShotQuality(int shotQuality) {
		ShotQuality = shotQuality;
	}
	public int getDefPressure() {
		return DefPressure;
	}
	public void setDefPressure(int defPressure) {
		DefPressure = defPressure;
	}
	public int getNumDefPlayers() {
		return NumDefPlayers;
	}
	public void setNumDefPlayers(int numDefPlayers) {
		NumDefPlayers = numDefPlayers;
	}
	public int getNumAttPlayers() {
		return NumAttPlayers;
	}
	public void setNumAttPlayers(int numAttPlayers) {
		NumAttPlayers = numAttPlayers;
	}
	public int getOutcome() {
		return Outcome;
	}
	public void setOutcome(int outcome) {
		Outcome = outcome;
	}
	public String getAssistPlayerName() {
		return AssistPlayerName;
	}
	public void setAssistPlayerName(String assistPlayerName) {
		AssistPlayerName = assistPlayerName;
	}
	public String getAssistType() {
		return AssistType;
	}
	public void setAssistType(String assistType) {
		AssistType = assistType;
	}
	public int getAssistLocationX() {
		return AssistLocationX;
	}
	public void setAssistLocationX(int assistLocationX) {
		AssistLocationX = assistLocationX;
	}
	public int getAssistLocationY() {
		return AssistLocationY;
	}
	public void setAssistLocationY(int assistLocationY) {
		AssistLocationY = assistLocationY;
	}
	public String getSecondAssistName() {
		return SecondAssistName;
	}
	public void setSecondAssistName(String secondAssistName) {
		SecondAssistName = secondAssistName;
	}
	public String getSecondAssistType() {
		return SecondAssistType;
	}
	public void setSecondAssistType(String secondAssistType) {
		SecondAssistType = secondAssistType;
	}
	public int getStrataPID() {
		return StrataPID;
	}
	public void setStrataPID(int strataPID) {
		StrataPID = strataPID;
	}
	public int getStrataAssistPID() {
		return StrataAssistPID;
	}
	public void setStrataAssistPID(int strataAssistPID) {
		StrataAssistPID = strataAssistPID;
	}
	public int getStrataSecondAssistPID() {
		return StrataSecondAssistPID;
	}
	public void setStrataSecondAssistPID(int strataSecondAssistPID) {
		StrataSecondAssistPID = strataSecondAssistPID;
	}
	String ChanceType; 
	String ChanceTime; 
	int LocationX; 
	int LocationY; 
	String Bodypart; 
	int ShotQuality; 
	int DefPressure; 
	int NumDefPlayers; 
	int NumAttPlayers; 
	int Outcome;
	String AssistPlayerName; 
	String AssistType;
	int AssistLocationX; 
	int AssistLocationY; 
	String SecondAssistName;
	String SecondAssistType;
	int StrataPID; 
	int StrataAssistPID; 
	int StrataSecondAssistPID;
}
