package state

import (
	"reflect"
	"testing"
)

func TestApplyMove(t *testing.T) {
	board := RandomBoard(12)
	successors := SuccessorSlice{}
	board.Successors(&successors)

	for i := range successors.Length {
		// Ensure that applying the move to the initial board yields the child.
		applied, err := Apply(board, successors.Array[i].Move)
		if err != nil {
			t.Errorf(
				"Expected move to be legal %v %s",
				successors.Array[i].Move,
				err.Error(),
			)
			continue
		}
		if !reflect.DeepEqual(applied, successors.Array[i]) {
			t.Errorf(
				"Expected inferred move to yield child %v",
				successors.Array[i].Move,
			)
		}
	}
}
