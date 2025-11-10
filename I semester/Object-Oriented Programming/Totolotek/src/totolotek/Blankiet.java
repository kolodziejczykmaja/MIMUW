package totolotek;

import java.util.*;

public class Blankiet {
	private final static int LICZBA_LICZB_W_ZAKLADZIE = 6;
	private final static int LICZBA_POL = 8;
	private final static int LICZBA_LICZB_W_POLU = 49;
	private final static int DOMYSLNA_LICZBA_LOSOWAN = 1;

	private final static int MIN_POLE = 1;
	private final static int MAX_POLE = 8;
	private final static int MIN_LICZBA = 1;
	private final static int MAX_LICZBA = 49;
	private final static int MIN_LICZBA_LOSOWAN = 1;
	private final static int MAX_LICZBA_LOSOWAN = 10;

	private boolean[][] pola;
	private boolean[] anulowane;
	private boolean[] zaznaczoneLosowania;

	public Blankiet() {
		pola = new boolean[MAX_POLE + 1][MAX_LICZBA + 1];
		anulowane = new boolean[MAX_POLE + 1];
		zaznaczoneLosowania = new boolean[MAX_LICZBA_LOSOWAN + 1];
		zaznaczoneLosowania[DOMYSLNA_LICZBA_LOSOWAN] = true;
	}

	public Blankiet(List<List<Integer>> zaklady, int[] liczbyLosowań, List<Boolean> anulowaePola) {
		pola = new boolean[MAX_POLE + 1][MAX_LICZBA + 1];
		anulowane = new boolean[MAX_POLE + 1];
		zaznaczoneLosowania = new boolean[MAX_LICZBA_LOSOWAN + 1];

		if (liczbyLosowań == null || liczbyLosowań.length == 0) {
			zaznaczoneLosowania[DOMYSLNA_LICZBA_LOSOWAN] = true;
		} else {
			for (int losowanie : liczbyLosowań) {
				if (losowanie < MIN_LICZBA_LOSOWAN || losowanie > MAX_LICZBA_LOSOWAN) {
					throw new IllegalArgumentException("Liczba losowań " + losowanie + " jest poza zakresem 1-10");
				}
				zaznaczoneLosowania[losowanie] = true;
			}
		}

		if (zaklady == null) {
			throw new IllegalArgumentException("Lista zakładów nie może być null");
		}
		if (zaklady.size() > LICZBA_POL) {
			throw new IllegalArgumentException("Maksymalnie 8 zakładów na blankiecie");
		}

		if (anulowaePola != null && anulowaePola.size() > LICZBA_POL) {
			throw new IllegalArgumentException("Lista anulowanych pól nie może być dłuższa niż 8");
		}

		for (int i = 0; i < zaklady.size(); i++) {
			List<Integer> zaklad = zaklady.get(i);
			int numerPola = i + MIN_POLE;

			if (zaklad == null) {
				throw new IllegalArgumentException("Zakład " + (i + 1) + " nie może być null");
			}
			if (zaklad.isEmpty()) {
				continue;
			}

			for (Integer liczba : zaklad) {
				if (liczba == null) {
					throw new IllegalArgumentException("Zakład " + (i + 1) + " zawiera null");
				}
				if (liczba < MIN_LICZBA || liczba > MAX_LICZBA) {
					throw new IllegalArgumentException("Liczba " + liczba + " w zakładzie " + (i + 1) + " jest poza zakresem 1-49");
				}
			}

			for (Integer liczba : zaklad) {
				pola[numerPola][liczba] = true;
			}
		}

		if (anulowaePola != null) {
			for (int i = 0; i < anulowaePola.size() && i < LICZBA_POL; i++) {
				int numerPola = i + MIN_POLE;
				Boolean czyAnulowac = anulowaePola.get(i);
				if (czyAnulowac != null && czyAnulowac) {
					anulowane[numerPola] = true;
				}
			}
		}
	}

	public void zaznaczLiczbe(int pole, int liczba) {
		if (pole < MIN_POLE || pole > MAX_POLE || liczba < MIN_LICZBA || liczba > MAX_LICZBA) {
			throw new IllegalArgumentException("Nieprawidłowy numer pola lub liczby");
		}
		pola[pole][liczba] = true;
	}

