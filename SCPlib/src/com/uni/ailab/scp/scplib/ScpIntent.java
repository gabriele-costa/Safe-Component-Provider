package com.uni.ailab.scp.scplib;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ScpIntent extends Intent
{
	public static final String ACTION_SCP = "it.unige.scp.action";

    public ScpIntent() {
        super();
    }

    public ScpIntent(Intent o) {
        super(o);
    }

    public ScpIntent(String action) {
        super(action);
    }

    public ScpIntent(String action, Uri uri) {
        super(action, uri);
    }

    public ScpIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public ScpIntent(String action, Uri uri, Context packageContext, Class<?> cls) {
        super(action,uri,packageContext,cls);
    }


}
