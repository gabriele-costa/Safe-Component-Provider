package com.uni.ailab.scp.provider;

import android.util.Log;

import com.uni.ailab.scp.util.ScpConstant;

public class Component
{
	private int _id;
	private String className;
	private String packageName;
	private String policy;
	private String type;
	private String permissions;
	private String policyType;

	public Component()
	{
		this(0, null, null);
	}

	public Component(String type)
	{
		this(0, null, null, null, type, null, null);
	}
	
	public Component(int _id, String packageName, String className)
	{
		this(_id, packageName, className, null, null, null, null);
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: costruttore!");
	}

	public Component(int _id, String packageName, String className,
			String policy, String type, String permissions, String policyType)
	{
		this._id = _id;
		this.packageName = packageName;
		this.className = className;
		this.policy = policy;
		this.type = type;
		this.permissions = permissions;
		this.policyType = policyType;

		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: costruttore!");
	}

	public int getId()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getId");
		return this._id;
	}

	public String getClassName()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getClassName");
		return this.className;
	}

	public String getPolicyType()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPolicyType");
		return this.policyType;
	}
	
	public String getPackageName()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPackageName");
		return this.packageName;
	}

	public String getPolicy()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPolicy");
		return this.policy;
	}

	public String getPermission()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPermission");
		return this.permissions;
	}
	
	/**
	 * 
	 * @return the type of this permission: Activity, BroadcastReceiver..
	 */
	
	public String getType()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getType");
		return this.type;
	}
	
	@Override
	public String toString()
	{
		return "ID " + this._id + ", Classe componente " + this.className
				+ ", Package componente " + this.packageName
				+ ", Tipo componente " + this.type + ", Permessi "
				+ this.permissions + ", Politica " + this.policy
				+ ", Tipo di politica " + this.policyType;
	}
}
