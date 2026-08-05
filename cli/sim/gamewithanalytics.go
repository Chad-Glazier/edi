package sim

import (
	"time"

	"github.com/Chad-Glazier/edi"
	"github.com/Chad-Glazier/edi/state"
)

// Makes two VIs play against each other, updating the board state through the
// returned channel. The channel is closed when the game is over. In this case,
// the winner of the game can be determined by which player is set to make the
// next move. That is, if White is the active player when the channel closes,
// that means that White had no moves left and Black is the winner.
func GameWithAnalytics(
	white, black edi.VI,
	turnTimer time.Duration,
) <-chan state.Board {

	ch := make(chan state.Board)

	go func() {
		defer close(ch)

		board := state.InitialState()
		ch <- board

		for !board.IsTerminal() {

			var move state.Move

			if board.Player == state.WHITE {
				move = *white.ConsultWithAnalytics(board, turnTimer)
			} else {
				move = *black.ConsultWithAnalytics(board, turnTimer)
			}

			newBoard, err := state.Apply(board, move)
			if err != nil {
				panic(err.Error())
			}
			board = newBoard
			ch <- board
		}
	}()

	return ch
}
