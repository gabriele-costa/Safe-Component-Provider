package com.uni.ailab.scp.runtime;

import com.uni.ailab.scp.cnf.Clause;
import com.uni.ailab.scp.cnf.Literal;
import com.uni.ailab.scp.log.Logger;
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
    	
    	long cTime = System.currentTimeMillis();
    	Logger.log("ENTER encodePermissions()");

        String[][][] PC = new String[conf.size()][][];
        for (int i = 0; i < conf.size(); i++) {
            PC[i] = conf.get(i).getPermissions();
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
        
        Logger.log(dimacs, M.size());

        IVec<IVecInt> ret = new Vec<IVecInt>();

        for (int i = 0; i < dimacs.length; i++) {
            ret.push(new VecInt(dimacs[i]));
        }
        
        Logger.log("EXIT encodePermissions() in " + (System.currentTimeMillis() - cTime));

        return ret;
    }

    private IVec<IVecInt> encodePolicies(ArrayList<Stack> conf, int s) {

    	long cTime = System.currentTimeMillis();
    	Logger.log("ENTER encodePolicies()");
    	
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
        	return new Vec<IVecInt>();

        Formula g = formulas.get(0);
        for (int i = 1; i < formulas.size(); i++) {
            g = Formula.and(g, formulas.get(i));
        }

        int[][] dimacs = g.toDIMACS(M);
        
        Logger.log(dimacs, M.size());

        IVec<IVecInt> ret = new Vec<IVecInt>();
        for (int i = 0; i < dimacs.length; i++) {
            ret.push(new VecInt(dimacs[i]));
        }
        
        Logger.log("EXIT encodePolicies() in " + (System.currentTimeMillis() - cTime));

        return ret;
    }

    public IVec<IVecInt> encodePop(String component) {
    	
    	long cTime = System.currentTimeMillis();
    	Logger.log("ENTER encodePop(" +component+ ")");

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
        	return new Vec<IVecInt>();

        IVec<IVecInt> spec = encodePermissions(target);
        IVec<IVecInt> pol = encodePolicies(target, index);

        for (int i = 0; i < pol.size(); i++) {
            spec.push(pol.get(i));
        }
        
        Logger.log("EXIT encodePop(" +component+ ") in " + (System.currentTimeMillis() - cTime));

        return spec;
    }

    public IVec<IVecInt> encodePush(Frame f, String component, boolean alloc) {

        // TODO: alloc unused
    	long cTime = System.currentTimeMillis();
    	Logger.log("ENTER encodePush(" + f.toString() + ", " +component+ ", " + alloc + ")");

        M = new TreeMap<String, Integer>();

        ArrayList<Stack> target = new ArrayList<Stack>();
        
        int index = -1;
        
        Stack Sn = new Stack();
        
        if(alloc) {
        	for(int i = 0; i < this.size(); i++) {
	            Stack S = this.get(i);
	            Stack Sp = new Stack();
	            Sp.addAll(S);
	            target.add(Sp);
	            
	            if(S.peek().component.compareTo(component) == 0) {
	            	Sn.addAll(S);
	            }
	        }
            index = target.size();
            Sn.push(f);
            target.add(Sn);    	
        }
        else {
	
	        for(int i = 0; i < this.size(); i++) {
	            Stack S = this.get(i);
	            if(S.peek().component.compareTo(component) == 0) {
	                index = i;
	                Stack Sp = new Stack();
	                Sp.addAll(S);
	                Sp.push(f);
	                target.add(Sp);
	            }
	            else
	            	target.add(S);
	            	
	        }
        }
        
        if(index < 0)
            return new Vec<IVecInt>();

        IVec<IVecInt> spec = encodePermissions(target);
        IVec<IVecInt> pol = encodePolicies(target, index);

        for (int i = 0; i < pol.size(); i++) {
            spec.push(pol.get(i));
        }
        
        Logger.log("EXIT encodePush(" + f.toString() + ", " +component+ ", " + alloc + ") in " + (System.currentTimeMillis() - cTime));

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
            V.addAll(S);
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
    
    public String[] getStackRoots() {
    	String[] res = new String[this.size()];
    	for(int i = 0; i < this.size(); i++) {
			res[i] = this.get(i).get(0).component;
		}
    	return res;
    }
    
    public String[] getStackElements(String root) {
    	for(int i = 0; i < this.size(); i++) {
    		if(this.get(i).get(0).component.compareTo(root) == 0) {
    			String[] res = new String[this.get(i).size()];
    			for (int j = 0; j < this.get(i).size(); j++) {
					res[j] = this.get(i).get(j).component;
				}
    			return res;
    		}
		}
    	return null;
    }
    
    public Frame getFrame(String root, String comp) {
    	for(int i = 0; i < this.size(); i++) {
    		if(this.get(i).get(0).component.compareTo(root) == 0) {
    			for (int j = 0; j < this.get(i).size(); j++) {
    				if(this.get(i).get(j).component.compareTo(comp) == 0) {
    					return this.get(i).get(j);
    				}
				}
    		}
		}
    	return null;
    }
    
    public boolean isTopFrame(String component) {
    	for(int i = 0; i < this.size(); i++) {
            Stack S = this.get(i);
            if(S.peek().component.compareTo(component) == 0) {
                return true;
            }
        }
    	return false;
    }

}
