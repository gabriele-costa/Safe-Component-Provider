package com.uni.ailab.scp.policy;

/**
 * Created by gabriele on 26/03/15.
 */
public class Permissions {

    public final static String NET = "a.p.INTERNET";
    public final static String BTT = "a.p.BLUETOOTH";
    public final static String CAM = "a.p.CAMERA";
    public final static String MIC = "a.p.MICROPHONE";
    public final static String RCP = "a.p.READ_CONTACTS";
    public final static String GAP = "a.p.GET_ACCOUNTS";
    public final static String RSD = "a.p.READ_SD";
    public final static String WSD = "a.p.WRITE_SD";
    public final static String APP = "AppAuthorized";
    public final static String UAP = "UserAuthorized";
    public final static String MPP = "MicroPayment";
    public final static String NPP = "NormalPayment";
    public final static String ACP = "AuthorizedComponent";
	
	public final static String[] PERMISSIONS = new String[] {
            NET,
            BTT,
            CAM,
            MIC,
            RCP,
            GAP,
            RSD,
            WSD,
            // App defined
            APP,
            UAP,
            MPP,
            NPP,
            ACP
    };

}
