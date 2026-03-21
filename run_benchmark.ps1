# Build the entire project
mvn clean 
mvn install -DskipTests;

# Build the jar file for the benchmarks.
mvn verify -pl benchmarking -DskipTests;

# Run the benchmarks jar.
java -jar ".\benchmarking\target\benchmarks.jar";
