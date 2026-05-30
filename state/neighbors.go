package state

import "github.com/Chad-Glazier/edi/bb"

// Returns a bitboard where each neighbor of a given position is flagged, where
// two squares p and q are neighbors if and only if a chess king could move
// from p to q (accounting for squares that are already occupied by arrows or
// queens).
func KNeighbors(occupancy bb.BitBoard, position bb.Position) bb.BitBoard {
	return bb.KAdj[position].AndNot(occupancy)
}

// Returns the frontier of a given territory. A territory is a set of positions
// on the board, and the frontier of a territory is defined to be the set of
// positions that are adjacent to some position in the territory, excluding any
// positions that are already in the territory. Two positions p and q are
// adjacent if and only if a chess king could move from p to q in a single
// move, accounting for any arrows or queens that could obstruct such a move.
func KFrontier(occupancy bb.BitBoard, territory bb.BitBoard) bb.BitBoard {

	frontier := bb.BitBoard{}

	iter := territory
	for pos := iter.Next(); pos != bb.NULL_POS; pos = iter.Next() {
		frontier.AssignOr(KNeighbors(occupancy, pos))
	}

	return frontier.AndNot(territory)
}

// Returns a bitboard where each neighbor of a given position is flagged, where
// two squares p and q are neighbors if and only if a chess queen could move
// from p to q (accounting for squares that are already occupied by arrows or
// queens).
func QNeighbors(occupancy bb.BitBoard, position bb.Position) bb.BitBoard {

	neighbors := bb.BitBoard{}

	// Iterate over the forward directions.
	for dir := bb.W; dir < bb.E; dir++ {

		ray := bb.RayExc[position][dir]
		blockers := ray.And(occupancy)

		nearestBlocker := blockers.Msb() // the direction is forward
		if nearestBlocker == bb.NULL_POS {
			neighbors.AssignOr(ray)
			continue
		}

		neighbors.AssignOr(ray.Xor(bb.RayInc[nearestBlocker][dir]))
	}

	// Iterate over the backward directions.
	for dir := bb.E; dir <= bb.SW; dir++ {

		ray := bb.RayExc[position][dir]
		blockers := ray.And(occupancy)

		nearestBlocker := blockers.Lsb() // the direction is backward
		if nearestBlocker == bb.NULL_POS {
			neighbors.AssignOr(ray)
			continue
		}

		neighbors.AssignOr(ray.Xor(bb.RayInc[nearestBlocker][dir]))
	}

	return neighbors
}

// Returns the frontier of a given territory. A territory is a set of positions
// on the board, and the frontier of a territory is defined to be the set of
// positions that are adjacent to some position in the territory, excluding any
// positions that are already in the territory. Two positions p and q are
// adjacent if and only if a chess queen could move from p to q in a single
// move, accounting for any arrows or queens that could obstruct such a move.
func QFrontier(occupancy bb.BitBoard, territory bb.BitBoard) bb.BitBoard {

	frontier := bb.BitBoard{}

	iter := territory
	for pos := iter.Next(); pos != bb.NULL_POS; pos = iter.Next() {
		frontier.AssignOr(QNeighbors(occupancy, pos))
	}

	return frontier.AndNot(territory)
}
