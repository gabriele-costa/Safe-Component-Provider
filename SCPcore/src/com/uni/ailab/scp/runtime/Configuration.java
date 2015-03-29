package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.policy.Policy;

import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;

/**
 * Created by gabriele on 26/03/15.
 */
public class Configuration extends ArrayList<Stack> {

    public IVec<IVecInt> encodePop(String component) {

        // TODO NYI
        return null;
    }

    public IVec<IVecInt> encodePush(Frame f, String component, boolean alloc) {

        // TODO NYI
        return null;
    }

    public void pop(String component) {
        // TODO NYI
    }

    public void push(Frame frame, String component, boolean alloc) {
        Stack S = new Stack();
        for (Stack T : this) {
            if (T.contains(component)) {
                S = T;
                break;
            }
        }

        ArrayList<Policy> PSticky = S.getSticky();

        if(alloc) {
            Stack V = new Stack();
            frame.policies.addAll(PSticky);
            V.add(frame);
            this.add(V);
        }
        else {
            frame.policies.addAll(PSticky);
            S.add(frame);
        }
    }

}
