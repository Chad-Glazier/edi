package ubc.cosc322.util;

import java.util.LinkedList;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

/**
 * This class contains a number of static methods meant for analyzing a given
 * board state as a graph.
 */
public class Graph {
	/** The number of rows on the board. */
	private static final int N = 10;
	/** The number of columns on the board. */
	private static final int M = 10;
	/** The number of squares on the board. */
	private static final int SIZE = N * M;

	/**
	 * Creates a list of vertices adjacent to <code>vertex</code> (i.e.,
	 * neighbors).
	 * <br /><br />
	 * Two vertices <code>v</code>, <code>u</code> are defined to be adjacent
	 * if and only if a queen positioned on <code>v</code> could move to
	 * <code>u</code> in a single move, or vice versa.
	 * <br /><br />
	 * 
	 * @param empty A bitboard where each empty square on the game board is
	 * flagged. See {@link BitBoard} for more details.
	 * @param vertex The index of the vertex for which you wish to find
	 * neighbors.
	 * @return A list of all vertices adjacent to <code>vertex</code>
	 */
	public static ByteArrayList neighbors(long[] empty, byte vertex) {

		// TODO: figure out a more performant propagation method, or use the
		// bitboard method

		// TODO: It's possible to implement this with bitboard operations:
		// https://tomcant.dev/posts/2023/05/building-a-chess-ai-part-2-move-generation/
		// This would be much faster, though more memory-intensive.

		final int[] origin = { vertex / M, vertex % M };

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

		final ByteArrayList neighbors = new ByteArrayList();

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
				BitBoard.flagged(empty, (byte) (row * M + col))
			) {
				neighbors.add((byte) (row * M + col));

				row += rowDelta;
				col += colDelta;
			}
		}

		return neighbors;
	}

	/**
	 * Generates and returns a mapping of positions to their distances from
	 * the specified <code>origin</code>.
	 * <br /><br />
	 * The distance between two vertices <code>v</code>, <code>u</code> is
	 * defined as the minimum number of moves it would take for a queen to get
	 * from <code>v</code> to <code>u</code>, or vice versa.
	 * <br /><br />
	 * If no path exists from the <code>origin</code> to a given square, the
	 * distance is marked as <code>100</code>.
	 * 
	 * @param empty A bitboard where each empty square on the game board is
	 * flagged. See {@link BitBoard} for more details. 
	 * @param origin The starting position (i.e., where the distance will be 
	 * <code>0</code>).
	 * @return An array of bytes, <code>d</code>, such that if there is a
	 * distance of <code>x</code> between the <code>origin</code> and another
	 * vertex <code>p</code>, then <code>d[p] == x</code>.
	 */
	public static byte[] distance(long[] empty, byte origin) {
		
		// This function is implemented with a slightly modified breadth-
		// first search.

		final long[] visited = BitBoard.create();
		final byte[] distance = new byte[SIZE];
		for (int i = 0; i < SIZE; i++) {
			distance[i] = 100;
		}

		final LinkedList<Byte> queue = new LinkedList<Byte>(); 
		queue.push(origin);
		BitBoard.flag(visited, origin);
		distance[origin] = 0;

		while(!queue.isEmpty()) {
			byte current = queue.pop();
			
			for (byte neighbor : neighbors(empty, current)) {
				if (BitBoard.flagged(visited, neighbor)) {

					// Since this is a graph, not a tree, it's possible that 
					// the first time we visit a square is not via the shortest
					// path. Thus, we should retroactively check visited 
					// neighbors to see if going there from the current square,
					// or vice versa, is faster than the previous path.

					if (distance[neighbor] + 1 < distance[current]) {
						distance[current] = (byte) (distance[neighbor] + 1);
					} else if (distance[neighbor] > distance[current] + 1) {
						distance[neighbor] = (byte) (distance[current] + 1);
					}
					continue;
				}

				BitBoard.flag(visited, neighbor);
				distance[neighbor] = (byte) (distance[current] + 1);
				queue.push(neighbor);				
			}
		}
		
		return distance;
	}
}
