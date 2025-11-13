/*
 * "Kostka Rubika"
 *
 * Program symuluje kostkę Rubika.
 *
 * Program wykonuje ciąg rozkazów obracających warstwy kostki rozmiaru N x N x
 * N, gdzie N jest dodatnią wartością z zakresu typu int. Program zaczyna od
 * kostki ułożonej. Na życzenie drukuje aktualny stan kostki. Rozkazem wydruku
 * stanu kostki jest koniec wiersza. Program ignoruje zawartość wejścia po
 * kropce kończącej dane.
 *
 * autor: Maja Kołodziejczyk
 * data: grudzień 2024
 */

#include <stdio.h>
#include <stdlib.h>

#ifndef N
#define N 5
#endif
#define LICZBA_ŚCIAN_W_KOSTCE 6
#define ŚCIANA_U 0
#define ŚCIANA_L 1
#define ŚCIANA_F 2
#define ŚCIANA_R 3
#define ŚCIANA_B 4
#define ŚCIANA_D 5
#define KĄT_90 1
#define KĄT_MINUS_90 3
#define KĄT_180 2
#define KĄT_360 4

void AlokacjaKostki(int ***Kostka) {
  for (int i = 0; i < LICZBA_ŚCIAN_W_KOSTCE; ++i) {
    Kostka[i] = (int **)malloc(N * sizeof(int *));
    for (int j = 0; j < N; ++j) {
      Kostka[i][j] = (int *)malloc(N * sizeof(int));
    }
  }
}

void DealokacjaKostki(int ***Kostka) {
  for (int i = 0; i < LICZBA_ŚCIAN_W_KOSTCE; ++i) {
    for (int j = 0; j < N; ++j) {
      free(Kostka[i][j]);
    }
    free(Kostka[i]);
  }
  free(Kostka);
}

/*
 * Wypełniamy kostkę odpowiednio liczbami 0 - 5,
 * w taki sposób, aby była ona w stanie początkowym (czyli ułożona).
 */
void UzupełnianieKostki(int ***Kostka) {
  for (int i = 0; i < LICZBA_ŚCIAN_W_KOSTCE; ++i) {
    for (int j = 0; j < N; ++j) {
      for (int k = 0; k < N; ++k) {
        Kostka[i][j][k] = i;
      }
    }
  }
}

// Wypisujemy aktualny stan kostki.
void WypisywanieKostki(int ***Kostka) {
  printf("\n");
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N + 1; ++j) {
      printf(" ");
    }
    for (int j = 0; j < N; ++j) {
      printf("%d", Kostka[ŚCIANA_U][i][j]);
    }
    printf("\n");
  }
  for (int j = 0; j < N; ++j) {
    for (int i = ŚCIANA_L; i <= ŚCIANA_B; ++i) {
      for (int k = 0; k < N; ++k) {
        printf("%d", Kostka[i][j][k]);
      }
      if (i != ŚCIANA_B) {
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
      printf("%d", Kostka[ŚCIANA_D][i][j]);
    }
    printf("\n");
  }
}

// Obracamy daną ścianę o 90 stopni.
void ObrtótŚciany(int ***Kostka, int ściana) {
  int tmp[N][N];
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      tmp[i][j] = Kostka[ściana][i][j];
    }
  }
  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      Kostka[ściana][j][N - i - 1] = tmp[i][j];
    }
  }
}

/*
 * Funkcje ŚcianaX wykonują rotację danej ilości warstw o zadany kąt,
 * patrząc od strony ściany X. Dla ścian 0 i 5 działamy na wierszach
 * sąsiednich ścian, dla ścian 1 i 3 dla dwóch ścian przesuwamy się po
 * wierszach, a dla pozostałych dwóch po kolumnach, natomiast dla ścian
 * 2 i 4 każda z ich sąsiednich ścian jest położona w innym kierunku,
 * a więc przesuwamy się odpowiednio od początku lub końca po wierszach lub
 * kolumnach, w zaleności od ściany.
 */
void ŚcianaZero(int ***Kostka, int warstwa, int kąt) {
  for (int i = 0; i < warstwa; ++i) {
    for (int k = 0; k < kąt; ++k) {
      int *tmp = Kostka[ŚCIANA_B][i];
      Kostka[ŚCIANA_B][i] = Kostka[ŚCIANA_L][i];
      Kostka[ŚCIANA_L][i] = Kostka[ŚCIANA_F][i];
      Kostka[ŚCIANA_F][i] = Kostka[ŚCIANA_R][i];
      Kostka[ŚCIANA_R][i] = tmp;
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_U);
  }
  /*
   * Naprzeciwległą ścianę do ściany zero (w tym przypadku piątą)
   * musimy obrócić o dany kąt, lecz w przeciwną stronę.
   * Analogicznie do kolejnych ścian.
   */
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_D);
    }
  }
}

void ŚcianaJeden(int ***Kostka, int warstwa, int kąt) {
  for (int i = 0, j = N - 1; i < warstwa && j >= N - warstwa; ++i, --j) {
    for (int k = 0; k < kąt; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Kostka[ŚCIANA_D][a][i];
        Kostka[ŚCIANA_D][a][i] = Kostka[ŚCIANA_F][a][i];
        Kostka[ŚCIANA_F][a][i] = Kostka[ŚCIANA_U][a][i];
        Kostka[ŚCIANA_U][a][i] = Kostka[ŚCIANA_B][b][j];
        Kostka[ŚCIANA_B][b][j] = tmp;
      }
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_L);
  }
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_R);
    }
  }
}

