package ubc.cosc322.util;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.search.SearchMethod;

/**
 * Use this class to simulate a game between two methods.
 */
public class Simulation {
	private static final byte WHITE = 0;
	private static final byte BLACK = 1;

	private final long[] empty = new long[2];
	private final byte[] white = new byte[4];
	private final byte[] black = new byte[4];

	private final SearchMethod whiteSearch;
	private final String whiteName;

	private final SearchMethod blackSearch;
	private final String blackName;

	public Simulation(
		SearchMethod whiteSearch, String whiteName,
		SearchMethod blackSearch, String blackName
	) {
		this.whiteSearch = whiteSearch;
		this.whiteName = whiteName;

		this.blackSearch = blackSearch;
		this.blackName = blackName;
	}

	public byte run(boolean printOutput) {
		resetBoard();

		boolean whiteTurn = true;
		int turn = 0;
		
		while (
			!Move.impossible(empty, black) 
			&& 
			!Move.impossible(empty, white)
		) {
			turn++;
			int move;
			if (whiteTurn) {
				whiteSearch.setBoard(empty, white, black);
				move = whiteSearch.execute();
			} else {
				blackSearch.setBoard(empty, white, black);
				move = blackSearch.execute();
			}

			Move.apply(empty, white, black, move);

			if (printOutput) {
				String label = 
					"T" + Integer.toString(turn) +  " " +
					(whiteTurn ? 
						whiteName + " (White)" 
						: 
						blackName + " (Black)");
				TextDisplay.boardWithLegend(
					empty, white, black, label
				);			
			}

			whiteTurn = !whiteTurn;
		}

		if (printOutput) {
			String label = "T" + Integer.toString(turn) +  " " +
				(whiteTurn ? whiteName + " (White)" : blackName + " (Black)");
			TextDisplay.boardWithLegend(empty, white, black, label);				
		}

		if (Move.impossible(empty, black)) {
			if (printOutput) {
				System.out.println("White (" + whiteName + ") wins!");
			}
			return WHITE;
		} else {
			if (printOutput) {
				System.out.println("Black (" + blackName + ") wins!");
			}
			return BLACK;
		}
	}

	private void resetBoard() {
		BitBoard.clear(empty);
		for (byte i = 0; i < 100; i++) {
			BitBoard.flag(empty, i);
		}

		white[0] = 60;
		white[1] = 93;
		white[2] = 96;
		white[3] = 69;

		black[0] = 30;
		black[1] = 03;
		black[2] = 06;
		black[3] = 39;

		for (byte queen : white) BitBoard.unflag(empty, queen);
		for (byte queen : black) BitBoard.unflag(empty, queen);
	}
}
