package com.uni.ailab.scp.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.uni.ailab.scp.gui.ReceiverChoiceActivity;


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
        Uri data = intent.getData();

        /*
        Retrieve receiver(s)
         */
        if(mode.compareTo("broadcast") == 0) {
            // Retrieve list and ask user if needed
            String query = dbHelper.getQuery(type, data);
            Intent i = new Intent(context, ReceiverChoiceActivity.class);
            i.setAction(Intent.ACTION_VIEW);
            //i.setClassName("com.uni.ailab.scp", ".gui.ReceiverChoiceActivity");
            //i.setComponent(new ComponentName(context, ReceiverChoiceActivity.class));
            i.putExtra("scp.query", query);
            i.putExtra("scp.caller", component);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
