package com.uni.ailab.scp.scplib;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.uni.ailab.scplib.util.ScpConstant;

public class ScpCall
{
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger serviceMessenger = new Messenger(
			new IncomingHandler());

	private int async;
	private boolean bound;
	private Context context;
	private Bundle data;
	private Intent scpService;
	private int what;

	/** Messenger for communicating with service. */
	private Messenger clientMessenger;

	public ScpCall()
	{
		this(null, 0);
	}

	/**
	 * Constructor of the ScpCall object.
	 * 
	 * @param context
	 *            : the context of the application;
	 * @param async
	 *            : flag for make asynchronous call. 1 for async, 0 for sync;
	 */
	public ScpCall(Context context, int async)
	{
		this.context = context;
		this.async = async;
		this.clientMessenger = null;
		bound = false;
		data = new Bundle();
		scpService = new Intent();
		scpService.setClassName(ScpConstant.SERVICE_PACKAGE,
				ScpConstant.SERVICE_CLASS);
		if (context != null)
		{
			this.context.startService(scpService);
		}
	}

	public void setContext(Context context)
	{
		this.context = context;
		this.context.startService(scpService);
	}

	/**
	 * Establish a connection with the service. We use an explicit class name to
	 * make the intent explicit.
	 */
	private void bindScpService()
	{
		context.bindService(scpService, serviceConnection,
				Context.BIND_AUTO_CREATE);
		bound = true;
		Log.d(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCall: service bounded on thred: "
						+ android.os.Process
								.getThreadPriority(android.os.Process.myTid()));
	}

	/**
	 * Close the connection with the service using a message.
	 */
	void doUnbindService()
	{
		if (bound)
		{
			context.unbindService(serviceConnection);
			bound = false;
			Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: service unbounded");
		}
	}

	/*
	 * TODO: per adesso lascio le exception standard ma potrei pensare di
	 * crearne una mia, del tipo SecurityActivityNotFoundException. A quel punto
	 * si potrebbe addirittura pensare di gestire l'eccezione con un allert, che
	 * chiede all'utente se vuole procedere in maniera non sicura. A quel punto
	 * invii la richiesta standard e metti un throws ActivityNotFoundException
	 * standard.
	 */

	/**
	 * Launch a new activity
	 * 
	 * @param intent
	 *            : The intent to start.
	 * @throws ActivityNotFoundException
	 */
	public void startActivity(Intent intent) throws ActivityNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivity");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_ACTIVITY);
		 */

