package ubc.cosc322.player;

import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.eval.MinDist;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.search.SearchMethod;
import ubc.cosc322.state.State;

public class EDI implements VI {
	
	public EDI() {}

	@Override
	public int consult(State state, byte color, int timeLimit) {

		HeuristicMethod heuristic = new MinDist();

		SearchMethod search = new AlphaBeta(state, heuristic, color);
		search.setShowOutput(true);
		search.setTimeLimit(timeLimit - 1); // add a small margin.

		return search.search();
	}
	
}
