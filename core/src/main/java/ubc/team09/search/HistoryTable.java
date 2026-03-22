package ubc.team09.search;

import java.util.Comparator;

import ubc.team09.state.Move;
import ubc.team09.state.State;

public class HistoryTable {
	// All possible moves. The indices correspond to:
	// - the player making the move (0 or 1).
	// - the "from" square.
	// - the "to" square.
	// - the "arrow" square.
	//
	// This table requires roughly 7.8 MB of raw data, plus any overhead that
	// the JVM tacks on.
	private final int[][][][] scores = new int[2][100][100][100];
	private static final int MAX_HISTORY = 2 << 13;

	public HistoryTable() {}

	public int score(int move) {
		return scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)];
	}

	/**
	 * Used to indicate that a move is sufficient; i.e., it's score should be
	 * increased.
	 * 
	 * @param move
	 * @param depth
	 */
	public void increaseScore(int move, int depth) {

		//
		// Below is the history score increment from Shaeffer's 2006 paper.
		//

		// scores
		// [Move.player(move)]
		// [Move.start(move)]
		// [Move.end(move)]
		// [Move.arrow(move)] += 2 << depth;

		//
		// Below is the History Gravity update, from this page:
		// https://www.chessprogramming.org/History_Heuristic
		//
		// The History Gravity formula has the benefit of removing the need for
		// any kind of explicit decay update between searches.
		//

		int bonus = Integer.max(
			-MAX_HISTORY, 
			Integer.min(depth * depth, MAX_HISTORY)
		);
		int initial = scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)];

		scores
			[Move.player(move)]
			[Move.start(move)]
			[Move.end(move)]
			[Move.arrow(move)] += bonus - initial * bonus / MAX_HISTORY;
	}

	/**
	 * A comparator for states that orders states in <em>descending</em> order
	 * based on their history scores.
	 */
	public final Comparator<State> descending = new Comparator<State>() {
		public int compare(State s1, State s2) {
			return score(s2.move) - score(s1.move);
		}
	};
}
