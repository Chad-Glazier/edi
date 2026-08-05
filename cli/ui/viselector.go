package ui

import (
	"github.com/Chad-Glazier/edi"

	"charm.land/bubbles/v2/list"
	tea "charm.land/bubbletea/v2"
	"charm.land/lipgloss/v2"
)

//
// Defining the list items.
//

type item struct {
	title string
	desc  string
	new   func() edi.VI
}

func (i item) Title() string       { return i.title }
func (i item) Description() string { return i.desc }
func (i item) FilterValue() string { return i.title }

var VIList = []list.Item{
	item{
		title: "EDI",
		desc:  "Alpha-beta search with the k-mindist heuristic for leaf evaluation and the History Heuristic for move ordering.",
		new:   edi.NewEDI,
	},
	item{
		title: "Arrow",
		desc:  "Alpha-beta search with the q-mindist heuristic for leaf evaluation and no move ordering.",
		new:   edi.NewArrow,
	},
	item{
		title: "Sparrow",
		desc:  "Alpha-beta search with the k-mindist heuristic for leaf evaluation and no move ordering.",
		new:   edi.NewSparrow,
	},
	item{
		title: "Random",
		desc:  "Chooses moves at random.",
		new:   edi.NewRandom,
	},
}

//
// Defining the model state.
//

type VISelectorStyle uint8

const (
	WHITE VISelectorStyle = iota
	BLACK
	NEUTRAL
)

type VISelector struct {
	list  list.Model
	VI    edi.VI
	NewVI func() edi.VI
	Style lipgloss.Style
}

func NewVISelector(style VISelectorStyle) VISelector {
	v := VISelector{}
	v.list = list.New(VIList, list.NewDefaultDelegate(), 0, 0)
	switch style {
	case WHITE:
		setWhiteStyles(&v)
	case BLACK:
		setBlackStyles(&v)
	default:
		setNeutralStyles(&v)
	}
	return v
}

//
// Bubbletea methods.
//

func (m VISelector) Init() tea.Cmd {
	return nil
}

func (m VISelector) Update(msg tea.Msg) (VISelector, tea.Cmd) {
	switch msg := msg.(type) {
	case tea.KeyPressMsg:
		if msg.String() == "ctrl+c" {
			return m, tea.Quit
		}
		if msg.String() == "enter" {
			item := m.list.SelectedItem().(item)
			m.VI = item.new()
			m.NewVI = item.new
			return m, tea.Quit
		}
	case tea.WindowSizeMsg:
		h, v := m.Style.GetFrameSize()
		m.list.SetSize(msg.Width-h, msg.Height-v)
	}

	var cmd tea.Cmd
	m.list, cmd = m.list.Update(msg)
	return m, cmd
}

func (m VISelector) View() tea.View {
	v := tea.NewView(m.Style.Render(m.list.View()))
	v.AltScreen = true
	return v
}

//
// Styling functions.
//

func setNeutralStyles(m *VISelector) {
	m.list.Title = " Select a VI "

	green := lipgloss.Color("#007bff")
	lime := lipgloss.Color("#26ff00")
	lightgray := lipgloss.Color("#eeeeee")
	white := lipgloss.Color("#ffffff")

	d := list.NewDefaultDelegate()
	d.Styles.SelectedTitle = d.Styles.SelectedTitle.
		Foreground(lime).
		BorderLeftForeground(lime)
	d.Styles.SelectedDesc = d.Styles.SelectedDesc.
		Foreground(lightgray).
		BorderLeftForeground(lime)
	m.list.SetDelegate(d)
	m.list.Styles.Title = lipgloss.NewStyle().
		Background(green).
		Foreground(white)
}

func setWhiteStyles(m *VISelector) {
	m.list.Title = " Select who plays White "

	blue := lipgloss.Color("#007bff")
	cyan := lipgloss.Color("#03befc")
	lightgray := lipgloss.Color("#eeeeee")
	white := lipgloss.Color("#ffffff")

	d := list.NewDefaultDelegate()

	d.Styles.SelectedTitle = d.Styles.SelectedTitle.
		Foreground(cyan).
		BorderLeftForeground(cyan)
	d.Styles.SelectedDesc = d.Styles.SelectedDesc.
		Foreground(lightgray).
		BorderLeftForeground(cyan)
	m.list.SetDelegate(d)

	m.list.Styles.Title = lipgloss.NewStyle().
		Background(blue).
		Foreground(white)
}

func setBlackStyles(m *VISelector) {
	m.list.Title = " Select who plays Black "

	red := lipgloss.Color("#c60000")
	lightRed := lipgloss.Color("#ff7070")
	lightgray := lipgloss.Color("#eeeeee")
	white := lipgloss.Color("#ffffff")

	d := list.NewDefaultDelegate()

	d.Styles.SelectedTitle = d.Styles.SelectedTitle.
		Foreground(lightRed).
		BorderLeftForeground(lightRed)
	d.Styles.SelectedDesc = d.Styles.SelectedDesc.
		Foreground(lightgray).
		BorderLeftForeground(lightRed)
	m.list.SetDelegate(d)

	m.list.Styles.Title = lipgloss.NewStyle().
		Background(red).
		Foreground(white)
}
