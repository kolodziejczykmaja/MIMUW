package totolotek;

import java.util.*;

public abstract class Gracz {
	protected static final long CENA_ZAKLADU = 3_00; // w groszach (3zł)
	protected String imie;
	protected String nazwisko;
	protected String pesel;
	protected long srodki;
	protected List<Kupon> kupony;

	public Gracz(String imie, String nazwisko, String pesel, long srodki) {
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.pesel = pesel;
		this.srodki = srodki;
		this.kupony = new ArrayList<>();
	}

	public abstract void kupKupon();

	public void sprawdzIOdbierzWygrane() {
		for (Kupon kupon : kupony) {
			// Sprawdź czy kupon ma wszystkie losowania zakończone
			if (kupon.czyWykonaneWszystkieLosowania()) {
				// Znajdź kolekturę, w której kupiono kupon
				Kolektura kolektura = kupon.getKolektura();
				// Odbierz wygraną
				kolektura.odebranieWygranej(this, kupon);
			}
		}
		// Usuń zrealizowane kupony
		kupony.removeIf(kupon -> kupon.czyZrealizowany());
	}

	public void wplac(long kwota) {
		srodki += kwota;
	}

	// Dodaje kupon do listy (używane przez kolekturę)
	public void dodajKupon(Kupon kupon) {
		kupony.add(kupon);
	}

	// Sprawdza czy gracz ma wystarczające środki
	public boolean czyMoznaKupicKupon(long cena) {
		return srodki >= cena;
	}

	// Pobiera środki (używane przy zakupie kuponu)
	public void pobierzSrodki(long kwota) {
		srodki -= kwota;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Gracz: ").append(imie).append(" ").append(nazwisko)
				.append("\nPESEL: ").append(pesel)
				.append("\nŚrodki: ").append(formatujKwote(srodki))
				.append("\nLiczba kuponów: ").append(kupony.size());

		if (kupony.isEmpty()) {
			sb.append("\nBrak kuponów");
		} else {
			sb.append("\nIdentyfikatory kuponów:");
			for (Kupon kupon : kupony) {
				sb.append("\n- ").append(kupon.getIdentyfikator());
			}
		}

		return sb.toString();
	}

	// Metoda pomocnicza do formatowania kwot
	private String formatujKwote(long kwotaWGroszach) {
		long zlote = kwotaWGroszach / 100;
		long grosze = kwotaWGroszach % 100;
		return String.format("%d zł %02d gr", zlote, grosze);
	}
}