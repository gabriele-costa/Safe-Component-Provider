package com.uni.ailab.scplib;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;

public class ContentResolverScp extends ContentResolver
{
	private ContextScp contextScp;
	private ContentResolver contentResolver;

	public ContentResolverScp(Context context, ContextScp contextScp)
	{
		super(context);
		this.contextScp = contextScp;
		contentResolver = context.getContentResolver();
	}

	public Cursor queryScp(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder,
			CancellationSignal cancellationSignal)
	{
		contextScp.checkUri(uri);
		return contentResolver.query(uri, projection, selection, selectionArgs,
				sortOrder, cancellationSignal);
	}

	public int deleteScp(Uri url, String where, String[] selectionArgs)
	{
		contextScp.checkUri(url);
		return contentResolver.delete(url, where, selectionArgs);
	}

	public int updateScp(Uri uri, ContentValues values, String where,
			String[] selectionArgs)
	{
		contextScp.checkUri(uri);
		return contentResolver.update(uri, values, where, selectionArgs);
	}

	public Uri insertScp(Uri url, ContentValues values)
	{
		contextScp.checkUri(url);
		return contentResolver.insert(url, values);
	}

}
