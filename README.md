# EDI

[![Go Reference](https://pkg.go.dev/badge/github.com/Chad-Glazier/edi.svg)](https://pkg.go.dev/github.com/Chad-Glazier/edi)
[![Go Report Card](https://goreportcard.com/badge/github.com/Chad-Glazier/edi)](https://goreportcard.com/report/github.com/Chad-Glazier/edi)

>The main website for the EDI project can be visited [here](https://ediproject.org). 

The EDI Project is an effort to analyze the programs that play the [Game of Amazons](https://en.wikipedia.org/wiki/Game_of_the_Amazons). Amazons has been historically studied and used for computer tournaments, but most of the existing research focuses on justifying and improving the authors' individual programs. In contrast, EDI is an effort to implement a variety of programs to directly compare them in terms of both raw performance (i.e., who wins more often), but also the more specific questions regarding the algorithms such as:
- What is the ideal tradeoff in terms of search depth versus evaluation strength?
- Which move ordering heuristics actually matter?
- Can strong Monte Carlo models be beaten by Alpha-Beta?

This package is the core EDI module which implements the actual game-playing programs and the means to collect certain analytics, while the [CLI tool](https://github.com/Chad-Glazier/edi_cli) is meant to run games between programs to collect and visualize statistics.

## Examples

To make two VI play against each other,

```go
package main

import (
	"fmt"
	"time"

	"github.com/Chad-Glazier/edi"
	"github.com/Chad-Glazier/edi/state"
)

func main() {
	// Set up the board state.
	board := state.InitialState()

	turnTimer := time.Second * 3 // The per-turn time limit.

	white := edi.NewArrow() // the VI to play White.
	black := edi.NewEDI()   // the VI to play Black.

	for !board.IsTerminal() { // while the game state is not terminal...
		
		var move state.Move
		if board.Player == state.WHITE { // If white moves next
			move = *white.Consult(board, turnTimer)
			fmt.Println(white.Id() + " moves " + move.String())
		} else {
			move = *black.Consult(board, turnTimer)
			fmt.Println(black.Id() + " moves " + move.String())
		}

		// Update the board state.
		newBoard, err := state.Apply(board, move)
		if err != nil { // If the move was illegal.
			panic(err.Error())
		}
		board = *newBoard
	}

	// In Amazons if it's some player's turn and they can't make a move, then
	// they lose. Since `board.Player` stores the player whos turn it is, this
	// means that `board.Player` in a terminal state is the loser.
	if board.Player == state.WHITE {
		fmt.Printf("%s (White) wins!\n", white.Id())
	} else {
		fmt.Printf("%s (Black) wins!\n", black.Id())
	}
}
```
