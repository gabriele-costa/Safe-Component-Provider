package com.uni.ailab.scp.provider;

import java.util.ArrayList;

import android.util.Log;

import com.uni.ailab.scp.util.ScpConstant;

public class Component2
{
	private int _id;
	private String className;
	private String packageName;
	private ArrayList<ScpPolicy> policies;
	private String type;
	private String permissions;

	public Component2()
	{
		this(0, null, null);
	}

	public Component2(int _id, String packageName, String className)
	{
		this(_id, packageName, className, null, null, null);
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: costruttore!");
	}

	public Component2(int _id, String packageName, String className,
			ArrayList<ScpPolicy> policies, String type, String permissions)
	{
		this._id = _id;
		this.packageName = packageName;
		this.className = className;
		this.policies = policies;
		this.type = type;
		this.permissions = permissions;

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

	public String getPackageName()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPackageName");
		return this.packageName;
	}

	public ArrayList<ScpPolicy> getPolicies()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "Component: getPolicy");
		return this.policies;
	}

	public String getPermissionsAsString()
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
				+ this.permissions + ", Politica " + this.policies;
	}

	public boolean isTemporary()
	{
		return className == null;
	}

	public String getLocalPoliciesAsString()
	{
		return policyToString(ScpConstant.POLICY_TYPE_LOCALL);
	}

	public String getUniversalPoliciesAsString()
	{
		return policyToString(ScpConstant.POLICY_TYPE_UNIVERSAL);
	}

	private String policyToString(String type)
	{
		String retValue = "";

		for (ScpPolicy policy : this.policies)
		{
			if (policy.getType().equals(type))
			{
				retValue = policy.getCnf();
			}
		}
		return retValue;
	}

}
