package state

import "testing"

//
// Tests
//

func TestIsTerminal(t *testing.T) {
	for range 100 {
		b := RandomBoard(10)
		successors := SuccessorSlice{}
		b.Successors(&successors)
		expected := successors.Length == 0
		actual := b.IsTerminal()
		if expected != actual {
			t.Fatalf("IsTerminal expected %v but got %v", expected, actual)
		}
	}
}

func TestInitialState(t *testing.T) {
	board := Initial()
	successors := SuccessorSlice{}
	board.Successors(&successors)

	if successors.Length != 2176 {
		t.Fatal("initial board state didn't have 2176 successors")
	}
}

//
// Benchmarks
//

func BenchmarkSuccessorsInitial(b *testing.B) {
	board := Initial()
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

