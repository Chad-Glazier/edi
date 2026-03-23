package ubc.team09.search;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ubc.team09.eval.QMinDist;
import ubc.team09.player.Util;
import ubc.team09.state.C;
import ubc.team09.state.Move;
import ubc.team09.state.State;

public class AlphaBetaTest {
	@Test
	@Tag("SearchTest")
	public void TestValidity() {
		State board = Util.initialBoard();

		AlphaBeta edi = new AlphaBeta(board, new QMinDist(), C.WHITE);
		edi.setTimeLimit(10);
		AlphaBeta legion = new AlphaBeta(board, new QMinDist(), C.BLACK);
		legion.setTimeLimit(10);

		int turns = 10;
		for (byte activePlayer = C.WHITE; turns-- > 0; activePlayer = activePlayer == C.WHITE ? C.BLACK : C.WHITE) {
			int move;
			if (activePlayer == C.WHITE) {
				edi.setBoard(board);
				move = edi.search();
			} else {
				legion.setBoard(board);
				move = legion.search();
			}
			assertTrue(Move.isLegal(board, move));
			board = new State(board, move);
		}
	}

	@Test
	@Tag("SearchTest")
	public void TestTimeConstraint() {
		final int errorMarginMs = 500;

		SearchMethod alphaBeta = new AlphaBeta(
				Util.initialBoard(),
				new QMinDist(),
				C.WHITE);

		for (int i = 0; i < 5; i++) {
			int timeLimit = 10 + i * 2;
			alphaBeta.setTimeLimit(timeLimit);
			long start = System.currentTimeMillis();
			alphaBeta.search();
			long end = System.currentTimeMillis();
			assertTrue(
					(end - start) < (errorMarginMs + timeLimit * 1000));
		}
	}
}
