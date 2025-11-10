package tramwaje;

abstract class PojazdKomunikacjiMiejskiej extends Pojazd {
	protected String numer;
	protected Zajezdnia zajezdnia;

	public PojazdKomunikacjiMiejskiej(double szybkoscMaksymalna, String numer) {
		super(szybkoscMaksymalna);
		this.numer = numer;
	}

	public String getNumer() {
		return numer;
	}

	public Zajezdnia getZajezdnia() {
		return zajezdnia;
	}

	public void setZajezdnia(Zajezdnia zajezdnia) {
		this.zajezdnia = zajezdnia;
	}

	@Override
	public abstract String toString();
}