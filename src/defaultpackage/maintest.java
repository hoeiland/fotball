package defaultpackage;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import Stratagem.ChanceReader;

public class maintest {
	public static void main(String[] args) throws ClassNotFoundException, SAXException, IOException, ParserConfigurationException, SQLException {
//		xmlReader.readEventFiles();
//		xmlReader.sendGamesFromEventFeeds();
//		MarkovBuilder.buildFromEvents();
//		MarkovBuilder.setStateAction();
//		Reinforcement.learning();
//		ReadSquads.insertAllSeasonsNorway();
//		ReadSquads.findPlayerTeam();
//		ChanceReader.readChanceCSV("chances/2017-04-13_chances.csv");
		GameTimeReader.setPlayerGameTime();
	}
}
