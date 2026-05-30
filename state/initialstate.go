package state

import "github.com/Chad-Glazier/edi/bb"

// Represents the starting position for an Amazons game.
func InitialState() Board {
	board := Board{
		Player: WHITE,
		White:  [4]bb.Position{30, 03, 06, 39},
		Black:  [4]bb.Position{60, 93, 96, 69},
	}

	for _, pos := range board.White {
		board.Occupancy.Flag(pos)
	}
	for _, pos := range board.Black {
		board.Occupancy.Flag(pos)
	}

	return board
}
