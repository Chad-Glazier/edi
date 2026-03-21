# Game of the Amazons AI

This project is meant to implement a simple AI to play the [Game of the Amazons](https://en.wikipedia.org/wiki/Game_of_the_Amazons). This is Team 9's group project for COSC 322 "Introduction to Artificial Intelligence," taught by Dr. Yong Gao during the second 2026 Winter term at UBC-O. The group members include:
- Chad Glazier
- Guohao Ma
- Zhishang Ma
- Aaryan Oberoi

## Running the Project

You can run a demo of the project, which makes the bot play against itself, with this command:

```sh
mvn exec:java "-Dexec.mainClass=ubc.cosc322.demo.RunGame"
```

### Running in Tournaments

When running the bot in a tournament, make sure that you have a clean installation.

```sh
mvn clean package
mvn exec:java -Dexec.mainClass=ubc.cosc322.demo.RunGame
```

Additionally, make sure that you've set the memory constraints. 

If you're on Windows, you can run everything by executing `.\run.ps1` in PowerShell (from this directory). This script will re-build the program, run the unit tests, and then start it with the increased memory limit.

## Guidelines for Making Contributions

(*This section is for group members.*)

The following are a list of rules for contributing to the project when making any significant changes (i.e. anything other than a minor bug fix or change to documentation):
1) Open an issue (you can do that from [here](https://github.com/Chad-Glazier/game-of-the-amazons-ai/issues)).
1) Create a new branch for the issue (i.e. go to the issue page, and click on the "create a branch" link).
2) Make changes to the branch.
3) Submit a pull request to merge the branch to `main`.
4) Wait for another group member to review the changes and merge the changes. Once the PR is merged, the issue should automatically be marked as closed.

## Unit Tests

We are using the [JUnit](https://junit.org/) testing framework.

You can add unit tests to the [test folder](src/test/java/ubc/cosc322/), and then execute them with:

```sh
mvn test
```

The file structure of the test folder should mirror the main folder, and tests for a given class `ClassName` should be put in a class named `ClassNameTest`. See [BitBoardTest](src/test/java/ubc/cosc322/util/BitBoardTest.java) for a simple example.

### Optional Tests

In the [POM](./pom.xml) file, there is this line:

```xml
<excludedGroups>SearchTest</excludedGroups>
```

If you comment out that line, then the search tests will be included in the test suite. They are normally excluded because they involve simulating games, testing time constraits, and so on, so they are very slow.

## Dependencies 

This project depends on the following packages:
- [Junit](https://docs.junit.org/5.10.5/user-guide/), which is used for creating the unit tests [here](src/test/java/ubc/cosc322/).
