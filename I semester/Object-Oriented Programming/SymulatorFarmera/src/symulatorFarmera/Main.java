package symulatorFarmera;

import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		// Główna symulacja
		uruchomSymulacje();
	}

	private static void uruchomSymulacje() {
		System.out.println("\n--- GŁÓWNA SYMULACJA ---");

		// Tworzenie farmerów z różnymi strategiami
		List<Farmer> farmerzy = Arrays.asList(
				new Farmer("Anna Limitowa",
						new ZakupLimitowy(30),
						new SprzedazLosowa()),

				new Farmer("Bartek Drogiusz",
						new ZakupNajdrozszy(),
						new SprzedazMaxZysk()),

				new Farmer("Celina Skomplikowana",
						new ZakupSkomplikowany(),
						new SprzedazPowyzejBazowej()),

				new Farmer("Darek Oszczędny",
						new ZakupLimitowy(20),
						new SprzedazMaxZysk())
		);

		// Tworzenie rozgrywki
		Rozgrywka rozgrywka = new Rozgrywka(farmerzy, 3);

		// Dodanie zwierząt na sprzedaż (różne typy i ceny)
		rozgrywka.dodajZwierzeNaSprzedaz(new Kura("Kwoka", 25, 8));
		rozgrywka.dodajZwierzeNaSprzedaz(new Kura("Ryba", 30, 12));
		rozgrywka.dodajZwierzeNaSprzedaz(new Kura("Kokoszka", 20, 6));

		rozgrywka.dodajZwierzeNaSprzedaz(new Owca("Baranek", 45, 6));
		rozgrywka.dodajZwierzeNaSprzedaz(new Owca("Owcza", 50, 7));
		rozgrywka.dodajZwierzeNaSprzedaz(new Owca("Wełniak", 40, 8));

		rozgrywka.dodajZwierzeNaSprzedaz(new Krowa("Mućka", 70, 15));
		rozgrywka.dodajZwierzeNaSprzedaz(new Krowa("Bela", 75, 18));
		rozgrywka.dodajZwierzeNaSprzedaz(new Krowa("Milka", 65, 12));

		// Test strategii "skomplikowanej" - dodaj drogie zwierzę > 77
		rozgrywka.dodajZwierzeNaSprzedaz(new Krowa("Droga Krowa", 85, 20));

		// Uruchomienie symulacji
		rozgrywka.przeprowadz();
	}
}