package com.uni.ailab.scp.plogic;

import java.util.Map;

/**
 * Created by gabriele on 30/03/15.
 */
public class Negation implements Formula {

    public Formula f;

    public Negation(Formula g) {
        f = g;
    }

    @Override
    public Formula toCNF() {
        if(f instanceof Atom)
            return f.negate();
        else if(f instanceof Negation)
            return f.negate();
        else
            // TODO
        return null;
    }

    @Override
    public boolean isCNF() {
        return false;
    }

    @Override
    public int[][] toDIMACS(Map<String, Integer> M) {
        return new int[0][0];
    }

    @Override
    public Formula negate() {
        // TODO should be a copy of f
        return f;
    }
}