void ŚcianaDwa(int ***Kostka, int warstwa, int kąt) {
  for (int i = 0, j = N - 1; i < warstwa && j >= N - warstwa; ++i, --j) {
    for (int k = 0; k < kąt; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Kostka[ŚCIANA_U][j][b];
        Kostka[ŚCIANA_U][j][b] = Kostka[ŚCIANA_L][a][j];
        Kostka[ŚCIANA_L][a][j] = Kostka[ŚCIANA_D][i][a];
        Kostka[ŚCIANA_D][i][a] = Kostka[ŚCIANA_R][b][i];
        Kostka[ŚCIANA_R][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_F);
  }
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_B);
    }
  }
}

void ŚcianaTrzy(int ***Kostka, int warstwa, int kąt) {
  for (int i = 0, j = N - 1; i < warstwa && j >= N - warstwa; ++i, --j) {
    for (int k = 0; k < kąt; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Kostka[ŚCIANA_U][a][j];
        Kostka[ŚCIANA_U][a][j] = Kostka[ŚCIANA_F][a][j];
        Kostka[ŚCIANA_F][a][j] = Kostka[ŚCIANA_D][a][j];
        Kostka[ŚCIANA_D][a][j] = Kostka[ŚCIANA_B][b][i];
        Kostka[ŚCIANA_B][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_R);
  }
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_L);
    }
  }
}

void ŚcianaCztery(int ***Kostka, int warstwa, int kąt) {
  for (int i = 0, j = N - 1; i < warstwa && j >= N - warstwa; ++i, --j) {
    for (int k = 0; k < kąt; ++k) {
      for (int a = 0, b = N - 1; a < N && b >= 0; ++a, --b) {
        int tmp = Kostka[ŚCIANA_U][i][a];
        Kostka[ŚCIANA_U][i][a] = Kostka[ŚCIANA_R][a][j];
        Kostka[ŚCIANA_R][a][j] = Kostka[ŚCIANA_D][j][b];
        Kostka[ŚCIANA_D][j][b] = Kostka[ŚCIANA_L][b][i];
        Kostka[ŚCIANA_L][b][i] = tmp;
      }
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_B);
  }
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_F);
    }
  }
}

void ŚcianaPięć(int ***Kostka, int warstwa, int kąt) {
  for (int j = N - 1; j >= N - warstwa; --j) {
    for (int k = 0; k < kąt; ++k) {
      int *tmp = Kostka[ŚCIANA_B][j];
      Kostka[ŚCIANA_B][j] = Kostka[ŚCIANA_R][j];
      Kostka[ŚCIANA_R][j] = Kostka[ŚCIANA_F][j];
      Kostka[ŚCIANA_F][j] = Kostka[ŚCIANA_L][j];
      Kostka[ŚCIANA_L][j] = tmp;
    }
  }
  for (int k = 0; k < kąt; ++k) {
    ObrtótŚciany(Kostka, ŚCIANA_D);
  }
  for (int k = 0; k < KĄT_360 - kąt; ++k) {
    if (warstwa == N) {
      ObrtótŚciany(Kostka, ŚCIANA_U);
    }
  }
}

// W zależności od ściany, wywołujemy odpowiednią funkcję.
void ZamianaŚcianKostki(int ***Kostka, int warstwa, int ściana, int kąt) {
  if (ściana == ŚCIANA_U) ŚcianaZero(Kostka, warstwa, kąt);
  if (ściana == ŚCIANA_L) ŚcianaJeden(Kostka, warstwa, kąt);
  if (ściana == ŚCIANA_F) ŚcianaDwa(Kostka, warstwa, kąt);
  if (ściana == ŚCIANA_R) ŚcianaTrzy(Kostka, warstwa, kąt);
  if (ściana == ŚCIANA_B) ŚcianaCztery(Kostka, warstwa, kąt);
  if (ściana == ŚCIANA_D) ŚcianaPięć(Kostka, warstwa, kąt);
}

void WczytywanieDanych(int ***Kostka) {
  int warstwa;
  int kąt;
  int ściana = 0;
  char znak;
  znak = (char)getchar();
  while (znak != '.') {
    warstwa = 0;
    if (znak == '\n') {
      WypisywanieKostki(Kostka);
      znak = (char)getchar();
    } else {
      while (znak >= '0' && znak <= '9') {
        warstwa = warstwa * 10 + znak - '0';
        znak = (char)getchar();
      }
      if (warstwa == 0) {
        warstwa = 1;
      }
      if (znak >= 'a' && znak <= 'z') {
        if (znak == 'u') ściana = ŚCIANA_U;
        if (znak == 'l') ściana = ŚCIANA_L;
        if (znak == 'f') ściana = ŚCIANA_F;
        if (znak == 'r') ściana = ŚCIANA_R;
        if (znak == 'b') ściana = ŚCIANA_B;
        if (znak == 'd') ściana = ŚCIANA_D;
        znak = (char)getchar();
      }
      if (znak == '"' || znak == '\'') {
        if (znak == '"') {
          kąt = KĄT_180;
        } else {
          kąt = KĄT_MINUS_90;
        }
        znak = (char)getchar();
      } else {
        kąt = KĄT_90;
      }
      ZamianaŚcianKostki(Kostka, warstwa, ściana, kąt);
    }
  }
}

int main(void) {
  int ***Kostka = (int ***)malloc(LICZBA_ŚCIAN_W_KOSTCE * sizeof(int **));
  AlokacjaKostki(Kostka);
  UzupełnianieKostki(Kostka);
  WczytywanieDanych(Kostka);
  DealokacjaKostki(Kostka);
  return 0;
}