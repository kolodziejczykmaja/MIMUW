package tablice;

	public class WycinekWektora extends Wektor {
	private final Wektor oryginal;
	private final int poczatek;
	private final int koniec;
	private boolean pionowy = false;

	public WycinekWektora(Wektor oryginal, int poczatek, int koniec, boolean pionowy ) {
		super(new double[1], oryginal != null ? oryginal.isPionowy() : false);
		if (oryginal == null) {
			throw new NullPointerException("oryginal wektor nie może być null");
		} else if ((poczatek < 0) || (liczba_elementów() < 0) || (liczba_elementów() > oryginal.liczba_elementów())) {
			throw new IllegalArgumentException("Nieprawidłowy zakres wycinka:" +
					" początek=" + poczatek + ", koniec=" + koniec);
		}
		this.oryginal = oryginal;
		this.poczatek = poczatek;
		this.koniec = koniec;
		this.pionowy = pionowy;
		oryginal.dodajDziecko(this);
	}

	@Override
	public int liczba_elementów() {
		return koniec - poczatek + 1;
	}

	@Override
	public double daj(int... indeksy) throws ZłyIndeks {
		if (indeksy.length != 1) {
			throw new ZłyIndeks("Wycinek wymaga jednego indeksu");
		}
		int i = indeksy[0];
		if (i < 0 || i >= liczba_elementów()) {
			throw new ZłyIndeks("Nieprawidłowy indeks wycinka: " + i);
		}
		return oryginal.daj(poczatek + i);
	}

	@Override
	public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
		if (indeksy.length != 1) {
			throw new ZłyIndeks("Wycinek wymaga jednego indeksu");
		}
		int i = indeksy[0];
		if (i < 0 || i >= liczba_elementów()) {
			throw new ZłyIndeks("Nieprawidłowy indeks wycinka: " + i);
		}
		oryginal.ustaw(wartosc, poczatek + i);
	}

	@Override
	public Tablica kopia() throws ZłyIndeks {
		double[] nowaTablica = new double[liczba_elementów()];
		for (int i = 0; i < liczba_elementów(); i++) {
				nowaTablica[i] = oryginal.daj(poczatek + i);
		}
		return new Wektor(nowaTablica, this.isPionowy());
	}

	@Override
	public boolean isPionowy() {
		return this.pionowy;
	}

	@Override
	public String toString() {
		String result = "Wycinek wektora:\n";
		for (int i = 0; i < liczba_elementów(); i++) {
			if (!isPionowy() && i == 0) {
				result += "[ ";
			}
			double wartosc = 0;
			try {
				wartosc = oryginal.daj(poczatek + i);
			} catch (ZłyIndeks e) {
				throw new RuntimeException(e);
			}
			if (oryginal.isPionowy()) {
				result += "[ " + wartosc + " ]\n";
			} else {
				result += wartosc + " ";
			}
		}
		if (!oryginal.isPionowy()) {
			result += "]\n" ;
		}
		return result;
	}

		@Override
		public WycinekWektora wycinek(int... zakresy) throws ZłyIndeks {
			if (zakresy.length != 2) {
				throw new ZłyIndeks("Wymagane 2 zakresy (początek, koniec)");
			}

			int początek= zakresy[0];
			int koniec = zakresy[1];

			// Walidacja zakresów
			if (początek< 0 || koniec >= liczba_elementów() || początek> koniec) {
				throw new ZłyIndeks("Nieprawidłowe zakresy: " + początek+ "-" + koniec +
						" dla wektora o długości " + liczba_elementów());
			}

			int dlugosc = koniec - początek+ 1;
			return new WycinekWektora(this.oryginal,
					początek + this.poczatek , koniec + this.poczatek ,
					this.isPionowy());
		}
}