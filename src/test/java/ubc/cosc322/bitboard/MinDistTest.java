package ubc.cosc322.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ubc.cosc322.eval.MinDist;

public class MinDistTest {
	@Test
	void testCorrectness() {
		for (int i = 0; i < 2; i++) {
			// Generate a random board state.
			long[] occupancy = BitBoard.create();
			double arrowDensity = 0.3;

			// add arrows
			for (byte j = 0; j < 100; j++) {
				if (Math.random() < arrowDensity) {
					BitBoard.flag(occupancy, j);
				}
			}

			// add queens
			byte[] queens =  new byte[8];
			for (byte j = 0; j < 8; j++) {
				queens[j] = (byte) (Math.random() * 100);
				if (!BitBoard.flagged(occupancy, queens[j])) {
					BitBoard.flag(occupancy, queens[j]);
				}
			}

			// create different board formatting
			long[] empty = BitBoard.notCopy(occupancy);
			byte[] white = Arrays.copyOfRange(queens, 0, 4);
			byte[] black = Arrays.copyOfRange(queens, 4, 8);			

			// evaluate with old method.
			MinDist minDist = new MinDist();
			minDist.setBoard(empty, white, black);
			int score = (int) minDist.evaluate((byte) 0);

			// evaluate with new method.
			BitMinDist bitMinDist = new BitMinDist(
				new BitState(occupancy, queens, (byte) 0)
			);
			int bitScore = bitMinDist.evaluate();

			assertEquals(score, bitScore);
		}
	}
}
