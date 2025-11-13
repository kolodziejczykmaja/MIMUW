package mastermind;

import java.util.*;

public class AlgorytmDrzewo {
	private MastermindGra gra;
	private List<int[]> mozliweKombinacje;

	public AlgorytmDrzewo(MastermindGra gra) {
		this.gra = gra;
		this.mozliweKombinacje = gra.wygenerujWszystkieKombinacje();
	}

	public int[] nastepnaProba() {
		if (mozliweKombinacje.isEmpty()) {
			return null;
		}

		if (mozliweKombinacje.size() == 1) {
			return mozliweKombinacje.getFirst().clone();
		}

		// Dla małej liczby możliwości użyj prostej strategii
		if (mozliweKombinacje.size() <= 3) {
			return mozliweKombinacje.getFirst().clone();
		}

		return znajdzNajlepszaProbe();
	}

	private int[] znajdzNajlepszaProbe() {
		int[] najlepszaProba = null;
		double najlepszyWynik = Double.MAX_VALUE;

		// Ograniczamy kandydatów dla wydajności
		List<int[]> kandydaci = mozliweKombinacje.size() > 50 ?
				mozliweKombinacje.subList(0, 50) : mozliweKombinacje;

		for (int[] proba : kandydaci) {
			double wynik = ocenProbe2Ruchy(proba);
			if (wynik < najlepszyWynik) {
				najlepszyWynik = wynik;
				najlepszaProba = proba;
			}
		}

		return najlepszaProba != null ? najlepszaProba.clone() :
				mozliweKombinacje.getFirst().clone();
	}

	private double ocenProbe2Ruchy(int[] proba) {
		Map<Odpowiedz, List<int[]>> grupy = new HashMap<>();

		// RUCH 1: Pogrupuj według odpowiedzi na moją próbę
		for (int[] kombinacja : mozliweKombinacje) {
			Odpowiedz odpowiedz = gra.dajOdpowiedz(proba, kombinacja);
			if (!grupy.containsKey(odpowiedz)) {
				grupy.put(odpowiedz, new ArrayList<>());
			}
			grupy.get(odpowiedz).add(kombinacja);
		}

		double suma = 0;

		// RUCH 2: Dla każdej grupy sprawdź najlepszy drugi ruch
		for (List<int[]> grupa : grupy.values()) {
			if (grupa.size() == 1) {
				suma += 1; // wygrywamy w następnym ruchu
			} else {
				// Symuluj drugi ruch - znajdź najlepszą próbę dla tej grupy
				int najlepszaDrugaProba = symulujDrugiRuch(grupa);
				suma += najlepszaDrugaProba;
			}
		}

		return suma;
	}

	//Dla kazdej grupy znow robily rozklad na odpowiedzi i wybieramy najwieksza
	private int symulujDrugiRuch(List<int[]> grupa) {
		if (grupa.size() <= 1) return 1;

		int najlepszyWynik = grupa.size();

		// Sprawdź kilka kandydatów na drugą próbę
		int limit = grupa.size();
		for (int i = 0; i < limit; i++) {
			int[] proba = grupa.get(i);

			Map<Odpowiedz, Integer> rozklad = new HashMap<>();
			for (int[] kombinacja : grupa) {
				Odpowiedz odpowiedz = gra.dajOdpowiedz(proba, kombinacja);
				rozklad.put(odpowiedz, rozklad.getOrDefault(odpowiedz, 0) + 1);
			}

			// Znajdź największą grupę po tym ruchu (najgorszy scenariusz)
			int maxGrupa = rozklad.values().stream().mapToInt(Integer::intValue).max().orElse(1);

			if (maxGrupa < najlepszyWynik) {
				najlepszyWynik = maxGrupa;
			}
		}

		return najlepszyWynik;
	}

	public void zaktualizujPoOdpowiedzi(int[] proba, Odpowiedz odpowiedz) {
		mozliweKombinacje = gra.filtrujKombinacje(mozliweKombinacje, proba, odpowiedz);
	}
}