package ubc.cosc322.view;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.misc.C;
import ubc.cosc322.state.State;

public class Util {
    /**
     * Generates the starting initial Amazons board state.
     */
    public static State initialBoard() {

        byte[] queens = new byte[8];

		// White queens
        queens[0] = 60;
        queens[1] = 93;
        queens[2] = 96;
        queens[3] = 69;

        // Black queens
        queens[4] = 30;
        queens[5] = 03;
        queens[6] = 06;
        queens[7] = 39;

        // Flag each queen's position.
        long[] occupancy = BitBoard.create();
        for (byte queen : queens) {
			BitBoard.flag(occupancy, queen);
        }

        return new State(
			occupancy, 
			queens, 
			C.WHITE, 
			0
		);
    }
}
