package ubc.team09.eval;

import ubc.team09.bitboard.BitBoard;
import ubc.team09.state.State;

public class MinDist {
	
	private final static int EARLY_END = 5;
	private final static int LATE_START = 50;

	public static HeuristicMethod weightedSum(State state) {
		
		int turn = BitBoard.count(state.occupancy) - 8;

		if (turn <= EARLY_END) return new KMinDist();
		if (turn >= LATE_START) return new QMinDist();

		double kWeight = 
			((double) (LATE_START - turn)) 
			/ (double) (LATE_START - EARLY_END);
		double qWeight = 1 - kWeight;

		return new HeuristicMethod() {
			private final HeuristicMethod kmindist = new KMinDist();
			private final HeuristicMethod qmindist = new QMinDist();

			@Override
			public double evaluate(State state) {
				return kWeight * kmindist.evaluate(state)
					 + qWeight * qmindist.evaluate(state);
			}
		};
	}
}
