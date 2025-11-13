package tramwaje;

import java.util.ArrayList;

abstract class Zajezdnia {
	protected String nazwa;
	protected ArrayList<PojazdKomunikacjiMiejskiej> pojazdy;

	public Zajezdnia(String nazwa) {
		this.nazwa = nazwa;
		this.pojazdy = new ArrayList<PojazdKomunikacjiMiejskiej>();
	}

	public String getNazwa() {
		return nazwa;
	}

	public ArrayList<PojazdKomunikacjiMiejskiej> getPojazdy() {
		return new ArrayList<PojazdKomunikacjiMiejskiej>(pojazdy); // kopia
	}

	public void dodajPojazd(PojazdKomunikacjiMiejskiej pojazd) {
		// Sprawdzanie typu pojazdu
		if (!czyMozeDodacPojazd(pojazd)) {
			throw new IllegalArgumentException("Nieprawidłowy typ pojazdu dla tej zajezdni");
		}

		// Sprawdzanie czy pojazd już należy do jakiejś zajezdni
		if (pojazd.getZajezdnia() != null) {
			throw new IllegalArgumentException("Pojazd " + pojazd.getNumer() +
					" już należy do zajezdni: " + pojazd.getZajezdnia().getNazwa());
		}

		pojazdy.add(pojazd);
		pojazd.setZajezdnia(this);
	}

	public void usunPojazd(PojazdKomunikacjiMiejskiej pojazd) {
		pojazdy.remove(pojazd);
		pojazd.setZajezdnia(null);
	}

	protected abstract boolean czyMozeDodacPojazd(PojazdKomunikacjiMiejskiej pojazd);

	public abstract String getTypZajezdni();

	@Override
	public String toString() {
		StringBuilder opis = new StringBuilder();
		opis.append("Zajezdnia: ").append(nazwa);
		opis.append(" (").append(getTypZajezdni()).append(")");
		opis.append("\nLiczba pojazdów: ").append(pojazdy.size());
		opis.append("\nPojazdy:");

		if (pojazdy.isEmpty()) {
			opis.append("\n  Brak pojazdów");
		} else {
			for (PojazdKomunikacjiMiejskiej pojazd : pojazdy) {
				opis.append("\n  - ").append(pojazd.toString());
			}
		}

		return opis.toString();
	}
}