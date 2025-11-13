/*
 * "Sokoban"
 *
 * Program symuluje grę w sokobana.
 *
 * Na wejściu programu jest opis stanu początkowego planszy
 * i niepusty ciąg rozkazów, zakończony kropką.
 * Pierwszym rozkazem jest rozkaz wydruku stanu planszy.
 * Program ignoruje zawartość wejścia po kropce kończącej dane.
 * Program czyta opis stanu początkowego planszy, a następnie
 * czyta i wykonuje kolejne rozkazy.
 * Rozpoznawane są rozkazy wydruku aktualnego stanu planszy,
 * pchnięcia skrzyni i cofnięcia wykonanego wcześniej pchnięcia.
 * Inaczej, niż w typowych implementacjach Sokobana, użytkownik
 * nie musi podawać ruchów przesuwających postać po wolnych polach.
 * Program sam ustala, jak doprowadzić postać na pole, z którego
 * będzie możliwe pchnięcie skrzyni we wskazanym kierunku.
 * Wynik programu jest efektem wykonania rozkazów drukowania
 * aktualnego stanu planszy.
 *
 * autor: Maja Kołodziejczyk
 * data: styczeń 2025
 */

#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define UP 8
#define LEFT 4
#define RIGHT 6
#define DOWN 2
#define POSSIBLE_MOVES 4
#define EMPTY '-'
#define GOAL '+'
#define PLAYER '@'
#define PLAYER_ON_GOAL '*'

// Funkcja do drukowania planszy.
void PrintBoard(char **Board, int row) {
	int i = 0;
	while (i < row) {
		printf("%s\n", Board[i]);
		++i;
	}
}

// Funckcja do dealokacji planszy.
void FreeBoard(char **Board, int row) {
	int i = 0;
	while (i < row) {
		free(Board[i]);
		++i;
	}
	free(Board);
}

// Funkcja do tworzenia kopii łańcucha znakó*.
char *ft_strdup(const char *string) {
	size_t len = strlen(string) + 1;
	char *copy = malloc(len);
	assert(copy != NULL);
	memcpy(copy, string, len);
	return copy;
}

// Funkcja do tworzenia kopii planszy.
char **CopyBoard(char **original, int row) {
	char **copy = malloc((size_t)row * sizeof(char *));
	assert(copy != NULL);
	for (int i = 0; i < row; ++i) {
		copy[i] = ft_strdup(original[i]);
		assert(copy[i] != NULL);
	}
	return copy;
}

// Struktura przechowująca współrzędne.
typedef struct Coordinates {
	int x;
	int y;
} Coordinates;

// Struktura przechowująca węzeł stosu.
typedef struct StackNode {
	char **board;
	struct StackNode *next;
} StackNode;

// Struktura przechowująca stos.
typedef struct Stack {
	StackNode *top;
} Stack;

// Inicjalizacja stosu.
void InitStack(Stack *stack) {
	stack->top = NULL;
}

// Sprawdzenie, czy stos jest pusty.
bool IsStackEmpty(Stack *stack) {
	return (stack->top == NULL);
}

// Dodanie planszy na stos.
void Push(Stack *stack, char **Board, int row) {
	StackNode *newNode = malloc(sizeof(StackNode));
	assert(newNode != NULL);
	newNode->board = CopyBoard(Board, row);
	newNode->next = stack->top;
	stack->top = newNode;
}

// Usunięcie planszy ze stosu.
char **Pop(Stack *stack) {
	assert(!IsStackEmpty(stack));
	StackNode *temp = stack->top;
	char **board = temp->board;
	stack->top = temp->next;
	free(temp);
	return board;
}

// Zwolnienie pamięci plansz na stosie.
void FreeStack(Stack *stack, int row) {
	while (!IsStackEmpty(stack)) {
		char **board = Pop(stack);
		FreeBoard(board, row);
	}
}

// Struktura przechowująca węzeł kolejki.
typedef struct QueueNode {
	Coordinates coord;
	struct QueueNode *next;
} QueueNode;

// Struktura przechowująca kolejkę.
typedef struct Queue {
	QueueNode *front;
	QueueNode *back;
} Queue;

// Inicjalizacja kolejki.
void InitQueue(Queue *q) {
	q->front = NULL;
	q->back = NULL;
}

// Sprawdzenie, czy kolejka jest pusta.
bool IsQueueEmpty(Queue *q) {
	return q->front == NULL;
}

// Dodanie elementu na kolejkę.
void Enqueue(Queue *q, Coordinates coord) {
	QueueNode *newNode = malloc(sizeof(QueueNode));
	assert(newNode != NULL);
	newNode->coord = coord;
	newNode->next = NULL;
	if (q->back != NULL) {
		q->back->next = newNode;
	} else {
		q->front = newNode;
	}
	q->back = newNode;
}

