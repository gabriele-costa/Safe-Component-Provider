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

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button startSA = (Button) findViewById(R.id.buttonSA);
		Button startSP = (Button) findViewById(R.id.buttonSP);
		Button startSR = (Button) findViewById(R.id.buttonSR);
		Button startSS = (Button) findViewById(R.id.buttonSS);
		
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String component = ((EditText) findViewById(R.id.editComponent)).getText().toString();
				
				switch (v.getId()) {
				case R.id.buttonSA: {
					startScpActivity(v, component);
					break;
				}
				case R.id.buttonSP: {
					startScpProvider(v, component);
					break;
				}
				case R.id.buttonSR: {
					startScpReceiver(v, component);
					break;
				}
				case R.id.buttonSS: {
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
