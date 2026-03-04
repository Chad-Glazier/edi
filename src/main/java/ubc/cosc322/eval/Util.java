package ubc.cosc322.eval;

/**
 * Contains general-purpose utility functions for working with the heuristic
 * evaluation functions.
 */
public class Util {
	/**
	 * Converts a board from `int[10][10]` to `int[100]`, which is the format
	 * used by heuristic evaluation functions at the time of writing.
	 * 
	 * As is convention, the <code>int[100]</code> array is row-major. The
	 * examples below demonstrate how to work with such an array.
	 * 
	 * <pre>{@code 
	 * // assume we have a board in `int[100]` form.
	 * int[] board; 
	 * 
	 * // if we want to get the row/column indices from a 0-99 index,
	 * // we can get them like so:
	 * int row = index / 10;
	 * int col = index % 10;
	 * 
	 * // conversely, we can access the row/column of the array like so:
	 * int value = board[row * 10 + col];
	 * }</pre>
	 * 
	 * @param board an `int[10][10]` representation of a board state.
	 * @return an `int[100]` representation of a board. 
	 */
	public static int[] flatten(int[][] board) {
		int[] arr = new int[100];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				arr[i * 10 + j] = board[i][j];
			}
		}

		return arr;
	}
}
