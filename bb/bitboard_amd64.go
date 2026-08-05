//go:build amd64 && goexperiment.simd

/*
This package implements a 10x10 bitboard for Amazons.
*/
package bb

import (
	"math/bits"
	"simd/archsimd"
)

// Represents a board where each position index (0-99, since Amazons is played
// on a 10x10 board) is either 0 or 1, which we refer to as "unflagged" and
// "flagged," respectively.
type BitBoard archsimd.Uint64x2

//
// We precompute the position bitboards below.
//

// Maps a position index to a bitboard which has only that corresponding
// position flagged.
var p = [100]archsimd.Uint64x2{}

// Maps a position index to a bitboard which has every position except that one
// flagged.
var notp = [100]archsimd.Uint64x2{}

func init() {
	for i := range uint64(100) {
		positionBoard := archsimd.BroadcastUint64x2(1)
		shift := archsimd.BroadcastUint64x2(i)
		shift = shift.SetElem(1, i-64)
		p[i] = positionBoard.ShiftLeft(shift)
		notp[i] = p[i].Not()
	}
}

//
// We implement the BitBoard methods below.
//

// Flags a bit in the bitboard.
func Flag(bb BitBoard, pos Position) BitBoard {
	return BitBoard(archsimd.Uint64x2(bb).Or(p[pos]))
}

// Unflags a bit in the bitboard.
func Unflag(bb BitBoard, pos Position) BitBoard {
	return BitBoard(archsimd.Uint64x2(bb).And(notp[pos]))
}

// Returns true if the bit in the board is flagged and false otherwise.
func IsFlagged(bb BitBoard, pos Position) bool {
	return !archsimd.Uint64x2(bb).And(p[pos]).IsZero()
}

// Returns true if and only if the bitboard has no flags.
func IsEmpty(bb BitBoard) bool {
	return archsimd.Uint64x2(bb).IsZero()
}

// Returns true if and only if the bitboard has at least one flag.
func IsNotEmpty(bb BitBoard) bool {
	return !archsimd.Uint64x2(bb).IsZero()
}

// Returns the "lowest" position on the board, meaning that which is the
// closest to the bottom-right corner, and unflags it. If the bitboard is
// empty, then NULL_POS is returned.
func Next(bb BitBoard) (BitBoard, Position) {

	lo := archsimd.Uint64x2(bb).GetElem(0)
	if lo != 0 {
		pos := Position(bits.TrailingZeros64(lo))
		lo &= lo - 1
		return BitBoard(archsimd.Uint64x2(bb).SetElem(0, lo)), pos
	}

	hi := archsimd.Uint64x2(bb).GetElem(1)
	if hi != 0 {
		pos := Position(bits.TrailingZeros64(hi) + 64)
		hi &= hi - 1
		return BitBoard(archsimd.Uint64x2(bb).SetElem(1, hi)), pos
	}

	return bb, NULL_POS
}

// Returns the number of flagged positions on this board.
func Count(bb BitBoard) int {
	popcounts := archsimd.Uint64x2(bb).OnesCount()
	return int(popcounts.GetElem(0) + popcounts.GetElem(1))
}

// Returns the greatest flagged position index on the board. If the
// board is empty, then the null position (NULL_POS) is returned.
func Lsb(bb BitBoard) Position {
	lo := archsimd.Uint64x2(bb).GetElem(0)
	if lo != 0 {
		return Position(bits.TrailingZeros64(lo))
	}
	hi := archsimd.Uint64x2(bb).GetElem(1)
	if hi != 0 {
		return Position(64 + bits.TrailingZeros64(hi))
	}
	return NULL_POS
}

// Returns the position index of the most-significant bit in the board. If the
// board is empty, then the null position (NULL_POS) is returned .
func Msb(bb BitBoard) Position {
	zeros := archsimd.Uint64x2(bb).LeadingZeros()
	if hiZeroes := zeros.GetElem(1); hiZeroes != 64 {
		return Position(127 - hiZeroes)
	}
	if loZeroes := zeros.GetElem(0); loZeroes != 64 {
		return Position(63 - loZeroes)
	}
	return NULL_POS
}

// Performs a bitwise OR operation (a | b) and returns the result.
func Or(a, b BitBoard) BitBoard {
	return BitBoard(archsimd.Uint64x2(a).Or(archsimd.Uint64x2(b)))
}

// Performs a bitwise XOR operation (a ^ b) and returns the result.
func Xor(a, b BitBoard) BitBoard {
	return BitBoard(archsimd.Uint64x2(a).Xor(archsimd.Uint64x2(b)))
}

// Performs a bitwise AND operation (a & b) and returns the result.
func And(a, b BitBoard) BitBoard {
	return BitBoard(archsimd.Uint64x2(a).And(archsimd.Uint64x2(b)))
}

// Performs a bitwise AND-NOT operation (a &^ b) and returns the result.
func AndNot(a, b BitBoard) BitBoard {
	return BitBoard(archsimd.Uint64x2(a).AndNot(archsimd.Uint64x2(b)))
}

// Performs a bitwise NOT operation (a ^ b) and returns the result.
func Not(bb BitBoard) BitBoard {
	return BitBoard(archsimd.Uint64x2(bb).Not())
}

//
// Below, we implement the BitBoardx2 type which uses SIMD to perform
// concurrent operations on two bitboards simultaneously.
//

type BitBoardx2 archsimd.Uint64x4

func NewBitBoardx2(a, b BitBoard) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4{}.
		SetLo(archsimd.Uint64x2(a)).
		SetHi(archsimd.Uint64x2(b)))
}

func BroadcastBitBoardx2(a BitBoard) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4{}.
		SetLo(archsimd.Uint64x2(a)).
		SetHi(archsimd.Uint64x2(a)),
	)
}

func GetLo(bb BitBoardx2) BitBoard {
	return BitBoard(archsimd.Uint64x4(bb).GetLo())
}

func GetHi(bb BitBoardx2) BitBoard {
	return BitBoard(archsimd.Uint64x4(bb).GetHi())
}

func IsEmptyx2(bb BitBoardx2) bool {
	return archsimd.Uint64x4(bb).IsZero()
}

func IsNotEmptyx2(bb BitBoardx2) bool {
	return !archsimd.Uint64x4(bb).IsZero()
}

func Countx2(bb BitBoardx2) int {
	popcounts := archsimd.Uint64x4(bb).OnesCount()
	return int(
		popcounts.GetLo().GetElem(0) + popcounts.GetLo().GetElem(1) +
			popcounts.GetHi().GetElem(0) + popcounts.GetHi().GetElem(1),
	)
}

func Orx2(a, b BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(a).Or(archsimd.Uint64x4(b)))
}

func Xorx2(a, b BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(a).Xor(archsimd.Uint64x4(b)))
}

func Andx2(a, b BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(a).And(archsimd.Uint64x4(b)))
}

func AndNotx2(a, b BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(a).AndNot(archsimd.Uint64x4(b)))
}

func Notx2(bb BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(bb).Not())
}

var swapPermutation archsimd.Uint64x4

func init() {
	swapPermutation = swapPermutation.SetHi(
		archsimd.Uint64x2{}.SetElem(0, 0).SetElem(1, 1),
	)
	swapPermutation = swapPermutation.SetLo(
		archsimd.Uint64x2{}.SetElem(0, 2).SetElem(1, 3),
	)
}

func Swap(bb BitBoardx2) BitBoardx2 {
	return BitBoardx2(archsimd.Uint64x4(bb).Permute(swapPermutation))
}
