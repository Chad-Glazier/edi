package analyze

import (
	"time"

	"github.com/Chad-Glazier/edi"
	"github.com/Chad-Glazier/edi/cli/cmd/flags"
	"github.com/Chad-Glazier/edi/cli/sim"
	"github.com/Chad-Glazier/edi/cli/ui"
	"github.com/Chad-Glazier/edi/state"

	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

//
// Defining the model state.
//

type gameModel struct {
	height int
	width  int

	white     edi.VI
	black     edi.VI
	turnTimer time.Duration
	game      <-chan state.Board
	winner    *state.PlayerColor

	viSelector      ui.VISelector
	board           ui.BoardModel
	timeSelector    ui.TimeSelector
	systemResources ui.SystemResources
}

func NewGameModel(vi flags.VI, turnTimer time.Duration) gameModel {
	m := gameModel{
		turnTimer:       turnTimer,
		viSelector:      ui.NewVISelector(ui.NEUTRAL),
		timeSelector:    ui.NewTimeSelector(),
		board:           ui.NewBoardModel(),
		systemResources: ui.NewSystemResources(),
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
// postconditions are not. That is, we ensure that everything is necessary for
// the state to be started, and the state is not yet "finished."
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
		m.game = sim.GameWithAnalytics(m.white, m.black, m.turnTimer)
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

		wAnalytics, err := ui.AnalyticsView(m.white)
		if err != nil {
			wAnalytics = "no analytics for white"
		}
		bAnalytics, err := ui.AnalyticsView(m.black)
		if err != nil {
			bAnalytics = "no analytics for black"
		}
		v := ui.GameLayoutWithAnalytics(
			m.width, m.height,
			m.systemResources,
			m.board,
			caption,
			lipgloss.JoinHorizontal(
				lipgloss.Center,
				wAnalytics,
				bAnalytics,
			),
		)
		v.AltScreen = true
		return v
	case m.ShowingEndScreen():
		caption := ""
		switch *m.winner {
		case state.WHITE:
			caption += ui.FgBrightCyan(m.white.Id())
			caption += " wins against "
			caption += ui.FgBrightRed(m.black.Id())
		case state.BLACK:
			caption += ui.FgBrightRed(m.black.Id())
			caption += " wins against "
			caption += ui.FgBrightCyan(m.white.Id())
		}
		v := ui.GameLayout(
			m.width, m.height,
			m.systemResources,
			m.board,
			caption,
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
			case state.WHITE:
				return GameOverMsg{winner: state.BLACK}
			case state.BLACK:
				return GameOverMsg{winner: state.WHITE}
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
