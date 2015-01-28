package com.uni.ailab.scp.scplib;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ScpContentResolver
{
	private ContentResolver contResolver;
	private Context context;

	public ScpContentResolver()
	{
		contResolver = null;
		context = null;
	}

	public ScpContentResolver(ContentResolver cr, Context c)
	{
		contResolver = cr;
		context = c;
	}

	public void setContentResolver(ContentResolver cr)
	{
		contResolver = cr;
	}

	public void setContext(Context c)
	{
		context = c;
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		Log.d("ScpContentResolver", "Query!");

		// Lancio l'activity che si occuperà di mostrare l'icona di caricaento
		// delle componenti e di chiamare il servizio. Preparo quindi l'intent
		// per l'invocazione, settando path e classe.
		Intent intent = new Intent();
		intent.setClassName(ScpConstant.SERVICE_PACKAGE,
				ScpConstant.SERVICE_CLASS);

		// Preparo i parametri da inviare
		Bundle params = new Bundle();
		params.putParcelable("URI", uri);
		params.putStringArray("projection", projection);
		params.putString("selection", selection);
		params.putStringArray("selectionArgs", selectionArgs);
		params.putString("sortOrder", sortOrder);

		// Preparo i dati da allegare alla richiesta: costruisco un bundle in
		// cui inserisco chi sono, cosa voglio e i parametri per eseguire
		// l'operazione richiesta.
		Bundle data = new Bundle();
		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
				context.getApplicationInfo().name);
		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
				ScpConstant.REQUEST_PROVIDER_QUERY);
		data.putBundle(ScpConstant.BUNDLE_PARAMETERS, params);

		// Faccio partire l'intent per far avviare l'activity.
		
		intent.putExtra("data", data);
		context.startActivity(intent);

		Log.d("ScpContentResolver", "Finito");
		return null;
	}

	public Uri insert(Uri url, ContentValues values)
	{
		return null;
	}

	public void update()
	{

	}

	public int delete(Uri url, String where, String[] selectionArgs)
	{
		return 0;
	}

	public String getType(Uri url)
	{
		return null;
	}
}
