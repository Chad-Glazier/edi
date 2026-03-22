package ubc.team09.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.eval.MinDist;
import ubc.team09.state.C;
import ubc.team09.state.Move;
import ubc.team09.state.State;

public class Display {

	public static String promptString(String prompt) {
		clear();
		printText(-13, prompt + "\n\n    > ");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		String input = scanner.nextLine().trim();

		while (input.length() == 0) {
			clear();
			printText(-13, prompt + "\n(Input cannot be empty)\n\n    > ");

			input = scanner.nextLine().trim();
		}

		return input;
	}

	public static int promptInt(String prompt) {
		clear();
		printText(-13, prompt + "\n\n    > ");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		String input = scanner.nextLine().trim();
		int inputInt = Integer.MIN_VALUE;

		while (inputInt == Integer.MIN_VALUE) {
			try {
				inputInt = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				clear();
				printText(
						-13, prompt + "\n(Input must be an integer)\n\n    > ");
				input = scanner.nextLine().trim();
			}
		}

		return inputInt;
	}

	public static String prompt(String prompt, Map<String, String> options) {
		clear();
		printText(-13, prompt + "\n\n    > ");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		String helpPrompt = "Unrecognized selection. Try one of the ones listed:\n";
		for (String option : options.keySet()) {
			helpPrompt += "    ~ " + Ansi.FG_BRIGHT_CYAN + option;
			helpPrompt += " " + Ansi.FG_BRIGHT_BLACK + options.get(option);
			helpPrompt += "\n";
		}
		helpPrompt += "\n    > ";

		while (true) {
			String input = scanner.nextLine().trim();

			for (String option : options.keySet()) {
				if (input.length() > 0 &&
						option.toLowerCase().startsWith(input.toLowerCase())) {
					return option;
				}
			}

			clear();
			printText(-13, helpPrompt);
		}
	}

	public static String prompt(String prompt, List<String> options) {
		clear();
		printText(-13, prompt + "\n\n    > ");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		String helpPrompt = "Unrecognized selection. Try one of the ones listed:\n";
		for (String option : options) {
			helpPrompt += "    ~ " + Ansi.FG_BRIGHT_CYAN + option + Ansi.FG_BRIGHT_BLACK;
			helpPrompt += "\n";
		}
		helpPrompt += "\n    > " + Ansi.RESET;

		while (true) {
			String input = scanner.nextLine().trim();

			for (String option : options) {
				if (input.length() > 0 &&
						option.toLowerCase().startsWith(input.toLowerCase())) {
					return option;
				}
			}

			clear();
			printText(-13, helpPrompt);
		}
	}

	public static void printText(int line, String text) {
		Ansi.clear(line + 15);
		System.out.print("    " + Ansi.FG_BRIGHT_BLACK + text);
		System.out.print(Ansi.RESET);
	}

	public static void printBlinkingText(int line, String text) {
		Ansi.clear(line + 15);
		System.out.print(Ansi.BLINK);
		System.out.print("    " + Ansi.FG_BRIGHT_BLACK + text);
		System.out.print(Ansi.RESET);
	}

	public static void printSystemMessages(LinkedList<String> messages) {
		Ansi.saveCursor();

		Ansi.moveCursor(4, 80);

		System.out.print(Ansi.FG_BRIGHT_BLACK);
		System.out.print(Ansi.UNDERLINE + "System messages" + Ansi.RESET);

		System.out.print(Ansi.FG_BRIGHT_BLACK);
		int i = 0;
		for (String message : messages) {
			if (i > 8) {
				break;
			}
			Ansi.moveCursor(5 + i++, 80);
			System.out.print("\u001B[K");
			System.out.print(Ansi.ITALIC + message);
		}

		System.out.print(Ansi.RESET);
		Ansi.restoreCursor();
	}

