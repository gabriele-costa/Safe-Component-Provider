package com.uni.ailab.scplib.components;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.uni.ailab.scplib.ContextScp;
import com.uni.ailab.scplib.util.ScpConstant;

/**
 * Classe che fornisce allo sviluppatore un contentProvider che rispetta il mio
 * "protocollo" Scp. Considerazioni: 1) non mi sto preoccupando del multi
 * threading. 2) i content provider sono legati al processo in cui vengono
 * invocati dal SO. se esso viene distrutto, essi muoiono, se nn viene
 * distrutto, vivono. Nel caso in cui siano loro a lanciare una nuova
 * componente, il comportamento di SCP e dell'albero delle dipendenze non varia
 * rispetto a quanto visto nelle altre componenti (si aggiungono nuovi elementi
 * sullo stack e bon).
 * 
 * Per garantire un corretto aggiornaento dello stack, eseguo l'iscrizione ad
 * scp all'interno del metodo oncreate, e, all'interno di ogni metodo insert,
 * query, gettype e remove. Verifico anche l'esistenza del campo whoiam: esso mi
 * vale come flag e mi indica se faccio parte di scp => se sono autorizzato ad
 * eseguire le chiamate.
 * 
 * 
 * @author giulio
 * 
 */
public abstract class VecchiaCopiaDiContentProviderScp extends ContentProvider
{
	private static final String QUERY_PARAMETER = "ScpQuery";

	private static boolean onCreateFlag = false;
	private static boolean queryFlag = false;
	private static boolean getTypeFlag = false;
	private static boolean insertFlag = false;
	private static boolean deleteFlag = false;
	private static boolean updateFlag = false;

	private ContextScp contextScp;

	private int SCP_whoIAm;
	
	private Uri scpUri;

	/*
	 * a differenza dell'activity, qui non ho l'obbligo alla chiamata al metodo
	 * super, che mi garantisce l'esecuzione del mio oncreate. => devo
	 * utilizzare il final, per evitare che lo sviluppatore possa overidire e
	 * bypassare il mio codice, un metodo onCreateScp, per permettere allo
	 * sviluppatore di implementare il suo codice, e un flag per evitare loop
	 * infiniti tra onCeate e onCreateScp nel caso lo sviluppatore utilizzasse
	 * super.onCreate();
	 */

	public VecchiaCopiaDiContentProviderScp()
	{
		super();
		Log.i(ScpConstant.LOG_TAG_SCPCONTENTPROVIDER, "Costruttore");
		// Prepare the uri for the call
		String URL1 = "content://com.uni.ailab.scp.provider/component";
		scpUri = Uri.parse(URL1);

		SCP_whoIAm = 0;
	}

	public ContextScp getContextScp()
	{
		return new ContextScp(getContext(), SCP_whoIAm, SCP_whoIAm);
	}

