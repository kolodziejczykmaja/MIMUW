# Rubik's Cube Simulator

A C program that simulates an N×N×N Rubik's Cube with support for layer rotations.

## Description

This program executes a sequence of commands that rotate layers of a cube of size N×N×N, where N is a positive integer. The program starts with a solved cube and can print the current state on demand.

The cube consists of movable cubic blocks. The faces of the cube are divided into colored squares belonging to individual blocks. In the solved state, all squares on a given face are covered with one color, different for each face.

## Features

- Configurable cube size (N×N×N) via compilation flag
- Support for rotating 1 to N layers simultaneously
- Three rotation angles: 90°, 180° and 270°
- Visual representation of the unfolded cube state
- Command-based interface following a strict grammar specification

## Compilation

The cube size N is defined as a symbolic constant that can be set during compilation:
```bash
gcc @options -DN=3 rubik.c -o rubik  # For a 3×3×3 cube
gcc @options -DN=5 rubik.c -o rubik  # For a 5×5×5 cube
```

Default value of N is 5 if not specified.

## Input Format

The program accepts a sequence of rotation commands and print commands, terminated by a period (`.`).

### Command Syntax

**Rotation command:** `[layers]<face>[angle]`

- `[layers]` (optional): Number of layers to rotate (1-N). If omitted, defaults to 1.
- `<face>` (required): One of:
  - `u` - upper face
  - `l` - left face
  - `f` - front face
  - `r` - right face
  - `b` - back face
  - `d` - down face
- `[angle]` (optional):
  - Empty - 90° clockwise
  - `'` (apostrophe) - 90° counter-clockwise
  - `"` (double quote) - 180°

**Print command:** `\n` (newline) - prints the current cube state

**End of input:** `.` - terminates the program

### Example Input
```
u
2r'
f"

3l
.
```

This sequence:
1. Rotates the top layer 90° clockwise
2. Rotates outer 2 right layers 90° counter-clockwise
3. Rotates the front layer 180°
4. Prints the cube state (empty line)
5. Rotates outer 3 left layers 90° clockwise
6. Ends input

## Output Format

The cube state is printed as an unfolded net in the following layout:
```
  u
l|f|r|b
  d
```

For a solved cube with N=5:
```

      00000
      00000
      00000
      00000
      00000
11111|22222|33333|44444
11111|22222|33333|44444
11111|22222|33333|44444
11111|22222|33333|44444
11111|22222|33333|44444
      55555
      55555
      55555
      55555
      55555
```


## Implementation Details

- The program uses dynamic memory allocation for the cube structure
- All memory is properly deallocated before program termination
- Input after the terminating period (`.`) is ignored

## Project Information

- **Author:** Maja Kołodziejczyk
- **Date:** December 2024
- **Institution:** University of Warsaw - Faculty of Mathematics, Informatics and Mechanics
- **Course:** Introduction to Programming (IPP)

## Assumptions

- Input data is assumed to be correct and follows the specified grammar
- Every line of input, including the last one, ends with a newline character (`\n`)
- The value of N during testing is not excessively large, so the cube representation fits in available memory