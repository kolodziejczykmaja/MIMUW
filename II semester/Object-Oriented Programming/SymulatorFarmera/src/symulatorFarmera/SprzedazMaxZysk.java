package symulatorFarmera;

import java.util.Comparator;
import java.util.List;

/**
 * Strategia sprzedaży zwierzęcia z maksymalnym zyskiem
 */
public class SprzedazMaxZysk extends StrategiaSprzedazy {

	@Override
	public Zwierze wybierzDoSprzedazy(List<? extends Zwierze> zwierzeta,
									  Cennik cennik, int numerTury) {
		return zwierzeta.stream()
				.max(Comparator.comparingInt(z -> cennik.wycenDar(z.wyprodukujDar())))
				.orElse(null);
	}
}