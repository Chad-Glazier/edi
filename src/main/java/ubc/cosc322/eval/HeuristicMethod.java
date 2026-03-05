package ubc.cosc322.eval;

public interface HeuristicMethod {
	/**
	 * Sets the board, preparing it for evaluation. Make sure to run this
	 * before {@code evaluate}.
	 * 
	 * The board should contain an <code>int</code> for each square, and its
	 * value should represent one of the following states:
	 * - <code>0</code> for empty spaces,
	 * - <code>1</code> for a white queen,
	 * - <code>2</code> for a black queen, or
	 * - <code>3</code> for a space removed by an arrow.
	 * 
	 * @param board A representation of the boardstate. Row <code>i</code> and
	 * column <code>j</code> should correspond to 
	 * <code>board[i * M + j]</code>, where <code>M</code> is the width of a
	 * row.
	 */
	void setBoard(int[] board);
	/**
	 * Sets the board via a bitboard and the positions of each queen, preparing
	 * it for evaluation. Make sure to run this before {@code evaluate}.
	 * 
	 * @param empty A bitboard where each empty square is flagged.
	 * @param black The position indices of each black queen.
	 * @param white The position indices of each white queen.
	 */
	void setBoard(long[] empty, int[] white, int[] black);

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
