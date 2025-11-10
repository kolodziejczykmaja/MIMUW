package totolotek;

import java.util.*;

public final class Zaklad {
	private static final int ILOSC_LICZB_W_ZAKLADZIE = 6;

	private final int[] liczby;

	public Zaklad(int[] liczby) {
		this.liczby = Arrays.copyOf(liczby, ILOSC_LICZB_W_ZAKLADZIE);
			Arrays.sort(this.liczby);
	}

	public int[] getLiczby() {
		return liczby;
	}
}