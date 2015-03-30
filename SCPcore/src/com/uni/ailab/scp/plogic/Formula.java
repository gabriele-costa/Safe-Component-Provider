package com.uni.ailab.scp.plogic;

import java.util.Map;

/**
 * Created by gabriele on 30/03/15.
 */
public interface Formula {

    public Formula toCNF();

    public boolean isCNF();

    public int[][] toDIMACS(Map<String,Integer> M);

    public Formula negate();
}
