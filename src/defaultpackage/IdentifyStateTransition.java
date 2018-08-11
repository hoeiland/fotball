package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import objects.markov.State;
import objects.markov.StateTransition;

public class IdentifyStateTransition {
	public static void gameTransitions (int gameid) throws ClassNotFoundException, SQLException, InterruptedException {
		ResultSet eventrs = Dbhandler.getMarkovEventsFromGame(gameid);
		ResultSet statesrs = Dbhandler.getDatabaseStates();
		ResultSet statetransitionrs = Dbhandler.getDatabaseStateTrans();
		ArrayList<State> stateList = getStateListFromRS(statesrs);
		Hashtable<String, StateTransition> transHash = getStateTransHashFromRS(statetransitionrs);
		State startState = null;
		State endState = null;
		boolean prevEndOfPeriod = false;
		String prevAction = "";
		ArrayList<String> sqlList = new ArrayList<String>();
		ArrayList<StateTransition> newTrans = new ArrayList<StateTransition>();
		int stateTransitionID = Dbhandler.getMaxTransitionID() + 1;
		while(eventrs.next()) {

			String action = eventrs.getString("Action");
			int outcome = eventrs.getInt("Outcome");
			int minute = eventrs.getInt("Minute");
			int period = eventrs.getInt("Period");
			int goalDifference = eventrs.getInt("GoalDifference");
			int teamID = eventrs.getInt("TeamID");
			int homeID = eventrs.getInt("HomeID");
			int awayID = eventrs.getInt("AwayID");
			float xStart = eventrs.getFloat("Xstart");
			float yStart = eventrs.getFloat("Ystart");
			float xEnd = eventrs.getFloat("Xend");
			float yEnd = eventrs.getFloat("Yend");
			int eventNumber = eventrs.getInt("Number");
			int eventID = eventrs.getInt("EventID");
			int startZone = MarkovBuilder.getZoneFromCoordinates(xStart, yStart);
			int endZone = MarkovBuilder.getZoneFromCoordinates(xEnd, yEnd);
			int statePeriod = MarkovBuilder.getPeriod(minute, period);

			int reward = MarkovBuilder.getReward(action, teamID==homeID);
			String team = "";
			String otherTeam="";
			if (teamID == homeID){
				team = "Home";
				otherTeam = "Away";
			}
			else {
				team = "Away";
				otherTeam = "Home";
			}
			int matchStatus = MarkovBuilder.getMatchStatus(goalDifference);
			if (action.equals("Goal") || action.equals("Out of play") || action.equals("Goalkeeper")){
				//				System.out.println(action);
				prevAction = action;
				continue;
			}
			else if (action.equals("End of period")){
				prevEndOfPeriod = true;
				continue;
			}
			if (eventNumber == 1 || prevEndOfPeriod || prevAction.equals("Goal") || prevAction.equals("Goalkeeper")) { //lager kun startState for event n�r det er f�rste event i en omgang!
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == startZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
							&& s.getReward() == 0){
						startState = s;
						break;
					}
				}
			}
			eventrs.next();
			String nextAction = eventrs.getString("Action");
			int nextZone = MarkovBuilder.getZoneFromCoordinates(eventrs.getFloat("Xstart"), eventrs.getFloat("Ystart"));
			int nextTeamID = eventrs.getInt("TeamID");
			String nextTeam = "";
			String nextOtherTeam = "";

			if (homeID==nextTeamID){
				nextTeam = "Home";
				nextOtherTeam ="Away";
			}
			else {
				nextTeam = "Away";
				nextOtherTeam = "Home";
			}
			int nextMatchStatus = MarkovBuilder.getMatchStatus(goalDifference);
			int nextReward = MarkovBuilder.getReward(nextAction, nextTeamID==homeID);
			eventrs.previous();
			
