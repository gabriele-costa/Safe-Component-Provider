package com.uni.ailab.scplib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import com.uni.ailab.scp.scplib.R;
import com.uni.ailab.scplib.listener.ActivityListener;
import com.uni.ailab.scplib.listener.BroadcastListener;
import com.uni.ailab.scplib.listener.ServiceListener;
import com.uni.ailab.scplib.util.NotInstanceOfActivityException;
import com.uni.ailab.scplib.util.ScpConstant;
import com.uni.ailab.scplib.util.SecureComponentNotFoundException;

public class ScpCallProvider implements LoaderCallbacks<Cursor>
{
	// We define the flags for the different types of SCPCall.
	// FLAG_BLOCKED: the call to the provider will be synchronous, so the
	// execution
	// thread will attend till the Provider finish the computation.
	// FLAG_ASYNC: the call to the provider will use a loader, so it will be
	// asynchronous;
	// FLAG_ASYNC_DIALOG: in the case of multiple components, a dialog will be
	// shown to the user for the selection of the preferred component;

	private static final int FLAG_BLOCKED = 0;
	private static final int FLAG_ASYNC = 1;
	private static final int FLAG_ASYNC_DIALOG = 2;

	// TODO: per usarli dovrai fare una seconda tipologia di chiamata al
	// provider, in modo che non sprechi il sat solver pr fargli calcolare tutte
	// le possibili soluzioni per poi prenderne solo una...
	// Inoltre devi implementare la cosa del loader per eseguire una chiamata
	// ascincrona.

	private int async;
	private int flag;
	private Context context;
	private Uri scpUri;

	public ScpCallProvider()
	{
		this(null, 0);
	}

