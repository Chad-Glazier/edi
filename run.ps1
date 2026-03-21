$env:MAVEN_OPTS="-Xmx4g -Xms1g";
mvn clean package;
mvn exec:java "-Dexec.mainClass=ubc.cosc322.Main";
