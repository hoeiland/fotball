package objects;

import java.util.ArrayList;
import java.util.Hashtable;

public class PlayerImpact {

	private int playerID;
	private int gameID;
	private int teamID;
	private Double tackle;
	private Double clearance;
	private Double aerialDuel;
	private Double dispossessed;
	private Double foulCommitted;
	private Double fouled;
	private Double interception;
	private Double ballRecovery;
	private Double ballTouch;
	private Double ballCarry;
	private Double pass;
	private Double longPass;
	private Double cross;
	private Double freekickPass;
	private Double cornerTaken;
	private Double throwInTaken;
	private Double takeOn;
	private Double shot;
	private Double headedShot;
	private Double ballReceived;
	private Double total;
	private Double blockedShot;
	private Double savedShot;
	private Hashtable<String, Integer> actionCount;
	
	public PlayerImpact(int playerID, int gameID, int teamID) {
		super();
		this.playerID = playerID;
		this.teamID = teamID;
		this.gameID = gameID;
		this.tackle =   0.0;
		this.clearance =  0.0;
		this.aerialDuel =  0.0;
		this.dispossessed =  0.0;
		this.foulCommitted =  0.0;
		this.fouled =  0.0;
		this.interception =  0.0;
		this.ballRecovery =  0.0;
		this.ballTouch =  0.0;
		this.ballCarry =  0.0;
		this.pass =  0.0;
		this.longPass =  0.0;
		this.cross =  0.0;
		this.freekickPass =  0.0;
		this.cornerTaken =  0.0;
		this.throwInTaken =  0.0;
		this.takeOn =  0.0;
		this.shot =  0.0;
		this.headedShot =  0.0;
		this.ballReceived =  0.0;
		this.blockedShot =  0.0;
		this.savedShot =  0.0;
		this.total= 0.0;
		this.actionCount =  new Hashtable<String, Integer>();
		generateCountTable();
	}
	
	private void generateCountTable(){
		actionCount.put("tackle",0);
		actionCount.put("clearance",0);
		actionCount.put("aerialDuel",0);
		actionCount.put("dispossessed",0);
		actionCount.put("foulCommitted",0);
		actionCount.put("fouled",0);
		actionCount.put("interception",0);
		actionCount.put("ballRecovery",0);
		actionCount.put("ballTouch",0);
		actionCount.put("ballCarry",0);
		actionCount.put("pass",0);
		actionCount.put("longPass",0);
		actionCount.put("cross",0);
		actionCount.put("freekickPass",0);
		actionCount.put("cornerTaken",0);
		actionCount.put("throwInTaken",0);
		actionCount.put("takeOn",0);
		actionCount.put("shot",0);
		actionCount.put("headedShot",0);
		actionCount.put("ballReceived",0);
		actionCount.put("total",0);
		actionCount.put("blockedShot",0);
		actionCount.put("actionCount",0);
		actionCount.put("savedShot", 0);
	}
	
	public void setTotal() {
		double totalVal = this.getAerialDuel()+this.getBallCarry()+this.getBallRecovery()+this.getBallTouch()+this.getClearance()+this.getCornerTaken()+this.getCross()+
					this.getDispossessed()+this.getFoulCommitted()+this.getFouled()+this.getFreekickPass()+this.getInterception()+this.getLongPass()+this.getPass()+this.getShot()+
					this.getTackle()+this.getTakeOn()+this.getThrowInTaken()+this.getBallReceived()+this.getBlockedShot()+this.getSavedShot()+this.getHeadedShot();
		this.total = totalVal;
	}
	
	@Override
	public String toString() {
		return "PlayerImpact [playerID=" + playerID + ", gameID=" + gameID + ", teamID=" + teamID + ", shot=" + shot
				+ ", total=" + total + "]";
	}

