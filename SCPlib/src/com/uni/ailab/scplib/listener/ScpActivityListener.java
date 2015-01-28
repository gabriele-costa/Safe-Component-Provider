package com.uni.ailab.scplib.listener;

import com.uni.ailab.scplib.components.ScpActivity;
import com.uni.ailab.scplib.util.ScpConstant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class ScpActivityListener implements ScpOnClickListener
{
	private Cursor cursor;
	private Context context;
	private Intent intent;
	private Bundle options;
	private int requestCode;

	public ScpActivityListener()
	{
		this(null);
	}

	public ScpActivityListener(Context context)
	{
		super();
		this.context = context;
		this.requestCode = -1;
	}

	@Override
	public void setCursor(Cursor cursor)
	{
		this.cursor = cursor;
	}

	@Override
	public void setIntent(Intent intent)
	{
		this.intent = intent;
	}

	@Override
	public void setRequestCode(int requestCode)
	{
		this.requestCode = requestCode;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// Adapt the position of the cursor to the selection
		cursor.moveToPosition(which);

		// Prepare the intent;
		String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
		String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
		intent.setClassName(pack, className);

		Log.d(ScpConstant.LOG_TAG_SCPLIB, "DialogListener: user choose "
				+ className);

		if (requestCode < 0)
		{
			context.startActivity(intent);
		}
		else if (context instanceof Activity)
		{
			Activity activity = (Activity) context;
			activity.startActivityForResult(intent, requestCode, options);
		}
	}

	public void setDundleOptions(Bundle options)
	{
		this.options = options;
	}
}
