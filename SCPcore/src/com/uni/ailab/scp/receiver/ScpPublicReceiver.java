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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

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
        Open DB if not yet initialized
         */
        if (dbHelper == null)
            dbHelper = new SQLiteHelper(context);

        /*
        Parse the request
         */
        intent.getStringExtra("sender");


        /*
        Retrieve components, permissions and policies from the DB
         */

        /*
        Encode the clauses
         */


        ISolver solver = SolverFactory.newLight();

        try {
            solver.addClause(null);
        } catch (ContradictionException e) {
            e.printStackTrace();
        }


    }
}
