package com.uni.ailab.scp.provider;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.util.Log;
import com.uni.ailab.scp.util.ScpConstant;

@SuppressLint("UseSparseArrays")
public class StacksSet
{
	private HashMap<Integer, ScpStack> stacksSet;

	public StacksSet()
	{
		stacksSet = new HashMap<Integer, ScpStack>();
	}

	/**
	 * TODO: LA COMPONENTE DEVE ESSERE GIÀ STATA VERIFICATA DAL SAT SOLVER!
	 * 
	 * Metodo per inserire le componenti negli stack. Saranno quelli a
	 * preoccuparsi di gestire le politiche sticky globali e locali
	 * 
	 * @param dadStackId
	 * @param componentId
	 * @param component
	 * @return
	 */
	public int pushComponent(int dadStackId, int componentId,
			Component componenta)
	{
		Component2 component = parse(componenta);

		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, pushComponent: "
				+ component.toString());

		/**
		 * Se la componente è un service devo creare un nuovo stack
		 */
		if (component.getType().equals(ScpConstant.COMPONENT_SERVICE))
		{
			ScpStack dadStack = null;
			ScpStack stack = null;

			/*
			 * creo un nuovo stack passandogli politiche e permessi dello stack
			 * padre. Utilizzo lo stesso id della componente, tanto so che è
			 * univoco..evito di generare un nuovo numero random..
			 */
			dadStack = stacksSet.get(dadStackId);

			stack = new ScpStack(componentId, dadStack.getPoliciesMap(),
					dadStack.getPermissionsMap(),
					dadStack.getGlobalPoliciesMap());

			stacksSet.put(componentId, stack);

			// inserisco la componente nello stack appena creato
			stack.push(component, componentId);

			// ritorno l'id dello stack creato
			return componentId;
		}
		// per tutte le altre componenti non devo fare nulla di particolare
		else
		{
			ScpStack dadStack = null;

			/*
			 * inserisco la componente nello stack indicato. Gli stack vengono
			 * creati o in fase di prima registrazione delle componenti ad SCP o
			 * in caso di service => non dovrei mai avere problemi.
			 */
			if (!stacksSet.containsKey(dadStackId))
			{
				// TODO: lancia eccezione appropriata
				return 0;
			}

			dadStack = stacksSet.get(dadStackId);

			dadStack.push(component, componentId);

			return dadStackId;
		}
	}

	public Component2 parse(Component componenta)
	{
		ArrayList<ScpPolicy> list = new ArrayList<>();
		list.add(new ScpPolicy(ScpConstant.POLICY_TYPE_LOCALL, componenta
				.getPolicy()));
		Component2 retValue = new Component2(componenta.getId(),
				componenta.getPackageName(), componenta.getClassName(), list,
				componenta.getType(), componenta.getPermission());
		return retValue;
	}

	public void updateComponent(int dadStackId, int componentId,
			Component component)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, updateComponent: "
				+ component.toString());
		// prendo un riferimento allo stack
		ScpStack dadStack = null;
		dadStack = stacksSet.get(dadStackId);

		// aggiorno il valore della componente
		dadStack.fillMaps(parse(component), componentId);
	}

	public int popComponent(int dadStackId, int componentId, String className)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, popComponent: "
				+ className);
		// prendo un riferimento allo stack
		ScpStack dadStack = null;
		int retValue = 0;
		dadStack = stacksSet.get(dadStackId);

		// poppo la componente passando il suo id per sicurezza (per debug)
		retValue = dadStack.pop(componentId, className);

		// elimino lo stack se vuoto
		if (dadStack.isEmpty())
		{
			stacksSet.remove(dadStackId);
		}

		// ritorno il valore della componente poppata
		return retValue;
	}

	public void createNewScpStack(int stackId)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, createNewScpStack");
		stacksSet.put(stackId, new ScpStack(stackId));
	}

	/**
	 * per ogni componente che voglio pushare devo fare due controlli: uno
	 * locale e uno universale. Posso pensare di tenerli separati (magari
	 * minisat è piú efficiente nell'eseguire due chiamate piuttosto che una piu
	 * cicciona..ma non credo) oppure fare un unico metodo. Implemento entrambe
	 * le soluzioni, poi vedrò quale sarà la piu efficiente
	 */

	/**
	 * Metodo che ritorna la cnf (locale) da inviare al Sat-Solver per
	 * verificare la possibile invocazione della componente.
	 * 
	 * @return
	 */
	public String getLocalCnf(int dadStackId, Component2 component)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, getLocalCnf");
		String retValue;
		ScpStack stack = stacksSet.get(dadStackId);

		String currentPermissions = component.getPermissionsAsString();

		/*
		 * per prima cosa devo verificare che l'insieme costituito dai permessi
		 * della componente e da quelli dello stack designato soddisfino
		 * l'insieme delle politiche locali
		 */
		retValue = currentPermissions + " 0 " + stack.getPermissionsAsString()
				+ " 0 " + component.getLocalPoliciesAsString() + " 0 "
				+ stack.getLocalPoliciesAsString() + " 0 ";

		return retValue;
	}

	public String getUniversalCnf(int dadStackId, Component2 component)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, getUniversalCnf");
		String retValue = "";

		/*
		 * quindi verifico che l'insieme dei permessi di tutti gli stack unito
		 * ai permessi della componente soddisfino l'insieme di tutte le
		 * politiche universali di tutti gli stack unito alle politiche
		 * universali della componente (in modo da verificare sia che la
		 * componente che aggiungo sia valida per le politiche universali gia
		 * presenti sui diversi stack, sia che le politiche universali della
		 * componente siano verificate da tutti i permessi di tutti gli stack)
		 */

		// prendo per ogni stack politiche globali e permessi, compreso il
		// mio stesso stack;
		for (ScpStack s : stacksSet.values())
		{
			retValue += s.getUniversalPoliciesAsString() + " 0 "
					+ s.getPermissionsAsString() + " 0 ";
		}

		// aggiungo i permessi e le politiche universali della componente
		// corrente non ancora presente sullo stack
		retValue += component.getUniversalPoliciesAsString() + " 0 "
				+ component.getPermissionsAsString();

		return retValue;
	}

	public String getCnf(int dadStackId, Component2 component)
	{
		Log.i(ScpConstant.LOG_TAG_SCPPROVIDER, "StacksSet, getCnf");
		// Prendo permessi, politiche locali e politiche universali della
		// componente corrente
		String retValue = component.getPermissionsAsString()
				+ component.getLocalPoliciesAsString()
				+ component.getUniversalPoliciesAsString();

		// prendo per ogni stack politiche globali e permessi;
		for (ScpStack s : stacksSet.values())
		{
			retValue += s.getUniversalPoliciesAsString()
					+ s.getPermissionsAsString();

			// quando arrivo allo stack designato, prendo anche i parametri
			// necessari per verificare la pushabilità locale
			if (s.getStackId() == dadStackId)
			{
				retValue += s.getLocalPoliciesAsString();
			}
		}

		return retValue;
	}

	// METODO DI DEBUG
	public String getStacksSetStatus()
	{
		String retValue = "Sono presenti " + stacksSet.size() + " stack\n";
		int count = 0;

		for (ScpStack s : stacksSet.values())
		{
			count++;
			retValue += "	Stack: " + count + ", id: " + s.getStackId()
					+ ", contiene " + s.size() + " componenti.\n"
					+ s.getScpStackStatus();
		}

		return retValue;
	}
}
