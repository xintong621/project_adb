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
	
	protected static boolean checkWriteState(String variableID) {
		// true condition:
		// 1. at least 1 site which contains this variable is up
		for(Site s : database) {
			if((s.isUp() == true) && (s.isVariableExists(variableID) == true)) return true;
		}
		return false;
	}
	
	protected static boolean checkReadState(String variableID) {
		for(Site s : database) {
			if((s.isUp() == true) && (s.isVariableExists(variableID) == true)) return true;
		}
		return false;
	}
	
	protected static void copyCurrentDB(Transaction transaction) {
		for(int i = 1 ; i < 21; i++) {
			transaction.tempTable.put("x" + i, -1);
		}
		for(String vID : transaction.tempTable.keySet()) {
			for(Site s : database) {
				if(s.isUp() && s.isVariableExists(vID) && (transaction.tempTable.get(vID) == -1)) {
					int value = s.getVariable(vID).getValue();
					transaction.tempTable.replace(vID, value);
				}
			}
		}
	}
	
	protected static void setLock(Transaction transaction, String variableID, String lockType) {
		// filling locktalbe
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				s.lockVariable(variableID, transaction, lockType); // add lock to locktable
			}
		}
	}
	
	
	protected static void setReadLock(String variable) {
		
	}
// below two function could merge to one
	protected static boolean checkWriteLock(String variableID) {
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				boolean lockState = s.checkLockState(variableID, "WL");
				return lockState;
			}
		}
		return false;
	}
	
	protected static boolean checkReadLock(String variableID) {
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				boolean lockState = s.checkLockState(variableID, "RL");
				return lockState;
			}
		}
		return false;
	}
	
	protected static Variable readVariable(int SiteIndex, String var) {	
		Site s = database.get(SiteIndex - 1);
		return s.getVariable(var);
	}
	
	protected static void updateDatabase(String changedVariableID, int changedValue) {
		for(Site s : DM.database) {
			if(s.isUp()) {
				if(s.isVariableExists(changedVariableID)) {
					s.changeVariableValue(changedVariableID, changedValue);
				}
			}
		}
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