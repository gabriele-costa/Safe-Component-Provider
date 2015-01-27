package com.uni.ailab.scp.provider;

import android.util.Log;
import android.util.SparseArray;

import com.uni.ailab.scp.util.ScpConstant;

public class ScpTree
{
	private SparseArray<String> policies;
	private SparseArray<String> permissions;
	private Node<Component> root;

	public ScpTree()
	{
		this(null);
	}

	public ScpTree(Node<Component> root)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: costruttore");
		this.permissions = new SparseArray<String>();
		this.policies = new SparseArray<String>();
		this.root = root;

		if ((root != null))
		{
			Component component = root.getData();

			if (component != null)
			{
				Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
						"ScpTree: costruttore, caso root = nodo normale");
				this.permissions.put(root.getId(), component.getPermission());
				this.policies.put(root.getId(), component.getPolicy());

				return;
			}
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpTree: costruttore, caso root = nodo temporaneo");
		}
	}

	// mi lascio la possibilità di usare questo metodo per resettare l'albero in
	// caso di sua cancellazione => non faccio controllo sul nodo perke potrei
	// volerlo mettere di proposito a null;
	public void setRoot(Node<Component> root)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: setRoot");

		if (root == null)
		{
			throw new IllegalArgumentException(
					"Node<Component> root mustn't be null");
		}
		if (root.getParentId() != 0)
		{
			// TODO: creati una tua eccezione..
			throw new NullPointerException(
					"Node<Component> root must have parentId = 0");
		}

		Component component = root.getData();

		// se la root non è temporanea aggiungo le politiche e i permessi
		if (component != null)
		{
			this.permissions.put(root.getId(), component.getPermission());
			this.policies.put(root.getId(), component.getPolicy());
		}
	}

	/**
	 * 
	 * @param node
	 * @param parentId
	 * @param flagTemp
	 * @return true se l'inserimento è andato a buon fine, false altrimenti;
	 */
	public boolean addNode(Node<Component> node, int parentId)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: addNode");

		if ((node == null) || (parentId == 0))
		{
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpTree: addNode, nodo nullo o parentId = 0");

			throw new IllegalArgumentException(
					"Node<Component> node mustn't be null or have parentId = 0");
		}

		if (root.addChild(node, parentId))
		{
			Component component = node.getData();

			// se non sono un nodo temporaneo aggiungo politiche e permessi
			if ((component != null))
			{
				Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
						"ScpTree: caso nodo normale");
				this.permissions.put(root.getId(), component.getPermission());
				this.policies.put(root.getId(), component.getPolicy());
				return true;
			}
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpTree: caso nodo temporaneo");
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param node
	 * @return true se la cancellazione è andata a buon fine, false altrimenti
	 */
	// a noi interessa rimuovere solo i nodi foglia!
	public boolean removeNode(int id)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: removeNode");

		if (id == 0)
		{
			throw new IllegalArgumentException("Id mustn't be zero");
		}

		if (root.removeChild(id))
		{
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpTree: removeNode, sono io il nodo");

			this.permissions.remove(id);
			this.policies.remove(id);
			return true;
		}
		return false;
	}

	/**
	 * Serve per andare ad aggiungere la componente ai nodi temporanei! => deve
	 * avere politica e permessi validi
	 * 
	 * @param component
	 * @param id
	 * @return true se la component viene assegnata, false altrimenti
	 */
	public boolean setData(Component component, int id)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: setData");

		if (component == null)
		{
			throw new IllegalArgumentException("Component mustn't be null");
		}

		if ((component.getPermission() == null)
				|| (component.getPolicy() == null))
		{
			// TODO: inventati tua eccezione
			throw new NullPointerException(
					"Component policy or permissions mustn't be null");
		}

		if (root.setData(component, id))
		{
			this.permissions.put(id, component.getPermission());
			this.policies.put(id, component.getPolicy());
			return true;
		}

		return false;
	}

	public String printPolicies()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: printPolicies");
		String retValue = "";
		int key = 0;

		for (int i = 0; i < policies.size(); i++)
		{
			key = policies.keyAt(i);
			retValue += policies.get(key) + " 0 ";
		}

		return retValue;
	}

	public String printPermissions()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpTree: printPermissions");
		String retValue = "";
		int key = 0;

		for (int i = 0; i < permissions.size(); i++)
		{
			key = permissions.keyAt(i);
			retValue += permissions.get(key) + " 0 ";
		}

		return retValue;
	}

	public int countNode()
	{
		return this.root.countChild();
	}
}
