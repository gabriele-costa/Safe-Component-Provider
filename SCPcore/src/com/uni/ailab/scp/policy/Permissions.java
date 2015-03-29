package com.uni.ailab.scp.policy;

/**
 * Created by gabriele on 26/03/15.
 */
public class Permissions {

    public final static String[] PERMISSIONS = new String[] {
            "android.permission.INTERNET",
            "android.permission.BLUETOOTH",
            "android.permission.CAMERA"
    };

    public static String getPermission(int enc) {
        return PERMISSIONS[enc - 1];
    }

    public static int getEncoding(String p) {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if(PERMISSIONS[i].compareTo(p) == 0)
                return i+1;
        }
        // Not found
        return 0;
    }
}
