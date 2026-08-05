/*
This package contains heuristic evaluation functions. I.e., functions that look
at a given board state and estimate how favorable the position is for each
player based on some heuristic.
*/
package eval

import "github.com/Chad-Glazier/edi/state"

// A function that evaluates a board state and returns a score that reflects
// the quality of the position. Positive values indicate favorability for
// White, negative values are favorable for Black, and 0 marks a neutral
// position.
type EvalFunc func(board state.Board) float64
