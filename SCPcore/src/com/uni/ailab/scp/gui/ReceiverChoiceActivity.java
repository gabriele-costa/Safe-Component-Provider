package com.uni.ailab.scp.gui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.uni.ailab.scp.R;
import com.uni.ailab.scp.receiver.SQLiteHelper;

/**
 * Created by gabriele on 01/04/15.
 */
public class ReceiverChoiceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        SQLiteHelper dbHelper = new SQLiteHelper(getApplicationContext());
        Cursor cursor = dbHelper.doQuery(intent.getStringExtra("scp.query"));

        if(cursor.getCount() == 0) {
            // Nothing to do
        }
        else if(cursor.getCount() == 1) {
            // send intent to component
        }
        else {

            setContentView(R.layout.receiverchoiceactivity);

            final ListView cListView = (ListView) findViewById(R.id.ComponentListView);

            ComponentCursorAdapter listAdapter = new ComponentCursorAdapter(this, cursor);
            // Attach cursor adapter to the ListView
            cListView.setAdapter(listAdapter);

            cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) cListView.getItemAtPosition(position);


                }

            });
        }
    }
}