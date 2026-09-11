package state

import (
	"fmt"

	"github.com/Chad-Glazier/edi/bb"
)

// Represents a move.
type Move struct {
	// The original position of the queen being moved.
	From bb.Position
	// The new position of the queen being moved.
	To bb.Position
	// The position where the queen fired her arrow.
	Arrow bb.Position
}

func (m Move) String() string {
	return fmt.Sprintf(
		"(%d, %d)->(%d, %d) X(%d, %d)",
		m.From/10, m.From%10,
		m.To/10, m.To%10,
		m.Arrow/10, m.Arrow%10,
	)
}

// If the given move is legal on the current board, this returns nil.
// Otherwise, it returns an error that explains why the move isn't allowed.
func (m Move) IsLegal(board Board) error {

	// Confirm that the `from` position is a queen.
	whiteFrom := false
	blackFrom := false
	for i := range 4 {
		if board.Black[i] == m.From {
			blackFrom = true
			break
		}
		if board.White[i] == m.From {
			whiteFrom = true
			break
		}
	}
	if !(whiteFrom || blackFrom) {
		return fmt.Errorf("bad move - queen not found %v", m)
	}

	// Confirm that the queen being moved belongs to the active player.
	if whiteFrom && board.Player != White {
		return fmt.Errorf("bad move - invalid 'from' position %v", m)
	}
	if blackFrom && board.Player != Black {
		return fmt.Errorf("bad move - moving opponent's queen %v", m)
	}

	// Confirm that the (from, to) squares are Q-adjacent.
	acceptableTo := QNeighbors(board.Occupancy, m.From)
	if !bb.IsFlagged(acceptableTo, m.To) {
		return fmt.Errorf("bad move - nonadjacent destination %v", m)
	}

	// Confirm that the arrow square is Q-adjacent to the destination.
	newOcc := board.Occupancy
	newOcc = bb.Unflag(newOcc, m.From)
	newOcc = bb.Flag(newOcc, m.To)
	acceptableArrow := QNeighbors(newOcc, m.To)
	if !bb.IsFlagged(acceptableArrow, m.Arrow) {
		return fmt.Errorf("bad move - nonadjacent arrow %v", m)
	}

	return nil
}

// Applies a move to the board, returning a new board with the updated state.
// If the move is illegal, then an error is returned.
func Apply(board Board, move Move) (Board, error) {

	err := move.IsLegal(board)
	if err != nil {
		return Board{}, err
	}

	for i := range 4 {
		if board.White[i] == move.From {
			board.White[i] = move.To
			break
		}
		if board.Black[i] == move.From {
			board.Black[i] = move.To
			break
		}
	}

	if board.Player == White {
		board.Player = Black
	} else {
		board.Player = White
	}

	board.Occupancy = bb.Unflag(board.Occupancy, move.From)
	board.Occupancy = bb.Flag(board.Occupancy, move.To)
	board.Occupancy = bb.Flag(board.Occupancy, move.Arrow)

	board.Move = move

	return board, nil
}
