package com.uni.ailab.scp.provider;

import java.util.ArrayList;
import java.util.Random;
import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import com.uni.ailab.scp.util.ScpConstant;

@SuppressLint("UseSparseArrays")
public class ScpProvider extends ContentProvider
{

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int COMPONENT_REQUESTED = 1;
	private static final int COMPONENT_ADDED = 2;
	private static final int COMPONENT_REMOVED = 3;
	private static final int COMPONENT_ROOT_ADDED = 4;
	private static final int COMPONENT_PROIDER_ADDED = 5;

	private static Random rand = new Random();

	private ScpForest forest;
	private StacksSet stacksSet;

	// lista delle componenti non assegnabili (deciso da sviluppatore)
	private ArrayList<Integer> notAssignable;
	// numero massimo di permessi assegnabili a una componente (deciso da
	// sviluppatore)
	private int maxAssignable;

	private static ArrayList<Integer> currentState = new ArrayList<Integer>() {

		private static final long serialVersionUID = 1L;

		{
			add(1);
			add(2);
			add(3);
			add(4);
			add(5);
			add(6);
			add(7);
			add(8);
			add(9);
			add(10);
			add(11);
			add(12);
			add(13);
			add(14);
			add(15);
			add(16);
			add(17);
			add(18);
			add(19);
			add(20);
			add(21);
			add(22);
			add(23);
			add(24);
			add(25);
			add(26);
			add(27);
			add(28);
			add(29);
			add(30);
		}
	};

	String fakeStatePermissions = "1 0 2 0 3 0 4 0 5 0 6 0 7 0 8 0 9 0 10 0 11 0 12 0 13 0 14 0 15 0 16 0 17 0 18 0 19 0 20 0 21 0 22 0 23 0 24 0 25 0 26 0 27 0 28 0 29 0 29 0 30 0";

	static
	{
		System.loadLibrary("minisat");
		System.loadLibrary("sqlite");
		System.loadLibrary("scplib");
	}

	public static native ArrayList<Component> query(String componentName);

	// TODO: static??
	public native String minisatJNI(String cnf, int debug);

	static
	{
		sURIMatcher.addURI("com.uni.ailab.scp.provider", "component",
				COMPONENT_REQUESTED);
		sURIMatcher.addURI("com.uni.ailab.scp.provider", "add_component",
				COMPONENT_ADDED);
		sURIMatcher.addURI("com.uni.ailab.scp.provider", "remove_component",
				COMPONENT_REMOVED);
		sURIMatcher.addURI("com.uni.ailab.scp.provider", "add_root_component",
				COMPONENT_ROOT_ADDED);
	}

	@Override
	public boolean onCreate()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpProvider: onCreate");

		// Qua posso mettere il caricamento della foresta da un db o da un file
		// e se non trovo da caricare la inizializzo nuovamente

		forest = new ScpForest();
		stacksSet = new StacksSet();

