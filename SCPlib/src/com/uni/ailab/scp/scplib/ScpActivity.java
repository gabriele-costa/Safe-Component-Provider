package com.uni.ailab.scp.scplib;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ScpActivity
{
//	private Bundle data;
//	private Intent scpIntent;
//	private Context context;
//
//	public ScpActivity()
//	{
//		context = null;
//		data = new Bundle();
//		scpIntent = new Intent();
//		scpIntent.setClassName(ScpConstant.SERVICE_PACKAGE,
//				ScpConstant.SERVICE_CLASS);
//	}
//
//	public ScpActivity(Context c)
//	{
//		context = c;
//		data = new Bundle();
//		scpIntent = new Intent();
//		scpIntent.setClassName(ScpConstant.SERVICE_PACKAGE,
//				ScpConstant.SERVICE_CLASS);
//	}
//
//	public void setContext(Context c)
//	{
//		context = c;
//	}
//
//	// TODO: per adesso lascio le exception standard ma potrei pensare di
//	// crearne
//	// una mia, del tipo SecurityActivityNotFoundException. A quel punto si
//	// potrebbe addirittura pensare di gestire l'eccezione con un allert, che
//	// chiede all'utente se vuole procedere in maniera non sicura. A quel punto
//	// invii la richiesta standard e metti un throws ActivityNotFoundException
//	// standard.
//
//	public void startActivity(Intent intent) throws ActivityNotFoundException
//	{
//		Log.d(ScpConstant.LOG_TAG_SCPLIB, "ScpStartActivity");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_ACTIVITY);
//		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);
//
//		// Lancio il service
//		try
//		{
//			context.startService(scpIntent.putExtras(data));
//		}
//		catch (SecurityException e)
//		{
//			// TODO: handle exception. Viene lanciata se non hai i permessi per
//			// chiamare il servizio.
//			// Dato che il servizio lo creiamo noi, potrebbe essere inutile. La
//			// tolgo?
//		}
//	}
//
//	public void startActivityForResult(Intent intent, int rc)
//			throws ActivityNotFoundException
//	{
//		Log.d("ScpLib", "ScpStartActivityForResult");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_ACTIVITY_RESULT);
//		data.putParcelable(ScpConstant.BUNDLE_INTENT, intent);
//		data.putInt(ScpConstant.BUNDLE_REQUEST_CODE, rc);
//
//		// Lancio il service
//		try
//		{
//			context.startService(scpIntent.putExtras(data));
//		}
//		catch (SecurityException e)
//		{
//			// TODO: handle exception. Viene lanciata se non hai i permessi per
//			// chiamare il servizio.
//			// Dato che il servizio lo creiamo noi, potrebbe essere inutile. La
//			// tolgo?
//		}
//	}

}
