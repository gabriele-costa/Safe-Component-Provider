package com.uni.ailab.scplib.listener;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class BroadcastListener implements OnClickListener
{
	private Cursor cursor;
	private Context context;
	private Intent intent;
	private int type;

	public BroadcastListener()
	{
		this(null, null, null, 0);
	}

	public BroadcastListener(Context context, Cursor cursor, Intent intent,
			int type)
	{
		super();
		this.context = context;
		this.cursor = cursor;
		this.intent = intent;
		this.type = type;
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
		cursor.moveToPosition(which + 1);

		// Prepare the intent;
		String pack = cursor.getString(ScpConstant.COLUMN_N_PACKAGE);
		String className = cursor.getString(ScpConstant.COLUMN_N_NAME);
		intent.setClassName(pack, className);

		Log.d(ScpConstant.LOG_TAG_SCPLIB, "DialogListener: user choose "
				+ className);

		switch (type)
		{
		case ScpConstant.BROADCAST_SIMPLE:
			context.sendBroadcast(intent);
			break;
		case ScpConstant.BROADCAST_ORDERED:
			context.sendOrderedBroadcast(intent, null);
			break;
		case ScpConstant.BROADCAST_STICKY:
			context.sendStickyBroadcast(intent);
			break;
		}
	}
}
