package symulatorFarmera;

public class Dar {
	private RodzajDaru rodzajDaru;
	private int iloscDaru;

	public Dar(RodzajDaru rodzajDaru, int iloscDaru) {
		this.rodzajDaru = rodzajDaru;
		this.iloscDaru = iloscDaru;
	}

	public RodzajDaru getRodzajDaru() {
		return rodzajDaru;
	}

	public int getIloscDaru() {
		return iloscDaru;
	}

	@Override
	public String toString() {
		return iloscDaru + " " + rodzajDaru.toString().toLowerCase();
	}
}