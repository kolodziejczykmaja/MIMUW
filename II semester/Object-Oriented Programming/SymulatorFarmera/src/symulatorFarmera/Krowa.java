package symulatorFarmera;

public class Krowa extends Zwierze<Krowa> {

	public Krowa(String imie, int cenaBazowa, int wielkoscDaru) {
		super(imie, cenaBazowa, wielkoscDaru);
	}

	@Override
	public Dar wyprodukujDar() {
		return new Dar(RodzajDaru.MLEKO, wielkoscDaru);
	}
}