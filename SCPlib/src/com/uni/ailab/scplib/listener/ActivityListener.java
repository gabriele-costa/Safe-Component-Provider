package com.uni.ailab.scplib.listener;

import com.uni.ailab.scplib.components.ScpActivity;
import com.uni.ailab.scplib.util.ScpConstant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class ActivityListener implements OnClickListener
{
	private Cursor cursor;
	private Context context;
	private Intent intent;
	private int requestCode;

	public ActivityListener()
	{
		this(null, null, null,0);
	}

	public ActivityListener(Context context, Cursor cursor, Intent intent,
			int requestCode)
	{
		super();
		this.context = context;
		this.cursor = cursor;
		this.intent = intent;
		this.requestCode = requestCode;
	}

	public void setCursor(Cursor cursor)
	{
		this.cursor = cursor;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public void setIntent(Intent intent)
	{
		this.intent = intent;
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
		
		if(requestCode < 0)
		{
			context.startActivity(intent);
		}
		else
		{
			Activity activity = (Activity) context;
			activity.startActivityForResult(intent, requestCode);
		}
	}
}
