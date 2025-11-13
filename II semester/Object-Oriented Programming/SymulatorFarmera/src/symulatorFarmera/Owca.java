package symulatorFarmera;

public class Owca extends Zwierze<Owca> {

	public Owca(String imie, int cenaBazowa, int wielkoscDaru) {
		super(imie, cenaBazowa, wielkoscDaru);
	}

	@Override
	public Dar wyprodukujDar() {
		return new Dar(RodzajDaru.WELNA, wielkoscDaru);
	}
}