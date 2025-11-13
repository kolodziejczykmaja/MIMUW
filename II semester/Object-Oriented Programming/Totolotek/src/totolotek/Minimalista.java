package totolotek;

public class Minimalista extends Gracz {

	private final Kolektura ulubionaKolektura;

	public Minimalista(String imie, String nazwisko, String pesel, long srodki, Kolektura ulubionaKolektura) {
		super(imie, nazwisko, pesel, srodki);
		if (ulubionaKolektura == null) {
			throw new IllegalArgumentException("Taka kolektura nie istnieje");
		}
		this.ulubionaKolektura = ulubionaKolektura;
	}

	public void kupKupon() {
		if (!czyMoznaKupicKupon(CENA_ZAKLADU)) {
			return;
		}
		Kupon kupon = ulubionaKolektura.generujKuponNaChybilTrafil(1,
				new int[0]);
		pobierzSrodki(CENA_ZAKLADU);
		dodajKupon(kupon);
	}

	@Override
	public String toString() {
		return super.toString() + "\nTyp gracza: Minimalista" +
				"\nUlubiona kolektura: " + ulubionaKolektura.getNumerKolektury();
	}
}