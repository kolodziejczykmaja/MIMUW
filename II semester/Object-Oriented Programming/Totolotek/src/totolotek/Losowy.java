package totolotek;

import java.util.concurrent.ThreadLocalRandom;

public class Losowy extends Gracz {
	private final static long MAX_SRODKI_GRACZA = 100_000_000L;
	private final static int MAX_ILOSC_KUPONOW = 100;
	private final static int MAX_ILOSC_ZAKLADOW = 8;
	private final static int MAX_ILOSC_LOSOWAN = 10;

	private final Kolektura losowaKolektura;

	public Losowy(String imie, String nazwisko, String pesel,
				  Kolektura losowaKolektura) {
		super(imie, nazwisko, pesel, ThreadLocalRandom.current().nextLong(1,
				MAX_SRODKI_GRACZA + 1));
		this.losowaKolektura = losowaKolektura;
	}

	private int losujIloscKuponow() {
		return ThreadLocalRandom.current().nextInt(1, MAX_ILOSC_KUPONOW);
	}

	private int losujIloscZakladow() {
		return ThreadLocalRandom.current().nextInt(1, MAX_ILOSC_ZAKLADOW + 1);
	}

	private int losujIloscLosowan() {
		return ThreadLocalRandom.current().nextInt(1, MAX_ILOSC_LOSOWAN + 1);
	}

	public void kupKupon() {
		int iloscKuponow = losujIloscKuponow();
		for (int i = 0; i < iloscKuponow; i++) {
			int iloscZakladow = losujIloscZakladow();
			int[] liczbaLosowan = new int[] {losujIloscLosowan()};
			if (!czyMoznaKupicKupon(CENA_ZAKLADU * iloscZakladow)) {
				return;
			} else {
				Kupon kupon =
						losowaKolektura.generujKuponNaChybilTrafil(iloscZakladow,
								liczbaLosowan);
				pobierzSrodki(CENA_ZAKLADU * iloscZakladow);
				dodajKupon(kupon);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + "\nTyp gracza: Losowy" ;
	}
}