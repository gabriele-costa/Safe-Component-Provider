package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.policy.Permissions;
import com.uni.ailab.scp.policy.Policy;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;
import orbital.logic.sign.SymbolBase;
import orbital.moon.logic.ClassicalLogic;

/**
 * Created by gabriele on 26/03/15.
 */
public class Configuration extends ArrayList<Stack> {

    private IVec<IVecInt> encodePermissions() {

        String[][][] PC = new String[this.size()][][];
        for (int i = 0; i < this.size(); i++) {
            String[][] PS = this.get(i).getPermissions();
        }

        // TODO: partially implemented

        // encode eq 1

        // encode eq 2

        // encode eq 3

        return null;
    }

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

    private int encToVar(int enc, int s, int f) {
        if(s == 0)
            return enc;
        else if(f == 0)
            return (Permissions.PERMISSIONS.length * (s-1)) + enc - 1;
        else {
            int base = Permissions.PERMISSIONS.length * (this.size());
            for (int i = 0; i < s-1; i++) {
                base += Permissions.PERMISSIONS.length * this.get(i).size();
            }
            return base + enc - 1;
        }
    }

    private int varToEnc(int var) {
        if(var < Permissions.PERMISSIONS.length)
            return var;
        else if(var < Permissions.PERMISSIONS.length * this.size())
            return (var % Permissions.PERMISSIONS.length) + 1;
        else {
            var -= (Permissions.PERMISSIONS.length * this.size());
            for (int i = 0; var > Permissions.PERMISSIONS.length; i++) {
                var -= Permissions.PERMISSIONS.length * this.get(i).size();
            }

            return var;
        }
    }

}
