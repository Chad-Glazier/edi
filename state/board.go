/*
This package is used to represent an Amazons board state and provide the means
to construct a game tree.
*/
package state

import (
	"math/rand"

	"github.com/Chad-Glazier/edi/bb"
)

// Represents a player color.
type PlayerColor byte

const (
	WHITE PlayerColor = 0 // Represents the player on White
	BLACK PlayerColor = 1 // Represents the player on Black
)

// Represents a board state.
type Board struct {
	// The occupied squares on the board. An occupied square is one that has
	// either a queen or an arrow on it.
	Occupancy bb.BitBoard
	// The positions of the black queens on the board.
	Black [4]bb.Position
	// The positions of the white queens on the board.
	White [4]bb.Position
	// The most recent move made.
	Move Move
	// The player who can make the next move.
	Player PlayerColor
}

type PositionStatus uint8

const (
	VACANT PositionStatus = iota
	WHITE_QUEEN
	BLACK_QUEEN
	ARROW
)

// Returns VACANT, WHITE_QUEEN, BLACK_QUEEN, or ARROW, depending on what the
// status of the position is on the board. This function is not optimal and is
// only provided for convenience.
func (b *Board) Status(pos bb.Position) PositionStatus {
	if !bb.IsFlagged(b.Occupancy, pos) {
		return VACANT
	}

	for i := range 4 {
		if b.White[i] == pos {
			return WHITE_QUEEN
		}
		if b.Black[i] == pos {
			return BLACK_QUEEN
		}
	}

	return ARROW
}

// Returns a pointer to the queens belonging to the active player. That is,
// the array of queens that can move from this position.
func (b *Board) ActiveQueens() *[4]bb.Position {
	if b.Player == WHITE {
		return &b.White
	} else {
		return &b.Black
	}
}

// Returns true if and only if the board state is terminal.
func (b *Board) IsTerminal() bool {
	for _, queen := range b.ActiveQueens() {
		if bb.Count(KNeighbors(b.Occupancy, queen)) > 0 {
			return false
		}
	}
	return true
}

// Returns a random board state after some number of turns by simulating a game
// where each player randomly picks moves. Note that the number of turns is the
// same as the number of arrows, and getting the zeroth turn will always yield
// the initial board state.
func RandomBoard(turns int) Board {
	board := InitialState()

	successors := SuccessorSlice{}

	// Run randomized moves.
	for range turns {
		board.Successors(&successors)
		board = successors.Arr[rand.Intn(int(successors.Len))]
	}

	return board
}

// Represents the starting position for an Amazons game.
func InitialState() Board {
	board := Board{
		Player: WHITE,
		White:  [4]bb.Position{30, 03, 06, 39},
		Black:  [4]bb.Position{60, 93, 96, 69},
	}

	for _, pos := range board.White {
		board.Occupancy = bb.Flag(board.Occupancy, pos)
	}
	for _, pos := range board.Black {
		board.Occupancy = bb.Flag(board.Occupancy, pos)
	}

	return board
}
