package ubc.team09.eval;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.state.C;
import ubc.team09.state.QGraph;
import ubc.team09.state.State;

public class MinDist implements HeuristicMethod {
	public int evaluate(State state) {

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

		// We count each contested territory as belonging partially to the
		// active player (i.e., the one who makes the next move). We consider
		// the relation to be 1 contested square = 1/5 of an "owned" square.
		// To get this into integer form, we just multiply the territory counts
		// by 5.
		// 
		// This is intended to mitigate odd-even effects and thereby support
		// narrow aspiration windows.

		long[] contested = new long[] {
			visited[0] & ~( whiteTerritory[0] | blackTerritory[0] ),
			visited[1] & ~( whiteTerritory[1] | blackTerritory[1] ),
		};
		int contestedScore = 
			(state.activePlayer == C.WHITE ? +1 : -1) *
			BitBoard.count(contested);

		int whiteTerritoryScore = +5 * BitBoard.count(whiteTerritory);
		int blackTerritoryScore = -5 * BitBoard.count(blackTerritory);

		return whiteTerritoryScore + blackTerritoryScore + contestedScore;
	}

	/**
	 * Performs an evaluation and then normalizes it to a value in the range
	 * of [0, 1]. This should be used in visualizations only, not in any actual
	 * search.
	 */
	public double evaluateAndNormalize(State state) {

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

		// We count each contested territory as belonging partially to the
		// active player (i.e., the one who makes the next move). We consider
		// the relation to be 1 contested square = 1/5 of an "owned" square.
		// To get this into integer form, we just multiply the territory counts
		// by 5.
		// 
		// This is intended to mitigate odd-even effects and thereby support
		// narrow aspiration windows.

		long[] contested = new long[] {
			visited[0] & ~( whiteTerritory[0] | blackTerritory[0] ),
			visited[1] & ~( whiteTerritory[1] | blackTerritory[1] ),
		};
		double contestedScore = 
			(state.activePlayer == C.WHITE ? +1 : -1) *
			(double) BitBoard.count(contested) / 5;

		double whiteTerritoryScore = +1 * BitBoard.count(whiteTerritory);
		double blackTerritoryScore = -1 * BitBoard.count(blackTerritory);

		// Calculate the extreme scores (if all squares were white or black).
		// We subtract 4 from the visited count because each queen counts as
		// "visited", but it's not actually possible for one's territory to
		// include an opponent's queen. Thus, there are four squares that are
		// never obtainable for either side.
		double max = (BitBoard.count(visited) - 4) * +1;
		double min = (BitBoard.count(visited) - 4) * -1;

		// If max - min = 0, then the board state is terminal (the visited node
		// count is 8, only one for each queen) and the loser is whoever the
		// active player is.
		if (BitBoard.count(visited) - 8 == 0) {
			if (state.activePlayer == C.WHITE) {
				return 0; // black wins
			} else {
				return 1; // white wins
			}
		}

		// Get the difference between the actual score and the minimum, then
		// divide it by the width of the interval to get a percentage.
		double normalized = 
			( whiteTerritoryScore 
			+ blackTerritoryScore 
			+ contestedScore
			- min ) / (max - min); 

		return normalized;
	}
}
