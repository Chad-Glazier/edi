package state

import (
	"math/rand/v2"
	"testing"

	"github.com/Chad-Glazier/edi/bb"
)

func randomBoard(density float64) (
	b bb.BitBoard, flagged map[bb.Position]bool, flagCount int,
) {
	b = bb.BitBoard{}
	flagged = make(map[bb.Position]bool, 100)
	flagCount = 0

	for pos := range bb.Position(100) {
		if rand.Float64() < density {
			flagged[pos] = true
			bb.Flag(b, pos)
			flagCount++
		} else {
			flagged[pos] = false
		}
	}

	return
}

//
// Ground-truth functions. These implementations are certainly correct, just
// terribly inefficient. We can use them to get the expected results.
//

func inBounds(row, col int) bool {
	return row >= 0 && row < 10 && col >= 0 && col < 10
}

func expectedKNeighbors(occ bb.BitBoard, pos bb.Position) bb.BitBoard {

	result := bb.BitBoard{}
	i, j := bb.Coords(pos)

	for di := -1; di <= 1; di++ {
		for dj := -1; dj <= 1; dj++ {
			if di == 0 && dj == 0 {
				continue
			}
			nx, ny := i+di, j+dj
			if !inBounds(nx, ny) {
				continue
			}
			p := bb.Pos(nx, ny)
			if !bb.IsFlagged(occ, p) {
				result = bb.Flag(result, p)
			}
		}
	}

	return result
}

func expectedQNeighbors(occ bb.BitBoard, pos bb.Position) bb.BitBoard {
	result := bb.BitBoard{}
	i, j := bb.Coords(pos)

	dirs := [8][2]int{
		{1, 0}, {-1, 0}, {0, 1}, {0, -1},
		{1, 1}, {1, -1}, {-1, 1}, {-1, -1},
	}

	for _, d := range dirs {
		ni, nj := i+d[0], j+d[1]
		for inBounds(ni, nj) {
			p := bb.Pos(ni, nj)
			if bb.IsFlagged(occ, p) {
				break
			}
			result = bb.Flag(result, p)
			ni += d[0]
			nj += d[1]
		}
	}

	return result
}

func expectedFrontier(
	occ bb.BitBoard,
	territory bb.BitBoard,
	neighborFn func(bb.BitBoard, bb.Position) bb.BitBoard,
) bb.BitBoard {

	result := bb.BitBoard{}

	for i, pos := bb.Next(territory); pos != bb.NULL_POS; i, pos = bb.Next(i) {
		neighbors := neighborFn(occ, pos)
		for i, n := bb.Next(neighbors); n != bb.NULL_POS; i, n = bb.Next(i) {
			if !bb.IsFlagged(territory, n) {
				result = bb.Flag(result, n)
			}
		}
	}

	return result
}

//
// Tests
//

func TestKNeighbors(t *testing.T) {
	for range 50 {
		occ, _, _ := randomBoard(0.2)

		for pos := range bb.Position(100) {
			got := KNeighbors(occ, pos)
			expected := expectedKNeighbors(occ, pos)

			if bb.Count(got) != bb.Count(expected) {
				t.Fatalf("KNeighbors size mismatch at %d", pos)
			}

			for i, p := bb.Next(expected); p != bb.NULL_POS; i, p = bb.Next(i) {
				if !bb.IsFlagged(got, p) {
					t.Errorf("KNeighbors missing %d from %d", p, pos)
				}
			}
		}
	}
}

func TestQNeighbors(t *testing.T) {
	for range 50 {
		occ, _, _ := randomBoard(0.2)

		for pos := range bb.Position(100) {
			got := QNeighbors(occ, pos)
			expected := expectedQNeighbors(occ, pos)

			if bb.Count(got) != bb.Count(expected) {
				t.Fatalf("QNeighbors size mismatch at %d", pos)
			}

			for i, p := bb.Next(expected); p != bb.NULL_POS; i, p = bb.Next(i) {
				if !bb.IsFlagged(got, p) {
					t.Errorf("QNeighbors missing %d from %d", p, pos)
				}
			}
		}
	}
}

func TestKFrontier(t *testing.T) {
	for range 50 {
		occ, _, _ := randomBoard(0.2)
		territory, _, _ := randomBoard(0.2)

		got := KFrontier(occ, territory)
		expected := expectedFrontier(occ, territory, expectedKNeighbors)

		if bb.Count(got) != bb.Count(expected) {
			t.Fatalf("KFrontier size mismatch")
		}

		for i, p := bb.Next(expected); p != bb.NULL_POS; i, p = bb.Next(i) {
			if !bb.IsFlagged(got, p) {
				t.Errorf("KFrontier missing %d", p)
			}
		}
	}
}

func TestQFrontier(t *testing.T) {
	for range 50 {
		occ, _, _ := randomBoard(0.2)
		territory, _, _ := randomBoard(0.2)

		got := QFrontier(occ, territory)
		expected := expectedFrontier(occ, territory, expectedQNeighbors)

		if bb.Count(got) != bb.Count(expected) {
			t.Fatalf("QFrontier size mismatch")
		}

		for i, p := bb.Next(expected); p != bb.NULL_POS; i, p = bb.Next(i) {
			if !bb.IsFlagged(got, p) {
				t.Errorf("QFrontier missing %d", p)
			}
		}
	}
}
