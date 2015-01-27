package com.uni.ailab.scp.util;

public class ScpConstant
{
	public static final int ADD_ACTIVITY = 1;
	public static final int RMV_ACTIVITY = 2;

	public static final int REQUEST_ACTIVITY = 3;
	public static final int REQUEST_ACTIVITY_RESULT = 4;

	public static final int REQUEST_SERVICE = 5;

	public static final int REQUEST_RECEIVER = 6;
	public static final int REQUEST_RECEIVER_ORDERED = 7;
	public static final int REQUEST_RECEIVER_STICKY = 8;

	public static final int REQUEST_PROVIDER = 12;
	public static final int REQUEST_PROVIDER_QUERY = 9;
	public static final int REQUEST_PROVIDER_INSERT = 10;

	public static final String SCP_PACKAGE = "com.uni.ailab.scp";
	public static final String SCP_ACTIVITY_CLASS = "com.uni.ailab.scp.MainActivity";

	public static final String SERVICE_PACKAGE = "com.uni.ailab.scp";
	public static final String SERVICE_CLASS = "com.uni.ailab.scp.service.ScpService";

	public static final String BUNDLE_REQUEST_TYPE = "type";
	public static final String BUNDLE_COMPONENT_ID = "cid";
	public static final String BUNDLE_INTENT = "intent";
	public static final String BUNDLE_REQUEST_CODE = "rc";
	public static final String BUNDLE_PARAMETERS = "params";

	public static final String LOG_TAG_SCPLIB = "ScpLib";
	public static final String LOG_TAG_SCPPROVIDER = "ScpProvider";
	public static final String LOG_TAG_SCPRECEIVER = "ScpReceiver";

	// Constants used inside ScpCallProvider and BroadcastListener to discern
	// between different type of broadcast receiver requests;
	public static final int BROADCAST_SIMPLE = 0;
	public static final int BROADCAST_ORDERED = 1;
	public static final int BROADCAST_STICKY = 2;

	// Constants used inside ScpCallProvider and SCPPubblicReceiver to discern
	// between different type of component during the calling to the DB
	// (ScpCallProvider) and the saving into the DB (ScpPubblicReceiver);
	/* Definisco le costanti delle 4 tipologie di componenti */
	public static final String COMPONENT_ACTIVITY = "Activity";
	public static final String COMPONENT_SERVICE = "Service";
	public static final String COMPONENT_RECEIVER = "Receiver";
	public static final String COMPONENT_PROVIDER = "Provider";

	// Column name used in the db
	public static final String COLUMN_PACKAGE = "ComponentPackage";
	public static final String COLUMN_NAME = "ComponentName";
	public static final String COLUMN_TYPE = "Type";
	public static final String COLUMN_PERMISSION = "Permissions";
	public static final String COLUMN_POLICY = "Policy";
	public static final String COLUMN_SCOPE = "Scope";

	public static final int COLUMN_N_PACKAGE = 1;
	public static final int COLUMN_N_NAME = 2;
	public static final int COLUMN_N_TYPE = 3;
	public static final int COLUMN_N_PERMISSION = 4;
	public static final int COLUMN_N_POLICY = 5;
	public static final int COLUMN_N_SCOPE = 6;
	public static final String LOG_TAG_SCPSERVICE = null;

//	public static final String POLICY_TYPE_GLOBAL = "Global";
//	public static final String POLICY_TYPE_LOCAL = "Local";
//	public static final String POLICY_TYPE_STICKY = "Sticky";
	
	public static final String POLICY_TYPE_LOCALL = "com.uni.ilab.scp.LOCAL";
	public static final String POLICY_TYPE_UNIVERSAL = "com.uni.ilab.scp.UNIVERSAL";
	public static final String POLICY_TYPE_STICKY_LOCAL = "com.uni.ilab.scp.STICKY_LOCAL";
	public static final String POLICY_TYPE_STICKY_UNIVERSAL = "com.uni.ilab.scp.STICKY_UNIVERSAL";

}
