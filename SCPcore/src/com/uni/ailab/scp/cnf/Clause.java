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
}
