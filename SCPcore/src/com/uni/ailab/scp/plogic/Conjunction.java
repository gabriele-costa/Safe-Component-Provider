package com.uni.ailab.scp.plogic;

import java.util.Map;

/**
 * Created by gabriele on 30/03/15.
 */
public class Conjunction implements Formula {

    public Formula[] f;

    public Conjunction(Formula[] g) {
        f = g;
    }

    @Override
    public Formula toCNF() {
        return null;
    }

    @Override
    public boolean isCNF() {
        for(Formula g : f) {
            if(!g.isCNF())
                return false;
        }
        return true;
    }

    @Override
    public int[][] toDIMACS(Map<String, Integer> M) {
        if(!isCNF())
            return new int[0][0];

        int[][] result = new int[f.length][];
        for (int i = 0; i < f.length; i++) {
            result[i] = f[i].toDIMACS(M)[0];
        }
        return result;
    }

    @Override
    public Formula negate() {
        // TODO should make a copy
        return new Negation(this);
    }
}
