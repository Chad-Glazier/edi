package state

import (
	"testing"
)

func TestApplyMove(t *testing.T) {
	board := RandomBoard(12)
	children := SuccessorsArray{}
	childCount := board.Successors(&children)

	for i := range childCount {
		// Ensure that applying the move to the initial board yields the child.
		applied, err := Apply(board, children[i].Move)
		if err != nil {
			t.Errorf("Expected move to be legal %v %s", children[i].Move, err.Error())
			continue
		}
		if *applied != children[i] {
			t.Errorf("Expected inferred move to yield child %v", children[i].Move)
		}
	}
}
