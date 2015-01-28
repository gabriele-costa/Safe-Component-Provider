package com.uni.ailab.scplib.components;

import com.uni.ailab.scplib.util.ScpConstant;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * NOTE: stabilisco che sia SCP a verificare sempre le chiamate (=> se chiedo di
 * aggiungere un nodo allo stack con un id gia presente, ritorna un errore o
 * null). Inoltre per ogni metodo che lascio da implementare all'utente, devo
 * usare un flag per evitare loop infiniti causabili da super.metodo(). Per
 * comunicare con lo sviluppatore sfrutto le eccezioni runtime in quanto non
 * posso definire nuove eccezioni (sto implementando metodi astratti definiti
 * senza eccezioni) oltre che specificare, nella descrizione dei metodi, che non
 * si deve mai chiamare super..
 */

public abstract class ServiceScp extends Service
{
	private static boolean onCreateFlag = false;
	private static boolean onStartCommandFlag = false;
	private static boolean onDestroyFlag = false;
	private static boolean onBindFlag = false;
	private static boolean onReBindFlag = false;
	private static boolean onUnBindFlag = false;

	private static int SCP_whoIAm = 0;

	@Override
	public final void onRebind(Intent intent)
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onRebind");

		if (onReBindFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		else
		{
			onReBindFlag = true;

			// TODO: chiama SCP

			onRebindScp(intent);

			onReBindFlag = false;

		}
	}

	@Override
	public final IBinder onBind(Intent intent)
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onBind");

		IBinder retValue = null;

		if (onBindFlag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		else
		{
			onBindFlag = true;

			// TODO: registrami ad SCP;

			retValue = onBindScp(intent);

			onBindFlag = false;

			return retValue;
		}
	}

	/**
	 * Dato che al bind eseguo sempre la creazione di uno stack, all'onbind
	 * eseguo una rimozione senza dovermi accertare di aver ricevuto tutte le
	 * unbind da tutte le possibile app interagenti col service (tanto tutte
	 * avranno creato il loro bind => lo stesso servizio avrà originato
	 * differenti stack). OK, MA COME FACCIO A SAPERE QUALE STACK HO UNBINDATO E
	 * DEVO RIMUOVERE? tramite il mio id che è univoco nel mondo degli stack..
	 */
	@Override
	public final boolean onUnbind(Intent intent)
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onUnbind");

		boolean retValue = false;

		if (onUnBindFlag)
		{
			throw new NullPointerException("You MUST NOT call super.method()");
		}
		else
		{
			onUnBindFlag = true;

			// TODO: deregistrazione da SCP;

			retValue = onUnbindScp(intent);

			onUnBindFlag = false;

			return retValue;
		}
	}

	@Override
	public final void onCreate()
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onCreate");

		// Per adesso parrebbe non mi serva a nulla onCreate.. eventualmente
		// rimuovilo e rimuovi onCreateScp()

		onCreateScp();
	}

	@Override
	public final int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onStart");

		int retValue = 0;

		if (onStartCommandFlag)
		{
			throw new NullPointerException("You MUST NOT call super.method()");
		}
		else
		{
			onStartCommandFlag = true;

			// TODO: registrazione ad SCP;

			retValue = onStartCommandScp(intent, flags, startId);

			// risetto il flag a false in quanto io non voglio inibire per
			// sempre la chiamata a questo metodo, ma evitare solo loop
			// inifiniti.
			onStartCommandFlag = false;

			return retValue;
		}
	}

	@Override
	public final void onDestroy()
	{
		Log.i(ScpConstant.LOG_TAG_SCPSERVICE, "onDestroy");

		if (onDestroyFlag)
		{
			throw new NullPointerException("You MUST NOT call super.method()");
		}
		else
		{
			onDestroyFlag = true;

			// TODO: rimozione da SCP;

			onDestroyScp();

			onDestroyFlag = false;
		}
	}

	// Metto a disposizioni gli analoghi metodi

	public abstract void onCreateScp();

	/**
	 * NON chiamare super.onStartCommand,
	 * 
	 * @param intent
	 * @param flags
	 * @param startId
	 * 
	 */
	public abstract int onStartCommandScp(Intent intent, int flags, int startId);

	public abstract IBinder onBindScp(Intent intent);

	public abstract boolean onUnbindScp(Intent intent);

	public abstract void onRebindScp(Intent intent);

	public abstract void onDestroyScp();

}