	/**
	 * Prints a board state to the console.
	 */
	public static void printBoard(
			State state, String label, String blackName, String whiteName) {
		// We can get the turn count by getting the number of flags in the
		// occupancy board and subtracting 8, for the queens (this gives us
		// the number of arrows).
		int turn = BitBoard.count(state.occupancy) - 8;

		// Clear the console.
		clear();

		// Create a board array of strings.
		String[][] board = new String[10][10];

		// Mark all occupied squares as arrows.
		for (byte i = 0; i < 10; i++) {
			for (byte j = 0; j < 10; j++) {
				if (BitBoard.flagged(state.occupancy, (byte) (i * 10 + j))) {
					board[i][j] = "X";
				} else {
					board[i][j] = ".";
				}
			}
		}

		// Place white queens
		for (int i = 0; i < 4; i++) {
			int queen = state.queens[i];
			int row = queen / 10;
			int col = queen % 10;
			board[row][col] = "W";
		}

		// Place black queens
		for (int i = 4; i < 8; i++) {
			int queen = state.queens[i];
			int row = queen / 10;
			int col = queen % 10;
			board[row][col] = "B";
		}

		// Print the board.
		System.out.println();

		System.out.print(
				Ansi.FG_BRIGHT_BLACK +
						"       0 1 2 3 4 5 6 7 8 9\n\n" +
						Ansi.RESET);
		for (int i = 0; i < 10; i++) {

			System.out.print(
					Ansi.FG_BRIGHT_BLACK +
							"    " + i + "  " +
							Ansi.RESET);
			for (int j = 0; j < 10; j++) {
				String s = board[i][j];
				switch (s) {
					case "W":
						System.out.print(
								Ansi.FG_CYAN +
										(state.activePlayer == C.WHITE ? Ansi.BLINK : "") +
										"■" +
										" " +
										Ansi.RESET);
						break;
					case "B":
						System.out.print(
								Ansi.FG_RED +
										(state.activePlayer == C.BLACK ? Ansi.BLINK : "") +
										"■" +
										" " +
										Ansi.RESET);
						break;
					case "X":
						System.out.print(
								Ansi.FG_BRIGHT_BLACK +
										s +
										" " +
										Ansi.RESET);
						break;
					default:
						System.out.print(
								Ansi.FG_BRIGHT_BLACK +
										"." +
										" " +
										Ansi.RESET);
				}
			}
			System.out.print("     ");
			switch (i) {
				case 0:
					System.out.print(
							Ansi.UNDERLINE +
									label +
									Ansi.RESET);
					break;
				case 2:
					System.out.print(
							Ansi.ITALIC +
									"turns taken       " +
									Ansi.RESET +
									Integer.toString(turn));
					break;
				case 3:
					System.out.print(
							Ansi.ITALIC +
									"active player     " +
									Ansi.RESET +
									playerToString(
											state.activePlayer,
											state.activePlayer == C.WHITE
													? whiteName
													: blackName)
									+
									Ansi.RESET);
					break;
				case 4:
					System.out.print(
							Ansi.ITALIC +
									"last move         " +
									Ansi.RESET +
									moveToString(state.move));
					break;
				case 5:
					System.out.print(
							Ansi.ITALIC +
									"last arrow        " +
									Ansi.RESET +
									arrowToString(state.move));
					break;
				case 9:
					System.out.print(
							Ansi.FG_BRIGHT_BLACK +
									"MinDist evaluation" +
									Ansi.RESET);
					break;
				case 7:
					System.out.print(evaluation(state));
					break;
			}
			System.out.println();
		}
		System.out.println(Ansi.RESET);
	}

	/**
	 * Prints a board state to the console.
	 */
	public static void printBoard(State state, String label) {
		printBoard(state, label, "Black", "White");
	}

	private static String evaluation(State state) {
		MinDist mindist = new MinDist();
		
		int width = 35;
		int whiteWidth = (int) (width * mindist.evaluateAndNormalize(state));
		int blackWidth = width - whiteWidth;

		String out = "";
		out += Ansi.RESET;

		out += Ansi.BG_CYAN;
		for (int i = 0; i < whiteWidth; i++) {
			out += " ";
		}

		out += Ansi.RESET;
		out += Ansi.BG_RED;
		for (int i = 0; i < blackWidth; i++) {
			out += " ";
		}

		out += Ansi.RESET;

		return out;
	}

	private static String playerToString(byte player, String name) {
		String out = "";
		out += Ansi.RESET;

		if (player == C.WHITE) {
			out += Ansi.BG_CYAN;
			out += "  ";
			out += Ansi.RESET;
			out += " " + name;
		} else {
			out += Ansi.BG_RED;
			out += "  ";
			out += Ansi.RESET;
			out += " " + name;
		}

		out += Ansi.RESET;

		return out;
	}

	private static String moveToString(int move) {
		String out = "";

		int fromRow = Move.start(move) / 10;
		int fromCol = Move.start(move) % 10;
		int toRow = Move.end(move) / 10;
		int toCol = Move.end(move) % 10;

		if (Move.player(move) == C.WHITE) {
			out += Ansi.FG_CYAN;
		} else {
			out += Ansi.FG_RED;
		}

		out += String.format("(%d, %d)", fromRow, fromCol);

		out += Ansi.RESET;

		out += " -> ";

		if (Move.player(move) == C.WHITE) {
			out += Ansi.FG_CYAN;
		} else {
			out += Ansi.FG_RED;
		}

		out += String.format("(%d, %d)", toRow, toCol);

		out += Ansi.RESET;
		return out;
	}

	private static String arrowToString(int move) {
		String out = "";

		int arrowRow = Move.arrow(move) / 10;
		int arrowCol = Move.arrow(move) % 10;

		out += Ansi.FG_BRIGHT_BLACK;
		out += String.format("(%d, %d)", arrowRow, arrowCol);
		out += Ansi.RESET;

		return out;
	}

	public static void clear() {
		System.out.print(Ansi.ERASE_SCREEN + Ansi.RESET_CURSOR);
	}
}
