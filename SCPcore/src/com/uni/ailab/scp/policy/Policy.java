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
    	
    	if(sticky) {
    		s += "S";
    	}
    	
    	return s + formula.toString();
    }
    
    public static Policy parse(String s) throws IllegalArgumentException {
    	
    	Scope sc;
    	boolean st;
    	Formula f;
    	
    	if(s.startsWith("D")) {
    		sc = Scope.DIRECT;
    	}
    	else if(s.startsWith("L")) {
    		sc = Scope.LOCAL;
    	}
    	else if(s.startsWith("G")) {
    		sc = Scope.GLOBAL;
    	}
    	else {
    		throw new IllegalArgumentException("Cannot parse " + s);
    	}
    	
    	s = s.substring(1);
    	
    	if(s.startsWith("S")) {
    		st = true;
    		s = s.substring(1);
    	}
    	else {
    		st = false;
    	}
    	
    	f = Formula.parse(s);
    	
    	return new Policy(sc, f, st);
    	
    }
}
