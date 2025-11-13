package symulatorFarmera;

import java.util.*;

public class Cennik {
	private Map<RodzajDaru, Integer> ceny;
	private Random random;

	public Cennik() {
		this.ceny = new HashMap<>();
		this.random = new Random();
		// Inicjalizuj wszystkie rodzaje darów
		for (RodzajDaru rodzaj : RodzajDaru.values()) {
			ceny.put(rodzaj, 0);
		}
	}


	//Aktualizuje ceny wszystkich rodzajów darów (losowo 0-10)
	public void aktualizuj() {
		for (RodzajDaru rodzaj : RodzajDaru.values()) {
			ceny.put(rodzaj, random.nextInt(11)); // 0-10
		}
	}

	//Wycenia dar na podstawie aktualnego cennika
	public int wycenDar(Dar dar) {
		return dar.getIloscDaru() * getCena(dar.getRodzajDaru());
	}

	public int getCena(RodzajDaru rodzaj) {
		return ceny.getOrDefault(rodzaj, 0);
	}

	@Override
	public String toString() {
		return "Cennik: " + ceny.toString();
	}
}