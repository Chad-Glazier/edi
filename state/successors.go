package state

import "github.com/Chad-Glazier/edi/bb"

const maxSuccessors = 3000

type SuccessorSlice struct {
	Arr [maxSuccessors]Board
	Len uint16
}

// Computes the successors of a state and stores them in the specified array.
// The number of computed successors is returned.
func (board Board) Successors(dest *SuccessorSlice) {

	var (
		i uint16

		queens     *[4]bb.Position
		nextPlayer PlayerColor
	)

	if board.Player == WHITE {
		queens = &board.White
		nextPlayer = BLACK
	} else {
		queens = &board.Black
		nextPlayer = WHITE
	}

	for queenIdx, from := range queens {

		i2 := QNeighbors(board.Occupancy, from)
		for i2, to := bb.Next(i2); to != bb.NULL_POS; i2, to = bb.Next(i2) {

			queens[queenIdx] = to

			board.Occupancy = bb.Unflag(board.Occupancy, from)
			board.Occupancy = bb.Flag(board.Occupancy, to)

			i3 := QNeighbors(board.Occupancy, to)
			for i3, arrow := bb.Next(i3); arrow != bb.NULL_POS; i3, arrow = bb.Next(i3) {

				board.Occupancy = bb.Flag(board.Occupancy, arrow)

				dest.Arr[i] = Board{
					Occupancy: board.Occupancy,
					White:     board.White,
					Black:     board.Black,
					Player:    nextPlayer,
					Move: Move{
						From:  from,
						To:    to,
						Arrow: arrow,
					},
				}
				i++

				board.Occupancy = bb.Unflag(board.Occupancy, arrow)
			}

			queens[queenIdx] = from

			board.Occupancy = bb.Flag(board.Occupancy, from)
			board.Occupancy = bb.Unflag(board.Occupancy, to)
		}
	}

	dest.Len = i
}