	public void odznaczLiczbe(int pole, int liczba) {
		if (pole < MIN_POLE || pole > MAX_POLE || liczba < MIN_LICZBA || liczba > MAX_LICZBA) {
			throw new IllegalArgumentException("Nieprawidłowy numer pola lub liczby");
		}
		pola[pole][liczba] = false;
	}

	public void anulujPole(int pole) {
		if (pole < MIN_POLE || pole > MAX_POLE) {
			throw new IllegalArgumentException("Nieprawidłowy numer pola");
		}
		anulowane[pole] = true;
	}

	public void przywrocPole(int pole) {
		if (pole < MIN_POLE || pole > MAX_POLE) {
			throw new IllegalArgumentException("Nieprawidłowy numer pola");
		}
		anulowane[pole] = false;
	}

	public void ustawLiczbaLosowań(int liczbaLosowań) {
		if (liczbaLosowań < MIN_LICZBA_LOSOWAN || liczbaLosowań > MAX_LICZBA_LOSOWAN) {
			throw new IllegalArgumentException("Liczba losowań musi być z zakresu 1-10");
		}
		for (int i = MIN_LICZBA_LOSOWAN; i <= MAX_LICZBA_LOSOWAN; i++) {
			zaznaczoneLosowania[i] = false;
		}
		zaznaczoneLosowania[liczbaLosowań] = true;
	}

	public int[] getZaznaczoneLosowania() {
		List<Integer> lista = new ArrayList<>();
		for (int i = MIN_LICZBA_LOSOWAN; i <= MAX_LICZBA_LOSOWAN; i++) {
			if (zaznaczoneLosowania[i]) {
				lista.add(i);
			}
		}
		return lista.stream().mapToInt(i -> i).toArray();
	}

	public boolean czyLosowanieZaznaczone(int liczbaLosowań) {
		if (liczbaLosowań < MIN_LICZBA_LOSOWAN || liczbaLosowań > MAX_LICZBA_LOSOWAN) {
			return false;
		}
		return zaznaczoneLosowania[liczbaLosowań];
	}

	public List<Set<Integer>> getPrawidloweZaklady() {
		List<Set<Integer>> zaklady = new ArrayList<>();

		for (int pole = MIN_POLE; pole <= MAX_POLE; pole++) {
			if (!anulowane[pole]) {
				Set<Integer> zaklad = new TreeSet<>();

				for (int liczba = MIN_LICZBA; liczba <= MAX_LICZBA; liczba++) {
					if (pola[pole][liczba]) {
						zaklad.add(liczba);
					}
				}

				if (zaklad.size() == LICZBA_LICZB_W_ZAKLADZIE) {
					zaklady.add(zaklad);
				}
			}
		}

		return zaklady;
	}

	public boolean czyLiczbaZaznaczona(int pole, int liczba) {
		if (pole < MIN_POLE || pole > MAX_POLE || liczba < MIN_LICZBA || liczba > MAX_LICZBA) {
			return false;
		}
		return pola[pole][liczba];
	}

	public boolean czyPoleAnulowane(int pole) {
		if (pole < MIN_POLE || pole > MAX_POLE) {
			return false;
		}
		return anulowane[pole];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int pole = MIN_POLE; pole <= MAX_POLE; pole++) {
			sb.append(pole).append("\n");

			for (int rzad = 0; rzad < 5; rzad++) {
				sb.append(" ");
				for (int kol = 0; kol < 10; kol++) {
					int liczba = rzad * 10 + kol + MIN_LICZBA;
					if (liczba <= MAX_LICZBA) {
						if (pola[pole][liczba]) {
							sb.append("[ -- ]");
						} else {
							sb.append(String.format("[ %2d ]", liczba));
						}
						sb.append(" ");
					}
				}
				sb.append("\n");
			}

			sb.append(" ");
			if (anulowane[pole]) {
				sb.append("[ -- ]");
			} else {
				sb.append("[    ]");
			}
			sb.append(" anuluj\n");
		}

		sb.append("Liczba losowań: ");
		for (int i = MIN_LICZBA_LOSOWAN; i <= MAX_LICZBA_LOSOWAN; i++) {
			sb.append(" ");
			if (zaznaczoneLosowania[i]) {
				sb.append("[ -- ]");
			} else {
				sb.append(String.format("[ %d ]", i));
			}
		}
		sb.append("\n");

		return sb.toString();
	}
}