package tramwaje;

abstract class Pojazd {
	protected double szybkoscMaksymalna;

	public Pojazd(double szybkoscMaksymalna) {
		this.szybkoscMaksymalna = szybkoscMaksymalna;
	}

	public double getSzybkoscMaksymalna() {
		return szybkoscMaksymalna;
	}

	@Override
	public abstract String toString();
}