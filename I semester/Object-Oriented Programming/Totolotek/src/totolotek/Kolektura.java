package totolotek;

import java.util.*;

public class Kolektura {
	private static final int LICZBA_LOSOWANYCH = 6;
	private static final int MIN_LICZBA = 1;
	private static final int MAX_LICZBA = 49;
	private static final long WYSOKA_WYGRANA = 2280_00;
	private static final double WYSOKA_WYGRANA_PROCENT_PODATKU = 0.1;
	private static final double WYSOKA_WYGRANA_PROCENT_DLA_GRACZA = 0.9;
	private final static int MAX_ILOSC_LOSOWAN = 10;
	private final static int MIN_ILOSC_LOSOWAN = 1;
	private final static int MAX_ILOSC_ZAKLADOW = 8;
	private final static int MIN_ILOSC_ZAKLADOW = 1;

	private static int ostatniNumer = 0;

	private final int numerKolektury;
	private final List<Kupon> sprzedaneKupony = new ArrayList<>();
	private final CentralaTotolotka centrala;

	public Kolektura(CentralaTotolotka centrala) {
		this.numerKolektury = ++ostatniNumer;
		this.centrala = centrala;
		centrala.dodajKolekture( this);
	}

	public int getNumerKolektury() {
		return numerKolektury;
	}

	public CentralaTotolotka getCentrala() {
		return centrala;
	}

	public List<Kupon> getKupony() {
		return sprzedaneKupony;
	}

	public Kupon generujKuponNaChybilTrafil(int liczbaZakladow, int[] liczbaLosowan) {
		if (liczbaZakladow < MIN_ILOSC_ZAKLADOW || liczbaZakladow > MAX_ILOSC_ZAKLADOW) {
			throw new IllegalArgumentException("Liczba zakładów musi być między 1 a 8");
		}
		if (liczbaLosowan == null) {
			throw new IllegalArgumentException("Nie podano liczby losowań");
		}

		int[][] liczby = new int[liczbaZakladow][];
		for (int i = 0; i < liczbaZakladow; i++) {
			liczby[i] = generujLiczbyNaChybilTrafil();
		}

		Kupon kupon = new Kupon(this, liczby, liczbaLosowan);
		sprzedaneKupony.add(kupon);
		centrala.dodajSrodki(kupon.getCenaNetto());
		BudzetPanstwa.dodajPodatek(kupon.getPodatek());
		centrala.zarejestrujZaklady(kupon.getNumeryLosowan(), kupon.getIleZakladow());
		return kupon;
	}

	private int[] generujLiczbyNaChybilTrafil() {
		List<Integer> liczby = new ArrayList<>();
		for (int i = MIN_LICZBA; i <= MAX_LICZBA; i++) {
			liczby.add(i);
		}
		Collections.shuffle(liczby);
		int[] wynik = new int[LICZBA_LOSOWANYCH];
		for (int i = 0; i < LICZBA_LOSOWANYCH; i++) {
			wynik[i] = liczby.get(i);
		}
		return wynik;
	}

