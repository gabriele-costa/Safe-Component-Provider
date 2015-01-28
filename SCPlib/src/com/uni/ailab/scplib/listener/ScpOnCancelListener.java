package com.uni.ailab.scplib.listener;

import com.uni.ailab.scplib.util.ScpConstant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.util.Log;

public class ScpOnCancelListener implements OnCancelListener
{
	private int nodeId;
	private int stackId;
	private Context context;

	public ScpOnCancelListener()
	{
		this(null);
	}

	public ScpOnCancelListener(Context context)
	{
		this.context = context;
	}

	public void setNodeId(int nodeId)
	{
		this.nodeId = nodeId;
	}

	public void setStackId(int stackId)
	{
		this.stackId = stackId;
	}

	@Override
	public void onCancel(DialogInterface dialog)
	{
		Log.i(ScpConstant.LOG_TAG_SCPLIB, "ScpOnCancelListener: onCancel");

		// devo chiamare scp provider per dirgli di cancellare il nodo
		// temporaneo

		String URL = "content://com.uni.ailab.scp.provider/remove_component";
		Uri scpUri = Uri.parse(URL);

		context.getContentResolver().delete(
				scpUri,
				null,
				new String[] { String.valueOf(nodeId), String.valueOf(stackId),
						null });
	}

}
