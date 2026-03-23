package ubc.team09.player;

import ubc.team09.eval.KMinDist;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

public class EDI implements VI {

	private final AlphaBeta ab;
	private final KMinDist kmindist = new KMinDist();

	public EDI(
		State state,
		byte color,
		int timeLimit
	) {
		ab = new AlphaBeta(state, kmindist, color);
		ab.setTimeLimit(timeLimit);
		ab.setShowOutput(true);
	}

	@Override
	public int consult(State state) {

		ab.setBoard(state);

		return ab.search();
	}
}