		return true;
	}

	private String obtainCnf(int dadStackId, Component component)
	{
		/*
		 * Sia permission che policy sono stringhe di clausole separate da zeri;
		 * dato che minisat prende come ingresso numerovariabili numeroclausole,
		 * devo, sulla base delle cnf da verificare, creare un'opportuna
		 * intestazione
		 */

		String splitter = " 0 ";

		/*
		 * passo la componente all'insieme di stack in modo da farmi ritornare
		 * la cnf da mandare al sat solver
		 */
		String cnf = stacksSet.getCnf(dadStackId, stacksSet.parse(component));

		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
				"ScpProvider: cnf da parsare per minisat: " + cnf);

		// String statePolicy = fakeStatePermissions;
		// String cnf = statePolicy + " " + permission + " " + policy;

		String cnfCount[] = cnf.split(splitter);
		int clausesNumber = cnfCount.length;

		String variableCount[];
		int max = 0;
		int o = 0;
		// conto il numero di variabili
		for (String s : cnfCount)
		{
			variableCount = s.split(" ");

			for (String k : variableCount)
			{
				if (k.equals(""))
					continue;
				if (k.equals("0"))
					continue;
				o = Math.abs(Integer.valueOf(k));
				if (o > max)
					max = o;
			}
		}

		/*
		 * Noi vogliamo lasciare la possibilità di aggiungere permessi
		 * all'utente => non dobbiamo mettere a not i permessi non posseduti da
		 * un'app. Vantaggi? beh, ad esempio, una componente sa che prima o poi
		 * gli servirà un certo permesso per poter invocare un servizio esterno
		 * => la richiede in modo da avercela già. String cnf2 = " " + cnf;
		 * 
		 * for (int i = 1; i <= max; i++) { if (!cnf2.contains(" " + i + " ")) {
		 * Log.i("MALE", "occorrenza " + i); cnf += "-" + i + " 0 "; } }
		 */

		return max + " " + clausesNumber + " 0 " + cnf;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "query: " + uri.toString());

		// prova per vedere se funzica
		notAssignable = new ArrayList<>();
		notAssignable.add(97);

		/*
		 * Creo una lista che andrà a contenere l'elenco dei permessi richiesti
		 * ma che non sono assegnabili
		 */
		String n_a_p = "";

		// definisco le colonne del cursore che andrò a ritornare
		String[] columns = { "_id", "package", "className", "SCPID",
				"permissionCount", "stackId", "notAssignable" };
		MatrixCursor retValue = new MatrixCursor(columns);

		int match = sURIMatcher.match(uri);
		switch (match)
		{
		case COMPONENT_REQUESTED:

			ArrayList<Component> componentList = null;
			Component component = null;
			int dadStackId = 0;

			// Recupero il campo WHOIAM assegnato dalla libreria;
			String fragment = uri.getFragment();

			/*
			 * Stabilisco che per poter avviare una componente essa debba già
			 * essersi "registrata" ad SCP => in possesso di un id che qua
			 * ricevo tramite fragment
			 */
			if (fragment == null)
			{
				throw new NullPointerException("Campo whoIam nullo");
			}

			dadStackId = Integer.parseInt(fragment);

			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"query COMPONENT_REQUESTED. Caller id: " + dadStackId
							+ " selection: " + selection);

			componentList = query(selection);

			/*
			 * Se non ho avuto problemi con la chiamata nativa e se ho trovato
			 * le componenti
			 */
			if ((componentList != null) && (componentList.size() != 0))
			{
				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"query COMPONENT_REQUESTED. il db ha restituito "
								+ componentList.size() + " componenti");

				String cnf, rescnf;

				int randId = rand.nextInt();
				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"query COMPONENT_REQUESTED. new randomId " + randId);

				// ciclo sulle componenti ottenute dal db
				for (int i = 0; i < componentList.size(); i++)
				{
					component = componentList.get(i);
					if (component != null)
					{
						// preparo la cnf da inviare al SAT-SOLVER
						cnf = obtainCnf(dadStackId, component);
						Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
								"query COMPONENT_REQUESTED.componente "
										+ component.toString()
										+ " cnf da checkkare " + cnf);

						rescnf = minisatJNI(cnf, 0);

						if (!rescnf.contains("UNSAT"))
						{
							/*
							 * a questo punto devo fare la differenza tra il
							 * numero di permessi
							 */
							// int permissionCount = checkres(rescnf, n_a_p);
							resBox box = checkres(rescnf);

							Log.i("SCPTEST", "res: '" + rescnf + "'");

							// UTILIZZATO PER CSV
							Log.i("SCPTEST",
									"ScpProvider ScpProvider Componente " + i
											+ " NumeroPermessiNecessari "
											+ box.getCount());

							retValue.addRow(new Object[] { component.getId(),
									component.getPackageName(),
									component.getClassName(), randId,
									box.getCount(), dadStackId, box.getNap() });
						}
						else
						{
							Log.i("SCPTEST", "res: '" + rescnf + "'");

							Log.i("SCPTEST",
									"ScpProvider ScpProvider Componente " + i
											+ " NumeroPermessiNecessari NULL");
						}
					}
				}

				// se ho componenti SAT
				if (retValue.getCount() != 0)
				{
					/*
					 * TODO: anche se ho solamente una componente, non inserisco
					 * la component nel nodo perke non posso essere sicuro che
					 * verrà effettivamente invocata (potrebbe succedere qlcs
					 * che impedisce il corretto avvio della componente da parte
					 * del client) quindi inserisco una voce "vuota"
					 * nell'albero, da completare non appena la componente
					 * richiesta chiamerà il suo metodo onCreate.
					 */

					// dato che devo poter controllare se la componente è un
					// service, devo passare il tipo.
					stacksSet.pushComponent(dadStackId, randId, new Component(
							component.getType()));
				}
			}
			else
			{
				throw new NullPointerException(
						"Nessuna componente trovata nel db");
			}
			break;
		default:
			break;
		}

		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
				"ScpProvider:" + stacksSet.getStacksSetStatus());

		return retValue;
	}

	private resBox checkres(String rescnf)
	{
		String space = " ";
		resBox retValue = new resBox();

		// String clauses[] = policy.split(splitter);
		String litterals[] = rescnf.split(space);

		Integer key = null;
		Integer count = 0;

		for (String s : litterals)
		{
			if (s.equals(""))
				continue;
			key = Integer.parseInt(s);

			// TODO: qua potresti mettere <=0
			// if (key == 0)
			if (key <= 0)
			{
				continue;
			}
			else if (key > 0)
			{
				// se il permesso è tra quelli non assegnabili,
				if (notAssignable.contains(key))
				{
					// non puoi tornare numeri..devi tornare stringhe
					// comprensibili

					retValue.putPermission(String.valueOf(key));
				}

				if (!currentState.contains(key))
				{
					// count++;
					retValue.cntIncrement();
				}

				// if (!fakeStatePermissions.contains(" " + Math.abs(key) +
				// " "))
				// {
				// count++;
				// }
			}
		}
		// return count;
		return retValue;
	}

	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "Insert");
		ArrayList<Component> cl = null;

		// prendo i parametri
		String className = values.getAsString("COMPONENT_NAME");
		int nodeId = values.getAsInteger("SCP_WHOIAM");

		if (className == null)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "Insert, class null");
			throw new IllegalArgumentException(
					"className argument mustn't be null");
		}

		// preparo la query al DB
		String selection = ScpConstant.COLUMN_NAME + " = '" + className + "'";
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "Insert, selection " + selection);

		int match = sURIMatcher.match(uri);
		switch (match)
		{
		/*
		 * Caso in cui una componente sicura è stata avviata => devo recuperare
		 * le info dal db ed andare ad aggiornare i valori della componente
		 * sullo stack
		 */
		case COMPONENT_ADDED:
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "Insert, COMPONENT_ADDED");

			int myStackId = values.getAsInteger("SCP_MYSTACKID");

			/*
			 * TODO: potrei pensare di creare un altro metodo nativo cosi
			 * anzichè stare a passare un intera lista passo solamente un
			 * oggetto (dovresti guadagnare qlcs in termini di numero di
			 * chiamate JNI => più efficiente). Inoltre, ricordati che fare la
			 * query all'interno dei casi e non esternamente, ti permette di
			 * guadagnare qlcs in sicurezza!! ti potrebbero lanciare chiamate a
			 * schifo con query malevole!
			 */
			cl = query(selection);

			if ((cl != null) && (cl.size() == 1))
			{
				stacksSet.updateComponent(myStackId, nodeId, cl.get(0));

				// preparo il messaggio di risposta
				String URL = "content://com.uni.ailab.scp.provider/add_component";
				Uri scpUri = Uri.parse(URL);
				Uri _uri = ContentUris.withAppendedId(scpUri, nodeId);

				// invio la risposta
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
			else
			{
				// lancia eccezione appropriata;
				throw new NullPointerException(
						"Nessuna componente trovata nel db");
			}

			/*
			 * Utilizzato per registrarsi ad SCP => viene chiamato da una
			 * componente all'avvio di un'applicazione => non appartiene a uno
			 * stack esistente, ma devo verificare se può essere invocata in
			 * quanto potrebbe possedere permessi o politiche universali
			 * incompatibili con altri stack. Inoltre, ottengo gia tutte le info
			 * necessarie per popolare lo stack; TODO: cambia il case in
			 * START_APP o qlcs del genere;
			 */
		case COMPONENT_ROOT_ADDED:
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"Insert COMPONENT_ROOT_ADDED: chiamo il db: " + selection);

			cl = query(selection);

			if ((cl != null) && (cl.size() == 1))
			{
				String cnf;
				String resCnf;

				/*
				 * dato che sto registrando per la prima volta una componente,
				 * devo assegnargli un mio id univoco;
				 */
				int id = rand.nextInt();

				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"Insert COMPONENT_ROOT_ADDED: assegno l'id " + id
								+ " alla componente di classe: "
								+ cl.get(0).getClassName());

				/*
				 * NOTE: Dal momento che lancio una nuova app, devo creare un
				 * nuovo stack. Utilizzo lo stesso id della componente, tanto il
				 * dominio in cui valgono sono differenti. TODO: perke lo creo
				 * qua che non sono sicuro che andrà a buon fine l'invocazione?
				 * non dovrei crearlo piu avanti?
				 */
				stacksSet.createNewScpStack(id);

				/*
				 * Controllo se i suoi permessi e le sue politiche universali
				 * sono compatibili con gli altri stack
				 */
				cnf = obtainCnf(id, cl.get(0));

				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"Insert COMPONENT_ROOT_ADDED: cnf ottenuta: " + cnf);

				// chiamo minisat
				resCnf = minisatJNI(cnf, 0);

				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"Insert COMPONENT_ROOT_ADDED: resCnf ottenuta: "
								+ resCnf);

				if (!resCnf.contains("UNSAT"))
				{
					// pusho la componente nello stack
					stacksSet.pushComponent(id, id, cl.get(0));
				}
				else
				{
					/*
					 * TODO: setto un valore che verrà ritornato e che mi
					 * permette di comunicare eentuali errori. oppure lancio
					 * eccezione.
					 */
					id = 0;
				}

				// preparo il messaggio di risposta
				String URL = "content://com.uni.ailab.scp.provider/add_root_component";
				Uri scpUri = Uri.parse(URL);
				Uri _uri = ContentUris.withAppendedId(scpUri, id);

				// invio la risposta
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
			else
			{
				/*
				 * Nel caso in cui non trovi nulla o trovi più di una componente
				 * TODO: metti un'opportuna eccezione valida anche per il caso
				 * più di una componente
				 */
				throw new NullPointerException(
						"Nessuna componente trovata nel db");
			}

		default:
			break;
		}
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
				"ScpProvider:" + stacksSet.getStacksSetStatus());

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpProvicer: delete");
		// selectionArgs contiene l'idStack e id componente da rimuovere

		int match = sURIMatcher.match(uri);
		switch (match)
		{

		case COMPONENT_REMOVED:

			int id = 0;
			int stackId = 0;
			String className = null;

			if (selectionArgs != null)
			{
				id = Integer.valueOf(selectionArgs[0]);
				stackId = Integer.valueOf(selectionArgs[1]);
				className = selectionArgs[2];
			}
			if (stacksSet.popComponent(stackId, id, className) != 0)
			{
				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"Delete: cancellazione avvenuta con successo");
			}
			else
			{
				// TODO: magari un'eccezione??? in realta no, in quanto se ho
				// eseguito questo metodo è perke una compnente è stata rimossa,
				// distrutta chiusa.. quindi non devo ritornare nulla a nessuno!
				Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
						"Delete: errore durante la cancellazione");
			}
			break;
		default:
			break;
		}

		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
				"Delete: lo stato degli stack è "
						+ stacksSet.getStacksSetStatus());
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
