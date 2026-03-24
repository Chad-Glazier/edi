package ubc.team09.player;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

public class EDI implements VI {
	/** The turn at which the algorithm shifts from kmindist to qmindist. */
	private final static int INFLECTION_POINT = 55;

	private final AlphaBeta ab;
	private final KMinDist kmindist = new KMinDist();
	private final QMinDist qmindist = new QMinDist();

	public EDI(
		State state,
		byte color,
		int timeLimit
	) {
		ab = new AlphaBeta(state, kmindist, color);
		ab.setTimeLimit(timeLimit);
		ab.setShowOutput(true);
		ab.setMaxDepth(93 - BitBoard.count(state.occupancy));
	}

	@Override
	public int consult(State state) {

		ab.setBoard(state);

		int turn = BitBoard.count(state.occupancy) - 8;
		if (turn >= INFLECTION_POINT) {
			ab.setHeuristic(qmindist);
		} else {
			ab.setHeuristic(kmindist);
		}

		return ab.search();
	}
}