	public void updateValue(String action, Double value){
		if(action.equals("Pass")){
			this.updatePass(value);
		}
		else if(action.equals("Ball received")){
			this.updateBallReceived(value);
		}
		else if(action.equals("Long pass")){
			this.updateLongPass(value);
		}
		else if(action.equals("Ball carry")){
			this.updateBallCarry(value);
		}
		else if(action.equals("Ball recovery")){
			this.updateBallRecovery(value);
		}
		else if(action.equals("Aerial duel")){
			this.updateAerial(value);
		}
		else if(action.equals("Clearance")){
			this.updateClearance(value);
		}
		else if(action.equals("Throw in taken")){
			this.updateThrowIn(value);
		}
		else if(action.equals("Ball touch")){
			this.updateBallTouch(value);
		}
		else if(action.equals("Interception")){
			this.updateInterception(value);
		}
		else if(action.equals("Cross")){
			this.updateCross(value);
		}
		else if(action.equals("Tackle")){
			this.updateTackle(value);
		}
		else if(action.equals("Shot")){
			this.updateShot(value);
		}
		else if(action.equals("Headed shot")){
			this.updateHeadedShot(value);
		}
		else if(action.equals("Take on")){
			this.updateTakeOn(value);
		}
		else if(action.equals("Free kick pass")){
			this.updateFreekickPass(value);
		}
		else if(action.equals("Foul committed")){
			this.updateFoulCommitted(value);
		}
		else if(action.equals("Fouled")){
			this.updateFouled(value);
		}
		else if(action.equals("Dispossessed")){
			this.updateDispossessed(value);
		}
		else if(action.equals("Corner taken")){
			this.updateCorner(value);
		}
		else if(action.equals("Blocked shot")){
			this.updateBlockedShot(value);
		}
		else if(action.equals("Shot saved")){
			this.updateSavedShot(value);
		}

	}

	private void updateSavedShot(Double value) {
		savedShot+=value;
		
	}

	private void updateBlockedShot(Double value) {
		blockedShot+=value;
		
	}

	private void updateCorner(Double value) {
		cornerTaken+=value;
		
	}

	private void updateDispossessed(Double value) {
		dispossessed+=value;
		
	}

	private void updateFouled(Double value) {
		fouled+=value;
		
	}

	private void updateFoulCommitted(Double value) {
		foulCommitted+=value;
		
	}

	private void updateFreekickPass(Double value) {
		freekickPass+=value;
		
	}

	private void updateTakeOn(Double value) {
		takeOn+=value;
		
	}

	private void updateHeadedShot(Double value) {
		headedShot+=value;
	}

	private void updateShot(Double value) {
		shot+=value;
		
	}

	private void updateTackle(Double value) {
		tackle+=value;
		
	}

	private void updateCross(Double value) {
		cross+=value;
		
	}

	private void updateInterception(Double value) {
		interception+=value;
		
	}

	private void updateBallTouch(Double value) {
		ballTouch+=value;
		
	}

	private void updateThrowIn(Double value) {
		throwInTaken+=value;
		
	}

	private void updateClearance(Double value) {
		clearance+=value;
	}

	private void updateAerial(Double value) {
		aerialDuel+=value;
		
	}

	private void updateBallRecovery(Double value) {
		ballRecovery+=value;
		
	}

	private void updateBallCarry(Double value) {
		ballCarry+=value;
		
	}

	private void updateLongPass(Double value) {
		longPass+=value;
		
	}

	private void updateBallReceived(Double value) {
		ballReceived+=value;
		
	}

	private void updatePass(Double value) {
		pass+=value;
		
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getGameID() {
		return gameID;
	}

	public int getTeamID() {
		return teamID;
	}

	public Double getTackle() {
		return tackle;
	}

	public Double getClearance() {
		return clearance;
	}

	public Double getAerialDuel() {
		return aerialDuel;
	}

	public Double getDispossessed() {
		return dispossessed;
	}

	public Double getFoulCommitted() {
		return foulCommitted;
	}

	public Double getFouled() {
		return fouled;
	}

	public Double getInterception() {
		return interception;
	}

	public Double getBallRecovery() {
		return ballRecovery;
	}

	public Double getBallTouch() {
		return ballTouch;
	}

	public Double getBallCarry() {
		return ballCarry;
	}

	public Double getPass() {
		return pass;
	}

	public Double getLongPass() {
		return longPass;
	}

	public Double getCross() {
		return cross;
	}

	public Double getFreekickPass() {
		return freekickPass;
	}

	public Double getCornerTaken() {
		return cornerTaken;
	}

	public Double getThrowInTaken() {
		return throwInTaken;
	}

	public Double getTakeOn() {
		return takeOn;
	}

	public Double getShot() {
		return shot;
	}

	public Double getHeadedShot() {
		return headedShot;
	}

	public Double getBallReceived() {
		return ballReceived;
	}

	public Double getTotal() {
		return total;
	}

	public Double getBlockedShot() {
		return blockedShot;
	}

	public Double getSavedShot() {
		return savedShot;
	}

	public Hashtable<String, Integer> getActionCount() {
		return actionCount;
	}
}

