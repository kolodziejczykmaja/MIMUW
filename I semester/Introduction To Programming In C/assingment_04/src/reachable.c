/*
 * "Reachable"
 *
 * The program reads a directed graph G description in a subset of DOT language.
 * The program receives a node name w as an argument.
 * The program prints names of nodes reachable in graph G from node w.
 * It orders them in non-decreasing order by distance from node w.
 * Node names with the same distance from w are ordered lexicographically.
 * The program output, called with argument w, is a sequence of non-empty lines
 * numbered from 1. The i-th line contains names of nodes reachable from node w
 * whose distance from that node equals i - 1. In each line, node names are
 * ordered lexicographically in ascending order. Between each pair of adjacent
 * names in a line there is one space.
 *
 * author: Maja Kołodziejczyk
 * date: January 2025
 */

#include <assert.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Structure representing a neighbor list
typedef struct Node {
  int index;
  struct Node *next;
} Node;

// Graph structure
typedef struct Graph {
  char **names;
  Node **adjacencyList;
  int nodeCount;
} Graph;

// Function to initialize the graph
void initGraph(Graph *graph) {
  graph->nodeCount = 0;
  graph->names = NULL;
  graph->adjacencyList = NULL;
}

// Function to allocate memory for a new string
char *str_dup(const char *src) {
  char *copy = malloc(strlen(src) + 1);
  if (copy) {
    strcpy(copy, src);
  }
  return copy;
}

// Function to add a new node to the graph
int addNewNode(Graph *graph, const char *name) {
  int index = graph->nodeCount;
  graph->names =
      realloc(graph->names, (size_t)(graph->nodeCount + 1) * sizeof(char *));
  assert(graph->names != NULL);
  graph->adjacencyList = realloc(
      graph->adjacencyList, (size_t)(graph->nodeCount + 1) * sizeof(Node *));
  assert(graph->adjacencyList != NULL);
  graph->names[graph->nodeCount] = str_dup(name);
  graph->adjacencyList[graph->nodeCount] = NULL;
  ++graph->nodeCount;
  return index;
}

// Function to add an edge to the graph
void addEdge(Graph *graph, const char *src, const char *dest) {
  int srcIndex = -1, destIndex = -1;
  for (int i = 0; i < graph->nodeCount; ++i) {
    if (strcmp(graph->names[i], src) == 0) {
      srcIndex = i;
    }
  }
  if (srcIndex == -1) {
    srcIndex = addNewNode(graph, src);
  }
  for (int i = 0; i < graph->nodeCount; ++i) {

    if (strcmp(graph->names[i], dest) == 0) {
      destIndex = i;
    }
  }
  if (destIndex == -1) {
    destIndex = addNewNode(graph, dest);
  }
  Node *newNode = malloc((size_t)sizeof(Node));
  newNode->index = destIndex;
  newNode->next = graph->adjacencyList[srcIndex];
  graph->adjacencyList[srcIndex] = newNode;
}

// Function to read input data
void getInput(Graph *graph) {
  char character = (char)getchar();
  while (character != '{') {
    character = (char)getchar();
  }
  character = (char)getchar();
  while (character != '}') {
    int src_count = 0;
    int dest_count = 0;
    char *dest = NULL;
    char *src = NULL;
    while (character != '>') {
      if (!isspace((int)(character)) && character != '-') {
        src = realloc(src, (size_t)(++src_count + 1) * sizeof(char));
        assert(src != NULL);
        src[src_count - 1] = character;
      }
      character = (char)getchar();
    }
    src[src_count] = '\0';
    character = (char)getchar();
    while (isspace((int)(character))) {
      character = (char)getchar();
    }
    while (!(isspace((int)(character))) && character != '}') {
      dest = realloc(dest, (size_t)(++dest_count + 1) * sizeof(char));
      assert(dest != NULL);
      dest[dest_count - 1] = character;
      character = (char)getchar();
    }
    dest[dest_count] = '\0';
    addEdge(graph, src, dest);
    while (isspace((int)(character))) {
      character = (char)getchar();
    }
    free(src);
    free(dest);
  }
}

// Function for sorting and printing
void sortingAndPrinting(int levelSize, char **levelNodes) {
  for (int i = 0; i < levelSize - 1; ++i) {
    for (int j = i + 1; j < levelSize; ++j) {
      if (strcmp(levelNodes[i], levelNodes[j]) > 0) {
        char *temp = levelNodes[i];
        levelNodes[i] = levelNodes[j];
        levelNodes[j] = temp;
      }
    }
  }
  for (int i = 0; i < levelSize; ++i) {
    printf("%s", levelNodes[i]);
    if (i != levelSize - 1) {
      printf(" ");
    }
  }
  printf("\n");
  free(levelNodes);
}

// Function for breadth-first search through graph nodes
void bfs(Graph *graph, const char *startNode) {
  int *visited = malloc((size_t)graph->nodeCount * sizeof(int));
  assert(visited != NULL);
  for (int i = 0; i < graph->nodeCount; ++i) {
    visited[i] = 0;
  }
  int startIndex = -1;
  int i = 0;
  while (startIndex == -1) {
    if (strcmp(graph->names[i], startNode) == 0) {
      startIndex = i;
    }
    ++i;
  }
  int *queue = malloc((size_t)graph->nodeCount * sizeof(int));
  assert(queue != NULL);
  int front = 0, rear = 0;
  queue[rear++] = startIndex;
  visited[startIndex] = 1;
  while (front < rear) {
    int levelSize = rear - front;
    char **levelNodes = malloc((size_t)levelSize * sizeof(char *));
    assert(levelNodes != NULL);
    int levelIndex = 0;
    for (i = 0; i < levelSize; ++i) {
      int current = queue[front++];
      levelNodes[levelIndex] = graph->names[current];
      ++levelIndex;
      Node *neighbor = graph->adjacencyList[current];
      while (neighbor != NULL) {
        if (!visited[neighbor->index]) {
          visited[neighbor->index] = 1;
          queue[rear++] = neighbor->index;
        }
        neighbor = neighbor->next;
      }
    }
    sortingAndPrinting(levelSize, levelNodes);
  }
  free(visited);
  free(queue);
}

// Function to free memory
void freeGraph(Graph *graph) {
  for (int i = 0; i < graph->nodeCount; ++i) {
    free(graph->names[i]);
    Node *current = graph->adjacencyList[i];
    while (current != NULL) {
      Node *temp = current;
      current = current->next;
      free(temp);
    }
  }
  free(graph->names);
  free(graph->adjacencyList);
}

int main(int argc, char *argv[]) {
  argc = argc;
  Graph graph;
  initGraph(&graph);
  getInput(&graph);
  bfs(&graph, argv[1]);
  freeGraph(&graph);
  return 0;
}