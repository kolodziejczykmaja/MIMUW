package mastermind;

import java.util.*;

public class AlgorytmProsty {
	private MastermindGra gra;
	private List<int[]> mozliweKombinacje;

	public AlgorytmProsty(MastermindGra gra) {
		this.gra = gra;
		this.mozliweKombinacje = gra.wygenerujWszystkieKombinacje();
	}

	public int[] nastepnaProba() {
		if (mozliweKombinacje.isEmpty()) {
			return null;
		}
		return mozliweKombinacje.getFirst().clone();
	}

	public void zaktualizujPoOdpowiedzi(int[] proba, Odpowiedz odpowiedz) {
		mozliweKombinacje = gra.filtrujKombinacje(mozliweKombinacje, proba, odpowiedz);
	}
}