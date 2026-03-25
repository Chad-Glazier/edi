mvn clean install;

# Run the stack profiler.
java -jar .\target\benchmarks.jar `
	SearchProfiling.AB `
	-prof stack;

# # Run the GC profiler
# java -jar .\target\benchmarks.jar `
# 	SearchProfiling.AB `
# 	-prof gc;
