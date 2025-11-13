package totolotek;

import java.util.*;
import java.util.stream.Collectors;

public class Kupon {
	private static final int PRAWIDLOWA_ILOSC_LICZB_W_ZAKLADZIE = 6;
	private final static int MAX_ILOSC_LOSOWAN = 10;
	private final static int MIN_ILOSC_LOSOWAN = 1;
	private final static int DOMYSLNA_ILOSC_LOSOWAN = 1;
	private final static int MAX_ILOSC_ZAKLADOW = 8;
	private final static int MIN_ILOSC_ZAKLADOW = 1;
	private final static long CENA_ZAKLADU = 3_00L;
	private final static long CENA_PODATKU = 60L;


	private static int ostatniNumer = 0;
	private final String identyfikator;
	private final List<Zaklad> zaklady;
	private final List<Integer> numeryLosowan;
	private final int ileLosowan;
	private final Kolektura kolektura;
	private final long cenaBrutto;
	private final long cenaNetto;
	private final long podatek;
	private boolean zrealizowany = false;

	public Kupon(Kolektura kolektura, int[][] liczby, int[] liczbaLosowan) {
		if (kolektura == null || liczby == null || liczby[0] == null || liczbaLosowan == null) {
			throw new IllegalArgumentException("Nieprawidłowe argumenty dla " +
					"kuponu");
		}
		if (liczby.length < MIN_ILOSC_ZAKLADOW || liczby.length > MAX_ILOSC_ZAKLADOW) {
			throw new IllegalArgumentException("Liczba zakładów musi być " +
					"pomiędzy 1 a 8");
		}
		if (liczbaLosowan.length == 0) {
			ileLosowan = DOMYSLNA_ILOSC_LOSOWAN;
		} else {
			ileLosowan = Arrays.stream(liczbaLosowan).max().getAsInt();
			if (ileLosowan < MIN_ILOSC_LOSOWAN || ileLosowan > MAX_ILOSC_LOSOWAN) {
				throw new IllegalArgumentException("Liczba losowań musi być " +
						"między 1 a 10");
			}
		}

		this.zaklady = generujPoprawneZaklady(liczby.length, liczby);
		this.numeryLosowan = generujNumeryLosowan(ileLosowan, kolektura.getCentrala());
		this.kolektura = kolektura;
		this.cenaBrutto = liczby.length * ileLosowan * CENA_ZAKLADU; // 3 zł w groszach
		this.podatek = liczby.length * ileLosowan * CENA_PODATKU; // 0.60 zł w groszach
		this.cenaNetto = cenaBrutto - podatek;

		// Generowanie identyfikatora
		ostatniNumer++;
		String znacznikLosowy = generujZnacznikLosowy();
		this.identyfikator = generujIdentyfikator(ostatniNumer, kolektura.getNumerKolektury(),
				znacznikLosowy);
	}

	private ArrayList<Zaklad> generujPoprawneZaklady(int liczbaZakladow, int liczby[][]) {
		ArrayList<Zaklad> poprawneZaklady = new ArrayList<>();
		for (int i = 0; i < liczbaZakladow; i++) {
			if (liczby[i].length == PRAWIDLOWA_ILOSC_LICZB_W_ZAKLADZIE) {
				poprawneZaklady.add(new Zaklad(liczby[i]));
			}
		}
		return poprawneZaklady;
	}

	private List<Integer> generujNumeryLosowan(int liczbaLosowan, CentralaTotolotka centrala) {
		int numerNajblizszegoLosowania =
				(int)(centrala.getLiczbaLosowan() + 1);
		List<Integer> numery = new ArrayList<>();
		for (int i = 0; i < liczbaLosowan; i++) {
			numery.add(numerNajblizszegoLosowania + i);
		}
		return numery;
	}

	private String generujZnacznikLosowy() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	private String generujIdentyfikator(int numerKuponu, int numerKolektury, String znacznikLosowy) {
		int suma = obliczSumeKontrolna(numerKuponu, numerKolektury, znacznikLosowy);
		return String.format("%d-%d-%s-%02d", numerKuponu, numerKolektury, znacznikLosowy, suma);
	}

	private int obliczSumeKontrolna(int numerKuponu, int numerKolektury, String znacznikLosowy) {
		int suma = 0;
		suma += sumujCyfry(numerKuponu);
		suma += sumujCyfry(numerKolektury);
		for (char c : znacznikLosowy.toCharArray()) {
			suma += Character.getNumericValue(c);
		}
		return suma % 100;
	}

	private int sumujCyfry(int liczba) {
		int suma = 0;
		while (liczba > 0) {
			suma += liczba % 10;
			liczba /= 10;
		}
		return suma;
	}

	public long getCenaBrutto() {
		return cenaBrutto;
	}

	public long getCenaNetto() {
		return cenaNetto;
	}

	public long getPodatek() {
		return podatek;
	}

	public String getIdentyfikator() {
		return identyfikator;
	}

	public Kolektura getKolektura() {
		return kolektura;
	}

	public boolean czyZrealizowany() {
		return zrealizowany;
	}

	public void oznaczJakoZrealizowany() {
		this.zrealizowany = true;
	}

	public ArrayList<Integer> getNumeryLosowan() {
		return new ArrayList<>(numeryLosowan);
	}

	public ArrayList<Zaklad> getZaklady() {
		return new ArrayList<>(zaklady);
	}

	public int getIleZakladow() {
		return zaklady.size();
	}

	// Sprawdzamy czy numer ostatniego losowania jest <= niz
	// ostatnieClosowanie w centrali
	public boolean czyWykonaneWszystkieLosowania() {
		return numeryLosowan.getLast() <= kolektura.getCentrala().getLiczbaLosowan();
	}

		@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("KUPON NR ").append(identyfikator).append("\n");

		// Wypisanie zakładów
		for (int i = 0; i < zaklady.size(); i++) {
			sb.append(i+1).append(":");
			for (int liczba : zaklady.get(i).getLiczby()) {
				sb.append(String.format("%3d", liczba));
			}
			sb.append("\n");
		}

		sb.append("LICZBA LOSOWAŃ: ").append(ileLosowan).append("\n");
		sb.append("NUMERY LOSOWAŃ:\n");
		sb.append(" ").append(getNumeryLosowan().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(" ")));
		sb.append("\nCENA: ").append(cenaBrutto / 100).append(" zł ");
		sb.append(String.format("%02d", cenaBrutto % 100)).append(" gr");

		return sb.toString();
	}
}