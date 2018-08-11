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
	
	public static ResultSet getMarkovEventsFromGame(int gameid) throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.EventID, E.Action, E.Outcome, E.Minute, E.Period, E.GoalDifference, E.TeamID, E.Xstart, E.Ystart, E.Xend, E.Yend, E.Number, "
				+ "G.HomeID, G.AwayID FROM Event AS E INNER JOIN Game AS G ON E.GameID = G.GameID WHERE E.GameID =" + gameid +" AND E.Action != 'Ball received' AND E.Action != 'Fouled' AND !(Action = 'Take on' AND Outcome = 0) "
				+ "AND !(Action = 'Aerial duel' AND Outcome=0) ORDER BY E.Number;";
		System.out.println(query);
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
	
	public static void insertStateTrans(ArrayList<StateTransition> transitionArray) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		for (StateTransition st : transitionArray){
			sql = "INSERT INTO StateTransition (TransitionID, StartID, EndID, Action, Occurrence) VALUES (" + st.getStateTransitionID() + "," +st.getStartState().getStateID() +"," + st.getEndState().getStateID() + "," + "'" + st.getAction()
			+ "'" + "," + st.getOccurrence() + ");\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
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

	public static ResultSet getDatabaseStateAction() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM StateAction";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static ResultSet getDatabaseStates() throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT* FROM State";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	public static void updateStateActionQ(Hashtable<String, StateAction> saList) throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "";
		Set<String> keys = saList.keySet();

		for(String key: keys ){
			int stateID = Integer.parseInt(key.replaceAll("[^\\d.]", ""));
			String action = key.replaceAll(Integer.toString(stateID), "");
			double qValue = saList.get(key).getValue();
			sql = "UPDATE StateAction SET Value = "+ qValue + " WHERE StateID = " + stateID + " AND Action = '" + action+ "';\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		System.out.println("StateActions Qvalues updated ");
		closeConnection();
		
	}

	public static void updateStateValues(Hashtable<Integer, State> stateList) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;
		Set<Integer> keys = stateList.keySet();
		for(Integer stateID : keys){
			State state = stateList.get(stateID);
			double stateValue = state.getValue();
			sql = "UPDATE State SET Value=" + stateValue +" WHERE StateID = "+stateID+";\n";
			stmt.addBatch(sql);

		}
		int [] updateCounts = stmt.executeBatch();
		System.out.println("StateValues updated on State");
		closeConnection();
		
	}

	public static void insertPlayer(String sqlString) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		stmt.execute(sqlString);
		closeConnection();
	}

	public static void updatePlayerTeam(String sql) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		System.out.println(sql);
		stmt.execute(sql);
		closeConnection();
	}
	
	public static ResultSet getEventsAndValues() throws ClassNotFoundException, SQLException{
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.Number, E.EventID, E.Action, E.TeamID, E.PlayerID, E.GameID, E.StateTransitionID, "
				+ "ST.TransitionID, ST.StartID, ST.EndID, SA.StateID, SA.Action, SA.Value AS QValue, StartS.Value AS StartValue, EndS.Value AS EndValue, EndS.Reward AS Endreward, G.HomeID, G.AwayID "
				+ "FROM `Event` AS E "
				+ "INNER JOIN StateTransition AS ST ON E.StateTransitionID=ST.TransitionID "
				+ "INNER JOIN State AS StartS ON ST.StartID=StartS.StateID "
				+ "INNER JOIN StateAction AS SA ON ST.StartID=SA.StateID "
				+ "INNER JOIN State AS EndS	ON ST.EndID=EndS.StateID "
				+ "INNER JOIN Game AS G	ON E.GameID=G.GameID WHERE SA.Action = E.Action "
				+ "ORDER BY E.EventID ASC";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	public static void insertPlayerImpact(ArrayList<PlayerImpact> playerValues) throws SQLException, ClassNotFoundException{
		openConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0 ; i < playerValues.size() ; i++){
			PlayerImpact pi = playerValues.get(i);
			String sql = "INSERT INTO playergameimpact"+" VALUES ("+pi.getPlayerID()+"," + pi.getGameID() + "," + pi.getTeamID() + "," +pi.getTotal() +","+pi.getPass() +","+ pi.getLongPass() + "," + pi.getBallCarry() + "," + pi.getBallRecovery() + "," + pi.getBallReceived() +
						"," + pi.getAerialDuel() + "," + pi.getClearance() + "," + pi.getThrowInTaken() + "," + pi.getBallTouch() + "," + pi.getInterception() + "," + pi.getBlockedShot() + "," + pi.getSavedShot() + "," + pi.getCross()
						+ "," + pi.getTackle() + "," + pi.getShot() + "," + pi.getHeadedShot() + "," + pi.getTakeOn() + "," + pi.getFreekickPass() + "," + pi.getFoulCommitted() + "," + pi.getFouled()
						+"," +pi.getDispossessed() + "," + pi.getCornerTaken()+");\n";
			stmt.addBatch(sql);
			System.out.println(sql);
			
		}
		int[] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertPlayerGameTime(Hashtable<Integer, Hashtable<Integer, PlayerGameTime>> teamGameTimeTable) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql;

		Set<Integer> teamKeys = teamGameTimeTable.keySet();

		for(int teamID : teamKeys){
			Hashtable<Integer, PlayerGameTime> playerGameTime = teamGameTimeTable.get(teamID);
			Set<Integer> playerKeys = playerGameTime.keySet();
			for (int playerID : playerKeys){
				int time14 = playerGameTime.get(playerID).getSeason2014();
				int time15 = playerGameTime.get(playerID).getSeason2015();
				int time16 = playerGameTime.get(playerID).getSeason2016();
				int time17 = playerGameTime.get(playerID).getSeason2017();
				int total = time14+time15+time16+time17;
				sql = "INSERT INTO PlayerGameTime VALUES ("+ playerID +"," + teamID + "," + time14 + "," + time15 + ","+ time16 + "," + time17 + "," + total +");\n";
				stmt.addBatch(sql);
			}
		}
		int [] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static void insertDefImpacts(ArrayList<DefImpact> playerValues) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0 ; i < playerValues.size() ; i++){
			DefImpact di = playerValues.get(i);
			String sql = "INSERT INTO playergamedefimpact VALUES ("+ di.getPlayerID()+","+di.getGameID()+","+ di.getTeamID() +","+di.getTackle()+","+di.getInterception()+","+di.getBallTouch()+","+di.getBlockedShot()+","+di.getClearance()+","+di.getBallRecovery()+");\n";
			stmt.addBatch(sql);
		}
		int[] updateCounts = stmt.executeBatch();
		closeConnection();
	}

	public static int getMaxTransitionID() throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT Max(TransitionID) FROM StateTransition";
		ResultSet rs = stmt.executeQuery(sql);
		int max = 0;
		while(rs.next()) {
			max = rs.getInt("Max(TransitionID)");
		}
		return max;
	}

	public static ResultSet getEventsAndValuesFromGame(int gameid) throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT E.Number, E.EventID, E.Action, E.TeamID, E.PlayerID, E.GameID, E.StateTransitionID, "
				+ "ST.TransitionID, ST.StartID, ST.EndID, SA.StateID, SA.Action, SA.Value AS QValue, StartS.Value AS StartValue, EndS.Value AS EndValue, EndS.Reward AS Endreward, G.HomeID, G.AwayID "
				+ "FROM `Event` AS E "
				+ "INNER JOIN StateTransition AS ST ON E.StateTransitionID=ST.TransitionID "
				+ "INNER JOIN State AS StartS ON ST.StartID=StartS.StateID "
				+ "INNER JOIN StateAction AS SA ON ST.StartID=SA.StateID "
				+ "INNER JOIN State AS EndS	ON ST.EndID=EndS.StateID "
				+ "INNER JOIN Game AS G	ON E.GameID=G.GameID WHERE E.GameID = "+ gameid + " AND SA.Action = E.Action "
				+ "ORDER BY E.EventID ASC";
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
}
