package totolotek;

import java.util.*;

public final class Losowanie {
	private static final int LICZBA_LOSOWANYCH = 6;
	private static final int MIN_LICZBA = 1;
	private static final int MAX_LICZBA = 49;
	private static final int I_STOPIEN = 6;
	private static final int IV_STOPIEN = 3;

	private final int numer;
	private final Set<Integer> wyniki;
	private Map<Integer, Integer> liczbaTrafien;
	private Map<Integer, Long> puleNagrod;
	private Map<Integer, Long> kwotyWygranych;

	public Losowanie(int numer) {
		this.numer = numer;
		this.wyniki = generujWyniki();
	}

	private Set<Integer> generujWyniki() {
		// Tworzymy listę wszystkich liczb (1-49)
		List<Integer> wszystkieLiczby = new ArrayList<>();
		for (int i = MIN_LICZBA; i <= MAX_LICZBA; i++) {
			wszystkieLiczby.add(i);
		}

		// Mieszamy liczby
		Collections.shuffle(wszystkieLiczby);

		// Tworzymy posortowany Set z pierwszych 6 liczb
		Set<Integer> wylosowane = new TreeSet<>();
		for (int i = 0; i < LICZBA_LOSOWANYCH; i++) {
			wylosowane.add(wszystkieLiczby.get(i));
		}

		return Collections.unmodifiableSet(wylosowane);
	}

	public int getNumer() {
		return this.numer;
	}

	public Set<Integer> getWyniki() {
		return this.wyniki;
	}

	public long getKwotaWygranej(int stopien) {
		return kwotyWygranych != null ? kwotyWygranych.getOrDefault(stopien, 0L) : 0L;
	}

	public void setKwotyWygranych(Map<Integer, Long> kwotyWygranych) {
		this.kwotyWygranych = kwotyWygranych;
	}

	public void setLiczbaTrafien(Map<Integer, Integer> liczbaTrafien) {
		this.liczbaTrafien = liczbaTrafien;
	}

	public void setPuleNagrod(Map<Integer, Long> puleNagrod) {
		this.puleNagrod = puleNagrod;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Losowanie nr ").append(numer).append("\n");
		sb.append("Wyniki: ");
		for (int wynik : wyniki) {
			sb.append(String.format("%2d ", wynik));
		}
		sb.append("\n\n");

		// Nagłówki kolumn
		sb.append(String.format("%-15s %-25s %-20s %-15s%n",
				"Stopień", "Łączna pula nagród", "Kwota wygranej", "Liczba trafień"));
		sb.append("------------------------------------------------------------\n");

		// Dane dla każdego stopnia (I-IV)
		for (int stopien = I_STOPIEN; stopien >= IV_STOPIEN; stopien--) {
			String nazwaStopnia = getNazwaStopnia(stopien);
			long pula = puleNagrod != null ? puleNagrod.getOrDefault(stopien, 0L) : 0L;
			long kwota = kwotyWygranych != null ? kwotyWygranych.getOrDefault(stopien, 0L) : 0L;
			int trafienia = liczbaTrafien != null ? liczbaTrafien.getOrDefault(stopien, 0) : 0;

			sb.append(String.format("%-15s %-25s %-20s %-15s%n", nazwaStopnia, formatujKwote(pula) + " zł", formatujKwote(kwota) + " zł", trafienia));
		}

		return sb.toString();
	}

	private String getNazwaStopnia(int stopien) {
		return switch (stopien) {
			case 6 -> "I STOPIEŃ (6)";
			case 5 -> "II STOPIEŃ (5)";
			case 4 -> "III STOPIEŃ (4)";
			case 3 -> "IV STOPIEŃ (3)";
			default ->
					throw new IllegalStateException("Nie ma takiego stopnia: " + stopien);
		};
	}

	private String formatujKwote(long kwotaWGroszach) {
		return String.format("%,d.%02d", kwotaWGroszach / 100, kwotaWGroszach % 100);
	}
}