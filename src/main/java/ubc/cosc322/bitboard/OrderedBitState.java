package ubc.cosc322.bitboard;

import java.util.Iterator;

import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import ubc.cosc322.eval.HeuristicMethod;

public class OrderedBitState extends BitState {
	private static final int MAX_CHILDREN = 400;

	private final long[] occupancy = new long[2];
	private final byte[] queens = new byte[8];
	private final byte activePlayer;
	
	/**
	 * 
	 * @param activePlayer The player who can make a move from this state;
	 * i.e., the maximizing player. <code>0</code> for White, <code>1</code>
	 * for Black.
	 * @param occupancy
	 * @param queens All eight queens on the board. They must be ordered such
	 * that the indices <code>0</code> through <code>3</code> are White's 
	 * queens, and indices <code>4</code> through <code>7</code> are Black's 
	 * queens. 
	 * @param heuristic The heuristic used to evaluate child board states. This
	 * is used to determine the order in which child nodes are iterated over.
	 * @param killers A set of moves that have been potent in prior iterations
	 * at this ply. These moves will be prioritized above all others.
	 */
	public OrderedBitState(
		byte activePlayer,
		long[] occupancy, 
		byte[] queens,
		HeuristicMethod heuristic,
		IntAVLTreeSet[] killers,
		int depth
	) {
		this.activePlayer = activePlayer;
		this.occupancy[0] = occupancy[0];
		this.occupancy[1] = occupancy[1];	
		this.queens[0] = queens[0];
		this.queens[1] = queens[1];
		this.queens[2] = queens[2];
		this.queens[3] = queens[3];
		this.queens[4] = queens[4];
		this.queens[5] = queens[5];
		this.queens[6] = queens[6];
		this.queens[7] = queens[7];
	}


}


