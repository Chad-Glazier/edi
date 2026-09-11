package mm

import (
	"math"
	"sort"
	"time"

	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/eval"
	"github.com/Chad-Glazier/edi/state"
)

type historicAbContext struct {
	alphaBetaContext
	history *HistoryTable
}

// Conducts an alpha-beta search that uses the history heuristic to order
// moves.
//
// The returned search analytics slice contains analytics for each
// depth-limited search conducted during the iterative deepening process.
//
// If an error is returned, it will be [ErrNoMoves].
func HistoricAlphaBeta(
	board state.Board,
	timeLimit time.Duration,
	heuristic eval.EvalFunc,
	history *HistoryTable,
) (state.Move, []AlphaBetaAnalytics, error) {

	if board.IsTerminal() {
		return state.Move{}, nil, ErrNoMoves
	}

	ctx := historicAbContext{
		heuristic: heuristic,
		history:   history,
	}

	go func() {
		<-time.After(timeLimit)
		ctx.outOfTime = true
	}()

	var (
		turn      = uint8(bb.Count(board.Occupancy)-8) + 1
		maxDepth  = 100 - bb.Count(board.Occupancy)
		bestMove  state.Move
		analytics = make([]AlphaBetaAnalytics, 1, maxDepth)
	)

	for depth := 1; depth <= maxDepth; depth++ {

		ctx.analytics = AlphaBetaAnalytics{
			Depth:         depth,
			Cutoffs:       make([]uint64, depth+1),
			Turn:          turn,
			InteriorNodes: 0,
			LeafNodes:     0,
		}
		start := time.Now()

		bestChildAtDepth, err := ctx.depthLimitedSearch(board, depth)
		if err != nil {
			break
		}
		ctx.analytics.Duration = time.Since(start)

		analytics = append(analytics, ctx.analytics)
		bestMove = bestChildAtDepth.Move
	}

	return bestMove, analytics, nil
}

// Conducts a depth-limited search from the specified state and returns the
// immediate child which has the best minimax score.
//
// If an error is returned, it will be [ErrNoMoves] or [ErrOutOfTime].
func (ctx *historicAbContext) depthLimitedSearch(
	board state.Board,
	depth int,
) (state.Board, error) {

	successors := state.SuccessorSlice{}
	board.Successors(&successors)
	ctx.history.Sort(&successors)

	if successors.Length == 0 {
		return state.Board{}, ErrNoMoves
	}

	var (
		α         = math.Inf(-1)
		β         = math.Inf(+1)
		bestChild state.Board
	)

	for i := range successors.Length {

		score, err := ctx.alphaBeta(
			successors.Array[i],
			-β, -α,
			depth-1,
			color(board),
		)
		if err != nil {
			return state.Board{}, ErrOutOfTime
		}

		if score > α {
			α = score
			bestChild = successors.Array[i]
		}

	}

	return bestChild, nil
}

// Conducts a recursive search to find the minimax score of a state.
//
// If an error is returned, it will be [ErrOutOfTime].
func (ctx *historicAbContext) alphaBeta(
	board state.Board,
	α, β float64,
	depth int,
	color float64,
) (float64, error) {
	if ctx.outOfTime {
		return 0.0, ErrOutOfTime
	}

	if depth == 0 {
		ctx.analytics.LeafNodes++
		return color * ctx.heuristic(board), nil
	}

	successors := state.SuccessorSlice{}
	board.Successors(&successors)
	ctx.history.Sort(&successors)

	if successors.Length == 0 {
		ctx.analytics.LeafNodes++
		return color * ctx.heuristic(board), nil
	}

	value := math.Inf(-1)
	for i := range successors.Length {

		result, err := ctx.alphaBeta(
			successors.Array[i],
			-β, -α,
			depth-1,
			-color,
		)
		if err != nil {
			return 0.0, err
		}

		value = max(value, -result)
		α = max(α, value)

		if α >= β {
			ctx.analytics.Cutoffs[depth]++
			ctx.history.Update(board, depth)
			break
		}
	}

	ctx.analytics.InteriorNodes++
	return value, nil
}

// A history table is used to track which moves have produced cutoffs in the
// past and increments their score accordingly. When searching the game tree,
// the history table can be used to order child states. Since this approach
// only involves table lookups, it's much faster than just about any other
// ordering method, and the quality of the ordering has been shown to be far
// more effective than ordering by evaluation scores.
type HistoryTable struct {
	scores [2][100][100][100]int32
}

// Retrieves the history score of the current state's move.
func (h *HistoryTable) Get(board state.Board) int32 {
	return h.scores[board.Player][board.Move.From][board.Move.To][board.Move.Arrow]
}

// Sets the history score to the given value.
func (h *HistoryTable) Set(board state.Board, value int32) {
	h.scores[board.Player][board.Move.From][board.Move.To][board.Move.Arrow] = value
}

const maxHistory int32 = 2 << 13

// Increases the history score of a state's move. You should call this whenever
// a move produces a cutoff.
func (h *HistoryTable) Update(board state.Board, depth int) {

	// We just use the simple depth^2 formula.

	initial := h.Get(board)
	bonus := int32(depth * depth)

	new := min(maxHistory, initial+bonus)

	h.Set(board, new)
}

// Sorts a slice of states in-place, in descending order by their history
// scores.
func (h *HistoryTable) Sort(successors *state.SuccessorSlice) {

	// Testing has shown that it's faster to grab all of the history scores
	// up-front than to repeatedly consult the table.
	historyScores := [3000]int32{}
	for i := range successors.Length {
		historyScores[i] = h.Get(successors.Array[i])
	}

	sort.Sort(&stateSorter{
		states:        successors,
		historyScores: &historyScores,
	})
}

type stateSorter struct {
	states        *state.SuccessorSlice
	historyScores *[3000]int32
}

func (s *stateSorter) Len() int {
	return s.states.Length
}

func (s *stateSorter) Less(i, j int) bool {
	return s.historyScores[i] > s.historyScores[j]
}

func (s *stateSorter) Swap(i, j int) {
	s.states.Array[i], s.states.Array[j] = s.states.Array[j], s.states.Array[i]
	s.historyScores[i], s.historyScores[j] = s.historyScores[j], s.historyScores[i]
}
