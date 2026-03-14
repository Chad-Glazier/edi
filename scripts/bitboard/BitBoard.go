package bitboard

import (
	"fmt"
	"io"
)

const (
	W int = iota
	NW
	N
	NE
	E 
	SE 
	S 
	SW
)

type BitBoard = [2]int64

func Flag(board *BitBoard, row, col int) {
	index := row*10 + col
	board[index >> 6] |= (1 << (index & 63))
}

func Clear(board *BitBoard) {
	board[0] = 0
	board[1] = 0
}

// Writes the bitboard to the format of a Java `long[]` literal. E.g.,
// `"{ 032828L, 2394712L }"`.
func Write(w io.Writer, board BitBoard) {
	fmt.Fprintf(w, "{ %dL, %dL }", board[0], board[1])	
}

func Ray(row, col, direction int) BitBoard {
	board := BitBoard{}

	// Rows are indexed from the top to the bottom. So, to move "north," we
	// would need to decrement the row index.
	//
	// Column indices are indexed left-to-right, so incrementing the column
	// index is the same as moving "east."

	for {
		switch direction {
		case N: 
			row--
		case NE:
			row--
			col++
		case E:
			col++
		case SE:
			row++
			col++
		case S:
			row++
		case SW:
			row++
			col--
		case W:
			col--
		case NW:
			row--
			col--
		}
		if row >= 10 || row < 0 || col >= 10 || col < 0 {
			break
		}

		Flag(&board, row, col)
	}

	return board
}

func InclusiveRay(row, col, direction int) BitBoard {
	board := BitBoard{}

	// Rows are indexed from the top to the bottom. So, to move "north," we
	// would need to decrement the row index.
	//
	// Column indices are indexed left-to-right, so incrementing the column
	// index is the same as moving "east."

	Flag(&board, row, col)

	for {
		switch direction {
		case N: 
			row--
		case NE:
			row--
			col++
		case E:
			col++
		case SE:
			row++
			col++
		case S:
			row++
		case SW:
			row++
			col--
		case W:
			col--
		case NW:
			row--
			col--
		}
		if row >= 10 || row < 0 || col >= 10 || col < 0 {
			break
		}

		Flag(&board, row, col)
	}

	return board
}

func Square(row, col int) BitBoard {
	board := BitBoard{}

	Flag(&board, row, col)

	return board
}
