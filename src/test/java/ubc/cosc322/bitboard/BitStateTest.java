package ubc.cosc322.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import ubc.cosc322.util.Move;

public class BitStateTest {
	private static final byte WHITE = 0;
	private static final byte BLACK = 1;

	@Test
	void testCorrectness() {
		// Generate a random board state.
		long[] occupancy = BitBoard.create();
		double arrowDensity = 0.3;

		// add arrows
		for (byte i = 0; i < 100; i++) {
			if (Math.random() < arrowDensity) {
				BitBoard.flag(occupancy, i);
			}
		}

		// add queens
		byte[] queens =  new byte[8];
		for (byte i = 0; i < 8; i++) {
			queens[i] = (byte) (Math.random() * 100);
			if (!BitBoard.flagged(occupancy, queens[i])) {
				BitBoard.flag(occupancy, queens[i]);
			}
		}

		// create different board formatting
		long[] empty = BitBoard.notCopy(occupancy);
		byte[] white = Arrays.copyOfRange(queens, 0, 4);
		byte[] black = Arrays.copyOfRange(queens, 4, 8);

		// generate moves with the old, correct method.
		IntArrayList allMoves = Move.getAll(empty, white, WHITE);
		BitState[] allStates = new BitState[allMoves.size()];

		for (int i = 0; i < allMoves.size(); i++) {
			
			long[] newEmpty = BitBoard.copy(empty);
			byte[] newWhite = Arrays.copyOf(white, 4); 
			byte[] newBlack = Arrays.copyOf(black, 4);

			Move.apply(
				newEmpty, 
				newWhite, 
				newBlack, 
				allMoves.getInt(i)
			);

			long[] newOcc = BitBoard.notCopy(newEmpty);
			byte[] newQueens = new byte[] {
				newWhite[0],
				newWhite[1],
				newWhite[2],
				newWhite[3],
				newBlack[0],
				newBlack[1],
				newBlack[2],
				newBlack[3],
			};
			byte activePlayer = BLACK;
			allStates[i] = new BitState(newOcc, newQueens, activePlayer);
		}

		// generate states with the new method.
		BitState initial = new BitState(occupancy, queens, WHITE);
		BitStateGenerator children = initial.children();
		int i = 0;
		BitState[] allBitStates = new BitState[allMoves.size()]; 

		int stateCount = 0;
		for (
			BitState child = children.next(); 
			child != null; 
			child = children.next()
		) {
			stateCount++;

			boolean matchFound = false;
			for (BitState state : allStates) {
				if (child.equals(state)) {
					matchFound = true;
					break;
				}
			}
			assertTrue(matchFound);

			allBitStates[i] = child;
			i++;
		}

		// verify that each state is unique.
		for (BitState state1 : allBitStates) {
			for (BitState state2: allBitStates) {
				if (state1 == state2) {
					continue;
				}
				assertFalse(state1.equals(state2));
			}
		}

		assertEquals(stateCount, allMoves.size());
	}
}
