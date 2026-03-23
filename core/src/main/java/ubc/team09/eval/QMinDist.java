package ubc.team09.eval;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.state.QGraph;
import ubc.team09.state.State;

public class QMinDist implements HeuristicMethod {
	public double evaluate(State state) {

		// Store the territory belonging to each color.

		long[] whiteTerritory = BitBoard.create();
		long[] blackTerritory = BitBoard.create();

		// Track which squares have already been visited.

		long[] visited = BitBoard.create();

		// Track the "frontier"; i.e., the squares that each territory is
		// expanding into and hasn't already visited.

		long[] whiteFrontier = BitBoard.create();
		long[] blackFrontier = BitBoard.create();

		// The frontiers each start simply with each side's queens. These are
		// the zero-distance squares. Since two queens can't sit on the same
		// square, we can also add these to their respective territories.

		for (int i = 0; i < 4; i++) {
			BitBoard.flag(whiteFrontier, state.queens[i]);
			BitBoard.flag(visited, state.queens[i]);
			BitBoard.flag(whiteTerritory, state.queens[i]);
		}

		for (int i = 4; i < 8; i++) {
			BitBoard.flag(blackFrontier, state.queens[i]);
			BitBoard.flag(visited, state.queens[i]);
			BitBoard.flag(blackTerritory, state.queens[i]);
		}

		// Now, the frontiers are expanded incrementally, until there is no
		// accessible territory that hasn't been explored.

		while (!BitBoard.isEmpty(blackFrontier)
				|| !BitBoard.isEmpty(whiteFrontier)) {

			// First, we expand the frontiers, omitting any previously explored
			// territory.

			long[] tmp;

			tmp = QGraph.neighbors(whiteFrontier, state.occupancy);
			whiteFrontier[0] = tmp[0] & ~visited[0];
			whiteFrontier[1] = tmp[1] & ~visited[1];

			tmp = QGraph.neighbors(blackFrontier, state.occupancy);
			blackFrontier[0] = tmp[0] & ~visited[0];
			blackFrontier[1] = tmp[1] & ~visited[1];

			// Next, we let white and black claim their respective territory.
			// Any new territory on the black frontier that is neither on the
			// white territory nor the white frontier is claimed for black, and
			// vice versa.

			whiteTerritory[0] |= whiteFrontier[0] & ~(blackFrontier[0] | blackTerritory[0]);
			whiteTerritory[1] |= whiteFrontier[1] & ~(blackFrontier[1] | blackTerritory[1]);

			blackTerritory[0] |= blackFrontier[0] & ~(whiteFrontier[0] | whiteTerritory[0]);
			blackTerritory[1] |= blackFrontier[1] & ~(whiteFrontier[1] | whiteTerritory[1]);

			// Finally, update the "visited" board to reflect that the new
			// frontiers have been explored.

			visited[0] |= whiteFrontier[0];
			visited[1] |= whiteFrontier[1];

			visited[0] |= blackFrontier[0];
			visited[1] |= blackFrontier[1];
		}

		return (double) (BitBoard.count(whiteTerritory) - BitBoard.count(blackTerritory))
				/
				(double) (BitBoard.count(visited) - 7);
	}
}
