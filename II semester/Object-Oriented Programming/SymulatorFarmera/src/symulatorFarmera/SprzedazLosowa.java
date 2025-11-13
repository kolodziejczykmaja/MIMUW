package symulatorFarmera;

import java.util.List;
import java.util.Random;

/**
 * Strategia losowej sprzedaży zwierząt
 */
public class SprzedazLosowa extends StrategiaSprzedazy {
	private Random random = new Random();

	@Override
	public Zwierze wybierzDoSprzedazy(List<? extends Zwierze> zwierzeta,
									  Cennik cennik, int numerTury) {
		if (zwierzeta.isEmpty()) return null;
		return zwierzeta.get(random.nextInt(zwierzeta.size()));
	}
}