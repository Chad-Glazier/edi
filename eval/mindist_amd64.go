//go:build amd64 && goexperiment.simd

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

	var (
		territories = bb.NewBitBoardx2(whiteTerritory, blackTerritory)
		frontiers   = bb.NewBitBoardx2(whiteFrontier, blackFrontier)
	)

	visited := bb.BroadcastBitBoardx2(bb.Or(whiteTerritory, blackTerritory))

	for bb.IsNotEmptyx2(frontiers) {

		// First, we let White and Black claim their respective territory.
		// Any new territory on the White frontier that isn't on the Black
		// frontier is claimed for White, and vice versa.

		territories = bb.Orx2(
			territories,
			bb.AndNotx2(territories, bb.Swap(territories)),
		)

		// Next, update the "visited" board to reflect that the new
		// frontiers have been explored.
		visited = bb.Orx2(visited, frontiers)

		// Finally, we expand the frontiers, omitting any previously explored
		// territory.
		frontiers = bb.AndNotx2(
			bb.NewBitBoardx2(
				state.KFrontier(board.Occupancy, whiteFrontier),
				state.KFrontier(board.Occupancy, blackFrontier),
			),
			visited,
		)
	}

	return float64(
		bb.Count(bb.GetLo(territories))-bb.Count(bb.GetHi(territories)),
	) / float64(bb.Count(bb.GetLo(visited))-7)
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

	var (
		territories = bb.NewBitBoardx2(whiteTerritory, blackTerritory)
		frontiers   = bb.NewBitBoardx2(whiteFrontier, blackFrontier)
	)

	visited := bb.BroadcastBitBoardx2(bb.Or(whiteTerritory, blackTerritory))

	for bb.IsNotEmptyx2(frontiers) {

		// First, we let White and Black claim their respective territory.
		// Any new territory on the White frontier that isn't on the Black
		// frontier is claimed for White, and vice versa.

		territories = bb.Orx2(
			territories,
			bb.AndNotx2(territories, bb.Swap(territories)),
		)

		// Next, update the "visited" board to reflect that the new
		// frontiers have been explored.
		visited = bb.Orx2(visited, frontiers)

		// Finally, we expand the frontiers, omitting any previously explored
		// territory.
		frontiers = bb.AndNotx2(
			bb.NewBitBoardx2(
				state.QFrontier(board.Occupancy, whiteFrontier),
				state.QFrontier(board.Occupancy, blackFrontier),
			),
			visited,
		)
	}

	return float64(
		bb.Count(bb.GetLo(territories))-bb.Count(bb.GetHi(territories)),
	) / float64(bb.Count(bb.GetLo(visited))-7)
}
