#!/bin/bash

mkdir -p ./in
mkdir -p ./out

echo "Testy 1-3: Testy przykladowe z moodla."

gcc @opcje kostka_rubika.c -o kostka_rubika

if [ $? -eq 0 ]; then
	echo "Kompilacja zakonczona sukcesem!"
else
	echo "Kompilacja dla N ndef nie powiodla sie :("
	exit 1
fi

for ((i = 1; i <= 3; i++)); do
	echo -n "Test $i: "

	valgrind --leak-check=full -q --error-exitcode=1 ./kostka_rubika < ./in/test$i.in > kostka.out

	if [ $? -eq 1 ]; then
		echo "Valgrind wykryl blad w twoim programie :(."
	else
		diff kostka.out ./out/test$i.out > /dev/null 2>&1
		if [ $? -eq 0 ]; then
			echo "OK."
		 elif [ $? -eq 1 ]; then
			echo "ZLE :(."
		else
			echo "BLAD?."
		fi
	fi
done

echo "Testy 4-10: Kostka 1 x 1 x 1."

gcc @opcje -DN=1 kostka_rubika.c -o kostka_rubika

if [ $? -eq 0 ]; then
	echo "Kompilacja zakonczona sukcesem!"
else
	echo "Kompilacja dla N=1 nie powiodla sie :("
	exit 1
fi

for ((i = 4; i <= 10; i++)); do
	echo -n "Test $i: "

	valgrind --leak-check=full -q --error-exitcode=1 ./kostka_rubika < ./in/test$i.in > kostka.out

	if [ $? -eq 1 ]; then
		echo "Valgrind wykryl blad w twoim programie :(."
	else
		diff kostka.out ./out/test$i.out > /dev/null 2>&1
		if [ $? -eq 0 ]; then
			echo "OK."
		 elif [ $? -eq 1 ]; then
			echo "ZLE :(."
		else
			echo "BLAD?."
		fi
	fi
done


echo "Testy 11-100: Kostka 2 x 2 x 2."

gcc @opcje -DN=2 kostka_rubika.c -o kostka_rubika

if [ $? -eq 0 ]; then
	echo "Kompilacja zakonczona sukcesem!"
else
	echo "Kompilacja dla N=2 nie powiodla sie :("
	exit 1
fi

for ((i = 11; i <= 100; i++)); do
	echo -n "Test $i: "

	valgrind --leak-check=full -q --error-exitcode=1 ./kostka_rubika < ./in/test$i.in > kostka.out

	if [ $? -eq 1 ]; then
		echo "Valgrind wykryl blad w twoim programie :(."
	else
		diff kostka.out ./out/test$i.out > /dev/null 2>&1
		if [ $? -eq 0 ]; then
			echo "OK."
		 elif [ $? -eq 1 ]; then
			echo "ZLE :(."
		else
			echo "BLAD?."
		fi
	fi
done

for ((N = 3; N <= 10; N++)); do

	dol=$((100*(N-2)+1))
	gora=$((100*(N-1)))
	echo "Testy $dol-$gora: Kostka $N x $N x $N."

	gcc @opcje -DN=$N kostka_rubika.c -o kostka_rubika

	if [ $? -eq 0 ]; then
		echo "Kompilacja zakonczona sukcesem!"
	else
		echo "Kompilacja dla N=$N nie powiodla sie :("
		exit 1
	fi

	for ((i = dol; i <= gora; i++)); do
		echo -n "Test $i: "

		valgrind --leak-check=full -q --error-exitcode=1 ./kostka_rubika < ./in/test$i.in > kostka.out

		if [ $? -eq 1 ]; then
			echo "Valgrind wykryl blad w twoim programie :(."
		else
			diff kostka.out ./out/test$i.out > /dev/null 2>&1
			if [ $? -eq 0 ]; then
				echo "OK."
			 elif [ $? -eq 1 ]; then
				echo "ZLE :(."
			else
				echo "BLAD?."
			fi
		fi
	done
done

echo "Testy 901-1000: Kostka 11 x 11 x 11. Kropka (znak konca komend) nie koniecznie w oddzielnej linii."
gcc @opcje -DN=11 kostka_rubika.c -o kostka_rubika

if [ $? -eq 0 ]; then
	echo "Kompilacja zakonczona sukcesem!"
else
	echo "Kompilacja dla N=11 nie powiodla sie :("
	exit 1
fi

for ((i = 901; i <= 1000; i++)); do
	echo -n "Test $i: "

	valgrind --leak-check=full -q --error-exitcode=1 ./kostka_rubika < ./in/test$i.in > kostka.out

	if [ $? -eq 1 ]; then
		echo "Valgrind wykryl blad w twoim programie :(."
	else
		diff kostka.out ./out/test$i.out > /dev/null 2>&1
		if [ $? -eq 0 ]; then
			echo "OK."
		 elif [ $? -eq 1 ]; then
			echo "ZLE :(."
		else
			echo "BLAD?."
		fi
	fi
done
