package com.uni.ailab.scplib.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.uni.ailab.scp.scplib.R;
import com.uni.ailab.scplib.ContextScp;
import com.uni.ailab.scplib.listener.ScpActivityListener;
import com.uni.ailab.scplib.listener.ScpOnCancelListener;
import com.uni.ailab.scplib.util.ScpConstant;

public class ScpActivity extends Activity
{
	// per sbarrare le componenti nel dialog..
	private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

	// private String SCP_whoIsDad = null;
	private int SCP_whoIAm = 0;
	private int SCP_myStackId = 0;
	private Uri scpUri = null;
	private boolean fakeFlag = false;
	private ContextScp contextScp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i(ScpConstant.LOG_TAG_SCPACTIVITY, "onCreate");
		super.onCreate(savedInstanceState);

		// Prepare the uri for the call
		String URL1 = "content://com.uni.ailab.scp.provider/component";
		scpUri = Uri.parse(URL1);

		// Controllo la presenza di extra nell'intent;
		Bundle extras = getIntent().getExtras();

		if ((savedInstanceState != null)
				&& (savedInstanceState.containsKey("SCP_WHOIAM") && (savedInstanceState
						.containsKey("SCP_MYSTACKID"))))
		{
			SCP_whoIAm = savedInstanceState.getInt("SCP_WHOIAM");
			SCP_myStackId = savedInstanceState.getInt("SCP_MYSTACKID");
			Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
					"onCreate, SCP_WHOIAM già presente: " + SCP_whoIAm);
			/*
			 * caso in cui sono già stato avviato in maniera sicura ma per
			 * qualche motivo sono stato pausato (rotazione schermo, distruzione
			 * da parte del SO..). Non devo fare nulla;
			 */
			return;
		}
		else if ((extras != null) && (extras.containsKey("SCP_WHOIAM")))
		{
			/*
			 * Caso in cui l'app è stata avviata da un intent contente extra =>
			 * verifico se è stata avviata da un app sicura: dal momento che le
			 * librerie scp impostano questo extra, se c'é significa che farò
			 * parte dell'albero di qualche componente => chiamo scp per dire
			 * che sono stata avviata, chi sono e di inserirmi nell'albero
			 */
			SCP_whoIAm = extras.getInt("SCP_WHOIAM");
			SCP_myStackId = extras.getInt("SCP_MYSTACKID");

			Log.i(ScpConstant.LOG_TAG_SCPACTIVITY, "onCreate, SCP_WHOIAM ("
					+ SCP_whoIAm + "e SCP_MYSTACKID (" + SCP_myStackId
					+ ") ricevuti dall'itent");

			String URL = "content://com.uni.ailab.scp.provider/add_component";
			Uri scpUri = Uri.parse(URL);
			ContentValues cv = new ContentValues();
			cv.put("SCP_WHOIAM", SCP_whoIAm);
			cv.put("SCP_MYSTACKID", SCP_myStackId);
			cv.put("COMPONENT_NAME", this.getComponentName().getClassName());

			this.getContentResolver().insert(scpUri, cv);

			// Creo l'oggetto scp context dal quale eseguirò le chiamate
			contextScp = new ContextScp(this, SCP_whoIAm, SCP_myStackId);
		}
		// altrimenti non sono registrato => avvio la registrazione
		else registerToScp();
	}

	/*
	 * do la possibilità di registrare la componente corrente in SCP. Perchè non
	 * lo metto nell'oncreate? xke altrimenti esterni potrebbe sfruttare in
	 * maniera automatica l'accesso ad SCP. Lasciando invece un metodo a parte,
	 * il programmatore pùo decidere é quando più opportuno registrarsi, magari
	 * dopo una login. RIsolvo davvero il problema cosi???? hmm public o
	 * private?
	 */
	private void registerToScp()
	{
		Log.i(ScpConstant.LOG_TAG_SCPACTIVITY, "registerToScp");
		/*
		 * prima volta che vengo invocato, e non da un app sicura (esempio vengo
		 * lanciato dal launcer) -> contantto scp per fargli sapere che esisto e
		 * che sarò una root
		 */

		String URL = "content://com.uni.ailab.scp.provider/add_root_component";
		Uri scpUri2 = Uri.parse(URL);
		ContentValues cv = new ContentValues();
		cv.put("COMPONENT_NAME", this.getComponentName().getClassName());
		cv.put("SCP_WHOIAM", 0);

		/*
		 * TODO: tapullo: sfrutto l'uri di ritorno per farmi passare l'id creato
		 * da SCP. Lo passo sotto forma di authority. Inoltre, dato che usiamo
		 * id_prima_componente_stack = id_stack, imposto anche lo stack_id. Sono
		 * cazzi se questo metodo viene chiamato anche in casi differenti dalla
		 * prima invocazione dell'app
		 */
		Uri u = this.getContentResolver().insert(scpUri2, cv);
		SCP_whoIAm = Integer.parseInt(u.getLastPathSegment());
		SCP_myStackId = SCP_whoIAm;
		Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
				"registerToScp, registrato. Ottenuto id: " + SCP_whoIAm);

		// Creo l'oggetto scp context dal quale eseguirò le chiamate
		contextScp = new ContextScp(this, SCP_whoIAm, SCP_myStackId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	public void onDestroy()
	{
		Log.i(ScpConstant.LOG_TAG_SCPACTIVITY, "onDestroy");
		// chiamo scp e gli dico che sto per essere eliminato
		if (SCP_whoIAm != 0)
		{
			Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
					"onDestroy, chiamo SCP per rimuovermi (sono " + SCP_whoIAm
							+ ")");
			String URL = "content://com.uni.ailab.scp.provider/remove_component";
			Uri scpUri = Uri.parse(URL);

			this.getContentResolver().delete(
					scpUri,
					null,
					new String[] { String.valueOf(SCP_whoIAm),
							String.valueOf(SCP_myStackId),
							this.getComponentName().getClassName() });
		}
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		Log.i(ScpConstant.LOG_TAG_SCPACTIVITY, "onSaveInstanceState");
		if (SCP_whoIAm != 0)
		{
			Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
					"onSaveInstanceState, ma con SCP_WHOIAM impostato, quindi lo salvo");
			// // save the current parent
			// savedInstanceState.putString("SCP_WHOISDAD", SCP_whoIsDad);
			// save the current id
			savedInstanceState.putInt("SCP_WHOIAM", SCP_whoIAm);
		}

		if (SCP_myStackId != 0)
		{
			Log.i(ScpConstant.LOG_TAG_SCPACTIVITY,
					"onSaveInstanceState, ma con SCP_MYSTACKID impostato, quindi lo salvo");
			// // save the current parent
			// savedInstanceState.putString("SCP_WHOISDAD", SCP_whoIsDad);
			// save the current id
			savedInstanceState.putInt("SCP_MYSTACKID", SCP_myStackId);
		}

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public final void startActivityForResult(Intent intent, int requestCode,
			Bundle options)
	{
		if (fakeFlag)
		{
			super.startActivityForResult(intent, requestCode, options);
			fakeFlag = false;
			return;
		}
		else
		{
			fakeFlag = contextScp.startActivityForResultScp(intent,
					requestCode, options);
		}
	}

	// TODO: hai tapullato la tua ecezione securecomponentnotfound...
	@Override
	public final void startActivity(Intent intent)
	{
		if (fakeFlag)
		{
			super.startActivity(intent);
			fakeFlag = false;
			return;
		}
		else
		{
			fakeFlag = contextScp.startActivityScp(intent);
		}

		/*
		 * // mi devo preoccupare del flag? in caso di multithreading.. if
		 * (fakeFlag) { super.startActivity(intent); fakeFlag = false; return; }
		 * 
		 * Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: startActivity (io sono "
		 * + SCP_whoIAm + ")");
		 * 
		 * int tempId = 0; int stackId = 0;
		 * 
		 * // chiamo il contentProvider SCP allegando l'ID dello stack String
		 * URL = "content://com.uni.ailab.scp.provider/component#" +
		 * SCP_myStackId; scpUri = Uri.parse(URL);
		 * 
		 * // eseguo la chiamata più opportuna ad SCP Cursor cursor =
		 * checkIntent(intent, ScpConstant.COMPONENT_ACTIVITY);
		 * 
		 * // Since the cursor is positioned before the entries, we have to //
		 * move it to the first element; cursor.moveToFirst();
		 * 
		 * // prendo dal cursore l'id temporaneo assegnato al nodo tempId =
		 * cursor.getInt(3); stackId = cursor.getInt(5);
		 * 
		 * // assegno all'intet sia l'id che avrà la componente sia il suo //
		 * stack intent.putExtra("SCP_WHOIAM", tempId);
		 * intent.putExtra("SCP_MYSTACKID", stackId);
		 * 
		 * Log.i(ScpConstant.LOG_TAG_SCPLIB,
		 * "ScpCallProvider: la nuova componente sarà " + tempId +
		 * " e andrà sullo stack " + stackId);
		 * 
		 * if (cursor.getCount() == 1) { // One secure component found;
		 * Log.i(ScpConstant.LOG_TAG_SCPLIB,
		 * "ScpCallProvider: found one secure Activity");
		 * 
		 * // Prepare the intent to be sent; String pack =
		 * cursor.getString(ScpConstant.COLUMN_N_PACKAGE); String className =
		 * cursor.getString(ScpConstant.COLUMN_N_NAME);
		 * 
		 * intent.setClassName(pack, className);
		 * 
		 * // Start the secure activity; super.startActivity(intent); } else {
		 * 
		 * Different secure components found. We want to show an UI (in our case
		 * a dialog) where the user can choose the component to be called;
		 * 
		 * Log.i(ScpConstant.LOG_TAG_SCPLIB,
		 * "ScpCallProvider: found more than one secure Activity");
		 * 
		 * // Prepare the listener to manage the click event;
		 * ScpActivityListener activityListener = new ScpActivityListener(this);
		 * activityListener.setCursor(cursor);
		 * activityListener.setIntent(intent);
		 * activityListener.setRequestCode(-1);
		 * 
		 * ScpOnCancelListener scpOnCancelListener = new ScpOnCancelListener(
		 * this); scpOnCancelListener.setNodeId(tempId);
		 * scpOnCancelListener.setStackId(stackId);
		 * 
		 * // Launch the dialog; launchDialog(cursor, activityListener,
		 * scpOnCancelListener); }
		 */
	}

