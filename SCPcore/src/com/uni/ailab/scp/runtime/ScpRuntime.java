package com.uni.ailab.scp.runtime;

/**
 * Created by gabriele on 27/03/15.
 */

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
        IVec<IVecInt> clauses = configuration.encodePop(component);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            
            Logger.log("CHECKING SAT at " + System.currentTimeMillis());
            
            if (problem.isSatisfiable()) {
            	Logger.log("SAT at " + System.currentTimeMillis());
                return true;
            }
            else {
            	Logger.log("UNSAT at " + System.currentTimeMillis());
            	return false;
            }
        } catch (ContradictionException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        }
    }

    public static boolean canAlloc(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        IVec<IVecInt> clauses = configuration.encodePush(f, component, true);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                return true;
            }
            else {
            	return false;
            }
        } catch (ContradictionException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        }
    }

    public static boolean canPush(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        IVec<IVecInt> clauses = configuration.encodePush(f, component, false);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                return true;
            }
            else {
            	return false;
            }
        } catch (ContradictionException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(ScpRuntime.class.toString(), e.toString());
            return false;
        }
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

}
