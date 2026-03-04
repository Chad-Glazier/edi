package ubc.cosc322.eval;

interface HeuristicMethod {
	/**
	 * Sets the board, preparing it for evaluation. Make sure to run this
	 * before {@code evaluate}.
	 * 
	 * @param board A representation of the boardstate. Row <code>i</code> and
	 * column <code>j</code> should correspond to 
	 * <code>board[i * M + j]</code>, where <code>M</code> is the width of a
	 * row.
	 */
	void setBoard(int[] board);

	/**
	 * Evaluates the board state. Make sure that you run {@code setBoard}
	 * before this function.
	 * 
	 * @param playerIsWhite a flag indicating whether to return the score for
	 * white (otherwise it returns the score for black).
	 * @return a number from 0 to 1, representing the favorability score based
	 * of the board state.
	 */
	double evaluate(boolean playerIsWhite);

	/**
	 * Displays some kind of visualization of the heuristic analysis. The kind
	 * of visualization can vary based on the method; simpler methods may
	 * print the board, while others might just be a set of numbers.
	 */
	void visualize();
}
