package ui

import (
	"fmt"
	"math"
	"sort"
	"strings"

	"github.com/Chad-Glazier/edi/vi"
)

// Returns a formatted string representation of a VI's analytics.
func AnalyticsView(v vi.VI) string {
	out := strings.Builder{}

	analytics := v.Analytics()

	if analytics == nil {
		return fmt.Sprintf(
			"  %swaiting...%s",
			strings.Repeat(" ", 8),
			strings.Repeat(" ", 7),
		)
	}

	orderedKeys := make([]string, 0)
	maxWidth := 0
	for key := range analytics {
		orderedKeys = append(orderedKeys, key)
		maxWidth = max(maxWidth, len([]rune(key)))
	}
	sort.Strings(orderedKeys)

	for _, key := range orderedKeys {
		fmt.Fprintf(&out,
			"  %s  %s\n",
			rightPad(key, maxWidth, " "),
			leftPad(formatF64(analytics[key]), 10, " "),
		)
	}

	return out.String()
}

func rightPad(s string, width int, ch string) string {
	delta := width - len([]rune(s))
	if delta <= 0 {
		return s
	}
	delta /= len([]rune(ch))
	return s + strings.Repeat(ch, delta)
}

func leftPad(s string, width int, ch string) string {
	delta := width - len([]rune(s))
	if delta <= 0 {
		return s
	}
	delta /= len([]rune(ch))
	return strings.Repeat(ch, delta) + s
}

func formatF64(x float64) string {
	if x == math.Trunc(x) {
		return fmt.Sprintf("%.0f", x)
	} else {
		return fmt.Sprintf("%.2f", x)
	}
}
