# Game of the Amazons AI

This project is meant to implement a simple AI to play the [Game of the Amazons](https://en.wikipedia.org/wiki/Game_of_the_Amazons). This is Team 9's group project for COSC 322 "Introduction to Artificial Intelligence," taught by Dr. Yong Gao during the second 2026 Winter term at UBC-O. The group members include:
- Chad Glazier
- Guohao Ma
- Zhishang Ma
- Aaryan Oberoi

# Running the Project

This project should be built with [Maven](https://maven.apache.org/). If you have Maven installed, you can build the project with 

```sh
mvn compile
```

You can then run a test of the project with

```sh
mvn exec:java "-Dexec.mainClass=ubc.cosc322.COSC322Test" "-Dexec.args=username password"
```

# Guidelines for Making Contributions

(*This section is for group members.*)

The following are a list of rules for contributing to the project when making any significant changes (i.e. anything other than a minor bug fix or change to documentation):
1) Open an issue (you can do that from [here](https://github.com/Chad-Glazier/game-of-the-amazons-ai/issues)).
1) Create a new branch for the issue (i.e. go to the issue page, and click on the "create a branch" link).
2) Make changes to the branch.
3) Submit a pull request to merge the branch to `main`.
4) Wait for another group member to review the changes and merge the changes. Once the PR is merged, the issue should automatically be marked as closed.

# About the Code

## The Heuristic Evaluation Function

Heuristic evaluation functions are in the `ubc.cosc322.eval` package. To use a heuristic evaluation function to score a board state, run the following:

```java
import ubc.cosc322.eval.MinDist; // at the time of writing, this is the only 
								 // evaluation function.

// ...

	int[] boardState = // ... some board state

	HeuristicMethod heuristic = new MinDist();
	heuristic.setBoard(boardState); // this part is necessary
	
	double whiteScore = heuristic.evaluate(true);
	//
	// or, if you want to evaluate black's position:
	//
	double blackScore = heuristic.evaluate(false);
```

Note that the board state is an `int[100]`, not an `int[10][10]`. To convert an `int[10][10]` to `int[100]`, you can use the helper function `Util.flatten`, which can be imported from `ubc.cosc322.eval.Util`.

If you want to evaluate multiple board states, just call `.setBoard` with the new board state, then run the `.evaluate` method again. 

## The Search Function

A search function determines how the game tree is traversed. Search functions are located in the `ubc.cosc322.search` package. To use a search function, run something like the following:

```java
import ubc.cosc322.eval.AlphaBeta; // At the time of writing, this is the only
								   // search function.

// ...

	SearchFunction alphaBeta = new AlphaBeta(
		new MinDist(), 	// the heuristic to use for the search.
		(byte) 0		// the player we want to win; 0 for white, 1 for black.
	)
	
	alphaBeta.setTimeLimit(10); // restrict the algorithm to 10 seconds per move.

	alphaBeta.setBoard(board); // assume that you have the board state in an 
							   // int[100] named `board`.

	int move = alphaBeta.execute(); // calculate the next move, represented by 
									// an `int`. See the `util.Move` class to 
									// get meaningful data from this number.
```

After you've done the initial setup, all you need to do to continue using the search function is update its state with `setBoard` before calling `execute` again. 

For more details about the Alpha-Beta search function, see the JavaDoc comment in its [file](src/main/java/ubc/cosc322/search/AlphaBeta.java). To see a demo of Alpha-Beta playing against a naive single-depth algorithm, run the demo like so:

```java
mvn exec:java "-Dexec.mainClass=ubc.cosc322.demo.AlphaBetaVsNaive"
```

## Unit Tests

We are using the [JUnit](https://junit.org/) testing framework.

You can add unit tests to the [test folder](src/test/java/ubc/cosc322/), and then execute them with:

```sh
mvn test
```

The file structure of the test folder should mirror the main folder, and tests for a given class `ClassName` should be put in a class named `ClassNameTest`. See [BitBoardTest](src/test/java/ubc/cosc322/util/BitBoardTest.java) for a simple example.

## Dependencies 

This project depends on the following packages:
- [fastutil](https://javadoc.io/doc/it.unimi.dsi/fastutil/latest/index.html), which we use for alternatives to `ArrayList` that avoid the performance overhead of boxing numeric values.
- [Junit](https://docs.junit.org/5.10.5/user-guide/), which is used for creating the unit tests [here](src/test/java/ubc/cosc322/).