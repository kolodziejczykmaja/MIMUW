package tablice;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Macierz extends Tablica {
    private double[][] wartosci;
    private boolean czyTransponowana = false;
    private List<WycinekMacierzy> dzieci = new ArrayList<>();

    public Macierz(double[][] wartosci) {
        // Sprawdzanie czy macierz nie jest pusta
        if (wartosci == null || wartosci.length == 0 || wartosci[0] == null || wartosci[0].length == 0) {
            throw new IllegalArgumentException("Macierze nie może być pusta");
        }

        // Sprawdzanie prostokątności (czy wszystkie wiersze mają tę samą liczbę kolumn)
        int liczbaKolumn = wartosci[0].length;
        for (int i = 1; i < wartosci.length; i++) {
            if (wartosci[i] == null || wartosci[i].length != liczbaKolumn) {
                throw new IllegalArgumentException("Macierz musi być prostokątna!");
            }
        }
        this.wartosci = kopiujMacierz(wartosci);
    }

    public void dodajDziecko(WycinekMacierzy dziecko) {
        dzieci.add(dziecko);
    }

    private double[][] kopiujMacierz(double[][] wartosci) {
        double[][] nowaMacierz = new double[wartosci.length][wartosci[0].length];
        for (int i = 0; i < wartosci.length; i++) {
            System.arraycopy(wartosci[i], 0, nowaMacierz[i], 0, wartosci[i].length);
        }
        return nowaMacierz;
    }

    @Override
    public int wymiar() {
        return 2;
    }

    @Override
    public int liczba_elementów() {
        return wartosci.length * wartosci[0].length;
    }

    @Override
    public int[] kształt() {
        return new int[]{wartosci.length, wartosci[0].length};
    }

    @Override
    public String toString() {
        String result = "Macierz:\n";

        for (int i = 0; i < wartosci.length; i++) {
            result += "[ ";
            for (int j = 0; j < wartosci[i].length; j++) {
                result += wartosci[i][j] + " ";
            }
            result += "]\n";
        }

        result += "ma wymiar równy 2, kształt równy [ " + wartosci.length + " " + wartosci[0].length +
                " ] i liczbę elementów równą " + (wartosci.length * wartosci[0].length) + ";";

        return result;
    }

    @Override
    public void transponuj() {
        double[][] transponowana = new double[getKolumny()][getWiersze()];
        for (int i = 0; i < getWiersze(); i++) {
            for (int j = 0; j < getKolumny(); j++) {
                transponowana[j][i] = wartosci[i][j];
            }
        }
        this.wartosci = transponowana;
        czyTransponowana = !czyTransponowana;
        for (WycinekMacierzy dziecko : dzieci) {
            dziecko.ojciecTransponowal();
        }
    }

    public boolean czyTransponowana() {
        return czyTransponowana;
    }

    @Override
    public Tablica kopia() {
        return new Macierz(wartosci);
    }

    public int getWiersze() {
        return wartosci.length;
    }
    public int getKolumny() {
        return wartosci[0].length;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Macierz)) return false;
        Macierz other = (Macierz) obj;

        // Porównaj wymiary
        if (this.getWiersze() != other.getWiersze() ||
                this.getKolumny() != other.getKolumny()) {
            return false;
        }

        // Porównaj każdy element
        for (int i = 0; i < getWiersze(); i++) {
            for (int j = 0; j < getKolumny(); j++) {
                try {
                    if (Double.compare(this.daj(i, j), other.daj(i, j)) != 0) {
                        return false;
                    }
                } catch (ZłyIndeks e) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Tablica suma(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;
            double[][] wynik = new double[getWiersze()][getKolumny()];
            double wartoscSkalara = skalar.daj();

            for (int i = 0; i < getWiersze(); i++) {
                for (int j = 0; j < getKolumny(); j++) {
                    wynik[i][j] = this.daj(i, j) + wartoscSkalara;
                }
            }
            return new Macierz(wynik);
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

            double[][] wynik = new double[getWiersze()][getKolumny()];

            if (wektor.isPionowy()) {
                // Dodaj wektor pionowy do każdej kolumny macierzy
                for (int i = 0; i < getWiersze(); i++) {
                    for (int j = 0; j < getKolumny(); j++) {
                        wynik[i][j] = this.daj(i, j) + wektor.daj(i);
                    }
                }
            } else {
                // Dodaj wektor poziomy do każdego wiersza macierzy
                for (int i = 0; i < getWiersze(); i++) {
                    for (int j = 0; j < getKolumny(); j++) {
                        wynik[i][j] = this.daj(i, j) + wektor.daj(j);
                    }
                }
            }

            return new Macierz(wynik);
        } else if (tablica.wymiar() == 2) {
            Macierz macierz = (Macierz) tablica;
            if (this.getWiersze() != macierz.getWiersze() || this.getKolumny() != macierz.getKolumny()) {
                throw new NiezgodneWymiaryException(
                        "Wymiary macierzy nie zgadzają się: [" + getWiersze() + "x" + getKolumny() +
                                "] != [" + macierz.getWiersze() + "x" + macierz.getKolumny() + "]");
            }

            double[][] wynik = new double[getWiersze()][getKolumny()];

            for (int i = 0; i < getWiersze(); i++) {
                for (int j = 0; j < getKolumny(); j++) {
                    wynik[i][j] = this.daj(i, j) + macierz.daj(i, j);
                }
            }

            return new Macierz(wynik);
        } else {
            throw new NieprawidlowaOperacjaException("Nie można dodać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
        }
    }

    @Override
    public Tablica iloczyn(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;
            double[][] wynik = new double[getWiersze()][getKolumny()];
            double wartoscSkalara = skalar.daj();

            for (int i = 0; i < getWiersze(); i++) {
                for (int j = 0; j < getKolumny(); j++) {
                    wynik[i][j] = this.daj(i, j) * wartoscSkalara;
                }
            }
            return new Macierz(wynik);
        } else if (tablica.wymiar() == 1) {
            Wektor wektor = (Wektor) tablica;
            if (wektor.isPionowy()) {
                // Mnożenie macierzy przez wektor pionowy (wynik: wektor pionowy)
                if (getKolumny() != wektor.liczba_elementów()) {
                    throw new NiezgodneWymiaryException(
                            "Liczba kolumn macierzy (" + getKolumny() +
                                    ") musi być równa długości wektora pionowego (" +
                                    wektor.liczba_elementów() + ")");
                }

                double[] wynik = new double[getWiersze()];
                for (int i = 0; i < getWiersze(); i++) {
                    for (int j = 0; j < getKolumny(); j++) {
                        wynik[i] += this.daj(i, j) * wektor.daj(j);
                    }
                }
                return new Wektor(wynik, true);
            } else {
                // Mnożenie macierzy przez wektor poziomy (wynik: wektor poziomy)
                if (getWiersze() != wektor.liczba_elementów()) {
                    throw new NiezgodneWymiaryException(
                            "Liczba wierszy macierzy (" + getWiersze() +
                                    ") musi być równa długości wektora poziomego (" +
                                    wektor.liczba_elementów() + ")");
                }

                double[] wynik = new double[getKolumny()];
                for (int j = 0; j < getKolumny(); j++) {
                    for (int i = 0; i < getWiersze(); i++) {
                        wynik[j] += this.daj(i, j) * wektor.daj(i);
                    }
                }
                return new Wektor(wynik, false);
            }
        } else if (wymiar() == 2) {
            Macierz macierz = (Macierz) tablica;
            if (this.getKolumny() != macierz.getWiersze()) {
                throw new NiezgodneWymiaryException(
                        "Liczba kolumn pierwszej macierzy (" + this.getKolumny() +
                                ") musi być równa liczbie wierszy drugiej macierzy (" +
                                macierz.getWiersze() + ")");
            }

            double[][] wynik = new double[this.getWiersze()][macierz.getKolumny()];

            for (int i = 0; i < this.getWiersze(); i++) {
                for (int j = 0; j < macierz.getKolumny(); j++) {
                    for (int k = 0; k < this.getKolumny(); k++) {
                        wynik[i][j] += this.daj(i, k) * macierz.daj(k, j);
                    }
                }
            }

            return new Macierz(wynik);
        } else {
            throw new NieprawidlowaOperacjaException("Nie można pomnożyć macierzy przez tablicę o " +
                    "wymiarze " + tablica.wymiar());
        }
    };

    @Override
    public Tablica negacja(){
        double[][] wynik = new double[getWiersze()][getKolumny()];
        for (int i = 0; i < getWiersze(); i++) {
            for (int j = 0; j < getKolumny(); j++) {
                wynik[i][j] = wartosci[i][j] == 0.0 ? 0.0 : -wartosci[i][j];
            }
        }
        return new Macierz(wynik);
    };

    @Override
    public void dodaj(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;

            for (int i = 0; i < getWiersze(); i++) {
                for (int j = 0; j < getKolumny(); j++) {
                    wartosci[i][j] += skalar.daj();
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
                        wartosci[i][j] += wektor.daj(i);
                    }
                }
            } else {
                // Dodaj wektor poziomy do każdego wiersza macierzy
                for (int i = 0; i < getWiersze(); i++) {
                    for (int j = 0; j < getKolumny(); j++) {
                        wartosci[i][j] += wektor.daj(j);
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
                    wartosci[i][j] += macierz.daj(i, j);
                }
            }
        } else {
            throw new NieprawidlowaOperacjaException("Nie można dodać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
        }
    }

    @Override
    public void przemnóż(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            Skalar skalar = (Skalar) tablica;

            for (int i = 0; i < getWiersze(); i++) {
                for (int j = 0; j < getKolumny(); j++) {
                    wartosci[i][j] *= skalar.daj();
                }
            }
        } else if (tablica.wymiar() == 1) {
            throw new NieprawidlowaOperacjaException("Nie można przemnożyć macierzy przez wektor, " +
                    "ponieważ w wyniku taka operacja daje wektor");
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
                    for (int k = 0; k < getKolumny(); k++) {
                        wynik[i][j] += this.daj(i, k) * macierz.daj(k, j);
                    }
                }
            }
            this.wartosci = wynik;
        } else {
            throw new NieprawidlowaOperacjaException("Nie można dodać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
        }
    }

    @Override
    public void zaneguj() throws ZłyIndeks {
        for (int i = 0; i < getWiersze(); i++) {
            for (int j = 0; j < getKolumny(); j++) {
                ustaw(daj(i, j) == 0 ? 0 : -daj(i, j));
            }
        }
    }

    public double daj(int wiersz, int kolumna) throws ZłyIndeks  {
        if (wiersz < 0 || wiersz >= getWiersze() || kolumna < 0 || kolumna >= getKolumny()) {
            throw new ZłyIndeks("Nieprawidłowe indeksy macierzy: [" + wiersz + "," + kolumna +
                    "] dla macierzy [" + getWiersze() + " x " + getKolumny() + "]");
        }
        return this.wartosci[wiersz][kolumna];
    }

    public void ustaw(double wartosc, int wiersz, int kolumna) throws ZłyIndeks {
        if (wiersz < 0 || wiersz >= getWiersze() || kolumna < 0 || kolumna >= getKolumny()) {
            throw new ZłyIndeks("Nieprawidłowe indeksy macierzy: [" + wiersz + "," + kolumna +
                    "] dla macierzy [" + getWiersze() + " x " + getKolumny() + "]");
        }
        this.wartosci[wiersz][kolumna] = wartosc;
    }

    // Uniwersalne metody ze zmienną liczbą parametrów
    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 2) {
            throw new ZłyIndeks("Macierz wymaga podania dokładnie dwóch indeksów");
        }
        return daj(indeksy[0], indeksy[1]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy.length != 2) {
            throw new ZłyIndeks("Macierz wymaga podania dokładnie dwóch indeksów");
        }
        ustaw(wartosc, indeksy[0], indeksy[1]);
    }

    @Override
    public void przypisz(Tablica tablica) throws NiezgodneWymiaryException, ZłyIndeks {
        if (tablica == null) {
            throw new NullPointerException("Argument nie może być null");
        } else if (tablica.wymiar() == 0) {
            // Przypisanie skalara do macierzy
            Skalar skalar = (Skalar) tablica;
            double wartosc = skalar.daj();
            for (int i = 0; i < this.wartosci.length; i++) {
                Arrays.fill(this.wartosci[i], wartosc);
            }
        } else if (tablica.wymiar() == 1) {
            // Przypisanie wektora do macierzy
            Wektor wektor = (Wektor) tablica;
            if (wektor.isPionowy()) {
                // Wektor pionowy - kopiuj do każdej kolumny
                if (wektor.liczba_elementów() != this.wartosci.length) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora pionowego nie zgadza się z liczbą wierszy");
                }
                for (int j = 0; j < this.wartosci[0].length; j++) {
                    for (int i = 0; i < this.wartosci.length; i++) {
                        this.wartosci[i][j] = wektor.daj(i);
                    }
                }
            } else {
                // Wektor poziomy - kopiuj do każdego wiersza
                if (wektor.liczba_elementów() != this.wartosci[0].length) {
                    throw new NiezgodneWymiaryException(
                            "Długość wektora poziomego nie zgadza się z liczbą kolumn");
                }
                for (int i = 0; i < this.wartosci.length; i++) {
                    for (int j = 0; j < this.wartosci[i].length; j++) {
                        this.wartosci[i][j] = wektor.daj(j);
                    }
                }
            }
        } else if (tablica.wymiar() == 2) {
            // Przypisanie macierzy do macierzy
            Macierz macierz = (Macierz) tablica;
            if (this.wartosci.length != macierz.getWiersze() ||
                    this.wartosci[0].length != macierz.getKolumny()) {
                throw new NiezgodneWymiaryException(
                        "Wymiary przypisywaniej macierzy nie zgadzają się");
            } else {
                for (int i = 0; i < this.wartosci.length; i++) {
                    System.arraycopy(macierz.wartosci[i], 0,
                            this.wartosci[i], 0,
                            this.wartosci[i].length);
                }
            }
        }
        else {
            throw new NieprawidlowaOperacjaException(
                    "Nie można przypisać tablicy o wymiarze " + tablica.wymiar() + " do macierzy");
        }
    }

    public WycinekMacierzy wycinek(int... zakresy) throws ZłyIndeks {
        if (zakresy.length != 4) throw new ZłyIndeks("Wymagane 4 zakresy (wPocz, wKoniec, kPocz, kKoniec)");

        int wPocz = zakresy[0], wKoniec = zakresy[1];
        int kPocz = zakresy[2], kKoniec = zakresy[3];

        if (wPocz < 0 || wKoniec >= wartosci.length ||
                kPocz < 0 || kKoniec >= wartosci[0].length ||
                wPocz > wKoniec || kPocz > kKoniec) {
            throw new ZłyIndeks("Nieprawidłowe zakresy");
        }

        return new WycinekMacierzy(this, wPocz, wKoniec, kPocz, kKoniec, false);
    }
}