package totolotek;

public class BudzetPanstwa {
	private static long podatki = 0;
	private static long subwencje = 0;

	public static void dodajPodatek(long kwota) {
		podatki += kwota;
	}

	public static void przekazSubwencje(long kwota) {
		subwencje += kwota;
	}

	public static String getStan() {
		return String.format("Podatki:%s Subwencje:%s", formatujKwote(podatki), formatujKwote(subwencje));
	}

	private static String formatujKwote(long kwotaWGroszach) {
		return String.format("%,d zł %02d gr", kwotaWGroszach / 100, kwotaWGroszach % 100);
	}
}