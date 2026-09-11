package ui

import (
	"fmt"
	"image/color"
	"math"
	"sort"

	"github.com/Chad-Glazier/edi/state"
	"github.com/Chad-Glazier/edi/vi"

	"charm.land/bubbles/v2/table"
	"charm.land/lipgloss/v2"
)

// Returns a formatted string representation of a VI's analytics.
func AnalyticsView(v vi.VI, colour state.PlayerColor) string {

	analytics := v.Analytics()

	orderedKeys := make([]string, 0)
	for key := range analytics {
		orderedKeys = append(orderedKeys, key)
	}
	sort.Strings(orderedKeys)

	columns := []table.Column{
		{Title: "Metric", Width: 16},
		{Title: "Latest Value", Width: 12},
	}

	rows := []table.Row{}
	for _, key := range orderedKeys {

		val := analytics[key]
		valStr := formatF64(val)

		rows = append(rows, table.Row{key, valStr})
	}

	t := table.New(
		table.WithColumns(columns),
		table.WithRows(rows),
		table.WithHeight(10),
		table.WithWidth(30),
		table.WithFocused(false),
	)
	setTableStyles(&t, colour)

	return t.View()
}

func setTableStyles(t *table.Model, colour state.PlayerColor) {
	var (
		borderColor     color.Color
		foregroundColor color.Color
		backgroundColor color.Color
	)
	if colour == state.White {
		backgroundColor = lipgloss.Color("#03befc")
		borderColor = lipgloss.Color("#03befc")
		foregroundColor = lipgloss.Color("#eeeeee")
	} else {
		backgroundColor = lipgloss.Color("#c60000")
		borderColor = lipgloss.Color("#c60000")
		foregroundColor = lipgloss.Color("#eeeeee")
	}

	s := table.DefaultStyles()
	s.Header = s.Header.
		Foreground(backgroundColor).
		Bold(false).
		BorderStyle(lipgloss.NormalBorder()).
		BorderBottom(true).
		BorderForeground(borderColor)
	s.Selected = s.Selected.
		Foreground(foregroundColor).
		Bold(false)
	s.Cell = s.Cell.
		Foreground(foregroundColor).
		BorderStyle(lipgloss.DoubleBorder()).
		BorderLeft(true).
		BorderForeground(borderColor)

	t.SetStyles(s)
}

//
// Helper functions
//

// Converts a float64 to a string with a precision of two fractional digits.
// However, in the case that the float represents a whole number, there will
// be no fractional digits.
func formatF64(x float64) string {
	if x == math.Trunc(x) {
		return fmt.Sprintf("%.0f", x)
	} else {
		return fmt.Sprintf("%.2f", x)
	}
}
