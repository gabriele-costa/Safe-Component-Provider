package com.uni.ailab.scp.receiver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class ScpPublicReceiver extends BroadcastReceiver
{

    private static SQLiteHelper dbHelper;
    private static SQLiteDatabase database;

	public ScpPublicReceiver()
    { }

	@Override
	public void onReceive(Context context, Intent intent)
	{
        /*
        TODO: should check intent authenticity
         */

        /*
        Open DB if not yet initialized
         */
        if (dbHelper == null)
            dbHelper = new SQLiteHelper(context);

        /*
        Parse the request
         */
        String component = intent.getStringExtra("scp.sender");
        String action = intent.getStringExtra("scp.action");
        Uri data = intent.getData();

        /*
        Retrieve receiver(s)
         */
        Cursor receivers = dbHelper.getReceivers(action, data);

        /*
        Insertion / Deletion
         */
        com.uni.ailab.scp.runtime.Runtime.push(null, component);

    }
}
