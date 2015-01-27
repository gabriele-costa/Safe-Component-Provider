package com.uni.ailab.scp.provider;

public class resBox
{
	private int count;
	private String n_a_p;

	public resBox()
	{
		this.count = 0;
		this.n_a_p = "";
	}

	public int getCount()
	{
		return this.count;
	}

	public String getNap()
	{
		return this.n_a_p;
	}

	public void putPermission(String permission)
	{
		this.n_a_p += permission + " $$ ";
	}

	public void cntIncrement()
	{
		this.count++;
	}
}
