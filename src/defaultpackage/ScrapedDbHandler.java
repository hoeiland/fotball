package defaultpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ScrapedDbHandler {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/football_analytics_scraped?autoReconnect=true&useSSL=false";
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
	public static ResultSet getScrapedEvents (int gameID) throws ClassNotFoundException, SQLException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql="";
		sql = "SELECT t.team_id, e.* FROM match_events as e "
				+ "inner join players as p on e.player_id=p.player_id "
				+ "inner join match_event_types as type on e.type_id=type.event_type_id "
				+ "inner join match_players as mp on e.player_id=mp.player_id AND mp.match_id=e.match_id "
				+ "inner join teams as t on mp.team_id=t.team_id "
				+ "where e.match_id = " + gameID + " order by match_event_id";
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static ResultSet getTeams(int gameid) throws SQLException, ClassNotFoundException {
		openConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT mt.*, m.season_id FROM match_teams as mt inner join matches as m on m.match_id = mt.match_id WHERE mt.match_id = " + gameid;
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

}
