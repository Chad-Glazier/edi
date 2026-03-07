package ubc.cosc322.search;

import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.gametree.State;

/**
 * This search simply looks at all available moves, then returns the one with
 * the highest evaluation. I.e., it is a best-move search with a depth of 1.
 * 
 * This search is deliberately trivial, it's meant to be a dummy bot for
 * testing purposes.
 */
public class Naive extends TimeConstrained implements SearchMethod {
	private State root;

	private final HeuristicMethod heuristic;

	private final byte player;

	public Naive(HeuristicMethod heuristic, byte player) {
		this.heuristic = heuristic;
		this.player = player;
	}

	public void setBoard(long[] empty, byte[] white, byte[] black) {
		this.root = new State(empty, white, black, player);
	}

	public int execute() {
		double bestScore = 0;
		int bestMove = 0;

		for (int move : root.moves()) {
			State child = new State(root, move);

			double score = child.value(heuristic, player);

			if (score >= bestScore) {
				bestScore = score;
				bestMove = child.move;
			}
		}

		return bestMove;
	}
}
