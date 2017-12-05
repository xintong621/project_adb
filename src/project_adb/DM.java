package project_adb;

import java.util.ArrayList;
import java.util.List;



public class DM {
	
	protected static List<Site> database;
	
	protected DM() {
		database = new ArrayList<Site>();
		for (int i = 1; i <= 10; i++) {
			Site s = new Site(i);
			database.add(s);
		}
	}
	
	protected static void setWriteLock(Transaction transaction, String variable, String lockType) {
		// filling locktalbe
	}
	
	protected static void setReadLock(String variable) {
		
	}
	
	protected static boolean checkWriteLock(String variable) {
		return false;
	}
	
	protected static boolean checkReadLock(String variable) {
		return false;
	}
	
	protected static Variable readValue(int SiteNum, String var) {
		Variable v;
		Site s = database.get(SiteNum - 1);
		v = s.getVariable(var);
		return v;
	}
	
	protected static void dump() {
		//print all information in all site
		for (Site s : database) {
			if (s.isUp()) {
				System.out.println("Site number: " + s.getSiteIndex());
				for (int i = 1; i <= 20; i++) {
					String var = "x" + Integer.toString(i);
					if (s.isVariableExists(var)) {
						Variable variable = readValue(s.getSiteIndex(), var);
						System.out.println("Variable: "
								+ variable.getVariableID() + ", Data: "
								+ variable.getValue());
					}
				}
			}
		}
	}
	protected static void dump(int siteNum) {
		//print all information in this site
	}
	protected static void dump(String variable) {
		//print all information with this variable
	}

}