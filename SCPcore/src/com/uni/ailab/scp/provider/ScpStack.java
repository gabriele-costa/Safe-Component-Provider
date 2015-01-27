package com.uni.ailab.scp.provider;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.uni.ailab.scp.util.ScpConstant;

@SuppressLint("UseSparseArrays")
public class ScpStack
{
	private int stackId;
	private Stack<Integer> stack;
	private HashMap<Integer, ArrayList<ScpPolicy>> localPoliciesMap;
	private HashMap<Integer, String> permissionsMap;

	/*
	 * Uso come chiave il nome della classe in quando non posso conoscere gli id
	 * delle componenti quando devo eseguire la ricerca negli altri stack
	 */
	private HashMap<String, ScpBox> universalPoliciesMap;

	public ScpStack()
	{
		this(0);
	}

	public ScpStack(int stackId)
	{
		this.stackId = stackId;
		this.stack = new Stack<Integer>();
		this.localPoliciesMap = new HashMap<Integer, ArrayList<ScpPolicy>>();
		this.permissionsMap = new HashMap<Integer, String>();
		this.universalPoliciesMap = new HashMap<String, ScpBox>();
	}

	/**
	 * Costruttore per le componenti service che ereditano politiche e permessi
	 * da altri stack.
	 * 
	 * @param localPoliciesMap
	 * @param permissionsMap
	 */
	@SuppressWarnings("unchecked")
	public ScpStack(int stackId,
			HashMap<Integer, ArrayList<ScpPolicy>> localPoliciesMap,
			HashMap<Integer, String> permissionsMap,
			HashMap<String, ScpBox> globalPoliciesMap)
	{
		this.stackId = stackId;
		this.localPoliciesMap = (HashMap<Integer, ArrayList<ScpPolicy>>) localPoliciesMap
				.clone();
		this.permissionsMap = (HashMap<Integer, String>) permissionsMap.clone();
		this.universalPoliciesMap = (HashMap<String, ScpBox>) globalPoliciesMap
				.clone();
	}

	/**
	 * Metodo utilizzato per completare le componenti temporanee inserite sullo
	 * stack.
	 * 
	 * @param component
	 */
	public void fillMaps(Component2 component, int componentId)
	{
		String type = null;
		ScpPolicy current = null;

		// Devo controllare il tipo di ogni politica della componente
		Iterator<ScpPolicy> iterator = component.getPolicies().iterator();

		while (iterator.hasNext())
		{
			current = (ScpPolicy) iterator.next();
			type = current.getType();

			/*
			 * TODO: togli doppia LL non appena ti sei assicurato riferimenti
			 * sconosciuti sparsi nel codice
			 */
			if (type.equals(ScpConstant.POLICY_TYPE_UNIVERSAL))
			{
				/*
				 * Caso in cui la politica è universale: va inserita nella mappa
				 * delle politiche universali => guardo se gia presente nella
				 * mappa. Se non c'é creo l'apposito box e salvo la politica,
				 * altrimenti aggiungo la politica nel box.
				 */

				String className = component.getClassName();
				ScpBox box = universalPoliciesMap.get(className);

				if (box == null)
				{
					box = new ScpBox();
					universalPoliciesMap.put(component.getClassName(), box);
				}

				box.addPolicy(componentId, current);
			}
			else if (type.equals(ScpConstant.POLICY_TYPE_STICKY_LOCAL))
			{
				/*
				 * devo appiccicare la politica a tutte le componenti del mio
				 * stack
				 */
				for (ArrayList<ScpPolicy> list : localPoliciesMap.values())
				{
					list.add(current);
				}
			}
			/*
			 * TODO: togli doppia LL non appena ti sei assicurato riferimenti
			 * sconosciuti sparsi nel codice
			 */
			else if (type.equals(ScpConstant.POLICY_TYPE_LOCALL))
			{
				localPoliciesMap.put(componentId, component.getPolicies());
			}
		}

		// quindi inserisco i permessi nella mappa
		permissionsMap.put(componentId, component.getPermissionsAsString());
	}

