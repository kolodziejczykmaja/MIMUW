package symulatorFarmera;

public class Kura extends Zwierze<Kura> {

	public Kura(String imie, int cenaBazowa, int wielkoscDaru) {
		super(imie, cenaBazowa, wielkoscDaru);
	}

	@Override
	public Dar wyprodukujDar() {
		return new Dar(RodzajDaru.JAJA, wielkoscDaru);
	}
}