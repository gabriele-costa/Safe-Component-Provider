package com.uni.ailab.scp.runtime;

/**
 * Created by gabriele on 27/03/15.
 */

import java.util.Arrays;

import android.util.Log;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import com.uni.ailab.scp.log.Logger;

public class ScpRuntime {

    private static Configuration configuration = new Configuration();
    
    public static boolean canPop(String component) {

        ISolver solver = SolverFactory.newLight();
        
        if(!configuration.isTopFrame(component))
        	return false;
        
        IVec<IVecInt> clauses = configuration.encodePop(component);
        
        long cTime = System.currentTimeMillis();
        int idx = -1;
        
        try {
        	for(idx = 0; idx < clauses.size(); idx++) {
                solver.addClause(clauses.get(idx));
            }
        	idx = -1;
            IProblem problem = solver;
            
            Logger.log("CHECKING SAT pop");
            
            if (problem.isSatisfiable()) {
            	Logger.log("SAT in " + (System.currentTimeMillis() - cTime));
                return true;
            }
            else {
            	Logger.log("UNSAT in " + (System.currentTimeMillis() - cTime));
            	return false;
            }
        } catch (ContradictionException e) {
        	Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        } catch (TimeoutException e) {
        	Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        }
        finally {
        	for(int i = 0; i < clauses.size(); i++) {
                Logger.log(clauses.get(i).toString());
                if(i == idx)
        			Logger.log("^^^");
            }
        }
        return false;
    }

    public static boolean canAlloc(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        
        if(component != null && "".compareTo(component) != 0)
        	if(!configuration.isTopFrame(component))
        		return false;
        
        IVec<IVecInt> clauses = configuration.encodePush(f, component, true);

        long cTime = System.currentTimeMillis();
        int idx = -1;
        
        try {
        	for(idx = 0; idx < clauses.size(); idx++) {
                solver.addClause(clauses.get(idx));
            }
        	idx = -1;
            IProblem problem = solver;
            
            Logger.log("CHECKING SAT alloc");
            cTime = System.currentTimeMillis();
            
            if (problem.isSatisfiable()) {
            	Logger.log("SAT in " + (System.currentTimeMillis() - cTime));
                return true;
            }
            else {
            	Logger.log("UNSAT in " + (System.currentTimeMillis() - cTime));
            	return false;
            }
        } catch (ContradictionException e) {
        	Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        } catch (TimeoutException e) {
        	Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        }
        finally {
        	for(int i = 0; i < clauses.size(); i++) {
                Logger.log(clauses.get(i).toString());
                if(i == idx)
        			Logger.log("^^^");
            }
        }
        return false;
    }

    public static boolean canPush(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        
        if(!configuration.isTopFrame(component))
        	return false;
        
        IVec<IVecInt> clauses = configuration.encodePush(f, component, false);

        long cTime = System.currentTimeMillis();
        
        int idx = -1;
        
        try {
        	for(idx = 0; idx < clauses.size(); idx++) {
                solver.addClause(clauses.get(idx));
            }
        	idx = -1;
            IProblem problem = solver;
            
            Logger.log("CHECKING SAT push");
            cTime = System.currentTimeMillis();
            
            if (problem.isSatisfiable()) {
            	Logger.log("SAT in " + (System.currentTimeMillis() - cTime));
                return true;
            }
            else {
            	Logger.log("UNSAT in " + (System.currentTimeMillis() - cTime));
            	return false;
            }
        } catch (ContradictionException e) {
            Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        } catch (TimeoutException e) {
        	Logger.log("UNSAT with in "+ (System.currentTimeMillis() - cTime) +" with " + e);
        }
        finally {
        	for(int i = 0; i < clauses.size(); i++) {
                Logger.log(clauses.get(i).toString());
                if(i == idx)
        			Logger.log("^^^");
            }
        }
        return false;
    }

    public static void pop(String component) {

        configuration.pop(component);

    }

    public static void alloc(Frame f, String component) {
        
        configuration.push(f, component, true);
        
    }

    public static void push(Frame f, String component) {

        configuration.push(f, component, false);
        
    }
    
    public static void allocService(Frame f, String component) {
    	configuration.push(f, component, true);
    }
    
    public static String[] getStackRoots() {
    	String[] s = configuration.getStackRoots();
    	Logger.log("Current Roots: " + Arrays.toString(s));
    	return s;
    	
    }
    
    public static String[] getStackElements(String root) {
    	return configuration.getStackElements(root);
    }
    
    public static Frame getFrame(String root, String comp) {
    	return configuration.getFrame(root, comp);
    }

}
