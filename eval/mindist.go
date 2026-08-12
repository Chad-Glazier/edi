package eval

import (
	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/state"
)

// Partitions territory between Black and White based on who can reach a given
// square faster if their queens moved the way that chess kings do, then
// calculates a score based on the sizes of the territories.
func KMinDist(board state.Board) float64 {

	var (
		whiteTerritory bb.BitBoard
		blackTerritory bb.BitBoard

		whiteFrontier bb.BitBoard
		blackFrontier bb.BitBoard
	)

	for i := range 4 {
		whiteTerritory = bb.Flag(whiteTerritory, board.White[i])
		blackTerritory = bb.Flag(blackTerritory, board.Black[i])

		whiteFrontier = bb.Or(
			whiteFrontier,
			state.KNeighbors(board.Occupancy, board.White[i]),
		)
		blackFrontier = bb.Or(
			blackFrontier,
			state.KNeighbors(board.Occupancy, board.Black[i]),
		)
	}

	visited := bb.Or(whiteTerritory, blackTerritory)

	for bb.IsNotEmpty(blackFrontier) || bb.IsNotEmpty(whiteFrontier) {

		// First, we let White and Black claim their respective territory.
		// Any new territory on the White frontier that isn't on the Black
		// frontier is claimed for White, and vice versa.
		whiteTerritory = bb.Or(
			whiteTerritory,
			bb.AndNot(whiteFrontier, blackFrontier),
		)
		blackTerritory = bb.Or(
			blackTerritory,
			bb.AndNot(blackFrontier, whiteFrontier),
		)

		// Next, update the "visited" board to reflect that the new
		// frontiers have been explored.
		visited = bb.Or(visited, bb.Or(whiteFrontier, blackFrontier))

		// Finally, we expand the frontiers, omitting any previously explored
		// territory.
		blackFrontier = bb.AndNot(
			state.KFrontier(board.Occupancy, blackFrontier),
			visited,
		)
		whiteFrontier = bb.AndNot(
			state.KFrontier(board.Occupancy, whiteFrontier),
			visited,
		)
	}

	return float64(bb.Count(whiteTerritory)-bb.Count(blackTerritory)) /
		float64(bb.Count(visited)-7)
}

// Partitions territory between Black and White based on who can reach a given
// square faster if their queens moved the way that chess queens do, then
// calculates a score based on the sizes of the territories.
func QMinDist(board state.Board) float64 {

	var (
		whiteTerritory bb.BitBoard
		blackTerritory bb.BitBoard

		whiteFrontier bb.BitBoard
		blackFrontier bb.BitBoard
	)

	for i := range 4 {
		whiteTerritory = bb.Flag(whiteTerritory, board.White[i])
		blackTerritory = bb.Flag(blackTerritory, board.Black[i])

		whiteFrontier = bb.Or(
			whiteFrontier,
			state.QNeighbors(board.Occupancy, board.White[i]),
		)
		blackFrontier = bb.Or(
			blackFrontier,
			state.QNeighbors(board.Occupancy, board.Black[i]),
		)
	}

	visited := bb.Or(whiteTerritory, blackTerritory)

	for bb.IsNotEmpty(blackFrontier) || bb.IsNotEmpty(whiteFrontier) {

		// First, we let White and Black claim their respective territory.
		// Any new territory on the White frontier that isn't on the Black
		// frontier is claimed for White, and vice versa.
		whiteTerritory = bb.Or(
			whiteTerritory,
			bb.AndNot(whiteFrontier, blackFrontier),
		)
		blackTerritory = bb.Or(
			blackTerritory,
			bb.AndNot(blackFrontier, whiteFrontier),
		)

		// Next, update the "visited" board to reflect that the new
		// frontiers have been explored.
		visited = bb.Or(visited, bb.Or(whiteFrontier, blackFrontier))

		// Finally, we expand the frontiers, omitting any previously explored
		// territory.
		blackFrontier = bb.AndNot(
			state.QFrontier(board.Occupancy, blackFrontier),
			visited,
		)
		whiteFrontier = bb.AndNot(
			state.QFrontier(board.Occupancy, whiteFrontier),
			visited,
		)
	}

	return float64(bb.Count(whiteTerritory)-bb.Count(blackTerritory)) /
		float64(bb.Count(visited)-7)
}
