# Reachable Nodes in Directed Graph

A C program that finds and lists all nodes reachable from a given starting node in a directed graph, ordered by distance.

## Description

This program reads a directed graph description in a subset of the DOT language and finds all nodes reachable from a specified starting node. The output lists reachable nodes grouped by their distance from the starting node, with lexicographic ordering within each distance level.

### Graph Concepts

A node `b` is **reachable** from node `a` if:
- `a` and `b` are the same node, OR
- There exists an edge from `a` to some node `c`, and `b` is reachable from `c`

The **distance** from node `a` to node `b` is:
- 0, if `a` and `b` are the same node
- 1 + M, where M is the minimum distance from nodes directly connected to `a` from which `b` is reachable

## Features

- Parses directed graphs in DOT language subset
- Implements breadth-first search (BFS) algorithm
- Groups nodes by distance from starting node
- Lexicographic ordering within distance groups
- Memory-efficient graph representation


## Input Format

The program accepts directed graph descriptions in a subset of DOT language:
```
digraph {
    node1 -> node2
    node2 -> node3
    node3 -> node4
    node1 -> node4
}
```

### Grammar
```
<graph> ::= DIGRAPH '{' ID '->' ID '}'
```

**Terminal symbols:**
- `DIGRAPH`: Case-insensitive keyword `[Dd][Ii][Gg][Rr][Aa][Pp][Hh]`
- `ID`: Node identifier (alphanumeric string starting with a letter)
- `->`: Arrow indicating directed edge
- Whitespace and newlines are allowed between tokens

### Node Names

Node identifiers (`ID`) must:
- Start with a letter
- Contain only letters and digits
- Not be reserved words: `digraph`, `edge`, `graph`, `node`, `strict`, `subgraph` (case-insensitive)


## Output Format

The output consists of non-empty lines, numbered from 1. Line `i` contains all node names reachable from the starting node at distance `i-1`.

- Nodes on each line are sorted lexicographically
- Node names are separated by single spaces
- Each line ends with a newline character

### Example Output

For a graph where nodes `a`, `b`, `c` are reachable from `start`:
```
start
a b
c
```

This indicates:
- Distance 0: `start` (the starting node itself)
- Distance 1: `a` and `b` (directly reachable from `start`)
- Distance 2: `c` (reachable through `a` or `b`)


## Project Information

- **Author:** Maja Kołodziejczyk
- **Date:** January 2025
- **Institution:** University of Warsaw - Faculty of Mathematics, Informatics and Mechanics
- **Course:** Introduction to Programming (IPP)

## Assumptions

- Input data is correct and follows the specified grammar
- Each input line ends with a newline character `\n`
- Program is called with exactly one argument (starting node name)
- Node name length, number of nodes, and number of edges are less than `INT_MAX`
- No memory overflow occurs during execution (malloc/realloc never return NULL)
- The starting node exists in the graph