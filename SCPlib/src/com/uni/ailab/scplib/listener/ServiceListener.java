package com.uni.ailab.scplib.listener;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class ServiceListener implements OnClickListener
{
	private Cursor cursor;
	private Context context;
	private Intent service;

	public ServiceListener()
	{
		this(null, null, null);
	}

	public ServiceListener(Context context, Cursor cursor, Intent service)
	{
		super();
		this.context = context;
		this.cursor = cursor;
		this.service = service;
	}

	public void setCursor(Cursor cursor)
	{
		this.cursor = cursor;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public void setService(Intent service)
	{
		this.service = service;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
			throws SecurityException
	{
		// Adapt the position of the cursor to the selection
		cursor.moveToPosition(which + 1);

		// Prepare the intent;
		String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
		String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
		service.setClassName(pack, className);

		Log.d(ScpConstant.LOG_TAG_SCPLIB, "DialogListener: user choose "
				+ className);

		// Start the secure service;
		context.startService(service);
	}
}
