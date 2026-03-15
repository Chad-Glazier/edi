package ubc.cosc322.demo;

import ubc.cosc322.eval.MinDist;
import ubc.cosc322.search.AlphaBeta;
import ubc.cosc322.state.C;
import ubc.cosc322.state.Move;
import ubc.cosc322.state.State;
import ubc.cosc322.view.Display;
import ubc.cosc322.view.Util;

public class RunGame {
	public static void main() {
		
		State board = Util.initialBoard();
		Display.printBoard(board, "Alpha-Beta vs Alpha-Beta");

		AlphaBeta edi = new AlphaBeta(board, new MinDist(), C.WHITE);
		edi.setTimeLimit(2);
		edi.setShowOutput(true);
		AlphaBeta legion = new AlphaBeta(board, new MinDist(), C.BLACK);
		legion.setTimeLimit(2);
		legion.setShowOutput(true);

		for (
			byte activePlayer = C.WHITE;
			true;
			activePlayer = activePlayer == C.WHITE ? C.BLACK : C.WHITE
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
			Display.printBoard(board, "Alpha-Beta vs Itself");
		}

		String winner = Move.player(board.move) == C.WHITE ? "White" : "Black";

		Display.printText(0, winner + " wins.\n\n");
	}
}
