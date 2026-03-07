package ubc.cosc322.demo;

import ubc.cosc322.eval.MinDist;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.util.Simulation;

public class AlphaBetaVsAlphaBeta {
	private static final byte WHITE = 0;
	private static final byte BLACK = 1;

	public static void main() {
		AlphaBeta alphaBetaA = new AlphaBeta(new MinDist(), WHITE);
		alphaBetaA.setTimeLimit(10);
		alphaBetaA.showOutput();

		AlphaBeta alphaBetaB = new AlphaBeta(new MinDist(), BLACK);
		alphaBetaB.setTimeLimit(10);
		alphaBetaB.showOutput();

		Simulation sim = new Simulation(
			alphaBetaA, "Tweedle Dee",
			alphaBetaB, "Tweedle Dum"
		);

		sim.run(true);
	}
}