		// qua trasmetto l'intero intent. Si potrebbe pensare si mantenere una
		// mappa delle richieste, dove memorizzare tutti gli extra, in modo da
		// inviare solo la component richiesta o la action richiesta..
		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);

		what = ScpConstant.REQUEST_ACTIVITY;

		bindScpService();
	}

	/*
	 * TODO: il funzionamento dovrebbe essere lo stesso del precedente, ma
	 * magari fai una prova :)
	 */
	/**
	 * Launch an activity for which you would like a result when it finished
	 * 
	 * @param intent
	 *            : The intent to start.
	 * @param requestCode
	 *            : If >= 0, this code will be returned in onActivityResult()
	 *            when the activity exits.
	 * @throws ActivityNotFoundException
	 */
	public void startActivityForResult(Intent intent, int requestCode)
			throws ActivityNotFoundException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivityForResult");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_ACTIVITY_RESULT);
		 */
		data.putInt(ScpConstant.BUNDLE_REQUEST_CODE, requestCode);
		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);

		what = ScpConstant.REQUEST_ACTIVITY_RESULT;

		bindScpService();
	}

	/**
	 * Broadcast the given intent to all interested BroadcastReceivers.
	 * 
	 * @param intent
	 *            : The Intent to broadcast; all receivers matching this Intent
	 *            will receive the broadcast.
	 */
	public void sendBroadcast(Intent intent)
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendBroadcast");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_RECEIVER);
		 */
		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);

		what = ScpConstant.REQUEST_RECEIVER;

		bindScpService();
	}

	/**
	 * Broadcast the given intent to all interested BroadcastReceivers,
	 * delivering them one at a time to allow more preferred receivers to
	 * consume the broadcast before it is delivered to less preferred receivers.
	 * 
	 * @param intent
	 *            : The Intent to broadcast; all receivers matching this Intent
	 *            will receive the broadcast.
	 */
	public void sendOrderedBroadcast(Intent intent)
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendOrderedBroadcast");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_RECEIVER_ORDERED);
		 */
		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);

		what = ScpConstant.REQUEST_RECEIVER_ORDERED;

		bindScpService();
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
	 */
	public void sendStickyBroadcast(Intent intent) throws SecurityException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendStickyBroadcast");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_RECEIVER_STICKY);
		 */
		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);

		what = ScpConstant.REQUEST_RECEIVER_STICKY;

		bindScpService();
	}

	// TODO: in realtà startService dovrebbe tornare la componente che risponde
	// al servizio. Dato che noi non stiamo eseguendo una chiamata diretta al
	// systema, ma passiamo dal nostro servizio SCP, non siamo in grado di
	// fornire qua ora subito la componente che risponderà. Si potrebbe pensare
	// di andare a dormire per risvegliarsi al completamento dell'operazione, ma
	// il comportamento del sistema risulterebbe imprevedibile.

	// TODO: gestisci il return!!!
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
	 * 
	 */
	public ComponentName startService(Intent service) throws SecurityException
	{
		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startService");

		// Preparo l'allegato
		data.clear();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getPackageName());
		/*
		 * data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
		 * ScpConstant.REQUEST_SERVICE);
		 */
		data.putParcelable(ScpConstant.BUNDLE_INTENT, service);

		what = ScpConstant.REQUEST_SERVICE;

		bindScpService();
		return null;
	}

	// TODO:
	// public abstract boolean bindService (Intent service, ServiceConnection
	// conn, int flags)

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO: gestire le vari possibili risposte da parte del servizio.
			// Quali possono essere? Cosa voglio tornare al client?

			Bundle ciccio = msg.getData();
			switch (msg.what)
			{
			case ScpConstant.REQUEST_ACTIVITY:

				ComponentName c = (ComponentName) ciccio.getParcelable("ret");
				Log.d(ScpConstant.LOG_TAG_SCPLIB,
						"IncomingHandler: requested activity arrived. "
								+ c.getPackageName() + "  " + c.getClassName());
				Intent a = new Intent();

				a.setClassName("it.unige.androscp.front",
						"it.unige.androscp.front.SATActivity");
				// a.setClassName("org.rohm1.androsat",
				// "org.rohm1.androsat.MainActivity");
				context.startActivity(a);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {

		/**
		 * This is called when the connection with the service has been
		 * established, giving us the service object we can use to interact with
		 * the service. We are communicating with our service through an IDL
		 * interface, so get a client-side representation of that from the raw
		 * service object.
		 */
		public void onServiceConnected(ComponentName className, IBinder service)
		{

			clientMessenger = new Messenger(service);
			Log.d(ScpConstant.LOG_TAG_SCPLIB, "ServiceConnection: attached");

			try
			{
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = async;
				// Set the callback messenger.
				msg.replyTo = serviceMessenger;
				msg.setData(data);
				clientMessenger.send(msg);
				Log.d(ScpConstant.LOG_TAG_SCPLIB,
						"ServiceConnection: message sent to the service");
			}
			catch (RemoteException e)
			{
				// TODO: gestiamo??
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(context, "Connected to the SCP Service",
					Toast.LENGTH_SHORT).show();
		}

		/**
		 * This is called when the connection with the service has been
		 * unexpectedly disconnected -- that is, its process crashed.
		 */
		public void onServiceDisconnected(ComponentName className)
		{
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ServiceConnection: detected crashed service");
			clientMessenger = null;
		}
	};
}
