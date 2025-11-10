# Sokoban Game Simulator

A C implementation of the classic Sokoban puzzle game with automatic pathfinding and undo functionality.

## About Sokoban

Sokoban is a classic puzzle game where the player pushes boxes around a warehouse, trying to place them on designated goal positions. The challenge lies in navigating tight spaces without getting boxes stuck in corners or against walls.

### Game Elements

- `@` - Player
- `*` - Player on goal position
- `-` - Empty space
- `+` - Goal position (target for boxes)
- `#` - Wall
- `a-z` - Box (lowercase = box on empty space)
- `A-Z` - Box on goal position (uppercase)

## Features

### Intelligent Movement
Unlike traditional Sokoban implementations, this program features **automatic pathfinding**:
- Players don't need to manually navigate empty spaces
- The program automatically calculates the shortest path to push boxes
- Uses Breadth-First Search (BFS) algorithm to find optimal routes

### Core Functionality
- **Box pushing**: Move boxes in four directions (up, down, left, right)
- **Undo system**: Revert previous moves using a stack-based history
- **Multiple boxes**: Track and manage multiple boxes with unique identifiers (a-z)
- **Goal tracking**: Boxes change appearance when placed on goal positions

## Input Format

### Board Description
The input begins with the initial board state, followed by a blank line:
```
#####
#@a-#
#-+-#
#####
```

- Each line represents a row on the board
- The board is terminated by a blank line (two consecutive newlines)

### Command Format

Commands follow the board description:

- **Print board**: `\n` (newline) - displays current state
- **Push box**: `<box><direction>` - push specific box in direction
  - `<box>`: Letter identifying the box (a-z)
  - `<direction>`: Numeric direction code
    - `8` - UP
    - `2` - DOWN  
    - `4` - LEFT
    - `6` - RIGHT
- **Undo**: `0` - revert last push
- **End input**: `.` - terminates command sequence

### Example Input
```
#######
#@a-b+#
#--+--#
#######

a6
b6

.
```

This:
1. Displays initial board
2. Pushes box 'a' right
3. Pushes box 'b' right
4. Displays final board
5. Ends execution

## Output Format

The program outputs the board state after each print command (`\n`):
```
#######
#@a-b+#
#--+--#
#######

#######
#-@aB+#
#--+--#
#######
```

Note: Box `b` becomes `B` (uppercase) when placed on a goal position (`+`).

## Algorithm Details

### Pathfinding (BFS)
When a push command is issued:
1. Program locates the player and target box
2. Calculates the position player must reach (behind the box)
3. Uses BFS to determine if that position is reachable
4. Only executes push if player can reach the required position

### Move Validation
Before executing a push, the program checks:
- Target position is within board bounds
- Destination cell is accessible (not a wall)
- Player can reach the position behind the box
- Box has space to move in the specified direction

### Undo System
- Maintains a stack of previous board states
- Each successful push saves current board state
- Undo command (`0`) restores previous state
- Multiple undos supported 

## Game Rules

1. Player can only push boxes, not pull them
2. Only one box can be moved at a time
3. Boxes cannot be pushed through walls or other boxes
4. Game typically ends when all boxes are on goal positions
5. Invalid commands are silently ignored

## Project Information

- **Author:** Maja Kołodziejczyk
- **Date:** January 2025
- **Institution:** University of Warsaw - Faculty of Mathematics, Informatics and Mechanics
- **Course:** Introduction to Programming (IPP)

## Assumptions

- Input data is correct and well-formed
- Each input line ends with newline character (`\n`)
- Board dimensions fit within available memory
- Maximum 26 boxes (limited by alphabet a-z)
- No memory overflow occurs during execution