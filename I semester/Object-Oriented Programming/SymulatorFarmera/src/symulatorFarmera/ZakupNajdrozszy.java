package symulatorFarmera;

import java.util.List;

/**
 * Strategia zakupu najdroższych zwierząt
 */
public class ZakupNajdrozszy extends StrategiaZakupu {
	private int najwyzszaCena = 0;

	@Override
	public void obejrzyjZwierzeta(List<? extends Zwierze> zwierzeta) {
		najwyzszaCena = zwierzeta.stream()
				.mapToInt(Zwierze::getCenaBazowa)
				.max()
				.orElse(0);
	}

	@Override
	public boolean czyDacOferte(Zwierze zwierze, int aktualnaOferta,
								int stanKonta, int numerTury) {
		if (zwierze.getCenaBazowa() == najwyzszaCena) {
			int nowaOferta = aktualnaOferta + 1;
			return nowaOferta <= stanKonta;
		}
		return false;
	}
}