package defaultpackage;

import objects.PlayerGameTime;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GameTimeReader {
	public static void setPlayerGameTime() throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException{

		Hashtable<Integer, Hashtable<Integer, PlayerGameTime>> fullTeamTime = new Hashtable<Integer, Hashtable<Integer, PlayerGameTime>>();
		File folder = new File("eventfeeds");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++){ //i er en bestemt kamp
			Document doc = xmlReader.getDocument(listOfFiles[i].toString());
			Node gameNode = doc.getElementsByTagName("Game").item(0); //Game-noden er det første og eneste elementet med TagName "Game"
	        Element gameElement = (Element) gameNode;  //Node castes til Element for å kunne bruke getAttribute()
	        int season = Integer.parseInt(gameElement.getAttribute("season_id"));

	        Hashtable<Integer, Hashtable<Integer, Integer>> gameTeamTime = new Hashtable<Integer, Hashtable<Integer, Integer>>(); //inneholder de to lagenes hashtabeller gamePlayerTime
			NodeList xmlEventList = doc.getElementsByTagName("Event"); //nodelist med alle event-nodene fra XML-filen
			for (int j = 0; j < xmlEventList.getLength(); j++){ // j er en bestemt event i xml
				Element event = (Element) xmlEventList.item(j);
				int typeID = Integer.parseInt(event.getAttribute("type_id"));

				if (typeID == 34){ //ved kampstart
					int teamID = Integer.parseInt(event.getAttribute("team_id"));
					NodeList qualifiers = event.getChildNodes();
					for (int k = 0; k < qualifiers.getLength(); k++){
						Node qualifier = qualifiers.item(k);
						if (qualifier.getNodeType()== Node.ELEMENT_NODE){
							Element qElement = (Element) qualifier;
							if((Integer.parseInt(qElement.getAttribute("qualifier_id")) == 30)){
								String value = qElement.getAttribute("value");
								List<String> idList = Arrays.asList(value.split("\\s*,\\s*"));
								gameTeamTime.put(teamID, new Hashtable<Integer, Integer>());
								for (int l = 0; l < 11; l++){
									int playerID = Integer.parseInt(idList.get(l));
									gameTeamTime.get(teamID).put(playerID,0);
								}
							}
						}
					}
				}

				else if (typeID == 18 || typeID == 20){ //spiller byttes ut eller går ut av banen pga skade etter antall minutter
					int playerID = Integer.parseInt(event.getAttribute("player_id"));
					int teamID = Integer.parseInt(event.getAttribute("team_id"));
					int minute = Integer.parseInt(event.getAttribute("min"));

					if (fullTeamTime.containsKey(teamID)){ //hvis lag finnes
						if (fullTeamTime.get(teamID).containsKey(playerID)){  // hvis lag inneholder spiller
							PlayerGameTime ptg = fullTeamTime.get(teamID).get(playerID);
							ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
							fullTeamTime.get(teamID).remove(playerID);
							fullTeamTime.get(teamID).put(playerID, ptg);
							gameTeamTime.get(teamID).remove(playerID);

						}
						else{ //hvis lag ikke inneholder spiller
							PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
							ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
							fullTeamTime.get(teamID).put(playerID, ptg);
							gameTeamTime.get(teamID).remove(playerID);
						}
					}
					else { //hvis lag ikke finnes og lag ikke inneholder spiller
						PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
						ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
						fullTeamTime.put(teamID, new Hashtable<Integer, PlayerGameTime>());
						fullTeamTime.get(teamID).put(playerID, ptg);
						gameTeamTime.get(teamID).remove(playerID);
					}
				}
				else if (typeID == 19 || typeID == 21){ // spiller byttes inn eller kommer inn igjen etter antall minutter
					int playerID = Integer.parseInt(event.getAttribute("player_id"));
					int teamID = Integer.parseInt(event.getAttribute("team_id"));
					int minute = Integer.parseInt(event.getAttribute("min"));
					gameTeamTime.get(teamID).put(playerID, minute);
				}

				else if (typeID == 17){ //Spiller går ut pga rødt kort
					NodeList qualifierList = event.getChildNodes();
					for(int m = 0; m < qualifierList.getLength(); m++){
						if(qualifierList.item(m).getNodeType() == Node.ELEMENT_NODE){
							Element qElement = (Element) qualifierList.item(m);
				    		int qid = Integer.parseInt(qElement.getAttribute("qualifier_id"));
				    		if (qid == 32 || qid== 33){
				    			int playerID =0;
				    			try{
				    				playerID = Integer.parseInt(event.getAttribute("player_id"));
				    			}
				    			catch (NumberFormatException e){
				    				continue;
				    			}

				    			int teamID = Integer.parseInt(event.getAttribute("team_id"));
								int minute = Integer.parseInt(event.getAttribute("min"));

								if (fullTeamTime.containsKey(teamID)){ //hvis lag finnes
									if (fullTeamTime.get(teamID).containsKey(playerID)){  // hvis lag inneholder spiller
										PlayerGameTime ptg = fullTeamTime.get(teamID).get(playerID);
										ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
										fullTeamTime.get(teamID).remove(playerID);
										fullTeamTime.get(teamID).put(playerID, ptg);
										gameTeamTime.get(teamID).remove(playerID);

									}
									else{ //hvis lag ikke inneholder spiller
										PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
										ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
										fullTeamTime.get(teamID).put(playerID, ptg);
										gameTeamTime.get(teamID).remove(playerID);
									}
								}
								else { //hvis lag ikke finnes og lag ikke inneholder spiller
									PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
									ptg.incrementGameTime(minute-gameTeamTime.get(teamID).get(playerID), season);
									fullTeamTime.put(teamID, new Hashtable<Integer, PlayerGameTime>());
									fullTeamTime.get(teamID).put(playerID, ptg);
									gameTeamTime.get(teamID).remove(playerID);
								}
				    		}
						}
					}
				}

			}
			//ved kampslutt:
			Set<Integer> teamKeys = gameTeamTime.keySet();
			for (int teamID: teamKeys){
				Hashtable<Integer, Integer> playergt = gameTeamTime.get(teamID);
				Set<Integer> playerKeys = playergt.keySet();
				ArrayList<Integer> p = new ArrayList<Integer>();
				p.addAll(playerKeys);
				for (int m =0; m < p.size(); m++){
					int playerID = p.get(m);
					if (fullTeamTime.containsKey(teamID)){ //hvis lag finnes
						if (fullTeamTime.get(teamID).containsKey(playerID)){  // hvis lag inneholder spiller
							PlayerGameTime ptg = fullTeamTime.get(teamID).get(playerID);
							ptg.incrementGameTime(90-gameTeamTime.get(teamID).get(playerID), season);
							fullTeamTime.get(teamID).remove(playerID);
							fullTeamTime.get(teamID).put(playerID, ptg);
							gameTeamTime.get(teamID).remove(playerID);

						}
						else{ //hvis lag ikke inneholder spiller
							PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
							ptg.incrementGameTime(90-gameTeamTime.get(teamID).get(playerID), season);
							fullTeamTime.get(teamID).put(playerID, ptg);
							gameTeamTime.get(teamID).remove(playerID);
						}
					}
					else { //hvis lag ikke finnes og lag ikke inneholder spiller
						PlayerGameTime ptg = new PlayerGameTime(playerID,teamID);
						ptg.incrementGameTime(90-gameTeamTime.get(teamID).get(playerID), season);
						fullTeamTime.put(teamID, new Hashtable<Integer, PlayerGameTime>());
						fullTeamTime.get(teamID).put(playerID, ptg);
						gameTeamTime.get(teamID).remove(playerID);
					}

				}
			}
			gameTeamTime.clear();
			System.out.println(listOfFiles[i].toString());
		}
		Dbhandler.insertPlayerGameTime(fullTeamTime);
	}
}
