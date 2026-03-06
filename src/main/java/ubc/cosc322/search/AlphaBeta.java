package ubc.cosc322.search;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.util.BitBoard;
import ubc.cosc322.util.Graph;
import ubc.cosc322.util.Move;

/**
 * This function implements minimax search with α-β pruning. You can set 
 * various properties of the search via certain methods, which can be used to
 * modify the number of threads used, the depth of the search, the width, and
 * a time limit.
 * 
 */
public class AlphaBeta implements SearchMethod {
	private final static byte WHITE = 0;
	private final static byte BLACK = 1;

	private final long[] empty = new long[2];
	private final byte[] white = new byte[4];
	private final byte[] black = new byte[4];
	private final byte player;
	private final HeuristicMethod heuristic;

	public AlphaBeta(HeuristicMethod heuristic, byte player) {
		this.heuristic = heuristic;
		this.player = player;
	}

	public void setBoard(long[] empty, byte[] white, byte[] black) {
		BitBoard.copyTo(empty, this.empty);
		for (int i = 0; i < 4; i++) {
			this.white[i] = white[i];
			this.black[i] = black[i];
		}
	}

	
}
