package Stratagem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import objects.StratagemChance;

public class ChanceReader {
	public static void readChanceCSV(String csvFileName) throws IOException {
		String csvFile = csvFileName;
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		String line = "";
		String csvSplitBy = ",";
		line=br.readLine();
		while ((line=br.readLine())!=null) {
			String[] chanceData = line.split(csvSplitBy);
			StratagemChance chance = new StratagemChance();
			chanceFromArray(chance, chanceData);
		}
	}
	
	public static StratagemChance chanceFromArray(StratagemChance sc, String[] line) {
		sc.setStrataGameID(Integer.parseInt(line[0]));
		sc.setCompetition(line[1]);
		sc.setStrataGameID(Integer.parseInt(line[2]));
		sc.setGameDate(line[3]);
//		sc.setPlayerName(line[7]);
		sc.setIcon(getIconcode(line[7]));
		sc.setRating(getIconcode(line[8]));
		sc.setChanceType(line[10]);
		System.out.println(sc.toString());
		
		
		
		
		return sc;
		
	}

	private static int getIconcode(String iconString) {
		switch (iconString) {
			case "goal":
				return 1;
			case "owngoal":
				return 2;
			case "penmissed":
				return 3;
			case "penawarded":
				return 4;
			case "superbchance":
				return 5;
			case "greatchance":
				return 6;
			case "verygoodchance":
				return 7;
			case "goodchance":
				return 8;
			case "fairlygoodchance":
				return 9;
			case "poorchance":
				return 10;
			case "-":
				return 11;
			
		}
		return 0;
	}
}
