package symulatorFarmera;


// Skomplikowana strategia zakupu - tylko pierwsza tura, najdroższe ≤ 77

public class ZakupSkomplikowany extends StrategiaZakupu {

	@Override
	public boolean czyDacOferte(Zwierze zwierze, int aktualnaOferta,
								int stanKonta, int numerTury) {
		// Strategia: kupuj tylko w pierwszej turze, tylko najdroższe ≤ 77
		if (numerTury == 1 && zwierze.getCenaBazowa() <= 77) {
			int nowaOferta = aktualnaOferta + 1;
			return nowaOferta <= stanKonta;
		}
		return false;
	}
}