package project_adb;

import java.util.ArrayList;
import java.util.List;

public class DM {
	
	protected static List<Site> database;
	
	protected DM() {
		database = new ArrayList<Site>();
	}
	
	protected static void setWriteLock(String variable) {
		
	}
	
	protected static void setReadLock(String variable) {
		
	}
	
	protected static boolean checkWriteLock(String variable) {
		return false;
	}
	
	protected static boolean checkReadLock(String variable) {
		return false;
	}
	
	protected static void dump() {
		//print all information in all site
	}
	protected static void dump(int siteNum) {
		//print all information in this site
	}
	protected static void dump(String variable) {
		//print all information with this variable
	}

}