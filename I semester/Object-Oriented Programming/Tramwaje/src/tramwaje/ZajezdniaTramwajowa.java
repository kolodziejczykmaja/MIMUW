package tramwaje;

class ZajezdniaTramwajowa<Tramwaj> extends Zajezdnia {
	public ZajezdniaTramwajowa(String nazwa) {
		super(nazwa);
	}

	@Override
	protected boolean czyMozeDodacPojazd(PojazdKomunikacjiMiejskiej pojazd) {
		return pojazd instanceof Tramwaj;
	}

	@Override
	public String getTypZajezdni() {
		return "Tramwajowa";
	}
}