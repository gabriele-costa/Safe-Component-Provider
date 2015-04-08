package it.unige.cseclab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("NewApi")
public class SelectActiveDialog extends DialogFragment {
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        String[] list = new String[Simulator.active.size()];
        list = Simulator.active.toArray(list);
        
        builder.setMessage("Pick a Component").setItems(list, 
        		new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
               
        			}
        		});
    
        // Create the AlertDialog object and return it
        return builder.create();
    }
}