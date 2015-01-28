package com.uni.ailab.scplib.util;

public class SecureComponentNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SecureComponentNotFoundException()
	{
		super();
	}

	public SecureComponentNotFoundException(String message)
	{
		super(message);
	}
}
