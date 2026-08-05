package state

import (
	"reflect"
	"testing"
)

func TestApplyMove(t *testing.T) {
	board := RandomBoard(12)
	successors := SuccessorSlice{}
	board.Successors(&successors)

	for i := range successors.Len {
		// Ensure that applying the move to the initial board yields the child.
		applied, err := Apply(board, successors.Arr[i].Move)
		if err != nil {
			t.Errorf(
				"Expected move to be legal %v %s",
				successors.Arr[i].Move,
				err.Error(),
			)
			continue
		}
		if !reflect.DeepEqual(applied, successors.Arr[i]) {
			t.Errorf(
				"Expected inferred move to yield child %v",
				successors.Arr[i].Move,
			)
		}
	}
}
