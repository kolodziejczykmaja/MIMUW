package mastermind;

import java.util.Objects;

public class Odpowiedz {
	private int czarne; // dokładne trafienia
	private int biale;  // trafienia koloru ale zła pozycja

	public Odpowiedz(int czarne, int biale) {
		this.czarne = czarne;
		this.biale = biale;
	}

	public int getCzarne() {
		return czarne;
	}

	public int getBiale() {
		return biale;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Odpowiedz)) return false;
		Odpowiedz inna = (Odpowiedz) obj;
		return czarne == inna.czarne && biale == inna.biale;
	}

	@Override
	public int hashCode() {
		return Objects.hash(czarne, biale);
	}

	@Override
	public String toString() {
		return "Czarne:" + czarne + " Białe:" + biale;
	}

	public boolean czyWygrana(int dlugoscHasla) {
		return czarne == dlugoscHasla;
	}
}