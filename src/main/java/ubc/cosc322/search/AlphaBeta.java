package ubc.cosc322.search;

import java.util.concurrent.TimeoutException;

import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.gametree.State;
import ubc.cosc322.util.BitBoard;

class SearchTimeout extends RuntimeException {}

/**
 * <h3>Alpha-Beta Search</h3>
 * 
 * This function implements minimax search with α-β pruning and iterative
 * deepening. At the time of writing, there is no constraint on search breadth,
 * nor any move ordering. This makes the algorithm notably weak in the early
 * and mid game, but its completeness makes it extremely potent in end-games.
 * 
 * <br /><br />
 * 
 * As this algorithm uses iterative deepening, it will continue running until
 * its time limit runs out to find the best result. To set a specific time 
 * limit, run 
 * {@link TimeConstrained#setTimeLimit()} or 
 * {@link TimeConstrained#setTimeLimitMs()}.
 * 
 * <h4>Example</h4>
 * 
 * <pre>{@code
 * // We need to specify the heuristic evaluation function and the player whos
 * // position we are trying to optimize (i.e., who we're controlling).
 * AlphaBeta alphaBeta = new AlphaBeta(new MinDist(), WHITE);
 * alphaBeta.setTimeLimit(10); // each move in 10s
 * 
 * int[] board; // assume this holds our boardstate
 * 
 * // Every time the board is updated, we should call .setBoard before running
 * // .execute again.
 * alphaBeta.setBoard(board);
 * int move = alphaBeta.execute();
 * }</pre>
 * 
 * The <code>board</code> is a flattened representation of the 10x10 board
 * state. See {@link ubc.cosc322.eval.Util#flatten} for a detailed explanation
 * of this format.
 * 
 * <br /><br />
 * 
 * The <code>move</code> output of a search function is an encoded integer. To
 * get details about the move from this integer, use the methods from the
 * {@link ubc.cosc322.util.Move} class. The most relevant ones are:
 * 
 * <ul>
 * 	<li>{@link ubc.cosc322.util.Move#arrow} to get the position index of 
 * the arrow being fired in this move,</li>
 * 	<li>{@link ubc.cosc322.util.Move#start} to get the starting position
 * of the queen that we want to move, and</li>
 * 	<li>{@link ubc.cosc322.util.Move#end} to get the position we want to 
 * move the queen to.</li>
 * </ul>
 * 
 * <hr />
 * 
 * <h4>See Also</h4>
 * <ul>
 * 	<li>(Wikipedia) <em><a href="https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search">Iterative deepening</a></em>.</li>
 * </ul>
 */
public class AlphaBeta 
	extends TimeConstrained 
	implements SearchMethod 
{
	// The initial board state. I.e., the root of the game tree.
	private State root;

	// The maximizing player's color (i.e., who we want to win).
	private final byte player;

	// The heuristic evaluation method.
	private final HeuristicMethod heuristic;

	private boolean logOutput = false;

	/**
	 * Prepares a minimax search with α-β pruning. Call {@link #setBoard}
	 * to set the board state before calling {@link #search} to find a
	 * promising move.
	 * 
	 * @param heuristic The heuristic method used to assess the quality of a
	 * board state.
	 * @param player The player that we want to win (<code>0</code> for White,
	 * <code>1</code> for black).
	 */
	public AlphaBeta(HeuristicMethod heuristic, byte player) {
		this.heuristic = heuristic;
		this.player = player;
	}

	public void setBoard(long[] empty, byte[] white, byte[] black) {
		this.root = new State(empty, white, black, player);
	}

	public void setBoard(int[] board) {
		final int EMPTY = 0;
		final int WHITE_QUEEN = 1;
		final int BLACK_QUEEN = 2;

		long[] empty = BitBoard.create();
		byte[] white = new byte[4];
		byte[] black = new byte[4];

		int whiteQueensMarked = 0;
		int blackQueensMarked = 0;

		for (byte i = 0; i < 100; i++) {
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

		this.root = new State(empty, white, black, player);
	}

	public void showOutput() {
		logOutput = true;
	}

	public int execute() {
		startTimer();
		return iterativeDeepening();
	}

	private int iterativeDeepening() {
		int bestMove = 0;

		for (int depth = 1; depth < 100; depth++) {
			if (logOutput) {
				System.out.println("searching at depth: " + 
					Integer.toString(depth)
				);				
			}
			try {
				bestMove = bestMove(depth);
				if (logOutput) {
					System.out.println("search complete. deepening.");
				}
			} catch (TimeoutException e) {
				if (logOutput) {
					System.out.println("timeout. search aborted.");
				}
				break;
			}
		}

		return bestMove;
	}

	private int bestMove(int depth) throws TimeoutException {
		double alpha = Double.MIN_VALUE;
		double beta = Double.MAX_VALUE;

		int bestMove = 0;
		double bestScore = Double.MIN_VALUE;

		for (int move : root.moves()) {
			checkTimeOccasionally();

			State child = new State(root, move);

			double score = search(child, depth - 1, alpha, beta);

			if (score > bestScore) {
				bestScore = score;
				bestMove = move;
			}

			alpha = Math.max(alpha, bestScore);

			if (beta <= alpha) {
				break;
			}
		}

		return bestMove;
	}

	private double search(
		State state, int depth, double alpha, double beta
	) throws TimeoutException {
		checkTimeOccasionally();

		if (state.isLeaf() || depth == 0) {
			return state.value(heuristic, player);
		}

		if (state.activePlayer() == player) {
			double value = Double.MIN_VALUE;
			
			for (int move : state.moves()) {
				checkTimeOccasionally();

				State child = new State(state, move);

				value = Math.max(value, search(child, depth - 1, alpha, beta));

				alpha = Math.max(alpha, value);

				if (beta <= alpha) {
					break;
				}
			}

			return value;
		}

		double value = Double.MAX_VALUE;

		for (int move : state.moves()) {
			checkTimeOccasionally();

			State child = new State(state, move);

			value = Math.min(value, search(child, depth - 1, alpha, beta));

			beta = Math.min(beta, value);

			if (beta <= alpha) {
				break;
			}
		}

		return value;
	}
}
