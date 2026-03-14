package ubc.cosc322.state;

import java.util.Arrays;

import ubc.cosc322.bitboard.BitBoard;

public class StateGenerator {
	private final static byte WHITE = 0;
	private final static byte BLACK = 1;

	private final State parent;

	private int queenIdx;
	private long[] arrows;
	private long[] destinations;
	private final long[] occupancy;
	private byte destination = -1;

	public StateGenerator(State parent) {
		this.parent = parent;
		queenIdx = parent.activePlayer * 4;

		destinations = QGraph.neighbors(
			parent.queens[queenIdx], parent.occupancy
		);
		destination = BitBoard.poll(destinations);
		occupancy = BitBoard.moveCopy(
			parent.occupancy, 
			parent.queens[queenIdx], 
			destination
		);

		if (destination == -1) {
			this.arrows = new long[2];
		} else {
			arrows = QGraph.neighbors(destination, occupancy);
		}
	}

	public State next() {
		// TODO: Optimize by replacing `poll`. Right now, `poll` will scan
		// both `long`s in a bitboard if needed, which leads to unnecessary
		// operations if we already know that the `lo` half of the bitboard
		// has been exhausted.

		// try to get the next arrow
		byte arrow = BitBoard.poll(arrows);
		
		while (arrow == -1) {

			// try to get the next destination
			destination = BitBoard.poll(destinations);
			while (destination == -1) {

				// try to get the next queen
				if (queenIdx >= parent.activePlayer * 4 + 3) {
					return null;
				}

				queenIdx++;
				destinations = QGraph.neighbors(
					parent.queens[queenIdx], parent.occupancy
				);

				destination = BitBoard.poll(destinations);
			}

			BitBoard.move(
				parent.occupancy, 
				parent.queens[queenIdx], 
				destination, 
				occupancy
			);

			arrows = QGraph.neighbors(destination, occupancy);
			arrow = BitBoard.poll(arrows);
		}

		State child = new State(
			BitBoard.flagCopy(occupancy, arrow),
			Arrays.copyOf(parent.queens, 8),
			parent.activePlayer == WHITE ? BLACK : WHITE,
			Move.encode(
				parent.queens[queenIdx], 
				destination, 
				arrow, 
				parent.activePlayer
			)
		);
		child.queens[queenIdx] = destination;
		
		return child;
	}
}
