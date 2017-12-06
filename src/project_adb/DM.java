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
	
	protected static void fail(int siteNum) {
		for (Site s : database) {
			if (s.getSiteIndex() == siteNum) {
				if (!s.isUp())
					System.err.println("Site " + siteNum + " is already down");
				else {
					s.fail();
					s.clearlockTable();
					s.clearVariableList();
					System.out.println("Fail: Site " + siteNum + " is down");
				}
				break;
			}
		}
	}
	
	protected static void recover(int siteNum) {
		for (Site s : database) {
			if (s.getSiteIndex() == siteNum) {
				if (s.isUp())
					System.err.println("The site " + siteNum + " is already up.");
				else {
					s.recover();
					System.out.println("Recover: Site " + siteNum + " is up now.");
				}
				break;
			}
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
			String variableID = "x" + i;
			transaction.tempTable.put(variableID, -1);
		}
		for(String vID : transaction.tempTable.keySet()) {
			for(Site s : database) {
				if(s.isUp() && s.isVariableExists(vID) && (transaction.tempTable.get(vID) == -1)) {
					int value = s.getVariable(vID).getValue();
					transaction.tempTable.put(vID, value);
				}
			}
		}
	}
	
	protected static void setLock(Transaction transaction, String variableID, String lockType) {
		// filling locktable
		for(Site s : database) {
			if(s.isUp() && s.isVariableExists(variableID)) {
				s.lockVariable(variableID, transaction, lockType); // add lock to locktable
			}
		}
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
	
	protected static Variable readVariable(int SiteIndex, String variableID) {	
		Site s = database.get(SiteIndex - 1);
		return s.getVariable(variableID);
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
	
	protected static void unLock(Transaction transaction) {
		for(Site s : database) {
			s.unLock(transaction);
		}
		
	}
	
	protected static void dump() {
		System.out.println("========================================================");
		System.out.println("Dump:");
		//print all information in all site
		for (Site s : database) {
			
			if (s.isUp()) {
				System.out.println("Site Number: " + s.getSiteIndex());
				
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
		System.out.println("========================================================");
		System.out.println("Dump:");
		//print all information in this site
		for (Site s : database) {
			if (s.getSiteIndex() == siteIndex) {
				
				if (!s.isUp())
					System.err.println("The site " + siteIndex + " is down.");
				else {
					System.out.println("Site Number: " + s.getSiteIndex());
					
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
	
	protected static void dump(String variableID) {
		System.out.println("========================================================");
		System.out.println("Dump:");
		//print all information with this variable
		System.out.println("Variable ID: " + variableID);
		for (Site s : database) {
			if (s.isUp()) {
				String site = "Site " + s.getSiteIndex();

				if (s.isVariableExists(variableID)) {
					Variable variable = readVariable(s.getSiteIndex(), variableID);
					System.out.println(site
							+ ": " + variable.getValue());
				}

			}
		}
	}

}