/*	private Cursor checkIntent(Intent intent, String type)
	// throws SecureComponentNotFoundException
	{
		Cursor retValue = null;
		String action = null;
		String selection = null;
		ComponentName componentName = null;

		// Get component's destination and action;
		componentName = intent.getComponent();
		action = intent.getAction();

		
		 * TODO: pensa a come gestire la richiesta sincrona di una sola
		 * componente scelta in automatico da SCP: potresti fare una chiamata
		 * differente, o mandare un flag al provider, in modo da non dover
		 * modificare la gestione del cursore di risposta.
		 

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

			retValue = this.getContentResolver().query(scpUri, null, selection,
					null, null);
		}
		else if (action != null)
		{
			// It's an implicit intent, so we query the content provider with
			// the action name;

			
			 * dato che non è possibile leggere da manifest i dettagli
			 * sull'intent filter, eseguo il seguente comando che interroga il
			 * sistema operativo per farmi restituire la lista di activity
			 * candidate a rispondere al mio intent.
			 
			selection = ScpConstant.COLUMN_TYPE + " = '" + type + "' AND "
					+ ScpConstant.COLUMN_NAME + " IN(";
			List<ResolveInfo> list = new ArrayList<ResolveInfo>();

			if (type.equals(ScpConstant.COMPONENT_ACTIVITY))
			{
				list = this.getPackageManager()
						.queryIntentActivities(intent, 0);
				Log.i(ScpConstant.LOG_TAG_SCPLIB,
						"ScpCallProvider: DEBUG ACTIVITY LIST DA "
								+ list.size());
			}

			else if (type.equals(ScpConstant.COMPONENT_SERVICE))
			{
				list = this.getPackageManager().queryIntentServices(intent, 0);
			}

			else if (type.equals(ScpConstant.COMPONENT_RECEIVER))
			{
				list = this.getPackageManager().queryBroadcastReceivers(intent,
						0);
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
			retValue = this.getContentResolver().query(scpUri, null, selection,
					null, null);

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

	public static boolean isBetween(int x, int lower, int upper)
	{
		return lower <= x && x <= upper;
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * private void launchDialog(Cursor cursor, OnClickListener onClickListener,
	 * OnCancelListener onCancelListener) { // sistema perke il colorare il
	 * background ti copre la scritta del // classname quindi la devi riinserire
	 * dopo lo switchcase; SimpleCursorAdapter adapter = new
	 * SimpleCursorAdapter(this, R.layout.list_row, cursor, new String[] {
	 * "className" }, new int[] { R.id.textView1 }, 0);
	 * 
	 * adapter.setViewBinder(new ViewBinder() {
	 * 
	 * public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	 * if (view.getId() == R.id.textView1) { final int permissionCount = cursor
	 * .getColumnIndex("permissionCount"); final int count =
	 * cursor.getInt(permissionCount); final String className =
	 * cursor.getString(cursor .getColumnIndex("className")); String n_a_p =
	 * cursor.getString(cursor .getColumnIndex("notAssignable"));
	 * 
	 * int color;
	 * 
	 * if (count == 0) { color = Color.GREEN; } else if (isBetween(count, 1, 5))
	 * { color = Color.CYAN; } else if (isBetween(count, 6, 10)) { color =
	 * Color.YELLOW; } else { color = Color.RED; } ((TextView)
	 * view).setBackgroundColor(color); if (n_a_p.isEmpty()) { ((TextView)
	 * view).setText(className); } else { ((TextView) view).setText(className,
	 * TextView.BufferType.SPANNABLE);
	 * 
	 * Spannable spannable = (Spannable) ((TextView) view) .getText();
	 * spannable.setSpan(STRIKE_THROUGH_SPAN, 0, className.length(),
	 * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); } return true; } return false; }
	 * 
	 * });
	 * 
	 * // Prepare and show the dialog for the choose; new
	 * AlertDialog.Builder(this).setTitle("Choose Activity")
	 * .setAdapter(adapter, onClickListener)
	 * .setOnCancelListener(onCancelListener).show();
	 * 
	 * // setto il flag per far inviare l'intent in maniera normale fakeFlag =
	 * true;
	 * 
	 * Log.i("TEST", "STAMPATO DIALOG"); }
	 * 
	 * public void startStandardActivity(Intent intent) {
	 * Log.i(ScpConstant.LOG_TAG_SCPLIB,
	 * "ScpCallProvider: startStandardActivity"); fakeFlag = true;
	 * super.startActivity(intent); }
	 */

	/*
	 * @Override public void sendBroadcast(Intent intent) // throws
	 * SecureComponentNotFoundException { Log.d(ScpConstant.LOG_TAG_SCPLIB,
	 * "ScpCall: sendBroadcast"); sendStandardBroadcast(intent,
	 * ScpConstant.BROADCAST_SIMPLE); }
	 *//**
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
	/*
	 * public void sendOrderedBroadcast(Intent intent) throws
	 * SecureComponentNotFoundException { Log.d(ScpConstant.LOG_TAG_SCPLIB,
	 * "ScpCall: sendOrderedBroadcast"); sendStandardBroadcast(intent,
	 * ScpConstant.BROADCAST_ORDERED); }
	 * 
	 * Perform a sendBroadcast(Intent) that is "sticky," meaning the Intent you
	 * are sending stays around after the broadcast is complete.
	 * 
	 * @param intent : The Intent to broadcast; all receivers matching this
	 * Intent will receive the broadcast, and the Intent will be held to be
	 * re-broadcast to future receivers.
	 * 
	 * @throws SecurityException
	 * 
	 * @throws SecureComponentNotFoundException
	 * 
	 * public void sendStickyBroadcast(Intent intent) // throws
	 * SecurityException, SecureComponentNotFoundException {
	 * Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpCall: sendStickyBroadcast");
	 * sendStandardBroadcast(intent, ScpConstant.BROADCAST_STICKY); } /* private
	 * void sendStandardBroadcast(Intent intent, int type) // throws
	 * SecurityException, SecureComponentNotFoundException { // Since a service
	 * can be launched by an action, we must check if the // intent is well
	 * formatted;
	 * 
	 * Cursor cursor = checkIntent(intent, ScpConstant.COMPONENT_RECEIVER);
	 * 
	 * // Since the cursor is positioned before the entries, we have to // move
	 * it to the first element; cursor.moveToFirst();
	 * 
	 * if (cursor.getCount() == 1) { // One secure component found;
	 * Log.d(ScpConstant.LOG_TAG_SCPLIB,
	 * "ScpCallProvider: found one secure Receiver");
	 * 
	 * // Prepare the intent to be sent; String pack =
	 * cursor.getString(ScpConstant.COLUMN_N_PACKAGE); String className =
	 * cursor.getString(ScpConstant.COLUMN_N_NAME); intent.setClassName(pack,
	 * className);
	 * 
	 * // Start the secure receiver; switch (type) { case
	 * ScpConstant.BROADCAST_SIMPLE: super.sendBroadcast(intent); break; case
	 * ScpConstant.BROADCAST_ORDERED: super.sendOrderedBroadcast(intent, null);
	 * break; case ScpConstant.BROADCAST_STICKY:
	 * super.sendStickyBroadcast(intent); ; break; default: throw new
	 * SecureComponentNotFoundException( "Invalid Broadcast Receiver type"); } }
	 * else { // Different secure components found. We want to show an UI (in
	 * our // case, a dialog) where the user can choose the component to be //
	 * called;
	 * 
	 * Log.d(ScpConstant.LOG_TAG_SCPLIB,
	 * "ScpCallProvider: found more than one secure Receiver");
	 * 
	 * // Prepare the listener to manage the click event; BroadcastListener
	 * broadcastListener = new BroadcastListener( super, cursor, intent, type);
	 * 
	 * // Launch the dialog; launchDialog(cursor, broadcastListener); }
	 */

