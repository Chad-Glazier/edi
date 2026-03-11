package ubc.cosc322.bitboard;

class BitStateGenerator {
	private final static byte WHITE = 0;
	private final static byte BLACK = 1;

	private final BitState parent;

	private int queenIdx;
	private long[] arrows;
	private long[] destinations;
	private final long[] occupancy;
	private byte destination = -1;

	public BitStateGenerator(BitState parent) {
		this.parent = parent;
		queenIdx = parent.activePlayer * 4;

		destinations = BitGraph.neighbors(
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
			arrows = BitGraph.neighbors(destination, occupancy);
		}
	}

	public BitState next() {
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
				destinations = BitGraph.neighbors(
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

			arrows = BitGraph.neighbors(destination, occupancy);
			arrow = BitBoard.poll(arrows);
		}

		BitState child = new BitState();
		child.activePlayer = parent.activePlayer == WHITE ? BLACK : WHITE;
		child.occupancy = BitBoard.flagCopy(occupancy, arrow);
		child.queens = new byte[] {
			parent.queens[0],
			parent.queens[1],
			parent.queens[2],
			parent.queens[3],
			parent.queens[4],
			parent.queens[5],
			parent.queens[6],
			parent.queens[7],
		};
		child.queens[queenIdx] = destination;
		
		return child;
	}
}
