package ubc.cosc322.eval;

import java.util.LinkedList;

public class MinDist implements HeuristicMethod {
	/** The number of rows on the board. */
	private static final int N = 10;
	/** The number of columns on the board. */
	private static final int M = 10;
	/** The number of squares on the board. */
	private static final int SIZE = N * M;
	/** The number of queens per player. */
	private static final int QUEENS = 4;

	private static final int EMPTY = 0;
	private static final int WHITE_QUEEN = 1;
	private static final int BLACK_QUEEN = 2;

	/**
	 * Bitboard where there is a <code>1</code> if and only if the square is
	 * empty.
	 */
	private final long[] empty = new long[SIZE / 64 + 1];
	/**
	 * Stores the indices of each white queen.
	 */
	private final int[] white = new int[QUEENS];
	/**
	 * Stores the indices of each black queen.
	 */
	private final int[] black = new int[QUEENS];

	/**
	 * Returns <code>true</code> if and only if the indexed bit is 
	 * <code>1</code> in the <code>empty</code> bitboard.
	 */
	private boolean isEmpty(int index) {
		return (empty[index >>> 6] & (1L << (index & 63))) != 0;
	}

	/**
	 * Sets the indexed bit to <code>1</code> on the <code>empty</code> 
	 * bitboard.
	 */
	private void setEmpty(int index) {
		empty[index >>> 6] |= (1L << (index & 63));
	}

	public MinDist() {}

