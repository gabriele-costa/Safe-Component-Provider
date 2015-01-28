package com.uni.ailab.scplib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.uni.ailab.scp.scplib.R;
import com.uni.ailab.scplib.listener.ScpActivityListener;
import com.uni.ailab.scplib.listener.ScpOnCancelListener;
import com.uni.ailab.scplib.listener.ScpOnClickListener;
import com.uni.ailab.scplib.util.ScpConstant;
import com.uni.ailab.scplib.util.SecureComponentNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class ContextScp
{
	private static String COMPONENT_REQUESTED_URL = "content://com.uni.ailab.scp.provider/component#";
	private static String COMPONENT_ADDED_URL = "content://com.uni.ailab.scp.provider/add_component#";
	private static String COMPONENT_ROOT_ADDED_URL = "content://com.uni.ailab.scp.provider/add_root_component#";

	private Context context;
	private int SCP_whoIAm;
	private int SCP_myStackId;

	private boolean fakeFlag = false;

	public ContextScp()
	{
		this(null, 0, 0);
	}

	// usato dai content provider
	public ContextScp(Context context)
	{
		this(context, 0, 0);
	}

	public ContextScp(Context context, int myId, int myStackId)
	{
		this.context = context;
		this.SCP_whoIAm = myId;
		this.SCP_myStackId = myStackId;
	}

	// metodo usato dai content provider
	public void setId(int SCP_whoIAm)
	{
		this.SCP_whoIAm = SCP_whoIAm;
	}

	// metodo usato dai content provider
	public void setStackId(int SCP_myStackId)
	{
		this.SCP_myStackId = SCP_myStackId;
	}

	public final boolean startActivityScp(Intent intent)
	{

		Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivity (io sono "
				+ SCP_whoIAm + ")");

		// Prepare the listener to manage the click event;
		ScpActivityListener activityListener = new ScpActivityListener(context);

		/*
		 * listener dedicato al caso in cui l'utente non esegua una selezione
		 * sul dialog => va a cancellare la componente temporanea sullo stack
		 */
		ScpOnCancelListener scpOnCancelListener = new ScpOnCancelListener(
				context);

		intent = launchIntent(intent, activityListener, scpOnCancelListener);

		/*
		 * nel caso in cui fosse stata trovata una sola componente (=> no
		 * dialog), lancio l'activity
		 */
		if (intent != null)
		{
			context.startActivity(intent);
		}

		return true;
	}

	public final boolean startActivityForResultScp(Intent intent,
			int requestCode, Bundle options)
	{
		Log.i(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCall: startActivityForResule (io sono " + SCP_whoIAm + ")");

		if (!(context instanceof Activity))
		{
			// TODO: lancia eccezione appropriata
		}

		Activity activity = (Activity) context;

		// Prepare the listener to manage the click event;
		ScpActivityListener activityListener = new ScpActivityListener(activity);

		activityListener.setRequestCode(requestCode);
		activityListener.setDundleOptions(options);

		/*
		 * listener dedicato al caso in cui l'utente non esegua una selezione
		 * sul dialog => va a cancellare la componente temporanea sullo stack
		 */
		ScpOnCancelListener scpOnCancelListener = new ScpOnCancelListener(
				activity);

		intent = launchIntent(intent, activityListener, scpOnCancelListener);

		/*
		 * nel caso in cui fosse stata trovata una sola componente (=> no
		 * dialog), lancio l'activity
		 */
		if (intent != null)
		{
			activity.startActivityForResult(intent, requestCode, options);
		}

		return true;
	}

	/**
	 * A dialog will be launched if more then one secure component will be found
	 * 
	 * @param intent
	 * @param onClickListener
	 * @param onCancelListener
	 * @return an intent if only one secure component is found, null otherwise.
	 */
	private Intent launchIntent(Intent intent,
			ScpOnClickListener onClickListener,
			ScpOnCancelListener onCancelListener)
	{

		Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: LaunchIntent (io sono "
				+ SCP_whoIAm + ")");

		int tempId = 0;
		int stackId = 0;

		/*
		 * chiamo il contentProvider SCP allegando il mio ID di componente come
		 * fragment
		 */
		Uri scpUri = Uri.parse(COMPONENT_REQUESTED_URL + SCP_whoIAm);

		// eseguo la chiamata più opportuna ad SCP
		Cursor cursor = checkIntent(intent, ScpConstant.COMPONENT_ACTIVITY,
				scpUri);

		// Since the cursor is positioned before the entries, we have to
		// move it to the first element;
		cursor.moveToFirst();

		// prendo dal cursore l'id temporaneo assegnato al nodo
		tempId = cursor.getInt(3);
		stackId = cursor.getInt(5);

		// assegno all'intet sia l'id che avrà la componente sia il suo
		// stack
		intent.putExtra("SCP_WHOIAM", tempId);
		intent.putExtra("SCP_MYSTACKID", stackId);

		Log.i(ScpConstant.LOG_TAG_SCPLIB,
				"ScpCallProvider: la nuova componente sarà " + tempId
						+ " e andrà sullo stack " + stackId);

		if (cursor.getCount() == 1)
		{
			// One secure component found;
			Log.i(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found one secure Activity");

			// Prepare the intent to be sent;
			String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
			String className = cursor.getString(ScpConstant.COLUMN_N_NAME);

			intent.setClassName(pack, className);

			return intent;
		}
		else
		{
			/*
			 * Different secure components found. We want to show an UI (in our
			 * case a dialog) where the user can choose the component to be
			 * called;
			 */
			Log.i(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: found more than one secure Activity");

			// Prepare the listener to manage the click event;
			onClickListener.setCursor(cursor);
			onClickListener.setIntent(intent);
			onClickListener.setRequestCode(-1);

			/*
			 * listener dedicato al caso in cui l'utente non esegua una
			 * selezione sul dialog => va a cancellare la componente temporanea
			 * sullo stack
			 */
			onCancelListener.setNodeId(tempId);
			onCancelListener.setStackId(stackId);

			// Launch the dialog;
			launchDialog(cursor, onClickListener, onCancelListener);

			return null;
		}

	}

	public static boolean isBetween(int x, int lower, int upper)
	{
		return lower <= x && x <= upper;
	}

	private void launchDialog(Cursor cursor, OnClickListener onClickListener,
			OnCancelListener onCancelListener)
	{
		// sistema perke il colorare il background ti copre la scritta del
		// classname quindi la devi riinserire dopo lo switchcase;
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
				R.layout.list_row, cursor, new String[] { "className" },
				new int[] { R.id.textView1 }, 0);

		adapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex)
			{
				if (view.getId() == R.id.textView1)
				{
					final int permissionCount = cursor
							.getColumnIndex("permissionCount");
					final int count = cursor.getInt(permissionCount);
					final String className = cursor.getString(cursor
							.getColumnIndex("className"));

					if (count == 0)
					{
						((TextView) view).setBackgroundColor(Color.GREEN);
					}

					else if (isBetween(count, 1, 5))
					{
						((TextView) view).setBackgroundColor(Color.CYAN);
					}
					else if (isBetween(count, 6, 10))
					{
						((TextView) view).setBackgroundColor(Color.YELLOW);
					}
					else
					{
						((TextView) view).setBackgroundColor(Color.RED);

					}
					((TextView) view).setText(className);
					return true;
				}
				return false;
			}
		});

		// Prepare and show the dialog for the choose;
		new AlertDialog.Builder(context).setTitle("Choose Activity")
				.setAdapter(adapter, onClickListener)
				.setOnCancelListener(onCancelListener).show();

		// setto il flag per far inviare l'intent in maniera normale
		fakeFlag = true;

		Log.i("TEST", "STAMPATO DIALOG");
	}

	public void sendBroadcastScp(Intent intent)
	{

	}

	public void sendOrderedBroadcastScp(Intent intent, String receiverPermission)
	{

	}

	public void sendStickyBroadcastScp(Intent intent)
	{

	}

	public ComponentName startServiceScp(Intent service)
	{
		return null;
	}

	public boolean bindServiceScp(Intent service, ServiceConnection conn,
			int flags)
	{
		return false;
	}

	private Cursor checkIntent(Intent intent, String type, Uri scpUri)
	// throws SecureComponentNotFoundException
	{
		Cursor retValue = null;
		String action = null;
		String selection = null;
		ComponentName componentName = null;

		// Get component's destination and action;
		componentName = intent.getComponent();
		action = intent.getAction();

		/*
		 * TODO: pensa a come gestire la richiesta sincrona di una sola
		 * componente scelta in automatico da SCP: potresti fare una chiamata
		 * differente, o mandare un flag al provider, in modo da non dover
		 * modificare la gestione del cursore di risposta.
		 */

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

			// piccola utility per assicurarmi che l'intent che mi arriva sia
			// formattato bene:
			String fullClassName = componentName.getClassName();
			String packageName = componentName.getPackageName();
			if (!(fullClassName.contains(packageName)))
			{
				fullClassName = packageName + "." + fullClassName;
			}

			Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpCallProvider: explicit start"
					+ type + " request");

			selection = ScpConstant.COLUMN_TYPE + " = '" + type + "' AND "
					+ ScpConstant.COLUMN_NAME + " = '" + fullClassName + "'";

			retValue = context.getContentResolver().query(scpUri, null,
					selection, null, null);
		}
		else if (action != null)
		{
			// It's an implicit intent, so we query the content provider with
			// the action name;

			/*
			 * dato che non è possibile leggere da manifest i dettagli
			 * sull'intent filter, eseguo il seguente comando che interroga il
			 * sistema operativo per farmi restituire la lista di activity
			 * candidate a rispondere al mio intent.
			 */
			selection = ScpConstant.COLUMN_TYPE + " = '" + type + "' AND "
					+ ScpConstant.COLUMN_NAME + " IN(";
			List<ResolveInfo> list = new ArrayList<ResolveInfo>();

			if (type.equals(ScpConstant.COMPONENT_ACTIVITY))
			{
				list = context.getPackageManager().queryIntentActivities(
						intent, 0);
				Log.i(ScpConstant.LOG_TAG_SCPLIB,
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

				Log.i(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG SELECTION ri " + ri.toString());

				name = ri.activityInfo.name;

				selection = selection + "'" + name + "'";
				Log.i(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG SELECTION " + name);

				if (iterator.hasNext())
					selection = selection + " , ";
			}
			selection = selection + ")";
			retValue = context.getContentResolver().query(scpUri, null,
					selection, null, null);

			Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpCallProvider: implicit start"
					+ type + " request");
		}

		if (retValue == null || retValue.getCount() == 0)
		{
			// No secure components found;
			Log.i(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: no secure components found");
			// throw new SecureComponentNotFoundException();
		}

		return retValue;
	}

	// metodo usato per verificare i content provider da invocare
	public void checkUri(Uri uri) throws SecureComponentNotFoundException
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

		Uri scpUri = Uri.parse(COMPONENT_REQUESTED_URL + SCP_whoIAm);

		cursor = context.getContentResolver().query(scpUri, null, selection,
				null, null);

		if (cursor == null || cursor.getCount() == 0)
		{
			// No secure components found;
			Log.d(ScpConstant.LOG_TAG_SCPLIB,
					"ScpCallProvider: no secure components found");
			throw new SecureComponentNotFoundException();
		}
	}

	public ContentResolver getContentResolverScp()
	{
		return new ContentResolverScp(context, this);
	}

	public ContentResolver getContentResolver()
	{
		return context.getContentResolver();
	}

}
