package tramwaje;

class ZajezdniaAutobusowa extends Zajezdnia {
	public ZajezdniaAutobusowa(String nazwa) {
		super(nazwa);
	}

	@Override
	protected boolean czyMozeDodacPojazd(PojazdKomunikacjiMiejskiej pojazd) {
		return pojazd instanceof Autobus;
	}

	@Override
	public String getTypZajezdni() {
		return "Autobusowa";
	}
}