			if(nextAction.equals("Goalkeeper")){
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						endState = s;
						break;
					}
				}
			}
			else if (action.equals("Pass") || action.equals("Long pass") || action.equals("Cross") || action.equals("Free kick pass") || action.equals("Throw in taken") || action.equals("Corner taken")){
				if (!nextAction.equals("Aerial duel")){
					if (outcome == 1){ // hvis outcome er 1 -> start og end state har samme lag
						//lager end state
						for (int i = 0; i < stateList.size(); i++){
							State s = stateList.get(i);
							if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
									&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
									&& s.getReward() == reward){
								endState = s;
								break;
							}
						}
					}
					else {//outcome ==0 -> start state hos team som slo pasning, end state har ikke noe team
						if (nextAction.equals("Out of play")){
							eventrs.next();
							eventrs.next();
							nextZone = MarkovBuilder.getZoneFromCoordinates(eventrs.getFloat("Xstart"), eventrs.getFloat("Ystart"));
							eventrs.previous();
							eventrs.previous();
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
										&& s.getReward() == reward){
									endState = s;
									break;
								}
							}
						}
						else if (nextAction.equals("Shot") || nextAction.equals("Headed shot") || nextAction.equals("Pass") || nextAction.equals("Ball carry") || nextAction.equals("Take on")
								|| nextAction.equals("Long pass") || nextAction.equals("Cross")){
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
										&& s.getReward() == reward){
									endState = s;
									break;
								}
							}
						}
						else {
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals("None") && s.getMatchStatus() == MarkovBuilder.getMatchStatus(goalDifference)
										&& s.getReward() == reward){
									endState = s;
									break;
								}
							}
						}
					}
				}
				else { //nextAction er hodeduell
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == endZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals("None") && s.getMatchStatus() == MarkovBuilder.getMatchStatus(goalDifference)
								&& s.getReward() == reward){
							endState = s;
							break;
						}
					}
				}
			}
			else if (nextAction.equals("Ball recovery")){
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == MarkovBuilder.getMatchStatus(goalDifference)
							&& s.getReward() == reward){
						endState = s;
						break;
					}
				}
			}
			else if (action.equals("Ball carry") || action.equals("Aerial duel") || action.equals("Ball recovery")){
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
							&& s.getReward() == reward){
						endState = s;
						break;
					}
				}
			}
			else if (action.equals("Foul committed")){
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == nextMatchStatus
							&& s.getReward() == reward){
						endState = s;
						break;
					}
				}
			}
			else if(nextAction.equals("Goal")){
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						endState = s;
						break;
					}
				}
			}
			else if (nextAction.equals("End of period")){ //neste event er ikke m�l
				prevEndOfPeriod = true;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						endState = s;
						break;
					}
				}
			}
			else { //action er en av de som ikke er sjekket eksplisitt (i.e. tackle, interception etc)

				if (nextAction.equals("Out of play")){ //neste er out of play
					eventrs.next();
					eventrs.next();
					nextZone = MarkovBuilder.getZoneFromCoordinates(eventrs.getFloat("Xstart"), eventrs.getFloat("Ystart"));
					eventrs.previous();
					eventrs.previous();
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							endState = s;
							break;
						}
					}
				}
				else if (nextAction.equals("Foul committed")){
					eventrs.next();
					eventrs.next();
					nextZone = MarkovBuilder.getZoneFromCoordinates(eventrs.getFloat("Xstart"), eventrs.getFloat("Ystart"));
					eventrs.previous();
					eventrs.previous();
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextOtherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							endState = s;
							break;
						}
					}
				}
				else {
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
								&& s.getReward() == nextReward){
							endState = s;
							break;
						}
					}
				}
			}
			StateTransition thisTransition = new StateTransition(startState, endState, action);
			String transKey = thisTransition.getStartState().getStateID() + thisTransition.getAction() + thisTransition.getEndState().getStateID();
			if (transHash.containsKey(transKey)){
				transHash.get(transKey).incrementOccurence();
				String sql = "UPDATE Event SET StateTransitionID="+transHash.get(transKey).getStateTransitionID()+" WHERE EventID="+eventID;
				sqlList.add(sql);
			}
			else {
				thisTransition.setStateTransitionID(stateTransitionID);
				transHash.put(transKey, thisTransition);
				String sql = "UPDATE Event SET StateTransitionID="+stateTransitionID+" WHERE EventID="+eventID;
				stateTransitionID++;
				sqlList.add(sql);
				newTrans.add(thisTransition);
			}
			startState = endState;
			prevAction = action;
		}
		Dbhandler.insertStateTrans(newTrans);
		TimeUnit.SECONDS.sleep(1);
		Dbhandler.updateEventStateID(sqlList);
		System.out.println("Updated game with ID " +gameid);
	}
	private static Hashtable<String, StateTransition> getStateTransHashFromRS(ResultSet rs) throws SQLException {
		//rs is a resultset consisting of StateTransitions
		Hashtable<String, StateTransition> transHash = new Hashtable<String, StateTransition>();
		while (rs.next()) {
			int stateTransitionID;
			State startState = new State (rs.getInt("StartID"));
			State endState = new State(rs.getInt("EndID"));
			String action = rs.getString("Action");
			StateTransition thisTransition = new StateTransition(startState, endState, action);
			thisTransition.setStateTransitionID(rs.getInt("TransitionID"));
			String key = thisTransition.getStartState().getStateID() + thisTransition.getAction() + thisTransition.getEndState().getStateID();
			transHash.put(key, thisTransition);
		}
		
		return transHash;
	}
	public static ArrayList<State> getStateListFromRS(ResultSet staters) throws SQLException{
		ArrayList<State> statelist = new ArrayList<State>();
		while (staters.next()) {
			int stateID = staters.getInt("StateID");
			int zone = staters.getInt("Zone");
			String team = staters.getString("Team");
			int period = staters.getInt("Period");
			int matchStatus = staters.getInt("MatchStatus");
			int occurrence = staters.getInt("Occurrence");
			int reward = staters.getInt("Reward");
			statelist.add(new State(stateID, zone, team, period, matchStatus, reward));
		}
		return statelist;
	}
	
}
