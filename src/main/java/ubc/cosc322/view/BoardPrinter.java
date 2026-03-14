package ubc.cosc322.view;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.state.State;

public class BoardPrinter {
    /**
     * Prints a 10x10 Amazons board state to the console.
     */
    public static void printBoard(State state) {
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
        System.out.println("   0 1 2 3 4 5 6 7 8 9\n");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + "  ");
            for (int j = 0; j < 10; j++) {
                String s = board[i][j];
                switch (s) {
                    case "W":
						System.out.print(
							Ansi.BG_CYAN + 
							Ansi.FG_BRIGHT_WHITE +
							s + 
							Ansi.RESET + 
							" "
						);
						break;
                    case "B":
						System.out.print(
							Ansi.BG_RED + 
							Ansi.FG_BLACK +
							s + 
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
							" " + 
							Ansi.RESET + 
							" "
						);
                }
            }
            System.out.println();
        }
		System.out.println();
    }
}
