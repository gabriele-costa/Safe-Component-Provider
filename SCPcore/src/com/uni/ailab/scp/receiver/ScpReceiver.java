package com.uni.ailab.scp.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.uni.ailab.scp.gui.ReceiverChoiceActivity;
import com.uni.ailab.scp.log.Logger;


public class ScpReceiver extends BroadcastReceiver
{

    private static SQLiteHelper dbHelper;
    
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
        String action = intent.getStringExtra("scp.action");
        Uri data = intent.getData();
        
        Logger.log("*** New request received");
        Logger.log("Component: " + component);
        Logger.log("Type: " + type);
        Logger.log("Mode: " + mode);
        Logger.log("Action: " + action);
        Logger.log("");

        /*
        Retrieve receiver(s)
         */
        if(mode.compareTo("broadcast") == 0) {
            // Retrieve list and ask user if needed
            String query = dbHelper.getQuery(type, data, action);
            Intent i = new Intent(context, ReceiverChoiceActivity.class);
            i.setAction(Intent.ACTION_VIEW);
            //i.setClassName("com.uni.ailab.scp", ".gui.ReceiverChoiceActivity");
            //i.setComponent(new ComponentName(context, ReceiverChoiceActivity.class));
            i.putExtra("scp.query", query);
            i.putExtra("scp.caller", component);
            i.putExtra("scp.type", type);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else {
            // TODO NYI
        	Log.i("ScpReceiver", "received mode " + mode);
        }
        

    }
}
