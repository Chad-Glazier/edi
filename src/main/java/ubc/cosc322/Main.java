package ubc.cosc322;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ubc.cosc322.player.EDI;
import ubc.cosc322.player.Player;
import ubc.cosc322.state.C;
import ubc.cosc322.view.Display;
import ygraph.ai.smartfox.games.GamePlayer;

/**
 * 
 */
public class Main {

    public static void main(String[] args) throws Exception {

		String username = Display.promptString(
			"Enter the username to use for server communication."
		);

		int timeLimit = Display.promptInt(
			"Enter the time limit (in seconds)."
		);

		LinkedList<String> playerColors = new LinkedList<>();
		playerColors.add("Black");
		playerColors.add("White");

		String startingColor = Display.prompt(
			"Enter which color starts.", 
			playerColors
		);
		byte starting;
		if (startingColor.equalsIgnoreCase("White")) {
			starting = C.WHITE;
		} else {
			starting = C.BLACK;
		}

		Map<String, String> players = new HashMap<>();
		players.put(
			"EDI", 
			"primarily uses an optimized alpha-beta search."
		);
		String playerName = 
			Display.prompt("Choose a VI to consult.", players);

		GamePlayer player = null;
		switch (playerName) {
		case "EDI":
			player = new Player(
				username, 
				new EDI(), 
				starting, 
				timeLimit
			);
			break;
		default:
			Display.printText(0, "Error: No bot chosen.");
			return;
		}

		try {
			player.connect();
		} catch (Exception e) {
			Display.clear();
			Display.printText(0, 
				"Error connecting to server." +
				"\nEnsure you're on the right network."
			);
		}
	}
}
