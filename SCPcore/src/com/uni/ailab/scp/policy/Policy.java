package com.uni.ailab.scp.policy;

import net.sf.tweety.commons.Formula;

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
