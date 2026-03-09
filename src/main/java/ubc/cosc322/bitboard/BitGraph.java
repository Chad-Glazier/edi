package ubc.cosc322.bitboard;

public class BitGraph {
	public static long[] neighbors(byte position, long[] occupied) {

		// We divide the 8 directions into two groups:
		// - "forward" directions are those where the bits most distant from
		//   the origin is the least-significant bit.
		// - "reverse" directions are those where the bits most distant from
		//   the origin is the most-significant bit.
		//
		// Because of the way that directions are enumerated, we know that if
		// a direction is less than or equal to P.NE (3), then it is a forward
		// direction. Otherwise, it is a reverse direction.

		BitBoard.unflag(occupied, position);

		final long[] moves = new long[2];

		long[] ray;
		byte nearestBlocker;
		long[] blockedSegment;

		// 
		// FORWARD DIRECTION -- West
		//

		ray = P.ray[position][P.W];
		nearestBlocker = BitBoard.msb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.W];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// FORWARD DIRECTION -- Northwest
		//
		
		ray = P.ray[position][P.NW];
		nearestBlocker = BitBoard.msb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.NW];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// FORWARD DIRECTION -- North
		//
		
		ray = P.ray[position][P.N];
		nearestBlocker = BitBoard.msb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.N];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// FORWARD DIRECTION -- Northeast
		//
		
		ray = P.ray[position][P.NE];
		nearestBlocker = BitBoard.msb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.NE];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// REVERSE DIRECTION -- East
		//
		
		ray = P.ray[position][P.E];
		nearestBlocker = BitBoard.lsb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.E];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// REVERSE DIRECTION -- Southeast
		//
		
		ray = P.ray[position][P.SE];
		nearestBlocker = BitBoard.lsb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.SE];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// REVERSE DIRECTION -- South
		//
		
		ray = P.ray[position][P.S];
		nearestBlocker = BitBoard.lsb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.S];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		// 
		// REVERSE DIRECTION -- Southwest
		//
		
		ray = P.ray[position][P.SW];
		nearestBlocker = BitBoard.lsb(new long[]{ 
			ray[0] & occupied[0],
			ray[1] & occupied[1]
		});
		if (nearestBlocker == -1) {
			moves[0] |= ray[0];
			moves[1] |= ray[1];
		} else {
			blockedSegment = P.inclusiveRay[nearestBlocker][P.SW];
			moves[0] |= ray[0] ^ blockedSegment[0];
			moves[1] |= ray[1] ^ blockedSegment[1];
		}

		return moves;
	}
}
