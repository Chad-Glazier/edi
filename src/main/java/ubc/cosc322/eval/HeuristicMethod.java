package ubc.cosc322.eval;

import ubc.cosc322.state.State;

public interface HeuristicMethod {
	/**
	 * Returns a number in the range of [-1, +1] where lower values indicate
	 * a more favorable position for Black, and greater values indicate a more
	 * favorable position for White.
	 * 
	 * @param state The board state to evaluate.
	 */
	public double evaluate(State state);	
}
