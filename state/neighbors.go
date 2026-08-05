package state

import "github.com/Chad-Glazier/edi/bb"

// Returns a bitboard where each neighbor of a given position is flagged, where
// two squares p and q are neighbors if and only if a chess king could move
// from p to q (accounting for squares that are already occupied by arrows or
// queens).
func KNeighbors(occupancy bb.BitBoard, position bb.Position) bb.BitBoard {
	return bb.AndNot(bb.KAdj[position], occupancy)
}

// Returns the frontier of a given territory. A territory is a set of positions
// on the board, and the frontier of a territory is defined to be the set of
// positions that are adjacent to some position in the territory, excluding any
// positions that are already in the territory. Two positions p and q are
// adjacent if and only if a chess king could move from p to q in a single
// move, accounting for any arrows or queens that could obstruct such a move.
func KFrontier(occupancy bb.BitBoard, territory bb.BitBoard) bb.BitBoard {

	frontier := bb.BitBoard{}

	for i, pos := bb.Next(territory); pos != bb.NULL_POS; i, pos = bb.Next(i) {
		frontier = bb.Or(frontier, KNeighbors(occupancy, pos))
	}

	return bb.AndNot(frontier, territory)
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
		blockers := bb.And(ray, occupancy)

		nearestBlocker := bb.Msb(blockers) // the direction is forward
		if nearestBlocker == bb.NULL_POS {
			neighbors = bb.Or(neighbors, ray)
			continue
		}

		neighbors = bb.Or(
			neighbors,
			bb.Xor(ray, bb.RayInc[nearestBlocker][dir]),
		)
	}

	// Iterate over the backward directions.
	for dir := bb.E; dir <= bb.SW; dir++ {

		ray := bb.RayExc[position][dir]
		blockers := bb.And(ray, occupancy)

		nearestBlocker := bb.Lsb(blockers) // the direction is backward
		if nearestBlocker == bb.NULL_POS {
			neighbors = bb.Or(neighbors, ray)
			continue
		}

		neighbors = bb.Or(
			neighbors,
			bb.Xor(ray, bb.RayInc[nearestBlocker][dir]),
		)
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

	for i, pos := bb.Next(territory); pos != bb.NULL_POS; i, pos = bb.Next(i) {
		frontier = bb.Or(frontier, QNeighbors(occupancy, pos))
	}

	return bb.AndNot(frontier, territory)
}
