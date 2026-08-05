package eval

import (
	"testing"

	"github.com/Chad-Glazier/edi/state"
)

func BenchmarkKMinDist(b *testing.B) {
	board := state.InitialState()
	for b.Loop() {
		KMinDist(board)
	}
}

func BenchmarkQMinDist(b *testing.B) {
	board := state.InitialState()
	for b.Loop() {
		QMinDist(board)
	}
}
