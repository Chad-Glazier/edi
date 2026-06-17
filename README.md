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

To make two Amazons programs play against each other, you can write a program like the following.

```go
package main

import (
	"fmt"
	"time"

	"github.com/Chad-Glazier/edi"
	"github.com/Chad-Glazier/edi/state"
)

func main() {
	board := state.InitialState() // Set up the initial board state.
	turnTimer := time.Second * 3  // The per-turn time limit.

	// Note: This library refers to game-playing programs as "VI," short for
	// "virtual intelligence," so as to avoid being conflated with "AI" which
	// has become a slightly muddled term in recent years.

	white := edi.NewArrow() // the VI to play White.
	black := edi.NewEDI()   // the VI to play Black.

	for !board.IsTerminal() {

		// Internally, VI don't identify themselves as playing White or Black;
		// they just look at a board state and suggest the best possible move
		// for whichever player moves next.
		//
		// The Consult method is how you get a VI's recommended move. It will
		// return a reference to a Move struct or nil if no move is available.

		var move state.Move
		if board.Player == state.WHITE {
			move = *white.Consult(board, turnTimer)
			fmt.Println(white.Id() + " moves " + move.String())
		} else {
			move = *black.Consult(board, turnTimer)
			fmt.Println(black.Id() + " moves " + move.String())
		}

		// Next, we apply the suggested move. The Apply function will only 
		// return an error if the move is illegal. While the VI provided by
		// this package will never suggest such a move, the error case can 
		// still be useful if you want to handle user-input commands or 
		// validate a VI you've made yourself.
		newBoard, err := state.Apply(board, move)
		if err != nil {
			panic(err.Error())
		}
		board = *newBoard
	}

	// Amazons games run until the active player has no available moves. This
	// means that, in a terminal board state, the active player is the loser.
	if board.Player == state.WHITE {
		fmt.Printf("%s (Black) wins!\n", black.Id())
	} else {
		fmt.Printf("%s (White) wins!\n", white.Id())
	}
}
```
