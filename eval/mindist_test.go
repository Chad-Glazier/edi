package eval

import (
	"testing"

	"github.com/Chad-Glazier/edi/state"
)

func BenchmarkKMinDist(b *testing.B) {
	board := state.Initial()
	for b.Loop() {
		KMinDist(board)
	}
}

func BenchmarkQMinDist(b *testing.B) {
	board := state.Initial()
	for b.Loop() {
		QMinDist(board)
	}
}
