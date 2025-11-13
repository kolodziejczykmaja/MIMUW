package symulatorFarmera;

import java.util.List;

public class SprzedazPowyzejBazowej extends StrategiaSprzedazy {

	@Override
	public Zwierze wybierzDoSprzedazy(List<? extends Zwierze> zwierzeta,
									  Cennik cennik, int numerTury) {
		return zwierzeta.stream()
				.filter(z -> cennik.wycenDar(z.wyprodukujDar()) > z.getCenaBazowa())
				.findFirst()
				.orElse(null);
	}
}