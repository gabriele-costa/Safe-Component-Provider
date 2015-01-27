package com.uni.ailab.scp.receiver;

public class Component
{
	private String pack;
	private String clazz;
	private String type;
	private String permissions;
	private String policy;
	private String ptype;
	private String ifilter;

	public Component()
	{
		pack = null;
		clazz = null;
		type = null;
		permissions = null;
		policy = null;
		ptype = null;
		ifilter = null;
	}

	public Component(String pa, String c, String t, String pe, String po,
			String pt, String i)
	{
		pack = pa;
		clazz = c;
		type = t;
		permissions = pe;
		policy = po;
		ptype = pt;
		ifilter = i;
	}
}