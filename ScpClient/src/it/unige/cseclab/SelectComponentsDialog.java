package it.unige.cseclab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("NewApi")
public class SelectComponentsDialog extends DialogFragment {
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Pick a Component").setItems(Simulator.components, 
        		new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
               
        			}
        		});
    
        // Create the AlertDialog object and return it
        return builder.create();
    }
}