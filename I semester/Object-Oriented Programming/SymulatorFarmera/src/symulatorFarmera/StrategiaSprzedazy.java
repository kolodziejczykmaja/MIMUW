package symulatorFarmera;

import java.util.List;

public abstract class StrategiaSprzedazy {

	public abstract Zwierze wybierzDoSprzedazy(List<? extends Zwierze> zwierzeta,
											   Cennik cennik, int numerTury);
}