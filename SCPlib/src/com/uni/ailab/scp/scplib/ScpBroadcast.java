package com.uni.ailab.scp.scplib;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ScpBroadcast
{
//	private Bundle data;
//	private Intent scpIntent;
//	private Context context;
//
//	public ScpBroadcast()
//	{
//		context = null;
//		data = new Bundle();
//		scpIntent = new Intent();
//		scpIntent.setClassName(ScpConstant.SERVICE_PACKAGE,
//				ScpConstant.SERVICE_CLASS);
//	}
//
//	public ScpBroadcast(Context c)
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
//	// per i broadcast receiver usi gli intent.
//	public void ScpSendBroadcast(Intent intent)
//	{
//		Log.d("ScpLib", "ScpSendBroadcast");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_RECEIVER);
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
//	public void ScpsendOrderedBroadcast(Intent intent)
//	{
//		Log.d("ScpLib", "ScpsendOrderedBroadcast");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_RECEIVER_ORDERED);
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
//	public void ScpSendStickyBroadcast(Intent intent) throws SecurityException
//	{
//		Log.d("ScpLib", "ScpSendStickyBroadcast");
//
//		// Preparo l'allegato
//		data.putString(ScpConstant.BUNDLE_COMPONENT_ID,
//				context.getPackageName());
//		data.putInt(ScpConstant.BUNDLE_REQUEST_TYPE,
//				ScpConstant.REQUEST_RECEIVER_STICKY);
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
