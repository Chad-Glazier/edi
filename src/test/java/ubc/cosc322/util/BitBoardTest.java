package ubc.cosc322.util;

import org.junit.jupiter.api.Test;

import ubc.cosc322.bitboard.BitBoard;

import static org.junit.jupiter.api.Assertions.*;

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
}
