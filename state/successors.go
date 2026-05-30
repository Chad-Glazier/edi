package state

import "github.com/Chad-Glazier/edi/bb"

const maxSuccessors = 3000

type SuccessorsArray [maxSuccessors]Board

// Computes the successors of a state and stores them in the specified array.
// The number of computed successors is returned.
func (board *Board) Successors(dest *SuccessorsArray) int {

	i := 0

	if board.Player == WHITE {
		for queenIdx, from := range board.White {

			i2 := QNeighbors(board.Occupancy, from)
			for to := i2.Next(); to != bb.NULL_POS; to = i2.Next() {

				board.White[queenIdx] = to

				board.Occupancy.Unflag(from)
				board.Occupancy.Flag(to)

				i3 := QNeighbors(board.Occupancy, to)
				for arrow := i3.Next(); arrow != bb.NULL_POS; arrow = i3.Next() {

					board.Occupancy.Flag(arrow)

					dest[i] = Board{
						Occupancy: board.Occupancy,
						White:     board.White,
						Black:     board.Black,
						Player:    BLACK,
						Move: Move{
							From:  from,
							To:    to,
							Arrow: arrow,
						},
					}
					i++

					board.Occupancy.Unflag(arrow)
				}

				board.White[queenIdx] = from

				board.Occupancy.Flag(from)
				board.Occupancy.Unflag(to)
			}
		}
	} else {
		for queenIdx, from := range board.Black {

			i2 := QNeighbors(board.Occupancy, from)
			for to := i2.Next(); to != bb.NULL_POS; to = i2.Next() {

				board.Black[queenIdx] = to

				board.Occupancy.Unflag(from)
				board.Occupancy.Flag(to)

				i3 := QNeighbors(board.Occupancy, to)
				for arrow := i3.Next(); arrow != bb.NULL_POS; arrow = i3.Next() {

					board.Occupancy.Flag(arrow)

					dest[i] = Board{
						Occupancy: board.Occupancy,
						White:     board.White,
						Black:     board.Black,
						Player:    WHITE,
						Move: Move{
							From:  from,
							To:    to,
							Arrow: arrow,
						},
					}
					i++

					board.Occupancy.Unflag(arrow)
				}

				board.Black[queenIdx] = from

				board.Occupancy.Flag(from)
				board.Occupancy.Unflag(to)
			}
		}
	}

	return i
}
