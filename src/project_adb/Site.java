package project_adb;

import java.util.HashMap;


public class Site {
	private int siteIndex;
	private boolean isUp; // All sites are up in initial state
	private HashMap<Variable, Boolean> variableList; // Boolean ====>> exist
	private HashMap<Variable, HashMap<Transaction, String>> lockTable; // variable(transaction, lockType)
	
	protected Site(int index) {
		variableList = new HashMap<>();
		isUp = true;
		this.siteIndex = index;
		lockTable = new HashMap<>();
		
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
			//locktable.put(var, new HashMap<Transaction, Lock>());
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
					// add lock
				} 
			}
		}
	}
	
	protected Variable getVariable(String varID) {
		for (Variable v : variableList.keySet()) {
			if (varID.equals(v.getVariableID()))
				return v;
		}
		return null;
	}
	
	protected boolean isVariableExists(String var) {
		for (Variable v : variableList.keySet()) {
			if (var.equals(v.getVariableID())) {
				boolean b = variableList.get(v);
				return b;
			}
		}
		return false;
	}
	
	protected boolean isUp(){
		return isUp;
	}
	
	protected void failSite() {
		isUp = false;
	}
}