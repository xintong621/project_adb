package project_adb;

import java.util.HashMap;
import java.util.Iterator;

public class Site {
	private int siteIndex;
	private boolean isUp; // All sites are up in initial state
	private HashMap<Variable, Boolean> variableList; // Boolean ====>> exist
	private HashMap<Variable, HashMap<Transaction, String>> lockTable; // variable(transaction, lockType)
	//lockTable could divided into readLtable and writeLtable
	protected Site(int index) {
		variableList = new HashMap<>();
		isUp = true;
		this.siteIndex = index;
		lockTable = new HashMap<Variable, HashMap<Transaction, String>>();
		
		// put all variables to this site along with exist
		for (int i = 1; i <= 20; i++) {
			Variable var = new Variable(i);
			if (i % 2 == 0) {
				variableList.put(var, true);
				//isReady.put(var.getVariableID(), true);
			} else if ((i + 1) % 10 == siteIndex) {
				variableList.put(var, true);
				//isReady.put(var.getVariableID(), true);
			} else {
				variableList.put(var, false);
				//isReady.put(var.getVariableID(), false);
			}
			lockTable.put(var, new HashMap<Transaction, String>());
		}
	}
	
	public int getSiteIndex() {
		return siteIndex;
	}
	
	public void lockVariable(String variableID, Transaction transaction, String lockType) {
		if(transaction.getType().equals("RO")) {
			
		} else {
			for (Variable v : lockTable.keySet()) {
				if(v.getVariableID().equals(variableID)) {
					HashMap<Transaction, String> value = new HashMap<Transaction, String>();
					value.put(transaction, lockType);
					lockTable.get(v).putAll(value);
				} 
			}
		}
	}
	
	protected Variable getVariable(String variableID) {
		for (Variable v : variableList.keySet()) {
			if (variableID.equals(v.getVariableID()))
				return v;
		}
		return null;
	}
	
	protected boolean isVariableExists(String variableID) {
		for (Variable v : variableList.keySet()) {
			if (variableID.equals(v.getVariableID())) {
				boolean b = variableList.get(v);
				return b;
			}
		}
		return false;
	}
	
	protected boolean checkLockState(String variableID) {
		//return true if readlocked || writelocked
		boolean result = false;
		
		boolean isLocked = false;
		
		for (Variable v : lockTable.keySet()) {
			if (variableID.equals(v.getVariableID())){
				if (lockTable.get(v) != null){
					isLocked = true;
				}
			}
		}
		
		String lockType = "";
		
		for (Variable v : lockTable.keySet()) {
			if (variableID.equals(v.getVariableID()) && variableList.get(v)) {
				if (!lockTable.get(v).isEmpty()) {
					
					Iterator<String> locks = lockTable.get(v).values().iterator();
					
					while (locks.hasNext()) {
						String tempType = locks.next();
						if (tempType.equals("RL") || tempType.equals("WL")){
							lockType= tempType;
						}
					}
				}
			}
		}
		
	
		if (isLocked && (lockType.equals("RL") || lockType.equals("WL"))) {
			result = true;
		}
		
		
		return result;
		
	}
	
	protected boolean isUp(){
		return isUp;
	}
	
	protected void failSite() {
		isUp = false;
	}
}