package totolotek;

import java.util.*;

public class Stalobankietowy extends Gracz {
	private final static int MAX_ILOSC_ZAKLADOW = 8;
	private final static int MIN_CZESTOTLIWOSC = 1;
	private final static int DOMYSLNA_ILOSC_LOSOWAN = 1;

	private final Blankiet ulubionyBlankiet; // stały blankiet do wypełniania
	private final List<Kolektura> ulubioneKolektury; // lista ulubionych kolektur
	private final int coIleLosowanKupuje; // co ile losowań kupuje nowy kupon
	private int aktualnaKolekturaIndex; // indeks aktualnie używanej kolektury
	private Kupon ostatniKupon; // ostatnio kupiony kupon

	public Stalobankietowy(String imie, String nazwisko, String pesel, long srodki,
						   Blankiet ulubionyBlankiet, List<Kolektura> ulubioneKolektury,
						   int coIleLosowanKupuje) {
		super(imie, nazwisko, pesel, srodki);

		// Walidacja blankietu
		if (ulubionyBlankiet == null) {
			throw new IllegalArgumentException("Ulubiony blankiet nie może być null");
		}

		// Sprawdź czy blankiet ma przynajmniej jeden prawidłowy zakład
		if (ulubionyBlankiet.getPrawidloweZaklady().isEmpty()) {
			throw new IllegalArgumentException("Ulubiony blankiet musi mieć przynajmniej jeden prawidłowy zakład");
		}

		// Sprawdź czy blankiet nie ma za dużo zakładów
		if (ulubionyBlankiet.getPrawidloweZaklady().size() > MAX_ILOSC_ZAKLADOW) {
			throw new IllegalArgumentException("Ulubiony blankiet nie może mieć więcej niż 8 zakładów");
		}

		// Walidacja kolektur
		if (ulubioneKolektury == null || ulubioneKolektury.isEmpty()) {
			throw new IllegalArgumentException("Gracz stałobankietowy musi mieć przynajmniej jedną ulubioną kolekturę");
		}

		// Walidacja częstotliwości kupowania
		if (coIleLosowanKupuje < MIN_CZESTOTLIWOSC) {
			throw new IllegalArgumentException("Częstotliwość kupowania musi być większa niż 0");
		}

		this.ulubionyBlankiet = ulubionyBlankiet;
		this.ulubioneKolektury = new ArrayList<>(ulubioneKolektury);
		this.coIleLosowanKupuje = coIleLosowanKupuje;
		this.aktualnaKolekturaIndex = 0;
		this.ostatniKupon = null;
	}

	@Override
	public void kupKupon() {
		// Sprawdź czy można kupić nowy kupon
		if (!moznaKupicNowyKupon()) {
			return; // Nie kupuj jeszcze, nie nadszedł jeszcze czas
		}

		// Oblicz koszt kuponu
		int liczbaZakladow = ulubionyBlankiet.getPrawidloweZaklady().size();
		int liczbaLosowan = ulubionyBlankiet.getZaznaczoneLosowania().length;
		if (liczbaLosowan == 0) {
			liczbaLosowan = DOMYSLNA_ILOSC_LOSOWAN; // Domyślnie jedno losowanie
		}

		long koszt = CENA_ZAKLADU * liczbaZakladow * liczbaLosowan;

		if (!czyMoznaKupicKupon(koszt)) {
			return;
		}

		// Wybierz aktualną ulubioną kolekturę
		Kolektura aktualnaKolektura = ulubioneKolektury.get(aktualnaKolekturaIndex);

		// Generuj kupon na podstawie ulubionego blankietu
		Kupon kupon = aktualnaKolektura.generujKuponZBlankietu(ulubionyBlankiet);

		// Zapłać za kupon
		pobierzSrodki(kupon.getCenaBrutto());
		dodajKupon(kupon);
		ostatniKupon = kupon;

		// Przejdź do następnej ulubionej kolektury na kolejny raz
		aktualnaKolekturaIndex = (aktualnaKolekturaIndex + 1) % ulubioneKolektury.size();
	}

	 // Sprawdza czy gracz może kupić nowy kupon.
	private boolean moznaKupicNowyKupon() {
		if (ostatniKupon == null) {
			return true; // Pierwszy kupon - można kupić od razu
		}

		// Pobierz numer losowania, w którym kupiono ostatni kupon
		int numerPierwszegoLosowaniaOstatniegoKuponu =
				ostatniKupon.getNumeryLosowan().getFirst();

		// Pobierz aktualną liczbę przeprowadzonych losowań z kolektury
		int aktualneLosowanie = ostatniKupon.getKolektura().getCentrala().getLiczbaLosowan();

		// Sprawdź czy minęło wystarczająco dużo losowań
		int roznicaLosowan = aktualneLosowanie - numerPierwszegoLosowaniaOstatniegoKuponu + 1;

		return roznicaLosowan >= coIleLosowanKupuje;
	}

	public Blankiet getUlubionyBlankiet() {
		return ulubionyBlankiet;
	}

	public List<Kolektura> getUlubioneKolektury() {
		return new ArrayList<>(ulubioneKolektury);
	}

	public int getCoIleLosowanKupuje() {
		return coIleLosowanKupuje;
	}

	public int getAktualnaKolekturaIndex() {
		return aktualnaKolekturaIndex;
	}

	public Kolektura getAktualnaKolektura() {
		return ulubioneKolektury.get(aktualnaKolekturaIndex);
	}

	public Kupon getOstatniKupon() {
		return ostatniKupon;
	}

	public String getInfoOBlankiecie() {
		StringBuilder sb = new StringBuilder();
		sb.append("Zakłady: ").append(ulubionyBlankiet.getPrawidloweZaklady().size());
		sb.append(", Losowania: ");

		int[] losowania = ulubionyBlankiet.getZaznaczoneLosowania();
		if (losowania.length == 0) {
			sb.append("1 (domyślnie)");
		} else {
			sb.append(Arrays.toString(losowania));
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("\nTyp gracza: Stałobankietowy");
		sb.append("\nUlubiony blankiet: ").append(getInfoOBlankiecie());
		sb.append("\nLiczba ulubionych kolektur: ").append(ulubioneKolektury.size());
		sb.append("\nKupuje kupon co ").append(coIleLosowanKupuje).append(" losowań");

		if (ostatniKupon != null) {
			sb.append("\nOstatni kupon: ").append(ostatniKupon.getIdentyfikator());

			// Pokaż kiedy można kupić następny kupon
			int numerPierwszegoLosowaniaOstatniegoKuponu =
					ostatniKupon.getNumeryLosowan().getFirst();
			int aktualneLosowanie = ostatniKupon.getKolektura().getCentrala().getLiczbaLosowan();
			int roznicaLosowan = aktualneLosowanie - numerPierwszegoLosowaniaOstatniegoKuponu + 1;
			int pozostaleDoKolnegoZakupu = Math.max(0, coIleLosowanKupuje - roznicaLosowan);

			if (pozostaleDoKolnegoZakupu == 0) {
				sb.append(" (można kupić następny kupon)");
			} else {
				sb.append(" (następny kupon za ").append(pozostaleDoKolnegoZakupu).append(" losowań)");
			}
		}

		return sb.toString();
	}
}