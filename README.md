# EDI: A Program to Play the Game of Amazons

This project is meant to implement a simple AI to play the [Game of the Amazons](https://en.wikipedia.org/wiki/Game_of_the_Amazons). This is Team 9's group project for COSC 322 "Introduction to Artificial Intelligence," taught by Dr. Yong Gao during the second 2026 Winter term at UBC-O. The group members include:
- Chad Glazier
- Guohao Ma
- Zhishang Ma
- Aaryan Oberoi

## Running the Program

At the top level, you can see three scripts:
- [run_benchmark.ps1](./run_benchmark.ps1) will run the benchmarks for the project (from the [benchmarking module](./benchmarking/)).
- [run_unit_tests.ps1](./run_unit_tests.ps1) will just run the unit tests for the core project (from [here](./core/src/test/java/com/chadglazier/)).
- [run.ps1](./run.ps1) will run the main program, which can be used for playing against other opponents on the game server.

These scripts are written for PowerShell, but they are really just Maven commands and they can be run in other shells if you reformat them slightly.

## Guidelines for Making Contributions

(*This section is for group members.*)

The following are a list of rules for contributing to the project when making any significant changes (i.e. anything other than a minor bug fix or change to documentation):
1) Open an issue (you can do that from [here](https://github.com/Chad-Glazier/game-of-the-amazons-ai/issues)).
1) Create a new branch for the issue (i.e. go to the issue page, and click on the "create a branch" link).
2) Make changes to the branch.
3) Submit a pull request to merge the branch to `main`.
4) Wait for another group member to review the changes and merge the changes. Once the PR is merged, the issue should automatically be marked as closed.

## Dependencies 

This project depends on the following packages:
- [Junit](https://docs.junit.org/5.10.5/user-guide/) is used for creating unit tests.
- [Java Microbenchmark Harness (JMH)](https://github.com/openjdk/jmh/tree/master) is used in the [benchmarking module](./benchmarking/) to compare the performance of different methods/implementations.

Not listed here is the package used to communicate with the game server used for the in-class tournament, which is written by Dr. Yong Gao.