	private boolean checkCaller(Uri uri)
	{
		/*
		 * dato che prima di poter chiamare me provider, la componente deve
		 * passare da SCP, essa avrà sempre un riferimento al mio id => lo uso
		 * come valori di autorizzazione per accedere a me (dalla serie, SCP fa
		 * da garante esterno: se garantisco che scp fornisce il mio id (di
		 * provider) solo a componenti sicure, per forza il chiamante è sicuro
		 * se possiede il mio e io sono autorizzato a rispondere. inoltre, ciò
		 * mi permette di ignorare le chiamate provenienti da chiamanti non SCP
		 * (anche se potrebbe scoprire il pattern..ossia, gli basta inserire un
		 * campo extra e passano il controllo..tuttavia piu avanti io eseguo un
		 * secondo controllo)
		 * 
		 * RICORDA: i content provider non li registro in fase di creazione ma
		 * in fase di chiamata (quindi nelle insert query ecc).
		 */
		String query = uri.getQueryParameter(QUERY_PARAMETER);

		if (query != null)
		{
			int id = Integer.parseInt(query);
			// ho due possibili casi:
			if ((id != 0) && (SCP_whoIAm == 0))
			{
				/*
				 * è la prima volta che vengo invocato da una componente => il
				 * mio whoIam è zero e va aggiornato interrogando scp. A
				 * DIFFERENZA DELLE ALTRE COMPONENTI, CON I CONTENT PROVIDER NON
				 * HO PIU POSSIBILI PROVIDER, SEMPRE E SOLO UNO (IL PRIMO CHE SI
				 * REGISTRA CON TALE URI) => tuttavia mi registro perke scp non
				 * può sapere se l'invocazione è andata a buon fine o meno.
				 * Inolre lo faccio per verificare il chiamante.
				 * 
				 * Qua forniamo un ulteriore livello di sicurezza: nel caso in
				 * cui il provider fosse esposto, noi cmq controlliamo la catena
				 * di invcazione, nel senso che può essere invocato solo da un
				 * app che usa le nostre librerie
				 */

				// TODO tutta questa parte è da sistemare! dai un url
				// differente..devi verificare l'esistenza dell'id ricevuto (in
				// modo da evitare che finte app sicure possano invocarti
				// sparando id a caso) => necessiterai di un valore di ritorno
				// per dare ok o ko!

				String URL = "content://com.uni.ailab.scp.provider/add_provider";
				Uri scpUri = Uri.parse(URL);
				ContentValues cv = new ContentValues();
				cv.put("SCP_WHOIAM", id);
				// Non passo che componente sono in quanto scp lo sa gia

				Uri u = getContext().getContentResolver().insert(scpUri, cv);
				int ret = Integer.parseInt(u.getLastPathSegment());
				Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
						"checkCaller, ottenuto: " + ret);

				if (ret == 0)
				{
					SCP_whoIAm = id;
					return true;
				}

			}
			if ((id != 0) && (id == SCP_whoIAm))
			{
				/*
				 * non è la prima volta che vengo invocato: (ricorda che io
				 * provider dipendo dal processo in cui eseguo => non viene
				 * creata una nuova istanza ad ogni giro..), quindi ho gia un
				 * whoiam. Devo tuttavia verificare che l'id che mi sta mandando
				 * il caller sia uguale al mio (in questo modo determino se la
				 * chiamata è lecita o è un tentativo per bucarmi => deve essere
				 * ignorata)
				 */

				return true;
			}
			return false;
		}
		else return false;
	}

	@Override
	public final boolean onCreate()
	{
		Log.i(ScpConstant.LOG_TAG_SCPCONTENTPROVIDER, "onCreate");

		boolean retValue = false;

		// per evitare che all'interno di onCreateScp l'utente possa invocare
		// super.onCreate..
		if (onCreateFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onCreate");
		}
		else
		{
			onCreateFlag = true;

			// creo il contextScp che mi servirà per permettere allo
			// sviluppatore di eseguire le invocazioni che vuole verso le altre
			// componenti
			contextScp = new ContextScp(super.getContext());
			

			/*
			 * TODO: un app può essere costituita da una sola activity o un solo
			 * content provider o un solo broadcastreceiver. tuttavia i content
			 * provider vengono lanciati al lancio dell'app => anche senza
			 * essere esplicitamente invocati tramite un uri. Se registrassi il
			 * provider nell'oncreate, creerei sempre una nuova root o un nuovo
			 * stack, e imporrei le sue politiche anche senza invocarlo.
			 * Pertanto non registro il provider in scp in fase di creazione, ma
			 * solo quando viene chiamato => all'interno dei metodi query insert
			 * delete...
			 */

			// qua non devo riportare il flag a false in quanto una volta
			// creato, il
			// content provider non richiama più questo metodo (se dai test
			// viene
			// fuori che richiama il metodo, magari prova a usare il
			// costruttore);
			// constructorFlag = false;

			retValue = onCreateScp();

			onCreateFlag = false;
			return retValue;
		}

	}

	public abstract boolean onCreateScp();

	public abstract Cursor queryScp(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder);

	public abstract String getTypeScp(Uri uri);

	public abstract Uri insertScp(Uri uri, ContentValues values);

	public abstract int deleteScp(Uri uri, String selection,
			String[] selectionArgs);

	public abstract int updateScp(Uri uri, ContentValues values,
			String selection, String[] selectionArgs);

	@Override
	public final Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		if (!checkCaller(uri))
		{
			// eccezione: non sono abilitato ad eseguire il camondo, prima devo
			// essermi registrato ad SCP
		}
		else if (queryFlag)
		{
			throw new NullPointerException("You MUST NOT call super.query(..)");
		}
		queryFlag = true;

		Cursor retValue = queryScp(uri, projection, selection, selectionArgs,
				sortOrder);

		/*
		 * ricorda che tutto cio che metti qui viene eseguito prima del termine
		 * di queryscp se queryscp lancia activity o dialog => non puoi poppare
		 * la componente dallo stack perke potrebbe essere eseguita primo del
		 * dovuto
		 */
		queryFlag = false;

		return retValue;
	}

	@Override
	public final String getType(Uri uri)
	{
		if (!checkCaller(uri))
		{
			// eccezione: non sono abilitato ad eseguire il camondo, prima devo
			// essermi registrato ad SCP
		}
		else if (getTypeFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		getTypeFlag = true;

		String retValue = getTypeScp(uri);

		getTypeFlag = false;

		return retValue;
	}

	@Override
	public final Uri insert(Uri uri, ContentValues values)
	{
		if (!checkCaller(uri))
		{
			// eccezione: non sono abilitato ad eseguire il camondo, prima devo
			// essermi registrato ad SCP
		}
		else if (insertFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		insertFlag = true;

		Uri retValue = insertScp(uri, values);

		insertFlag = false;

		return retValue;
	}

	@Override
	public final int delete(Uri uri, String selection, String[] selectionArgs)
	{
		if (!checkCaller(uri))
		{
			// eccezione: non sono abilitato ad eseguire il camondo, prima devo
			// essermi registrato ad SCP
		}
		else if (deleteFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		deleteFlag = true;

		int retValue = deleteScp(uri, selection, selectionArgs);

		deleteFlag = false;

		return retValue;
	}

	@Override
	public final int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		if (!checkCaller(uri))
		{
			// eccezione: non sono abilitato ad eseguire il camondo, prima devo
			// essermi registrato ad SCP
		}
		else if (updateFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		updateFlag = true;

		int retValue = updateScp(uri, values, selection, selectionArgs);

		updateFlag = false;

		return retValue;
	}
}
