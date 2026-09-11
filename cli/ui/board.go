/*
This package contains UI elements, including ANSI escape codes, related utility
functions, and bubbletea components.
*/
package ui

import (
	"fmt"
	"strings"

	"github.com/Chad-Glazier/edi/bb"
	"github.com/Chad-Glazier/edi/state"

	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

const (
	lineHorizontal    = "\u2500" // ─
	lineVertical      = "\u2502" // │
	cornerTopLeft     = "\u250C" // ┌
	cornerTopRight    = "\u2510" // ┐
	cornerBottomLeft  = "\u2514" // └
	cornerBottomRight = "\u2518" // ┘
	tVerticalRight    = "\u251C" // ├
	tVerticalLeft     = "\u2524" // ┤
	tHorizontalDown   = "\u252C" // ┬
	tHorizontalUp     = "\u2534" // ┴
	cross             = "\u253C" // ┼
	diagUpperRight    = "\u2571" // ╱
	diagLowerLeft     = "\u2571" // ╱
	diagUpperLeft     = "\u2572" // ╲
	diagLowerRight    = "\u2572" // ╲
	whiteQueenSquare  = "\u25A0" // ■
	blackQueenSquare  = "\u25A0" // ■
	arrowSquare       = "\u2715" // ✕
	vacantSquare      = "\u00B7" // ·
)

//
// Defining the model state.
//

type BoardModel struct {
	State state.Board
	Style lipgloss.Style
}

func NewBoardModel() BoardModel {
	return BoardModel{
		Style: lipgloss.NewStyle(),
	}
}

//
// Bubbletea methods.
//

func (b BoardModel) Init() tea.Cmd {
	return nil
}

func (b BoardModel) Update(msg tea.Msg) (BoardModel, tea.Cmd) {
	switch msg := msg.(type) {
	case SetBoardMsg:
		b.State = state.Board(msg)
	}

	return b, nil
}

func (b BoardModel) View() string {
	lines := []string{
		"    0 1 2 3 4 5 6 7 8 9 ",
		"  " +
			cornerTopLeft +
			Repeat(21, lineHorizontal) +
			cornerTopRight,
	}

	for row := range 10 {
		var line strings.Builder
		fmt.Fprintf(&line, "%d %s", row, lineVertical)
		for col := range 10 {
			var s string
			switch b.State.Status(bb.Pos(row, col)) {
			case state.StatusVacant:
				s = FgBrightBlack(vacantSquare)
			case state.StatusWhiteQueen:
				s = FgBrightCyan(whiteQueenSquare)
			case state.StatusBlackQueen:
				s = FgBrightRed(blackQueenSquare)
			case state.StatusArrow:
				s = FgBrightBlack(arrowSquare)
			}
			line.WriteString(" ")
			line.WriteString(s)
		}
		line.WriteString(" " + lineVertical)
		lines = append(lines, line.String())
	}

	lines = append(lines,
		"  "+
			cornerBottomLeft+
			Repeat(21, lineHorizontal)+
			cornerBottomRight,
	)

	return b.Style.Render(strings.Join(lines, "\n"))
}

//
// Messages.
//

type SetBoardMsg state.Board
