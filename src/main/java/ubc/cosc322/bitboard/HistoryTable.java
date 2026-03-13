package ubc.cosc322.bitboard;

import ubc.cosc322.util.Move;

public class HistoryTable {
	// All possible moves. The indices correspond to:
	// - the player making the move (0 or 1).
	// - the "from" square.
	// - the "to" square.
	// - the "arrow" square.
	//
	// This table requires, therefore, 2 * 100 * 100 * 100 * 32 bits; roughly 
	// 7.8 MB.
	private final int[][][][] historyScores = new int[2][100][100][100];


	// A "sufficient" move is one that (1) causes a cutoff, or (2) if no cutoff
	// occurs, yields the best minimax score. Every time that a sufficient move
	// is found, the history score is increased. 
	//
	// Upon reaching a non-terminal state, the subsequent states (child states)
	// should be ordered based on their history score.

	public HistoryTable() {}

	public int score(int move) {
		return historyScores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)];
	}

	public void increaseScore(int move, int depth) {
		historyScores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)] += 2 << depth;
	}
}
