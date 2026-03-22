package ubc.team09.state;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.bitboard.P;

/**
 * This class includes operations that model the board state as a graph, where
 * <br />
 * - vertices are board positions, and <br />
 * - two vertices <code>v</code> and <code>u</code> are adjacent if and only
 * if a queen could move from one to the other in a single move.<br />
 * <br />
 * The queen-move definition of edges is the reason for the "Q" in the name.
 * 
 * @see {@link KGraph}
 */
public class QGraph {
	public static final byte UNREACHABLE = 100;

	public static long[] neighbors(byte position, long[] occupancy) {

		// We divide the 8 directions into two groups:
		// - "forward" directions are those where the bits most distant from
		// the origin is the least-significant bit.
		// - "reverse" directions are those where the bits most distant from
		// the origin is the most-significant bit.
		//
		// Because of the way that directions are enumerated, we know that if
		// a direction is less than or equal to P.NE (3), then it is a forward
		// direction. Otherwise, it is a reverse direction.

		final long[] occupied = BitBoard.unflagCopy(occupancy, position);

		final long[] moves = new long[2];

		final long[][] rays = P.ray[position];

		long[] ray;
		byte nearestBlocker;
		long[] blockedSegment;

		//
		// FORWARD DIRECTION -- West
		//

		ray = rays[P.W];
		nearestBlocker = BitBoard.msb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.W];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// FORWARD DIRECTION -- Northwest
		//

		ray = rays[P.NW];
		nearestBlocker = BitBoard.msb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.NW];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// FORWARD DIRECTION -- North
		//

		ray = rays[P.N];
		nearestBlocker = BitBoard.msb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.N];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// FORWARD DIRECTION -- Northeast
		//

		ray = rays[P.NE];
		nearestBlocker = BitBoard.msb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.NE];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// REVERSE DIRECTION -- East
		//

		ray = rays[P.E];
		nearestBlocker = BitBoard.lsb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.E];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// REVERSE DIRECTION -- Southeast
		//

		ray = rays[P.SE];
		nearestBlocker = BitBoard.lsb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.SE];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// REVERSE DIRECTION -- South
		//

		ray = rays[P.S];
		nearestBlocker = BitBoard.lsb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.S];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		//
		// REVERSE DIRECTION -- Southwest
		//

		ray = rays[P.SW];
		nearestBlocker = BitBoard.lsb(new long[] {
				ray[0] & occupied[0],
				ray[1] & occupied[1]
		});
		blockedSegment = P.inclusiveRay[nearestBlocker][P.SW];
		moves[0] |= ray[0] ^ blockedSegment[0];
		moves[1] |= ray[1] ^ blockedSegment[1];

		return moves;
	}

	/**
	 * Given a set of origins (on a bitboard) and an occupancy board, this
	 * returns a board where each position is flagged if and only if it is
	 * adjacent to any one of the origins.
	 * 
	 * @param originalPositions A bitboard with each possible origin flagged.
	 * @param occupancy         A bitboard with each occupied square flagged.
	 */
	public static long[] neighbors(
		long[] originalPositions, long[] occupancy
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

	/**
	 * Given an origin, returns the distance from that point to each other
	 * point on the board. If the point is unreachable, then it is given a
	 * distance of <code>100</code>.
	 */
	public static byte[] distance(byte origin, long[] occupancy) {
		
		byte[] distances = new byte[100];
		for (int i = 0; i < 100; i++) {
			distances[i] = UNREACHABLE;
		}

		long[] visited = BitBoard.create();
		long[] frontier = BitBoard.create();
		BitBoard.flag(frontier, origin);
		BitBoard.flag(visited, origin);
		distances[origin] = 0;

		for (byte d = 1; !BitBoard.isEmpty(frontier); d++) {

			long[] tmp = neighbors(frontier, occupancy);
			frontier[0] = tmp[0] & ~visited[0];
			frontier[1] = tmp[1] & ~visited[1];

			// update distances
			long[] newFrontier = BitBoard.copy(frontier);
			while (!BitBoard.isEmpty(newFrontier)) {
				distances[BitBoard.poll(newFrontier)] = d;
			}

			// update visited
			visited[0] |= frontier[0];
			visited[1] |= frontier[1];
		}

		return distances;
	}
}
