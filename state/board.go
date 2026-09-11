/*
Package state implements a representation of Amazons board states and provides
the means to construct a game tree.
*/
package state

import (
	"math/rand"

	"github.com/Chad-Glazier/edi/bb"
)

// Represents a player color.
type PlayerColor byte

const (
	White PlayerColor = 0 // Represents the player on White
	Black PlayerColor = 1 // Represents the player on Black
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

// Represents the status of a given position on the board. That is, whether it
// is vacant, covered by an arrow, or occupied by a queen.
type PositionStatus uint8

const (
	StatusVacant PositionStatus = iota
	StatusWhiteQueen
	StatusBlackQueen
	StatusArrow
)

// Returns [StatusVacant], [StatusWhiteQueen], [StatusBlackQueen], or
// [StatusArrow], depending on what the status of the position is on the board.
// This function is not optimal and is only provided for convenience.
func (b *Board) Status(pos bb.Position) PositionStatus {
	if !bb.IsFlagged(b.Occupancy, pos) {
		return StatusVacant
	}

	for i := range 4 {
		if b.White[i] == pos {
			return StatusWhiteQueen
		}
		if b.Black[i] == pos {
			return StatusBlackQueen
		}
	}

	return StatusArrow
}

// Returns true if and only if the board state is terminal.
func (b *Board) IsTerminal() bool {

	var activeQueens [4]bb.Position
	if b.Player == White {
		activeQueens = b.White
	} else {
		activeQueens = b.Black
	}

	for _, queen := range activeQueens {
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

	board := Initial()
	successors := SuccessorSlice{}

	for range turns {
		board.Successors(&successors)
		if successors.Length == 0 {
			break
		}

		board = successors.Array[rand.Intn(int(successors.Length))]
	}

	return board
}

// Represents the starting position for an Amazons game.
func Initial() Board {
	board := Board{
		Player: White,
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

// Computes the successors of a state and stores them in the specified array.
// The number of computed successors is returned.
func (board Board) Successors(dst *SuccessorSlice) {

	var (
		i          int
		queens     *[4]bb.Position
		nextPlayer PlayerColor
	)

	if board.Player == White {
		queens = &board.White
		nextPlayer = Black
	} else {
		queens = &board.Black
		nextPlayer = White
	}

	for queenIdx, from := range queens {

		i2 := QNeighbors(board.Occupancy, from)
		for i2, to := bb.Next(i2); to != bb.NullPos; i2, to = bb.Next(i2) {

			queens[queenIdx] = to

			board.Occupancy = bb.Unflag(board.Occupancy, from)
			board.Occupancy = bb.Flag(board.Occupancy, to)

			i3 := QNeighbors(board.Occupancy, to)
			for i3, arrow := bb.Next(i3); arrow != bb.NullPos; i3, arrow = bb.Next(i3) {

				board.Occupancy = bb.Flag(board.Occupancy, arrow)

				dst.Array[i] = Board{
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

	dst.Length = i
}

const maxSuccessors = 3000

type SuccessorSlice struct {
	Array [maxSuccessors]Board
	Length int
}

