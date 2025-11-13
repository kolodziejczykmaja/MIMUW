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

typedef struct Coordinates {
	int x;
	int y;
} Coordinates;

typedef struct StackNode {
	char **board;
	struct StackNode *next;
} StackNode;

typedef struct Stack {
	StackNode *top;
} Stack;

void InitStack(Stack *stack) {
	stack->top = NULL;
}

bool IsStackEmpty(Stack *stack) {
	return (stack->top == NULL);
}

char* ft_strdup(const char *s) {
	size_t len = strlen(s) + 1;
	char *copy = malloc(len);
	assert(copy != NULL);
	return (char *)memcpy(copy, s, len);
}

char **CopyBoard(char **original, int row) {
	char **copy = malloc((size_t)row * sizeof(char *));
	assert(copy != NULL);
	for (int i = 0; i < row; ++i) {
		copy[i] = ft_strdup(original[i]);
		assert(copy[i] != NULL);
	}
	return copy;
}

void Push(Stack *stack, char **Board, int row) {
	StackNode *newNode = malloc(sizeof(StackNode));
	assert(newNode != NULL);
	newNode->board = CopyBoard(Board, row);
	newNode->next = stack->top;
	stack->top = newNode;
}

char **Pop(Stack *stack) {
	assert(!IsStackEmpty(stack));
	StackNode *temp = stack->top;
	char **board = temp->board;
	stack->top = temp->next;
	free(temp);
	return board;
}

void FreeBoard(char **Board, int row) {
	int i = 0;
	while (i < row) {
		free(Board[i]);
		++i;
	}
	free(Board);
}

void FreeStack(Stack *stack) {
	while (!IsStackEmpty(stack)) {
		Pop(stack);
	}
}

Coordinates FindPlayer(char **Board, int row) {
	Coordinates player;
	int flag = 0;
	for (int i = 0; i < row && flag == 0; ++i) {
		for (int j = 0; Board[i][j] != '\0' && flag == 0; ++j) {
			if (Board[i][j] == PLAYER || Board[i][j] == PLAYER_ON_GOAL) {
				player.x = j;
				player.y = i;
				flag = 1;
			}
		}
	}
	return player;
}

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

void PrintBoard(char **Board, int row) {
	int i = 0;
	while (i < row) {
		printf("%s\n", Board[i]);
		++i;
	}
}

void GetBoard(char ***Board, int *row) {
	int symbol;
	int flag = 0;
	*Board = NULL;
	symbol = (char)getchar();
	while (flag == 0) {
		int col = 0;
		*Board = realloc(*Board, (size_t)(*row + 1) * sizeof(char *));
		assert(*Board != NULL);
		(*Board)[*row] = NULL;
		while (symbol != '\n') {
			(*Board)[*row] = realloc((*Board)[*row], (size_t)(col + 1) * sizeof(char));
			assert((*Board)[*row] != NULL);
			(*Board)[*row][col] = (char)symbol;
			++col;
			symbol = getchar();
		}
		(*Board)[*row] =
			realloc((*Board)[*row], (size_t)(col + 1) * sizeof(char));
		assert((*Board)[*row] != NULL);
		(*Board)[*row][col] = '\0';
		++(*row);
		symbol = (char)getchar();
		if (symbol == '\n') {
			flag = 1;
		}
	}
}

typedef struct QueueNode {
	Coordinates coord;
	struct QueueNode *next;
} QueueNode;

typedef struct Queue {
	QueueNode *front;
	QueueNode *back;
} Queue;

void InitQueue(Queue *q) {
	q->front = NULL;
	q->back = NULL;
}

bool IsQueueEmpty(Queue *q) {
	return q->front == NULL;
}

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

bool IsCellEmpty(char cell) {
	return (cell == EMPTY || cell == GOAL);
}

bool IsValidMove(char **Board, int row, bool **visited, int x, int y) {
	if (y >= 0 && y < row && x >= 0 && (size_t)x < strlen(Board[y])) {
		return (!visited[y][x] && IsCellEmpty(Board[y][x]));
	}
	return false;
}

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
	for (int i = 0; i < row; ++i) {
		free(visited[i]);
	}
	free(visited);
	return false;
}

bool IsAccessible(char cell) {
	return (cell == EMPTY || cell == GOAL || cell == PLAYER || cell == PLAYER_ON_GOAL);
}

bool Posibility(char **Board, Coordinates boxCoordinates, int row,
				int direction) {
	int x = boxCoordinates.x;
	int y = boxCoordinates.y;
	if (direction == UP || direction == DOWN) {
		if ((y == 0 || y == row - 1) || (!IsAccessible(Board[y + 1][x])) ||
			(!IsAccessible(Board[y - 1][x]))) {
			return false;
		}
	}
	if (direction == LEFT || direction == RIGHT) {
		if ((x == 0 || (size_t)x >= strlen(Board[y])) ||
			(!IsAccessible(Board[y][x + 1])) ||
			(!IsAccessible(Board[y][x - 1]))) {
			return false;
		}
	}
	return true;
}

void ChangingPlayer(char** Board, Coordinates player, Coordinates box) {
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

void ChangingBox(char** Board, Coordinates box, int direction, char tmp) {
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

void ChangingPosition(char **Board, Coordinates player, Coordinates box,
					  int direction) {
	char tmp = Board[box.y][box.x];
	if (tmp >= 'A' && tmp <= 'Z') {
		tmp = (char)(tmp - 'A' + 'a');
	}
	ChangingPlayer(Board, player, box);
	ChangingBox(Board, box, direction, tmp);
}

void GetCommands(char **Board, int row) {
	Coordinates player;
	Coordinates box;
	char symbol;
	Stack stack;
	InitStack(&stack);
	symbol = (char)getchar();
	while (symbol != '.') {
		if (symbol == '\n') {
			PrintBoard(Board, row);
		} else if (symbol == '0') {
			if (!IsStackEmpty(&stack)) {
				char **PrevBoard = Pop(&stack);
				Board = PrevBoard;
			}
		} else if (symbol >= 'a' && symbol <= 'z') {
			char letter = symbol;
			symbol = (char)getchar();
			int direction = (int)(symbol - '0');
			player = FindPlayer(Board, row);
			box = FindBox(Board, row, letter);
			if (Posibility(Board, box, row, direction) &&
				BFS(Board, player, box, row, direction)) {
				Push(&stack, Board, row);
				ChangingPosition(Board, player, box, direction);
			}
		}
		symbol = (char)getchar();
	}
	FreeStack(&stack);
}

int main(void) {
	char **Board = NULL;
	int row = 0;
	GetBoard(&Board, &row);
	PrintBoard(Board, row);
	GetCommands(Board, row);
	FreeBoard(Board, row);
	return 0;
}