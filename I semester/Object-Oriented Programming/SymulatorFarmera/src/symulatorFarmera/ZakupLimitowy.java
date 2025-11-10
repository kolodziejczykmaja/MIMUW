package symulatorFarmera;

/**
 * Strategia zakupu z limitem - farmer zawsze chce mieć określoną kwotę na koncie
 */
public class ZakupLimitowy extends StrategiaZakupu {
	private int limit;

	public ZakupLimitowy(int limit) {
		this.limit = limit;
	}

	@Override
	public boolean czyDacOferte(Zwierze zwierze, int aktualnaOferta,
								int stanKonta, int numerTury) {
		int nowaOferta = aktualnaOferta + 1;
		return (nowaOferta + limit <= stanKonta);
	}
}