package defaultpackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import objects.DefImpact;
import objects.PlayerImpact;

public class DefImpactReader {
	public static void buildDefImpact() throws ClassNotFoundException, SQLException {	
		ResultSet events = Dbhandler.getEventsAndValues();
		
		Hashtable<Integer, DefImpact> gameValues = new Hashtable<Integer, DefImpact>(); //one aggregated value per player per game
		Hashtable<Integer,Hashtable<Integer, DefImpact>> playerValues = new Hashtable<Integer, Hashtable<Integer, DefImpact>>(); //key is a gameid pointing to a hashtable of gameValues (line above)
		ArrayList<DefImpact> playerValueList= new ArrayList<DefImpact>();  //komplett liste med playerImpact objekter for alle kamper og spillere som sendes til db
		int prevGameID = 0;
		int c = 0;
		
		while (events.next()) {
			int teamID = events.getInt("TeamID");
			int gameID = events.getInt("GameID");
			int playerID = events.getInt("PlayerID");
			int homeID =  events.getInt("HomeID");
			int awayID = events.getInt("AwayID");
			int endStateID = events.getInt("EndID");

			double qValue = events.getDouble("QValue");
			double endStateVal =  events.getDouble("EndValue");
			double endStateReward = events.getDouble("Endreward");
			double startStateVal = events.getDouble("StartValue");
			String action = events.getString("E.Action");
			double prevStartVal = 0;
			
			if(gameID != prevGameID){
				System.out.println("Begynner med gameID: " + gameID);
				c++;
				gameValues = new Hashtable<Integer, DefImpact>();
				playerValues.put(gameID, gameValues);
				prevStartVal = 0;
			}

			Double value=0.0;
			if (action.equals("Tackle") || action.equals("Interception") || action.equals("Ball touch") || action.equals("Blocked shot") || action.equals("Ball recovery") || action.equals("Clearance")) {
				if (teamID == homeID){ //hjemmelags event
					value = (endStateVal-startStateVal);
				}
				else { //bortelags event
					value = -(endStateVal-startStateVal);
				}	
				if(gameValues.containsKey(playerID)){
					DefImpact di = gameValues.get(playerID);
					di.updateDefImpact(action, value);
					gameValues.put(playerID, di);
				}
				else {
					DefImpact di = new DefImpact(playerID, gameID,teamID);
					di.updateDefImpact(action, value);
					gameValues.put(playerID, di);
				} 
			}
			prevStartVal = startStateVal;
			prevGameID = gameID;
		}
		Set<Integer> gameIDs = playerValues.keySet();
		for (Integer gID: gameIDs){
			Hashtable<Integer, DefImpact> playervals = playerValues.get(gID);
			Set<Integer> playerIDs = playervals.keySet();
			for (Integer pID: playerIDs){
				playerValueList.add(playervals.get(pID));
//				System.out.println(playervals.get(pID)); //Testing purposes
			}
		}
		Dbhandler.insertDefImpacts(playerValueList);
	}
}
