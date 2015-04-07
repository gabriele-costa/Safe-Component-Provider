package com.uni.ailab.scp.policy;

import com.uni.ailab.scp.cnf.Formula;
/**
 * Created by gabriele on 26/03/15.
 */
public class Policy {

    public boolean sticky;
    public Scope scope;
    public Formula formula;

    public Policy(Scope s, Formula f, boolean st) {
        sticky = st;
        scope = s;
        formula = f;
    }
    
    public String toString() {
    	String s = "";
    	
    	switch (scope) {
    	case DIRECT:
    		s = "D";
    	case LOCAL: 
    		s = "L";
    	case GLOBAL: 
    		s = "G";
    	}
    	
    	 return s + formula.toString();
    }
}
