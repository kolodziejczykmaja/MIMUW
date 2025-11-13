package tablice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Wektor extends Tablica {
    private double[] wartosci;
    private boolean pionowy;
    private List<WycinekWektora> dzieci = new ArrayList<>();

    public Wektor(double[] wartosci, boolean pionowy) {
        if (wartosci == null || wartosci.length == 0) {
            throw new IllegalArgumentException("Wektor nie może być pusty");
        }
        this.wartosci = Arrays.copyOf(wartosci, wartosci.length);
        this.pionowy = pionowy;
    }

    public void dodajDziecko(WycinekWektora dziecko) {
        dzieci.add(dziecko);
    }

    @Override
    public int wymiar() {
        return 1;
    }

    @Override
    public int liczba_elementów() {
        return wartosci.length;
    }

    @Override
    public int[] kształt() {
        return new int[]{liczba_elementów()};
    }

    @Override
    public String toString() {
        String result = "Wektor:\n";
        if (pionowy) {
            for (int i = 0; i < liczba_elementów(); i++) {
                result += "[ " + wartosci[i] + " ]\n";
            }
            result += "ma orientację pionową, wymiar równy 1, kształt równy [" + liczba_elementów() + "] i liczbę elementów równą " + liczba_elementów() + ";";
        } else {
            result += "[ ";
            for (int i = 0; i < liczba_elementów(); i++) {
                result += wartosci[i] + " ";
            }
            result += " ] ma orientację poziomą, wymiar równy 1, kształt równy [" + liczba_elementów() + "] i liczbę elementów równą " + liczba_elementów() + ";";
        }
        return result;
    }

        @Override
    public void transponuj() {
        pionowy = !pionowy;
        for (WycinekWektora dziecko : dzieci) {
            dziecko.transponuj();
        }
    }

    @Override
    public Tablica kopia() throws ZłyIndeks {
        return new Wektor(wartosci, pionowy);
    }

    public boolean isPionowy() {
        return pionowy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Wektor)) return false;
        Tablica that = (Wektor) obj;
        if (this.liczba_elementów() != that.liczba_elementów()) return false;
        if (this.isPionowy() != ((Wektor) that).isPionowy()) return false;
        for (int i = 0; i < this.liczba_elementów(); i++) {
            try {
                if (Double.compare(this.daj(i), that.daj(i)) != 0) {
                    return false;
                }
            } catch (ZłyIndeks e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPionowy(), Arrays.hashCode(wartosci));
    }

    @Override
    public Tablica suma(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {  // Wektor + Skalar
            Skalar skalar = (Skalar) tablica;
            double[] noweWartosci = new double[liczba_elementów()];
            for (int i = 0; i < liczba_elementów(); i++) {
                noweWartosci[i] = this.daj(i) + skalar.daj();
            }
            return new Wektor(noweWartosci, this.isPionowy());
        } else if (tablica.wymiar() == 1) { // Wektor + Wektor
            Wektor wektor = (Wektor) tablica;
            if (this.isPionowy() != wektor.isPionowy()) {
                throw new NieprawidlowaOperacjaException("Nie można dodawać wektorów różnych orientacji");
            } else if (this.liczba_elementów() != wektor.liczba_elementów()) {
                throw new NieprawidlowaOperacjaException("Nie można dodawać wektorów o róznych " +
                        "wymiarach");
            } else {
                double[] noweWartosci = new double[liczba_elementów()];
                for (int i = 0; i < liczba_elementów(); i++) {
                    noweWartosci[i] = this.daj(i) + wektor.daj(i);
                }
                return new Wektor(noweWartosci, this.isPionowy());
            }
        } else if (tablica.wymiar() == 2) {
            Macierz macierz = (Macierz) tablica;
            if (this.isPionowy()) {
                // Wektor pionowy: długość musi równać się liczbie wierszy macierzy
                if (this.liczba_elementów() != macierz.getWiersze()) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora pionowego (" + this.liczba_elementów() +
                                    ") nie zgadza się z liczbą wierszy macierzy (" + macierz.getWiersze() + ")"
                    );
                }
            } else {
                // Wektor poziomy: długość musi równać się liczbie kolumn macierzy
                if (this.liczba_elementów() != macierz.getKolumny()) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora poziomego (" + this.liczba_elementów() +
                                    ") nie zgadza się z liczbą kolumn macierzy (" + macierz.getKolumny() + ")");
                }
            }
            // Utworzenie macierzy wynikowej
            double[][] wynik = new double[macierz.getWiersze()][macierz.getKolumny()];

            // Dodawanie
            if (this.isPionowy()) {
                // Wektor pionowy: dodajemy do każdej kolumny macierzy
                for (int j = 0; j < macierz.getKolumny(); j++) {
                    for (int i = 0; i < macierz.getWiersze(); i++) {
                        wynik[i][j] = macierz.daj(i, j) + this.daj(i);
                    }
                }
            } else {
                // Wektor poziomy: dodajemy do każdego wiersza macierzy
                for (int i = 0; i < macierz.getWiersze(); i++) {
                    for (int j = 0; j < macierz.getKolumny(); j++) {
                        wynik[i][j] = macierz.daj(i, j) + this.daj(j);
                    }
                }
            }
            return new Macierz(wynik);
        }
        throw new NieprawidlowaOperacjaException("Nie można dodać tablicy o " +
                "wymiarze" + tablica.wymiar() + " do wektora");
    }

    @Override
    public Tablica iloczyn(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) { //Wektor * Skalar
            Skalar skalar = (Skalar) tablica;
            double[] noweWartosci = new double[this.liczba_elementów()];
            for (int i = 0; i < this.liczba_elementów(); i++) {
                noweWartosci[i] = this.daj(i) * skalar.daj();
            }
            return new Wektor(noweWartosci, this.isPionowy());
        } else if (tablica.wymiar() == 1) { //Wektor * Wektor
            Wektor wektor = (Wektor) tablica;
            if (this.liczba_elementów() != wektor.liczba_elementów() && !this.isPionowy() && wektor.isPionowy()) {
                throw new NiezgodneWymiaryException("Jeśli mnożymy wektor poziomy z wektorem pionowym, to liczba" +
                        "kolumn pierwszego wektora musi być równa liczbie wierszy drugiego wektora");
            } else if (this.isPionowy() == wektor.isPionowy() && this.liczba_elementów() != wektor.liczba_elementów()) {
                throw new NiezgodneWymiaryException("Aby pomnożyć wektory tej samej orientacji liczby elementów muszą być sobie równe");
            } else if (this.isPionowy() == wektor.isPionowy()) {
                double iloczynSkalarny = 0;
                for (int i = 0; i < this.liczba_elementów(); i++) {
                    iloczynSkalarny += this.daj(i) * wektor.daj(i);
                }
                return new Skalar(iloczynSkalarny);
            } else {
                if (this.isPionowy()) {
                    double[][] wynik =
                            new double[this.liczba_elementów()][wektor.liczba_elementów()];
                    for (int i = 0; i < this.liczba_elementów(); i++) {
                        for (int j = 0; j < wektor.liczba_elementów(); j++) {
                            wynik[i][j] = this.daj(i) * wektor.daj(j);
                        }
                    }
                    return new Macierz(wynik);
                } else {
                    double[][] wynik = new double[1][1]; // Macierz 1×1
                    for (int i = 0; i < this.liczba_elementów(); i++) {
                        wynik[0][0] += this.daj(i) * wektor.daj(i);
                    }
                    return new Macierz(wynik);
                }
            }
        } else if (tablica.wymiar() == 2) { // Wektor * Macierz
            Macierz macierz = (Macierz) tablica;
            if (this.isPionowy()) {
                if (this.liczba_elementów() != macierz.getKolumny()) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora pionowego (" + this.liczba_elementów() +
                                    ") nie zgadza się z liczbą kolumn macierzy (" + macierz.getKolumny() + ")");
                }
                // Mnożenie wektora pionowego przez macierz (wynik: wektor pionowy)
                double[] wynik = new double[macierz.getWiersze()];
                for (int i = 0; i < macierz.getWiersze(); i++) {
                    for (int j = 0; j < macierz.getKolumny(); j++) {
                        wynik[i] += this.daj(j) * macierz.daj(i, j);
                    }
                }
                return new Wektor(wynik, true); // Wynik pionowy (N×1)
            } else { // Wektor poziomy
                // Wektor poziomy może być mnożony tylko przez macierz, jeśli liczba kolumn macierzy = długość wektora
                if (this.liczba_elementów() != macierz.getWiersze()) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora poziomego (" + this.liczba_elementów() +
                                    ") nie zgadza się z liczbą wierszy macierzy (" + macierz.getWiersze() + ")");
                }
                // Mnożenie wektora poziomego przez macierz (wynik: wektor poziomy)
                double[] wynik = new double[macierz.getKolumny()];
                for (int j = 0; j < macierz.getKolumny(); j++) {
                    for (int i = 0; i < macierz.getWiersze(); i++) {
                        wynik[j] += this.daj(i) * macierz.daj(i, j);
                    }
                }
                return new Wektor(wynik, false);
            }
        } else {
            throw new NieprawidlowaOperacjaException("Nie można pomnożyć tablicy o wymiarze" + tablica.wymiar() + " z wektorem");
        }
    }

    @Override
    public Tablica negacja() throws ZłyIndeks {
        double[] noweWartosci = new double[liczba_elementów()];
        for (int i = 0; i < liczba_elementów(); i++) {
            noweWartosci[i] = daj(i) == 0.0 ? 0.0 : -wartosci[i];
        }
        return new Wektor(noweWartosci, this.pionowy);
    };

    @Override
    public void dodaj(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;
            for (int i = 0; i < liczba_elementów(); i++) {
                wartosci[i] += skalar.daj();
            }
        } else if (tablica.wymiar() == 1) {
            Wektor wektor = (Wektor) tablica;
            if (this.pionowy != wektor.pionowy) {
                throw new NieprawidlowaOperacjaException("Nie można dodać wektora innej orientacji");
            } else if (this.liczba_elementów() != wektor.liczba_elementów()) {
                throw new NieprawidlowaOperacjaException("Nie można dodać wektora o innym rozmiarze");
            } else {
                for (int i = 0; i < liczba_elementów(); i++) {
                    this.wartosci[i] += wektor.wartosci[i];
                }
            }
        } else {
            throw new NieprawidlowaOperacjaException("Nie można dodać tablicy o wymiarze" + tablica.wymiar() + " do wektora");
        }
    }

    @Override
    public void przemnóż(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;
            for (int i = 0; i < liczba_elementów(); i++) {
                ustaw(daj(i) * skalar.daj(), i);
            }
        } else {
            throw new NieprawidlowaOperacjaException("Operacja przemnóż dla wektora jest możliwa " +
                    "jedynie ze skalarem");
        }
    }

    @Override
    public void zaneguj() throws ZłyIndeks {
        for (int i = 0; i < liczba_elementów(); i++) {
            ustaw(daj(i) == 0 ? 0 : -daj(i), i);
        }
    }

    // Specjalizowana metoda dla wektora (jeden indeks)
    public double daj(int i) throws ZłyIndeks {
        if (i < 0 || i >= this.liczba_elementów()) {
            throw new ZłyIndeks("Nieprawidłowy indeks wektora");
        }
        return this.wartosci[i];
    }

    public void ustaw(double wartosc, int i) throws ZłyIndeks {
        if (i < 0 || i >= this.liczba_elementów()) {
            throw new ZłyIndeks("Nieprawidłowy indeks wektora");
        }
        this.wartosci[i] = wartosc;
    }

    // Uniwersalna metoda ze zmienną liczbą indeksów
    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 1) {
            throw new ZłyIndeks("Wektor wymaga jednego indeksu");
        }
        return daj(indeksy[0]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 1) {
            throw new ZłyIndeks("Wektor wymaga jednego indeksu");
        }
        ustaw(wartosc, indeksy[0]);
    }

    @Override
    public void przypisz(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            // Przypisanie skalara do wektora
            double wartosc = ((Skalar) tablica).daj();
            for (int i = 0; i < this.liczba_elementów(); i++) {
                this.ustaw(wartosc, i);
            }
        }
        else if (tablica.wymiar() == 1) {
            // Przypisanie wektora do wektora
            Wektor wektor = (Wektor) tablica;
            if (this.liczba_elementów() != wektor.liczba_elementów()) {
                throw new NiezgodneWymiaryException("Nie można przypisać wektora do wektora o innej liczbie elementów");
            } else {
                for (int i = 0; i < this.liczba_elementów(); i++) {
                    this.ustaw(wektor.daj(i), i);
                }
                this.pionowy = wektor.isPionowy();
            }
        } else if (tablica.wymiar() == 2) {
            // Przypisanie macierzy do wektora - niedozwolone
            throw new NieprawidlowaOperacjaException("Nie można przypisać macierzy do wektora");
        }
        else {
            throw new NieprawidlowaOperacjaException(
                    "Nie można przypisać tablicy o wymiarze " + tablica.wymiar() + " do wektora");
        }
    }

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

        return new WycinekWektora(this, początek, koniec, this.isPionowy());
    }
}