# Intelligent Delivery Robot - Dynamic Programming Solver

## 1. Project Overview

This project is for the **Design and Analysis of Algorithms** course. The goal is to design and implement an intelligent delivery robot that moves inside a restaurant represented as an `n x m` grid.

The robot starts from cell `S`, moves only **right** or **down**, avoids obstacles, may use teleport gates, and tries to serve the maximum possible number of customers `G` using at most `k` units of energy.

If two paths serve the same number of customers, the algorithm chooses the path that uses less energy.

---

## 2. Grid Symbols

| Symbol | Meaning |
|---|---|
| `S` | Start position of the robot |
| `G` | Customer cell |
| `X` | Obstacle / blocked cell |
| `T` | Teleport source cell |
| `.` | Empty cell |

---

## 3. Movement Rules

1. The robot can move only:
   - Right: `(row, col + 1)`
   - Down: `(row + 1, col)`
2. Each normal move costs `1` unit of energy.
3. Entering a teleport cell `T` also costs `1` unit of energy.
4. After entering a teleport cell, the robot is transferred immediately to its destination.
5. The teleport transfer itself has no extra energy cost.
6. The robot cannot enter an obstacle cell `X`.
7. The robot can stop anywhere; it does not have to end at a specific destination.
8. Teleport destinations must move forward:
   - `destinationRow >= sourceRow`
   - `destinationCol >= sourceCol`

---

## 4. Objective

The solver optimizes the result using this priority order:

1. Maximize the number of served customers.
2. If there is a tie, minimize the amount of used energy.

Output:

```text
Max customers served: <number>
Energy used: <number>
Path:
<path cells>
```

---

## 5. Algorithm

The project uses **Dynamic Programming** with an energy dimension.

### DP State

```text
dp[row][col][energy]
```

This state stores the maximum number of customers that can be served when the robot reaches cell `(row, col)` after using exactly `energy` units of energy.

If a state is unreachable, its value is `-1`.

### Initial State

```text
dp[startRow][startCol][0] = 0
```

### Transition

From every reachable state, the robot tries to move right and down.

For a normal cell:

```text
nextEnergy = energy + 1
newCustomers = dp[row][col][energy] + customerValue(nextRow, nextCol)
```

For a teleport cell:

```text
nextEnergy = energy + 1
finalCell = teleportDestination(nextRow, nextCol)
newCustomers = dp[row][col][energy] + customerValue(finalCell)
```

The teleport jump itself does not add extra energy.

---

## 6. Path Reconstruction

The solver stores a `Parent` object for every updated DP state.

The parent keeps:

- previous row
- previous column
- previous energy
- whether a teleport was used
- teleport source position, when needed

After solving, the program starts from the best final state and moves backward through the parent links until it reaches the start state.

Then the reconstructed path is reversed and printed in the correct order.

---

## 7. Time and Space Complexity

Let:

- `n` = number of rows
- `m` = number of columns
- `k` = maximum energy

There are `n * m * (k + 1)` DP states.

For each state, the robot checks two possible moves: right and down.

### Time Complexity

```text
O(n * m * k)
```

### Space Complexity

```text
O(n * m * k)
```

The space is used by:

- the DP table
- the parent table for path reconstruction

---

## 8. Class Responsibilities

| Class | Responsibility |
|---|---|
| `Cell` | Stores row and column of a grid cell |
| `TeleportDefinition` | Stores one teleport source and destination |
| `TeleportManager` | Stores and retrieves teleport destinations |
| `InputValidator` | Validates dimensions, grid symbols, start cell, and teleports |
| `Parent` | Stores previous state for path reconstruction |
| `Result` | Stores final answer: customers, energy, and path |
| `RobotSolver` | Runs the DP algorithm and reconstructs the optimal path |
| `Main` | Handles user input, sample execution, and output printing |

---

## 9. How to Compile and Run

Open a terminal in the `src` folder and run:

```bash
javac *.java
java Main
```

Then choose one of the menu options:

```text
1. Enter input manually
2. Paste full input
3. Run official built-in sample test
4. Show input guide
0. Exit
```

---

## 10. Input Format

First line:

```text
n m k
```

Next `n` lines:

```text
grid row values
```

Then one line for each teleport:

```text
Teleport: (sourceRow,sourceCol) -> (destRow,destCol)
```

Rows and columns are zero-indexed.

---

## 11. Official Sample Input

```text
6 6 11
S . . X . G
. X . X . .
. . T . X .
X . . . . .
. X . X . G
. . . . . G
Teleport: (2,2) -> (4,4)
```

## 12. Expected Output

```text
Max customers served: 2
Energy used: 6
Path:
(0,0) -> (0,1) -> (0,2) -> (1,2) -> (2,2) -> T -> (4,4) -> (4,5) -> (5,5)
```

---

## 13. Improvements Added in This Version

1. `InputValidator` is now the central validation class.
2. Duplicate validation logic was removed from `Main`.
3. `RobotSolver` now checks missing teleport definitions before using a teleport.
4. `RobotSolver` validates teleport destinations before updating DP states.
5. Path reconstruction now throws a clear error if parent data is missing unexpectedly.
6. The built-in sample now matches the official 6x6 project sample.
7. Small model classes use final fields for cleaner and safer data handling.
8. A complete README file was added.

---

## 14. Suggested Test Cases

You should test the program with:

1. `k = 0`
2. No reachable customer
3. Teleport destination is a customer `G`
4. Multiple teleport cells
5. Blocked path with obstacles
6. Small energy values
7. Large grid with large `k`



## Compile from source

```sh
javac -d build src/*.java
java -cp build Main
```

The project is intended for an algorithms course and uses standard Java.