	public Kupon generujKuponZBlankietu(Blankiet blankiet) {
		// Walidacja blankietu
		if (blankiet == null) {
			throw new IllegalArgumentException("Blankiet nie może być null");
		}

		// Pobierz prawidłowe zakłady z blankietu
		List<Set<Integer>> prawidloweZaklady = blankiet.getPrawidloweZaklady();

		if (prawidloweZaklady.isEmpty()) {
			throw new IllegalArgumentException("Blankiet nie zawiera żadnych prawidłowych zakładów (z dokładnie 6 liczbami)");
		}

		if (prawidloweZaklady.size() > MAX_ILOSC_ZAKLADOW) {
			throw new IllegalArgumentException("Za dużo prawidłowych zakładów na blankiecie (maksymalnie 8)");
		}

		// Pobierz zaznaczone losowania z blankietu
		int[] zaznaczoneLosowania = blankiet.getZaznaczoneLosowania();

		if (zaznaczoneLosowania.length == 0) {
			throw new IllegalArgumentException("Nie zaznaczono żadnych losowań na blankiecie");
		}

		// Sprawdź czy zaznaczone losowania są w prawidłowym zakresie
		for (int losowanie : zaznaczoneLosowania) {
			if (losowanie < MIN_ILOSC_LOSOWAN || losowanie > MAX_ILOSC_LOSOWAN) {
				throw new IllegalArgumentException("Liczba losowań " + losowanie + " jest poza zakresem 1-10");
			}
		}

		// Konwertuj zakłady na format wymagany przez konstruktor Kupon
		int[][] liczby = new int[prawidloweZaklady.size()][];
		for (int i = 0; i < prawidloweZaklady.size(); i++) {
			Set<Integer> zaklad = prawidloweZaklady.get(i);

			// Dodatkowa walidacja - sprawdź czy zakład ma dokładnie 6 liczb
			if (zaklad.size() != LICZBA_LOSOWANYCH) {
				throw new IllegalArgumentException("Zakład " + (i + 1) + " ma " + zaklad.size() + " liczb zamiast 6");
			}

			// Sprawdź czy wszystkie liczby są w prawidłowym zakresie
			for (int liczba : zaklad) {
				if (liczba < MIN_LICZBA || liczba > MAX_LICZBA) {
					throw new IllegalArgumentException("Liczba " + liczba + " w zakładzie " + (i + 1) + " jest poza zakresem 1-49");
				}
			}

			// Konwertuj Set na tablicę int[]
			liczby[i] = zaklad.stream().mapToInt(Integer::intValue).toArray();
			Arrays.sort(liczby[i]); // Posortuj liczby w zakładzie
		}

		// Stwórz kupon
		Kupon kupon = new Kupon(this, liczby, zaznaczoneLosowania);

		// Dodaj kupon do listy sprzedanych kuponów
		sprzedaneKupony.add(kupon);

		// Przekaż środki do centrali i budżetu
		centrala.dodajSrodki(kupon.getCenaNetto());
		BudzetPanstwa.dodajPodatek(kupon.getPodatek());

		// Zarejestruj zakłady w centrali
		centrala.zarejestrujZaklady(kupon.getNumeryLosowan(), kupon.getIleZakladow());

		return kupon;
	}

	public void odebranieWygranej(Gracz gracz, Kupon kupon) {
		// Sprawdź czy kupon został sprzedany w tej kolekturze
		if (!sprzedaneKupony.contains(kupon)) {
			throw new IllegalArgumentException("Ten kupon nie został sprzedany w tej kolekturze");
		}

		// Sprawdź czy kupon nie został już zrealizowany
		if (kupon.czyZrealizowany()) {
			throw new IllegalArgumentException("Ten kupon został już zrealizowany");
		}

		// Oblicz wygraną
		Map<Integer, ArrayList<Long>> wygrane =
				centrala.obliczWygrane(kupon);
		long kwotaWygranejGracza = 0;
		long podatek = 0;

		for (int losowanie : wygrane.keySet()) {
			for (long wygrana : wygrane.get(losowanie)) {
				if (czyWysokieWygrane(wygrana)) {
					kwotaWygranejGracza += (long) (wygrana * WYSOKA_WYGRANA_PROCENT_DLA_GRACZA);
					podatek += (long) (wygrana * WYSOKA_WYGRANA_PROCENT_PODATKU);
				} else {
					kwotaWygranejGracza += wygrana;
				}
			}
		}

		// Wypłać nagrodę (pobierz środki z centrali)
		centrala.wyplacNagrode(kwotaWygranejGracza + podatek);

		// Przekaż pieniądze dla gracza
		gracz.wplac(kwotaWygranejGracza);

		// Przekaż podatek do budżetu
		BudzetPanstwa.dodajPodatek(podatek);

		// Oznacz kupon jako zrealizowany
		kupon.oznaczJakoZrealizowany();
	}

	private boolean czyWysokieWygrane(long wygrana) {
		return wygrana >= WYSOKA_WYGRANA;
	}
}