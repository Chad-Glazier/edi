package ubc.cosc322.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {
	@Test
	public void testEncoding() {
		byte start = 24;
		byte end = 54;
		byte arrow = 13;
		byte player = 0;

		int move = Move.encode(start, end, arrow, player);

		assertEquals(start, Move.start(move));
		assertEquals(end, Move.start(end));
		assertEquals(arrow, Move.start(arrow));
		assertEquals(player, Move.player(move));
	}

}