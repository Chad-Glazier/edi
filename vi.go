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
	// Determines the best move and returns it within the given time limit.
	Consult(board state.Board, timeLimit time.Duration) *state.Move
	// Determines the best move and returns it within the given time limit,
	// while also collecting search analytics. The analytics can be accessed
	// with GetAnalytics(). If you don't need the analytics and you want to
	// optimize for performance, then you should use Consult() instead.
	ConsultWithAnalytics(board state.Board, timeLimit time.Duration) *state.Move
	// Returns the analytics from the most recent call to ConsultWithAnalytics.
	GetAnalytics() any
	// Returns all analytics from past calls to ConsultWithAnalytics, ordered
	// so that the last element is from the most recent call. This value will
	// be a slice of the same type that GetAnalytics() returns.
	GetAllAnalytics() any
	// Returns a string that represents the VI's model.
	Id() string
}
