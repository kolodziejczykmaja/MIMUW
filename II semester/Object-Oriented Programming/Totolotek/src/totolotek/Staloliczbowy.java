package totolotek;

import java.util.*;

public class Staloliczbowy extends Gracz {
	private final static int LICZBA_LOSOWAN_GRACZA_LOSOWEGO = 10;
	private final static int LICZBA_ULUBIONYCH_LICZB = 6;
	private final static int MAX_WARTOSC_LICZBY = 49;
	private final static int MIN_WARTOSC_LICZBY = 1;

	private final int[] ulubioneLiczby; // 6 ulubionych liczb
	private final List<Kolektura> ulubioneKolektury; // lista ulubionych kolektur
	private int aktualnaKolekturaIndex; // indeks aktualnie używanej kolektury
	private Kupon ostatniKupon; // ostatnio kupiony kupon

	public Staloliczbowy(String imie, String nazwisko, String pesel, long srodki,
						 int[] ulubioneLiczby, List<Kolektura> ulubioneKolektury) {
		super(imie, nazwisko, pesel, srodki);

		// Walidacja ulubionych liczb
		if (ulubioneLiczby == null || ulubioneLiczby.length != LICZBA_ULUBIONYCH_LICZB) {
			throw new IllegalArgumentException("Gracz stałoliczbowy musi mieć dokładnie 6 ulubionych liczb");
		}

		// Sprawdź czy liczby są w prawidłowym zakresie (1-49) i czy są unikalne
		Set<Integer> sprawdzUnikalne = new HashSet<>();
		for (int liczba : ulubioneLiczby) {
			if (liczba < MIN_WARTOSC_LICZBY || liczba > MAX_WARTOSC_LICZBY) {
				throw new IllegalArgumentException("Ulubione liczby muszą być z zakresu 1-49");
			}
			if (!sprawdzUnikalne.add(liczba)) {
				throw new IllegalArgumentException("Ulubione liczby muszą być unikalne");
			}
		}

		// Walidacja kolektur
		if (ulubioneKolektury == null || ulubioneKolektury.isEmpty()) {
			throw new IllegalArgumentException("Gracz stałoliczbowy musi mieć przynajmniej jedną ulubioną kolekturę");
		}

		this.ulubioneLiczby = Arrays.copyOf(ulubioneLiczby, LICZBA_ULUBIONYCH_LICZB);
		Arrays.sort(this.ulubioneLiczby); // Sortuj dla spójności
		this.ulubioneKolektury = new ArrayList<>(ulubioneKolektury);
		this.aktualnaKolekturaIndex = 0;
		this.ostatniKupon = null;
	}

	@Override
	public void kupKupon() {
		// Sprawdź czy można kupić nowy kupon
		if (!moznaKupicNowyKupon()) {
			return; // Nie kupuj jeszcze, poprzedni kupon nadal aktywny
		}

		// Oblicz koszt kuponu (1 zakład na 10 losowań)
		long koszt = CENA_ZAKLADU * LICZBA_LOSOWAN_GRACZA_LOSOWEGO;

		if (!czyMoznaKupicKupon(koszt)) {
			return;
		}

		// Stwórz blankiet z ulubionymi liczbami
		Blankiet blankiet = stworzBlankietZUlubionymi();

		// Wybierz aktualną ulubioną kolekturę
		Kolektura aktualnaKolektura = ulubioneKolektury.get(aktualnaKolekturaIndex);

		// Generuj kupon na podstawie blankietu w aktualnej kolekturze
		Kupon kupon = aktualnaKolektura.generujKuponZBlankietu(blankiet);

		// Zapłać za kupon
		pobierzSrodki(kupon.getCenaBrutto());
		dodajKupon(kupon);
		ostatniKupon = kupon;

		// Przejdź do następnej ulubionej kolektury na kolejny raz
		aktualnaKolekturaIndex = (aktualnaKolekturaIndex + 1) % ulubioneKolektury.size();
	}

	private boolean moznaKupicNowyKupon() {
		if (ostatniKupon == null) {
			return true; // Pierwszy kupon
		}

		// Sprawdź czy wszystkie losowania z ostatniego kuponu zostały przeprowadzone
		return ostatniKupon.czyWykonaneWszystkieLosowania();
	}

	private Blankiet stworzBlankietZUlubionymi() {
		// Przygotuj listę zakładów - tylko jeden zakład z ulubionymi liczbami
		List<List<Integer>> zaklady = new ArrayList<>();

		// Skonwertuj tablicę ulubionych liczb na listę
		List<Integer> ulubioneLiczbyLista = new ArrayList<>();
		for (int liczba : ulubioneLiczby) {
			ulubioneLiczbyLista.add(liczba);
		}
		zaklady.add(ulubioneLiczbyLista);

		// Ustaw 10 losowań
		int[] liczbyLosowan = {LICZBA_LOSOWAN_GRACZA_LOSOWEGO};

		// Brak anulowanych pól
		List<Boolean> anulowaePola = null;

		return new Blankiet(zaklady, liczbyLosowan, anulowaePola);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("\nTyp gracza: Stałoliczbowy");
		sb.append("\nUlubione liczby: ");
		for (int i = 0; i < ulubioneLiczby.length; i++) {
			if (i > 0) sb.append(", ");
			sb.append(ulubioneLiczby[i]);
		}
		return sb.toString();
	}
}