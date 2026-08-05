package ui

import (
	"fmt"
	"time"

	"charm.land/bubbles/v2/progress"
	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

//
// Styles.
//

type Styles struct {
	Border lipgloss.Style
	Label  lipgloss.Style
	Value  lipgloss.Style
}

func DefaultStyles() Styles {
	return Styles{
		Border: lipgloss.NewStyle(),
		Label:  lipgloss.NewStyle().Bold(true),
		Value:  lipgloss.NewStyle(),
	}
}

//
// Model State.
//

type SystemResources struct {
	res        resources
	styles     Styles
	progress   progress.Model
	peakMemory uint64
	width      int
}

func NewSystemResources() SystemResources {
	p := progress.New(progress.WithDefaultBlend())
	p.SetWidth(20)

	return SystemResources{
		res:        getResources(),
		styles:     DefaultStyles(),
		progress:   p,
		peakMemory: 0,
	}
}

//
// Custom Messages and Commands.
//

type TickMsg struct{}

func TickEvery() tea.Cmd {
	return tea.Every(
		time.Second,
		func(t time.Time) tea.Msg {
			return TickMsg{}
		},
	)
}

//
// Bubbletea Methods.
//

func (m SystemResources) Init() tea.Cmd {
	return TickEvery()
}

func (m SystemResources) Update(msg tea.Msg) (SystemResources, tea.Cmd) {

	switch msg.(type) {
	case TickMsg:
		m.res = getResources()

		if m.res.memory > m.peakMemory {
			m.peakMemory = m.res.memory
		}

		m.progress.SetPercent(m.res.cpuPercent)

		return m, TickEvery()
	}

	return m, nil
}

func (m SystemResources) View() string {
	r := m.res

	cpuBar := m.progress.ViewAs(r.cpuPercent)

	currentMemMB := float64(r.memory) / 1024 / 1024
	peakMemMB := float64(m.peakMemory) / 1024 / 1024

	body := fmt.Sprintf(
		"%s %s\t%s %4.0f MB\t%s %4.0f MB",
		m.styles.Label.Render("CPU"),
		cpuBar,

		m.styles.Label.Render("Memory:"),
		currentMemMB,

		m.styles.Label.Render("Peak:"),
		peakMemMB,
	)

	panel := m.styles.Border.Render(body)

	return panel
}
