package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.cnf.Formula;
import com.uni.ailab.scp.policy.Policy;
import com.uni.ailab.scp.policy.Scope;

import java.util.Vector;

/**
 * Created by gabriele on 26/03/15.
 */
public class Frame {

    public String component;
    public String[] permissions;
    public Vector<Policy> policies;

    public Vector<Formula> getScopePolicies(Scope scope) {
        Vector<Formula> v = new Vector<Formula>();
        for(Policy p : policies)
            if(p.scope == scope)
                v.add(p.formula);
        return v;
    }
    
    @Override
    public String toString() {
    	return component;
    }
}
