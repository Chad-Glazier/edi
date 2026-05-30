package state

import (
	"testing"
)

func BenchmarkSuccessorsInitial(b *testing.B) {
	board := InitialState()
	successors := SuccessorsArray{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn15(b *testing.B) {
	board := RandomBoard(15)
	successors := SuccessorsArray{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn30(b *testing.B) {
	board := RandomBoard(30)
	successors := SuccessorsArray{}
	for b.Loop() {
		board.Successors(&successors)
	}
}

func BenchmarkSuccessorsTurn45(b *testing.B) {
	board := RandomBoard(45)
	successors := SuccessorsArray{}
	for b.Loop() {
		board.Successors(&successors)
	}
}
