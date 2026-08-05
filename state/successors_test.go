package state

import (
	"testing"
)

//
// Testing
//

func TestInitialState(t *testing.T) {
	board := InitialState()
	successors := SuccessorSlice{}
	board.Successors(&successors)

	if successors.Len != 2176 {
		t.Fatal("initial board state didn't have 2176 successors")
	}
}

//
// Benchmarks
//

func BenchmarkSuccessorsInitial(b *testing.B) {
	board := InitialState()
	successors := SuccessorSlice{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn15(b *testing.B) {
	board := RandomBoard(15)
	successors := SuccessorSlice{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn30(b *testing.B) {
	board := RandomBoard(30)
	successors := SuccessorSlice{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn45(b *testing.B) {
	board := RandomBoard(45)
	successors := SuccessorSlice{}
	for b.Loop() {
		board.Successors(&successors)
	}
}
