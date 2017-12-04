package project_adb;

import java.io.*;
import java.util.Scanner;

public class Main {
	public static void readfile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("testcase/test1.txt"));
		String line = null;
		while((line = br.readLine()) != null) {
			String[] lineSplit = line.split("\\(");
			String action = lineSplit[0];
			if(action.equals("begin")) {
				
			}
			else if (action.equals("beginRO")) {
				
			}
			else if(action.equals("W")) {
				
			}
			else if(action.equals("R")) {
				
			}
			else if(action.equals("dump")) {
				
			}
			else if(action.equals("fail")) {
				
			}
			else if(action.equals("recover")) {
				
			}
			else if(action.equals("end")) {
				
			}
			
			
		}
	}
	public static void main(String[] args) throws IOException {
		readfile();
	}
}