	/**
	 * Push della componente sullo stack.
	 * 
	 * @param component
	 */
	public void push(Component2 component, int componentId)
	{
		/*
		 * per prima cosa push la componente sullo stack. Poi controllo se è
		 * temporanea: se si ritorno in quanto mi accontento di essermi segnato
		 * la sua esistenza, altrimenti elaboro la richiesta e vado a inserire
		 * le opportune voci mappe dei permessi e delle politiche
		 */

		stack.push(componentId);
		if (component.isTemporary())
		{
			return;
		}
		fillMaps(component, componentId);
	}

	/**
	 * 
	 * @param componentId
	 * @return l'id della componente rimossa o 0 nel caso vi sia un errore..MA
	 *         MI SERVE? non sarebbe meglio un boolean che mi indica se lo stack
	 *         è vuoto?
	 */
	public int pop(int componentId, String className)
	{
		/*
		 * controllo che effettivamente la componente da rimuovere sia in cima
		 * allo stack...x debug
		 */
		if (stack.peek() != componentId)
		{
			return 0;
		}
		else
		{
			int key = stack.pop();
			permissionsMap.remove(key);
			localPoliciesMap.remove(key);

			/*
			 * se classname non è nullo (caso in cui voglio rimuovere componenti
			 * temporanee..quindi quando l'utente non seleziona nulla dai
			 * dialog)
			 */
			if (className != null)
			{
				ScpBox box = universalPoliciesMap.get(className);

				/*
				 * rimuovo la politica del box e, nel caso fosse l'ultima
				 * rimasta di quella classe, rimuovo anche la entry dalla mappa
				 */
				if ((box != null) && (box.removePolicy(key)))
				{
					universalPoliciesMap.remove(className);
				}
			}

			return key;
		}
	}

	public int size()
	{
		return stack.size();
	}

	public boolean isEmpty()
	{
		return stack.isEmpty();
	}

	public HashMap<String, ScpBox> getGlobalPoliciesMap()
	{
		return this.universalPoliciesMap;
	}

	public HashMap<Integer, ArrayList<ScpPolicy>> getPoliciesMap()
	{
		return this.localPoliciesMap;
	}

	public HashMap<Integer, String> getPermissionsMap()
	{
		return this.permissionsMap;
	}

	public int getStackId()
	{
		return stackId;
	}

	/**
	 * 
	 * @return: null if the policies map is not complete, string if it's all ok
	 * 
	 */
	public String getLocalPoliciesAsString()
	{
		String retValue = "";

		for (ArrayList<ScpPolicy> policyList : localPoliciesMap.values())
		{
			/*
			 * controllo se la lista di politiche di quella componente non è
			 * nulla. Se si, ritorno null in quanto non voglio mai una lista
			 * inconsistente o non completa
			 */
			if (policyList == null)
			{
				return null;
			}
			for (ScpPolicy s : policyList)
			{
				retValue += s.getCnf();
			}
		}
		return retValue;
	}

	/**
	 * 
	 * @return: null if the policies map is not complete, string if it's all ok
	 * 
	 */
	public String getPermissionsAsString()
	{
		String retValue = "";

		for (String s : permissionsMap.values())
		{
			if (s == null)
			{
				return null;
			}
			else
			{
				retValue += s;
			}
		}
		return retValue;
	}

	public String getUniversalPoliciesAsString()
	{
		String retValue = "";

		for (ScpBox box : universalPoliciesMap.values())
		{
			/*
			 * controllo se la lista di politiche di quella componente non è
			 * nulla. Se si, ritorno null in quanto non voglio mai una lista
			 * inconsistente o non completa
			 */
			retValue += box.getGlobalPolicies();
		}
		return retValue;
	}

	public String getScpStackStatus()
	{
		String retValue = "";

		Iterator<Integer> i = stack.iterator();

		while (i.hasNext())
		{
			retValue += i.next() + "\n";
		}

		return retValue;
	}
}
