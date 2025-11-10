package symulatorFarmera;

import java.util.ArrayList;
import java.util.List;

public class Farmer {
	private String imie;
	private int stanKonta;
	private List<Zwierze> zwierzeta;
	private StrategiaZakupu strategiaZakupu;
	private StrategiaSprzedazy strategiaSprzedazy;

	public Farmer(String imie, StrategiaZakupu strategiaZakupu,
				  StrategiaSprzedazy strategiaSprzedazy) {
		this.imie = imie;
		this.stanKonta = 100; // startowe 100 kg złota
		this.zwierzeta = new ArrayList<>();
		this.strategiaZakupu = strategiaZakupu;
		this.strategiaSprzedazy = strategiaSprzedazy;
	}

	public String getImie() {
		return imie;
	}

	public int getStanKonta() {
		return stanKonta;
	}

	public List<Zwierze> getZwierzeta() {
		return new ArrayList<>(zwierzeta);
	}

	public void setStanKonta(int kwota) {
		this.stanKonta = kwota;
	}

	// Główne metody farmera
	public void obejrzyjZwierzeta(List<? extends Zwierze> dostepne) {
		strategiaZakupu.obejrzyjZwierzeta(dostepne);
	}

	public boolean czyDacOferte(Zwierze zwierze, int aktualnaOferta, int numerTury) {
		return strategiaZakupu.czyDacOferte(zwierze, aktualnaOferta, stanKonta, numerTury);
	}

	public Zwierze wybierzDoSprzedazy(Cennik cennik, int numerTury) {
		return strategiaSprzedazy.wybierzDoSprzedazy(zwierzeta, cennik, numerTury);
	}

	public void przyjmijZwierze(Zwierze zwierze) {
		zwierzeta.add(zwierze);
	}

	public void sprzedajZwierze(Zwierze zwierze) {
		zwierzeta.remove(zwierze);
	}

	@Override
	public String toString() {
		return imie + " (konto: " + stanKonta + " kg złota, zwierząt: " + zwierzeta.size() + ")";
	}
}