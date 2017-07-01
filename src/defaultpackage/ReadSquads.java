package defaultpackage;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import objects.Player;

public class ReadSquads {
	public static void readPlayersFromFile(String filename) throws ClassNotFoundException, SAXException, IOException, ParserConfigurationException {

		Document doc = xmlReader.getDocument(filename);
		NodeList players = doc.getElementsByTagName("Player");
		String allPlayers = "";

		for(int i = 0; i < players.getLength(); i++){
			Element p = (Element)players.item(i);
			Player player = new Player();
			String uid=p.getAttribute("uID");
			uid = uid.replace("p", "");
			player.setId(Integer.parseInt(uid));
			String columns = "(PlayerID";
			String values = "("+uid;

			NodeList stats = p.getElementsByTagName("Stat");
			NodeList position = p.getElementsByTagName("Position");
			for (int j = 0; j<position.getLength();j++){
				if (position.item(j).getNodeType()==Node.ELEMENT_NODE){
					columns += ", Position";
					values += ",'" + position.item(j).getTextContent() +"'";
					System.out.println(position.item(j).getTextContent());
				}
			}
			for (int j=0;j<stats.getLength();j++){
				if(stats.item(j).getNodeType()==Node.ELEMENT_NODE){
					Element s = (Element)stats.item(j);
					if(s.getAttribute("Type").equals("first_name")){
						player.setFirst_name(s.getTextContent());
						columns+=", FirstName";
						values+=", "+"'"+s.getTextContent()+"'";
					}
					else if (s.getAttribute("Type").equals("last_name")){
						player.setLast_name(s.getTextContent());
						columns+=", LastName";
						values+=", "+"'"+s.getTextContent()+"'";
					}
					else if (s.getAttribute("Type").equals("birth_date")){
						String birth = s.getTextContent();
						if (!birth.equals("Unknown")){
							player.setBirth_year(Integer.parseInt(birth.substring(0, 4)));
							columns+=", BirthYear";
							values+=", "+birth.substring(0,4);
						}
					}
					else if (s.getAttribute("Type").equals("height")){
						String content=s.getTextContent();
						if (!content.equals("Unknown")){
							int height = Integer.parseInt(content);
							player.setHeight(height);
							if(height>0){
								columns+=", Height";
								values+=", "+content;
							}
						}
					}
					else if (s.getAttribute("Type").equals("real_position")){
						String content=s.getTextContent();
						if (!content.equals("Unknown")){
							player.setReal_position(content);
							columns+=", RealPosition";
							values+=", "+"'"+content+"'";
						}
					}
					else if (s.getAttribute("Type").equals("country")){
						String country;
						if (s.getTextContent().contains("'")){
							country = s.getTextContent().replace("'", " ");
							player.setCountry(country);
						}
						else{
							country = s.getTextContent();
							player.setCountry(country);

						}
						columns+=", Country";
						values+=", "+"'"+country+"'";
					}
					else if (s.getAttribute("Type").equals("position")){
						
					}
				}

			}
			columns+=")";
			values+=")";
			String sqlString="INSERT INTO Player " +columns+" VALUES " +values +";\n";
			allPlayers += sqlString;
			System.out.println(sqlString);
			try {
				Dbhandler.insertPlayer(sqlString);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println();
			}
		}
	}
	public static void insertAllSeasonsNorway() throws ClassNotFoundException, SAXException, IOException, ParserConfigurationException{
//		readPlayersFromFile("squads/srml-90-2014-squads.xml");
//		readPlayersFromFile("squads/srml-90-2015-squads.xml");
		readPlayersFromFile("squads/srml-90-2016-squads.xml");
	}
	public static void findPlayerTeam() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, SQLException{
		Document doc = xmlReader.getDocument("squads/srml-90-2014-squads.xml");
		NodeList teams = doc.getElementsByTagName("Team");
		String sql="";
		for(int i = 0; i < teams.getLength(); i++){
			Element t = (Element)teams.item(i);
			String teamID = t.getAttribute("uID");
			teamID = teamID.replace("t", "");
			NodeList players = t.getChildNodes();
			for (int j = 0; j < players.getLength(); j++){
				if (players.item(j).getNodeType()==Node.ELEMENT_NODE){
					Element p = (Element)players.item(j);
					String playerID = p.getAttribute("uID");
					playerID = playerID.replace("p", "");
					if (!playerID.equals("") && !playerID.startsWith("man")){
						sql = "UPDATE Player SET TeamID = " +teamID +" WHERE PlayerID = " + playerID +";\n";
						Dbhandler.updatePlayerTeam(sql);
						
					}
				}
			}
		}
		doc = xmlReader.getDocument("squads/srml-90-2015-squads.xml");
		teams = doc.getElementsByTagName("Team");
		for(int i = 0; i < teams.getLength(); i++){
			Element t = (Element)teams.item(i);
			String teamID = t.getAttribute("uID");
			teamID = teamID.replace("t", "");
			NodeList players = t.getChildNodes();
			for (int j = 0; j < players.getLength(); j++){
				if (players.item(j).getNodeType()==Node.ELEMENT_NODE){
					Element p = (Element)players.item(j);
					String playerID = p.getAttribute("uID");
					playerID = playerID.replace("p", "");
					if (!playerID.equals("") && !playerID.startsWith("man")){
						sql = "UPDATE Player SET TeamID = " +teamID +" WHERE PlayerID = " + playerID +";\n";
						Dbhandler.updatePlayerTeam(sql);
					}
				}
			}
		}
		doc = xmlReader.getDocument("squads/srml-90-2016-squads.xml");
		teams = doc.getElementsByTagName("Team");
		for(int i = 0; i < teams.getLength(); i++){
			Element t = (Element)teams.item(i);
			String teamID = t.getAttribute("uID");
			teamID = teamID.replace("t", "");
			NodeList players = t.getChildNodes();
			for (int j = 0; j < players.getLength(); j++){
				if (players.item(j).getNodeType()==Node.ELEMENT_NODE){
					Element p = (Element)players.item(j);
					String playerID = p.getAttribute("uID");
					playerID = playerID.replace("p", "");
					if (!playerID.equals("") && !playerID.startsWith("man")){
						sql = "UPDATE Player SET TeamID = " +teamID +" WHERE PlayerID = " + playerID +";\n";
						Dbhandler.updatePlayerTeam(sql);
					}
				}
			}
		}
//		Dbhandler.updatePlayerTeam(sql);
		
	}
	
	
}
