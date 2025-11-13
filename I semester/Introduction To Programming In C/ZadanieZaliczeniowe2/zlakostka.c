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

void UzupełnianieKostki(int ***Kostka) {
	for (int i = 0; i < LICZBA_ŚCIAN_W_KOSTCE; ++i) {
		for (int j = 0; j < N; ++j) {
			for (int k = 0; k < N; ++k) {
				Kostka[i][j][k] = i;
			}
		}
	}
}

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
			}
			else {
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

void ŚcianaZero(int ***Kostka, int warstwa, int kąt) {
	for (int i = 0; i < warstwa; ++i) {
		for (int j = 0; j < kąt; ++j) {
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
	//PRZECIWNA SCIANE MUSIMY OBRC
	for (int k = 0; k < KĄT_360 - kąt; ++k) {
		if (warstwa == N) {
			ObrtótŚciany(Kostka, ŚCIANA_D);
		}
	}
}

void ŚcianaJeden(int ***Kostka, int warstwa, int kąt) {
	for (int i = 0, n = N - 1; i < warstwa && n >= N - warstwa; ++i, --n) {
		for (int j = 0; j < kąt; ++j) {
			for (int k = 0, m = N - 1; k < N && m >= 0; ++k, --m) {
				int tmp = Kostka[ŚCIANA_D][k][i];
				Kostka[ŚCIANA_D][k][i] = Kostka[ŚCIANA_F][k][i];
				Kostka[ŚCIANA_F][k][i] = Kostka[ŚCIANA_U][k][i];
				Kostka[ŚCIANA_U][k][i] = Kostka[ŚCIANA_B][m][n];
				Kostka[ŚCIANA_B][m][n] = tmp;
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

/*
 * IJSDSKDJSD
 * CDJC
 */
void ŚcianaDwa(int ***Kostka, int warstwa, int kąt) {
	for (int i = 0, n = N - 1; i < warstwa && n >= N - warstwa; ++i, --n) {
		for (int k = 0; k < kąt; ++k) {
			for (int a = 0, d = N - 1; a < N && d >= 0; ++a, --d) {
				int tmp = Kostka[ŚCIANA_U][n][d];
				Kostka[ŚCIANA_U][n][d] = Kostka[ŚCIANA_L][a][n];
				Kostka[ŚCIANA_L][a][n] = Kostka[ŚCIANA_D][i][a];
				Kostka[ŚCIANA_D][i][a] = Kostka[ŚCIANA_R][d][i];
				Kostka[ŚCIANA_R][d][i] = tmp;
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
	for (int n = 0, i = N - 1; n < warstwa && i >= N - warstwa; ++n, --i) {
		for (int j = 0; j < kąt; ++j) {
			for (int m = 0, k = N - 1; m < N && k >= 0; ++m, --k) {
				int tmp = Kostka[ŚCIANA_U][m][i];
				Kostka[ŚCIANA_U][m][i] = Kostka[ŚCIANA_F][m][i];
				Kostka[ŚCIANA_F][m][i] = Kostka[ŚCIANA_D][m][i];
				Kostka[ŚCIANA_D][m][i] = Kostka[ŚCIANA_B][k][n];
				Kostka[ŚCIANA_B][k][n] = tmp;
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
	for (int i = 0, n = N - 1; i < warstwa && n >= N - warstwa; ++i, --n) {
		for (int j = 0; j < kąt; ++j) {
			for (int a = 0, d = N - 1; a < N && d >= 0; ++a, --d) {
				int tmp = Kostka[ŚCIANA_U][i][a];
				Kostka[ŚCIANA_U][i][a] = Kostka[ŚCIANA_R][a][n];
				Kostka[ŚCIANA_R][a][n] = Kostka[ŚCIANA_D][n][d];
				Kostka[ŚCIANA_D][n][d] = Kostka[ŚCIANA_L][d][i];
				Kostka[ŚCIANA_L][d][i] = tmp;
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
	for (int i = N - 1; i >= N - warstwa; --i) {
		for (int j = 0; j < kąt; ++j) {
			int *tmp = Kostka[ŚCIANA_B][i];
			Kostka[ŚCIANA_B][i] = Kostka[ŚCIANA_R][i];
			Kostka[ŚCIANA_R][i] = Kostka[ŚCIANA_F][i];
			Kostka[ŚCIANA_F][i] = Kostka[ŚCIANA_L][i];
			Kostka[ŚCIANA_L][i] = tmp;
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