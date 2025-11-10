package symulatorFarmera;

import java.util.*;
import java.util.stream.Collectors;


public class Rozgrywka {
	private List<Farmer> farmerzy;
	private Cennik cennik;
	private List<Zwierze> zwierzetaNaSprzedaz;
	private int liczbaTur;
	private Random random;

	public Rozgrywka(List<Farmer> farmerzy, int liczbaTur) {
		this.farmerzy = new ArrayList<>(farmerzy);
		this.cennik = new Cennik();
		this.zwierzetaNaSprzedaz = new ArrayList<>();
		this.liczbaTur = liczbaTur;
		this.random = new Random();
	}

	//Główna metoda przeprowadzająca całą symulację
	public void przeprowadz() {

		for (int tura = 1; tura <= liczbaTur; tura++) {

			// Farmerzy oglądają dostępne zwierzęta
			for (Farmer farmer : farmerzy) {
				farmer.obejrzyjZwierzeta(zwierzetaNaSprzedaz);
			}

			// Faza kupowania
			fazaKupowania(tura);

			// Faza sprzedawania
			fazaSprzedawania(tura);

			// Podsumowanie tury
			podsumowanieTury(tura);
		}

		// Wyniki końcowe
		wyniki();
	}

	public void dodajZwierzeNaSprzedaz(Zwierze zwierze) {
		zwierzetaNaSprzedaz.add(zwierze);
	}

	private void fazaKupowania(int numerTury) {

		// Losowa kolejność farmerów
		List<Farmer> kolejnosc = new ArrayList<>(farmerzy);
		Collections.shuffle(kolejnosc, random);

		// Licytacja każdego zwierzęcia
		for (Zwierze zwierze : new ArrayList<>(zwierzetaNaSprzedaz)) {
			licytujZwierze(zwierze, numerTury, kolejnosc);
		}
	}

	private void fazaSprzedawania(int numerTury) {

		// Aktualizuj cennik
		cennik.aktualizuj();

		// Farmerzy wybierają zwierzęta do sprzedaży
		Map<Farmer, Zwierze> wybory = new HashMap<>();
		for (Farmer farmer : farmerzy) {
			Zwierze wybrane = farmer.wybierzDoSprzedazy(cennik, numerTury);
			if (wybrane != null) {
				wybory.put(farmer, wybrane);
			}
		}

		// Znajdź maksymalny zysk
		int maksymalnyZysk = wybory.values().stream()
				.mapToInt(z -> cennik.wycenDar(z.wyprodukujDar()))
				.max()
				.orElse(0);

		// Wypłać zyski i bonusy
		for (Map.Entry<Farmer, Zwierze> entry : wybory.entrySet()) {
			Farmer farmer = entry.getKey();
			Zwierze zwierze = entry.getValue();
			int zysk = cennik.wycenDar(zwierze.wyprodukujDar());

			farmer.setStanKonta(farmer.getStanKonta() + zysk);

			// Bonus za najlepszy wynik
			if (zysk == maksymalnyZysk) {
				farmer.setStanKonta(farmer.getStanKonta() + 50);
			}

			// Usuń zwierzę z farmy i dodaj z powrotem na sprzedaż
			farmer.sprzedajZwierze(zwierze);
			zwierzetaNaSprzedaz.add(zwierze);
		}
	}

	private void licytujZwierze(Zwierze zwierze, int numerTury, List<Farmer> kolejnosc) {

		int aktualnaOferta = zwierze.getCenaBazowa();
		Farmer najlepszyLicytant = null;
		boolean bylLicytant = false;

		// Każdy farmer może licytować wiele razy, aż nikt nie podniesie
		while (true) {
			boolean ktosPodniosl = false;

			for (Farmer farmer : kolejnosc) {
				if (farmer.czyDacOferte(zwierze, aktualnaOferta, numerTury)) {
					aktualnaOferta++;
					najlepszyLicytant = farmer;
					ktosPodniosl = true;
					bylLicytant = true;
				}
			}

			if (!ktosPodniosl) break; // Nikt nie podniósł, koniec licytacji
		}

		// Finalizacja licytacji
		if (bylLicytant && najlepszyLicytant != null) {
			najlepszyLicytant.setStanKonta(najlepszyLicytant.getStanKonta() - aktualnaOferta);
			najlepszyLicytant.przyjmijZwierze(zwierze);
			zwierzetaNaSprzedaz.remove(zwierze);
		}
	}

	private void podsumowanieTury(int numerTury) {
		System.out.println("  STAN PO TURZE " + numerTury + ":");
		for (Farmer farmer : farmerzy) {
			System.out.println("    " + farmer);
		}
	}

	private void wyniki() {

		farmerzy.sort((f1, f2) -> Integer.compare(f2.getStanKonta(), f1.getStanKonta()));

		for (int i = 0; i < farmerzy.size(); i++) {
			Farmer farmer = farmerzy.get(i);
			System.out.println((i + 1) + ". " + farmer);
		}

		System.out.println("\nZWYCIĘZCA: " + farmerzy.get(0).getImie() +
				" z " + farmerzy.get(0).getStanKonta() + " kg złota!");
	}
}