package com.uni.ailab.scp.provider;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.uni.ailab.scp.util.ScpConstant;

public class Node<T>
{
	// TODO: dato che la classe nodo non sarà parte dell'API, se controlli tutti
	// i metodi che la utilizzano puoi pensare di rimuovere i check null

	private int id;
	private T data;
	private Node<T> parent;
	private List<Node<T>> children;

	public Node()
	{
		this(0, null);
	}

	public Node(int id)
	{
		this(id, null, null);
	}

	public Node(int id, T data)
	{
		this(id, data, null);
	}

	public Node(int id, T data, Node<T> parent)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: costruttore id " + id);
		this.id = id;
		this.data = data;
		this.parent = parent;
		this.children = new LinkedList<Node<T>>();
	}

	public boolean isRoot()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: isRoot");
		return (this.parent == null);
	}

	public boolean isLeaf()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: isLeaf");
		return (this.children.isEmpty());
	}

	public boolean isEmpty()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: isEmpty");
		return (this.data == null);
	}

	/**
	 * 
	 * @param node
	 * @param parentId
	 * @return il nodo appena inserito o null
	 */
	public boolean addChild(Node<T> node, int parentId)
	{
		// TODO: anche qua stiamo controllando se nodo è nullo.. magari da
		// qualche parte togli questo controllo
		// dobbiamo controllare che parentId sia diverso da zero??

		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: addChild");
		if (node == null)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: addChild, nodo nullo");
			return false;
		}
		// controllo se il nodo corrente è il parent a cui appendete il nodo
		if (this.id == parentId)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: addChild, trovato il parent, appendo il nodo "
							+ parentId);
			// insert the node into the childrenList
			children.add(node);
			// set the parent node
			node.setParent(this);
			return true;
		}
		else
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: addChild, parte iterazione");
			// altrimenti faccio partire l'iterazione sui nodi figli
			if (this.children.size() != 0)
			{
				for (Node<T> n : children)
				{
					if (n.addChild(node, parentId))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean childSuicide(Node<T> node)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: childSuicide");
		return this.children.remove(node);
	}

	/**
	 * 
	 * @param node
	 * @return True se la cancellazione va a buon fine, false altrimenti
	 */
	// dovrei aver implementato una ricerca in profondità
	public boolean removeChild(int id)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: removeChild");
		if (id == 0)
		{
			throw new NullPointerException("Id mustn't be zero");
		}

		// RIMUOVO SOLO I NODI FOGLIA
		if ((this.children.isEmpty() && (this.id == id)))
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: removeChild, sono la foglia, mi elimino "
							+ this.id);

			// mi rimuovo dalla lista del mio babbo:
			if ((this.parent != null) && (this.parent.childSuicide(this)))
			{
				// mi azzero
				this.id = 0;
				this.data = null;
				this.parent = null;
				this.children = null;

				return true;
			}
		}
		else
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: removeChild, faccio partire l'iterazione");
			for (Node<T> n : children)
			{
				if (n.removeChild(id))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean setData(T data, int id)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: setData");
		if (data == null)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: setData, data nullo");
			return false;
		}
		if (this.id == id)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: setData, sono il nodo dove insereri il data "
							+ this.id);
			this.data = data;
			return true;
		}
		else
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: setData, faccio partire l'iterazione");
			for (Node<T> n : children)
			{
				if (n.setData(data, id))
				{
					return true;
				}
			}
		}
		return false;
	}

	public int getParentId()
	{
		return this.parent.getId();
	}

	private boolean setParent(Node<T> node)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: setParent");
		if (node == null)
		{
			Log.i(ScpConstant.LOG_TAG_SCPPROVIDER,
					"ScpNode: setParent, nodo nullo");
			return false;
		}
		this.parent = node;
		return true;
	}

	public T getData()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: getData");
		return this.data;
	}

	public int getId()
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: getId");
		return this.id;
	}

	public int countChild()
	{
		return this.children.size();
	}

	// Per me possono esistere due nodi aventi stessa componente e stessi figli,
	// in quanto posso avere più istanze della stessa componente con i relativi
	// flussi di invocazione => l'unica cosa univoca deve essere l'id!
	@Override
	public boolean equals(Object o)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "ScpNode: equals overridato");
		if (o == null)
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof Node))
		{
			return false;
		}

		Node<?> other = (Node<?>) o;

		if ((this.id) == (other.id))
		{
			return true;
		}

		return false;
	}
}
