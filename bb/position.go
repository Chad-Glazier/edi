package bb

// Represents a position on the 10x10 Amazons board with an index from 0 to 99.
// We use row-major ordering, so you can get the row index with position / 10
// and the column with position % 10.
type Position uint8

// Represents a null position. I.e., for functions that return a position,
// the null position should be returned if no valid position exists.
const NULL_POS Position = 100

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
