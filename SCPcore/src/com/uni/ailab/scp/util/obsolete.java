package com.uni.ailab.scp.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.util.Log;

import com.uni.ailab.scp.util.ScpConstant;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("unused")
public class obsolete
{
	private HashMap<Integer, Integer> map;
	private ArrayList<Integer> mapb;

	String fakeStatePermissions = "1 0 2 0 3 0 4 0 5 0 6 0 7 0 8 0 9 0 10 0 11 0 12 0 13 0 14 0 15 0 16 0 17 0 18 0 19 0 20 0 21 0 22 0 23 0 24 0 25 0 26 0 27 0 28 0 29 0 29 0 30 0";
	
	
	public obsolete()
	{
		map = new HashMap<Integer, Integer>();
		mapb = new ArrayList<Integer>();
	}
	
	/*
	 * Metodo usato per comprimere le cnf in modo che contengano solo quei
	 * permessi che compaiono nelle politiche e nei permessi della componente =>
	 * per ridurre il numero di variabili su cui il sat solver dovrà lavorare.
	 * 
	 * DATO CHE QUESTO CALCOLO È ONEROSO E CHE MINISAT MINIMIZZA GIA DI SUO IL
	 * NUMERO DI PERMESSI A TRUE, NON UTILIZZIAMO PIU QUESTI METODI
	 * 
	 * Per prima cosa, dato che vogliamo far eseguire minisat SOLO sul set di
	 * permessi che compaiono nelle permission e nelle policy, popoliamo un
	 * dizionario per mappare i permessi nelle variabili da inviare a minisat:
	 */

	private String parseString(String string)
	{
		String zero = " 0";
		String space = " ";
		String cnf = fakeStatePermissions + space + string;

		map.clear();
		mapb.clear();

		// inserisco un primo elemento in quanto poi non potro utilizzare lo
		// zero come chiave..
		mapb.add(0);

		// string += fakeStatePermissions;

		// String clauses[] = policy.split(splitter);
		String litterals[] = cnf.split(space);

		Integer value = 1;
		Integer clausesCount = 0;
		Integer key = null;
		Integer count = 1;
		Integer abskey = null;
		String parsedPolicy = "";

		for (String s : litterals)
		{
			key = Integer.parseInt(s);
			if (key == 0)
			{
				parsedPolicy += zero;
				clausesCount++;
				continue;
			}

			abskey = Math.abs(key);
			value = map.get(abskey);

			if (value == null)
			{
				value = count;
				map.put(abskey, value);
				mapb.add(value, abskey);
				count++;
			}

			value = value * Integer.signum(key);

			parsedPolicy += space + value;
		}

		return map.size() + space + clausesCount + zero + parsedPolicy;
	}

	/**
	 * Metodo usato per deparsare le cnf => decomprimerle.
	 * 
	 * @param rescnf
	 * @return
	 */
	
	private String deParseCnf(String rescnf)
	{
		// devo scorre ogni letterale e sostituirlo con il suo valore presente
		// nel dizionario:
		String zero = " 0";
		String space = " ";

		// String clauses[] = policy.split(splitter);
		String litterals[] = rescnf.split(space);

		Integer value = 1;

		Integer key = null;
		Integer count = 1;
		Integer abskey = null;
		String parsedPolicy = "";

		for (String s : litterals)
		{
			key = Integer.parseInt(s);
			if (key == 0)
			{
				parsedPolicy += zero;
				continue;
			}

			abskey = Math.abs(key);

			value = mapb.get(abskey);

			if (value == null)
				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ERRORE TREMENDO");

			value = value * Integer.signum(key);

			parsedPolicy += space + value;
		}

		return parsedPolicy;
	}
}
