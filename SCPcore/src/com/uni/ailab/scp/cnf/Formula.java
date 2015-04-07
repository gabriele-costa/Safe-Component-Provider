package com.uni.ailab.scp.cnf;

import java.util.Map;
import java.util.Vector;

/**
 * Created by gabriele on 31/03/15.
 */
public class Formula {

    Vector<Clause> clauses;

    private Formula(String x) {
        Literal l = new Literal(x, false);
        clauses = new Vector<Clause>();
        clauses.add(new Clause(l));
    }

    private Formula(Vector<Clause> c) {
        clauses = c;
    }

    private Formula(Clause c) {
        clauses = new Vector<Clause>();
        clauses.add(c);
    }

    private Formula(Connective op, Formula f, Formula g) {
        clauses = new Vector<Clause>();
        switch (op) {
            case NOT:
                Vector<Clause> c0 = new Vector<Clause>();
                for(Literal l : f.clauses.firstElement().literals)
                    c0.add(new Clause(new Literal(l.name, !l.neg)));

                Formula f0 = new Formula(c0);

                for(int i = 1; i < clauses.size(); i++) {
                    c0 = new Vector<Clause>();
                    Clause ci = clauses.get(i);
                    for(Literal l : ci.literals)
                        c0.add(new Clause(new Literal(l.name, !l.neg)));

                    f0 = new Formula(Connective.OR, f0, new Formula(c0));
                }
                clauses.addAll(f0.clauses);
                break;
            case AND: {
                clauses.addAll(f.clauses);
                clauses.addAll(g.clauses);
                break;
            }
            case OR: {
                for(Clause p : f.clauses)
                    for(Clause q : g.clauses)
                        for(Literal l : p.literals)
                            for(Literal k : q.literals)
                                clauses.add(new Clause(new Literal[] {l, k}));
                break;
            }
            case ARROW: {
                Formula self = new Formula(Connective.OR, new Formula(Connective.NOT, f, null), g);
                clauses.addAll(self.clauses);
                break;
            }
        }
    }

    public static Formula lit(String x) {
        return new Formula(x);
    }

    public static Formula fromClause(Clause c) {
        return new Formula(c);
    }

    public static Formula fromClause(Vector<Clause> c) {
        return new Formula(c);
    }

    public static Formula not(Formula f) {
        return new Formula(Connective.NOT, f, null);
    }

    public static Formula and(Formula f, Formula g) {
        return new Formula(Connective.AND, f, g);
    }

    public static Formula or(Formula f, Formula g) {
        return new Formula(Connective.OR, f, g);
    }

    public static Formula imply(Formula f, Formula g) {
        return new Formula(Connective.ARROW, f, g);
    }

    public static Formula iff(Formula f, Formula g) {
        return new Formula(Connective.AND, new Formula(Connective.ARROW, f, g), new Formula(Connective.ARROW, g, f));
    }

    public int[][] toDIMACS(Map<String, Integer> M) {

        int[][] result = new int[clauses.size()][];
        for (int i = 0; i < clauses.size(); i++) {
            result[i] = clauses.get(i).toDIMACS(M);
        }
        return result;
    }

    public Formula rename(String suffix) {
        Vector<Clause> vc = new Vector<Clause>();
        for(Clause c : clauses) {
            vc.add(c.rename(suffix));
        }
        return new Formula(vc);
    }
    
    public static Formula parse(String s) {
    	s = s.substring(s.indexOf("{") + 1, s.lastIndexOf("}"));
    	
    	String[] scla = s.split(";");
    	Vector<Clause> vec = new Vector<Clause>();
    	for(int i = 0; i < scla.length; i++) {
    		vec.add(Clause.parse(scla[i]));
    	}
    	
    	return new Formula(vec);
    	
    }
    
    @Override
    public String toString() {
    	String s = "{";
    	
    	for(Clause c : clauses) {
    		s += c.toString() + ";";
    	}
    	
    	return s.substring(0, s.lastIndexOf(";")) + "}";
    }
}
