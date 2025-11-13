package tablice;

public class WycinekMacierzy extends Macierz {
	private final Macierz oryginal;
	private int wStart, wKoniec;
	private int kStart, kKoniec;
	private boolean czyTransponowana;

	public WycinekMacierzy(Macierz oryginal,
						   int wStart, int wKoniec,
						   int kStart, int kKoniec, boolean czyTransponowana) {
		super(new double[1][1]);  // Nie przechowujemy danych – tylko referencję
		this.oryginal = oryginal;
		this.wStart = wStart;
		this.wKoniec = wKoniec;
		this.kStart = kStart;
		this.kKoniec = kKoniec;
		this.czyTransponowana = czyTransponowana;
		oryginal.dodajDziecko(this);
	}

	void ojciecTransponowal() {
		czyTransponowana = !czyTransponowana;
		int tmp = wStart;
		wStart = kStart;
		kStart = tmp;

		tmp = wKoniec;
		wKoniec = kKoniec;
		kKoniec = tmp;
	}

	@Override
	public double daj(int i, int j) throws ZłyIndeks {
		sprawdzIndeksy(i, j);
		if ((!czyTransponowana && !oryginal.czyTransponowana()) || (!czyTransponowana && oryginal.czyTransponowana())) {
			return oryginal.daj(wStart + i, kStart + j);
		} else {
			oryginal.transponuj();
			this.ojciecTransponowal();
			double tmp = oryginal.daj(kStart + i, wStart + j);
			oryginal.transponuj();
			this.ojciecTransponowal();
			return tmp;
		}
	}

	@Override
	public void ustaw(double wartosc, int i, int j) throws ZłyIndeks {
		sprawdzIndeksy(i, j);
		if ((!czyTransponowana && !oryginal.czyTransponowana()) || (!czyTransponowana && oryginal.czyTransponowana())) {
			oryginal.ustaw(wartosc,wStart + i, kStart + j);
		} else {
			oryginal.transponuj();
			this.ojciecTransponowal();
			oryginal.ustaw(wartosc, kStart + i, wStart + j);
			oryginal.transponuj();
			this.ojciecTransponowal();
		}
	}

	@Override
	public void transponuj() {
		czyTransponowana = !czyTransponowana;
	}

	@Override
	public int getWiersze() {
		return ((!czyTransponowana && !oryginal.czyTransponowana()) || (!czyTransponowana && oryginal.czyTransponowana()))
				? ((wKoniec - wStart) + 1)
				: ((kKoniec - kStart) + 1);
	}

	@Override
	public int getKolumny() {
		return ((!czyTransponowana && !oryginal.czyTransponowana()) || (!czyTransponowana && oryginal.czyTransponowana()))
				? ((kKoniec - kStart) + 1)
				: ((wKoniec - wStart) + 1);
	}

	@Override
	public Tablica kopia() {
		return new WycinekMacierzy(oryginal, wStart, wKoniec, kStart, kKoniec, czyTransponowana);
	}

	private void sprawdzIndeksy(int i, int j) throws ZłyIndeks {
		if (i < 0 || i >= getWiersze() || j < 0 || j >= getKolumny()) {
			throw new ZłyIndeks(
					String.format("Indeks [%d, %d] poza zakresem wycinka [%d x %d]",
							i, j, getWiersze(), getKolumny())
			);
		}
	}

	@Override
	public String toString() {
		String result = "WycinekMacierzy:\n";
		int rows = getWiersze();
		int cols = getKolumny();
		for (int i = 0; i < rows; i++) {
			result += "[ ";
			for (int j = 0; j < cols; j++) {
				try {
					result += daj(i, j) + " ";
				} catch (ZłyIndeks e) {
					throw new RuntimeException(e);
				}
			}
			result += "]\n";;
		}
		return result;
	}

