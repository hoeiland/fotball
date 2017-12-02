package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objects.Event;
import objects.Game;

public class convertScraped {
	
	public static void insertEventlistsFromGames() throws ClassNotFoundException, SQLException {
		ArrayList<ArrayList<Event>> List = new ArrayList<ArrayList<Event>>();
		for (int i = 28560;i<28928;i++) {
			ArrayList<Event> events= convertGame(i);
			List.add(events);
			System.out.println(i);
		}
		Dbhandler.insertEvents(List);
	}
	
	
	
	public static ArrayList<Event> convertGame(int gameid) throws ClassNotFoundException, SQLException{
		ResultSet rs = ScrapedDbHandler.getScrapedEvents(gameid);
		ResultSet teams = ScrapedDbHandler.getTeams(gameid);
		Game g = getGame(teams);
		ArrayList<Event> eventlist = new ArrayList<Event>();
		int gd = 0; //goal difference
		String eventid;
		int sequence=1; //sequence nummer internt i en game
		int number = 0;
		while (rs.next()) {
			int typeid = rs.getInt("type_id");
			int headed = rs.getInt("headed");
			int longball = rs.getInt("longball");
			int throwin = rs.getInt("throw_in");
			int fk = rs.getInt("freekick");
			int owngoal = rs.getInt("own_goal");
			String outcomeString = rs.getString("outcome");
			int outcome =  rs.getInt("outcome");
			if (outcomeString == null) outcome = 1;

			float x = rs.getFloat("x");
			float y = rs.getFloat("y");
			float xend = rs.getFloat("end_x");
			float yend = rs.getFloat("end_y");
			String type = convertIdToType(typeid, headed, longball, throwin, fk, owngoal, outcome, xend);
			int teamid = rs.getInt("team_id");
			int playerid = rs.getInt("player_id");
			int min = rs.getInt("minsec")/60;
			int sec = rs.getInt("minsec")%60;
			int period = rs.getInt("period");
			if (type.equals("skip")) continue;
			if (eventlist.size()!=0) { //sjekk for å kunne legge inn endOfPeriod event
				if (eventlist.get(eventlist.size()-1).getPeriod()==1 && period == 2) {
					number++;
					eventid = gameid + "" + number;
					eventlist.add(new Event(Long.parseLong(eventid), "End of period", 1, 0, 0, 50, 50, 50, 50, number, sequence, gameid, 1, min, sec, gd));
					sequence ++;
				}
			}

			if (type.equals("Goalkeeper")){
				number++;
				eventid = gameid + "" + number;
				eventlist.add(new Event(Long.parseLong(eventid),type,1,teamid,playerid,x,y,x,y,number,sequence,gameid,period,min,sec,gd));
				sequence ++;
				continue;
			}
			if (type.equals("Aerial duel") && outcome==1){ //laget som vinner hodeduellen kommer alltid fï¿½rst i eventlisten
				Event prevEvent = eventlist.get(eventlist.size()-1);
				if (prevEvent.getAction_type()=="Aerial duel"){
					eventlist.remove(eventlist.get(eventlist.size()-1));
					eventid = Long.toString(prevEvent.getEvent_id());
					eventlist.add(new Event(Long.parseLong(eventid), type, outcome, teamid, playerid, x, y, xend, yend, number, sequence, gameid, period, min, sec, gd));
					number++;
					eventid = gameid + "" + number;
					prevEvent.setNumber(number); 
					prevEvent.setEvent_id(Long.parseLong(eventid));
					eventlist.add(prevEvent);
					continue;
				}
				else {
					number ++ ;
					eventid = gameid + "" + number;
					eventlist.add(new Event(Long.parseLong(eventid), type, outcome, teamid, playerid, x, y, xend, yend, number, sequence, gameid, period, min, sec, gd));
					continue;
				}
			}
			if (!(type.equals("Goal") || type.equals("owngoal"))){
				number++;
				eventid = gameid + "" + number;
				eventlist.add(new Event(Long.parseLong(eventid), type, outcome, teamid, playerid, x, y, xend, yend, number, sequence, gameid, period, min, sec, gd));
			}
			else {
				if (type.equals("owngoal")) {
					number++;
					eventid = gameid + "" + number;
					eventlist.add(new Event(Long.parseLong(eventid), "Ball touch", 0, teamid, playerid, x, y, xend, yend, number, sequence, gameid, period, min, sec, gd));
					number++;
					eventid = gameid + "" + number;
					eventlist.add(new Event(Long.parseLong(eventid), "Goal", outcome, g.getOtherTeam(teamid), playerid, x, y, xend, yend, number, sequence, gameid, period, min, sec, gd));
					if (teamid == g.getHome_team_id()) gd --;
					else gd++;
					sequence ++;
				}
				else { //Goal
					number ++;
					eventid = gameid + "" + number;
					if (headed == 1) {
						eventlist.add(new Event(Long.parseLong(eventid), "Headed shot", outcome, teamid, playerid, x, y, x, y, number, sequence, gameid, period, min, sec, gd));						
					}
					else eventlist.add(new Event(Long.parseLong(eventid), "Shot", outcome, teamid, playerid, x, y, x, y, number, sequence, gameid, period, min, sec, gd));
					number ++;
					eventid = gameid + "" + number;
					eventlist.add(new Event(Long.parseLong(eventid), "Goal", outcome, teamid, playerid, x, y, x, y, number, sequence, gameid, period, min, sec, gd));
					if (teamid == g.getHome_team_id()) gd ++;
					else gd --;
					sequence ++;
				}
			}
		}
		number++;
		eventid = gameid + "" + number;
		Event prevEvent = eventlist.get(eventlist.size()-1);
		eventlist.add(new Event(Long.parseLong(eventid), "End of period", 1, 0, 0, 50, 50, 50, 50, number, sequence, gameid, 2, prevEvent.getMinute(), prevEvent.getSecond(), gd));
		eventlist = insertConstructedEvents(eventlist);
//		for (int i= 0; i<eventlist.size(); i++) {
//			System.out.println(eventlist.get(i));
//		}
		System.out.println(gd);
		return eventlist;
	}


