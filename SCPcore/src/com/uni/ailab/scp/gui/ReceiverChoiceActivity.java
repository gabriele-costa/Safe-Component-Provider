package com.uni.ailab.scp.gui;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uni.ailab.scp.R;
import com.uni.ailab.scp.receiver.SQLiteHelper;
import com.uni.ailab.scp.runtime.Frame;
import com.uni.ailab.scp.runtime.ScpRuntime;

/**
 * Created by gabriele on 01/04/15.
 */
public class ReceiverChoiceActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String component = intent.getStringExtra("scp.caller");
        final String type = intent.getStringExtra("scp.type");
        SQLiteHelper dbHelper = new SQLiteHelper(getApplicationContext());
        final Frame[] frame = dbHelper.doQuery(intent.getStringExtra("scp.query"));
        
        /*
         * TODO cursor must be refined by removing unsuitable components
         */
        Vector<String> components = checkPushableComponents(frame, component);

        if(components.size() == 0) {
            // Nothing to do
        }
        else if(components.size() == 1) {
            // send intent to component
        	
        	for(Frame f : frame) {
				if(f.component.compareTo(components.get(0)) == 0) {
					if(component.compareTo("") == 0)
						ScpRuntime.alloc(f, component);
					else if(type.compareTo("Service") == 0)
						ScpRuntime.allocService(f, component);
					else
						ScpRuntime.push(f, component);
                	break;
				}
			}
        }
        else {

            setContentView(R.layout.receiverchoiceactivity);

            final ListView cListView = (ListView) findViewById(R.id.ComponentListView);

            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, components);
            
            cListView.setAdapter(listAdapter);

            cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                	String s = (String) cListView.getItemAtPosition(position);
                	
                	for(Frame f : frame) {
						if(f.component.compareTo(s) == 0) {
							if(component.compareTo("") == 0)
								ScpRuntime.alloc(f, component);
							else
								ScpRuntime.push(f, component);
	                    	break;
						}
					}
                	
                	finish();
                }
            });
        }
    }

	private Vector<String> checkPushableComponents(Frame[] f, String component) {
		Vector<String> comps = new Vector<String>();
		
		for(Frame frame : f) {
			if(component.compareTo("") == 0) {
				if(ScpRuntime.canAlloc(frame, component))
					comps.add(frame.component);
			}
			else {
				if(ScpRuntime.canPush(frame, component))
					comps.add(frame.component);
			}

		}
		
		return comps;
	}
	
}