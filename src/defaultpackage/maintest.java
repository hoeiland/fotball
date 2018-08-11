package defaultpackage;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import Stratagem.ChanceReader;

public class maintest {
	public static void main(String[] args) throws ClassNotFoundException, SAXException, IOException, ParserConfigurationException, SQLException, InterruptedException {
//		xmlReader.readEventFiles();
//		xmlReader.sendGamesFromEventFeeds();
//		MarkovBuilder.buildFromEvents();
//		MarkovBuilder.setStateAction();
//		Reinforcement.learning();
//		ReadSquads.insertAllSeasonsNorway();
//		ReadSquads.findPlayerTeam();
//		ChanceReader.readChanceCSV("chances/2017-04-13_chances.csv");
//		GameTimeReader.setPlayerGameTime();
//		convertScraped.insertEventlistsFromGames(30451,30545);
		convertScraped.convertAndInsertGames(30166, 30300);
//		convertScraped.setTransitionIdRangeOfGames(28300, 28378);
//		convertScraped.findPlayerImpactFromRangeOfGames(28015, 28378);
		
	}
}
