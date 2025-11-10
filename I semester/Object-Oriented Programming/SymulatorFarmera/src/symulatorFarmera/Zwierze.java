package symulatorFarmera;

/**
 * Abstrakcyjna klasa bazowa dla wszystkich zwierząt z generykami (BONUS)
 */
public abstract class Zwierze<T extends Zwierze<T>> {
	protected String imie;
	protected int cenaBazowa;
	protected int wielkoscDaru;

	public Zwierze(String imie, int cenaBazowa, int wielkoscDaru) {
		this.imie = imie;
		this.cenaBazowa = cenaBazowa;
		this.wielkoscDaru = wielkoscDaru;
	}

	public String getImie() {
		return imie;
	}

	public int getCenaBazowa() {
		return cenaBazowa;
	}

	public int getWielkoscDaru() {
		return wielkoscDaru;
	}

	/**
	 * Abstrakcyjna metoda - każde zwierzę produkuje inny rodzaj daru
	 */
	public abstract Dar wyprodukujDar();

	/**
	 * BONUS: porównywanie darów zwierząt tego samego typu
	 */
	public int porownajDar(T inne) {
		return Integer.compare(this.wielkoscDaru, inne.wielkoscDaru);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + imie +
				" (cena: " + cenaBazowa + ", dar: " + wielkoscDaru + ")";
	}
}