// Usunięcie elementu z kolejki.
Coordinates Dequeue(Queue *q) {
	assert(!IsQueueEmpty(q));
	QueueNode *temp = q->front;
	Coordinates coord = temp->coord;
	q->front = temp->next;
	if (q->front == NULL) {
		q->back = NULL;
	}
	free(temp);
	return coord;
}

// Zwolnienie pamięci elementów kolejki.
void FreeQueue(Queue *q) {
	while (!IsQueueEmpty(q)) {
		Dequeue(q);
	}
}

// Znalezienie współrzędnych gracza.
Coordinates FindPlayer(char **Board, int row) {
	Coordinates player;
	int flag = 0;
	for (int i = 0; i < row && flag == 0; ++i) {
		for (int j = 0; Board[i][j] != '\0' && flag == 0; ++j) {
			if (Board[i][j] == '@' || Board[i][j] == '*') {
				player.x = j;
				player.y = i;
				flag = 1;
			}
		}
	}
	return player;
}

// Znalezienie współrzędnych skrzyni.
Coordinates FindBox(char **Board, int row, char symbol) {
	Coordinates box;
	int flag = 0;
	for (int i = 0; i < row && flag == 0; ++i) {
		for (int j = 0; Board[i][j] != '\0' && flag == 0; ++j) {
			if (Board[i][j] == symbol || Board[i][j] == symbol - 'a' + 'A') {
				box.x = j;
				box.y = i;
				flag = 1;
			}
		}
	}
	return box;
}

// Sprawdzenie, czy pole jest puste.
bool IsCellEmpty(char cell) {
	return (cell == EMPTY || cell == GOAL);
}

// Sprawdzenie, czy ruch jest możliwy.
bool IsValidMove(char **Board, int row, bool **visited, int x, int y) {
	if (y >= 0 && y < row && x >= 0 && (size_t)x < strlen(Board[y])) {
		return (!visited[y][x] && IsCellEmpty(Board[y][x]));
	}
	return false;
}

// Zmiana współrzędnych skrzyni.
Coordinates ChangeBox(Coordinates box, int direction) {
	Coordinates newBox = box;
	if (direction == UP) {
		++newBox.y;
	}
	if (direction == DOWN) {
		--newBox.y;
	}
	if (direction == LEFT) {
		++newBox.x;
	}
	if (direction == RIGHT) {
		--newBox.x;
	}
	return newBox;
}

// Sprawdzanie czy można dojść przed skrzynię.
bool BFS(char **Board, Coordinates player, Coordinates box, int row,
		 int direction) {
	bool **visited = malloc((size_t)row * sizeof(bool *));
	assert(visited != NULL);
	for (int i = 0; i < row; ++i) {
		visited[i] = calloc(strlen(Board[i]), sizeof(bool));
		assert(visited[i] != NULL);
	}
	Queue q;
	InitQueue(&q);
	Enqueue(&q, player);
	visited[player.y][player.x] = true;
	int dx[] = {0, 0, -1, 1};
	int dy[] = {-1, 1, 0, 0};
	box = ChangeBox(box, direction);
	while (!IsQueueEmpty(&q)) {
		Coordinates current = Dequeue(&q);
		if (current.x == box.x && current.y == box.y) {
			for (int i = 0; i < row; ++i) {
				free(visited[i]);
			}
			free(visited);
			FreeQueue(&q);
			return true;
		}
		for (int i = 0; i < POSSIBLE_MOVES; ++i) {
			int newX = current.x + dx[i];
			int newY = current.y + dy[i];
			if (IsValidMove(Board, row, visited, newX, newY)) {
				visited[newY][newX] = true;
				Coordinates next = {newX, newY};
				Enqueue(&q, next);
			}
		}
	}
	FreeQueue(&q);
	for (int i = 0; i < row; ++i) {
		free(visited[i]);
	}
	free(visited);
	return false;
}

// Sprawdzenie, czy pole jest dostępne.
bool IsAccessible(char cell) {
	return (cell == EMPTY || cell == GOAL || cell == PLAYER ||
			cell == PLAYER_ON_GOAL);
}

