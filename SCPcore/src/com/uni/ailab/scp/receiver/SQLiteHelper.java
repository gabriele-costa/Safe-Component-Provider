package com.uni.ailab.scp.receiver;

import java.util.Arrays;
import java.util.Vector;

import com.uni.ailab.scp.cnf.Formula;
import com.uni.ailab.scp.policy.Permissions;
import com.uni.ailab.scp.policy.Policy;
import com.uni.ailab.scp.policy.Scope;
import com.uni.ailab.scp.runtime.Frame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMPONENTS = "components";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_SCHEME = "scheme";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_POLICIES = "policies";
    public static final String COLUMN_PERMISSIONS = "permissions";

    private static final String DATABASE_NAME = "components.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMPONENTS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_TYPE + " text not null, " +
            COLUMN_POLICIES + " text, " +
            COLUMN_PERMISSIONS + " text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
    	
    	database.execSQL(DATABASE_CREATE);
        
        /*
         * TEST: fill DB with example components
         */
    	
    	// MaplePay
        this.insertComponent("MainActivity", "Activity", new Policy[0], new String[0], database);
        this.insertComponent("LoginActivity", "Activity", 
        		new Policy[] {new Policy(Scope.GLOBAL, 
        				Formula.not(Formula.or(Formula.lit(Permissions.MIC), Formula.lit(Permissions.CAM))), false)}, 
        				new String[0], database);
        this.insertComponent("ContactPayRec.", "Receiver", 
        		new Policy[] {
        		new Policy(Scope.DIRECT, Formula.imply(Formula.not(Formula.lit(Permissions.APP)), Formula.lit(Permissions.UAP)), false),
        		new Policy(Scope.LOCAL, Formula.and(Formula.lit(Permissions.RCP), Formula.lit(Permissions.GAP)), false)
        },
        		new String[0], database);
        this.insertComponent("BalanceActivity", "Activity", 
        		new Policy[] {
        		new Policy(Scope.LOCAL, Formula.imply(
        				Formula.not(Formula.lit(Permissions.ACP)), 
        				Formula.not(Formula.or(Formula.or(Formula.lit(Permissions.NET), Formula.lit(Permissions.WSD)),
        						Formula.lit(Permissions.BTT)))), 
        				true)
        }, new String[0], database);
        this.insertComponent("PaymentActivity", "Activity", new Policy[0], new String[0], database);
        this.insertComponent("NormalPayRec.", "Receiver", 
        		new Policy[] {
        		new Policy(Scope.DIRECT, Formula.and(Formula.lit(Permissions.NPP), Formula.lit(Permissions.UAP)), false)
        },
        		new String[0], database);
        this.insertComponent("MicroPayRec.", "Receiver", 
        		new Policy[] {
        		new Policy(Scope.DIRECT, 
        				Formula.and(Formula.lit(Permissions.MPP), 
        						Formula.or(Formula.lit(Permissions.UAP), Formula.lit(Permissions.APP))), 
        						false)
        },
        		new String[0], database);
        this.insertComponent("ConnectionSer.", "Service", new Policy[0], new String[] {Permissions.NET, Permissions.ACP},database);
        this.insertComponent("HistoryProvider", "Provider", new Policy[0], new String[] {Permissions.RSD, Permissions.WSD, Permissions.ACP}, database);
        
        // QRScanner
        this.insertComponent("QRScannerActivity", "Activity", new Policy[0], new String[] {Permissions.CAM, Permissions.MPP, Permissions.UAP}, database);
        
        // FancyEditor
        this.insertComponent("EditorActivity", "Activity", new Policy[0], new String[] {Permissions.RSD}, database);
        this.insertComponent("DocEditorAct.", "Activity", new Policy[0], new String[0], database);
        this.insertComponent("OpenDocRec.", "Receiver", new Policy[0], new String[] {Permissions.RSD}, database);
        this.insertComponent("CloudSer.", "Service", new Policy[0], new String[] {Permissions.NET}, database);
        
        // TamerReader
        this.insertComponent("ReaderActivity", "Activity", new Policy[0], new String[] {Permissions.RSD}, database);
        this.insertComponent("DocViewAct.", "Activity", new Policy[0], new String[0], database);
        this.insertComponent("ViewDocRec.", "Receiver", new Policy[0], new String[] {Permissions.RSD}, database);
        
    }
    
    public void insertComponent(String name, String type, Policy[] policies, String[] permissions, SQLiteDatabase database) {
    	
    	String pol = formatPolicies(policies);
    	String per = formatPermissions(permissions);
    	
    	ContentValues values = new ContentValues(); 
    	values.put(COLUMN_NAME, name); 
    	values.put(COLUMN_TYPE, type);  
    	values.put(COLUMN_POLICIES, pol);
    	values.put(COLUMN_PERMISSIONS, per);
    	
    	database.insert(TABLE_COMPONENTS, null, values); 
    }

    private String formatPolicies(Policy[] policies) {
		String s = "";
		for(int i = 0; i < policies.length; i++) {
			s += policies[i].toString() + ":";
		}
		return s;
	}
    
    private Policy[] parsePolicies(String s) {
    	
    	if(s.compareTo("") == 0)
    		return new Policy[0];
    	
		String[] str = s.split(":");
    	Policy[] pol = new Policy[str.length];
    	
		for(int i = 0; i < str.length; i++) {
			pol[i] = Policy.parse(str[i]);
		}
		return pol;
	}

	private String formatPermissions(String[] permissions) {
		String s = "";
		for(int i = 0; i < permissions.length; i++) {
			s += permissions[i].toString() + ":";
		}
		return s;
	}
	
	private String[] parsePermissions(String s) {
		
		if(s.compareTo("") == 0)
			return new String[0];
		else 
			return s.split(":");
	}

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPONENTS);
        onCreate(db);
    }

    public String getQuery(String type, Uri data, String action) {
        return "SELECT * FROM " + TABLE_COMPONENTS +" WHERE " + COLUMN_TYPE +" = '"+ type + "'";
    }

    public Cursor doCursorQuery(String query) {
        // TODO: should check Uri scheme
        return this.getReadableDatabase().rawQuery(query, null);
    }
    
    public Frame[] doQuery(String query) {
        Cursor c = doCursorQuery(query);
        Frame[] v = new Frame[c.getCount()];
        
        for (int i = 0; i < c.getCount(); i++) {
    		c.moveToPosition(i);
    		v[i] = new Frame();
    		v[i].component = c.getString(1);
    		
    		Policy[] pol = parsePolicies(c.getString(3));
    		Vector<Policy> pVec = new Vector<Policy>();
    		pVec.addAll(Arrays.asList(pol));
    		v[i].policies = pVec;
    		
    		String[] perm = parsePermissions(c.getString(4));
    		v[i].permissions = perm;
		}
        
        return v;
    }

    public Cursor getReceivers(String type, Uri data) {

        // TODO: should check Uri scheme
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_COMPONENTS +" WHERE " + COLUMN_TYPE +" = '"+ type + "'", null);

        return cursor;
    }

} 