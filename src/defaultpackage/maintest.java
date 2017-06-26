package defaultpackage;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class maintest {
	public static void main(String[] args) throws ClassNotFoundException, SAXException, IOException, ParserConfigurationException, SQLException {
		xmlReader.readEventFiles();
		//MarkovBuilder.buildFromEvents();
		//xmlReader.sendGamesFromEventFeeds();
	}
}
