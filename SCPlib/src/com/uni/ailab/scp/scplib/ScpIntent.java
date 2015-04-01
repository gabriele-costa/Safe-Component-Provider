package com.uni.ailab.scp.scplib;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class ScpIntent extends Intent
{

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
