package mastermind;

import java.util.*;

public class MastermindGra {
	private int dlugoscHasla;
	private int liczbaKolorow;
	private Random random;

	public MastermindGra(int dlugoscHasla, int liczbaKolorow) {
		this.dlugoscHasla = dlugoscHasla;
		this.liczbaKolorow = liczbaKolorow;
		this.random = new Random();
	}

	public int getDlugoscHasla() {
		return dlugoscHasla;
	}

	public Odpowiedz dajOdpowiedz(int[] proba, int[] haslo) {
		if (proba.length != dlugoscHasla || haslo.length != dlugoscHasla) {
			throw new IllegalArgumentException("Nieprawidłowa długość kombinacji");
		}

		int czarne = 0;
		int biale = 0;

		boolean[] uzytePozycjeHasla = new boolean[dlugoscHasla];
		boolean[] uzytePozycjeProby = new boolean[dlugoscHasla];

		// Najpierw liczymy czarne (dokładne trafienia)
		for (int i = 0; i < dlugoscHasla; i++) {
			if (proba[i] == haslo[i]) {
				czarne++;
				uzytePozycjeHasla[i] = true;
				uzytePozycjeProby[i] = true;
			}
		}

		// Potem liczymy białe (kolor się zgadza ale pozycja nie)
		for (int i = 0; i < dlugoscHasla; i++) {
			if (!uzytePozycjeProby[i]) {
				for (int j = 0; j < dlugoscHasla; j++) {
					if (!uzytePozycjeHasla[j] && proba[i] == haslo[j]) {
						biale++;
						uzytePozycjeHasla[j] = true;
						break;
					}
				}
			}
		}

		return new Odpowiedz(czarne, biale);
	}

	public List<int[]> wygenerujWszystkieKombinacje() {
		List<int[]> kombinacje = new ArrayList<>();
		wygenerujKombinacjeRekurencyjnie(new int[dlugoscHasla], 0, kombinacje);
		return kombinacje;
	}

	private void wygenerujKombinacjeRekurencyjnie(int[] aktualna, int pozycja, List<int[]> kombinacje) {
		if (pozycja == dlugoscHasla) {
			kombinacje.add(aktualna.clone());
			return;
		}

		for (int kolor = 0; kolor < liczbaKolorow; kolor++) {
			aktualna[pozycja] = kolor;
			wygenerujKombinacjeRekurencyjnie(aktualna, pozycja + 1, kombinacje);
		}
	}

	public List<int[]> filtrujKombinacje(List<int[]> kombinacje, int[] proba, Odpowiedz odpowiedz) {
		List<int[]> zgodne = new ArrayList<>();

		for (int[] kombinacja : kombinacje) {
			Odpowiedz testowa = dajOdpowiedz(proba, kombinacja);
			if (testowa.equals(odpowiedz)) {
				zgodne.add(kombinacja);
			}
		}

		return zgodne;
	}

	public int[] wylosujHaslo() {
		int[] haslo = new int[dlugoscHasla];
		for (int i = 0; i < dlugoscHasla; i++) {
			haslo[i] = random.nextInt(liczbaKolorow);
		}
		return haslo;
	}
}