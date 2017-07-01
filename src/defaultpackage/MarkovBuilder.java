package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;


import objects.markov.State;
import objects.markov.StateTransition;

public class MarkovBuilder {

	public static void buildFromEvents() throws ClassNotFoundException, SQLException{
		ResultSet eventSet = Dbhandler.getMarkovEvents();
		ArrayList<State> stateList = new ArrayList<State>();
		ArrayList<StateTransition> transList = new ArrayList<StateTransition>();
		Hashtable<String, StateTransition> transHash = new Hashtable<String, StateTransition>();
		ArrayList<String> sqlList = new ArrayList<String>();
		State startState = null;
		State endState = null;
		boolean prevEndOfPeriod = false;
		String prevAction = "";
		int stateID = 1;
		int stateTransitionID = 1;

		int stateincCount = 0;
		int eventCount = 0;
		while(eventSet.next()){
			String action = eventSet.getString("Action");
			int outcome = eventSet.getInt("Outcome");
			int minute = eventSet.getInt("Minute");
			int period = eventSet.getInt("Period");
			int goalDifference = eventSet.getInt("GoalDifference");
			int teamID = eventSet.getInt("TeamID");
			int homeID = eventSet.getInt("HomeID");
			int awayID = eventSet.getInt("AwayID");
			float xStart = eventSet.getFloat("Xstart");
			float yStart = eventSet.getFloat("Ystart");
			float xEnd = eventSet.getFloat("Xend");
			float yEnd = eventSet.getFloat("Yend");
			int eventNumber = eventSet.getInt("Number");
			int eventID = eventSet.getInt("EventID");

			int startZone = getZoneFromCoordinates(xStart, yStart);
			int endZone = getZoneFromCoordinates(xEnd, yEnd);
			int statePeriod = getPeriod(minute, period);

			int reward = getReward(action, teamID==homeID);
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
			int matchStatus = getMatchStatus(goalDifference);
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
				if (prevEndOfPeriod) prevEndOfPeriod = false;
				if (stateList.size() == 0){ //hvis statelist tom -> legg til ny state
					startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
					stateList.add(startState);
					stateID++;
				}
				else { //hvis statelist ikke tom
					boolean startStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == startZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
								&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
								&& s.getReward() == 0){
							s.incrementOccurrence();
							startState = s;
							startStateExists = true;
							stateincCount++;
							break;
						}
					}
					if (!startStateExists){
						startState = new State(stateID, startZone, team, statePeriod, matchStatus, 0);
						stateList.add(startState);
						stateID++;
					}
				}
			}
			eventSet.next();
			String nextAction = eventSet.getString("Action");
			int nextZone = getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
			int nextTeamID = eventSet.getInt("TeamID");
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
			int nextMatchStatus = getMatchStatus(goalDifference);
			int nextReward = getReward(nextAction, nextTeamID==homeID);
			eventSet.previous();

			if(nextAction.equals("Goalkeeper")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, 0, "None", 0, 0, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}

			else if (action.equals("Pass") || action.equals("Long pass") || action.equals("Cross") || action.equals("Free kick pass") || action.equals("Throw in taken") || action.equals("Corner taken")){
				if (!nextAction.equals("Aerial duel")){
					if (outcome == 1){ // hvis outcome er 1 -> start og end state har samme lag
						//lager end state
						boolean endStateExists = false;
						for (int i = 0; i < stateList.size(); i++){
							State s = stateList.get(i);
							if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
									&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
									&& s.getReward() == reward){
								s.incrementOccurrence();
								stateincCount++;
								endState = s;
								endStateExists = true;
								break;
							}
						}
						if (!endStateExists){
							endState = new State(stateID, endZone, team, statePeriod, matchStatus, reward);
							stateList.add(endState);
							stateID++;
						}
					}
					else {//outcome ==0 -> start state hos team som slo pasning, end state har ikke noe team
						if (nextAction.equals("Out of play")){
							eventSet.next();
							eventSet.next();
							nextZone = getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
							eventSet.previous();
							eventSet.previous();
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;
									break;
								}
							}
							if (!endStateExists){
								endState = new State(stateID, nextZone, otherTeam, period, matchStatus, reward);
								stateList.add(endState);
								stateID++;
							}
						}
						else if (nextAction.equals("Shot") || nextAction.equals("Headed shot") || nextAction.equals("Pass") || nextAction.equals("Ball carry") || nextAction.equals("Take on")
								|| nextAction.equals("Long pass") || nextAction.equals("Cross")){
							//if (eventID== 6472650) System.out.println(eventID + " " + "123" +nextAction) ;
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;
									break;
								}
							}
							if (!endStateExists){
								endState = new State(stateID, nextZone, nextTeam, period, nextMatchStatus, reward);
								stateList.add(endState);
								stateID++;
							}
						}
						else {

							//								if (eventID == 6472637 || eventID == 6472650) System.out.println(eventID + " " + stateList.size());
							boolean endStateExists = false;
							for (int i = 0; i < stateList.size(); i++){
								State s = stateList.get(i);
								if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
										&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
										&& s.getReward() == reward){
									s.incrementOccurrence();
									stateincCount++;
									endState = s;
									endStateExists = true;

									break;
								}
							}
							if (!endStateExists){
								if (eventID == 6472650) System.out.println(6472650);
								endState = new State(stateID, nextZone, "None", period, getMatchStatus(goalDifference), reward);
								stateList.add(endState);
								stateID++;
							}

						}
					}
				}
				else { //nextAction er hodeduell
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == endZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
								&& s.getReward() == reward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, endZone, "None", period, getMatchStatus(goalDifference), reward);
						stateList.add(endState);
						stateID++;
					}
				}
			}
			else if (nextAction.equals("Ball recovery")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == getMatchStatus(goalDifference)
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, nextZone, "None", statePeriod, getMatchStatus(goalDifference), reward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (action.equals("Ball carry") || action.equals("Aerial duel") || action.equals("Ball recovery")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == endZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(team) && s.getMatchStatus() == matchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, endZone, team, statePeriod, matchStatus, reward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (action.equals("Foul committed")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == nextZone && s.getPeriod() == statePeriod //hvis state finnes fra f�r
							&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == nextMatchStatus
							&& s.getReward() == reward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, nextZone, otherTeam, statePeriod, nextMatchStatus, reward);
					stateList.add(endState);
					stateID++;
				}
			}

			else if(nextAction.equals("Goal")){
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, 0, nextTeam, 0, 0, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}
			else if (nextAction.equals("End of period")){ //neste event er ikke m�l
				prevEndOfPeriod = true;
				boolean endStateExists = false;
				for (int i = 0; i < stateList.size(); i++){
					State s = stateList.get(i);
					if(s.getZone() == 0 && s.getPeriod() == 0 //hvis state finnes fra f�r
							&& s.getTeam().equals("None") && s.getMatchStatus() == 0
							&& s.getReward() == nextReward){
						s.incrementOccurrence();
						stateincCount++;
						endState = s;
						endStateExists = true;
						break;
					}
				}
				if (!endStateExists){
					endState = new State(stateID, 0, "None", 0, 0, nextReward);
					stateList.add(endState);
					stateID++;
				}
			}
			else { //action er en av de som ikke er sjekket eksplisitt (i.e. tackle, interception etc)

				if (nextAction.equals("Out of play")){ //neste er out of play
					eventSet.next();
					eventSet.next();
					nextZone = getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
					eventSet.previous();
					eventSet.previous();
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(otherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, otherTeam, period, matchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}
				else if (nextAction.equals("Foul committed")){
					eventSet.next();
					eventSet.next();
					nextZone = getZoneFromCoordinates(eventSet.getFloat("Xstart"), eventSet.getFloat("Ystart"));
					eventSet.previous();
					eventSet.previous();
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextOtherTeam) && s.getMatchStatus() == matchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, nextOtherTeam, period, matchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}
				else {
					boolean endStateExists = false;
					for (int i = 0; i < stateList.size(); i++){
						State s = stateList.get(i);
						if(s.getZone() == nextZone && s.getPeriod() == period //hvis state finnes fra f�r
								&& s.getTeam().equals(nextTeam) && s.getMatchStatus() == nextMatchStatus
								&& s.getReward() == nextReward){
							s.incrementOccurrence();
							stateincCount++;
							endState = s;
							endStateExists = true;
							break;
						}
					}
					if (!endStateExists){
						endState = new State(stateID, nextZone, nextTeam, period, nextMatchStatus, nextReward);
						stateList.add(endState);
						stateID++;
					}
				}

			}

			//lager eller oppdaterer transitions
			StateTransition thisTransition = new StateTransition(startState, endState, action);
			boolean transitionExists = false;
			if (transHash.isEmpty()){
				thisTransition.setStateTransitionID(stateTransitionID);
				String key = thisTransition.getStartState().getStateID() + thisTransition.getAction() + thisTransition.getEndState().getStateID();
				transHash.put(key, thisTransition);
				String sql = "UPDATE Event SET StateTransitionID="+stateTransitionID+" WHERE EventID="+eventID;
				stateTransitionID++;
				sqlList.add(sql);
			}
			else {
				String transKey = thisTransition.getStartState().getStateID() + thisTransition.getAction() + thisTransition.getEndState().getStateID();
				if (transHash.containsKey(transKey)){
					transHash.get(transKey).incrementOccurence();
					transitionExists = true;
					String sql = "UPDATE Event SET StateTransitionID="+transHash.get(transKey).getStateTransitionID()+" WHERE EventID="+eventID;
					sqlList.add(sql);
				}
				else {
					thisTransition.setStateTransitionID(stateTransitionID);
					transHash.put(transKey, thisTransition);
					String sql = "UPDATE Event SET StateTransitionID="+stateTransitionID+" WHERE EventID="+eventID;
					stateTransitionID++;
					sqlList.add(sql);
				}
			}
			eventCount++;
			if (eventCount%1000 == 0) System.out.println(eventCount + " eventer er behandlet.");
			startState = endState;
			prevAction = action;
		}
		System.out.println(stateincCount);
		Set<String> transKeys = transHash.keySet();
		for (String key: transKeys){
			transList.add(transHash.get(key));
		}
		Dbhandler.updateEventStateID(sqlList);
		Dbhandler.insertStatesAndTrans(stateList, transList);

	}
	private static int getMatchStatus(int goaldifference){
		if (goaldifference>0) return 1;
		else if (goaldifference<0) return -1;
		else return 0;
	}

	public static void setStateAction() throws ClassNotFoundException, SQLException{
		ResultSet stateTransSet = Dbhandler.getDatabaseStateTrans();
		Hashtable<Integer, Hashtable<String, Integer>> stateAction = new Hashtable<Integer, Hashtable<String, Integer>>();
		while (stateTransSet.next()){
			int stateID = stateTransSet.getInt("StartID");
			String action = stateTransSet.getString("Action");
			int occurrence = stateTransSet.getInt("Occurrence");
			if(stateAction.containsKey(stateID)){
				Hashtable<String, Integer> actionOccurrence = stateAction.get(stateID);
				if (actionOccurrence.containsKey(action)){
					int prevOcc = actionOccurrence.get(action);
					actionOccurrence.put(action, prevOcc+occurrence);
					stateAction.put(stateID, actionOccurrence);
				}
				else{
					actionOccurrence.put(action, occurrence);
					stateAction.put(stateID, actionOccurrence);

				}
			}
			else{
				Hashtable<String, Integer> actionOcc = new Hashtable<String, Integer>();
				actionOcc.put(action, occurrence);
				stateAction.put(stateID, actionOcc);
			}
		}
		Dbhandler.insertStateAction(stateAction);
	}


	
	public static int getZoneFromCoordinates(float x, float y){
		if (x < 50){
			if (x < 50/3){
				if (y < 100.0/3){
					return 3;
				}
				else if (y < 200.0/3){
					return 2;
				}
				else{
					return 1;
				}
			}
			else if (x < 100.0/3){
				if (y < 100.0/3){
					return 6;
				}
				else if (y < 200.0/3){
					return 5;
				}
				else{
					return 4;
				}
			}
			else{
				if (y < 100.0/3){
					return 9;
				}
				else if (y < 200.0/3){
					return 8;
				}
				else{
					return 7;
				}
			}
		}
		else { //x>=50
			if (x < 4.0/6*100){
				if (y < 100/4){
					return 13;
				}
				else if (y < 200.0/4){
					return 12;
				}
				else if (y < 300.0/4){
					return 11;
				}
				else{
					return 10;
				}
			}
			else if ( x < 5.0/6*100){

				if (y < 100.0/4){
					return 17;
				}
				else if (y < 200.0/4){
					return 16;
				}
				else if (y < 300.0/4){
					return 15;
				}
				else{
					return 14;
				}
			}
			else{
				if (y < 100.0/4){
					return 21;
				}
				else if (y < 200.0/4){
					return 20;
				}
				else if (y < 300.0/4){
					return 19;
				}
				else{
					return 18;
				}
			}
		}
	}

	public static int getPeriod(int minutes, int period){
		if (minutes < 23){
			return 1;
		}
		else if (period == 1){
			return 2;
		}
		else if(minutes < 68){
			return 3;
		}
		else return 4;
	}

	public static int getReward(String action, boolean home){
		if (action.equals("Goal")){
			if(home) return 1;
			else return -1;
		}
		else return 0;
	}
	
	
}
