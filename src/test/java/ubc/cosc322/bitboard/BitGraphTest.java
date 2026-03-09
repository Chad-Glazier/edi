package ubc.cosc322.bitboard;

import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ubc.cosc322.util.Graph;

public class BitGraphTest {

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

			long[] moves = BitGraph.neighbors(queen, occupied);

			ByteArrayList neighbors = Graph.neighbors(empty, queen);

			for (byte i = 0; i < 100; i++) {
				assertEquals(BitBoard.flagged(moves, i), neighbors.contains(i));
			}
		}
	}

	private final int ITERATIONS = 1000000;

	@Test
	void testPerf() {
		System.out.printf("\n\tRunning comparison of move search functions...\n");

		long totalTimeGraph = 0;

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

			long start = System.currentTimeMillis();
			for (int i = 0; i < ITERATIONS; i++) {
				Graph.neighbors(empty, queen);
			}
			totalTimeGraph += System.currentTimeMillis() - start;
		}

		long totalTimeBitGraph = 0;

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

			long start = System.currentTimeMillis();
			for (int i = 0; i < ITERATIONS; i++) {
				long[] moves = BitGraph.neighbors(queen, occupied);
			}
			totalTimeBitGraph += System.currentTimeMillis() - start;
		}

		System.out.printf("\n\t%d Function calls\n\n\tGraph.neighbors() time:   \t%.2fs\n\tBitGraph.neighbors() time: \t%.2fs\n\n", ITERATIONS * 100, (double) totalTimeGraph / (double) 1000, (double) totalTimeBitGraph / (double) 1000);
	}

	// @Test
	// void testRays() {
	// 	for (byte pos = 0; pos < 100; pos++){
	// 		for (byte dir = 0; dir < 8; dir++) {
	// 			TextDisplay.sideBySideWithLegend(
	// 				P.ray[pos][dir], new byte[0], new byte[0], Byte.toString(pos) + " " + dirToString(dir),
	// 				P.inclusiveRay[pos][dir], new byte[0], new byte[0], "Incl. ray");
	// 		}
	// 	}
	// }

	// private String dirToString(byte direction) {
	// 	switch (direction) {
	// 	case P.N:
	// 		return "North";
	// 	case P.NE:
	// 		return "Northeast";
	// 	case P.E:
	// 		return "East";
	// 	case P.SE:
	// 		return "Southeast";
	// 	case P.S:
	// 		return "South";
	// 	case P.SW:
	// 		return "Southwest";
	// 	case P.W:
	// 		return "West";
	// 	case P.NW:
	// 		return "Northwest";
	// 	default:
	// 		return "";
	// 	}
	// }
}
