package ubc.cosc322.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitBoardTest {
	@Test
	void testFlagUnflag() {
		long[] bb = BitBoard.create();

		BitBoard.flag(bb, 0);
		assertTrue(BitBoard.flagged(bb, 0));

		BitBoard.unflag(bb, 0);
		assertFalse(BitBoard.flagged(bb, 0));

		assertEquals(bb[0], 0L);
		assertEquals(bb[1], 0L);
	}	

	@Test
	void testMove() {
		long[] bb = BitBoard.create();

		BitBoard.flag(bb, 0);
		long[] moved = BitBoard.move(bb, 0, 23);

		assertFalse(BitBoard.flagged(moved, 0));
		assertTrue(BitBoard.flagged(moved, 23));

		assertNotEquals(bb, moved);
	}
}