	public void setBoard(int[] board) {
		int whiteQueensMarked = 0;
		int blackQueensMarked = 0;

		for (int i = 0; i < SIZE; i++) {
			switch (board[i]) {
			case EMPTY:
				setEmpty(i);
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
	}

	/**
	 * For a given position index, returns a list of adjacent positions. Two
	 * positions <code>a</code> and <code>b</code> are adjacent if and only if
	 * a queen on <code>a</code> could move to <code>b</code> (or vice versa) in
	 * a single move.
	 * 
	 * TODO: implement memoization.
	 */
	public LinkedList<Integer> getNeighbors(int position) {
		
		final int[] origin = { position / M, position % M };

		final int[][] directions = {
			{ -1, -1 },
			{ -1,  0 },
			{ -1,  1 },

			{  0, -1 },
		//  {  0,  0 },	
			{  0,  1 },
			
			{  1, -1 },
			{  1,  0 },
			{  1,  1 }
		};

		final LinkedList<Integer> neighbors = new LinkedList<Integer>();

		for (int[] delta : directions) {
			int row = origin[0];
			int col = origin[1];
			int rowDelta = delta[0];
			int colDelta = delta[1];

			row += rowDelta;
			col += colDelta;

			while (
				row < N && row >= 0 &&
				col < M && col >= 0 &&
				isEmpty(row * M + col)
			) {
				neighbors.push(row * M + col);

				row += rowDelta;
				col += colDelta;
			}
		}

		return neighbors;
	}

	/**
	 * Generates a distance board from the given origin. I.e., a board where
	 * each square has a value equal to its distance from the origin.
	 * 
	 * The distance between two positions is defined as the minimum number of
	 * moves needed to get from one to the other, while accounting for any
	 * obstacles. If there is no possible path between the two squares, then
	 * the distance is <code>1000</code>. I.e., each obstacle will always have
	 * that distance.
	 * 
	 * TODO: Prove whether or not the double-check in the BFS is necessary. 
	 */
	public int[] distanceBoard(int origin) {

		// This function implements a breadth-first search to calculate the
		// distances.

		final boolean[] visited = new boolean[SIZE];
		final int[] distance = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			distance[i] = 1000;
		}

		final LinkedList<Integer> queue = new LinkedList<Integer>(); 
		queue.push(origin);
		visited[origin] = true;
		distance[origin] = 0;

		while(!queue.isEmpty()) {
			int current = queue.pop();
			
			getNeighbors(current).forEach(neighbor -> {
				if (visited[neighbor]) {

					// Since this is a graph, not a tree, it's possible that 
					// the first time we visit a square is not via the shortest
					// path. Thus, we should retroactively check visited 
					// neighbors to see if going there from the current square,
					// or vice versa, is faster than the previous path.

					if (distance[neighbor] + 1 < distance[current]) {
						distance[current] = distance[neighbor] + 1;
					} else if (distance[neighbor] > distance[current] + 1) {
						distance[neighbor] = distance[current] + 1;
					}
					return;
				}

				visited[neighbor] = true;
				distance[neighbor] = distance[current] + 1;
				queue.push(neighbor);
			});
		}
		
		return distance;
	}

	public double evaluate(boolean playerIsWhite) {
		int[] blackMinDistBoard = distanceBoard(black[0]);
		int[] whiteMinDistBoard = distanceBoard(white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			int[] newBlackDistBoard = distanceBoard(black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			int[] newWhiteDistBoard = distanceBoard(white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = 100;
		for (int i = 0; i < SIZE; i++) {
			if (!isEmpty(i)) {
				availableSquares--;
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				continue;
			}
		}

		if (playerIsWhite) {
			return ((double) (whiteSquares - blackSquares)) / ((double) 2 * availableSquares) + 0.50;
		} else {
			return ((double) (blackSquares - whiteSquares)) / ((double) 2 * availableSquares) + 0.50;
		}
	}

	public void visualize() {
		int[] blackMinDistBoard = distanceBoard(black[0]);
		int[] whiteMinDistBoard = distanceBoard(white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			int[] newBlackDistBoard = distanceBoard(black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			int[] newWhiteDistBoard = distanceBoard(white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		String[] boardDisplay = new String[SIZE];

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = SIZE;
		int contestedSquares = 0;
		for (int i = 0; i < SIZE; i++) {
			if (!isEmpty(i)) {
				availableSquares--;
				// boardDisplay[i] = "\u21A1";
				boardDisplay[i] = "X";
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				// boardDisplay[i] = "\u25A0";
				boardDisplay[i] = "b";
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				// boardDisplay[i] = "\u25A1";
				boardDisplay[i] = "w";
				continue;
			}
			// boardDisplay[i] = "\u2591";
			boardDisplay[i] = "?";
			contestedSquares++;
		}

		for (int queen : white) {
			// boardDisplay[queen] = Character.toString(0x1FA0A);
			boardDisplay[queen] = "W";
		}

		for (int queen : black) {
			// boardDisplay[queen] = Character.toString(0x1FA3A);
			boardDisplay[queen] = "B";
		}

		for (int i = 0; i < SIZE; i++) {
			if (i % M == 0) {
				System.out.print("\n\t");
			}
			System.out.print(boardDisplay[i] + " ");
			if (i % M == M - 1) {
				switch (i / M) {
				case 0:
					System.out.print("\tLegend");
					break;
				case 1: 
					System.out.print("\t-----------------------");
					break;
				case 2:
					System.out.print("\tX: arrow-struck square");
					break;
				case 3:
					System.out.print("\tW: white queen");
					break;
				case 4:
					System.out.print("\tB: black queen");
					break;
				case 5:
					System.out.print("\tw: white territory");
					break;
				case 6:
					System.out.print("\tb: black territory");
					break;
				case 7:
					System.out.print("\tc: contested territory");
					break;
				}
			}
		}
		System.out.print("\n\n");
		System.out.printf("\tWhite squares:      %d (%.0f%%)\n", 
			whiteSquares,
			100 * (whiteSquares / (double) availableSquares)
		);
		System.out.printf("\tBlack squares:      %d (%.0f%%)\n", 
			blackSquares,
			100 * (blackSquares / (double) availableSquares)
		);
		System.out.printf("\tContested squares:  %d (%.0f%%)\n", 
			contestedSquares,
			100 * (contestedSquares / (double) availableSquares)
		);
		System.out.println();
	}

	/**
	 * Prints a distance board originating from the specified position. This is
	 * just useful for debugging.
	 */
	public void printDistanceBoard(int origin) {
		int[] distBoard = distanceBoard(origin);

		for (int i = 0; i < 100; i++) {
			if (i % 10 == 0) {
				System.out.print("\n\t");
			}

			String character = Integer.toString(distBoard[i]);
			switch (distBoard[i]) {
			case 1000:
				character = "∞";
				break;
			}
			System.out.print(character + " ");
		}
		System.out.println();
		System.out.println();
	} 
}
