package tramwaje;

public class Main {
	public static void main(String[] args) {

		// Tworzenie zajezdni
		ZajezdniaTramwajowa zajezdniaTram1 = new ZajezdniaTramwajowa("Zajezdnia Tramwajowa Główna");
		ZajezdniaTramwajowa zajezdniaTram2 = new ZajezdniaTramwajowa("Zajezdnia Tramwajowa Wschód");
		ZajezdniaAutobusowa zajezdniaAuto1 = new ZajezdniaAutobusowa("Zajezdnia Autobusowa Centrum");
		ZajezdniaAutobusowa zajezdniaAuto2 = new ZajezdniaAutobusowa("Zajezdnia Autobusowa Południe");

		// Tworzenie tramwajów
		Tramwaj tramwaj1 = new Tramwaj(70.0, "T-001", 2);
		Tramwaj tramwaj2 = new Tramwaj(65.0, "T-002", 3);
		Tramwaj tramwaj3 = new Tramwaj(75.0, "T-003", 1);
		Tramwaj tramwaj4 = new Tramwaj(68.0, "T-004", 2);

		// Tworzenie autobusów
		Autobus autobus1 = new Autobus(90.0, "A-101", 850.5);
		Autobus autobus2 = new Autobus(85.0, "A-102", 920.3);
		Autobus autobus3 = new Autobus(88.0, "A-103", 780.8);
		Autobus autobus4 = new Autobus(92.0, "A-104", 1050.2);

		// Przydzielanie pojazdów do zajezdni
		try {
			zajezdniaTram1.dodajPojazd(tramwaj1);
			zajezdniaTram1.dodajPojazd(tramwaj2);
			zajezdniaTram2.dodajPojazd(tramwaj3);
			zajezdniaTram2.dodajPojazd(tramwaj4);

			zajezdniaAuto1.dodajPojazd(autobus1);
			zajezdniaAuto1.dodajPojazd(autobus2);
			zajezdniaAuto2.dodajPojazd(autobus3);
			zajezdniaAuto2.dodajPojazd(autobus4);

		} catch (IllegalArgumentException e) {
			System.err.println("Błąd podczas przydzielania pojazdów: " + e.getMessage());
		}

		// Wyświetlanie opisów zajezdni
		System.out.println(zajezdniaTram1.toString());

		System.out.println(zajezdniaTram2.toString());

		System.out.println(zajezdniaAuto1.toString());

		System.out.println(zajezdniaAuto2.toString());

		// Przenoszenie pojazdu między zajezdniami tego samego typu
		zajezdniaTram2.usunPojazd(tramwaj3);
		zajezdniaTram1.dodajPojazd(autobus1);

		System.out.println("Po przeniesieniu:");
		System.out.println(zajezdniaTram1.toString());
		System.out.println(zajezdniaTram2.toString());

		System.out.println("\nTEST BŁĘDNEGO PRZYDZIAŁU");
		try {
			zajezdniaTram1.dodajPojazd(autobus1);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

		try {
			zajezdniaTram2.dodajPojazd(tramwaj1); // tramwaj1 już jest w zajezdniaTram1
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
}