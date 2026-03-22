package ubc.team09.eval;

import ubc.team09.state.QGraph;
import ubc.team09.state.State;

/**
 * An implementation of the <em>X</em> term presented in a 2014 paper.
 * 
 * <hr />
 * 
 * <h4>See Also</h4>
 * <ul>
 * 	<li>R. Zhong and K. Zhou, <a href="https://ieeexplore.ieee.org/abstract/document/7064199">An Optimization for Amazons Search and Evaluation,</a> 2014 </li>
 * </ul>
 */
public class X implements HeuristicMethod {
	public double evaluate(State state) {
	
		byte[][] whiteDistances = new byte[4][100];
		byte[][] blackDistances = new byte[4][100];

		for (int i = 0; i < 4; i++) {
			whiteDistances[i] = QGraph.distance(
				state.queens[i], state.occupancy
			);
			blackDistances[i] = QGraph.distance(
				state.queens[i + 4], state.occupancy
			);
		}

		double score = 0;
		for (int i = 0; i < 100; i++) {
			for (byte[] whiteQueenDistance : whiteDistances) {

				byte d = whiteQueenDistance[i];
				if (d == QGraph.UNREACHABLE || d == 0) {
					continue;
				}

				double k = switch (d) {
					case 1 -> 0.2;
					case 2 -> 0.1;
					default -> 0;
				};

				score += 1 / (double) d + k;
			}

			for (byte[] blackQueenDistance : blackDistances) {

				byte d = blackQueenDistance[i];
				if (d == QGraph.UNREACHABLE || d == 0) {
					continue;
				}

				double k = switch (d) {
					case 1 -> 0.2;
					case 2 -> 0.1;
					default -> 0;
				};

				score -= 1 / (double) d + k;
			}
		}

		return score;
	}
}
