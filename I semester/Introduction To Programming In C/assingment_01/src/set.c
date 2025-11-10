/*
 * This program implements the game "Set", which is a card game that involves
 * identifying sets of cards based on their attributes. Each card has
 * four attributes, and a set consists of three cards where each attribute
 * is either all the same or all different.
 *
 * Author: Maja Kołodziejczyk
 * Created in: November 2024
 */

#include <stdio.h>

#define NUMBER_OF_CARDS 81
#define NUMBER_OF_ATTRIBUTES 4
#define NUMBER_OF_CARDS_IN_SET 3
#define CARDS_12 12

// Function to load cards into a 2D array.
int LoadCards(int card[][NUMBER_OF_ATTRIBUTES]) {
  int i = 0;
  int character;
  while ((character = getchar()) != EOF) {
    if (character >= '1' && character <= '3') {
      card[i][0] = character - '0';
      for (int j = 1; j < NUMBER_OF_ATTRIBUTES; ++j) {
        card[i][j] = getchar() - '0';
      }
      ++i;
    }
  }
  return i;
}

// Check if cards at indices i, j, k form a set.
int SetFound(int i, int j, int k, int card[][NUMBER_OF_ATTRIBUTES]) {
  int set = 1;
  for (int x = 0; x < NUMBER_OF_ATTRIBUTES; ++x) {
    if (!((card[i][x] == card[j][x] && card[j][x] == card[k][x]) ||
          (card[i][x] != card[j][x] && card[j][x] != card[k][x] &&
           card[i][x] != card[k][x]))) {
      set = 0;
    }
  }
  return set;
}

/* Print the cards that formed a set, and then
remove them by assigning -1 to their attributes. */
void PrintSet(int i, int j, int k, int card[][NUMBER_OF_ATTRIBUTES]) {
  printf("- ");
  for (int x = 0; x < NUMBER_OF_ATTRIBUTES; ++x) {
    printf("%d", card[i][x]);
    card[i][x] = -1;
  }
  printf(" ");
  for (int x = 0; x < NUMBER_OF_ATTRIBUTES; ++x) {
    printf("%d", card[j][x]);
    card[j][x] = -1;
  }
  printf(" ");
  for (int x = 0; x < NUMBER_OF_ATTRIBUTES; ++x) {
    printf("%d", card[k][x]);
    card[k][x] = -1;
  }
  printf("\n");
}

/* Iterate through the cards that have not been removed and are
available, then check if there is a set among them.
If there is, set found to 1 and call the PrintSet function. */
int IsThereASet(int DeckLocation, int card[][NUMBER_OF_ATTRIBUTES]) {
  int i = 0, j, k;
  int Set = 0;
  while (i < DeckLocation - 2 && Set == 0) {
    if (card[i][0] != -1) {
      j = i + 1;
      while (j < DeckLocation - 1 && Set == 0) {
        if (card[j][0] != -1) {
          k = j + 1;
          while (k < DeckLocation && Set == 0) {
            if (card[k][0] != -1) {
              if (SetFound(i, j, k, card)) {
                PrintSet(i, j, k, card);
                Set = 1;
              }
            }
            ++k;
          }
        }
        ++j;
      }
    }
    ++i;
  }
  if (Set) {
    return 1;
  } else {
    return 0;
  }
}

/* Print all cards that are currently on the table. */
void PrintCurrentState(int NumberOfCardsOnTable,
                       int card[][NUMBER_OF_ATTRIBUTES]) {
  int i = 0;
  int PrintedCards = 0;
  printf("=");
  while (PrintedCards != NumberOfCardsOnTable) {
    if (card[i][0] != -1) {
      printf(" ");
      for (int j = 0; j < NUMBER_OF_ATTRIBUTES; ++j) {
        printf("%d", card[i][j]);
      }
      ++PrintedCards;
    }
    ++i;
  }
  printf("\n");
}

int main(void) {
  int card[NUMBER_OF_CARDS][NUMBER_OF_ATTRIBUTES];
  int TotalCards, NumberOfCardsOnTable, DeckLocation;
  TotalCards = LoadCards(card);
  // Check how many cards to place on the table initially.
  if (TotalCards >= CARDS_12) {
    NumberOfCardsOnTable = CARDS_12;
  } else {
    NumberOfCardsOnTable = TotalCards;
  }
  DeckLocation = NumberOfCardsOnTable;
  TotalCards -= NumberOfCardsOnTable;
  // Print the initial state.
  PrintCurrentState(NumberOfCardsOnTable, card);
  // Enter this function if there are still cards in the deck.
  while (TotalCards > 0) {
    if (IsThereASet(DeckLocation, card)) {
      if (NumberOfCardsOnTable > CARDS_12) {
        NumberOfCardsOnTable -= NUMBER_OF_CARDS_IN_SET;
        DeckLocation -= NUMBER_OF_CARDS_IN_SET;
      }
      TotalCards -= NUMBER_OF_CARDS_IN_SET;
      DeckLocation += NUMBER_OF_CARDS_IN_SET;
      PrintCurrentState(NumberOfCardsOnTable, card);
    } else {
      printf("+\n");
      NumberOfCardsOnTable += NUMBER_OF_CARDS_IN_SET;
      if (TotalCards == 3) {
        TotalCards -= NUMBER_OF_CARDS_IN_SET;
      }
      DeckLocation += NUMBER_OF_CARDS_IN_SET;
      PrintCurrentState(NumberOfCardsOnTable, card);
    }
  }
  // Enter this function if there are no cards left in the deck.
  while (IsThereASet(DeckLocation, card)) {
    NumberOfCardsOnTable -= NUMBER_OF_CARDS_IN_SET;
    PrintCurrentState(NumberOfCardsOnTable, card);
  }
  return 0;
}
