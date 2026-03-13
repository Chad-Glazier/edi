package ubc.cosc322.bitboard;

import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ubc.cosc322.util.Graph;

public class QGraphTest {
	@Test
	void testCorrectness() {
		for (byte queen = 0; queen < 100; queen++) {
			long[] empty = BitBoard.create();

			for (byte i = 0; i < 100; i++) {
				if (Math.random() > 0.3) {
					BitBoard.flag(empty, i);
				}
			}

			if (BitBoard.flagged(empty, queen)) {
				BitBoard.unflag(empty, queen);
			}

			long[] occupied = BitBoard.notCopy(empty);

			long[] moves = QGraph.neighbors(queen, occupied);

			ByteArrayList neighbors = Graph.neighbors(empty, queen);

			for (byte i = 0; i < 100; i++) {
				assertEquals(BitBoard.flagged(moves, i), neighbors.contains(i));
			}
		}
	}

	
}
