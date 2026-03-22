package ubc.team09.eval;

import ubc.team09.state.State;

public interface HeuristicMethod {
	/**
	 * Returns a number in the range of (-∞, +∞) where lower values indicate
	 * a more favorable position for Black, and greater values indicate a more
	 * favorable position for White.
	 * 
	 * @param state The board state to evaluate.
	 */
	public int evaluate(State state);
}
