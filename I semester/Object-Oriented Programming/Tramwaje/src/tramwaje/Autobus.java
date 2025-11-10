package tramwaje;

class Autobus extends PojazdKomunikacjiMiejskiej {
	private double zuzytePaliwoMiesiac;

	public Autobus(double szybkoscMaksymalna, String numer, double zuzytePaliwoMiesiac) {
		super(szybkoscMaksymalna, numer);
		if (zuzytePaliwoMiesiac < 0) {
			throw new IllegalArgumentException("Zużycie paliwa nie może być ujemne!");
		}
		this.zuzytePaliwoMiesiac = zuzytePaliwoMiesiac;
	}

	public double getZuzytePaliwoMiesiac() {
		return zuzytePaliwoMiesiac;
	}

	public void dodajZuzytePaliwo(double ilosc) {
		this.zuzytePaliwoMiesiac += ilosc;
	}

	@Override
	public String toString() {
		StringBuilder opis = new StringBuilder();
		opis.append("Autobus nr ").append(numer);
		opis.append(", szybkość maksymalna: ").append(szybkoscMaksymalna).append(" km/h");
		opis.append(", zużycie paliwa w tym miesiącu: ").append(zuzytePaliwoMiesiac).append(" l");
		if (zajezdnia != null) {
			opis.append(", zajezdnia: ").append(zajezdnia.getNazwa());
		}
		return opis.toString();
	}
}