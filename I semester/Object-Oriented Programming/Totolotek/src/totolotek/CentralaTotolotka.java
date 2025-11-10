package totolotek;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CentralaTotolotka {
	private static final long CENA_ZAKLADU_BEZ_PODATKU = 2_40; // w groszach (2,4zł)
	private static final int I_STOPIEN = 6;
	private static final int II_STOPIEN = 5;
	private static final int III_STOPIEN = 4;
	private static final int IV_STOPIEN = 3;
	private static final long MINIMALNA_PULA_I_STOPNIA = 2_000_000_00; // w
	// groszach (2 mln zł)
	private static final long MINIMALNA_NAGRODA_III_STOPNIA = 36_00;
	private static final long MINIMALNA_NAGRODA_IV_STOPNIA = 24_00;


	private long srodkiFinansowe;
	private long kumulacjaNagrodIStopnia;
	private final List<Losowanie> losowania;
	private final Map<Integer, Integer> zakladyNaLosowanie = new HashMap<>(); // <Numer losowania, Liczba zakładów>
	private final List<Kolektura> kolektury = new ArrayList<>();

	public CentralaTotolotka(long poczatkoweSrodki) {
		this.srodkiFinansowe = poczatkoweSrodki;
		this.losowania = new ArrayList<>();
		this.kumulacjaNagrodIStopnia = 0;
	}

	public int getLiczbaLosowan() {
		return losowania.size();
	}

	public List<Kolektura> getKolektury() {
		return kolektury;
	}

	// Metoda do rejestracji zakładów
	public void zarejestrujZaklady(ArrayList<Integer> numeryLosowania,
								   int liczbaZakladow) {
		for (int numerLosowania : numeryLosowania) {
			if (zakladyNaLosowanie.containsKey(numerLosowania)) {
				// Jeśli losowanie już istnieje - zwiększ liczbę zakładów
				int staraLiczba = zakladyNaLosowanie.get(numerLosowania);
				zakladyNaLosowanie.put(numerLosowania, staraLiczba + liczbaZakladow);
			} else {
				// Jeśli to nowe losowanie - dodaj do mapy
				zakladyNaLosowanie.put(numerLosowania, liczbaZakladow);
			}
		}
	}

	public void przeprowadzLosowanie() {
		int numer = losowania.size() + 1;

		int liczbaZakladow = zakladyNaLosowanie.get(numer);
		long kwotaZakladow = liczbaZakladow * CENA_ZAKLADU_BEZ_PODATKU;


		Losowanie losowanie = new Losowanie(numer);
		losowania.add(losowanie);
		Map<Integer, Integer> liczbaTrafien = policzLiczbeTrafien(losowanie);
		Map<Integer, Long> puleNagrod = obliczPuleNagrod(losowanie,
				liczbaTrafien, kwotaZakladow);
		Map<Integer, Long> kwotyWygranych =
				obliczKwotyWygranych(puleNagrod, liczbaTrafien);
		losowanie.setKwotyWygranych(kwotyWygranych);
		losowanie.setPuleNagrod(puleNagrod);
		losowanie.setLiczbaTrafien(liczbaTrafien);
	}

	private Map<Integer, Integer> policzLiczbeTrafien(Losowanie losowanie) {
		Set<Integer> wylosowaneLiczby = losowanie.getWyniki();

		// Mapy do zliczania trafień: stopień -> liczba trafień
		Map<Integer, Integer> liczbaTrafien = new HashMap<>();
		liczbaTrafien.put(I_STOPIEN, 0); // I stopień (6 trafień)
		liczbaTrafien.put(II_STOPIEN, 0); // II stopień (5 trafień)
		liczbaTrafien.put(III_STOPIEN, 0); // III stopień (4 trafienia)
		liczbaTrafien.put(IV_STOPIEN, 0); // IV stopień (3 trafienia)

		// Przejdź przez wszystkie kolektury
		for (Kolektura kolektura : kolektury) {
			// Przejdź przez wszystkie kupony w kolekturze
			for (Kupon kupon : kolektura.getKupony()) {
				// Sprawdź czy kupon dotyczy tego losowania
				if (kupon.getNumeryLosowan().contains(losowanie.getNumer())) {
					// Sprawdź każdy zakład w kuponie
					for (Zaklad zaklad : kupon.getZaklady()) {
						int trafienia = policzTrafieniaWZakladzie(zaklad,
								wylosowaneLiczby);

						// Zaktualizuj liczbę trafień dla odpowiedniego stopnia
						if (trafienia >= 3 && trafienia <= 6) {
							liczbaTrafien.put(trafienia, liczbaTrafien.get(trafienia) + 1);
						}
					}
				}
			}
		}
		return liczbaTrafien;
	}

	// Metoda pomocnicza do liczenia trafień w pojedynczym zakładzie
	private int policzTrafieniaWZakladzie(Zaklad zaklad,
								  Set<Integer> wylosowaneLiczby) {
		int trafienia = 0;
		for (int liczba : zaklad.getLiczby()) {
			if (wylosowaneLiczby.contains(liczba)) { // Szybkie sprawdzanie
				trafienia++;
			}
		}
		return trafienia;
	}

	private Map<Integer, Long> obliczPuleNagrod(Losowanie losowanie,
												Map<Integer, Integer> liczbaTrafien, long kwotaZakladow) {
		// Obliczanie pul nagród
		long kwotaNaNagrody = (long)(kwotaZakladow * 0.51);
		long pulaIStopnia =
		Math.max((long)(kwotaNaNagrody * 0.44) + kumulacjaNagrodIStopnia, MINIMALNA_PULA_I_STOPNIA);
		long pulaIIStopnia = (long)(kwotaNaNagrody * 0.08);
		// Sprawdamy czy jest wystarczajaco na 4 pule
		long pulaIVStopnia = MINIMALNA_NAGRODA_IV_STOPNIA * liczbaTrafien.get(IV_STOPIEN);
		// Sprawdzamy czy zostały jeszcze jakieś środki
		long pulaIIIStopnia =
				Math.max(MINIMALNA_NAGRODA_III_STOPNIA * liczbaTrafien.get(III_STOPIEN),
						kwotaNaNagrody - pulaIStopnia - pulaIIStopnia - pulaIVStopnia);

		Map<Integer, Long> PuleNagrod = new HashMap<>();
		PuleNagrod.put(I_STOPIEN, pulaIStopnia);
		PuleNagrod.put(II_STOPIEN, pulaIIStopnia);
		PuleNagrod.put(III_STOPIEN, pulaIIIStopnia);
		PuleNagrod.put(IV_STOPIEN, pulaIVStopnia);

		return PuleNagrod;
	}

	private Map<Integer, Long> obliczKwotyWygranych(Map<Integer, Long> puleNagrod,
													Map<Integer, Integer> liczbaTrafien) {
		Map<Integer, Long> kwotyWygranych = new HashMap<>();
		if (liczbaTrafien.get(I_STOPIEN) == 0) {
			kwotyWygranych.put(I_STOPIEN, 0L);
			kumulacjaNagrodIStopnia = puleNagrod.get(I_STOPIEN);
		} else {
			kwotyWygranych.put(I_STOPIEN,
					puleNagrod.get(I_STOPIEN) / liczbaTrafien.get(I_STOPIEN));
			kumulacjaNagrodIStopnia = 0;
		}
		if (liczbaTrafien.get(II_STOPIEN) == 0) {
			kwotyWygranych.put(II_STOPIEN, 0L);
		} else {
			kwotyWygranych.put(II_STOPIEN,
					puleNagrod.get(II_STOPIEN) / liczbaTrafien.get(II_STOPIEN));
		}
		if (liczbaTrafien.get(III_STOPIEN) == 0) {
			kwotyWygranych.put(III_STOPIEN, 0L);
		} else {
			kwotyWygranych.put(III_STOPIEN,
					puleNagrod.get(III_STOPIEN) / liczbaTrafien.get(III_STOPIEN));
		}
		if (liczbaTrafien.get(IV_STOPIEN) == 0) {
			kwotyWygranych.put(IV_STOPIEN, 0L);
		} else {
			kwotyWygranych.put(IV_STOPIEN,
					puleNagrod.get(IV_STOPIEN) / liczbaTrafien.get(IV_STOPIEN));
		}
		return kwotyWygranych;
	}

	public void dodajSrodki(long zyskCentrali) {
		srodkiFinansowe += zyskCentrali;
	}

	public void dodajKolekture(Kolektura kolektura) {
		kolektury.add(kolektura);
	}

	public Map<Integer, ArrayList<Long>> obliczWygrane(Kupon kupon) {
		Map<Integer, ArrayList<Long>> wygrane = new HashMap<>();

		for (int numerLosowania : kupon.getNumeryLosowan()) {
			Losowanie losowanie = znajdzLosowanie(numerLosowania);
			for (Zaklad zaklad : kupon.getZaklady()) {
				int trafienia = policzTrafieniaWZakladzie(zaklad,
						losowanie.getWyniki());
				if (trafienia >= 3) {
					int stopien = trafienia;
					long wygrana = losowanie.getKwotaWygranej(stopien);

					if (!wygrane.containsKey(numerLosowania)) {
						wygrane.put(numerLosowania, new ArrayList<>());
					}
					wygrane.get(numerLosowania).add(wygrana);
				}
			}
		}

		return wygrane;
	}

	private Losowanie znajdzLosowanie(int numerLosowania) {
		for (Losowanie losowanie : losowania) {
			if (losowanie.getNumer() == numerLosowania) {
				return losowanie;
			}
		}
		throw new IllegalArgumentException("Nie znaleziono losowania o numerze " + numerLosowania);
	}

	public void wyplacNagrode(long kwota) {
		if (srodkiFinansowe < kwota) {
			long brakujacaKwota = kwota - srodkiFinansowe;
			BudzetPanstwa.przekazSubwencje(brakujacaKwota);
			srodkiFinansowe += brakujacaKwota;
		}
		srodkiFinansowe -= kwota;
	}

	public Kolektura losujKolektureDlaLosowegoGracza() {
		int losowyIndeks = ThreadLocalRandom.current().nextInt(0,
				kolektury.size());
		Kolektura kolektura = kolektury.get(losowyIndeks);
		return kolektura;
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Losowanie losowanie : losowania) {
            sb.append(losowanie).append(System.lineSeparator());
        }
        sb.append("Srodki finansowe centrali: ").append(formatujKwote(srodkiFinansowe));
        return sb.toString();
    }

	private static String formatujKwote(long kwotaWGroszach) {
		return String.format("%,d zł %02d gr", kwotaWGroszach / 100, kwotaWGroszach % 100);
	}
}