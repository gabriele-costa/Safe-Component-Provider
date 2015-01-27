package com.uni.ailab.scp.provider;

import java.util.LinkedList;

import com.uni.ailab.scp.util.ScpConstant;

import android.util.Log;

public class ScpForest
{
	private LinkedList<ScpTree> forest;

	public ScpForest()
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpForest: costruttore");
		forest = new LinkedList<ScpTree>();
	}

	// TODO: il nostro generatore di numeri casuali potrebbe generare zero...
	public boolean removeNode(int id)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpForest: removeNode");
		if (id == 0)
		{
			throw new IllegalArgumentException("id mustn't be zero");
		}
		for (ScpTree tree : forest)
		{
			if (tree.removeNode(id))
			{
				return true;
			}
		}
		return false;
	}

	// metodo per andare ad inserire un nodo sotto un dato nodo padre
	public boolean addNode(Node<Component> node, int dadId)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpForest: addNode");
		if (node == null)
		{
			throw new IllegalArgumentException(
					"Node<Component> node mustn't be null");
		}
		if (dadId == 0)
		{
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpForest: addNode dadId=0, devo creare una root");
			// caso in cui voglio creare un nuovo albero
			ScpTree tree = new ScpTree(node);

			return forest.add(tree);
		}
		for (ScpTree tree : forest)
		{
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpForest: addNode, itero tra gli alberi");
			if (tree.addNode(node, dadId))
			{
				return true;
			}
		}
		return false;
	}

	// metodo che va ad inserire una certa componente in un dato nodo
	public boolean setData(Component component, int nodeId)
	{
		Log.d(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpForest: setData");

		if (component == null)
		{
			throw new IllegalArgumentException("Component mustn't be null");
		}

		for (ScpTree tree : forest)
		{
			Log.d(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpForest: setData, itero tra gli alberi");
			if (tree.setData(component, nodeId))
			{
				return true;
			}
		}
		return false;
	}

	public String print()
	{
		int count = this.forest.size();
		String ret = "La foresta è composta da " + count + " alberi.\n";
		if (count != 0)
		{
			int k = 0;
			int i = 0;
			for (ScpTree tree : forest)
			{
				k = tree.countNode() + 1;
				ret = ret + "Albero " + i + " contiene " + k + " nodi.\n";
				i++;
			}
		}

		return ret;
	}

	/**
	 * Metodo che verifica l'esistenza dell'id ricevuto come nodo foglia e
	 * appartenente a un content provider. Se passa questo controllo, "attiva"
	 * il nodo foglia e ritorna true, altrimenti ritorna false
	 * 
	 * @param nodeId
	 * @return
	 */
	public boolean validateId(int nodeId)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
