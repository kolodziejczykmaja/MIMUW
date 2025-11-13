/*
 * "Osiągalne"
 *
 * Program czyta opis grafu skierowanego G, w podzbiorze języka DOT.
 * Program dostaje jako argument nazwę węzła w tego grafu.
 * Program pisze nazwy węzłów osiągalnych w grafie G z węzła w.
 * Porządkuje je niemalejąco po odległości węzłów od węzła w.
 * Nazwy węzłów, których odległości od w są takie same, porządkuje
 * leksykograficznie. Wynikiem programu, wywołanego z argumentem w,
 * jest ciąg niepustych wierszy, numerowanych od 1. W i-tym wierszu
 * są nazwy węzłów, osiągalnych z węzła w, których odległość od tego
 * węzła jest równa i - 1. W każdym wierszu nazwy węzłów są
 * uporządkowane leksykograficznie rosnąco. Między każdą parą nazw
 * sąsiadujących w wierszu jest jedna spacja.
 *
 * autor: Maja Kołodziejczyk
 * data: styczeń 2025
 */

#include <assert.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Struktura reprezentująca listę sąsiadów
typedef struct Node {
	int index;
	struct Node *next;
} Node;

// Struktura grafu
typedef struct Graph {
	char **names;
	Node **adjacencyList;
	int nodeCount;
} Graph;

// Funkcja do inicjalizacji grafu
void initGraph(Graph *graph) {
	graph->nodeCount = 0;
	graph->names = NULL;
	graph->adjacencyList = NULL;
}

// Funkcja do alokacji pamięci dla nowego łańcucha znaków
char *Strdup(const char *src) {
	char *copy = malloc(strlen(src) + 1);
	if (copy) {
		strcpy(copy, src);
	}
	return copy;
}

// Funkcja do dodawania nowego wierzchołka do grafu
int addNewNode(Graph *graph, const char *name) {
	int index = graph->nodeCount;
	graph->names =
		realloc(graph->names, (size_t)(graph->nodeCount + 1) * sizeof(char *));
	assert(graph->names != NULL);
	graph->adjacencyList = realloc(
		graph->adjacencyList, (size_t)(graph->nodeCount + 1) * sizeof(Node *));
	assert(graph->adjacencyList != NULL);
	graph->names[graph->nodeCount] = Strdup(name);
	graph->adjacencyList[graph->nodeCount] = NULL;
	graph->nodeCount++;
	return index;
}

// Funkcja do dodawania krawędzi do grafu
void addEdge(Graph *graph, const char *src, const char *dest) {
	int srcIndex = -1, destIndex = -1;
	for (int i = 0; i < graph->nodeCount; ++i) {
		if (strcmp(graph->names[i], src) == 0) {
			srcIndex = i;
		}
		if (strcmp(graph->names[i], dest) == 0) {
			destIndex = i;
		}
	}
	if (srcIndex == -1) {
		srcIndex = addNewNode(graph, src);
	}
	if (destIndex == -1) {
		destIndex = addNewNode(graph, dest);
	}
	Node *newNode = malloc((size_t)sizeof(Node));
	newNode->index = destIndex;
	newNode->next = graph->adjacencyList[srcIndex];
	graph->adjacencyList[srcIndex] = newNode;
}

// Funkcja do wcztania danych
void GetInput(Graph *graph) {
	char znak = (char)getchar();
	while (znak != '{') {
		znak = (char)getchar();
	}
	znak = (char)getchar();
	while (znak != '}') {
		int src_count = 0;
		int dest_count = 0;
		char *dest = NULL;
		char *src = NULL;
		while (znak != '>') {
			if (!isspace((int)(znak)) && znak != '-') {
				src = realloc(src, (size_t)(++src_count + 1) * sizeof(char));
				assert(src != NULL);
				src[src_count - 1] = znak;
			}
			znak = (char)getchar();
		}
		src[src_count] = '\0';
		znak = (char)getchar();
		while (isspace((int)(znak))) {
			znak = (char)getchar();
		}
		while (!(isspace((int)(znak))) && znak != '}') {
			dest = realloc(dest, (size_t)(++dest_count + 1) * sizeof(char));
			assert(dest != NULL);
			dest[dest_count - 1] = znak;
			znak = (char)getchar();
		}
		dest[dest_count] = '\0';
		addEdge(graph, src, dest);
		while (isspace((int)(znak))) {
			znak = (char)getchar();
		}
		free(src);
		free(dest);
	}
}

// Funkcja do sortowania i wypisywania
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

// Funkcja do przeszukiwania wierzchołków grafu
void bfs(Graph *graph, const char *startNode) {
	int *visited = malloc((size_t)graph->nodeCount * sizeof(int));
	assert(visited != NULL);
	for (int i = 0; i < graph->nodeCount; ++i) {
		visited[i] = 0;
	}
	int startIndex = -1;
	for (int i = 0; i < graph->nodeCount; ++i) {
		if (strcmp(graph->names[i], startNode) == 0) {
			startIndex = i;
			break;
		}
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
		for (int i = 0; i < levelSize; i++) {
			int current = queue[front++];
			levelNodes[levelIndex] = graph->names[current];
			levelIndex++;
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

// Funkcja do zwalniania pamięci
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
	GetInput(&graph);
	bfs(&graph, argv[1]);
	freeGraph(&graph);
	return 0;
}