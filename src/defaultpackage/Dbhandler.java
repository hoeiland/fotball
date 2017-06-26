package defaultpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import objects.*;
import objects.markov.*;

public class Dbhandler {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/Football?autoReconnect=true&useSSL=false";
	static final String USER = "root";
	static final String PASS = "thesaints";
	static Connection conn = null;
	
	public static void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		System.out.println("Connected database successfully...");
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	public static void closeConnection() throws SQLException{
		conn.close();
		System.out.println("Connection closed...");
	}

	public static void insertEvents(ArrayList<ArrayList<Event>> gameList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql="";
		for (ArrayList<Event> eventList : gameList){
			for(Event e : eventList){
				sql="INSERT INTO Event (Action,Outcome,TeamID,PlayerID,GameID,XStart,YStart,Xend,Yend,Number,Sequence,Minute,Second,"
						+"GoalDifference,Period,OptaEventID)"+"\n"+"VALUES "+"('"+e.getAction_type()+"',"+e.getOutcome()+","+e.getTeam_id()+","+e.getPlayer_id()+","
						+e.getGame_id()+","+e.getXstart()+","+e.getYstart()+","+e.getXend()+","+e.getYend()+","+e.getNumber()+","+e.getSequence()+","+e.getMinute()+","+e.getSecond()+","
						+e.getGoaldifference()+","+e.getPeriod()+","+e.getEvent_id()+")"+";\n";
				stmt.addBatch(sql);
			}
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}
	public static ResultSet getMarkovEvents() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.EventID, E.Action, E.Outcome, E.Minute, E.Period, E.GoalDifference, E.TeamID, E.Xstart, E.Ystart, E.Xend, E.Yend, E.Number, "
				+ "G.HomeID, G.AwayID FROM Event AS E INNER JOIN Game AS G ON E.GameID = G.GameID WHERE E.Action != 'Ball received' AND E.Action != 'Fouled' AND !(Action = 'Take on' AND Outcome = 0) "
				+ "AND !(Action = 'Aerial duel' AND Outcome=0);";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	public static void updateEventStateID(ArrayList<String> sqlList) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		for(String s : sqlList){
			stmt.addBatch(s);
		}
		int [] updateCounts = stmt.executeBatch();
	}
	
	public static void insertStatesAndTrans(ArrayList<State> stateList, ArrayList<StateTransition> transitionArray) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		for (State s : stateList){
			sql = "INSERT INTO State VALUES (" + s.getStateID() + "," + s.getZone() + "," + "'"+s.getTeam()+"'" + "," + s.getPeriod() + "," + s.getMatchStatus() + ","
					+ s.getOccurrence() + "," + s.getValue() + ","+s.getReward() + ");\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("States inserted");
		for (StateTransition st : transitionArray){
			sql = "INSERT INTO StateTransition (TransitionID, StartID, EndID, Action, Occurrence) VALUES (" + st.getStateTransitionID() + "," +st.getStartState().getStateID() +"," + st.getEndState().getStateID() + "," + "'" + st.getAction()
			+ "'" + "," + st.getOccurrence() + ");\n";
			stmt.addBatch(sql);
		}
		updateCounts = stmt.executeBatch();
		System.out.println("StateTransitions inserted");
		closeConnection();
	}
	
	public static ResultSet getDatabaseStateTrans() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM StateTransition";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static void insertStateAction(Hashtable<Integer, Hashtable<String, Integer>> stateActions) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		Set<Integer> stateIDs = stateActions.keySet();
		for (int stateID : stateIDs){
			Set<String> actions = stateActions.get(stateID).keySet();
			for(String action : actions){
				int occurrence = stateActions.get(stateID).get(action);
				sql = "INSERT INTO StateAction (StateID, Action, Occurrence, Value) VALUES ( "+ stateID + ",'"+ action + "'," + occurrence + "," + 0 +");\n";
				stmt.addBatch(sql);
			}
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("StateActions inserted");
		closeConnection();
	}

	public static void insertGames(ArrayList<Game> gameList) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		for(Game g : gameList){

			sql = "INSERT INTO Game (GameID, HomeID, AwayID, Matchday, SeasonID, Score) VALUES ("+g.getGame_id()+","+g.getHome_team_id()+","+g.getAway_team_id() + "," + g.getMatchday() + "," + g.getSeason() + ","+ g.getScore() + ");\n";
			System.out.println(sql);
			stmt.addBatch(sql);
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
		
	}
}
