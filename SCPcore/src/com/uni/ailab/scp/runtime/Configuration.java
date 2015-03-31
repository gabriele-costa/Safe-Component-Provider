package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.cnf.Clause;
import com.uni.ailab.scp.cnf.Literal;
import com.uni.ailab.scp.policy.Permissions;
import com.uni.ailab.scp.policy.Policy;

import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;
import java.util.Vector;

import com.uni.ailab.scp.cnf.Formula;

/**
 * Created by gabriele on 26/03/15.
 */
public class Configuration extends ArrayList<Stack> {

    private IVec<IVecInt> encodePermissions() {

        String[][][] PC = new String[this.size()][][];
        for (int i = 0; i < this.size(); i++) {
            String[][] PS = this.get(i).getPermissions();
        }

        Vector<Formula> formulas = new Vector<Formula>();

        Vector<Clause> claij = new Vector<Clause>();
        for(int i = 0; i < PC.length; i++) {
            Vector<Literal> lit3 = new Vector<Literal>();

            for(int j = 0; j < PC[i].length; j++) {
                Vector<Literal> lit2 = new Vector<Literal>();

                for (String pij : PC[i][j]) {
                    claij.add(new Clause(new Literal(permToVar(pij, i, j), false)));
                    lit2.add(new Literal(permToVar(pij, i, j), false));
                    lit3.add(new Literal(permToVar(pij, i, 0), false));
                }
                Formula f = Formula.fromClause(new Clause(lit2));

                for(String pi : Permissions.PERMISSIONS)
                    formulas.add(Formula.iff(f, Formula.lit(permToVar(pi, i, 0))));
            }
        }

        // encode eq 1
        formulas.add(Formula.fromClause(claij));

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

    private String permToVar(String enc, int s, int f) {
        if(s == 0)
            return enc;
        else if(f == 0)
            return enc + "_" + s;
        else {
            return enc + "_" + s + "_" + f;
        }
    }

    private String varToPerm(String var) {
        if(var.contains("_")) {
            return var.substring(0, var.indexOf("_"));
        }
        else
            return var;
    }

}
