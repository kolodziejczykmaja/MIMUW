package tablice;

public class Skalar extends Tablica {
    private double wartosc;

    public Skalar(double wartosc) {
        this.wartosc = wartosc;
    }

    @Override
    public int wymiar() {
        return 0;
    }

    @Override
    public int liczba_elementów() {
        return 1;
    }

    @Override
    public int[] kształt() {
        return new int[]{};
    }

    @Override
    public String toString() {
        return "Wielkość skalarna [" + daj() + "] ma wymiar równy 0, kształt " +
                "równy [], liczbę elementów równą 1;";
    }

    @Override
    public Tablica kopia() {
            return new Skalar(daj());
    }

    @Override
    public void transponuj() {
        // Skalar pozostaje taki sam po transpozycji
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skalar that = (Skalar) obj;
        return Double.compare(daj(), daj()) == 0;
    }

    @Override
    public Tablica suma(Tablica tablica) throws ZłyIndeks, NiezgodneWymiaryException {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        }

        switch (tablica.wymiar()) {
            case 0:
                return sumaSkalar((Skalar) tablica);
            case 1:
                return sumaWektor((Wektor) tablica);
            case 2:
                return sumaMacierz((Macierz) tablica);
            default:
                throw new NiezgodneWymiaryException("Nieobsługiwany wymiar: " + tablica.wymiar());
        }
    }

    // Metoda prywatna dla sumy skalara ze skalarem
    private Tablica sumaSkalar(Skalar skalar) {
        return new Skalar(daj() + skalar.daj());
    }

    // Metoda prywatna dla sumy skalara z wektorem
    private Tablica sumaWektor(Wektor wektor) throws ZłyIndeks {
        double[] noweWartosci = new double[wektor.liczba_elementów()];
        for (int i = 0; i < noweWartosci.length; i++) {
            noweWartosci[i] = wektor.daj(i) + daj();
        }
        return new Wektor(noweWartosci, wektor.isPionowy());
    }

    // Metoda prywatna dla sumy skalara z macierzą
    private Tablica sumaMacierz(Macierz macierz) throws ZłyIndeks {
        double[][] noweWartosci = new double[macierz.getWiersze()][macierz.getKolumny()];
        for (int i = 0; i < noweWartosci.length; i++) {
            for (int j = 0; j < noweWartosci[i].length; j++) {
                noweWartosci[i][j] = macierz.daj(i, j) + daj();
            }
        }
        return new Macierz(noweWartosci);
    }

    @Override
    public void dodaj(Tablica tablica) throws NiezgodneWymiaryException {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        }
        else if (tablica.wymiar() != 0) {
            throw new NiezgodneWymiaryException("Nie można dodać tablicy o wymiarze" + tablica.wymiar() + " do skalara");
        } else {
            ustaw(daj() + ((Skalar)tablica).daj());
        }
	}

    @Override
    public Tablica iloczyn(Tablica tablica) throws ZłyIndeks, NiezgodneWymiaryException {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        }

        switch (tablica.wymiar()) {
            case 0:
                return iloczynSkalar((Skalar) tablica);
            case 1:
                return iloczynWektor((Wektor) tablica);
            case 2:
                return iloczynMacierz((Macierz) tablica);
            default:
                throw new NiezgodneWymiaryException("Nieobsługiwany wymiar: " + tablica.wymiar());
        }
    }

    // Metoda prywatna dla iloczynu skalara ze skalarem
    private Tablica iloczynSkalar(Skalar skalar) throws ZłyIndeks {
        return new Skalar(daj() * skalar.daj());
    }

    // Metoda prywatna dla iloczynu skalara z wektorem
    private Tablica iloczynWektor(Wektor wektor) throws ZłyIndeks {
        double[] noweWartosci = new double[wektor.liczba_elementów()];
        for (int i = 0; i < noweWartosci.length; i++) {
            noweWartosci[i] = wektor.daj(i) * daj();
        }
        return new Wektor(noweWartosci, wektor.isPionowy());
    }

    // Metoda prywatna dla iloczynu skalara z macierzą
    private Tablica iloczynMacierz(Macierz macierz) throws ZłyIndeks {
        double[][] noweWartosci = new double[macierz.getWiersze()][macierz.getKolumny()];
        for (int i = 0; i < noweWartosci.length; i++) {
            for (int j = 0; j < noweWartosci[i].length; j++) {
                noweWartosci[i][j] = macierz.daj(i, j) * daj();
            }
        }
        return new Macierz(noweWartosci);
    }

    @Override
    public void przemnóż(Tablica tablica) throws NiezgodneWymiaryException {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        }
        else if (tablica.wymiar() != 0) {
            throw new NiezgodneWymiaryException("Nie można pomnożyć " + tablica.getClass().getSimpleName() + " ze skalarem");
        } else {
            ustaw(daj() * ((Skalar)tablica).daj());
        }
    }

    @Override
    public Tablica negacja() {
        return new Skalar(daj() == 0.0 ? 0.0 : -daj());
    }

    @Override
    public void zaneguj() {
        ustaw(daj() == 0.0 ? 0.0 : -daj() );
    }

    // Specjalizowana metoda dla skalara (brak indeksów)
    public double daj() {
        return this.wartosc;
    }

    public void ustaw(double wartosc) {
        this.wartosc = wartosc;
    }

    // Uniwersalna metoda ze zmienną liczbą indeksów
    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 0) {
            throw new ZłyIndeks("Skalar nie wymaga indeksów");
        }
        return daj();
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 0) {
            throw new ZłyIndeks("Skalar nie wymaga indeksów");
        }
        ustaw(wartosc);
    }

    @Override
    public void przypisz(Tablica tablica) throws NiezgodneWymiaryException {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            // Przypisanie skalara do skalara
            this.wartosc = ((Skalar) tablica).daj();
        } else if (tablica.wymiar() == 1) {
            // Przypisanie wektora do skalara - niedozwolone
            throw new NiezgodneWymiaryException("Nie można przypisać wektora do skalara");
        } else if (tablica.wymiar() == 2) {
            // Przypisanie macierzy do skalara - niedozwolone
            throw new NiezgodneWymiaryException("Nie można przypisać macierzy do skalara");
        }
        else {
            throw new NiezgodneWymiaryException(
                    "Nie można przypisać tablicy o wymiarze " + tablica.wymiar());
        }
    }

    public Skalar wycinek(int... zakresy) throws ZłyIndeks {
        if (zakresy.length != 0) {
            throw new ZłyIndeks("Skalar nie wymaga zakresów");
        }
        return this;
    }
}