package com.uni.ailab.scp.provider;

import android.util.SparseArray;

public class ScpBox
{
	/*
	 * Dato che posso avere più occorrenze della stessa classe all'interno di
	 * uno stesso stack, devo poter memorizzare la coppia
	 * idComponente-PoliticaGlobale in quanto, in caso di pop, devo mantenere la
	 * politica nello stack per la presenza delle altre occorrenze della classe.
	 * Per risparmiare qlcsina, uso SparseArray anziche HashMap.
	 */
	private SparseArray<ScpPolicy> policyMap;

	public ScpBox()
	{
		policyMap = new SparseArray();
	}

	public void addPolicy(int componentId, ScpPolicy policy)
	{
		policyMap.append(componentId, policy);
	}

	/**
	 * 
	 * @param componentId
	 * @return true if there aren't other policies in this box, false otherwise
	 */
	public boolean removePolicy(int componentId)
	{
		policyMap.remove(componentId);
		return policyMap.size() == 0;
	}

	/**
	 * 
	 * @return le politiche globali come una stringa
	 */
	public String getGlobalPolicies()
	{
		String retValue = "";

		for (int i = 0; i <= policyMap.size(); i++)
		{
			retValue += policyMap.get(i).getCnf() + " 0 ";
		}

		return retValue;
	}
}
