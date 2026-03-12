package ubc.cosc322.eval;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.util.Graph;

/**
 * <h3>Min-Dist Heuristic Evaluation</h3>
 * 
 * This class implements the minimum-distance ("mindist") heuristic evaluation
 * function described by Martin Müller and Theodore Tegos.
 * 
 * <h4>Example</h4>
 * 
 * Suppose that you have some <code>int[100]</code> array called 
 * <code>board</code> that represents a board state 
 * (see {@link Util#flatten} for more information about how to represent a
 * board in this way). You can then evaluate its board state like so:
 * <pre>{@code
 * HeuristicMethod mindist = new HeuristicMethod();
 * mindist.setBoard(board);
 * double score = mindist.evaluate(playerColor); 
 * }</pre>
 * 
 * <code>playerColor</code> is a byte that indicates whether the player is
 * White or Black, where <code>0</code> represents White and <code>1</code>
 * represents Black. A higher <code>score</code> indicates a better position
 * for the specified player.
 * 
 * <br /><br />
 * 
 * To evaluate a second board state, you need not instantiate this class again.
 * Instead, you can just use {@link #setBoard()} to update the board state
 * before running {@link #evaluate()} again.
 * 
 * <hr />
 * 
 * <h4>See Also</h4>
 * <ul>
 * 	<li>Müller M, Tegos T. <em><a href="http://doi.org/10.1017/9781316135167.018">Experiments in Computer Amazons</a></em>. The algorithm is described in section 6.2.</li>
 * </ul>
 */
public class MinDist implements HeuristicMethod {
	/** The number of rows on the board. */
	private static final int N = 10;
	/** The number of columns on the board. */
	private static final int M = 10;
	/** The number of squares on the board. */
	private static final int SIZE = N * M;
	/** The number of queens per player. */
	private static final int QUEENS = 4;

	private static final int EMPTY = 0;
	private static final int WHITE_QUEEN = 1;
	private static final int BLACK_QUEEN = 2;

	private static final int WHITE = 0;

	/**
	 * Bitboard where empty squares should be flagged.
	 */
	private final long[] empty = BitBoard.create();
	/**
	 * Stores the indices of each white queen.
	 */
	private final byte[] white = new byte[QUEENS];
	/**
	 * Stores the indices of each black queen.
	 */
	private final byte[] black = new byte[QUEENS];

	public void setBoard(int[] board) {
		BitBoard.clear(empty);

		int whiteQueensMarked = 0;
		int blackQueensMarked = 0;

		for (byte i = 0; i < SIZE; i++) {
			switch (board[i]) {
			case EMPTY:
				BitBoard.flag(empty, i);
				break;
			case WHITE_QUEEN:
				white[whiteQueensMarked] = i;
				whiteQueensMarked++;
				break;
			case BLACK_QUEEN:
				black[blackQueensMarked] = i;
				blackQueensMarked++;
				break;
			}
		}
	}

	public void setBoard(long[] empty, byte[] white, byte[] black) {
		BitBoard.copyTo(empty, this.empty);

		for (int i = 0; i < QUEENS; i++) {
			this.white[i] = white[i];
			this.black[i] = black[i];
		}
	}

	public double evaluate(byte player) {
		byte[] blackMinDistBoard = Graph.distance(empty, black[0]);
		byte[] whiteMinDistBoard = Graph.distance(empty, white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			byte[] newBlackDistBoard = Graph.distance(empty, black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			byte[] newWhiteDistBoard = Graph.distance(empty, white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = 100;
		for (byte i = 0; i < SIZE; i++) {
			if (!BitBoard.flagged(empty, i)) {
				availableSquares--;
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				continue;
			}
		}

		// if (player == WHITE) {
		// 	return ((double) (whiteSquares - blackSquares)) / ((double) 2 * availableSquares) + 0.50;
		// } else {
		// 	return ((double) (blackSquares - whiteSquares)) / ((double) 2 * availableSquares) + 0.50;
		// }
		return whiteSquares - blackSquares;
	}

	public void visualize() {
		final String arrowMark = "X"; // "\u21A1"
		final String blackTerritory = "b"; // "\u25A0";
		final String whiteTerritory = "w"; // "\u25A1";
		final String contestedTerritory = "?"; // "\u2591";
		final String whiteQueen = "W"; // Character.toString(0x1FA0A);
		final String blackQueen = "B"; // Character.toString(0x1FA3A);

		byte[] blackMinDistBoard = Graph.distance(empty, black[0]);
		byte[] whiteMinDistBoard = Graph.distance(empty, white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			byte[] newBlackDistBoard = Graph.distance(empty, black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			byte[] newWhiteDistBoard = Graph.distance(empty, white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		String[] boardDisplay = new String[SIZE];

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = SIZE;
		int contestedSquares = 0;
		for (byte i = 0; i < SIZE; i++) {
			if (!BitBoard.flagged(empty, i)) {
				availableSquares--;
				boardDisplay[i] = arrowMark;
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				boardDisplay[i] = blackTerritory;
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				boardDisplay[i] = whiteTerritory;
				continue;
			}
			boardDisplay[i] = contestedTerritory;
			contestedSquares++;
		}

		for (int queen : white) {
			boardDisplay[queen] = whiteQueen;
		}

		for (int queen : black) {
			boardDisplay[queen] = blackQueen;
		}

		for (int i = 0; i < SIZE; i++) {
			if (i % M == 0) {
				System.out.print("\n\t");
			}
			System.out.print(boardDisplay[i] + " ");
			if (i % M == M - 1) {
				switch (i / M) {
				case 0:
					System.out.print("\tLegend");
					break;
				case 1: 
					System.out.print("\t-----------------------");
					break;
				case 2:
					System.out.print(
						"\t"+ arrowMark + ": arrow-struck square"
					);
					break;
				case 3:
					System.out.print(
						"\t" + whiteQueen + ": white queen");
					break;
				case 4:
					System.out.print(
						"\t" + blackQueen + ": black queen");
					break;
				case 5:
					System.out.print(
						"\t" + whiteTerritory + ": white territory");
					break;
				case 6:
					System.out.print(
						"\t" + blackTerritory + ": black territory");
					break;
				case 7:
					System.out.print(
						"\t" + contestedTerritory + ": contested territory");
					break;
				}
			}
		}
		System.out.print("\n\n");
		System.out.printf("\tWhite squares:      %d (%.0f%%)\n", 
			whiteSquares,
			100 * (whiteSquares / (double) availableSquares)
		);
		System.out.printf("\tBlack squares:      %d (%.0f%%)\n", 
			blackSquares,
			100 * (blackSquares / (double) availableSquares)
		);
		System.out.printf("\tContested squares:  %d (%.0f%%)\n", 
			contestedSquares,
			100 * (contestedSquares / (double) availableSquares)
		);
		System.out.println();

		System.out.printf(
			"\tHeuristic evaluation for White: %.2f%%\n", 
			(((double) (whiteSquares - blackSquares)) / ((double) 2 * availableSquares) + 0.50) * 100
		);
		System.out.printf(
			"\tHeuristic evaluation for Black: %.2f%%\n\n", 
			(((double) (blackSquares - whiteSquares)) / ((double) 2 * availableSquares) + 0.50) * 100
		);
	}
}
