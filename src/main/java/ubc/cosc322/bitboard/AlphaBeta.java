package ubc.cosc322.bitboard;

import java.util.Comparator;

/**
 * 
 */
public class AlphaBeta {
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	public static final int MAX_DEPTH = 100;

	// Configuration options.
	/** Dictates how many possible branches to consider at each sub-tree. */
	public final int maxBranchingFactor = 400;
	/** The initial board state. */
	private final AlphaBetaState root;
	/** The player who we want to win. */
	private final byte player;
	/** The heuristic used to evaluate game states. */
	private final HeuristicMethod heuristic;
	/** The table of history scores. */
	private final HistoryTable history = new HistoryTable();

	AlphaBeta(
		State initialState, 
		HeuristicMethod heuristic,
		byte maximizingPlayer
	) {
		this.root = new AlphaBetaState(this, initialState.copy());
		this.player = maximizingPlayer;
		this.heuristic = heuristic;
	}

	public final Comparator<State> comparator = 
		new Comparator<State>() {
			public int compare(State s1, State s2) {
				return history.score(s1.move) - history.score(s2.move);
			}
		};
}
