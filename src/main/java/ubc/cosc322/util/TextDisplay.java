package ubc.cosc322.util;

import ubc.cosc322.bitboard.BitBoard;

class TextBoard {
	private static final int N = 10; // number of rows
	private static final int M = 10; // number of columns
	private static final int SIZE = N * M;

	private static final String BLACK_QUEEN = "B";
	private static final String WHITE_QUEEN = "W";
	private static final String EMPTY = "_";
	private static final String ARROW = "X";

	private String[] board;
	private String label;

	private static final String[] LEGEND = {
		"Legend",
		"-----------------------",
		EMPTY + ": empty square",
		ARROW + ": arrow-struck square",
		WHITE_QUEEN + ": white queen",
		BLACK_QUEEN + ": black queen"
	};

	public TextBoard(long[] empty, byte[] white, byte[] black, String label) {
		this.board = new String[100];

		for (byte i = 0; i < SIZE; i++) {
			if (BitBoard.flagged(empty, i)) {
				board[i] = EMPTY;
			} else {
				board[i] = ARROW;
			}
		}

		for (byte queen : white) {
			board[queen] = WHITE_QUEEN;
		}

		for (byte queen : black) {
			board[queen] = BLACK_QUEEN;
		}

		this.label = label;
	}

	public String row(int index) {
		String row = "";
		for (int i = index * M; i < index * M + M; i++) {
			row += board[i];
			if (i != index * M + M - 1) row += " ";
		}
		return row;
	}

	private static String centerPad(String text, int width) {
		boolean alternating = true;
		while (text.length() < width) {
			if (alternating) {
				text = text + " ";
			} else {
				text = " " + text;
			}
			alternating = !alternating;
		}

		return text;
	}

	public static void print(TextBoard board) {
		System.out.print("\n\t" + "   0 1 2 3 4 5 6 7 8 9\n");
		for (int i = 0; i < N; i++) {
			System.out.printf(
				"\t%d  %s\n",
				i, board.row(i)
			);
		}
		System.out.print("\n\t" + centerPad(board.label, 22) + "\n\n");
	}

	public static void printLegend(TextBoard[] board) {
		System.out.println();
		for (String row : LEGEND) {
			System.out.printf("\t%s\n", row);
		}
		System.out.println();
	}

	public static void printWithLegend(TextBoard board) {
		System.out.print("\n\t" + "   0 1 2 3 4 5 6 7 8 9\n");
		for (int i = 0; i < N; i++) {
			System.out.printf(
				"\t%d  %s",
				i, board.row(i)
			);
			if (i < LEGEND.length) {
				System.out.printf("  %s", LEGEND[i]);
			}
			System.out.print("\n");
		}
		System.out.print("\n\t" + centerPad(board.label, 22) + "\n\n");
	}

	public static void printTogether(TextBoard left, TextBoard right) {
		String gap = "    ";
		System.out.print(
			"\n\t" + "   0 1 2 3 4 5 6 7 8 9" +
			gap + "   0 1 2 3 4 5 6 7 8 9\n"
		);
		for (int i = 0; i < N; i++) {
			System.out.printf("\t%d  %s", i, left.row(i));
			System.out.print(gap);
			System.out.printf("%d  %s", i, right.row(i));
			System.out.print("\n");
		}
		System.out.print("\n\t" + centerPad(left.label, 22));
		System.out.print(gap + centerPad(right.label, 22));
		System.out.print("\n\n");
	}

	public static void printTogetherWithLegend(
		TextBoard left, TextBoard right
	) {
		String gap = "    ";
		System.out.print(
			"\n\t" + "   0 1 2 3 4 5 6 7 8 9" +
			gap + "   0 1 2 3 4 5 6 7 8 9\n"
		);
		for (int i = 0; i < N; i++) {
			System.out.printf("\t%d  %s", i, left.row(i));
			System.out.print(gap);
			System.out.printf("%d  %s", i, right.row(i));
			if (i < LEGEND.length) {
				System.out.printf("%s%s", gap, LEGEND[i]);
			}
			System.out.print("\n");
		}
		System.out.print("\n\t" + centerPad(left.label, 22));
		System.out.print(gap + centerPad(right.label, 22));
		System.out.print("\n\n");
	}

	public static void printMove(int move) {
		
	}
}

/**
 * This class contains a number of static functions to display (in text format)
 * various parts of the game, including board states, moves, the game tree,
 * etc.
 */
public class TextDisplay {
	public static void boardWithLegend(
		long[] empty, byte[] white, byte[] black, String label
	) {
		TextBoard textBoard = new TextBoard(empty, white, black, label);
		TextBoard.printWithLegend(textBoard);
	}

	public static void sideBySideWithLegend(
		long[] leftEmpty, byte[] leftWhite, byte[] leftBlack, 
		String leftLabel,
		long[] rightEmpty, byte[] rightWhite, byte[] rightBlack, 
		String rightLabel
	) {
		TextBoard left = new TextBoard(
			leftEmpty, leftWhite, leftBlack, leftLabel
		);
		TextBoard right = new TextBoard(
			rightEmpty, rightWhite, rightBlack, rightLabel
		);
		TextBoard.printTogetherWithLegend(left, right);
	}
}
