package bb

import (
	"math/rand"
	"testing"
)

//
// Helper functions.
//

func randomBoard(density float64) (
	bb BitBoard, flagged map[Position]bool, flagCount int,
) {
	bb = BitBoard{}
	flagged = make(map[Position]bool, 100)
	flagCount = 0

	for pos := range Position(100) {
		if rand.Float64() < density {
			flagged[pos] = true
			bb = Flag(bb, pos)
			flagCount++
		} else {
			flagged[pos] = false
		}
	}

	return
}

//
// Tests.
//

func TestFlagging(t *testing.T) {

	bb, flagged, flagCount := randomBoard(0.40)

	for pos := range Position(100) {
		if flagged[pos] && !IsFlagged(bb, pos) {
			t.Errorf("Expected flag at %d", pos)
		}
		if !flagged[pos] && IsFlagged(bb, pos) {
			t.Errorf("Unexpected flag at %d", pos)
		}
	}

	iteratedPositions := 0
	for bb, pos := Next(bb); pos != NULL_POS; bb, pos = Next(bb) {
		iteratedPositions++
		if !flagged[pos] {
			t.Errorf("Expected iterated position %d to be flagged.", pos)
		}
	}

	if iteratedPositions != flagCount {
		t.Errorf(
			"Iterated over %d flags, but expected %d",
			iteratedPositions, flagCount,
		)
	}

	for pos := range Position(100) {

		bb = Unflag(bb, pos)
		if flagged[pos] {
			flagCount--
		}

		if Count(bb) != flagCount {
			t.Errorf(
				"Expected flag count %d to be %d",
				Count(bb), flagCount,
			)
		}
	}

	if IsNotEmpty(bb) {
		t.Errorf("Expected board to be empty")
	}
}

func TestMsbLsb(t *testing.T) {

	empty := BitBoard{}
	if Msb(empty) != NULL_POS {
		t.Errorf(
			"Expected MSB of empty board to be NULL_POS, got %d", Msb(empty),
		)
	}
	if Lsb(empty) != NULL_POS {
		t.Errorf(
			"Expected LSB of empty board to be NULL_POS, got %d", Lsb(empty),
		)
	}

	bb, flagged, _ := randomBoard(0.40)

	minPos := Position(99)
	maxPos := Position(0)

	for pos := range Position(100) {
		if flagged[pos] {
			if pos < minPos {
				minPos = pos
			}
			if pos > maxPos {
				maxPos = pos
			}
		}
	}

	if Lsb(bb) != minPos {
		t.Errorf("Expected LSB %d, got %d", minPos, Lsb(bb))
	}
	if Msb(bb) != maxPos {
		t.Errorf("Expected MSB %d, got %d", maxPos, Msb(bb))
	}

	for pos := range Position(100) {
		single := BitBoard{}
		single = Flag(single, pos)

		if Lsb(single) != pos {
			t.Errorf("Single-bit LSB failed at %d, got %d", pos, Lsb(single))
		}
		if Msb(single) != pos {
			t.Errorf("Single-bit MSB failed at %d, got %d", pos, Msb(single))
		}
	}
}

//
// Benchmarks
//

func BenchmarkFlag(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Unflag(x, 65)
	}
}

func BenchmarkUnflag(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Unflag(x, 65)
	}
}

func BenchmarkIsFlagged(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		IsFlagged(x, 65)
	}
}

func BenchmarkIsEmpty(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		IsEmpty(x)
	}
}

func BenchmarkIsNotEmpty(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		IsNotEmpty(x)
	}
}

func BenchmarkNext(b *testing.B) {
	bb, _, _ := randomBoard(0.20)
	for b.Loop() {
		for bb, pos := Next(bb); pos != NULL_POS; bb, pos = Next(bb) {
			// ...
		}
	}
}

func BenchmarkBitwiseOr(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		Or(x, y)
	}
}

func BenchmarkBitwiseXor(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		Xor(x, y)
	}
}

func BenchmarkBitwiseAnd(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		And(x, y)
	}
}

func BenchmarkBitwiseAndNot(b *testing.B) {
	x, y := BitBoard{}, BitBoard{}
	for b.Loop() {
		And(x, y)
	}
}

func BenchmarkBitwiseNot(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Not(x)
	}
}

func BenchmarkLsb(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Lsb(x)
	}
}

func BenchmarkMsb(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Msb(x)
	}
}

func BenchmarkCount(b *testing.B) {
	x := BitBoard{}
	for b.Loop() {
		Count(x)
	}
}
