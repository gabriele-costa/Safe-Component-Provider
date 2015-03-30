package com.uni.ailab.scp.plogic;

import java.util.Map;

/**
 * Created by gabriele on 30/03/15.
 */

public class Atom implements Formula {

    public String name;
    public boolean neg;

    public Atom(String n, boolean not) {
        name = n;
        neg = not;
    }

    public Formula toCNF() {
        return new Atom(name, neg);
    }

    @Override
    public boolean isCNF() {
        return true;
    }

    @Override
    public int[][] toDIMACS(Map<String, Integer> M) {
        int e = M.get(name);
        return new int[][] {new int[] {((neg) ? -e : e)}};
    }

    @Override
    public Formula negate() {
        return new Atom(name, !neg);
    }

}
