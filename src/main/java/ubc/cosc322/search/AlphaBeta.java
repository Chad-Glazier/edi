package ubc.cosc322.search;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.misc.C;
import ubc.cosc322.state.State;
import ubc.cosc322.state.StateGenerator;
import ubc.cosc322.view.Ansi;

/**
 * <h3>Alpha-Beta Search</h3>
 * 
 * This function implements minimax search with α-β pruning, iterative
 * deepening, and the History heuristic (a generalization of the Killer
 * heuristic).
 * 
 * <br /><br />
 * 
 * As this algorithm uses iterative deepening, it will continue running until
 * its time limit runs out to find the best result. To set a specific time 
 * limit, run 
 * {@link #setTimeLimit()} or 
 * {@link #setTimeLimitMs()}.
 * 
 * <h4>Example</h4>
 * 
 * Assume that <code>board</code> and <code>heuristic</code> are already 
 * defined.
 * 
 * <pre>{@code
 * SearchMethod alphaBeta = new AlphaBeta(board, heuristic, C.WHITE);
 * alphaBeta.setTimeLimit(10); // 10 seconds per search
 * alphaBeta.setShowOutput(true); // `false` by default
 * 
 * // Then, whenever the boardstate changes,
 * alphaBeta.setBoard(board);
 * int move = alphaBeta.search();
 * }</pre>
 * 
 * Configuration options like the time limit and the maximizing player color
 * will be preserved between <code>search</code> calls. The only thing you 
 * need to remember to update is the board state.
 * 
 * <h4>Remarks</h4>
 * 
 * The <code>move</code> output of a search function is an encoded integer. To
 * get details about the move from this integer, use the methods from the
 * {@link ubc.cosc322.state.Move} class. The most relevant ones are:
 * 
 * <ul>
 * 	<li>{@link ubc.cosc322.state.Move#arrow} to get the position index of 
 * the arrow being fired in this move,</li>
 * 	<li>{@link ubc.cosc322.state.Move#start} to get the starting position
 * of the queen that we want to move, and</li>
 * 	<li>{@link ubc.cosc322.state.Move#end} to get the position we want to 
 * move the queen to.</li>
 * </ul>
 * 
 * <hr />
 * 
 * <h4>See Also</h4>
 * <ul>
 * 	<li>(Wikipedia) <em><a href="https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search">Iterative deepening</a></em>.</li>
 *  <li>Schaeffer, J. <em><a href="https://webdocs.cs.ualberta.ca/~jonathan/publications/ai_publications/pami.pdf">The History Heuristic and Alpha-Beta Search Enhancements in Practice</a></em>.</li>
 * </ul>
 */
public class AlphaBeta 
	extends TimeConstrained 
	implements SearchMethod
	{
	private static final int MAX_DEPTH = 100;

	/** The table of history scores. */
	private final HistoryTable history = new HistoryTable();

	// Configuration options.
	/** The initial board state. */
	private State root;
	/** The player who we want to win. */
	private final byte player;
	/** The heuristic used to evaluate game states. */
	private HeuristicMethod heuristic;
	/** Indicates whether or not we want to see output to the console. */
	private boolean showOutput = false;

	public AlphaBeta(
		State initialState, 
		HeuristicMethod heuristic,
		byte maximizingPlayer
	) {
		this.root = initialState.copy();
		this.player = maximizingPlayer;
		this.heuristic = heuristic;
	}

	// 
	// Methods for setting configurations.
	//

	public void setShowOutput(boolean showOutput) {
		this.showOutput = showOutput;
	}

	public void setHeuristic(HeuristicMethod heuristic) {
		this.heuristic = heuristic;
	}

	public void setBoard(State state) {
		this.root = state.copy();
	}

	//
	// The actual search method.
	//

	public int search() {
		startTimer();
		int bestMove = 0;

		try {
			for (int depth = 1; depth < MAX_DEPTH; depth++) {
				int bestMoveAtDepth = depthLimitedSearch(depth);
				if (bestMoveAtDepth == 0) {
					break;
				} else {
					bestMove = bestMoveAtDepth;
					if (showOutput) {
						System.out.printf(
							Ansi.MOVE_CURSOR_TO_LINE_START + 
							Ansi.FG_BRIGHT_BLACK +
							"    Searching at depth %d..." +
							Ansi.RESET, 
							depth + 1
						);
					}
				}
			}				
		} catch (TimeoutException e) {}
		
		return bestMove;
	}

	private int depthLimitedSearch(int depth) throws TimeoutException {

		//
		// Note: The heuristic evaluation will return lesser values if a state
		// favors Black, and greater values if a state favors White.
		//

		//
		// Collect all the children.
		//

		ArrayList<State> children = new ArrayList<State>(400);
		StateGenerator iter = root.children();
		for (
			State child = iter.next();
			child != null;
			child = iter.next()
		) {
			children.add(child);
		}

		//
		// Check if the state is terminal.
		//

		if (children.isEmpty()) {
			return 0;
		}

		//
		// Order the children based on their history scores.
		//

		children.sort(history.descending);

		//
		// Find the best move.
		//

		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		int color = this.player == C.WHITE ? +1 : -1;

		int bestMove = 0;

		for (State child : children) {

			double score = - alphaBeta(
				child, - beta, - alpha, depth - 1, - color
			);

			if (score > alpha) {
				alpha = score;
				bestMove = child.move;
			}
		}

		return bestMove;
	}

	/**
	 * 
	 * @param state
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @param color <code>-1</code> to find the ideal score for Black, and
	 * <code>+1</code> to find the ideal score for White.
	 * @return
	 * @throws TimeoutException
	 */
	private double alphaBeta(
		State state, 
		double alpha, 
		double beta, 
		int depth,
		int color
	) throws TimeoutException {

		//
		// Check the time limit; if time limit is expired, we throw an 
		// exception. To save sytem calls, this function only actually checks
		// the time once every ~1000 invocations.
		//

		checkTimeOccasionally();

		//
		// Check if we are maximum depth.
		//

		if (depth == 0) {
			return color * heuristic.evaluate(state);
		}

		//
		// Collect all the children.
		//

		ArrayList<State> children = new ArrayList<State>(400);
		StateGenerator iter = state.children();
		for (
			State child = iter.next();
			child != null;
			child = iter.next()
		) {
			children.add(child);
		}

		//
		// Check if the state is terminal.
		//

		if (children.isEmpty()) {
			return color * heuristic.evaluate(state);
		}

		//
		// Sort the children based on their history scores.
		//

		children.sort(history.descending);

		//
		// Find the best score using the negamax implementation of alpha-beta.
		//

		int bestMove = 0;
		double score = Double.NEGATIVE_INFINITY;
		for (State child : children) {

			double result = - alphaBeta(
				child, - beta, - alpha, depth - 1, - color
			);
			if (result > score) {
				score = result;
			}

			if (score >= beta) {
				bestMove = child.move;
				// update the history score for "sufficient" moves; i.e., ones
				// that cause a cutoff.
				history.increaseScore(bestMove, depth);
				break;
			}

			alpha = Math.max(alpha, score);
		}

		//
		// Return the score.
		//

		return score;
	}
}