	/**
	 * Constructor of the ScpCall object.
	 * 
	 * @param context
	 *            : the context of the application;
	 * @param userChoice
	 *            : flag for make asynchronous call. If set to 1, in case of SCP
	 *            find more than 1 eligible component, an UI will displayed for
	 *            the choose the component. Otherwise, if set to 0, SCP will
	 *            identify only one eligible component and no UI will be
	 *            displayed;
	 */
	public ScpCallProvider(Context context, int flag)
	{
		// Set the global variables
		this.context = context;
		this.flag = flag;

		// Prepare the uri for the call
		String URL = "content://com.uni.ailab.scp.provider/component";
		scpUri = Uri.parse(URL);

		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCallProvider: new SCP object"
				+ scpUri.toString());
	}

	/**
	 * Set the context.
	 * 
	 * @param context
	 *            : the context of the application;
	 */
	public void setContext(Context context)
	{
		this.context = context;
	}

	/**
	 * TODO: ma se nel listener dove lancio l'activity capita un'eccezione
	 * (activity not found exception..cosa succede?
	 */

	/**
	 * Launch a new activity
	 * 
	 * @param intent
	 *            : The intent to start.
	 * @throws SecureComponentNotFoundException
	 */
	public void startActivity(Intent intent)
			throws SecureComponentNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivity");

		Cursor cursor = checkIntent(intent, ScpConstant.COMPONENT_ACTIVITY);

		// Since the cursor is positioned before the entries, we have to
		// move it to the first element;
		cursor.moveToFirst();

		if (cursor.getCount() == 1)
		{
			// One secure component found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found one secure Activity");

			// Prepare the intent to be sent;
			String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
			String className = cursor.getString(ScpConstant.COLUMN_N_NAME);

			intent.setClassName(pack, className);

			// Start the secure activity;
			context.startActivity(intent);
		}
		else
		{
			// Different secure components found. We want to show an UI (in our
			// case, a dialog) where the user can choose the component to be
			// called;

			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found more than one secure Activity");

			// Prepare the listener to manage the click event;
			ActivityListener activityListener = new ActivityListener(context,
					cursor, intent, -1);

			// Launch the dialog;
			launchDialog(cursor, activityListener);
		}
	}

	/**
	 * Launch an activity for which you would like a result when it finished
	 * 
	 * @param intent
	 *            : The intent to start.
	 * @param requestCode
	 *            : If >= 0, this code will be returned in onActivityResult()
	 *            when the activity exits.
	 * @throws NotInstanceOfActivityException
	 * @throws ActivityNotFoundException
	 */
	public void startActivityForResult(Intent intent, int requestCode)
			throws SecureComponentNotFoundException,
			NotInstanceOfActivityException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivityForResult");

		// first of all we've to check if the current context is an activity
		if (!(context instanceof Activity))
		{
			throw new NotInstanceOfActivityException();
		}

		Cursor cursor = checkIntent(intent, ScpConstant.COMPONENT_ACTIVITY);

		// Casting the context to an activity for allow the
		// startActivityForResult command;
		Activity activity = (Activity) context;

		// Since the cursor is positioned before the entries, we have to
		// move it to the first element;
		cursor.moveToFirst();

		if (cursor.getCount() == 1)
		{
			// One secure component found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found one secure Activity");

			// Prepare the intent to be sent;
			String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
			String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
			intent.setClassName(pack, className);

			// Start the secure activity;
			activity.startActivityForResult(intent, requestCode);
		}
		else
		{
			// Different secure components found. We want to show an UI (in our
			// case, a dialog) where the user can choose the component to be
			// called;

			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found more than one secure Activity");

			// Prepare the listener to manage the click event;
			ActivityListener activityListener = new ActivityListener(context,
					cursor, intent, requestCode);

			// Launch the dialog;
			launchDialog(cursor, activityListener);
		}
	}

	/**
	 * Broadcast the given intent to all interested BroadcastReceivers.
	 * 
	 * @param intent
	 *            : The Intent to broadcast; all receivers matching this Intent
	 *            will receive the broadcast.
	 * @throws SecureComponentNotFoundException
	 */
	public void sendBroadcast(Intent intent)
			throws SecureComponentNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendBroadcast");
		sendStandardBroadcast(intent, ScpConstant.BROADCAST_SIMPLE);
	}

	/**
	 * Broadcast the given intent to all interested BroadcastReceivers,
	 * delivering them one at a time to allow more preferred receivers to
	 * consume the broadcast before it is delivered to less preferred receivers.
	 * Since SCP guarantee the observance of secure policies, there's no need to
	 * specify any receiver permissions;
	 * 
	 * @param intent
	 *            : The Intent to broadcast; all receivers matching this Intent
	 *            will receive the broadcast.
	 * @throws SecureComponentNotFoundException
	 */
	public void sendOrderedBroadcast(Intent intent)
			throws SecureComponentNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendOrderedBroadcast");
		sendStandardBroadcast(intent, ScpConstant.BROADCAST_ORDERED);
	}

	/**
	 * Perform a sendBroadcast(Intent) that is "sticky," meaning the Intent you
	 * are sending stays around after the broadcast is complete.
	 * 
	 * @param intent
	 *            : The Intent to broadcast; all receivers matching this Intent
	 *            will receive the broadcast, and the Intent will be held to be
	 *            re-broadcast to future receivers.
	 * @throws SecurityException
	 * @throws SecureComponentNotFoundException
	 */
	public void sendStickyBroadcast(Intent intent) throws SecurityException,
			SecureComponentNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendStickyBroadcast");
		sendStandardBroadcast(intent, ScpConstant.BROADCAST_STICKY);
	}

	private void sendStandardBroadcast(Intent intent, int type)
			throws SecurityException, SecureComponentNotFoundException
	{
		// Since a service can be launched by an action, we must check if the
		// intent is well formatted;

		Cursor cursor = checkIntent(intent, ScpConstant.COMPONENT_RECEIVER);

		// Since the cursor is positioned before the entries, we have to
		// move it to the first element;
		cursor.moveToFirst();

		if (cursor.getCount() == 1)
		{
			// One secure component found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found one secure Receiver");

			// Prepare the intent to be sent;
			String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
			String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
			intent.setClassName(pack, className);

			// Start the secure receiver;
			switch (type)
			{
			case ScpConstant.BROADCAST_SIMPLE:
				context.sendBroadcast(intent);
				break;
			case ScpConstant.BROADCAST_ORDERED:
				context.sendOrderedBroadcast(intent, null);
				break;
			case ScpConstant.BROADCAST_STICKY:
				context.sendStickyBroadcast(intent);
				;
				break;
			default:
				throw new SecureComponentNotFoundException(
						"Invalid Broadcast Receiver type");
			}
		}
		else
		{
			// Different secure components found. We want to show an UI (in our
			// case, a dialog) where the user can choose the component to be
			// called;

			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found more than one secure Receiver");

			// Prepare the listener to manage the click event;
			BroadcastListener broadcastListener = new BroadcastListener(
					context, cursor, intent, type);

			// Launch the dialog;
			launchDialog(cursor, broadcastListener);
		}
	}

	/**
	 * Checks if the intent is well formatted and, eventually, calls the SCP
	 * application for getting the secure components;
	 * 
	 * @param intent
	 *            : the intent to check;
	 * @param type
	 *            : the type of component the intent is designed for;
	 * @return : the cursor of all secure component found by the sat solver;
	 * @throws SecureComponentNotFoundException
	 */
	private Cursor checkIntent(Intent intent, String type)
			throws SecureComponentNotFoundException
	{
		Cursor retValue = null;
		String action = null;
		String selection = null;
		ComponentName componentName = null;

		// Get component's destination and action;
		componentName = intent.getComponent();
		action = intent.getAction();

		// TODO: pensa a come gestire la richiesta sincrona di una sola
		// componente scelta in automatico da SCP: potresti fare una chiamata
		// differente, o mandare un flag al provider, in modo da non dover
		// modificare la gestione del cursore di risposta.

		// Check intent types (explicit or implicit)
		if (componentName == null && action == null)
		{
			// Intent bad formatted
			throw new NullPointerException(
					"Intent component or action mustun't be null");
		}

		if (componentName != null)
		{
			// It's an explicit intent, so we query the content provider with
			// the name of the destination component to check if it satisfy our
			// policies;

			Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCallProvider: explicit start"
					+ type + " request");

			selection = ScpConstant.COLUMN_TYPE + "= " + type + ","
					+ ScpConstant.COLUMN_NAME + " = " + componentName;
			retValue = context.getContentResolver().query(scpUri, null,
					selection, null, null);
		}
		else if (action != null)
		{
			// It's an implicit intent, so we query the content provider with
			// the action name;

			// dato che non è possibile leggere da manifest i dettagli
			// sull'intent filter, eseguo il seguente comando che interroga il
			// sistema operativo per farmi restituire la lista di activity
			// candidate a
			// rispondere al mio intent.
			selection = ScpConstant.COLUMN_TYPE + " = '" + type + "' AND "
					+ ScpConstant.COLUMN_NAME + " IN(";
			List<ResolveInfo> list = new ArrayList<ResolveInfo>();

			if (type.equals(ScpConstant.COMPONENT_ACTIVITY))
			{
				list = context.getPackageManager().queryIntentActivities(
						intent, 0);
				Log.d(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG ACTIVITY LIST DA "
								+ list.size());
			}

			else if (type.equals(ScpConstant.COMPONENT_SERVICE))
			{
				list = context.getPackageManager().queryIntentServices(intent,
						0);
			}

			else if (type.equals(ScpConstant.COMPONENT_RECEIVER))
			{
				list = context.getPackageManager().queryBroadcastReceivers(
						intent, 0);
			}

			// TODO: sistema sta roba qua sotto.. è sempre activity info!
			ResolveInfo ri = null;

			String name = null;
			Iterator<ResolveInfo> iterator = list.iterator();
			while (iterator.hasNext())
			{

				ri = iterator.next();

				Log.d(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG SELECTION ri " + ri.toString());

				name = ri.activityInfo.name;

				selection = selection + "'" + name + "'";
				Log.d(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG SELECTION " + name);

				if (iterator.hasNext())
					selection = selection + " , ";
			}
			selection = selection + ")";
			retValue = context.getContentResolver().query(scpUri, null,
					selection, null, null);

			Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCallProvider: implicit start"
					+ type + " request");
		}

		if (retValue == null || retValue.getCount() == 0)
		{
			// No secure components found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: no secure components found");
			throw new SecureComponentNotFoundException();
		}

		return retValue;
	}

	// TODO: gestisci il return!!! inserendo il runnable o quant'altro da
	// eseguire come post esecuzione del dialog
	/**
	 * Request that a given application service be started.
	 * 
	 * @param service
	 *            :Identifies the service to be started.
	 * @return: If the ScpCall is set synchronous and the service is being
	 *          started or is already running, the ComponentName of the actual
	 *          service that was started is returned; else if the ScpCall is set
	 *          asynchronous or the service does not exist null is returned.
	 * @throws SecurityException
	 * @throws SecureComponentNotFoundException
	 * 
	 */
	public ComponentName startService(Intent service) throws SecurityException,
			SecureComponentNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startService");

		// Since a service can be launched by an action, we must check if the
		// intent is well formatted;

		Cursor cursor = checkIntent(service, ScpConstant.COMPONENT_SERVICE);

		// Since the cursor is positioned before the entries, we have to
		// move it to the firse element;
		cursor.moveToFirst();

		if (cursor.getCount() == 1)
		{
			// One secure component found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found one secure Service");

			// Prepare the intent to be sent;
			String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
			String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
			service.setClassName(pack, className);

			// Start the secure activity;
			context.startService(service);
		}
		else
		{
			// Different secure components found. We want to show an UI (in our
			// case, a dialog) where the user can choose the component to be
			// called;

			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found more than one secure Service");

			// Prepare the listener to manage the click event;
			ServiceListener serviceListener = new ServiceListener(context,
					cursor, service);

			// Launch the dialog;
			launchDialog(cursor, serviceListener);
		}

		//
		return null;
	}

	// non posso ottenere prima un riferimento a un app sicura e poi invocare i
	// metodi in quanto come ben sappiamo l'ottenimento della componente sicura
	// richiede tempo e eventualmetne l'iterazione dell'utente. pertanto l'idea
	// è di fornire direttamente da scpcallprovider i metodi per la chiamata al
	// provider. In ogni metodo si eseguirà il processo di ricerca della
	// componente sicura: questo risulta sconveniente in quanto spesso si esegue
	// più di un comando verso il provider. Tuttavia è prevista un meccanismo di
	// cache, pertanto, sebbene verrà chiamato scp provider, non verrà eseguito
	// nuavamente il sat solver salvo ovviamente eventuali cambiamenti nelle
	// politiche del contesto di esecuzione
	public Cursor scpQuery(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
			throws SecureComponentNotFoundException
	{
		// per quanto riguarda i content provider non è possibile che vi siano
		// più match in quanto ogni uri è unico (il primo provider che si
		// registra al sistema operativo ottiene l'uri, gli altri ricevono
		// errore durante l'installazione) => non ho il problema del dialog

		// pertanto la chiamata ad scp si riduce semplicemente ad una verifica
		// della componente.

		checkUri(uri);

		Log.d(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCallProvider: found secure Provider");

		return context.getContentResolver().query(uri, projection, selection,
				selectionArgs, sortOrder);
	}

	public Uri insert(Uri url, ContentValues values)
			throws SecureComponentNotFoundException
	{
		checkUri(url);

		Log.d(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCallProvider: found secure Provider");

		return context.getContentResolver().insert(url, values);
	}

	public int update(Uri uri, ContentValues values, String where,
			String[] selectionArgs) throws SecureComponentNotFoundException
	{
		checkUri(uri);

		Log.d(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCallProvider: found secure Provider");

		return context.getContentResolver().update(uri, values, where,
				selectionArgs);
	}

	public int delete(Uri url, String where, String[] selectionArgs)
			throws SecureComponentNotFoundException
	{
		checkUri(url);

		Log.d(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCallProvider: found secure Provider");

		return context.getContentResolver().delete(url, where, selectionArgs);
	}

	private void checkUri(Uri uri) throws SecureComponentNotFoundException
	{
		String authority = null;
		String selection = null;
		Cursor cursor = null;

		if (uri == null)
		{
			throw new NullPointerException("Uri mustn't be null");
		}

		authority = uri.getAuthority();

		if (authority == null)
		{
			throw new NullPointerException("Uri's Authority mustn't be null");
		}

		selection = "type = " + ScpConstant.COMPONENT_PROVIDER
				+ ", component = " + authority;
		cursor = context.getContentResolver().query(uri, null, selection, null,
				null);

		if (cursor == null || cursor.getCount() == 0)
		{
			// No secure components found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: no secure components found");
			throw new SecureComponentNotFoundException();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		// TODO Auto-generated method stub

	}

	private void launchDialog(Cursor cursor, OnClickListener listener)
	{
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
				R.layout.list_row, cursor, new String[] { "className" },
				new int[] { R.id.textView1 }, 0);

		// Prepare and show the dialog for the choose;
		new AlertDialog.Builder(context).setTitle("Choose Activity")
				.setAdapter(adapter, listener).show();
	}

	// TODO:
	// public abstract boolean bindService (Intent service, ServiceConnection
	// conn, int flags)

}