/*	// metodo usato dai vari test (150..)
	public void getchisono(String n)
	{
		Uri aa = null;
		String URL = "content://com.uni.ailab.scp.provider/component#"
				+ SCP_whoIAm;
		aa = Uri.parse(URL);
		String selection = ScpConstant.COLUMN_TYPE + " = '" + "activity"
				+ "' AND " + ScpConstant.COLUMN_PACKAGE + " = '" + n + "'";

		Cursor cursor = this.getContentResolver().query(aa, null, selection,
				null, null);

		// Since the cursor is positioned before the entries, we have to
		// move it to the first element;
		cursor.moveToFirst();

		int number = cursor.getCount();

		if (number == 0)
		{
			// One secure component found;
			Log.i("TEST", "CASO: " + n + " PROBLEMA, NON TROVO NULLA");

		}
		else if (number == 1)
		{
			// One secure component found;
			Log.i("TEST", "CASO: " + n + " TROVATA UNA COMPONENTE");

		}
		else
		{
			// Different secure components found. We want to show an UI (in our
			// case, a dialog) where the user can choose the component to be
			// called;

			Log.i("TEST", "CASO: " + n + " TROVATE " + number + " COMPONENTI");

			// prendo dal cursore l'id temporaneo assegnato al nodo
			int tempId = cursor.getInt(3);

			// Prepare the listener to manage the click event;
			ScpActivityListener activityListener = new ScpActivityListener(this);
			activityListener.setCursor(cursor);
			activityListener.setIntent(new Intent());
			activityListener.setRequestCode(-1);

			ScpOnCancelListener scpOnCancelListener = new ScpOnCancelListener(
					this);
			scpOnCancelListener.setNodeId(tempId);

			// Launch the dialog;
			launchDialog(cursor, activityListener, scpOnCancelListener);
		}
	}*/

}
