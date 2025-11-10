package totolotek;

import java.util.*;

public class PrezentacjaKlasWDzialaniu {

	public static void main(String[] args) {

		CentralaTotolotka centrala = new CentralaTotolotka(10_000_000_00L);
		// 10 mln zł

		// Tworzenie 10 kolektur
		List<Kolektura> kolektury = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Kolektura kolektura = new Kolektura(centrala);
			kolektury.add(kolektura);
		}

		List<Gracz> wszyscyGracze = new ArrayList<>();

		// 200 Minimalistów
		List<Gracz> minimalisci = stworzMinimalistow(kolektury);
		wszyscyGracze.addAll(minimalisci);

		// 200 Losowych
		List<Gracz> losowi = stworzLosowych(kolektury);
		wszyscyGracze.addAll(losowi);

		// 200 Stałoliczbowych
		List<Gracz> staloliczbowi = stworzStaloliczbowych(kolektury);
		wszyscyGracze.addAll(staloliczbowi);

		// 200 Stałobankietowych
		List<Gracz> stalobankietowi = stworzStalobankietowych(kolektury);
		wszyscyGracze.addAll(stalobankietowi);

		for (int numerLosowania = 1; numerLosowania <= 20; numerLosowania++) {


			for (Gracz gracz : wszyscyGracze) {
				gracz.kupKupon();
			}
			centrala.przeprowadzLosowanie();

			for (Gracz gracz : wszyscyGracze) {
				gracz.sprawdzIOdbierzWygrane();
			}
		}
		System.out.println(centrala);
		System.out.println(BudzetPanstwa.getStan());
	}

	private static List<Gracz> stworzMinimalistow(List<Kolektura> kolektury) {
		List<Gracz> minimalisci = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 200; i++) {
			// Przydziel równomiernie do kolektur
			Kolektura kolektura = kolektury.get(i % kolektury.size());

			// Losowe środki od 1000 do 50000 zł
			long srodki = (1000 + random.nextInt(49000)) * 100L;

			String imie = "Minimalista" + (i + 1);
			String nazwisko = "Kowalski";
			String pesel = String.format("80%08d", 10000000 + i);

			minimalisci.add(new Minimalista(imie, nazwisko, pesel, srodki, kolektura));
		}

		return minimalisci;
	}

	private static List<Gracz> stworzLosowych(List<Kolektura> kolektury) {
		List<Gracz> losowi = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 200; i++) {
			// Przydziel równomiernie do kolektur
			Kolektura kolektura = kolektury.get(i % kolektury.size());

			String imie = "Losowy" + (i + 1);
			String nazwisko = "Nowak";
			String pesel = String.format("85%08d", 20000000 + i);

			losowi.add(new Losowy(imie, nazwisko, pesel, kolektura));
		}

		return losowi;
	}

	private static List<Gracz> stworzStaloliczbowych(List<Kolektura> kolektury) {
		List<Gracz> staloliczbowi = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 200; i++) {
			// Losowe ulubione liczby
			Set<Integer> liczbySet = new HashSet<>();
			while (liczbySet.size() < 6) {
				liczbySet.add(1 + random.nextInt(49));
			}
			int[] ulubioneLiczby = liczbySet.stream().mapToInt(Integer::intValue).toArray();

			// Przydziel 2-4 ulubione kolektury
			int liczbaKolektur = 2 + random.nextInt(3); // 2-4 kolektury
			List<Kolektura> ulubioneKolektury = new ArrayList<>();
			Set<Integer> wybrane = new HashSet<>();

			while (ulubioneKolektury.size() < liczbaKolektur) {
				int indeks = random.nextInt(kolektury.size());
				if (wybrane.add(indeks)) {
					ulubioneKolektury.add(kolektury.get(indeks));
				}
			}

			// Losowe środki od 5000 do 100000 zł
			long srodki = (5000 + random.nextInt(95000)) * 100L;

			String imie = "Stały" + (i + 1);
			String nazwisko = "Wiśniewski";
			String pesel = String.format("90%08d", 30000000 + i);

			staloliczbowi.add(new Staloliczbowy(imie, nazwisko, pesel, srodki,
					ulubioneLiczby, ulubioneKolektury));
		}

		return staloliczbowi;
	}

	private static List<Gracz> stworzStalobankietowych(List<Kolektura> kolektury) {
		List<Gracz> stalobankietowi = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 200; i++) {
			// Stwórz losowy blankiet (2-5 zakładów)
			int liczbaZakladow = 2 + random.nextInt(4);
			List<List<Integer>> zaklady = new ArrayList<>();

			for (int j = 0; j < liczbaZakladow; j++) {
				Set<Integer> liczbySet = new HashSet<>();
				while (liczbySet.size() < 6) {
					liczbySet.add(1 + random.nextInt(49));
				}
				zaklady.add(new ArrayList<>(liczbySet));
			}

			// Losowa liczba losowań (1-5)
			int liczbaLosowan = 1 + random.nextInt(5);
			int[] liczbyLosowan = {liczbaLosowan};

			Blankiet blankiet = new Blankiet(zaklady, liczbyLosowan, null);

			// Przydziel 2-3 ulubione kolektury
			int liczbaKolektur = 2 + random.nextInt(2); // 2-3 kolektury
			List<Kolektura> ulubioneKolektury = new ArrayList<>();
			Set<Integer> wybrane = new HashSet<>();

			while (ulubioneKolektury.size() < liczbaKolektur) {
				int indeks = random.nextInt(kolektury.size());
				if (wybrane.add(indeks)) {
					ulubioneKolektury.add(kolektury.get(indeks));
				}
			}

			// Losowa częstotliwość kupowania (2-8 losowań)
			int coIleLosowanKupuje = 2 + random.nextInt(7);

			// Losowe środki od 10000 do 200000 zł
			long srodki = (10000 + random.nextInt(190000)) * 100L;

			String imie = "StaloBlankietowy" + (i + 1);
			String nazwisko = "Lewandowski";
			String pesel = String.format("95%08d", 40000000 + i);

			stalobankietowi.add(new Stalobankietowy(imie, nazwisko, pesel, srodki,
					blankiet, ulubioneKolektury, coIleLosowanKupuje));
		}

		return stalobankietowi;
	}

	private static String formatujKwote(long kwotaWGroszach) {
		return String.format("%,d zł %02d gr", kwotaWGroszach / 100, kwotaWGroszach % 100);
	}
}