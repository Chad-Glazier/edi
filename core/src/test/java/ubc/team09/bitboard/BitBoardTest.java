package ubc.team09.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BitBoardTest {
	@Test
	void testFlagUnflag() {
		long[] bb = BitBoard.create();

		byte pos = 0;

		BitBoard.flag(bb, pos);
		assertTrue(BitBoard.flagged(bb, pos));

		BitBoard.unflag(bb, pos);
		assertFalse(BitBoard.flagged(bb, pos));

		assertEquals(bb[0], 0L);
		assertEquals(bb[1], 0L);
	}

	@Test
	void testMove() {
		long[] bb = BitBoard.create();

		byte src = 0;
		byte dst = 23;

		BitBoard.flag(bb, src);
		long[] moved = BitBoard.moveCopy(bb, src, dst);

		assertFalse(BitBoard.flagged(moved, src));
		assertTrue(BitBoard.flagged(moved, dst));

		assertNotEquals(bb, moved);
	}

	@Test
	void testNoLeadingZeros() {
		long[] b = BitBoard.create();

		for (byte i = 0; i < 100; i++) {
			if (Math.random() > 0.5) {
				BitBoard.flag(b, i);
			}
		}

		final long mask = ~((1L << 36) - 1);

		assertEquals(mask & b[1], 0);

		BitBoard.not(b);
		assertTrue(BitBoard.lsb(b) >= 0);
		assertTrue(BitBoard.msb(b) < 100);
	}

	@Test
	void testCount() {
		for (int i = 0; i < 100; i++) {
			long[] board = BitBoard.create();
			int flagCount = 0;
			double flagDensity = Math.random();
			for (byte j = 0; j < 100; j++) {
				if (Math.random() < flagDensity) {
					BitBoard.flag(board, j);
					flagCount++;
				}
			}
			assertEquals(flagCount, BitBoard.count(board));
		}
	}
}
