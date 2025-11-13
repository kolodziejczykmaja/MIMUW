package test;

import tablice.*;

import org.junit.jupiter.api.Test;
import tablice.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Zad1Test {
	// Testy z treści zadania.
	// Wydanie kompletne.

	@Test
	void testWłasnościSkalarów() {
		Skalar skalar = new Skalar(1.0);
		assertEquals(0, skalar.wymiar());
		assertArrayEquals(new int[]{}, skalar.kształt());
		assertEquals(1, skalar.liczba_elementów());
	}

	@Test
	void testWłasnościWektorów() {
		Wektor wektor1 = new Wektor(new double[]{1.0, 2.0, 1.0}, true);
		Wektor wektor2 = new Wektor(new double[]{2.0, 2.0, 3.0}, false);
		assertEquals(1, wektor1.wymiar());
		assertArrayEquals(new int[]{3}, wektor1.kształt());
		assertEquals(3, wektor1.liczba_elementów());
		assertEquals(1, wektor2.wymiar());
		assertArrayEquals(new int[]{3}, wektor2.kształt());
		assertEquals(3, wektor2.liczba_elementów());
	}

	@Test
	void testWłasnościMacierzy() {
		Macierz matrix = new Macierz(new double[][]{
				{1.0, 0.0, 2.0},
				{2.0, 1.0, 3.0},
				{1.0, 1.0, 1.0},
				{2.0, 3.0, 1.0}
		});
		assertEquals(2, matrix.wymiar());
		assertArrayEquals(new int[]{4, 3}, matrix.kształt());
		assertEquals(12, matrix.liczba_elementów());
	}

	@Test
	void testArytmetykiSkalarów() throws ZłyIndeks, NiezgodneWymiaryException {
		Skalar skalar1 = new Skalar(3.5);
		Skalar skalar2 = new Skalar(11.5);
		assertEquals(new Skalar(15.0), skalar1.suma(skalar2));

		Skalar skalar3 = new Skalar(3.0);
		Skalar skalar4 = new Skalar(12.0);
		assertEquals(new Skalar(36.0), skalar3.iloczyn(skalar4));
	}

	@Test
	void testArytmetykiSkalarWektor() throws ZłyIndeks, NiezgodneWymiaryException {
		for(boolean b: new boolean[]{true, false}) {
			// 3.0 + [1.0, 2.5] = [4.0, 5.5]
			Skalar skalar = new Skalar(3.0);
			Wektor wektor1 = new Wektor(new double[]{1.0, 2.5}, b);
			assertEquals(new Wektor(new double[]{4.0, 5.5}, b), skalar.suma(wektor1));

			// 4.0 * [1.5, 2.25] = [6.0, 9.0]
			Wektor wektor2 = new Wektor(new double[]{1.5, 2.25}, b);
			assertEquals(new Wektor(new double[]{6.0, 9.0}, b),
					new Skalar(4.0).iloczyn(wektor2));
		}  // for b
	}

	@Test
	void testArytmetykiWektorSkalar() throws ZłyIndeks, NiezgodneWymiaryException {
		for(boolean b: new boolean[]{true, false}) {
			// [1.0, 2.5] + 3.0 = [4.0, 5.5]
			Skalar skalar = new Skalar(3.0);
			Wektor wektor1 = new Wektor(new double[]{1.0, 2.5}, b);
			assertEquals(new Wektor(new double[]{4.0, 5.5}, b),
					wektor1.suma(skalar));

			// [1.5, 2.25] * 4.0 = [6.0, 9.0]
			Wektor wektor2 = new Wektor(new double[]{1.5, 2.25}, b);
			assertEquals(new Wektor(new double[]{6.0, 9.0}, b),
					wektor2.iloczyn(new Skalar(4.0)));
		}  // for b
	}

	@Test
	void testArytmetykiSkalarMacierz() throws ZłyIndeks, NiezgodneWymiaryException{
		Skalar skalar = new Skalar(3.0);
		Macierz macierz = new Macierz(new double[][]{
				{1.25, 3.0, -12.0},
				{-51.0, 8.0, 3.5}
		});
		Macierz oczekiwanyWynikDodawania = new Macierz(new double[][]{
				{4.25, 6.0, -9.0},
				{-48.0, 11.0, 6.5}
		});
		assertEquals(oczekiwanyWynikDodawania, skalar.suma(macierz));

		Skalar skalar2 = new Skalar(-3.0);
		Macierz oczekiwanyWynikMnożenia = new Macierz(new double[][]{
				{-3.75, -9.0, 36.0},
				{153.0, -24.0, -10.5}
		});
		assertEquals(oczekiwanyWynikMnożenia, skalar2.iloczyn(macierz));

		// Odwrotna Kolejność
		assertEquals(oczekiwanyWynikDodawania, macierz.suma(skalar));
		assertEquals(oczekiwanyWynikMnożenia, macierz.iloczyn(skalar2));
	}

	@Test
	void testDodawaniaIMnożeniaWektorWektor() throws ZłyIndeks, NiezgodneWymiaryException {
		// Wektor + Wektor
		Wektor wektor1 = new Wektor(new double[]{1.0, 2.0, 3.0}, false);
		Wektor wektor2 = new Wektor(new double[]{1.0, 1.0, -2.0}, false);
		assertEquals(new Wektor(new double[]{2.0, 3.0, 1.0}, false),
				wektor1.suma(wektor2));

		Wektor wektor3 = new Wektor(new double[]{-2.0, 5.0}, true);
		Wektor wektor4 = new Wektor(new double[]{-5.0, 2.0}, true);
		assertEquals(new Wektor(new double[]{-7.0, 7.0}, true),
				wektor3.suma(wektor4));

		// Wektor * Wektor (Scalar result)
		Wektor wektor5 = new Wektor(new double[]{3.0, 2.0, -1.0}, false);
		Wektor wektor6 = new Wektor(new double[]{-2.0, 2.0, 1.0}, false);
		assertEquals(new Skalar(-3.0), wektor5.iloczyn(wektor6));

		Wektor wektor7 = new Wektor(new double[]{-2.0, -5.0, 1.0, 3.0}, true);
		Wektor wektor8 = new Wektor(new double[]{-5.0, 1.0, 2.0, -3.0}, true);
		assertEquals(new Skalar(-2.0), wektor7.iloczyn(wektor8));

		Wektor wektor9 = new Wektor(new double[]{1.0, 1.0, -2.0}, true);
		assertEquals(new Macierz(new double[][]{{-3.0}}), wektor1.iloczyn(wektor9));

		Wektor wektor10 = new Wektor(new double[]{1.0, 2.0, 3.0}, true);
		Wektor wektor11 = new Wektor(new double[]{1.0, 1.0, -2.0}, false);
		assertEquals(new Macierz(new double[][]{
				{1.0, 1.0, -2.0},
				{2.0, 2.0, -4.0},
				{3.0, 3.0, -6.0}
		}), wektor10.iloczyn(wektor11));
	}

	@Test
	void testDodawaniaWektorMacierz() throws ZłyIndeks, NiezgodneWymiaryException {
		// Wektor + Macierz
		Wektor wektor1 = new Wektor(new double[]{3.0, 1.5, -2.0}, false);
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 3.5, -12.0},
				{-5.0, 8.0, 3.0}
		});
		assertEquals(new Macierz(new double[][]{
				{4.0, 5.0, -14.0},
				{-2.0, 9.5, 1.0}
		}), wektor1.suma(macierz1));

		Wektor wektor2 = new Wektor(new double[]{7.5, -5.0}, true);
		assertEquals(new Macierz(new double[][]{
				{8.5, 11.0, -4.5},
				{-10.0, 3.0, -2.0}
		}), wektor2.suma(macierz1));

		// Odwrotna Kolejność

		// Macierz + Wektor (odwrotna kolejność)
		assertEquals(new Macierz(new double[][]{
				{4.0, 5.0, -14.0},
				{-2.0, 9.5, 1.0}
		}), macierz1.suma(wektor1));

		assertEquals(new Macierz(new double[][]{
				{8.5, 11.0, -4.5},
				{-10.0, 3.0, -2.0}
		}), macierz1.suma(wektor2));

	}

	@Test
	void testMnożeniaWektorMacierz() throws ZłyIndeks, NiezgodneWymiaryException {
		// Wektor * Macierz
		Wektor wektor1 = new Wektor(new double[]{1.0, 2.0, 3.0}, false);
		Wektor wektor2 = new Wektor(new double[]{1.0, 1.0, -2.0}, true);
		assertEquals(new Macierz(new double[][]{{-3.0}}), wektor1.iloczyn(wektor2));

		Wektor wektor3 = new Wektor(new double[]{1.0, 2.0, 3.0}, true);
		Wektor wektor4 = new Wektor(new double[]{1.0, 1.0, -2.0}, false);
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 1.0, -2.0},
				{2.0, 2.0, -4.0},
				{3.0, 3.0, -6.0}
		});
		assertEquals(macierz1, wektor3.iloczyn(wektor4));
	}

	@Test
	void testMnożeniaMacierzWektor() throws ZłyIndeks, NiezgodneWymiaryException {
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 2.0},
				{3.0, -2.0},
				{2.0, 1.0}
		});
		Wektor wektor1 = new Wektor(new double[]{-1.0, 3.0}, true);
		Wektor oczekiwany1 = new Wektor(new double[]{5.0, -9.0, 1.0}, true);
		assertEquals(oczekiwany1, macierz1.iloczyn(wektor1));

		// [1.0, -1.0, 2.0] * [[1.0, 2.0], [3.0, -2.0], [2.0, 1.0]] = [2.0, 6.0]
		Wektor wektor2 = new Wektor(new double[]{1.0, -1.0, 2.0}, false);
		Macierz macierz2 = new Macierz(new double[][]{
				{1.0, 2.0},
				{3.0, -2.0},
				{2.0, 1.0}
		});
		Wektor oczekiwany2 = new Wektor(new double[]{2.0, 6.0}, false);
		assertEquals(oczekiwany2, wektor2.iloczyn(macierz2));
	}

	@Test
	void testDodawaniaMacierzMacierz() throws ZłyIndeks, NiezgodneWymiaryException {
		// [[1.0, -2.0, 3.0], [2.0, 1.0, -1.0]] + [[3.0, -1.0, 2.0], [1.0, 1.0, -2.0]] = [[4.0, -3.0, 5.0], [3.0, 2.0, -3.0]]
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, -2.0, 3.0},
				{2.0, 1.0, -1.0}
		});
		Macierz macierz2 = new Macierz(new double[][]{
				{3.0, -1.0, 2.0},
				{1.0, 1.0, -2.0}
		});
		Macierz oczekiwany = new Macierz(new double[][]{
				{4.0, -3.0, 5.0},
				{3.0, 2.0, -3.0}
		});
		assertEquals(oczekiwany, macierz1.suma(macierz2));
	}

	@Test
	void testMnożeniaMacierzMacierz() throws ZłyIndeks, NiezgodneWymiaryException {
		// [[2.0, 0.5], [1.0, -2.0], [-1.0, 3.0]] * [[2.0, -1.0, 5.0], [-3.0, 2.0, -1.0]] = [[2.5, -1.0, 9.5], [8.0, -5.0, 7.0], [-11.0, 7.0, -8.0]]
		Macierz macierz1 = new Macierz(new double[][]{
				{2.0, 0.5},
				{1.0, -2.0},
				{-1.0, 3.0}
		});
		Macierz macierz2 = new Macierz(new double[][]{
				{2.0, -1.0, 5.0},
				{-3.0, 2.0, -1.0}
		});
		Macierz oczekiwany = new Macierz(new double[][]{
				{2.5, -1.0, 9.5},
				{8.0, -5.0, 7.0},
				{-11.0, 7.0, -8.0}
		});
		assertEquals(oczekiwany, macierz1.iloczyn(macierz2));
	}

	@Test
	void testNegacji() throws ZłyIndeks {
		Skalar skalar = new Skalar(17.0);
		assertEquals(new Skalar(-17.0), skalar.negacja());

		Wektor wektor = new Wektor(new double[]{10.0, -45.0, 0.0, 29.0, -3.0}, true);
		assertEquals(new Wektor(new double[]{-10.0, 45.0, 0.0, -29.0, 3.0}, true),
				wektor.negacja());

		Macierz macierz = new Macierz(new double[][]{
				{0.0, 0.5, -1.25},
				{11.0, -71.0, -33.5},
				{-2.0, -1.75, -99.0}
		});
		Macierz oczekiwany = new Macierz(new double[][]{
				{0.0, -0.5, 1.25},
				{-11.0, 71.0, 33.5},
				{2.0, 1.75, 99.0}
		});
		assertEquals(oczekiwany, macierz.negacja());
	}

	@Test
	void testPrzypisaniaSkalarów() throws NiezgodneWymiaryException, ZłyIndeks {
		// Przypisz skalar [0.5] do skalara [1.0]
		Skalar skalar1 = new Skalar(1.0);
		skalar1.przypisz(new Skalar(0.5));
		assertEquals(new Skalar(0.5), skalar1);

		// Przypisz skalar [0.5] do wektora [1.0, 2.0, 3.0]
		Wektor wektor1 = new Wektor(new double[]{1.0, 2.0, 3.0}, true);
		wektor1.przypisz(new Skalar(0.5));
		assertEquals(new Wektor(new double[]{0.5, 0.5, 0.5}, true), wektor1);

		// Przypisz skalar [0.5] do macierzy
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 2.0},
				{-3.0, -4.0},
				{5.0, -6.0}
		});
		macierz1.przypisz(new Skalar(0.5));
		assertEquals(new Macierz(new double[][]{
				{0.5, 0.5},
				{0.5, 0.5},
				{0.5, 0.5}
		}), macierz1);
	}

	@Test
	void testPrzypisaniaWektorów() throws NiezgodneWymiaryException, ZłyIndeks {
		// Przypisz wektor [1.5, 2.5, 3.5] do wektora [-1.0, 0.0, 1.0]
		Wektor wektor1 = new Wektor(new double[]{1.5, 2.5, 3.5}, true);
		Wektor wektor2 = new Wektor(new double[]{-1.0, 0.0, 1.0}, true);
		wektor2.przypisz(wektor1);
		assertEquals(new Wektor(new double[]{1.5, 2.5, 3.5}, true), wektor2);

		// Przypisz wektor [1.5, 2.5, 3.5] do wektora [-1.0, 0.0, 1.0] (wektor wierszowy i kolumnowy)
		Wektor wektor3 = new Wektor(new double[]{-1.0, 0.0, 1.0}, false);
		wektor3.przypisz(wektor1);
		assertEquals(new Wektor(new double[]{1.5, 2.5, 3.5}, true), wektor3);

		// Przypisz wektor [1.5, 2.5, 3.5] do macierzy
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 2.0, -1.0, -2.0},
				{-3.0, -4.0, 3.0, 4.0},
				{5.0, -6.0, -5.0, 6.0}
		});
		macierz1.przypisz(wektor1);
		assertEquals(new Macierz(new double[][]{
				{1.5, 1.5, 1.5, 1.5},
				{2.5, 2.5, 2.5, 2.5},
				{3.5, 3.5, 3.5, 3.5}
		}), macierz1);
	}

	@Test
	void testPrzypisaniaMacierzy() throws NiezgodneWymiaryException, ZłyIndeks{
		// Przypisz macierz [10.5, 20.5, 30.5; -1.5, 0.0, 1.5] do macierzy [1.0, 2.0, 3.0; 3.0, 2.0, 1.0]
		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 2.0, 3.0},
				{3.0, 2.0, 1.0}
		});
		Macierz macierz2 = new Macierz(new double[][]{
				{10.5, 20.5, 30.5},
				{-1.5, 0.0, 1.5}
		});

		macierz1.przypisz(macierz2);
		assertEquals(macierz2, macierz1);
	}

	@Test
	void testWycinków() throws ZłyIndeks, NiezgodneWymiaryException {
		Skalar skalar = new Skalar(13.125);
		assertEquals(skalar, skalar.wycinek());

		Wektor wektor = new Wektor(new double[]{1.0, 21.0, 32.0, 43.0, 54.0}, true);
		Wektor oczekiwanyWycinekWektora = new Wektor(new double[]{32.0, 43.0}, true);
		assertEquals(oczekiwanyWycinekWektora, wektor.wycinek(2, 3));

		Macierz macierz = new Macierz(new double[][]{
				{7.0, -21.0, 15.0, -31.0, 25.0},
				{-21.0, 15.0, -31.0, 25.0, 7.0},
				{15.0, -31.0, 25.0, -7.0, -21.0},
				{-31.0, 25.0, 7.0, -21.0, 15.0}
		});
		Macierz oczekiwanyWycinekMacierzy = new Macierz(new double[][]{
				{15.0, -31.0},
				{-31.0, 25.0},
				{25.0, 7.0}
		});
		assertEquals(oczekiwanyWycinekMacierzy, macierz.wycinek(1, 3, 1, 2));
	}

	@Test
	void testWycinany() throws NieprawidlowaOperacjaException,ZłyIndeks,
			NiezgodneWymiaryException {
		Macierz macierz1 = new Macierz(new double[][]{
				{1, 2, 3, 4, 5},
				{6, 7, 8, 9, 10},
				{11, 12, 13, 14, 15},
				{16, 17, 18, 19, 20},
				{21, 22, 23, 24, 25}
		});

		WycinekMacierzy wycinek1 = macierz1.wycinek(1, 2, 1, 3);
		macierz1.transponuj();
		Macierz macierz2 = new Macierz(new double[][]{{7, 8, 9}, {12, 13, 14}});
		assertEquals(wycinek1, macierz2);

		WycinekMacierzy wycinek2 = macierz1.wycinek(2, 4, 2, 4);
		wycinek1.przemnóż(wycinek2);
		Macierz macierz3 = new Macierz(new double[][]{
				{1, 6, 11, 16, 21},
				{2, 338, 548, 17, 22},
				{3, 458, 743, 18, 23},
				{4, 578, 938, 19, 24},
				{5, 10, 15, 20, 25}
		});
		assertEquals(macierz1, macierz3);

		WycinekMacierzy wycinek3 = macierz1.wycinek(1, 1, 2, 3);
		WycinekMacierzy wycinek4 = macierz1.wycinek(3, 3, 0, 1);
		wycinek4.transponuj();
		Macierz macierz4 = new Macierz(new double[][]{{12018}});
		assertEquals(wycinek3.iloczyn(wycinek4), macierz4);
	}

	@Test
	void testRownosciWycinekWycinek() throws NiezgodneWymiaryException, ZłyIndeks{
		Macierz macierz2 = new Macierz(new double[][]{
				{7.0, 7.0, 7.0, 7.0, 7.0},
				{7.0, 7.0, 1.0, 7.0, 7.0},
				{7.0, 7.0, 7.0, 7.0, 7.0},
				{7.0, 7.0, 7.0, 7.0, 7.0},
		});
		Macierz macierz3 = new WycinekMacierzy(macierz2, 0, 1, 0, 1, false);
		Macierz macierz4 = new WycinekMacierzy(macierz2, 2, 3, 3, 4, false);
		assertEquals(macierz4, macierz3);
	}
	@Test
	void testWycinkaWycinka() throws NiezgodneWymiaryException {
		Macierz macierz2 = new Macierz(new double[][]{
				{7.0, 7.0, 7.0, 7.0, 7.0},
				{7.0, 7.0, 7.0, 7.0, 7.0},
				{7.0, 7.0, 1.0, 7.0, 7.0},
				{7.0, 7.0, 7.0, 7.0, 7.0},
				{7.0, 7.0, 7.0, 7.0, 7.0}
		});
		Macierz wycinek1 = new WycinekMacierzy(macierz2, 0, 2 , 0 , 2, false);
		Macierz wycinek2 = new WycinekMacierzy(macierz2, 2, 4, 2 , 4, false);
		Macierz wycinek11 = new WycinekMacierzy(wycinek1, 0, 1, 0 , 1, false);
		Macierz wycinek22 = new WycinekMacierzy(wycinek2, 1, 2, 1 , 2, false);
		assertEquals(wycinek11, wycinek22);
	}

	@Test
	void tTest() throws Exception {
		Macierz macierz = new Macierz(new double[][]{
				{1, 2, 3, 4, 5},
				{-1, -2, -3, -4, -5},
				{1, 2, 3, 4, 5},
				{-1, -2, -3, -4, -5}
		});
		Macierz wycinek1 = macierz.wycinek(1, 3, 1, 2);
		Macierz wycinek2 = macierz.wycinek(0, 2, 2, 4);
		wycinek2.transponuj();
		Macierz wycinek4 = wycinek2.wycinek(1, 2, 0, 1);
		macierz.transponuj();
		wycinek1.przemnóż(wycinek4);
		Macierz wynik = new Macierz(new double[][]{{-23, 23},{23,-23},{-23,23}});
		assertEquals(wynik, wycinek1);
	}

	@Test
	void testWycinekITranspozycjaReferencje_ZeroBased() throws NiezgodneWymiaryException, ZłyIndeks {
		// Macierz 3x3
		Macierz macierz = new Macierz(new double[][]{
				{1.0, 2.0, 3.0},
				{4.0, 5.0, 6.0},
				{7.0, 8.0, 9.0}
		});

		Macierz wycinek1 = macierz.wycinek(0, 1, 0, 1);
		// => bierze wiersze 0 i 1, kolumny 0 i 1
		// [1.0, 2.0]
		// [4.0, 5.0]

		Macierz wycinek2 = macierz.wycinek(0, 1, 1, 2);
		// => bierze wiersze 0 i 1, kolumny 1 i 2
		// [2.0, 3.0]
		// [5.0, 6.0]

		Macierz wycinek3 = macierz.wycinek (1, 2, 0, 1);
		// [4.0, 5.0]
		// [7.0, 8.0]
		Macierz iloczyn = new Macierz(new double[][]{
				{29.0, 34.0},
				{62.0, 73.0}
		});
		assertEquals(iloczyn, wycinek2.iloczyn(wycinek3));

		// wchodzimy na głębokie wody

		wycinek2.transponuj();
		// [2.0 5.0]
		// [3.0 6.0]

		wycinek2.dodaj(new Skalar(1));      // dodajemy 1 do wszyskiego

		// wycinek2 *= wycinek3
		// [3 6] * [4 6]  =  [54 66]
		// [4 7]   [7 8]     [65 80]
		wycinek2.przemnóż(wycinek3);
		// patrzymy teraz czy macierz sie zgadza

		Macierz macierz1 = new Macierz(new double[][]{
				{1.0, 54.0, 65.0},
				{4.0, 66.0, 80.0},
				{7.0, 8.0, 9.0}
		});

		assertEquals(macierz1, macierz);

	}
}
