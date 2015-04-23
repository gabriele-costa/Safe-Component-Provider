package it.unige.cseclab;

import com.uni.ailab.scp.scplib.ScpContext;
import com.uni.ailab.scp.scplib.ScpIntent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button startSA = (Button) findViewById(R.id.buttonActivity);
		Button startSP = (Button) findViewById(R.id.buttonProvider);
		Button startSR = (Button) findViewById(R.id.buttonReceiver);
		Button startSS = (Button) findViewById(R.id.buttonService);
		
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				final Spinner componentSpinner = (Spinner) findViewById(R.id.spinner1);
				
				String component = componentSpinner.getSelectedItem().toString();
				if(component.compareTo(getString(R.string.none)) == 0)
					component = "";
						
				switch (v.getId()) {
				case R.id.buttonActivity: {
					startScpActivity(v, component);
					break;
				}
				case R.id.buttonProvider: {
					startScpProvider(v, component);
					break;
				}
				case R.id.buttonReceiver: {
					startScpReceiver(v, component);
					break;
				}
				case R.id.buttonService: {
					startScpService(v, component);
					break;
				}
				}
				
			}};
			
		startSA.setOnClickListener(listener);
		startSP.setOnClickListener(listener);
		startSR.setOnClickListener(listener);
		startSS.setOnClickListener(listener);
	}
	
	public void startScpActivity(View v, String cmp) {
		ScpIntent intent = new ScpIntent();
		intent.setAction(ScpIntent.ACTION_SCP);
		intent.putExtra("scp.caller", cmp);
        intent.putExtra("scp.type", "Activity");
        ScpContext c = new ScpContext(v.getContext());
		c.sendBroadcast(intent);
	}
	
	public void startScpProvider(View v, String cmp) {
		ScpIntent intent = new ScpIntent();
		intent.setAction(ScpIntent.ACTION_SCP);
		intent.putExtra("scp.caller", cmp);
        intent.putExtra("scp.type", "Provider");
        ScpContext c = new ScpContext(v.getContext());
		c.sendBroadcast(intent);
	}
	
	public void startScpReceiver(View v, String cmp) {
		ScpIntent intent = new ScpIntent();
		intent.setAction(ScpIntent.ACTION_SCP);
		intent.putExtra("scp.caller", cmp);
        intent.putExtra("scp.type", "Receiver");
        ScpContext c = new ScpContext(v.getContext());
		c.sendBroadcast(intent);
	}
	
	public void startScpService(View v, String cmp) {
		ScpIntent intent = new ScpIntent();
		intent.setAction(ScpIntent.ACTION_SCP);
		intent.putExtra("scp.caller", cmp);
        intent.putExtra("scp.type", "Service");
        ScpContext c = new ScpContext(v.getContext());
		c.sendBroadcast(intent);
	}
}
