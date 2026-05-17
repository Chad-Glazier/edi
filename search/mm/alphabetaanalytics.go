package mm

import "time"

// Analytics collected from a depth-limited alpha-beta search.
type AlphaBetaAnalytics struct {
	// The depth limit of the search.
	Depth int
	// The number of leaf nodes that were evaluated.
	LeafNodes uint64
	// The number of interior nodes that were expanded.
	InteriorNodes uint64
	// The time it took to complete the search at this depth.
	Duration time.Duration
	// The number of cutoffs made at each depth.
	Cutoffs []uint64
	// The turn that the search begins from. This is important because later
	// turns have more arrows, which significantly reduces the branching
	// factor.
	Turn uint8
	// The effective branching factor of the search.
	Ebf float64
}
