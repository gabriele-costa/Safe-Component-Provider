package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.cnf.Formula;
import com.uni.ailab.scp.policy.Policy;
import com.uni.ailab.scp.policy.Scope;

import java.util.Vector;

/**
 * Created by gabriele on 26/03/15.
 */
public class Stack extends java.util.Stack<Frame> {

    public boolean contains(String component) {
        for(Frame f : this)
            if(f.component.compareTo(component) == 0)
                return true;

        return false;
    }

    public Policy[][] getPolicies() {

        Policy[][] pol = new Policy[this.size()][];

        for (int i = 0; i < this.size(); i++) {
            pol[i] = this.elementAt(i).policies.toArray(new Policy[] {});
        }

        return pol;
    }

    public String[][] getPermissions() {

        String[][] perm = new String[this.size()][];

        for (int i = 0; i < this.size(); i++) {
            perm[i] = this.elementAt(i).permissions;
        }

        return perm;
    }

    public Vector<Formula> getDirect() {
        Vector<Formula> Phi = new Vector<Formula>();
        for(Frame f : this) {
            Phi.addAll(f.getScopePolicies(Scope.DIRECT));
        }
        return Phi;
    }

    public Vector<Formula> getLocal() {
        Vector<Formula> Phi = new Vector<Formula>();
        for(Frame f : this) {
            Phi.addAll(f.getScopePolicies(Scope.LOCAL));
        }
        return Phi;
    }

    public Vector<Formula> getGlobal() {
        Vector<Formula> Phi = new Vector<Formula>();
        for(Frame f : this) {
            Phi.addAll(f.getScopePolicies(Scope.GLOBAL));
        }
        return Phi;
    }

    public Vector<Policy> getSticky() {
        Vector<Policy> Phi = new Vector<Policy>();
        for(Frame f : this) {
            for(Policy p : f.policies)
                if(p.sticky)
                    Phi.add(p);
        }
        return Phi;
    }
}
