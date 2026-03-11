package ubc.cosc322.bitboard;

import java.util.Arrays;

/**
 * Represents a board state using bitboards. Each instance uses 200 bits plus
 * any overhead that the JVM uses.
 */
class BitState {
	public long[] occupancy = new long[2];
	public byte[] queens = new byte[8];
	public byte activePlayer;

	public BitState() {}
	public BitState(long[] occupancy, byte[] queens, byte activePlayer) {
		this.activePlayer = activePlayer;
		this.occupancy[0] = occupancy[0];
		this.occupancy[1] = occupancy[1];	
		this.queens[0] = queens[0];
		this.queens[1] = queens[1];
		this.queens[2] = queens[2];
		this.queens[3] = queens[3];
		this.queens[4] = queens[4];
		this.queens[5] = queens[5];
		this.queens[6] = queens[6];
		this.queens[7] = queens[7];
	}

	public BitStateGenerator children() {
		return new BitStateGenerator(this);
	}

	public boolean equals(BitState other) {
		byte[] thisWhite = Arrays.copyOfRange(queens, 0, 4);
		byte[] otherWhite = Arrays.copyOfRange(other.queens, 0, 4);
		byte[] thisBlack = Arrays.copyOfRange(queens, 4, 8);
		byte[] otherBlack = Arrays.copyOfRange(other.queens, 4, 8);

		for (byte thisQueen : thisWhite) {
			boolean anyMatch = false;
			for (byte otherQueen : otherWhite) {
				if (thisQueen == otherQueen) {
					anyMatch = true;
					break;
				}
			}
			if (!anyMatch) {
				return false;
			}
		}

		for (byte thisQueen : thisBlack) {
			boolean anyMatch = false;
			for (byte otherQueen : otherBlack) {
				if (thisQueen == otherQueen) {
					anyMatch = true;
					break;
				}
			}
			if (!anyMatch) {
				return false;
			}
		}

		return 
			activePlayer == other.activePlayer &&
			occupancy[0] == other.occupancy[0] &&
			occupancy[1] == other.occupancy[1];
	}
}
