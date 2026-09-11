package ui

import (
	"fmt"

	"github.com/Chad-Glazier/edi/vi"
)

// Returns a formatted string representation of a VI's analytics. If the VI
// yields unsupported analytics, an error is returned.
func AnalyticsView(v vi.VI) (string, error) {
	switch a := v.GetAnalytics().(type) {
	case vi.EDIAnalytics:
		if len(a) == 0 {
			return "waiting for analytics...", nil
		}
		search := a[len(a)-1]
		return fmt.Sprintf(
			"  greatest depth:  %d\n"+
				"  time for search: %.1fs\n"+
				"  ebf:             %.0f",
			search.Depth,
			float64(search.Duration.Milliseconds())/1000.0,
			search.Ebf,
		), nil
	case vi.ArrowAnalytics:
		return fmt.Sprintf("Arrow analytics: %v", a), nil
	case vi.SparrowAnalytics:
		return fmt.Sprintf("Sparrow analytics: %v", a), nil
	default:
		return "", fmt.Errorf("analyticsView: unhandled analytics type %T", a)
	}
}

//
// Single search alpha-beta analytics.
//

//
// Aggregate alpha-beta analytics.
//
