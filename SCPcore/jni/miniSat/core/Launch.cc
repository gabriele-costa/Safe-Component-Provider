/*****************************************************************************************[Main.cc]
 Copyright (c) 2003-2006, Niklas Een, Niklas Sorensson
 Copyright (c) 2007-2010, Niklas Sorensson

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **************************************************************************************************/

/*****************************************************************************************[Launch.cc]
 @author rohm1
 @create 10.2012

 Modification of the original Main.cc file for the AndroSAT project.
 I do not owe this, please refer to the above mentionned copyright owners.
 **************************************************************************************************/

#include <errno.h>
#include <signal.h>
#include <android/log.h>
#include "miniSat/utils/System.h"
#include "miniSat/core/Solver.h"

using namespace Minisat;

//=================================================================================================

static Solver* solver;
// Terminate by notifying the solver and back out gracefully. This is mainly to have a test-case
// for this feature of the Solver as it may take longer than an immediate call to '_exit()'.
static void SIGINT_interrupt(int signum)
{
	solver->interrupt();
}

// Note that '_exit()' rather than 'exit()' has to be used. The reason is that 'exit()' calls
// destructors and may cause deadlocks if a malloc/free function happens to be running (these
// functions are guarded by locks for multithreaded use).
static void SIGINT_exit(int signum)
{
	//~ printf("\n"); printf("*** INTERRUPTED ***\n");
	_exit(1);
}

static void mySkipWhitespace(const char* in, int* i, int l)
{
	while (*i < l && ((in[*i] >= 9 && in[*i] <= 13) || in[*i] == 32))
		++*i;
}

static int myParseInt(const char* in, int* i, int l)
{
	int val = 0;
	bool neg = false;
	mySkipWhitespace(in, i, l);
	if (in[*i] == '-')
		neg = true, ++*i;
	else if (in[*i] == '+')
		++*i;
	if (in[*i] < '0' || in[*i] > '9')
	{
		__android_log_print(ANDROID_LOG_INFO, "MINISAT",
				"PARSE ERROR! Unexpected char: %c\n", in[*i]);
		//fprintf(stderr, "PARSE ERROR! Unexpected char: %c\n", in[*i]);
	}
	while (*i < l && in[*i] >= '0' && in[*i] <= '9')
		val = val * 10 + (in[*i] - '0'), ++*i;
	mySkipWhitespace(in, i, l);
	return neg ? -val : val;
}

//=================================================================================================
// Main:

char* minisat(const char* in, bool debug)
{
	char* res = new char[2048];
	res[0] = '\0';

	try
	{
		// Extra options:
		//
		IntOption verb("MAIN", "verb",
				"Verbosity level (0=silent, 1=some, 2=more).", 0,
				IntRange(0, 2));
		IntOption cpu_lim("MAIN", "cpu-lim",
				"Limit on CPU time allowed in seconds.\n", INT32_MAX,
				IntRange(0, INT32_MAX));
		IntOption mem_lim("MAIN", "mem-lim",
				"Limit on memory usage in megabytes.\n", INT32_MAX,
				IntRange(0, INT32_MAX));

		Solver S;
		double initial_time = cpuTime();
		S.verbosity = verb;
		solver = &S;
		// Use signal handlers that forcibly quit until the solver will be able to respond to
		// interrupts:
		signal(SIGINT, SIGINT_exit);
		signal(SIGXCPU, SIGINT_exit);

		// Set limit on CPU-time:
		if (cpu_lim != INT32_MAX)
		{
			rlimit rl;
			getrlimit(RLIMIT_CPU, &rl);
			if (rl.rlim_max == RLIM_INFINITY || (rlim_t) cpu_lim < rl.rlim_max)
			{
				rl.rlim_cur = cpu_lim;
				if (setrlimit(RLIMIT_CPU, &rl) == -1)
					strcat(res, "WARNING NO_SET_RESSOURCE:cputime\n");
			}
		}

		// Set limit on virtual memory:
		if (mem_lim != INT32_MAX)
		{
			rlim_t new_mem_lim = (rlim_t) mem_lim * 1024 * 1024;
			rlimit rl;
			getrlimit(RLIMIT_AS, &rl);
			if (rl.rlim_max == RLIM_INFINITY || new_mem_lim < rl.rlim_max)
			{
				rl.rlim_cur = new_mem_lim;
				if (setrlimit(RLIMIT_AS, &rl) == -1)
					strcat(res, "WARNING NO_SET_RESSOURCE:memory\n");
			}
		}

		int i = 0, l = 0, clause = -1, clauses, vars, parsed_lit, var;
		while (in[l] != '\0')
			l++;

		vec<Lit> lits;

		while (i < l)
		{
			if (clause == -1)
			{
				vars = myParseInt(in, &i, l);
				clauses = myParseInt(in, &i, l);
				if (debug)
					sprintf(res, "%sVARS %d\nCLAUSES %d\n", res, vars, clauses);
				if (in[i] == '0')
					clause++, i++;
			}
			else
			{
				if (debug)
					sprintf(res, "%sCLAUSE ", res);
				while (i < l && in[i] != '0')
				{
					parsed_lit = myParseInt(in, &i, l);
					var = abs(parsed_lit) - 1;
					while (var >= S.nVars())
						S.newVar();
					if (debug)
						sprintf(res, "%s%d ", res, parsed_lit);
					lits.push((parsed_lit > 0) ? mkLit(var) : ~mkLit(var));
				}
				if (debug)
					sprintf(res, "%s\n", res);
				i++;
				S.addClause_(lits);
				lits.clear();
				clause++;
				mySkipWhitespace(in, &i, l);
			}
		}
		if (debug && vars != S.nVars())
			sprintf(res, "%sWARNING DIMACS:variables:%d\n", res, S.nVars());
		if (debug && clause != clauses)
			sprintf(res, "%sWARNING DIMACS:clauses:%d\n", res, clause);

		double parsed_time = cpuTime();

		// Change to signal-handlers that will only notify the solver and allow it to terminate
		// voluntarily:
		signal(SIGINT, SIGINT_interrupt);
		signal(SIGXCPU, SIGINT_interrupt);

		if (!S.simplify())
		{
			strcat(res, "UNSAT");
		}
		else
		{
			vec<Lit> dummy;
			lbool ret = S.solveLimited(dummy);
			if (ret == l_True)
			{
				strcat(res, "");
				for (i = 0; i < S.nVars(); i++)
					if (S.model[i] != l_Undef)
						sprintf(res, "%s%s%s%d", res, (i == 0) ? "" : " ",
								(S.model[i] == l_True) ? "" : "-", i + 1);
				strcat(res, " 0");
			}
			else if (ret == l_False)
				strcat(res, "UNSAT");
			else
				strcat(res, "INDETERMINATE");
		}

	}
	catch (OutOfMemoryException&)
	{
		strcat(res, "INDETERMINATE\n");
	}

//	__android_log_print(ANDROID_LOG_INFO, "TEST", "res: '%s'", res);

	return res;
}
