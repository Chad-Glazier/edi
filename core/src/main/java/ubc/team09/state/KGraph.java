package ubc.team09.state;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.bitboard.P;

/**
 * This class includes operations that model the board state as a graph, where
 * <br />
 * - vertices are board positions, and <br />
 * - two vertices <code>v</code> and <code>u</code> are adjacent if and only
 * if a king could move from one to the other in a single move.<br />
 * <br />
 * The king-move definition of edges is the reason for the "K" in the name.
 * 
 * @see {@link QGraph}
 */
public class KGraph {
	public static long[] neighbors(byte position, long[] occupancy) {
		long[] adjacentSquares = P.kingAttack[position];
		long[] neighbors = {
			adjacentSquares[0] & ~occupancy[0],
			adjacentSquares[1] & ~occupancy[1]
		};

		return neighbors;
	}

	public static long[] neighbors(
		long[] originalPositions,
		long[] occupancy
	) {
		long[] positions = BitBoard.copy(originalPositions);
		long[] domain = BitBoard.create();

		for (
			byte position = BitBoard.poll(positions); 
			position != -1; 
			position = BitBoard.poll(positions)
		) {
			long[] adjacent = neighbors(position, occupancy);

			domain[0] |= adjacent[0];
			domain[1] |= adjacent[1];
		}

		return domain;
	}
}
