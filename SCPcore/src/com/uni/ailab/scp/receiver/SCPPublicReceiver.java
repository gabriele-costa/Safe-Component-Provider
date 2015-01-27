package com.uni.ailab.scp.receiver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.uni.ailab.scp.util.ScpConstant;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.util.Log;

public class SCPPublicReceiver extends BroadcastReceiver
{
	/* Definisco le costanti dei tag META-DATA */
	final static String PERMISSIONS = "PERMISSIONS";
	final static String POLICY = "POLICY";
	final static String SCOPE = "SCOPE";
	final static String STICKY = "STICKY";

	private Bundle metaData = null;
	private String permissions = null;
	private String policy = null;
	private String scope = null;
	private String sticky = null;

	public SCPPublicReceiver()
	{
		Log.i("SCPreceiver", "Broadcast creato!");
	}

	// Dichiaro la libreria c nella quale sarà presente il metodo nativo che mi
	// interessa
	static
	{
		// System.loadLibrary("sqlite3");
		System.loadLibrary("scplib");
	}

	// Dichiaro il metodo nativo che utilizzerò. si potrebbe pensare di
	// sostituire packageName e classname con un solo campo indicante il
	// fullpath della componente. Inoltre, nel caso di provider si potrebbe
	// pensare di sostituire tale valore con l'authority del provider piu il
	// nome della tabella. Ha senso? beh, dato che un provider può fornire
	// l'accesso a più tabelle, senza questo accorgimento otterresti tante entry
	// aventi stesso fullpackage e uri differenti (uno per ogni tabella) => non
	// ha molto senso. Invece ponendo l'uri al posto del fullpath faresti prima
	// sia nella ricerca sia nell'inserimento, ed elimineresti una colonna nel
	// db inutile a tutte le altre componenti

	/**
	 * 
	 * @param packageName
	 *            : nome del package della compoente
	 * @param className
	 *            : nome della classe della componente;
	 * @param componentType
	 *            : tipo di componente: activity,receiver, service o provider;
	 * @param permissions
	 *            : i permesse che dispone la componente;
	 * @param policy
	 *            : la politica di sicurezza associata alla componente;
	 * @param scope
	 *            : local global o sticky;
	 * @param uri
	 *            : campo dedicato ai content provider. Sostituisce il campo
	 *            action, non popolabile in fase di installazione in quanto i
	 *            campi intent-filter non restano accessibili (richiedono un
	 *            parser xml per interpretare il manifest
	 * @return
	 */
	public static native int insert(String packageName, String className,
			String componentType, String permissions, String policy,
			String scope);

	public static native int remove(String packageName);

