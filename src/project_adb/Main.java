package project_adb;

import java.io.*;
import java.util.Scanner;

public class Main {
	public static void readfile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("testcase/test1.txt"));
		String line = null;
		TM tm = new TM();
		DM dm = new DM();
		
		while((line = br.readLine()) != null) {
			String[] lineSplit = line.split("\\(");
			String action = lineSplit[0];
			if(action.equals("begin")) {
				String transactionID = lineSplit[1].split("\\)")[0];
				tm.begin(transactionID, "RW");
			}
			else if (action.equals("beginRO")) {
				String transactionID = lineSplit[1].split("\\)")[0];
				tm.begin(transactionID, "RO");
			}
			else if(action.equals("W")) {
				String[] actionInfo = lineSplit[1].split(",");
				String transactionID = actionInfo[0];
				String onChangeVariable = actionInfo[1];
				int onChangeValue = Integer.parseInt(actionInfo[2].split("\\)")[0]);
				
				tm.write(transactionID, onChangeVariable, onChangeValue);
			}
			else if(action.equals("R")) {

			}
			else if(action.equals("dump")) {
				String content;
				if(lineSplit[1].equals(")")) {
					tm.dump();
				} else if((content = lineSplit[1].split("\\)")[0]).matches("[0-9]+")) {
					tm.dump(Integer.parseInt(content));
				} else {
					tm.dump(lineSplit[1].split("\\)")[0]);
				}
			}
			else if(action.equals("fail")) {
				
			}
			else if(action.equals("recover")) {
				
			}
			else if(action.equals("end")) {
				String transactionID = lineSplit[1].split("\\)")[0];
				tm.end(transactionID);
			}
		}
	}
	public static void main(String[] args) throws IOException {
		readfile();
	}
}