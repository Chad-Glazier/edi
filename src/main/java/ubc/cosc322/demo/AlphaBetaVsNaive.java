package ubc.cosc322.demo;

import ubc.cosc322.eval.MinDist;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.search.Naive;
import ubc.cosc322.util.Simulation;

public class AlphaBetaVsNaive {
	private static final byte WHITE = 0;
	private static final byte BLACK = 1;

	public static void main() {
		AlphaBeta alphaBeta = new AlphaBeta(new MinDist(), WHITE);
		alphaBeta.setTimeLimit(10);
		alphaBeta.showOutput();

		Simulation sim = new Simulation(
			alphaBeta, "Alpha-Beta",
			new Naive(new MinDist(), BLACK), "Naive"
		);

		sim.run(true);
	}
}
