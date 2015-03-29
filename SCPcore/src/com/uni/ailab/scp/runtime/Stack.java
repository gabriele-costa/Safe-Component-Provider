package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.policy.Policy;
import com.uni.ailab.scp.policy.Scope;

import java.util.ArrayList;

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

    public ArrayList<Policy> getDirect() {
        ArrayList<Policy> Phi = new ArrayList<Policy>();
        for(Frame f : this) {
            for(Policy p : f.policies)
                if(p.scope == Scope.DIRECT)
                    Phi.add(p);
        }
        return Phi;
    }

    public ArrayList<Policy> getLocal() {
        ArrayList<Policy> Phi = new ArrayList<Policy>();
        for(Frame f : this) {
            for(Policy p : f.policies)
                if(p.scope == Scope.LOCAL)
                    Phi.add(p);
        }
        return Phi;
    }

    public ArrayList<Policy> getGlobal() {
        ArrayList<Policy> Phi = new ArrayList<Policy>();
        for(Frame f : this) {
            for(Policy p : f.policies)
                if(p.scope == Scope.GLOBAL)
                    Phi.add(p);
        }
        return Phi;
    }

    public ArrayList<Policy> getSticky() {
        ArrayList<Policy> Phi = new ArrayList<Policy>();
        for(Frame f : this) {
            for(Policy p : f.policies)
                if(p.sticky)
                    Phi.add(p);
        }
        return Phi;
    }
}
