package com.uni.ailab.scplib.components;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.uni.ailab.scplib.ContextScp;
import com.uni.ailab.scplib.listener.BroadcastListener;
import com.uni.ailab.scplib.util.ScpConstant;
import com.uni.ailab.scplib.util.SecureComponentNotFoundException;

public abstract class BroadcastReceiverScp extends BroadcastReceiver
{

	private Uri scpUri;
	private int SCP_whoIAm = 0;
	private Context context;

	/*
	 * flag per evitare che all'interno di onReceiveScp lo svilupatore possa
	 * invocare un metodo di broadcastReceiver tramite Super.
	 */
	private static boolean onReceiveflag = false;

	/*
	 * a differenza dell'activity, il ciclo vitale del broadcastReceiver è
	 * concentrato tutto nel metodo oncreate. Ciò è dovuto al fatto che questa
	 * componente si comporta come "usa e getta": eseguito il metodo onReceive,
	 * essa non risulta essere più attiva.. è una sorta di task in back ground
	 * non soggetto a tutti gli eventi presenti nell'activity. Se da un lato
	 * questo sembra complicare le cose, dall'altro le semplifica in quanto, una
	 * volta etichettato come inattivo (ossia terminato il termine onreceive)
	 * esso non torna piu active se non con una nuova creazione (ossia un
	 * intent) => non è necessario salvare il suo stato con il suo id fornito da
	 * scp.
	 */

	/*
	 * per gestire il tutto, è necessario aggiungere due metodi che verranno
	 * invocati automaticamente: onCreate(), che dovrà essere invocato come
	 * primo elemento all'interno di onreceive, e onDestroy(), da chiamare alla
	 * fine.
	 */

	/**
	 * 
	 * @param contextScp
	 *            context sicuro dal quale avviare nuove componenti;
	 * @param intent
	 *            intent ricevuto che ha avviato il receiver;
	 * @param context
	 *            context tradizionale, messo a disposizione per poter
	 *            utilizzare i metodi non ancora implementati da contextScp
	 */
	public abstract void onReceiveScp(ContextScp contextScp, Intent intent,
			Context context);

	@Override
	final public void onReceive(Context context, Intent intent)
	{
		if (onReceiveflag)
		{
			throw new NullPointerException("You MUST NOT call super.onRebind");
		}
		else
		{
			onReceiveflag = true;

			onCreate(context, intent);

			onReceiveScp(new ContextScp(context, SCP_whoIAm, SCP_whoIAm),
					intent, context);
			onDestroy();

			onReceiveflag = false;
		}
	}

	/**
	 * 
	 * @param context
	 *            , the context received in the onReceive method;
	 * @param intent
	 *            , the intent received in the onReceived method;
	 */
	private void onCreate(Context context, Intent intent)
	{

		onReceiveflag = true;

		// provvedo a registrare in SCP il broadcast receiver

		{
			Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER, "onCreate");

			// Prepare the uri for the call
			String URL1 = "content://com.uni.ailab.scp.provider/component";
			scpUri = Uri.parse(URL1);

			Bundle extras = intent.getExtras();

			if ((extras != null) && (extras.containsKey("SCP_WHOIAM")))
			{
				Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER,
						"onCreate, SCP_WHOIAM presente nell'itent: sono stato avviato da una componente sicura");
				/*
				 * Caso in cui l'app è stata avviata da un intent contente extra
				 * => verifico se è stata avviata da un app sicura: dal momento
				 * che le librerie scp impostano questo extra, se c'é significa
				 * che farò parte dell'albero di qualche componente => chiamo
				 * scp per dire che sono stata avviata, chi sono e di inserirmi
				 * nell'albero
				 */

				SCP_whoIAm = extras.getInt("SCP_WHOIAM");

				String URL = "content://com.uni.ailab.scp.provider/add_component";
				Uri scpUri = Uri.parse(URL);
				ContentValues cv = new ContentValues();
				cv.put("SCP_WHOIAM", SCP_whoIAm);
				cv.put("COMPONENT_NAME", this.getClass().getName());

				context.getContentResolver().insert(scpUri, cv);
			}
			// altrimenti non sono registrato => avvio la registrazione
			else registerToScp();
		}
	}

	private void registerToScp()
	{
		Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER, "registeToScp");
		// prima volta che vengo invocato, e non da un app sicura (esempio
		// vengo lanciato dal launcer) -> contantto scp per fargli sapere
		// che esisto e che sarò una root

		String URL = "content://com.uni.ailab.scp.provider/add_root_component";
		Uri scpUri2 = Uri.parse(URL);
		ContentValues cv = new ContentValues();
		cv.put("COMPONENT_NAME", this.getClass().getName());
		cv.put("SCP_WHOIAM", 0);

		// tapullo: sfrutto l'uri di ritorno per farmi passare l'id creato
		// da SCP. Lo passo sotto forma di authority
		Uri u = context.getContentResolver().insert(scpUri2, cv);
		SCP_whoIAm = Integer.parseInt(u.getLastPathSegment());
		Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER,
				"registeToScp, registrato. Ottenuto id: " + SCP_whoIAm);
	}

	private void onDestroy()
	{

		Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER, "onDestroy");
		// chiamo scp e gli dico che sto per essere eliminato
		if (SCP_whoIAm != 0)
		{
			Log.i(ScpConstant.LOG_TAG_SCPBROADCASTRECEIVER,
					"onDestroy, ma con SCP_WHOIAM diverso da zero, quindi lo salvo");
			String URL = "content://com.uni.ailab.scp.provider/remove_component";
			Uri scpUri = Uri.parse(URL);

			context.getContentResolver().delete(scpUri,
					String.valueOf(SCP_whoIAm), null);
			onReceiveflag = false;
		}
	}

}
