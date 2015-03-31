package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.cnf.Clause;
import com.uni.ailab.scp.cnf.Literal;
import com.uni.ailab.scp.policy.Permissions;
import com.uni.ailab.scp.policy.Policy;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import com.uni.ailab.scp.cnf.Formula;
import com.uni.ailab.scp.policy.Scope;

/**
 * Created by gabriele on 26/03/15.
 */
public class Configuration extends ArrayList<Stack> {

    TreeMap<String, Integer> M;

    private IVec<IVecInt> encodePermissions(ArrayList<Stack> conf) {

        String[][][] PC = new String[conf.size()][][];
        for (int i = 0; i < conf.size(); i++) {
            String[][] PS = conf.get(i).getPermissions();
        }

        Vector<Formula> formulas = new Vector<Formula>();

        Vector<Clause> claij = new Vector<Clause>();
        for(int i = 0; i < PC.length; i++) {
            Vector<Literal> lit3 = new Vector<Literal>();

            for(int j = 0; j < PC[i].length; j++) {
                Vector<Literal> lit2 = new Vector<Literal>();

                for (String p : PC[i][j]) {
                    claij.add(new Clause(new Literal(permToVar(p, i, j), false)));
                    lit2.add(new Literal(permToVar(p, i, j), false));
                    Literal pi = new Literal(permToVar(p, i, 0), false);
                    if(!lit3.contains(pi))
                        lit3.add(pi);
                }
                // encode eq 2
                Formula f = Formula.fromClause(new Clause(lit2));

                for(String pi : Permissions.PERMISSIONS)
                    formulas.add(Formula.iff(f, Formula.lit(permToVar(pi, i, 0))));
            }
            // encode eq 3
            Formula f = Formula.fromClause(new Clause(lit3));

            for(String p : Permissions.PERMISSIONS)
                formulas.add(Formula.iff(f, Formula.lit(permToVar(p, i, 0))));
        }

        // encode eq 1
        formulas.add(Formula.fromClause(claij));

        Formula form = formulas.get(0);

        for(int i = 1; i < formulas.size(); i++)
            form = Formula.and(form, formulas.get(i));

        int[][] dimacs = form.toDIMACS(M);

        IVec<IVecInt> ret = new Vec<IVecInt>();

        for (int i = 0; i < dimacs.length; i++) {
            ret.push(new VecInt(dimacs[i]));
        }

        return ret;
    }

    private IVec<IVecInt> encodePolicies(ArrayList<Stack> conf, int s) {

        Vector<Formula> formulas = new Vector<Formula>();
        for (int i = 0; i < conf.size(); i++) {
            Stack Si = conf.get(i);
            formulas.addAll(Si.getGlobal());
            if(i == s) {
                for(int j = 0; j < Si.size(); j++) {
                    Vector<Formula> Lij = Si.get(j).getScopePolicies(Scope.LOCAL);
                    Vector<Formula> Dij = Si.get(j).getScopePolicies(Scope.DIRECT);
                    for(Formula dij : Dij) {
                        formulas.add(dij.rename("_" + (i+1) + "_" + (j+1)));
                    }
                    for(Formula lij : Lij) {
                        formulas.add(lij.rename("_" + (i+1)));
                    }
                }
            }
        }

        if(formulas.isEmpty())
            return null;

        Formula g = formulas.get(0);
        for (int i = 1; i < formulas.size(); i++) {
            g = Formula.and(g, formulas.get(i));
        }

        int[][] dimacs = g.toDIMACS(M);

        IVec<IVecInt> ret = new Vec<IVecInt>();
        for (int i = 0; i < dimacs.length; i++) {
            ret.push(new VecInt(dimacs[i]));
        }

        return ret;
    }

    public IVec<IVecInt> encodePop(String component) {

        M = new TreeMap<String, Integer>();

        ArrayList<Stack> target = new ArrayList<Stack>();

        int index = -1;

        for(int i = 0; i < this.size(); i++) {
            Stack S = this.get(i);
            if(S.peek().component.compareTo(component) == 0) {
                index = i;
                Stack Sp = new Stack();
                Frame top = S.pop();
                Sp.addAll(S);
                S.push(top);
                target.add(Sp);
            }
            else
                target.add(S);
        }

        if(index < 0)
            return null;

        IVec<IVecInt> spec = encodePermissions(target);
        IVec<IVecInt> pol = encodePolicies(target, index);

        for (int i = 0; i < pol.size(); i++) {
            spec.push(pol.get(i));
        }

        return spec;
    }

    public IVec<IVecInt> encodePush(Frame f, String component, boolean alloc) {

        // TODO: alloc unused

        M = new TreeMap<String, Integer>();

        ArrayList<Stack> target = new ArrayList<Stack>();

        int index = -1;

        for(int i = 0; i < this.size(); i++) {
            Stack S = this.get(i);
            if(S.peek().component.compareTo(component) == 0) {
                index = i;
                Stack Sp = new Stack();
                Sp.addAll(S);
                S.push(f);
                target.add(Sp);
            }
            else
                target.add(S);
        }

        if(index < 0)
            return null;

        IVec<IVecInt> spec = encodePermissions(target);
        IVec<IVecInt> pol = encodePolicies(target, index);

        for (int i = 0; i < pol.size(); i++) {
            spec.push(pol.get(i));
        }

        return spec;
    }

    public void pop(String component) {
        for (Stack S : this) {
            if (S.peek().component.compareTo(component) == 0) {
                S.pop();
                if(S.isEmpty())
                    this.remove(S);
            }
        }
    }

    public void push(Frame frame, String component, boolean alloc) {
        Stack S = new Stack();
        for (Stack T : this) {
            if (T.peek().component.compareTo(component) == 0) {
                S = T;
                break;
            }
        }

        Vector<Policy> PSticky = S.getSticky();

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
        String var;

        if(s == 0)
            var = enc;
        else if(f == 0)
            var = enc + "_" + s;
        else {
            var = enc + "_" + s + "_" + f;
        }

        if(!M.containsKey(var))
            M.put(var, M.size()+1);

        return var;
    }

    private String varToPerm(String var) {
        if(var.contains("_")) {
            return var.substring(0, var.indexOf("_"));
        }
        else
            return var;
    }

}
