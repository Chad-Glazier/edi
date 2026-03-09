package bitboard

func SegmentBetween(arow, acol, brow, bcol int) BitBoard {
	board := BitBoard{}

	rowDelta := brow - arow
	colDelta := bcol - acol

	if rowDelta == 0 && colDelta == 0 {
		return board
	}

	if !(rowDelta == 0 ||	// if one of the deltas is zero, then we're fine
		 colDelta == 0 ||
		 rowDelta == colDelta ||  // if the magnitude of the deltas is the 
		 rowDelta == -colDelta) { // same, we're fine.

		// If none of the conditions are true, then there are no "in-between"
		// squares.

		return board
	}

	if  (rowDelta < 2 && -rowDelta < 2) && // to have in-between squares, we 
	 	(colDelta < 2 && -colDelta < 2) {  // need the squares to be non-
										   // adjacent.
		return board
	}

	// Normalize the deltas
	if rowDelta < 0 { 
		rowDelta = -1
	}
	if rowDelta > 0 {
		rowDelta = 1
	}
	if colDelta < 0 {
		colDelta = -1
	} 
	if colDelta > 0 {
		colDelta = 1
	}

	// Finally, we iterate over all of the in-between squares.
	row := arow
	col := acol
	for {
		row += rowDelta
		col += colDelta
		
		Flag(&board, row, col)

		if (row == brow && col == bcol) {
			break
		}
	}

	return board
} 
