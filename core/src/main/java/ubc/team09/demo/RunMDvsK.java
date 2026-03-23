package ubc.team09.demo;

import ubc.team09.eval.KMinDist;
import ubc.team09.eval.MinDist;
import ubc.team09.player.Util;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.C;
import ubc.team09.state.Move;
import ubc.team09.state.State;
import ubc.team09.view.Display;

public class RunMDvsK {
	public static void main() {

		String title = "KMinDist vs DynamicMinDist";

		State board = Util.initialBoard();
		Display.printBoard(board, title, "DynamicMinDist", "KMinDist");

		AlphaBeta edi = new AlphaBeta(board, new KMinDist(), C.WHITE);
		edi.setTimeLimit(30);
		edi.setShowOutput(true);

		AlphaBeta legion = new AlphaBeta(
			board, MinDist.weightedSum(board), C.BLACK
		);
		legion.setTimeLimit(30);
		legion.setShowOutput(true);

		for (byte activePlayer = C.WHITE; true; activePlayer = (activePlayer == C.WHITE ? C.BLACK : C.WHITE)) {
			int move;
			if (activePlayer == C.WHITE) {
				edi.setBoard(board);
				move = edi.search();
			} else {
				legion.setBoard(board);
				legion.setHeuristic(MinDist.weightedSum(board));
				move = legion.search();
			}
			if (move == 0) {
				break;
			}

			board = new State(board, move);
			Display.printBoard(board, title, "DynamicMinDist", "KMinDist");
		}

		String winner = Move.player(board.move) == C.WHITE ? "KMinDist" : "DynamicMinDist";

		Display.printText(0, winner + " wins.\n\n");
	}
}
