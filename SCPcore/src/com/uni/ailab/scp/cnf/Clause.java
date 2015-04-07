package com.uni.ailab.scp.cnf;

import java.util.Map;
import java.util.Vector;

/**
 * Created by gabriele on 30/03/15.
 */
public class Clause {

    public Vector<Literal> literals;

    public Clause(Vector<Literal> l) {
        literals = l;
    }

    public Clause(Literal[] l) {
        literals = new Vector<Literal>();
        for(Literal lit : l)
            literals.add(lit);
    }

    public Clause(Literal lit) {
        literals = new Vector<Literal>();
        literals.add(lit);
    }

    public int[] toDIMACS(Map<String, Integer> M) {

        int[] result = new int[literals.size()];
        for (int i = 0; i < literals.size(); i++) {
            result[i] = literals.get(i).toDIMACS(M);
        }
        return result;
    }

    public Clause rename(String suffix) {
        Vector<Literal> vl = new Vector<Literal>();
        for(Literal l : literals) {
            vl.add(new Literal(l.name + suffix, l.neg));
        }
        return new Clause(vl);
    }
    
    public static Clause parse(String s) {
    	s = s.substring(s.indexOf("[") + 1, s.lastIndexOf("]"));
    	String[] slit = s.split(",");
    	Literal[] lit = new Literal[slit.length];
    	for(int i = 0; i < slit.length; i++) {
    		lit[i] = Literal.parse(slit[i]);
    	}
    	return new Clause(lit);
    }
    
    @Override
    public String toString() {
    	String s = "[";
    	for(Literal l : literals) {
    		s += l.toString() + ",";
    	}
    	return s.substring(0, s.lastIndexOf(",")) + "]";
    }
}
