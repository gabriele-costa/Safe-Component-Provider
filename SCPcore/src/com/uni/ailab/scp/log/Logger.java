package com.uni.ailab.scp.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;

public class Logger {
	
	public static final String LOG_FILE = "/log.txt";
	private static boolean open = false;
	
	private static PrintWriter writer;
	
	private static void open() throws IOException {
		String s = Environment.getExternalStorageDirectory().getCanonicalPath();
		File log = new File(s+LOG_FILE);
		if(log.exists())
			log.delete();
        writer = new PrintWriter(new FileWriter(log, true));
        open = true;
	}
	
	public static void log(String s) {
		if(!s.endsWith("\n"))
			s += "\n";
		try {
			if(!open) open();
			
			writer.write(s);
			writer.flush();
		}
		catch(IOException e) {
			e.printStackTrace();
		}			
	}
	
	private static void close() {
	    writer.flush();
	    writer.close();
	    open = false;
	}

	public static void log(int[][] dimacs, int nbvar) {
		String cnf = "p cnf " + nbvar + " " + dimacs.length + "\n";
		for(int i = 0; i < dimacs.length; i++) {
			for(int j = 0; j < dimacs[i].length; j++) {
				cnf += dimacs[i][j] + " ";
			}
			cnf += "0\n";
		}
		log(cnf);
	}

}
