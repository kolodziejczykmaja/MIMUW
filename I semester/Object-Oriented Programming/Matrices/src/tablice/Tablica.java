package tablice;

// Główna klasa abstrakcyjna
public abstract class Tablica {

    public abstract int wymiar();
    public abstract int liczba_elementów();
    public abstract int[] kształt();
    public abstract Tablica kopia() throws ZłyIndeks;
    public abstract void transponuj();

    @Override
    public abstract String toString();

    public abstract Tablica suma(Tablica t) throws NiezgodneWymiaryException, ZłyIndeks;
    public abstract Tablica iloczyn(Tablica t) throws NiezgodneWymiaryException, ZłyIndeks;
    public abstract Tablica negacja() throws ZłyIndeks;

    public abstract void dodaj(Tablica t) throws NiezgodneWymiaryException, ZłyIndeks;
    public abstract void przemnóż(Tablica t) throws NiezgodneWymiaryException, ZłyIndeks;
    public abstract void zaneguj() throws ZłyIndeks;

    public abstract double daj(int... indeksy) throws ZłyIndeks;
    public abstract void ustaw(double wartosc, int... indeksy) throws ZłyIndeks;
    public abstract void przypisz(Tablica t) throws NiezgodneWymiaryException, ZłyIndeks;

    }

