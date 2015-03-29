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

public class Runtime {

    private static Configuration configuration = new Configuration();

    public static boolean pop(String component) {

        ISolver solver = SolverFactory.newLight();
        IVec<IVecInt> clauses = configuration.encodePop(component);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                configuration.pop(component);
                return true;
            }
        } catch (ContradictionException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        }

        return false;
    }

    public static boolean alloc(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        IVec<IVecInt> clauses = configuration.encodePush(f, component, true);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                configuration.push(f, component, true);
                return true;
            }
        } catch (ContradictionException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        }

        return false;
    }

    public static boolean push(Frame f, String component) {

        ISolver solver = SolverFactory.newLight();
        IVec<IVecInt> clauses = configuration.encodePush(f, component, false);

        try {
            solver.addAllClauses(clauses);
            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                configuration.push(f, component, false);
                return true;
            }
        } catch (ContradictionException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        } catch (TimeoutException e) {
            Log.w(Runtime.class.toString(), e.toString());
            return false;
        }

        return false;
    }

}
