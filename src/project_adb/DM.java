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
	
	protected static void setWriteLock(Transaction transaction, String variableID, String lockType) {
		// filling locktalbe
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				s.lockVariable(variableID, transaction, lockType); // add lock to locktable
			}
		}
	}
	
	protected static void setReadLock(String variable) {
		
	}
	
	protected static boolean checkWriteLock(String variableID) {
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				boolean state = s.checkLockState(variableID);
			}
		}
		return false;
	}
	
	protected static boolean checkReadLock(String variableID) {
		return false;
	}
	
	protected static Variable readVariable(int SiteIndex, String var) {	
		Site s = database.get(SiteIndex - 1);
		return s.getVariable(var);
	}
	
	protected static void dump() {
		//print all information in all site
		for (Site s : database) {
			
			if (s.isUp()) {
				System.out.println("Site " + s.getSiteIndex());
				
				for (int i = 1; i <= 20; i++) {
					String var = "x" + Integer.toString(i);
					
					if (s.isVariableExists(var)) {
						Variable variable = readVariable(s.getSiteIndex(), var);
						System.out.print(variable.getVariableID() + ": "
									   + variable.getValue() + "   ");
					}
				}
			}
			
			System.out.println();
		}
	}
	
	protected static void dump(int siteIndex) {
		//print all information in this site
		for (Site s : database) {
			if (s.getSiteIndex() == siteIndex) {
				
				if (!s.isUp())
					System.err.println("The site " + siteIndex + " is down.");
				else {
					System.out.println("Site " + s.getSiteIndex());
					
					for (int i = 1; i <= 20; i++) {
						String var = "x" + Integer.toString(i);
						
						if (s.isVariableExists(var)) {
							Variable variable = readVariable(s.getSiteIndex(), var);
							System.out.print(variable.getVariableID() + ": "
										   + variable.getValue() + "   ");
						}
					}
				}
			}
		}
	}
	
	protected static void dump(String variable) {
		//print all information with this variable
	}

}