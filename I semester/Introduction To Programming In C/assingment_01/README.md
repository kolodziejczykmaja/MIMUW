# SET Card Game Solver

A C implementation of an automated SET card game simulator that finds and executes valid sets following the game's rules.

## About SET

SET is a real-time card game where players search for valid combinations of three cards. Each card has four attributes with three possible values:

- **Number**: 1, 2, or 3 shapes
- **Color**: Red, Green, or Purple
- **Shading**: Empty, Striped, or Solid
- **Shape**: Diamond, Wave, or Oval

A valid **SET** consists of three cards where each attribute is either:
- All the same across the three cards, OR
- All different across the three cards

### Example of a Valid SET
- 2 Red Solid Diamonds
- 3 Purple Solid Waves  
- 1 Green Solid Oval

(Number: all different; Color: all different; Shading: all same; Shape: all different)

## Project Description

This program simulates the endgame of SET, automatically finding and removing sets from the table until no more sets remain or the deck is exhausted.

### Game Rules Implemented

1. The game starts with 12 cards on the table (or all remaining cards if fewer than 12)
2. The program always selects the "first" set available (lexicographically earliest by position)
3. When a set is removed and fewer than 12 cards remain on the table, three new cards are dealt from the deck
4. If no set exists on the table, three additional cards are dealt
5. The game ends when no sets remain and the deck is empty

## Input Format

Input consists of integers in the range 1111-3333, with no repetitions. Each number represents a card using four digits:

```
ABCD where:
A = Number (1, 2, or 3)
B = Color (1=Red, 2=Green, 3=Purple)
C = Shading (1=Empty, 2=Striped, 3=Solid)
D = Shape (1=Diamond, 2=Wave, 3=Oval)
```

Example: `2131` = 2 Red Solid Diamonds

## Output Format

The program outputs:
- Initial table state (line starting with `=`)
- Each move: either a set removal (line starting with `-`) or dealing three cards (`+`)
- Table state after each move

### Example Output
```
= 1111 1222 1333 2111 2222 2333 3111 3222 3333 1123 2231 3312
- 1111 2222 3333
= 2111 2333 3111 3222 1123 2231 3312 1231 2312 3123
- 2111 2333 3222
= 3111 1123 2231 3312 1231 2312 3123
+
= 3111 1123 2231 3312 1231 2312 3123 1213 2321 3132
...
```

## Building and Running

### Prerequisites
- GCC compiler (C17 standard)
- Valgrind (for memory leak detection)
- Make

### Compilation

```bash
make build
```

This compiles the program with strict compiler flags including:
- C17 standard compliance
- All warnings enabled and treated as errors
- Stack protection and undefined behavior sanitizer
- Memory safety checks

### Running the Program

```bash
./set_solver < input_file.in
```

Or with Valgrind:

```bash
valgrind --leak-check=full -q --error-exitcode=1 ./set_solver < input_file.in
```

## Testing

The project includes 113 test cases. Run all tests with:

```bash
make test
```

This will:
1. Run each test through Valgrind to check for memory leaks
2. Compare output with expected results using `diff`
3. Report pass/fail status for each test
4. Summarize total results

### Individual Test

To run a specific test manually:

```bash
./set_solver < test/test1.in | diff - test/test1.out
```

## Compiler Flags

The project uses strict compilation flags to ensure code quality:

```
-std=c17 -pedantic -Wall -Wextra -Wformat-security
-Wduplicated-cond -Wfloat-equal -Wshadow -Wconversion
-Wjump-misses-init -Wlogical-not-parentheses
-Wnull-dereference -Wvla -Werror -fstack-protector-strong
-fsanitize=undefined -fno-sanitize-recover -g
-fno-omit-frame-pointer -O1
```
## Cleaning

Remove compiled binaries:

```bash
make clean
```

## Implementation Notes

- The program reads all input from stdin
- Output is written to stdout
- All memory allocations are properly freed (verified by Valgrind)
- No variable-length arrays (VLAs) are used
- Exit code is 0 on success, non-zero on error

## License

This is an academic project. Please check with your institution's policies regarding code sharing and collaboration.

## Author

Created as a programming assignment for algorithmic problem-solving coursework.
