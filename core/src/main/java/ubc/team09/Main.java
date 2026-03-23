package ubc.team09;

import java.util.LinkedList;

import ubc.team09.player.Player;
import ubc.team09.state.C;
import ubc.team09.view.Display;

import ygraph.ai.smartfox.games.GamePlayer;

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
			playerColors);
		byte starting;
		if (startingColor.equalsIgnoreCase("White")) {
			starting = C.WHITE;
		} else {
			starting = C.BLACK;
		}

		GamePlayer player = new Player(
			username,
			starting,
			timeLimit
		);

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
