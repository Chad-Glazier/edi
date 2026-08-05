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
	LINE_HORIZONTAL     = "\u2500" // ─
	LINE_VERTICAL       = "\u2502" // │
	CORNER_TOP_LEFT     = "\u250C" // ┌
	CORNER_TOP_RIGHT    = "\u2510" // ┐
	CORNER_BOTTOM_LEFT  = "\u2514" // └
	CORNER_BOTTOM_RIGHT = "\u2518" // ┘
	T_VERTICAL_RIGHT    = "\u251C" // ├
	T_VERTICAL_LEFT     = "\u2524" // ┤
	T_HORIZONTAL_DOWN   = "\u252C" // ┬
	T_HORIZONTAL_UP     = "\u2534" // ┴
	CROSS               = "\u253C" // ┼
	DIAG_UPPER_RIGHT    = "\u2571" // ╱
	DIAG_LOWER_LEFT     = "\u2571" // ╱
	DIAG_UPPER_LEFT     = "\u2572" // ╲
	DIAG_LOWER_RIGHT    = "\u2572" // ╲
	WHITE_QUEEN_SQUARE  = "\u25A0" // ■
	BLACK_QUEEN_SQUARE  = "\u25A0" // ■
	ARROW_SQUARE        = "\u2715" // ✕
	VACANT_SQUARE       = "\u00B7" // ·
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
			CORNER_TOP_LEFT +
			Repeat(21, LINE_HORIZONTAL) +
			CORNER_TOP_RIGHT,
	}

	for row := range 10 {
		var line strings.Builder
		fmt.Fprintf(&line, "%d %s", row, LINE_VERTICAL)
		for col := range 10 {
			var s string
			switch b.State.Status(bb.Pos(row, col)) {
			case state.VACANT:
				s = FgBrightBlack(VACANT_SQUARE)
			case state.WHITE_QUEEN:
				s = FgBrightCyan(WHITE_QUEEN_SQUARE)
			case state.BLACK_QUEEN:
				s = FgBrightRed(BLACK_QUEEN_SQUARE)
			case state.ARROW:
				s = FgBrightBlack(ARROW_SQUARE)
			}
			line.WriteString(" ")
			line.WriteString(s)
		}
		line.WriteString(" " + LINE_VERTICAL)
		lines = append(lines, line.String())
	}
	lines = append(lines,
		"  "+
			CORNER_BOTTOM_LEFT+
			Repeat(21, LINE_HORIZONTAL)+
			CORNER_BOTTOM_RIGHT,
	)

	return b.Style.Render(strings.Join(lines, "\n"))
}

//
// Messages.
//

type SetBoardMsg state.Board
