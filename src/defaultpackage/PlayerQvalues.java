package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import objects.PlayerImpact;

public class PlayerQvalues {

	public static void buildPlayerImpactFromGame(int gameid) throws ClassNotFoundException, SQLException{

		ResultSet events = Dbhandler.getEventsAndValuesFromGame(gameid);

		
		Hashtable<Integer, PlayerImpact> gameValues = new Hashtable<Integer, PlayerImpact>(); //one aggregated value per player per game
		Hashtable<Integer,Hashtable<Integer, PlayerImpact>> playerValues = new Hashtable<Integer, Hashtable<Integer, PlayerImpact>>(); //key is a gameid pointing to a hashtable of gameValues (line above)
		ArrayList<PlayerImpact> playerValueList= new ArrayList<PlayerImpact>();  //komplett liste med playerImpact objekter for alle kamper og spillere som sendes til db
		int prevGameID = 0;
		int c = 0;

		while(events.next()){
			int teamID = events.getInt("TeamID");
			int gameID = events.getInt("GameID");
			int playerID = events.getInt("PlayerID");
			int homeID =  events.getInt("HomeID");

			double qValue = events.getDouble("QValue");
			double startStateVal = events.getDouble("StartValue");
			String action = events.getString("E.Action");

			if(gameID != prevGameID){
				System.out.println("Begynner med gameID: " + gameID);
				c++;
				gameValues = new Hashtable<Integer, PlayerImpact>();
				playerValues.put(gameID, gameValues);
			}

			Double value=0.0;
			if (teamID == homeID){ //hjemmelags event
				value = qValue;
			}
			else { //bortelags event
				value = (-qValue);
			}

			if(gameValues.containsKey(playerID)){
				PlayerImpact pi = gameValues.get(playerID);
				pi.updateValue(action, value);
				gameValues.put(playerID, pi);
			}
			else {
				PlayerImpact pi = new PlayerImpact(playerID, gameID,teamID);
				pi.updateValue(action, value);
				gameValues.put(playerID, pi);
			}
			prevGameID = gameID;

		}
		Set<Integer> gameIDs = playerValues.keySet();
		for (Integer gID: gameIDs){
			Hashtable<Integer, PlayerImpact> playervals = playerValues.get(gID);
			Set<Integer> playerIDs = playervals.keySet();
			for (Integer pID: playerIDs){
				playervals.get(pID).setTotal();
				playerValueList.add(playervals.get(pID));
			}
		}

		ScrapedDbHandler.insertPlayerImpact(playerValueList);
	}

}
