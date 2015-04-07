package com.uni.ailab.scp.cnf;

import java.util.Map;

/**
 * Created by gabriele on 30/03/15.
 */

public class Literal {

    public String name;
    public boolean neg;

    public Literal(String n, boolean not) {
        name = n;
        neg = not;
    }

    public int toDIMACS(Map<String, Integer> M) {
        if(!M.containsKey(name))
            M.put(name, M.size()+1);

        int e = M.get(name);
        return ((neg) ? -e : e);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Literal))
            return false;

        Literal l = (Literal) o;
        return (l.name.compareTo(name) == 0) && (l.neg == neg);
    }
    
    public static Literal parse(String s) {
    	if(s.startsWith("!"))
    		return new Literal(s.substring(1), true);
    	else
    		return new Literal(s, false);
    }
    
    @Override
    public String toString() {
    	if(neg)
    		return "!" + name;
    	else
    		return name;
    }

}
