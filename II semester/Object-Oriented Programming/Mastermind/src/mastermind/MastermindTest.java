package mastermind;

public class MastermindTest {

	public static void main(String[] args) {
		MastermindGra gra = new MastermindGra(4, 6);

		System.out.println("MASTERMIND - Test algorytmów");
		System.out.println("Hasło: 4 pozycje, 6 kolorów (0-5)");
		System.out.println();

		int sumaProsty = 0;
		int sumaDrzewo = 0;

		// 100 testów z losowymi hasłami
		for (int i = 0; i < 100; i++) {
			int[] haslo = gra.wylosujHaslo();
			System.out.printf("Test %d - hasło: [%d,%d,%d,%d]\n",
					i+1, haslo[0], haslo[1], haslo[2], haslo[3]);

			int ruchyP = testAlgorytm(gra, new AlgorytmProsty(gra), haslo);
			int ruchyD = testAlgorytm(gra, new AlgorytmDrzewo(gra), haslo);

			System.out.printf("  Prosty: %d ruchów, Drzewo: %d ruchów\n", ruchyP, ruchyD);

			sumaProsty += ruchyP;
			sumaDrzewo += ruchyD;
			System.out.println();
		}

		System.out.println("PODSUMOWANIE:");
		System.out.printf("Prosty - suma: %d, średnia: %.1f\n",
				sumaProsty, (double)sumaProsty/100);
		System.out.printf("Drzewo - suma: %d, średnia: %.1f\n",
				sumaDrzewo, (double)sumaDrzewo/100);

		if (sumaDrzewo < sumaProsty) {
			System.out.println("Algorytm drzewo jest lepszy!");
		} else if (sumaProsty < sumaDrzewo) {
			System.out.println("Algorytm prosty jest lepszy!");
		} else {
			System.out.println("Oba algorytmy równe!");
		}
	}

	private static int testAlgorytm(MastermindGra gra, Object alg, int[] haslo) {
		int ruchy = 0;

		while (ruchy < 15) {
			int[] proba = null;

			if (alg instanceof AlgorytmProsty) {
				AlgorytmProsty a = (AlgorytmProsty) alg;
				proba = a.nastepnaProba();
				if (proba == null) break;

				Odpowiedz odp = gra.dajOdpowiedz(proba, haslo);
				ruchy++;

				if (odp.getCzarne() == gra.getDlugoscHasla()) {
					break; // wygrana
				}

				a.zaktualizujPoOdpowiedzi(proba, odp);
			} else {
				AlgorytmDrzewo a = (AlgorytmDrzewo) alg;
				proba = a.nastepnaProba();
				if (proba == null) break;

				Odpowiedz odp = gra.dajOdpowiedz(proba, haslo);
				ruchy++;

				if (odp.getCzarne() == gra.getDlugoscHasla()) {
					break; // wygrana
				}

				a.zaktualizujPoOdpowiedzi(proba, odp);
			}
		}

		return ruchy;
	}
}