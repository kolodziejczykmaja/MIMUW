package symulatorFarmera;

import java.util.List;

public abstract class StrategiaZakupu {

	public void obejrzyjZwierzeta(List<? extends Zwierze> zwierzeta) {
	}

	public abstract boolean czyDacOferte(Zwierze zwierze, int aktualnaOferta,
										 int stanKonta, int numerTury);
}