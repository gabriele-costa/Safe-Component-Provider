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
import android.os.Parcelable;
import android.util.Log;

import com.uni.ailab.scp.gui.ComponentCursorAdapter;
import com.uni.ailab.scp.gui.ReceiverChoiceActivity;


public class ScpReceiver extends BroadcastReceiver
{

    private static SQLiteHelper dbHelper;
    private static SQLiteDatabase database;

	public ScpReceiver()
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
        String component = intent.getStringExtra("scp.caller");
        String type = intent.getStringExtra("scp.type");
        String mode = intent.getStringExtra("scp.mode");
        Uri data = intent.getData();

        /*
        Retrieve receiver(s)
         */
        if(mode.compareTo("broadcast") == 0) {
            // Retrieve list and ask user if needed
            String query = dbHelper.getQuery(type, data);
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setClass(context, ReceiverChoiceActivity.class);
            i.putExtra("scp.query", query);
            i.putExtra("scp.caller", component);
            context.startActivity(i);
        }
        else {
            // TODO NYI
        }

        /*
        Insertion / Deletion
         */
        

    }
}
