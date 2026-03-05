package ubc.cosc322.util;

import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {
	@Test
	public void testEncoding() {
		byte start = 24;
		byte end = 54;
		byte arrow = 13;

		int move = Move.encode(start, end, arrow);

		assertEquals(start, Move.start(move));
		assertEquals(end, Move.start(end));
		assertEquals(arrow, Move.start(arrow));
	}

	@Test
	public void testGetAllMoves() {
		long[] allEmpty = BitBoard.create();
		allEmpty[0] = Long.MAX_VALUE;
		allEmpty[1] = Long.MAX_VALUE;

		for (byte i = 0; i < 99; i++) {
			BitBoard.unflag(allEmpty, i);
			IntArrayList moves = Move.getAll(allEmpty, i);
			for (int move : moves) {
				assertEquals(Move.start(move), i);
			}

			// at least one of the moves should end with the queen firing an
			// arrow back to her original position.
			boolean oneHitStart = false;
			for (int move : moves) {
				if (Move.arrow(move) == i) {
					oneHitStart = true;
				}
			}
			assertTrue(oneHitStart);
			BitBoard.flag(allEmpty, i);
		}


	}
}
