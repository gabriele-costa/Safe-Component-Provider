package com.uni.ailab.scp.scplib;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ScpService
{

//	private Bundle data;
//	private Intent scpIntent;
//	private Context context;
//
//	public ScpService()
//	{
//		context = null;
//		data = new Bundle();
//		scpIntent = new Intent();
//		scpIntent.setClassName(ScpConstant.servicePack,
//				ScpConstant.serviceClass);
//	}
//
//	public ScpService(Context c)
//	{
//		context = c;
//		data = new Bundle();
//		scpIntent = new Intent();
//		scpIntent.setClassName(ScpConstant.servicePack,
//				ScpConstant.serviceClass);
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
//	// TODO: in realtà startService dovrebbe tornare la componente che risponde
//	// al servizio. Dato che noi non stiamo eseguendo una chiamata diretta al
//	// systema, ma passiamo dal nostro servizio SCP, non siamo in grado di
//	// fornire qua ora subito la componente che risponderà. Si potrebbe pensare
//	// di andare a dormire per risvegliarsi al completamento dell'operazione, ma
//	// il comportamento del sistema risulterebbe imprevedibile.
//	public void ScpStartService(Intent intent) throws SecurityException
//	{
//		Log.d("ScpLib", "ScpStartService");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_SERVICE);
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
}
