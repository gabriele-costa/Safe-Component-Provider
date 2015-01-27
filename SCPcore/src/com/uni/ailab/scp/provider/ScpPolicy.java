package com.uni.ailab.scp.provider;

public class ScpPolicy
{
	private String type;
	private String cnf;

	public ScpPolicy()
	{
		this(null, null);
	}

	public ScpPolicy(String type, String cnf)
	{
		this.type = type;
		this.cnf = cnf;
	}

	public String getType()
	{
		return this.type;
	}

	public String getCnf()
	{
		return this.cnf;
	}
}
