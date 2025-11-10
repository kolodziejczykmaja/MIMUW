package testy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import totolotek.Losowy;
import totolotek.Minimalista;
import totolotek.Stalobankietowy;
import totolotek.Staloliczbowy;
import totolotek.BudzetPanstwa;
import totolotek.CentralaTotolotka;
import totolotek.Kolektura;
import totolotek.Losowanie;
import totolotek.Blankiet;
import totolotek.Kupon;
import totolotek.Zaklad;
import totolotek.Gracz;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class TotolotekTesty {

	// Test klasy CentralaTotolotka
	@Nested
	@DisplayName("Testy CentralaTotolotka")
	class CentralaTotolotkaTest {

		private CentralaTotolotka centrala;

		@BeforeEach
		void setUp() {
			centrala = new CentralaTotolotka(10_000_000_00L);
		}

		@Test
		@DisplayName("Tworzenie centrali z początkowymi środkami")
		void testTworzenieCentrali() {
			assertNotNull(centrala);
			assertEquals(0, centrala.getLiczbaLosowan());
			assertTrue(centrala.getKolektury().isEmpty());
		}

		@Test
		@DisplayName("Dodawanie kolektury do centrali")
		void testDodawanieKolektury() {
			Kolektura kolektura = new Kolektura(centrala);
			assertEquals(1, centrala.getKolektury().size());
			assertTrue(centrala.getKolektury().contains(kolektura));
		}

		@Test
		@DisplayName("Rejestracja zakładów")
		void testRejestracjaZakladow() {
			ArrayList<Integer> numeryLosowan = new ArrayList<>(Arrays.asList(1, 2));
			centrala.zarejestrujZaklady(numeryLosowan, 5);

			// Dodaj ponownie zakłady na te same losowania
			centrala.zarejestrujZaklady(numeryLosowan, 3);

			// Test przebiegł pomyślnie jeśli nie rzucił wyjątku
			assertDoesNotThrow(() -> centrala.zarejestrujZaklady(numeryLosowan, 1));
		}

		@Test
		@DisplayName("Przeprowadzanie losowania bez zakładów")
		void testPrzeprowadzanieLosowamiaBezZakladow() {
			// Próba przeprowadzenia losowania bez rejestracji zakładów
			assertThrows(NullPointerException.class, () -> centrala.przeprowadzLosowanie());
		}

		@Test
		@DisplayName("Dodawanie środków do centrali")
		void testDodawanieSrodkow() {
			long poczatkoweSrodki = 10_000_000_00L;
			centrala.dodajSrodki(1_000_00L);

			// Sprawdzenie odbywa się pośrednio przez brak wyjątków
			assertDoesNotThrow(() -> centrala.dodajSrodki(500_00L));
		}

		@Test
		@DisplayName("Wypłata nagrody z wystarczającymi środkami")
		void testWyplataNagrodyZWystarczajacymiSrodkami() {
			assertDoesNotThrow(() -> centrala.wyplacNagrode(1_000_00L));
		}

		@Test
		@DisplayName("Wypłata nagrody z niewystarczającymi środkami - subwencja")
		void testWyplataNagrodyZSubwencja() {
			// Reset budżetu państwa
			BudzetPanstwa.przekazSubwencje(-BudzetPanstwa.getStan().hashCode()); // Reset test

			assertDoesNotThrow(() -> centrala.wyplacNagrode(50_000_000_00L));
		}
	}

	// Test klasy Kolektura
	@Nested
	@DisplayName("Testy Kolektura")
	class KolekturaTest {

		private CentralaTotolotka centrala;
		private Kolektura kolektura;

		@BeforeEach
		void setUp() {
			centrala = new CentralaTotolotka(10_000_000_00L);
			kolektura = new Kolektura(centrala);
		}

		@Test
		@DisplayName("Tworzenie kolektury")
		void testTworzenieKolektury() {
			assertNotNull(kolektura);
			assertTrue(kolektura.getNumerKolektury() > 0);
			assertEquals(centrala, kolektura.getCentrala());
			assertTrue(kolektura.getKupony().isEmpty());
		}

		@Test
		@DisplayName("Generowanie kuponu na chybił trafił - prawidłowe parametry")
		void testGenerowanieKuponuNaChybilTrafil() {
			Kupon kupon = kolektura.generujKuponNaChybilTrafil(2, new int[]{3});

			assertNotNull(kupon);
			assertEquals(2, kupon.getIleZakladow());
			assertEquals(1, kolektura.getKupony().size());
			assertTrue(kolektura.getKupony().contains(kupon));
		}

		@Test
		@DisplayName("Generowanie kuponu - nieprawidłowa liczba zakładów")
		void testGenerowanieKuponuNieprawidlowaLiczbaZakladow() {
			assertThrows(IllegalArgumentException.class,
					() -> kolektura.generujKuponNaChybilTrafil(0, new int[]{1}));

			assertThrows(IllegalArgumentException.class,
					() -> kolektura.generujKuponNaChybilTrafil(9, new int[]{1}));
		}

		@Test
		@DisplayName("Generowanie kuponu - null jako liczba losowań")
		void testGenerowanieKuponuNullLiczbaLosowan() {
			assertThrows(IllegalArgumentException.class,
					() -> kolektura.generujKuponNaChybilTrafil(1, null));
		}

		@Test
		@DisplayName("Generowanie kuponu z blankietu - prawidłowy blankiet")
		void testGenerowanieKuponuZBlankietu() {
			List<List<Integer>> zaklady = Arrays.asList(
					Arrays.asList(1, 2, 3, 4, 5, 6),
					Arrays.asList(7, 8, 9, 10, 11, 12)
			);
			int[] losowania = {2};
			Blankiet blankiet = new Blankiet(zaklady, losowania, null);

			Kupon kupon = kolektura.generujKuponZBlankietu(blankiet);

			assertNotNull(kupon);
			assertEquals(2, kupon.getIleZakladow());
		}

		@Test
		@DisplayName("Generowanie kuponu z blankietu - null blankiet")
		void testGenerowanieKuponuZBlankietuNull() {
			assertThrows(IllegalArgumentException.class,
					() -> kolektura.generujKuponZBlankietu(null));
		}

		@Test
		@DisplayName("Generowanie kuponu z blankietu - pusty blankiet")
		void testGenerowanieKuponuZBlankietuPusty() {
			Blankiet blankiet = new Blankiet();
			// Usuń domyślne zaznaczenie
			blankiet.ustawLiczbaLosowań(1);

			assertThrows(IllegalArgumentException.class,
					() -> kolektura.generujKuponZBlankietu(blankiet));
		}
	}

	// Test klasy Kupon
	@Nested
	@DisplayName("Testy Kupon")
	class KuponTest {

		private CentralaTotolotka centrala;
		private Kolektura kolektura;

		@BeforeEach
		void setUp() {
			centrala = new CentralaTotolotka(10_000_000_00L);
			kolektura = new Kolektura(centrala);
		}

		@Test
		@DisplayName("Tworzenie kuponu - prawidłowe parametry")
		void testTworzenieKuponu() {
			int[][] liczby = {{1, 2, 3, 4, 5, 6}, {7, 8, 9, 10, 11, 12}};
			int[] losowania = {2};

			Kupon kupon = new Kupon(kolektura, liczby, losowania);

			assertNotNull(kupon);
			assertEquals(2, kupon.getIleZakladow());
			assertNotNull(kupon.getIdentyfikator());
			assertFalse(kupon.czyZrealizowany());
		}

		@Test
		@DisplayName("Tworzenie kuponu - null kolektura")
		void testTworzenieKuponuNullKolektura() {
			int[][] liczby = {{1, 2, 3, 4, 5, 6}};
			int[] losowania = {1};

			assertThrows(IllegalArgumentException.class,
					() -> new Kupon(null, liczby, losowania));
		}

		@Test
		@DisplayName("Tworzenie kuponu - null liczby")
		void testTworzenieKuponuNullLiczby() {
			int[] losowania = {1};

			assertThrows(IllegalArgumentException.class,
					() -> new Kupon(kolektura, null, losowania));
		}

		@Test
		@DisplayName("Tworzenie kuponu - nieprawidłowa liczba zakładów")
		void testTworzenieKuponuNieprawidlowaLiczbaZakladow() {
			int[][] liczby = new int[9][6]; // 9 zakładów - za dużo
			int[] losowania = {1};

			assertThrows(IllegalArgumentException.class,
					() -> new Kupon(kolektura, liczby, losowania));
		}

		@Test
		@DisplayName("Obliczanie ceny kuponu")
		void testObliczanieCenyKuponu() {
			int[][] liczby = {{1, 2, 3, 4, 5, 6}, {7, 8, 9, 10, 11, 12}};
			int[] losowania = {3};

			Kupon kupon = new Kupon(kolektura, liczby, losowania);

			// 2 zakłady * 3 losowania * 3zł = 18zł = 1800 groszy
			assertEquals(1800, kupon.getCenaBrutto());
			// Podatek: 2 * 3 * 60 groszy = 360 groszy
			assertEquals(360, kupon.getPodatek());
			// Netto: 1800 - 360 = 1440 groszy
			assertEquals(1440, kupon.getCenaNetto());
		}

		@Test
		@DisplayName("Oznaczanie kuponu jako zrealizowany")
		void testOznaczanieKuponuJakoZrealizowany() {
			int[][] liczby = {{1, 2, 3, 4, 5, 6}};
			int[] losowania = {1};

			Kupon kupon = new Kupon(kolektura, liczby, losowania);

			assertFalse(kupon.czyZrealizowany());
			kupon.oznaczJakoZrealizowany();
			assertTrue(kupon.czyZrealizowany());
		}
	}

	// Test klasy Blankiet
	@Nested
	@DisplayName("Testy Blankiet")
	class BlankietTest {

		@Test
		@DisplayName("Tworzenie pustego blankietu")
		void testTworzeniePustegoBlankietu() {
			Blankiet blankiet = new Blankiet();

			assertNotNull(blankiet);
			assertTrue(blankiet.czyLosowanieZaznaczone(1));
			assertEquals(1, blankiet.getZaznaczoneLosowania().length);
			assertTrue(blankiet.getPrawidloweZaklady().isEmpty());
		}

		@Test
		@DisplayName("Tworzenie blankietu z zakładami")
		void testTworzenieBlankietuZZakladami() {
			List<List<Integer>> zaklady = Arrays.asList(
					Arrays.asList(1, 2, 3, 4, 5, 6),
					Arrays.asList(7, 8, 9, 10, 11, 12)
			);
			int[] losowania = {3};

			Blankiet blankiet = new Blankiet(zaklady, losowania, null);

			assertEquals(2, blankiet.getPrawidloweZaklady().size());
			assertTrue(blankiet.czyLosowanieZaznaczone(3));
		}

		@Test
		@DisplayName("Zaznaczanie i odznaczanie liczb")
		void testZaznaczanieOdznaczanieLiczb() {
			Blankiet blankiet = new Blankiet();

			blankiet.zaznaczLiczbe(1, 15);
			assertTrue(blankiet.czyLiczbaZaznaczona(1, 15));

			blankiet.odznaczLiczbe(1, 15);
			assertFalse(blankiet.czyLiczbaZaznaczona(1, 15));
		}

		@Test
		@DisplayName("Anulowanie i przywracanie pola")
		void testAnulowaniePrzywracaniePola() {
			Blankiet blankiet = new Blankiet();

			assertFalse(blankiet.czyPoleAnulowane(1));

			blankiet.anulujPole(1);
			assertTrue(blankiet.czyPoleAnulowane(1));

			blankiet.przywrocPole(1);
			assertFalse(blankiet.czyPoleAnulowane(1));
		}

		@Test
		@DisplayName("Ustawianie liczby losowań")
		void testUstawianieLiczbyLosowan() {
			Blankiet blankiet = new Blankiet();

			blankiet.ustawLiczbaLosowań(5);
			assertTrue(blankiet.czyLosowanieZaznaczone(5));
			assertFalse(blankiet.czyLosowanieZaznaczone(1));
		}

		@Test
		@DisplayName("Nieprawidłowe parametry dla blankietu")
		void testNieprawidloweParametryDlaBlankietu() {
			assertThrows(IllegalArgumentException.class,
					() -> new Blankiet(null, new int[]{1}, null));

			assertThrows(IllegalArgumentException.class,
					() -> {
						Blankiet blankiet = new Blankiet();
						blankiet.zaznaczLiczbe(0, 15); // Nieprawidłowe pole
					});

			assertThrows(IllegalArgumentException.class,
					() -> {
						Blankiet blankiet = new Blankiet();
						blankiet.ustawLiczbaLosowań(11); // Za dużo losowań
					});
		}
	}

	// Test klasy Losowanie
	@Nested
	@DisplayName("Testy Losowanie")
	class LosowanieTest {

		@Test
		@DisplayName("Tworzenie losowania")
		void testTworzenieLosowania() {
			Losowanie losowanie = new Losowanie(1);

			assertNotNull(losowanie);
			assertEquals(1, losowanie.getNumer());
			assertEquals(6, losowanie.getWyniki().size());

			// Sprawdź czy wszystkie wyniki są w zakresie 1-49
			for (int wynik : losowanie.getWyniki()) {
				assertTrue(wynik >= 1 && wynik <= 49);
			}
		}

		@Test
		@DisplayName("Unikalne wyniki losowania")
		void testUnikalneWynikiLosowania() {
			Losowanie losowanie = new Losowanie(1);
			Set<Integer> wyniki = losowanie.getWyniki();

			// Set automatycznie zapewnia unikatowość
			assertEquals(6, wyniki.size());
		}

		@Test
		@DisplayName("Ustawianie kwot wygranych")
		void testUstawianieKwotWygranych() {
			Losowanie losowanie = new Losowanie(1);
			Map<Integer, Long> kwotyWygranych = new HashMap<>();
			kwotyWygranych.put(6, 1000000L);
			kwotyWygranych.put(5, 5000L);
			kwotyWygranych.put(4, 100L);
			kwotyWygranych.put(3, 50L);

			losowanie.setKwotyWygranych(kwotyWygranych);

			assertEquals(1000000L, losowanie.getKwotaWygranej(6));
			assertEquals(5000L, losowanie.getKwotaWygranej(5));
			assertEquals(100L, losowanie.getKwotaWygranej(4));
			assertEquals(50L, losowanie.getKwotaWygranej(3));
		}
	}

	// Test klasy Zaklad
	@Nested
	@DisplayName("Testy Zaklad")
	class ZakladTest {

		@Test
		@DisplayName("Tworzenie zakładu")
		void testTworzenieZakladu() {
			int[] liczby = {1, 5, 12, 23, 34, 45};
			Zaklad zaklad = new Zaklad(liczby);

			assertNotNull(zaklad);
			assertEquals(6, zaklad.getLiczby().length);

			// Sprawdź czy liczby są posortowane
			int[] wynik = zaklad.getLiczby();
			for (int i = 0; i < wynik.length - 1; i++) {
				assertTrue(wynik[i] <= wynik[i + 1]);
			}
		}

		@Test
		@DisplayName("Niezmienność tablicy w zakładzie")
		void testNiezmiennoscTablicyWZakladzie() {
			int[] liczby = {5, 1, 12, 23, 34, 45};
			Zaklad zaklad = new Zaklad(liczby);

			// Modyfikacja oryginalnej tablicy nie powinna wpłynąć na zakład
			liczby[0] = 99;

			int[] wynikiZakladu = zaklad.getLiczby();
			assertNotEquals(99, wynikiZakladu[0]);
		}
	}

	// Test klas graczy
	@Nested
	@DisplayName("Testy Graczy")
	class GraczyTest {

		private CentralaTotolotka centrala;
		private Kolektura kolektura;

		@BeforeEach
		void setUp() {
			centrala = new CentralaTotolotka(10_000_000_00L);
			kolektura = new Kolektura(centrala);
		}

		@Test
		@DisplayName("Tworzenie gracza Minimalista")
		void testTworzenieGraczaMinimalista() {
			Minimalista gracz = new Minimalista("Jan", "Kowalski", "12345678901", 10000, kolektura);

			assertNotNull(gracz);
			assertTrue(gracz.toString().contains("Minimalista"));
		}

		@Test
		@DisplayName("Tworzenie gracza Minimalista - null kolektura")
		void testTworzenieGraczaMinimalistaNullKolektura() {
			assertThrows(IllegalArgumentException.class,
					() -> new Minimalista("Jan", "Kowalski", "12345678901", 10000, null));
		}

		@Test
		@DisplayName("Kupowanie kuponu przez Minimalistę")
		void testKupowanieKuponuPrzezMinimaliste() {
			Minimalista gracz = new Minimalista("Jan", "Kowalski", "12345678901", 1000, kolektura);

			gracz.kupKupon();

			assertEquals(1, kolektura.getKupony().size());
		}

		@Test
		@DisplayName("Tworzenie gracza Losowy")
		void testTworzenieGraczaLosowy() {
			Losowy gracz = new Losowy("Anna", "Nowak", "98765432109", kolektura);

			assertNotNull(gracz);
			assertTrue(gracz.toString().contains("Losowy"));
		}

		@Test
		@DisplayName("Tworzenie gracza Stałoliczbowy")
		void testTworzenieGraczaStaloliczbowy() {
			int[] ulubioneLiczby = {1, 2, 3, 4, 5, 6};
			List<Kolektura> ulubioneKolektury = Arrays.asList(kolektura);

			Staloliczbowy gracz = new Staloliczbowy("Piotr", "Wiśniewski", "11111111111",
					50000, ulubioneLiczby, ulubioneKolektury);

			assertNotNull(gracz);
			assertTrue(gracz.toString().contains("Stałoliczbowy"));
		}

		@Test
		@DisplayName("Tworzenie gracza Stałoliczbowy - nieprawidłowe ulubione liczby")
		void testTworzenieGraczaStaloliczbowy_NieprawidloweLiczby() {
			int[] ulubioneLiczby = {1, 2, 3, 4, 5}; // Za mało liczb
			List<Kolektura> ulubioneKolektury = Arrays.asList(kolektura);

			assertThrows(IllegalArgumentException.class,
					() -> new Staloliczbowy("Piotr", "Wiśniewski", "11111111111",
							50000, ulubioneLiczby, ulubioneKolektury));
		}

		@Test
		@DisplayName("Tworzenie gracza Stałobankietowy")
		void testTworzenieGraczaStalobankietowy() {
			List<List<Integer>> zaklady = Arrays.asList(Arrays.asList(1, 2, 3, 4, 5, 6));
			Blankiet blankiet = new Blankiet(zaklady, new int[]{2}, null);
			List<Kolektura> ulubioneKolektury = Arrays.asList(kolektura);

			Stalobankietowy gracz = new Stalobankietowy("Maria", "Lewandowska", "22222222222",
					100000, blankiet, ulubioneKolektury, 3);

			assertNotNull(gracz);
			assertTrue(gracz.toString().contains("Stałobankietowy"));
		}
	}

	// Test klasy BudzetPanstwa
	@Nested
	@DisplayName("Testy BudzetPanstwa")
	class BudzetPanstwaTest {

		@Test
		@DisplayName("Dodawanie podatku")
		void testDodawaniePodatku() {
			// Test jest ograniczony ze względu na statyczną naturę klasy
			assertDoesNotThrow(() -> BudzetPanstwa.dodajPodatek(1000));
		}

		@Test
		@DisplayName("Przekazywanie subwencji")
		void testPrzekazываnieSubwencji() {
			assertDoesNotThrow(() -> BudzetPanstwa.przekazSubwencje(5000));
		}

		@Test
		@DisplayName("Pobieranie stanu budżetu")
		void testPobieranieStanuBudzetu() {
			String stan = BudzetPanstwa.getStan();
			assertNotNull(stan);
			assertTrue(stan.contains("Podatki:"));
			assertTrue(stan.contains("Subwencje:"));
		}
	}

	// Testy integracyjne
	@Nested
	@DisplayName("Testy Integracyjne")
	class TestyIntegracyjne {

		@Test
		@DisplayName("Pełny cykl gry - kupno kuponu i odbiór wygranej")
		void testPelnyCyklGry() {
			// Przygotowanie
			CentralaTotolotka centrala = new CentralaTotolotka(10_000_000_00L);
			Kolektura kolektura = new Kolektura(centrala);
			Minimalista gracz = new Minimalista("Jan", "Kowalski", "12345678901", 10000, kolektura);

			// Kupno kuponu
			gracz.kupKupon();
			assertEquals(1, kolektura.getKupony().size());

			// Zarejestrowanie zakładów i przeprowadzenie losowania
			ArrayList<Integer> numerLosowan = new ArrayList<>(Arrays.asList(1));
			centrala.zarejestrujZaklady(numerLosowan, 1);

			assertDoesNotThrow(() -> centrala.przeprowadzLosowanie());
			assertEquals(1, centrala.getLiczbaLosowan());
		}

		@Test
		@DisplayName("Tworzenie wielu graczy różnych typów")
		void testTworzenieWieluGraczyRoznychTypow() {
			CentralaTotolotka centrala = new CentralaTotolotka(10_000_000_00L);

			List<Kolektura> kolektury = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				kolektury.add(new Kolektura(centrala));
			}

			// Minimaliści
			Minimalista min1 = new Minimalista("Jan", "Kowalski", "111", 10000, kolektury.get(0));
			Minimalista min2 = new Minimalista("Anna", "Nowak", "222", 15000, kolektury.get(1));

			// Losowi
			Losowy los1 = new Losowy("Piotr", "Wiśniewski", "333", kolektury.get(0));
			Losowy los2 = new Losowy("Maria", "Lewandowska", "444", kolektury.get(2));

			List<Gracz> gracze = Arrays.asList(min1, min2, los1, los2);

			assertEquals(4, gracze.size());
			assertEquals(3, centrala.getKolektury().size());
		}

		@Test
		@DisplayName("Symulacja kilku rund gry")
		void testSymulacjaKilkuRundGry() {
			CentralaTotolotka centrala = new CentralaTotolotka(10_000_000_00L);
			Kolektura kolektura = new Kolektura(centrala);

			List<Gracz> gracze = Arrays.asList(
					new Minimalista("Gracz1", "Kowalski", "111", 50000, kolektura),
					new Minimalista("Gracz2", "Nowak", "222", 50000, kolektura),
					new Losowy("Gracz3", "Wiśniewski", "333", kolektura)
			);

			// Symulacja 3 rund
			for (int runda = 1; runda <= 3; runda++) {
				// Kupowanie kuponów
				for (Gracz gracz : gracze) {
					gracz.kupKupon();
				}

				// Rejestracja zakładów (uproszczona)
				ArrayList<Integer> numerLosowan = new ArrayList<>(Arrays.asList(runda));
				centrala.zarejestrujZaklady(numerLosowan, kolektura.getKupony().size());

				// Przeprowadzenie losowania
				assertDoesNotThrow(() -> centrala.przeprowadzLosowanie());

				// Sprawdzenie i odbiór wygranych
				for (Gracz gracz : gracze) {
					assertDoesNotThrow(() -> gracz.sprawdzIOdbierzWygrane());
				}
			}

			assertEquals(3, centrala.getLiczbaLosowan());
			assertTrue(kolektura.getKupony().size() >= 3); // Gracze kupili kupony w 3 rundach
		}
	}
}