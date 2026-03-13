package ubc.cosc322.bitboard;

import java.util.PriorityQueue;

public class AlphaBetaState {
	private final State state;
	private final PriorityQueue<State> children;

	/**
	 * 
	 * 
	 */
	public AlphaBetaState(
		AlphaBeta config,
		State state
	) {
		this.state = state;
		
		this.children = new PriorityQueue<State>(config.comparator);

		StateGenerator iter = state.children();
		for (
			State child = iter.next();
			child != null;
			child = iter.next()
		) {
			this.children.add(child);
		}
	}

	public byte activePlayer() {
		return state.activePlayer;
	}
}