	private static Game getGame(ResultSet teams) throws SQLException {
		int gameid = 0;
		int homegoals = 0;
		int awaygoals = 0;
		int homeid = 0;
		int awayid = 0;
		int season = 0;
		while (teams.next()) {
			if (teams.getString("team_venue").equals("H")) {
				homegoals = teams.getInt("team_goals_for");
				homeid = teams.getInt("team_id");
				gameid = teams.getInt("mt.match_id");
				season = teams.getInt("season_id");
			}
			else {
				awaygoals = teams.getInt("team_goals_for");
				awayid = teams.getInt("team_id");
			}
		}
		Game g = new Game(gameid, homegoals-awaygoals);
		g.setAway_team_id(awayid);
		g.setHome_team_id(homeid);
		g.setSeason(season);
		return g;
	}

	private static String convertIdToType(int typeid, int headed, int longball, int throwin, int freekick, int owngoal, int outcome, float xend) {
		String type = "";
		switch (typeid) {
		case 1: 
			if (throwin==1) type = "Throw in taken";
			else if (freekick == 1) type = "Free kick pass";
			else if (longball == 1) type = "Long pass";
			else type = "Pass";
			break;
		case 2:
			if (freekick == 1) type = "Free kick pass";
			else if (throwin == 1) type = "Throw in taken";
			else type = "Cross";
			break;
		case 3:
			type = "Corner taken";
			break;
		case 4:
			if (owngoal == 1) type = "owngoal";
			else type = "Goal";
			break;
		case 5:
			if (headed == 1) type = "Headed shot";
			else type = "Shot";
			break;
		case 6:
			if (headed == 1) type = "Headed shot";
			else type = "Shot";
			break;
		case 7: 
			if (headed == 1) type = "Headed shot";
			else type = "Shot";
			break;
		case 8:
			if (headed == 1) type = "Headed shot";
			else type = "Shot";
			break;
		case 9:
			type = "Foul";
			break;
		case 10://offside
			type = "skip";
			break;
		case 11:
			type = "Aerial duel";
			break;
		case 12:
			type = "Tackle";
			break;
		case 13: // tackled
			type = "skip";
			break;
		case 14:
			type = "Take on";
			break;
		case 15: //taken on
			type = "skip";
			break;
		case 16: //yellow card
			type = "skip";
			break;
		case 17://2nd yellow
			type = "skip";
			break;
		case 18://red card
			break;
		case 19:
			type = "Clearance";
			break;
		case 20:
			type = "Fouled";
			break;
		case 21:
			type = "Blocked shot";
			break;
		case 22:
			type = "Shot saved";
			break;
		case 23: //gkpunch
			type = "skip";
			break;
		case 24:
			type = "Clearance";
			break;
		case 25://gk failed catch
			type = "skip";
			break;
		case 26:
			type = "Interception";
			break;
		case 27:
			type = "Interception";
			break;
		case 28:
			type = "Ball touch";
			break;
		case 29:
			if (xend>100) type = "Goalkeeper";
			else type = "Out of play";
			break;
		case 30://blocked cross
			type = "skip";
			break;
		case 31:
			type = "Pass";
			break;
		case 32:
			type = "Goalkeeper";
			break;
		case 33:
			type = "Goalkeeper";
			break;
		case 34: //gkfailedclearance
			type = "skip";
			break;
		}
		return type;
	}
	private static ArrayList<Event> insertConstructedEvents(ArrayList<Event> eventlist) {
		ArrayList<Event> completeeventlist = new ArrayList<Event>();
		completeeventlist.add(eventlist.get(0));
		System.out.println(eventlist.size());
		String eventid;
		int gameid = eventlist.get(0).getGame_id();
		for (int i = 1; i < eventlist.size(); i++){ //gï¿½r gjennom eventlisten for ï¿½ legge til "kunstige" events (ball carry, ball received osv.)
			Event prevEvent = completeeventlist.get(completeeventlist.size()-1);
			Event thisEvent = eventlist.get(i);
			if (prevEvent.getOutcome() == 1){
				if (prevEvent.getAction_type().equals("Pass") || prevEvent.getAction_type().equals("Long pass") || prevEvent.getAction_type().equals("Throw in taken")
						|| prevEvent.getAction_type().equals("Cross") || prevEvent.getAction_type().equals("Free kick pass") || prevEvent.getAction_type().equals("Corner taken")){
					if (prevEvent.getTeam_id()==thisEvent.getTeam_id()){
						eventid = gameid + "" + (completeeventlist.get(completeeventlist.size()-1).getNumber()+1); 
						completeeventlist.add(new Event(Long.parseLong(eventid), "Ball received", 1, prevEvent.getTeam_id(), thisEvent.getPlayer_id(), prevEvent.getXend(),
								prevEvent.getYend(), prevEvent.getXend(), prevEvent.getYend(), completeeventlist.get(completeeventlist.size()-1).getNumber()+1, thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), thisEvent.getMinute(),
								thisEvent.getSecond(), thisEvent.getGoaldifference())); // legger til "Ball received" event etter pasninger som kom frem
						prevEvent = completeeventlist.get(completeeventlist.size()-1);
					}
					else{ //lag er ulike
						if (thisEvent.getAction_type().equals("Foul committed")){
							Event nextEvent = eventlist.get(i+1);
							try {
								int nextEventPlayerID = nextEvent.getPlayer_id();
								eventid = gameid + "" + (completeeventlist.get(completeeventlist.size()-1).getNumber()+1); 
								completeeventlist.add(new Event(Long.parseLong(eventid), "Ball received", 1, prevEvent.getTeam_id(), nextEventPlayerID, prevEvent.getXend(), prevEvent.getYend(),
										100 - thisEvent.getXstart(), 100 - thisEvent.getYstart(), completeeventlist.get(completeeventlist.size()-1).getNumber()+1,
										thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
								prevEvent = completeeventlist.get(completeeventlist.size()-1);
								//	System.out.println("Ball received hvor foul committed");
							}
							catch (NumberFormatException E){
								continue;
							}
						}
					}
				}

				if (prevEvent.getAction_type().equals("Pass") || prevEvent.getAction_type().equals("Ball received") || prevEvent.getAction_type().equals("Long pass") || prevEvent.getAction_type().equals("Ball recovery") ||
						prevEvent.getAction_type().equals("Throw in taken") || prevEvent.getAction_type().equals("Cross") || prevEvent.getAction_type().equals("Free kick pass") ||
						prevEvent.getAction_type().equals("Corner taken")){

					if (!thisEvent.getAction_type().equals("Aerial duel")){
						if (prevEvent.getTeam_id() == thisEvent.getTeam_id()){ //hvis false: neste event mest sannsynlig foul committed

							if (xmlReader.getCarryLength(prevEvent, thisEvent.getXstart(), thisEvent.getYstart(), thisEvent.getTeam_id()) > 7.5){
								//System.out.println("Ball carry true hvor prevteam == currTeam");
								eventid = gameid + "" + (completeeventlist.get(completeeventlist.size()-1).getNumber()+1); 
								completeeventlist.add(new Event(Long.parseLong(eventid), "Ball carry", 1, thisEvent.getTeam_id(), thisEvent.getPlayer_id(), prevEvent.getXend(),
										prevEvent.getYend(), thisEvent.getXstart(), thisEvent.getYstart(), completeeventlist.get(completeeventlist.size()-1).getNumber()+1,
										thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
								prevEvent = completeeventlist.get(completeeventlist.size()-1);
							}
						}
						else { // to forskjellige lag
							if (thisEvent.getAction_type().equals("Foul committed")){
								Event nextEvent = eventlist.get(i+1);
								if (xmlReader.getCarryLength(prevEvent, nextEvent.getXstart(), nextEvent.getYstart(), nextEvent.getTeam_id()) > 7.5){

									try {
										int nextEventPlayerID = nextEvent.getPlayer_id();
										eventid = gameid + "" + (completeeventlist.get(completeeventlist.size()-1).getNumber()+1); 
										completeeventlist.add(new Event(Long.parseLong(eventid), "Ball carry", 1, prevEvent.getTeam_id(), nextEventPlayerID, prevEvent.getXend(),
												prevEvent.getYend(), nextEvent.getXstart(), nextEvent.getYstart(), completeeventlist.get(completeeventlist.size()-1).getNumber()+1,
												thisEvent.getSequence(), thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
										prevEvent = completeeventlist.get(completeeventlist.size()-1);
										//	System.out.println("Ball carry hvor foul committed");
									}
									catch (NumberFormatException E){
										continue;
									}
								}
							}
							else {
								if (xmlReader.getCarryLength(prevEvent, thisEvent.getXstart(), thisEvent.getYstart(), thisEvent.getTeam_id()) > 7.5){
									eventid = gameid + "" + (completeeventlist.get(completeeventlist.size()-1).getNumber()+1); 
									completeeventlist.add(new Event(Long.parseLong(eventid), "Ball carry", 1, prevEvent.getTeam_id(), prevEvent.getPlayer_id(), prevEvent.getXend(), prevEvent.getYend(),
											100 - thisEvent.getXstart(), 100 - thisEvent.getYstart(), completeeventlist.get(completeeventlist.size()-1).getNumber()+1, thisEvent.getSequence(),
											thisEvent.getGame_id(), thisEvent.getPeriod(), prevEvent.getMinute(), prevEvent.getSecond(), thisEvent.getGoaldifference()));
									prevEvent = completeeventlist.get(completeeventlist.size()-1);
									//System.out.println("Ball carry hvor team er ulike");
								}
							}
						}
					}
				}
			}
			thisEvent.setNumber((completeeventlist.get(completeeventlist.size()-1).getNumber()+1));
			thisEvent.setEvent_id(Long.parseLong(gameid + "" + thisEvent.getNumber()));
			completeeventlist.add(thisEvent);
		}
		for (int i = 0; i< completeeventlist.size(); i++) {
			System.out.println(completeeventlist.get(i));
		}
		return completeeventlist;			
	}


}
