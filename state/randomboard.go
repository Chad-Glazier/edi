package state

import (
	"math/rand"
)

// Returns a random board state after some number of turns by simulating a game
// where each player randomly picks moves. Note that the number of turns is the
// same as the number of arrows, and getting the zeroth turn will always yield
// the initial board state.
func RandomBoard(turns int) Board {
	board := InitialState()

	successors := SuccessorsArray{}

	// Run randomized moves.
	for range turns {
		n := board.Successors(&successors)
		board = successors[rand.Intn(n)]
	}

	return board
}