// Sprawdzenie, czy można wykonać ruch.
bool Posibility(char **Board, Coordinates boxCoordinates, int row,
				int direction) {
	int x = boxCoordinates.x;
	int y = boxCoordinates.y;
	if (direction == UP || direction == DOWN) {
		if (y <= 0 || y >= row - 1) {
			return false;
		}
		if (strlen(Board[y - 1]) <= (size_t)x ||
			strlen(Board[y + 1]) <= (size_t)x) {
			return false;
		}
		if ((!IsAccessible(Board[y + 1][x])) ||
			(!IsAccessible(Board[y - 1][x]))) {
			return false;
		}
	}
	if (direction == LEFT || direction == RIGHT) {
		if ((x <= 0 || (size_t)x >= strlen(Board[y]))) {
			return false;
		}
		if ((!IsAccessible(Board[y][x + 1])) ||
			(!IsAccessible(Board[y][x - 1]))) {
			return false;
		}
	}
	return true;
}

// Zmiana pozycji gracza.
void ChangingPlayer(char **Board, Coordinates player, Coordinates box) {
	char playerSymbol = Board[player.y][player.x];
	char boxSymbol = Board[box.y][box.x];
	if (playerSymbol == PLAYER) {
		Board[player.y][player.x] = EMPTY;
	} else {
		Board[player.y][player.x] = GOAL;
	}
	if (boxSymbol >= 'a' && boxSymbol <= 'z') {
		Board[box.y][box.x] = PLAYER;
	} else {
		Board[box.y][box.x] = PLAYER_ON_GOAL;
	}
}

// Zmiana pozycji skrzynki.
void ChangingBox(char **Board, Coordinates box, int direction, char tmp) {
	if (direction == UP) {
		box.y -= 1;
	}
	if (direction == DOWN) {
		box.y += 1;
	}
	if (direction == LEFT) {
		box.x -= 1;
	}
	if (direction == RIGHT) {
		box.x += 1;
	}
	if (Board[box.y][box.x] == EMPTY) {
		Board[box.y][box.x] = tmp;
	} else {
		Board[box.y][box.x] = (char)(tmp - 'a' + 'A');
	}
}

// Zmiana pozycji gracza i skrzynki.
void ChangingPosition(char **Board, Coordinates player, Coordinates box,
					  int direction) {
	char tmp = Board[box.y][box.x];
	if (tmp >= 'A' && tmp <= 'Z') {
		tmp = (char)(tmp - 'A' + 'a');
	}
	ChangingPlayer(Board, player, box);
	ChangingBox(Board, box, direction, tmp);
}

// Wczytywanie i wykonywanie rozkazów.
void GetCommands(char ***Board, int row) {
	Coordinates player;
	Coordinates box;
	char symbol;
	Stack stack;
	InitStack(&stack);
	symbol = (char)getchar();
	while (symbol != '.') {
		if (symbol == '\n') {
			PrintBoard(*Board, row);
		} else if (symbol == '0') {
			if (!IsStackEmpty(&stack)) {
				char **PrevBoard = Pop(&stack);
				FreeBoard(*Board, row);
				*Board = PrevBoard;
			}
		} else if (symbol >= 'a' && symbol <= 'z') {
			char letter = symbol;
			symbol = (char)getchar();
			int direction = (int)(symbol - '0');
			player = FindPlayer(*Board, row);
			box = FindBox(*Board, row, letter);
			if (Posibility(*Board, box, row, direction) &&
				BFS(*Board, player, box, row, direction)) {
				Push(&stack, *Board, row);
				ChangingPosition(*Board, player, box, direction);
			}
		}
		symbol = (char)getchar();
	}
	FreeStack(&stack, row);
}

// Wczytywanie planszy.
void GetBoard(char ***Board, int *row) {
	int symbol;
	int flag = 0;
	*Board = NULL;
	*row = 0;
	symbol = getchar();
	while (flag == 0) {
		int col = 0;
		char **newBoard = realloc(*Board, (size_t)(*row + 1) * sizeof(char *));
		assert(newBoard != NULL);
		*Board = newBoard;
		(*Board)[*row] = NULL;
		while (symbol != '\n') {
			char *newRow =
				realloc((*Board)[*row], (size_t)(col + 1) * sizeof(char));
			assert(newRow != NULL);
			(*Board)[*row] = newRow;
			(*Board)[*row][col] = (char)symbol;
			++col;
			symbol = getchar();
		}
		char *newRow =
			realloc((*Board)[*row], (size_t)(col + 1) * sizeof(char));
		assert(newRow != NULL);
		(*Board)[*row] = newRow;
		(*Board)[*row][col] = '\0';
		++(*row);
		symbol = getchar();
		if (symbol == '\n') {
			flag = 1;
		}
	}
}

int main(void) {
	char **Board = NULL;
	int row = 0;
	GetBoard(&Board, &row);
	PrintBoard(Board, row);
	GetCommands(&Board, row);
	FreeBoard(Board, row);
	return 0;
}