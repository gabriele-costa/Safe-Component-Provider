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
        int e = M.get(name);
        return ((neg) ? -e : e);
    }

    @Override
    public boolean equals(Object o) {

    }

}
