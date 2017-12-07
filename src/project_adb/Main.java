/**
 * Advanced Database System
 * Replicated Concurrency Control and Recovery
 * 
 * @Author: Xintong Wang(N18322289)
 * @Author: Dailing Zhu(N11754882)
 * 
 * @Date: 07/12/2017
 * 
 * @Class_Description:
 * This is the main class of the project, 
 * reading the input file and decide which action 
 * the program should take.
**/


package project_adb;

import java.io.*;
import java.util.Scanner;

public class Main {
	public static void readfile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("testcase/partialTest.txt"));
		String line = null;
		TM tm = new TM();
		DM dm = new DM();
		
		
		while((line = br.readLine()) != null) {
			String[] lineSplit = line.split("\\(");
			String action = lineSplit[0];
			if(action.equals("begin")) {
				String transactionID = lineSplit[1].split("\\)")[0].trim();
				tm.begin(transactionID, "RW");
			}
			else if (action.equals("beginRO")) {
				String transactionID = lineSplit[1].split("\\)")[0].trim();
				tm.begin(transactionID, "RO");
			}
			else if(action.equals("W")) {
				String[] actionInfo = lineSplit[1].split(",");
				String transactionID = actionInfo[0].trim();
				String onChangeVariableID = actionInfo[1].trim();
				int onChangeValue = Integer.parseInt(actionInfo[2].split("[\\)]")[0].trim());
				
				tm.write(transactionID, onChangeVariableID, onChangeValue);
			}
			else if(action.equals("R")) {
				String[] actionInfo = lineSplit[1].split(",");
				String transactionID = actionInfo[0].trim();
				String onReadVariableID = actionInfo[1].split("\\)")[0].trim();
				tm.read(transactionID, onReadVariableID);
			}
			else if(action.equals("dump")) {
				String content;
				if(lineSplit[1].equals(")")) {
					tm.dump();
				} else if((content = lineSplit[1].split("\\)")[0]).trim().matches("[0-9]+")) {
					tm.dump(Integer.parseInt(content));
				} else {
					tm.dump(lineSplit[1].split("\\)")[0].trim());
				}
			}
			else if(action.equals("fail")) {
				String failSiteID = lineSplit[1].split("\\)")[0].trim();
				tm.fail(failSiteID);
			}
			else if(action.equals("recover")) {
				String recoverSiteID = lineSplit[1].split("\\)")[0].trim();
				tm.recover(recoverSiteID);
			}
			else if(action.equals("end")) {
				// only related to write action
				String transactionID = lineSplit[1].split("\\)")[0].trim();
				tm.end(transactionID);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		readfile();
	}
}