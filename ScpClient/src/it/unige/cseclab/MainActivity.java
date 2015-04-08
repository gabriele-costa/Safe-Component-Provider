package it.unige.cseclab;

import com.uni.ailab.scp.scplib.ScpContext;
import com.uni.ailab.scp.scplib.ScpIntent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button start = (Button) findViewById(R.id.button1);
		
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onStartClick(v);
				
			}});
	}
	
	public void onStartClick(View v) {
		ScpIntent intent = new ScpIntent();
		intent.setAction("TESTSCP");
		intent.putExtra("scp.caller", "");
        intent.putExtra("scp.type", "Activity");
        ScpContext c = new ScpContext(v.getContext());
		c.sendBroadcast(intent);
	}
}
