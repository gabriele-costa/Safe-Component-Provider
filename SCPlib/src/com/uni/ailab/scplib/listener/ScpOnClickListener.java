package com.uni.ailab.scplib.listener;

import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;

public interface ScpOnClickListener extends OnClickListener
{
	public void setCursor(Cursor cursor);

	public void setIntent(Intent intent);
	
	public void setRequestCode(int requestCode);
}
