package ui

import (
	"os"
	"strings"

	"github.com/shirou/gopsutil/v4/cpu"
	"github.com/shirou/gopsutil/v4/process"
)

// Repeat a string some number of times.
func Repeat(times int, str string) string {
	var s strings.Builder
	for range times {
		s.WriteString(str)
	}
	return s.String()
}

type resources struct {
	memory        uint64 // bytes.
	memoryPercent float32
	cpuPercent    float64
}

// Gets information about the system resources being used by the process.
func getResources() resources {

	r := resources{}

	c, _ := cpu.Percent(0, false)

	proc, _ := process.NewProcess(int32(os.Getpid()))
	r.cpuPercent = c[0]
	r.memoryPercent, _ = proc.MemoryPercent()
	m, _ := proc.MemoryInfo()
	r.memory = m.RSS

	r.cpuPercent /= 100
	r.memoryPercent /= 100

	return r
}
