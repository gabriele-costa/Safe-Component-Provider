package com.uni.ailab.scp.policy;

import com.uni.ailab.scp.plogic.Formula;
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

}