	@Override
	public void przemnóż(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
		if (tablica == null) {
			throw new NullPointerException("Argument nie może być null");
		} else if (tablica.wymiar() == 0) {
			Skalar skalar = (Skalar) tablica;
			for (int i = 0; i < getWiersze(); i++) {
				for (int j = 0; j < getKolumny(); j++) {
					double val = daj(i, j) * skalar.daj();
					ustaw(val, i, j);
				}
			}
		} else if (tablica.wymiar() == 1) {
			throw new NiezgodneWymiaryException("Nie można przemnożyć macierzy przez wektor, ponieważ w wyniku taka operacja daje wektor");
		} else if (tablica.wymiar() == 2) {
			Macierz macierz = (Macierz) tablica;
			if (this.getKolumny() != macierz.getWiersze() ||
					this.getKolumny() != macierz.getKolumny()) {
				throw new NiezgodneWymiaryException(
						"Mnożenie w miejscu możliwe tylko dla macierzy kwadratowych tych samych rozmiarów");
			}
			double[][] wynik = new double[getWiersze()][getKolumny()];
			for (int i = 0; i < getWiersze(); i++) {
				for (int j = 0; j < getKolumny(); j++) {
					double val = 0.0;
					for (int k = 0; k < getKolumny(); k++) {
						val += this.daj(i, k) * macierz.daj(k, j);
					}
					wynik[i][j] = val;
				}
			}
			for (int i = 0; i < getWiersze(); i++) {
				for (int j = 0; j < getKolumny(); j++) {
					ustaw(wynik[i][j], i, j);
				}
			}
		} else {
			throw new NiezgodneWymiaryException("Nie można dodać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
		}
	}

	@Override
	public void dodaj(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
		if (tablica == null) {
			throw new NullPointerException("Argument nie może być null");
		} else if (tablica.wymiar() == 0) {
			Skalar skalar = (Skalar) tablica;

			for (int i = 0; i < getWiersze(); i++) {
				for (int j = 0; j < getKolumny(); j++) {
					double val = daj(i, j) + skalar.daj();
					ustaw(val, i, j);
				}
			}
		} else if (tablica.wymiar() == 1) {
			Wektor wektor = (Wektor) tablica;
			if (wektor.isPionowy() && wektor.liczba_elementów() != getWiersze()) {
				throw new NiezgodneWymiaryException(
						"Długość wektora pionowego (" + wektor.liczba_elementów() +
								") nie zgadza się z liczbą wierszy macierzy (" + getWiersze() + ")");
			}
			if (!wektor.isPionowy() && wektor.liczba_elementów() != getKolumny()) {
				throw new NiezgodneWymiaryException(
						"Długość wektora poziomego (" + wektor.liczba_elementów() +
								") nie zgadza się z liczbą kolumn macierzy (" + getKolumny() + ")");
			}
			if (wektor.isPionowy()) {
				// Dodaj wektor pionowy do każdej kolumny macierzy
				for (int i = 0; i < getWiersze(); i++) {
					for (int j = 0; j < getKolumny(); j++) {
						double val = daj(i, j) + wektor.daj();
						ustaw(val, i, j);
					}
				}
			} else {
				// Dodaj wektor poziomy do każdego wiersza macierzy
				for (int i = 0; i < getWiersze(); i++) {
					for (int j = 0; j < getKolumny(); j++) {
						double val = daj(i, j) + wektor.daj(j);
						ustaw(val, i, j);
					}
				}
			}
		} else if (tablica.wymiar() == 2) {
			Macierz macierz = (Macierz) tablica;
			if (this.getWiersze() != macierz.getWiersze() || this.getKolumny() != macierz.getKolumny()) {
				throw new NiezgodneWymiaryException(
						"Wymiary dodawanej macierzy nie zgadzają się: [" + getWiersze() + "x" + getKolumny() +
								"] != [" + macierz.getWiersze() + "x" + macierz.getKolumny() + "]");
			}

			for (int i = 0; i < getWiersze(); i++) {
				for (int j = 0; j < getKolumny(); j++) {
					double val = daj(i, j) + macierz.daj(i,j);
					System.out.println( val + " = " + daj(i, j) + " + " + macierz.daj(i,j) );
					ustaw(val, i, j);
				}
			}
		} else {
			throw new NiezgodneWymiaryException("Nie można dodać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
		}
	}

	@Override
	public void zaneguj() throws ZłyIndeks {
		for (int i = 0; i < getWiersze(); i++) {
			for (int j = 0; j < getKolumny(); j++) {
				ustaw(-daj(i,j),i,j);
			}
		}
	}

	@Override
	public void przypisz(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
		if (tablica == null)
			throw new NullPointerException("Argument nie może być null");

		int wiersze = this.getWiersze();
		int kolumny = this.getKolumny();

		if (tablica.wymiar() == 0) {
			double wartosc = ((Skalar) tablica).daj();
			for (int i = 0; i < wiersze; i++) {
				for (int j = 0; j < kolumny; j++) {
					this.ustaw(wartosc, i, j);
				}
			}
		} else if (tablica.wymiar() == 1) {
			Wektor wektor = (Wektor) tablica;
			if (wektor.isPionowy()) {
				if (wektor.liczba_elementów() != wiersze)
					throw new NiezgodneWymiaryException("Niezgodna liczba wierszy");

				for (int i = 0; i < wiersze; i++) {
					for (int j = 0; j < kolumny; j++) {
						this.ustaw(wektor.daj(i), i, j);
					}
				}
			} else {
				if (wektor.liczba_elementów() != kolumny)
					throw new NiezgodneWymiaryException("Niezgodna liczba kolumn");

				for (int i = 0; i < wiersze; i++) {
					for (int j = 0; j < kolumny; j++) {
						this.ustaw(wektor.daj(j), i, j);
					}
				}
			}
		} else if (tablica.wymiar() == 2) {
			Macierz macierz = (Macierz) tablica;

			if (macierz.getWiersze() != wiersze || macierz.getKolumny() != kolumny)
				throw new NiezgodneWymiaryException("Wymiary nie pasują");

			for (int i = 0; i < wiersze; i++) {
				for (int j = 0; j < kolumny; j++) {
					this.ustaw(macierz.daj(i, j), i, j);
				}
			}
		} else {
			throw new NiezgodneWymiaryException("Nieobsługiwany wymiar: " + tablica.wymiar());
		}
	}

	@Override
	public WycinekMacierzy wycinek(int... zakresy) throws ZłyIndeks {
		if (zakresy.length != 4) throw new ZłyIndeks("Wymagane 4 zakresy (wPocz, wKoniec, kPocz, kKoniec)");

		int wPocz, wKoniec, kPocz, kKoniec;
		if (czyTransponowana && !oryginal.czyTransponowana() || !czyTransponowana && oryginal.czyTransponowana()) {
			wPocz = zakresy[2] + this.wStart;
			wKoniec = zakresy[3] + this.wStart;
			kPocz = zakresy[0] + this.kStart;
			kKoniec = zakresy[1] + this.kStart;
		} else {
			wPocz = zakresy[0] + this.wStart;
			wKoniec = zakresy[1] + this.wStart;
			kPocz = zakresy[2] + this.kStart;
			kKoniec = zakresy[3] + this.kStart;
		}

		if (wPocz < 0 || wKoniec >= oryginal.getWiersze() ||
				kPocz < 0 || kKoniec >= oryginal.getKolumny() ||
				wPocz > wKoniec || kPocz > kKoniec) {
			throw new ZłyIndeks("Nieprawidłowe zakresy");
		}

		return new WycinekMacierzy(this.oryginal, wPocz, wKoniec, kPocz,
				kKoniec, czyTransponowana);
	}
}