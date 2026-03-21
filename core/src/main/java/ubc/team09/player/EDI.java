package ubc.team09.player;

import ubc.team09.eval.HeuristicMethod;
import ubc.team09.eval.MinDist;
import ubc.team09.search.AlphaBeta;
import ubc.team09.search.SearchMethod;
import ubc.team09.state.State;

public class EDI implements VI {

	public EDI() {
	}

	@Override
	public int consult(State state, byte color, int timeLimit) {

		HeuristicMethod heuristic = new MinDist();

		SearchMethod search = new AlphaBeta(state, heuristic, color);
		search.setShowOutput(true);
		search.setTimeLimit(timeLimit - 1); // add a small margin.

		return search.search();
	}

}
