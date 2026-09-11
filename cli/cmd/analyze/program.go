package analyze

import (
	"os"
	"strings"
	"time"

	"github.com/Chad-Glazier/edi/cli/cmd/flags"
	"github.com/Chad-Glazier/edi/cli/ui"
	"github.com/Chad-Glazier/edi/state"
	"github.com/Chad-Glazier/edi/vi"

	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

//
// Defining the model state.
//

type gameModel struct {
	height int
	width  int

	white          vi.VI
	black          vi.VI
	turnTimer      time.Duration
	game           <-chan state.Board
	winner         *state.PlayerColor
	outputFilename string

	viSelector      ui.VISelector
	board           ui.BoardModel
	timeSelector    ui.TimeSelector
	systemResources ui.SystemResources
}

func NewGameModel(
	vi flags.VI,
	turnTimer time.Duration,
	outputFilename string,
) gameModel {
	m := gameModel{
		turnTimer:       turnTimer,
		viSelector:      ui.NewVISelector(ui.NEUTRAL),
		timeSelector:    ui.NewTimeSelector(),
		board:           ui.NewBoardModel(),
		systemResources: ui.NewSystemResources(),
		outputFilename:  outputFilename,
	}

	if vi.New != nil {
		m.white = vi.New()
		m.black = vi.New()
	}

	return m
}

//
// Helper Methods.
//
// These functions are meant to check what broad state the UI is in. For
// example, whether the user is currently selecting the timer, or the game is
// running, etc. Such states are determined by checking whether the
// preconditions for the state are satisfied and ensuring that the
// postconditions are not.
//

func (m *gameModel) ChoosingTimer() bool {
	preconditions := true
	postconditions := m.turnTimer != 0

	return preconditions && !postconditions
}

func (m *gameModel) ChoosingVI() bool {
	preconditions := !m.ChoosingTimer()
	postconditions := m.white != nil

	return preconditions && !postconditions
}

func (m *gameModel) ReadyToStartGame() bool {
	preconditions :=
		!m.ChoosingTimer() &&
			!m.ChoosingVI()
	postconditions := m.game != nil

	return preconditions && !postconditions
}

func (m *gameModel) RunningGame() bool {
	preconditions := m.game != nil
	postconditions := m.winner != nil

	return preconditions && !postconditions
}

func (m *gameModel) ShowingEndScreen() bool {
	preconditions := m.winner != nil
	postconditions := false

	return preconditions && !postconditions
}

// Returns true if and only if the file was successfully written.
func (m *gameModel) WriteAnalyticsFile() bool {
	if m.outputFilename == "" {
		return false
	}

	var (
		parts         = strings.Split(m.outputFilename, ".")
		whiteFilename string
		blackFilename string
	)

	switch len(parts) {
	case 0:
		return false
	case 1:
		whiteFilename = parts[0] + "_white.csv"
		blackFilename = parts[0] + "_black.csv"
	default:
		wPart := parts[len(parts)-2] + "_white"
		bPart := parts[len(parts)-2] + "_black"

		parts[len(parts)-2] = wPart
		whiteFilename = strings.Join(parts, ".")
		parts[len(parts)-2] = bPart
		blackFilename = strings.Join(parts, ".")
	}

	wOut, err := os.Create(whiteFilename)
	if err != nil {
		return false
	}
	defer wOut.Close()

	bOut, err := os.Create(blackFilename)
	if err != nil {
		return false
	}
	defer bOut.Close()

	vi.DumpAnalyticsCsv(m.white, wOut)
	vi.DumpAnalyticsCsv(m.black, bOut)
	return true
}

//
// Bubbletea methods.
//

func (m gameModel) Init() tea.Cmd {
	return tea.Batch(
		m.viSelector.Init(),
		m.timeSelector.Init(),
		m.board.Init(),
		m.systemResources.Init(),
	)
}

func (m gameModel) Update(msg tea.Msg) (tea.Model, tea.Cmd) {

	switch {
	case m.ChoosingTimer():
		m.timeSelector, _ = m.timeSelector.Update(msg)
		m.turnTimer = m.timeSelector.TurnTimer
	case m.ChoosingVI():
		m.viSelector, _ = m.viSelector.Update(msg)
		if m.viSelector.NewVI != nil {
			m.white = m.viSelector.NewVI()
			m.black = m.viSelector.NewVI()
		}
	case m.RunningGame():
		switch msg := msg.(type) {
		case ui.SetBoardMsg:
			m.board, _ = m.board.Update(msg)
			return m, awaitGameUpdate(&m)
		case GameOverMsg:
			m.winner = &msg.winner
			return m, tea.Quit
		}
	}

	switch msg := msg.(type) {
	case tea.WindowSizeMsg:
		m.width = msg.Width
		m.height = msg.Height
		m.viSelector, _ = m.viSelector.Update(msg)
		m.timeSelector, _ = m.timeSelector.Update(msg)
		m.board, _ = m.board.Update(msg)
	case tea.KeyPressMsg:
		switch msg.String() {
		case "ctrl+c", "q":
			return m, tea.Quit
		}
	case ui.TickMsg:
		newSystemResources, tickCmd := m.systemResources.Update(msg)
		m.systemResources = newSystemResources
		return m, tickCmd
	}

	if m.ReadyToStartGame() {
		m.game = vi.Game(m.white, m.black, m.turnTimer)
		return m, awaitGameUpdate(&m)
	}

	return m, nil
}

func (m gameModel) View() tea.View {
	switch {
	case m.width == 0:
		return tea.NewView(ui.FgBrightBlack("Loading..."))
	case m.ChoosingTimer():
		return m.timeSelector.View()
	case m.ChoosingVI():
		return m.viSelector.View()
	case m.RunningGame():
		caption := ui.FgBrightCyan(m.white.Id())
		caption += " vs "
		caption += ui.FgBrightRed(m.white.Id())

		wAnalytics := ui.AnalyticsView(m.white, state.White)
		bAnalytics := ui.AnalyticsView(m.black, state.Black)

		v := ui.GameLayoutWithAnalytics(
			m.width, m.height,
			m.systemResources,
			m.board,
			caption,
			lipgloss.JoinHorizontal(
				lipgloss.Center,
				"   ",
				wAnalytics,
				"   ",
				bAnalytics,
			),
		)
		v.AltScreen = true
		return v
	case m.ShowingEndScreen():
		caption := ""
		switch *m.winner {
		case state.White:
			caption += ui.FgBrightCyan(m.white.Id())
			caption += " wins against "
			caption += ui.FgBrightRed(m.black.Id())
		case state.Black:
			caption += ui.FgBrightRed(m.black.Id())
			caption += " wins against "
			caption += ui.FgBrightCyan(m.white.Id())
		}

		if ok := m.WriteAnalyticsFile(); ok {
			caption += "\noutput written to file"
		}

		wAnalytics := ui.AnalyticsView(m.white, state.White)
		bAnalytics := ui.AnalyticsView(m.black, state.Black)

		v := ui.GameLayoutWithAnalytics(
			m.width, m.height,
			m.systemResources,
			m.board,
			caption,
			lipgloss.JoinHorizontal(
				lipgloss.Center,
				"   ",
				wAnalytics,
				"   ",
				bAnalytics,
			),
		)
		v.AltScreen = false
		return v
	}

	return tea.NewView("Error.")
}

//
// Custom commands.
//

func awaitGameUpdate(m *gameModel) tea.Cmd {
	return func() tea.Msg {
		updatedGameState, ok := <-m.game
		if !ok {
			switch m.board.State.Player {
			case state.White:
				return GameOverMsg{winner: state.Black}
			case state.Black:
				return GameOverMsg{winner: state.White}
			}
		}
		return ui.SetBoardMsg(updatedGameState)
	}
}

//
// Custom messages.
//

type GameOverMsg struct {
	winner state.PlayerColor
}
