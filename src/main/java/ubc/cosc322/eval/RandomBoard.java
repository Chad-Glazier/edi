package ubc.cosc322.eval;

import java.util.HashSet;

class RandomBoard {
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
	private static final int ARROW = 3;

	public int[] arr = new int[100];

	public RandomBoard(double arrowDensity) {
		arr = new int[SIZE];

		for (int i = 0; i < SIZE; i++) {
			if (Math.random() > arrowDensity) {
				arr[i] = EMPTY;
			} else {
				arr[i] = ARROW;
			}
		}

		HashSet<Integer> queenPositions = new HashSet<Integer>();
		while (queenPositions.size() < QUEENS * 2) {
			queenPositions.add(randomPosition());
		}
		
		int whiteQueensPlaced = 0;
		for (int i : queenPositions) {
			if (whiteQueensPlaced < QUEENS) {
				arr[i] = WHITE_QUEEN;
				whiteQueensPlaced++;
			} else {
				arr[i] = BLACK_QUEEN;
			}
		}
	}

	private int randomPosition() {
		Double random = Math.random() * SIZE;
		return random.intValue();
	}

	public void print() {
		String[] displayBoard = new String[SIZE];

		for (int i = 0; i < SIZE; i++) {
			switch (arr[i]) {
			case EMPTY:
				displayBoard[i] = "_";
				break;
			case ARROW:
				displayBoard[i] = "X";
				break;
			case WHITE_QUEEN:
				displayBoard[i] = "W";
				break;
			case BLACK_QUEEN:
				displayBoard[i] = "B";
				break;
			}
		}

		for (int i = 0; i < SIZE; i++) {
			if (i % M == 0) {
				System.out.print("\n\t");
			}
			System.out.print(displayBoard[i] + " ");
		}

		System.out.println();
		System.out.println();
	}
}
