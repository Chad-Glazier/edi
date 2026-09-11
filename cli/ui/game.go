package ui

import (
	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

// Groups elements of a game UI into a single view.
func GameLayout(
	width, height int, // The dimensions of the model.
	systemResources SystemResources,
	board BoardModel,
	boardCaption string,
) tea.View {
	return tea.NewView(lipgloss.Place(
		width, height,
		lipgloss.Center,
		lipgloss.Center,
		lipgloss.JoinVertical(
			lipgloss.Center,
			systemResources.View(),
			"\n\n",
			board.View(),
			"\n",
			boardCaption,
		),
	))
}

func GameLayoutWithAnalytics(
	width, height int, // The dimensions of the model.
	systemResources SystemResources,
	board BoardModel,
	boardCaption string,
	analytics string,
) tea.View {
	return tea.NewView(lipgloss.Place(
		width, height,
		lipgloss.Center,
		lipgloss.Center,
		lipgloss.JoinVertical(
			lipgloss.Center,
			systemResources.View(),
			"\n\n",
			lipgloss.JoinHorizontal(
				lipgloss.Center,
				board.View(),
				analytics,
			),
			"\n",
			boardCaption,
		),
	))
}
