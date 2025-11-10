/*
 * "Rubik's Cube"
 *
 * Program simulates a Rubik's cube.
 *
 * The program executes a sequence of commands rotating layers of a cube of size
 * N x N x N, where N is a positive value in the range of int type. The program
 * starts with a solved cube. On request, it prints the current state of the
 * cube. The command to print the cube state is a newline. The program ignores
 * input content after the period that ends the data.
 *
 * author: Maja Kołodziejczyk
 * date: December 2024
 */

#include <stdio.h>
#include <stdlib.h>

#ifndef N
#define N 5
#endif
#define NUMBER_OF_FACES 6
#define FACE_U 0
#define FACE_L 1
#define FACE_F 2
#define FACE_R 3
#define FACE_B 4
#define FACE_D 5
#define ANGLE_90 1
#define ANGLE_MINUS_90 3
#define ANGLE_180 2
#define ANGLE_360 4

void AllocateCube(int ***Cube) {
  for (int i = 0; i < NUMBER_OF_FACES; ++i) {
    Cube[i] = (int **)malloc(N * sizeof(int *));
    for (int j = 0; j < N; ++j) {
      Cube[i][j] = (int *)malloc(N * sizeof(int));
    }
  }
}

void DeallocateCube(int ***Cube) {
  for (int i = 0; i < NUMBER_OF_FACES; ++i) {
    for (int j = 0; j < N; ++j) {
      free(Cube[i][j]);
    }
    free(Cube[i]);
  }
  free(Cube);
}

/*
 * Fill the cube with numbers 0 - 5 appropriately,
 * so that it is in the initial state (i.e., solved).
 */
void InitializeCube(int ***Cube) {
  for (int i = 0; i < NUMBER_OF_FACES; ++i) {
    for (int j = 0; j < N; ++j) {
      for (int k = 0; k < N; ++k) {
        Cube[i][j][k] = i;
      }
    }
  }
}

// Print the current state of the cube.
void PrintCube(int ***Cube) {
  printf("\n");
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N + 1; ++j) {
      printf(" ");
    }
    for (int j = 0; j < N; ++j) {
      printf("%d", Cube[FACE_U][i][j]);
    }
    printf("\n");
  }
  for (int j = 0; j < N; ++j) {
    for (int i = FACE_L; i <= FACE_B; ++i) {
      for (int k = 0; k < N; ++k) {
        printf("%d", Cube[i][j][k]);
      }
      if (i != FACE_B) {
        printf("|");
      } else {
        printf("\n");
      }
    }
  }
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N + 1; ++j) {
      printf(" ");
    }
    for (int j = 0; j < N; ++j) {
      printf("%d", Cube[FACE_D][i][j]);
    }
    printf("\n");
  }
}

// Rotate the given face by 90 degrees.
void RotateFace(int ***Cube, int face) {
  int tmp[N][N];
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      tmp[i][j] = Cube[face][i][j];
    }
  }
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      Cube[face][j][N - i - 1] = tmp[i][j];
    }
  }
}

/*
 * FaceX functions perform rotation of a given number of layers by a specified
 * angle, looking from the side of face X. For faces 0 and 5 we work on rows
 * of adjacent faces, for faces 1 and 3 for two faces we move along rows,
 * and for the other two along columns, while for faces 2 and 4 each of their
 * adjacent faces is positioned in a different direction, so we move
 * accordingly from the beginning or end along rows or columns, depending on
 * the face.
 */
void FaceZero(int ***Cube, int layer, int angle) {
  for (int i = 0; i < layer; ++i) {
    for (int k = 0; k < angle; ++k) {
      int *tmp = Cube[FACE_B][i];
      Cube[FACE_B][i] = Cube[FACE_L][i];
      Cube[FACE_L][i] = Cube[FACE_F][i];
      Cube[FACE_F][i] = Cube[FACE_R][i];
      Cube[FACE_R][i] = tmp;
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_U);
  }
  /*
   * The opposite face to face zero (in this case the fifth)
   * must be rotated by the given angle, but in the opposite direction.
   * Similarly for subsequent faces.
   */
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_D);
    }
  }
}

void FaceOne(int ***Cube, int layer, int angle) {
  for (int i = 0, j = N - 1; i < layer && j >= N - layer; ++i, --j) {
    for (int k = 0; k < angle; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Cube[FACE_D][a][i];
        Cube[FACE_D][a][i] = Cube[FACE_F][a][i];
        Cube[FACE_F][a][i] = Cube[FACE_U][a][i];
        Cube[FACE_U][a][i] = Cube[FACE_B][b][j];
        Cube[FACE_B][b][j] = tmp;
      }
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_L);
  }
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_R);
    }
  }
}

