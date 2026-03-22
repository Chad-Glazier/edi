# Export the memory configuration.
$env:MAVEN_OPTS="-Xmx4g -Xms1g";

# Build `core` and run the main method.
mvn -pl core clean `
	package "-Dmaven.test.skip=true" `
	exec:java "-Dexec.mainClass=ubc.team09.demo.RunGame";