	@Override
	public void onReceive(Context context, Intent intent)
	{
		PackageInfo pi = null;
		ArrayList<ComponentRec> componentsList = null;

		PackageManager pm = context.getPackageManager();
		String action = intent.getAction();
		String packageName = "";
		if (intent.getData() != null)
		{
			packageName = intent.getData().getEncodedSchemeSpecificPart();

			Log.i(ScpConstant.LOG_TAG_SCPRECEIVER, "Bella, intent " + action
					+ " ricevuto: " + packageName);
		}
		if (action.equals(Intent.ACTION_PACKAGE_ADDED))
		{
			Log.i(ScpConstant.LOG_TAG_SCPRECEIVER, "Package added case");

			// devo ottenere le info dell'app => lista delle componenti con i
			// loro parametri

			try
			{
				pi = pm.getPackageInfo(packageName,
						PackageManager.GET_PERMISSIONS
								| PackageManager.GET_ACTIVITIES
								| PackageManager.GET_PROVIDERS
								| PackageManager.GET_SERVICES
								| PackageManager.GET_RECEIVERS
								| PackageManager.GET_META_DATA);

				Log.i(ScpConstant.LOG_TAG_SCPRECEIVER,
						"Packageinfo in elaboarazione");
			}
			catch (NameNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (pi != null)
			{
				Log.i(ScpConstant.LOG_TAG_SCPRECEIVER, "Packageinfo non nullo");

				componentsList = new ArrayList<ComponentRec>();

				parser(componentsList, pi.activities,
						ScpConstant.COMPONENT_ACTIVITY);
				parser(componentsList, pi.receivers,
						ScpConstant.COMPONENT_RECEIVER);
				parser(componentsList, pi.providers,
						ScpConstant.COMPONENT_PROVIDER);
				parser(componentsList, pi.services,
						ScpConstant.COMPONENT_SERVICE);

			}
		}
		else if (action.equals(Intent.ACTION_PACKAGE_CHANGED))
		{
			Log.i(ScpConstant.LOG_TAG_SCPRECEIVER, "Package changed case");
			// per ora questo caso lo gestiamo con una delete e con una insert
			// al db
			remove(packageName);
			// TODO: verifica che invii l'intent di installazione cosi non devi
			// fare nulla perke in automatico ricade nel caso precedente

			// TODO: verifica anche che non chiami gia in automatico remove
			// tramite intent: in tal caso rimuovi il metodo qua sopra remove

		}
		else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)
				|| action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED))
		{
			Log.i(ScpConstant.LOG_TAG_SCPRECEIVER,
					"Package removed or package fully removed case");
			// call al metodo nativo per cancellare dal db le componenti aventi
			// questa package;
			remove(packageName);

		}
		else if (action.equals("TESTSCP"))
		{
			Bundle extra = intent.getExtras();
			String policy[] = extra.getStringArray("policy");
			String testClass = extra.getString("testClass");
			String testName = extra.getString("testName");

			String testPermissions = "";

			for (int i = 0; i < policy.length; i++)
			{
				// insert("com.example.provaclient",
				// "com.example.provaclient.MainActivity", "activity",
				// testPermissions, policy[i], "local");

				insert(testName, testName + i, "activity", testPermissions,
						policy[i], "local");
			}
		}
	}

	void parser(ArrayList<ComponentRec> componentsList,
			ComponentInfo[] componentInfo, String type)
	{
		if (componentInfo != null)
		{
			Log.i(ScpConstant.LOG_TAG_SCPRECEIVER, "Parser: ci sono " + componentInfo.length + " componenti" );
			
			for (ComponentInfo c : componentInfo)
			{
				Log.i(ScpConstant.LOG_TAG_SCPRECEIVER,
						"Componente " + c.name + "di tipo "+type );
			
				testApp(c,type);
				
				metaData = c.metaData;
				
				if (metaData == null)
				{
					Log.i(ScpConstant.LOG_TAG_SCPRECEIVER,
							"metadata nullo, ritorno");
					continue;
				}

				permissions = String.valueOf(metaData.get(PERMISSIONS));
				policy = String.valueOf(metaData.get(POLICY));

				// metto stringa null
				permissions = (permissions == null) ? "NULL" : permissions + " ";
				policy = (policy == null) ? "NULL" : policy  + " ";

				scope = "local";
				sticky = "false";

				String pack = c.packageName;
				String className = c.name;

				// Ho due possibili soluzioni: ho faccio un ggetto (una
				// lista ad esempio) che riempo con tutte le componenti e
				// poi passo a JNI, oppure chiamo direttamente la insert per
				// ogni componente. La prima soluzione sembrerebbe la più
				// furba ma richiede diverse sotto chiamate da parte della
				// classe c per poter interpretare correttamente i parametri
				// e gli oggetti component (almeno uno per ogni parametro
				// della classe component uno per caricare la classe
				// arraylist di java, uno per la classe component da me
				// definita, una chiamata per ogni metodo per navigare la
				// lista, uno per ogni metodo per manipolare i campi del
				// component (se dichiarati privati). Con la seconda, faccio
				// una sola chiamata per ogni componente. Forse si dovrebbe
				// analizzare il caso medio: quante componenti ha di media
				// un app?

				if (type.equals(ScpConstant.COMPONENT_PROVIDER))
				{
					ProviderInfo pi = (ProviderInfo) c;

					insert(pi.packageName, pi.authority, type, permissions,
							policy, scope);
					continue;
				}
			
				try
				{
					String packs = new String(c.packageName.getBytes("UTF-8"),
							"UTF-8");
					String names = new String(c.name.getBytes("UTF-8"), "UTF-8");
					String types = new String(type.getBytes("UTF-8"), "UTF-8");
					String permissionss = new String(
							permissions.getBytes("UTF-8"), "UTF-8");
					String policys = new String(policy.getBytes("UTF-8"),
							"UTF-8");
					String scopes = new String(scope.getBytes("UTF-8"), "UTF-8");
					insert(packs, names, types, permissionss, policys, scopes);
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// insert(c.packageName, c.name, type, permissions, policy,
				// scope);

				// TODO unire scope con sticky a seconda
				// dell'implementazione in c++
				// TODO verificare che c.name sia effettibamente il
				// nome della classe

				// componentsList.add(new Component(c.packageName, c.name,
				// type, permissions, policy, scope, "intent"));
				// }
			}
		}
	}

	private void testApp(ComponentInfo c, String type)
	{

		if (type.equals(ScpConstant.COMPONENT_PROVIDER))
		{
			ProviderInfo pi = (ProviderInfo) c;

			// log x test app
			Log.i("TESTAPP", type + pi.name + ", "
					+ pi.readPermission + ", " + pi.writePermission);

			return;
		}
		
		// log per test app
		if (type.equals(ScpConstant.COMPONENT_SERVICE))
		{
			ServiceInfo pi = (ServiceInfo) c;

			Log.i("TESTAPP", type + pi.name + ", "
					+ pi.permission);
			return;
		}
		// log per test app
		if ((type.equals(ScpConstant.COMPONENT_ACTIVITY))
				|| (type.equals(ScpConstant.COMPONENT_RECEIVER)))
		{
			ActivityInfo pi = (ActivityInfo) c;

			Log.i("TESTAPP", type + pi.name + ", "
					+ pi.permission);
			return;
		}
		
	}
}
