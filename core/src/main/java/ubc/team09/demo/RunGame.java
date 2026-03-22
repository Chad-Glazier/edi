package ubc.team09.demo;

import ubc.team09.eval.MinDist;
import ubc.team09.player.Util;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.C;
import ubc.team09.state.Move;
import ubc.team09.state.State;
import ubc.team09.view.Display;

public class RunGame {
	public static void main() {

		String title = "AB vs AB";

		State board = Util.initialBoard();
		Display.printBoard(board, title, "Black", "White");

		AlphaBeta edi = new AlphaBeta(board, new MinDist(), C.WHITE);
		edi.setTimeLimit(30);
		edi.setShowOutput(true);

		AlphaBeta legion = new AlphaBeta(board, new MinDist(), C.BLACK);
		legion.setTimeLimit(30);
		legion.setShowOutput(true);

		for (
			byte activePlayer = C.WHITE; 
			true; 
			activePlayer = (activePlayer == C.WHITE ? C.BLACK : C.WHITE)
		) {
			int move;
			if (activePlayer == C.WHITE) {
				edi.setBoard(board);
				move = edi.search();
			} else {
				legion.setBoard(board);
				move = legion.search();
			}
			if (move == 0) {
				break;
			}

			board = new State(board, move);
			Display.printBoard(
				board, title, 
				"X", "mindist"
			);
		}

		String winner = Move.player(board.move) == C.WHITE ? "White" : "Black";

		Display.printText(0, winner + " wins.\n\n");
	}
}