void FaceTwo(int ***Cube, int layer, int angle) {
  for (int i = 0, j = N - 1; i < layer && j >= N - layer; ++i, --j) {
    for (int k = 0; k < angle; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Cube[FACE_U][j][b];
        Cube[FACE_U][j][b] = Cube[FACE_L][a][j];
        Cube[FACE_L][a][j] = Cube[FACE_D][i][a];
        Cube[FACE_D][i][a] = Cube[FACE_R][b][i];
        Cube[FACE_R][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_F);
  }
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_B);
    }
  }
}

void FaceThree(int ***Cube, int layer, int angle) {
  for (int i = 0, j = N - 1; i < layer && j >= N - layer; ++i, --j) {
    for (int k = 0; k < angle; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Cube[FACE_U][a][j];
        Cube[FACE_U][a][j] = Cube[FACE_F][a][j];
        Cube[FACE_F][a][j] = Cube[FACE_D][a][j];
        Cube[FACE_D][a][j] = Cube[FACE_B][b][i];
        Cube[FACE_B][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_R);
  }
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_L);
    }
  }
}

void FaceFour(int ***Cube, int layer, int angle) {
  for (int i = 0, j = N - 1; i < layer && j >= N - layer; ++i, --j) {
    for (int k = 0; k < angle; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Cube[FACE_U][i][a];
        Cube[FACE_U][i][a] = Cube[FACE_R][a][j];
        Cube[FACE_R][a][j] = Cube[FACE_D][j][b];
        Cube[FACE_D][j][b] = Cube[FACE_L][b][i];
        Cube[FACE_L][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_B);
  }
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_F);
    }
  }
}

void FaceFive(int ***Cube, int layer, int angle) {
  for (int j = N - 1; j >= N - layer; --j) {
    for (int k = 0; k < angle; ++k) {
      int *tmp = Cube[FACE_B][j];
      Cube[FACE_B][j] = Cube[FACE_R][j];
      Cube[FACE_R][j] = Cube[FACE_F][j];
      Cube[FACE_F][j] = Cube[FACE_L][j];
      Cube[FACE_L][j] = tmp;
    }
  }
  for (int k = 0; k < angle; ++k) {
    RotateFace(Cube, FACE_D);
  }
  for (int k = 0; k < ANGLE_360 - angle; ++k) {
    if (layer == N) {
      RotateFace(Cube, FACE_U);
    }
  }
}

// Depending on the face, call the appropriate function.
void RotateCubeFaces(int ***Cube, int layer, int face, int angle) {
  if (face == FACE_U)
    FaceZero(Cube, layer, angle);
  if (face == FACE_L)
    FaceOne(Cube, layer, angle);
  if (face == FACE_F)
    FaceTwo(Cube, layer, angle);
  if (face == FACE_R)
    FaceThree(Cube, layer, angle);
  if (face == FACE_B)
    FaceFour(Cube, layer, angle);
  if (face == FACE_D)
    FaceFive(Cube, layer, angle);
}

void ReadInput(int ***Cube) {
  int layer;
  int angle;
  int face = 0;
  char character;
  character = (char)getchar();
  while (character != '.') {
    layer = 0;
    if (character == '\n') {
      PrintCube(Cube);
      character = (char)getchar();
    } else {
      while (character >= '0' && character <= '9') {
        layer = layer * 10 + character - '0';
        character = (char)getchar();
      }
      if (layer == 0) {
        layer = 1;
      }
      if (character >= 'a' && character <= 'z') {
        if (character == 'u')
          face = FACE_U;
        if (character == 'l')
          face = FACE_L;
        if (character == 'f')
          face = FACE_F;
        if (character == 'r')
          face = FACE_R;
        if (character == 'b')
          face = FACE_B;
        if (character == 'd')
          face = FACE_D;
        character = (char)getchar();
      }
      if (character == '"' || character == '\'') {
        if (character == '"') {
          angle = ANGLE_180;
        } else {
          angle = ANGLE_MINUS_90;
        }
        character = (char)getchar();
      } else {
        angle = ANGLE_90;
      }
      RotateCubeFaces(Cube, layer, face, angle);
    }
  }
}

int main(void) {
  int ***Cube = (int ***)malloc(NUMBER_OF_FACES * sizeof(int **));
  AllocateCube(Cube);
  InitializeCube(Cube);
  ReadInput(Cube);
  DeallocateCube(Cube);
  return 0;
}