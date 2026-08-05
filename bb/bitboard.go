//go:build !amd64 || !goexperiment.simd

/*
This package implements a 10x10 bitboard for Amazons.
*/
package bb

import "math/bits"

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
// empty, then NULL_POS is returned.
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
		return bb, NULL_POS
	}
}

// Returns the number of flagged positions on this board.
func Count(bb BitBoard) int {
	return bits.OnesCount64(bb.lo) + bits.OnesCount64(bb.hi)
}

// Returns the greatest flagged position index on the board. If the
// board is empty, then the null position (NULL_POS) is returned.
func Lsb(bb BitBoard) Position {
	switch {
	case bb.lo != 0:
		return Position(bits.TrailingZeros64(bb.lo))
	case bb.hi != 0:
		return Position(64 + bits.TrailingZeros64(bb.hi))
	default:
		return NULL_POS
	}
}

// Returns the position index of the most-significant bit in the board. If the
// board is empty, then the null position (NULL_POS) is returned .
func Msb(bb BitBoard) Position {
	switch {
	case bb.hi != 0:
		return Position(127 - bits.LeadingZeros64(bb.hi))
	case bb.lo != 0:
		return Position(63 - bits.LeadingZeros64(bb.lo))
	default:
		return NULL_POS
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
