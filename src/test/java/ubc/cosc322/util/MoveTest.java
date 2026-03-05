package ubc.cosc322.util;

import org.junit.jupiter.api.Test;
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
}
