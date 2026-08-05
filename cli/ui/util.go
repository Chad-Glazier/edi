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

type cpuStats struct {
	model     string
	frequency float64 // MHz.
	cores     int32
}
type resources struct {
	memory        uint64 // bytes.
	memoryPercent float32
	cpuPercent    float64
	cpuStats      cpuStats
}

// Gets information about the system resources being used by the process.
func getResources() resources {

	r := resources{}

	c, _ := cpu.Info()
	r.cpuStats.model = c[0].ModelName
	r.cpuStats.frequency = c[0].Mhz
	r.cpuStats.cores = c[0].Cores

	proc, _ := process.NewProcess(int32(os.Getpid()))
	r.cpuPercent, _ = proc.CPUPercent()
	r.memoryPercent, _ = proc.MemoryPercent()
	m, _ := proc.MemoryInfo()
	r.memory = m.RSS

	r.cpuPercent /= 100
	r.memoryPercent /= 100

	return r
}
