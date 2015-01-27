package com.uni.ailab.scp.service;

import java.util.ArrayList;
import java.util.List;

import com.uni.ailab.scp.util.ScpConstant;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ScpService extends Service
{
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger messenger = new Messenger(new IncomingHandler());

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle receivedData = msg.getData();
			// Possiamo pensare di utilizzare il precedente??
			Bundle responseData = new Bundle();
			Message response = Message.obtain();

			switch (msg.what)
			{
			case ScpConstant.REQUEST_ACTIVITY:

				ArrayList<ComponentName> cn;
				Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
						"ProvaService: activity requested. Working thread: "
								+ android.os.Process
										.getThreadPriority(android.os.Process
												.myTid()));

				response.what = ScpConstant.REQUEST_ACTIVITY;

				if (msg.arg1 == 0)
				{
					cn = computeRequest(receivedData);
					responseData.putSerializable("ret", cn);
					response.setData(responseData);
					try
					{
						msg.replyTo.send(response);
					}
					catch (RemoteException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (msg.arg1 == 1)
				{
					new asyncTask().execute("ciao");
				}

				break;

			case ScpConstant.REQUEST_ACTIVITY_RESULT:
				Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
						"ProvaService: activity for result requested. Working thread: "
								+ android.os.Process
										.getThreadPriority(android.os.Process
												.myTid()));

				// devo distingure tra activity normale e activity for result.
				// Nel primo caso posso far viaggiare tranquillamente un task
				// asincrono, in quanto si tratta di far partire un intent senza
				// ritornare nulla al client. Nel secondo la cosa potrebbe
				// essere uguale in quanto probabilmente viene passato un
				// riferimento a un handler o qlcs risiedente nell'activity
				// chiamante.
				break;

			case ScpConstant.REQUEST_RECEIVER:
				// anche in questo caso non ho problemi a far partire un thread
				// dedicato all'elaborazione nativa, in quanto si tratta di
				// inviare intent senza ritornare nulla al client.
				break;

			case ScpConstant.REQUEST_PROVIDER:
				// in questo caso devo ritornare un cursore o altri valori => si
				// potrebbe pensare di implementare sia versione asicnrona che
				// sincrona, fornendo le due possibilità allo sviluppatore.
				break;

			case ScpConstant.REQUEST_SERVICE:
				// se non erro il bind o lo start a un servizio avviene per
				// mezzo di intent, quindi anche in questo caso

				// new calcoloComponente().execute("ciao");

				try
				{
					Message mio = Message.obtain(null, 0, 0, 0);
					// Bundle data = new Bundle();
					// data.putString("componente", "fasfs");
					// mio.setData(data);
					msg.replyTo.send(mio);
				}
				catch (RemoteException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private ArrayList<ComponentName> computeRequest(Bundle data)
	{
		// TODO: far partire un'activity da una componente diversa da una
		// activity comporta la creazione di un nuovo task di esecuzione,
		// portando il precedete in background. Ciò non complica troppo le cose,
		// se non per il fatto che:
		// 1) troppi task comportano la cancellazione di activity nel caso le
		// risorse di sistema inizino a scarseggiare => le activity cancellate
		// perdono lo stato;
		// 2) startActivity for result richiede che l'activity lanciata da cui
		// ottenere il risultato debba risiedere nello stesso task della
		// genitrici, altrimenti non viene ritornato nulla;
		// Pertanto, le attiviti, i risultati e tutte le chiamate, le facciamo
		// svolgere dall'applicazione chiamante, ritornandogli la componente più
		// adatta, in modo da evitare complicazioni e rendere naturale il
		// comportamento delle componenti.

		ArrayList<ComponentName> retvalue = new ArrayList<ComponentName>();

		Intent intent = (Intent) data.getParcelable(ScpConstant.BUNDLE_INTENT);
		ComponentName cmp = intent.getComponent();

		if (cmp == null)
		{
			// Si tratta di un intent implicito => l'elaborazione si basa sulla
			// sua action

			Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
					"ProvaService: compute request. looking for action: "
							+ intent.getAction());
			retvalue = retrieveResultByAction(intent.getAction());
		}
		else if (cmp != null)
		{
			// Se cmp è diverso da nullo significa che l'intent è di tipo
			// esplicito. Esso potrebbe possedere un action ma non ci interessa.
			// NOTA: funziona solo se lo sviluppatore ha compilato correttamente
			// il manifest, inserendo il package corretto e il nome della
			// componente senza il package. ES: package: com.it.ciao componente:
			// .miaActivity

			String fullPath = cmp.getPackageName() + "." + cmp.getClassName();

			Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
					"ProvaService: compute request. looking for: " + fullPath);
			retvalue = retrieveResult(fullPath);
		}

		try
		{
			Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
					"ProvaService: fake computation started");
			Thread.sleep(4000);
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}

		int size = retvalue.size();

		if (size == 0)
		{
			// TODO caso in cui non vi sia una componente installata che
			// soddisfi le politiche di sicurezza.
		}

		else if (size == 1)
		{
			// TODO: verifica che nel caso vi sia un elemento solo esso risieda
			// nello slot 0;
			return retvalue;
		}
		else if (size > 1)
		{
			// nel caso in cui vi siano più componenti eseguibili, richiedo
			// l'interazione dell'utente per mezzo di un'activity che si
			// occuperà di mostrare a schermo tutte le componenti trovate per
			// far scegliere all'utente quale eseguire. Tuttavia, il lancio di
			// questa activity avverra in un nuovo task: ciò potrebbe comportare
			// la cancellazione di altri task in background, ma non di quello
			// dell'app chiamante in quanto la nostra elaborazione in questa
			// nuova activity sarà molto leggera;

			// ok qua facciamo partire un'activity che implementerà il
			// necessario per mostrare i risultati ottenuti dal sat. Come prendi
			// però la scelta dell'utente? O bindiamo il service qua, e gli
			// facciamo gestire in qualche modo la chiamata, oppure da dentro
			// l'activity provi ad inviare un messaggio all'activity che ha
			// richiesto il sicuro e che sicuramente possiede un handler a cui
			// inviare il messaggio.
		}

		return null;
	}

	private ArrayList<ComponentName> retrieveResult(String fullPath)
	{
		// TODO metodo che invoca quello nativo al sat solver;
		return null;
	}

	private ArrayList<ComponentName> retrieveResultByAction(String action)
	{
		// TODO come il precedente ma con una query differente;
		return null;
	}

	private class asyncTask extends AsyncTask<String, Integer, String>
	{
		@Override
		protected String doInBackground(String... params)
		{
			Log.d("ProvaService",
					"Sono il thread asincrono...credo: "
							+ android.os.Process
									.getThreadPriority(android.os.Process
											.myTid()));
			try
			{
				Thread.sleep(3000);
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
			Log.d("NUOVO", "SERVER finito calcolo");
			return params + "calcolati buahahah";
		}

		@Override
		protected void onPostExecute(String result)
		{

		}
	}

	@Override
	public void onCreate()
	{
		Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
				"ProvaService: created. Working thread: "
						+ android.os.Process
								.getThreadPriority(android.os.Process.myTid()));
	}

	@Override
	public void onDestroy()
	{
		Log.d(ScpConstant.LOG_TAG_SCPSERVICE, "ProvaService: destroyed");
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
				"ProvaService: bounded. Working thread: "
						+ android.os.Process
								.getThreadPriority(android.os.Process.myTid()));

		return messenger.getBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(ScpConstant.LOG_TAG_SCPSERVICE,
				"ProvaService: started. Working thread: "
						+ android.os.Process
								.getThreadPriority(android.os.Process.myTid()));
		return super.onStartCommand(intent, flags, startId);
	}
}