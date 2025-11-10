package tramwaje;

class Tramwaj extends PojazdKomunikacjiMiejskiej {
	private int liczbaWagonow;

	public Tramwaj(double szybkoscMaksymalna, String numer, int liczbaWagonow) {
		super(szybkoscMaksymalna, numer);
		if (liczbaWagonow < 1 || liczbaWagonow > 3) {
			throw new IllegalArgumentException("Tramwaj może mieć od 1 do 3 wagonów");
		}
		this.liczbaWagonow = liczbaWagonow;
	}

	public int getLiczbaWagonow() {
		return liczbaWagonow;
	}

	@Override
	public String toString() {
		StringBuilder opis = new StringBuilder();
		opis.append("Tramwaj nr ").append(numer);
		opis.append(", szybkość maksymalna: ").append(szybkoscMaksymalna).append(" km/h");
		opis.append(", liczba wagonów: ").append(liczbaWagonow);
		if (zajezdnia != null) {
			opis.append(", zajezdnia: ").append(zajezdnia.getNazwa());
		}
		return opis.toString();
	}
}