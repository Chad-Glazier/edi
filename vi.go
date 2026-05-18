package edi

import (
	"time"

	"github.com/Chad-Glazier/edi/state"
)

// We use VI, short for "virtual intelligence," to refer to a program that can
// recommend a move from a given board state within a certain amount of time.
// In contrast with a search function, which is defined to be stateless, a VI
// may "remember" certain things such as transposition tables between searches.
//
// The term VI is borrowed from a videogame:
// https://masseffect.fandom.com/wiki/Virtual_Intelligence. Traditionally we
// would call such a program "AI," but that term has been diluted by dorks.
type VI interface {
	Consult(board state.Board, timeLimit time.Duration) *state.Move
	Id() string
}
