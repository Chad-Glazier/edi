/*
Package bb implements a 10x10 bitboard for Amazons.
*/
package bb

import (
	"fmt"
	"math/bits"
	"strings"
)

// Represents a position on the 10x10 Amazons board with an index from 0 to 99.
// We use row-major ordering, so you can get the row index with position / 10
// and the column with position % 10.
type Position uint8

// Represents a null position. I.e., for functions that return a position,
// the null position should be returned if no valid position exists.
const NullPos Position = 100

// Converts row and column indices into a position index.
func Pos(row, col int) Position {
	return Position(row*10 + col)
}

// Converts a position index into row and column coordinates
func Coords(pos Position) (row, col int) {
	row = int(pos) / 10
	col = int(pos) % 10
	return
}

// Represents a board where each position index (0-99, since Amazons is played
// on a 10x10 board) is either 0 or 1, which we refer to as "unflagged" and
// "flagged," respectively.
type BitBoard struct {
	hi uint64
	lo uint64
}

// Flags a bit in the bitboard.
func Flag(bb BitBoard, pos Position) BitBoard {
	if pos < 64 {
		bb.lo |= 1 << pos
	} else {
		bb.hi |= 1 << (pos - 64)
	}
	return bb
}

// Unflags a bit in the bitboard.
func Unflag(bb BitBoard, pos Position) BitBoard {
	if pos < 64 {
		bb.lo &^= 1 << pos
	} else {
		bb.hi &^= 1 << (pos - 64)
	}
	return bb
}

// Returns true if the bit in the board is flagged and false otherwise.
func IsFlagged(bb BitBoard, pos Position) bool {
	if pos < 64 {
		return bb.lo&(1<<pos) != 0
	} else {
		return bb.hi&(1<<(pos-64)) != 0
	}
}

// Returns true if and only if the bitboard has no flags.
func IsEmpty(bb BitBoard) bool {
	return bb.lo == 0 && bb.hi == 0
}

// Returns true if and only if the bitboard has at least one flag.
func IsNotEmpty(bb BitBoard) bool {
	return (bb.lo | bb.hi) != 0
}

// Returns the "lowest" position on the board, meaning that which is the
// closest to the bottom-right corner, and unflags it. If the bitboard is
// empty, then [NullPos] is returned.
func Next(bb BitBoard) (BitBoard, Position) {
	switch {
	case bb.lo != 0:
		pos := Position(bits.TrailingZeros64(bb.lo))
		bb.lo &= bb.lo - 1
		return bb, pos
	case bb.hi != 0:
		pos := Position(bits.TrailingZeros64(bb.hi) + 64)
		bb.hi &= bb.hi - 1
		return bb, pos
	default:
		return bb, NullPos
	}
}

// Returns the number of flagged positions on this board.
func Count(bb BitBoard) int {
	return bits.OnesCount64(bb.lo) + bits.OnesCount64(bb.hi)
}

// Returns the greatest flagged position index on the board. If the
// board is empty, then [NullPos] is returned.
func Lsb(bb BitBoard) Position {
	switch {
	case bb.lo != 0:
		return Position(bits.TrailingZeros64(bb.lo))
	case bb.hi != 0:
		return Position(64 + bits.TrailingZeros64(bb.hi))
	default:
		return NullPos
	}
}

// Returns the position index of the most-significant bit in the board. If the
// board is empty, then [NullPos] is returned .
func Msb(bb BitBoard) Position {
	switch {
	case bb.hi != 0:
		return Position(127 - bits.LeadingZeros64(bb.hi))
	case bb.lo != 0:
		return Position(63 - bits.LeadingZeros64(bb.lo))
	default:
		return NullPos
	}
}

// Performs a bitwise OR operation (a | b) and returns the result.
func Or(a, b BitBoard) BitBoard {
	return BitBoard{
		lo: a.lo | b.lo,
		hi: a.hi | b.hi,
	}
}

// Performs a bitwise XOR operation (a ^ b) and returns the result.
func Xor(a, b BitBoard) BitBoard {
	return BitBoard{
		lo: a.lo ^ b.lo,
		hi: a.hi ^ b.hi,
	}
}

// Performs a bitwise AND operation (a & b) and returns the result.
func And(a, b BitBoard) BitBoard {
	return BitBoard{
		lo: a.lo & b.lo,
		hi: a.hi & b.hi,
	}
}

// Performs a bitwise AND-NOT operation (a &^ b) and returns the result.
func AndNot(a, b BitBoard) BitBoard {
	return BitBoard{
		lo: a.lo &^ b.lo,
		hi: a.hi &^ b.hi,
	}
}

// Performs a bitwise NOT operation (a ^ b) and returns the result.
func Not(bb BitBoard) BitBoard {
	return BitBoard{
		lo: ^bb.lo,
		hi: ^bb.hi,
	}
}

// Visualizes a bitboard.
func (bb BitBoard) String() string {

	lines := []string{
		"    0 1 2 3 4 5 6 7 8 9 ",
		"  " +
			cornerTopLeft +
			strings.Repeat(lineHorizontal, 21) +
			cornerTopRight,
	}

	for row := range 10 {
		var line strings.Builder
		fmt.Fprintf(&line, "%d %s", row, lineVertical)
		for col := range 10 {
			var s string
			line.WriteString(" ")
			if IsFlagged(bb, Pos(row, col)) {
				line.WriteString(flaggedSquare)
			} else {
				line.WriteString(vacantSquare)
			}
			line.WriteString(s)
		}
		line.WriteString(" " + lineVertical)
		lines = append(lines, line.String())
	}

	lines = append(lines,
		"  "+
			cornerBottomLeft+
			strings.Repeat(lineHorizontal, 21)+
			cornerBottomRight,
	)

	return strings.Join(lines, "\n")
}

const (
	lineHorizontal    = "\u2500" // ─
	lineVertical      = "\u2502" // │
	cornerTopLeft     = "\u250C" // ┌
	cornerTopRight    = "\u2510" // ┐
	cornerBottomLeft  = "\u2514" // └
	cornerBottomRight = "\u2518" // ┘
	flaggedSquare     = "\u2715" // ✕
	vacantSquare      = "\u00B7" // ·
)
