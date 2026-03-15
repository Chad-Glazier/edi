package ubc.cosc322.view;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.misc.C;
import ubc.cosc322.state.Move;
import ubc.cosc322.state.State;

public class Display {
    /**
     * Prints a board state to the console.
     */
    public static void printBoard(State state, String label) {
		// We can get the turn count by getting the number of flags in the
		// occupancy board and subtracting 8, for the queens (this gives us
		// the number of arrows).
		int turn = BitBoard.count(BitBoard.copy(state.occupancy)) - 8;

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
			Ansi.RESET
		);
        for (int i = 0; i < 10; i++) {

            System.out.print(
				Ansi.FG_BRIGHT_BLACK +
				"    " + i + "  " +
				Ansi.RESET
			);
            for (int j = 0; j < 10; j++) {
                String s = board[i][j];
                switch (s) {
				case "W":
					System.out.print(
						Ansi.BG_CYAN + 
						Ansi.FG_BRIGHT_WHITE +
						" " + 
						Ansi.RESET + 
						" "
					);
					break;
				case "B":
					System.out.print(
						Ansi.BG_RED + 
						Ansi.FG_BLACK +
						" " + 
						Ansi.RESET + 
						" "
					);
					break;
				case "X":
					System.out.print(
						Ansi.BG_BLACK + 
						Ansi.FG_BRIGHT_BLACK +
						s + 
						Ansi.RESET + 
						" "
					);
					break;
				default:
					System.out.print(
						Ansi.BG_BLACK + 
						Ansi.FG_BRIGHT_BLACK +
						"." + 
						Ansi.RESET + 
						" "
					);
                }
            }
			System.out.print("     ");
			switch (i) {
			case 0:
				System.out.print(
					Ansi.UNDERLINE +
					label +
					Ansi.RESET
				);
				break;
			case 2:
				System.out.print(
					Ansi.ITALIC +
					"turns taken       " +
					Ansi.RESET +
					Integer.toString(turn)
				);
				break;
			case 3:
				System.out.print(
					Ansi.ITALIC +
					"active player     " +
					Ansi.RESET + 
					playerToString(state.activePlayer) +
					Ansi.RESET
				);
				break;
			case 4:
				System.out.print(
					Ansi.ITALIC +
					"last move         " +
					Ansi.RESET +
					moveToString(state.move)
				);
				break;
			case 5:
				System.out.print(
					Ansi.ITALIC +
					"last arrow        " +
					Ansi.RESET +
					arrowToString(state.move)
				);
				break;
			}
            System.out.println();
        }
		System.out.println(Ansi.RESET);
    }

	private static String playerToString(byte player) {
		String out = "";
		out += Ansi.RESET;
		
		if (player == C.WHITE) {
			out += Ansi.BG_CYAN;
			out += "  ";
			out += Ansi.RESET;
			out += Ansi.FG_BRIGHT_WHITE;
			out += " White";		
		} else {
			out += Ansi.BG_RED;
			out += "  ";
			out += Ansi.RESET;
			out += Ansi.FG_BRIGHT_BLACK;
			out += " Black";	
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

	private static void clear() {
		System.out.print(Ansi.ERASE_SCREEN + Ansi.RESET_CURSOR);
	}
}
