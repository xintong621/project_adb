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
 * This is the Site class of the project, 
 * it defines Site structure containing index, variable list and state etc.
**/

package project_adb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
			} else {
				if (i % 10 + 1== siteIndex) {
					variableList.put(var, true);
				} else {
					variableList.put(var, false);
				}
			}
			lockTable.put(var, new HashMap<Transaction, String>());
		}
	}
	
	public int getSiteIndex() {
		return siteIndex;
	}
	
	public boolean getSiteStatus() {
		return true;
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
	
	public Set<Transaction> getLockTransaction(String variableID) {
		for (Variable v : lockTable.keySet()) {
			if(v.getVariableID().equals(variableID)) {
				return lockTable.get(v).keySet();
			}
		}
		return null;
		
	}
	
	protected void unLock(Transaction transaction) {
		for (Variable v : lockTable.keySet()) {
			for(Transaction tr : lockTable.get(v).keySet()) {
				if(tr.equals(transaction)) {
					lockTable.get(v).remove(tr);
				}
			}
		}
	}
	
	protected Variable getVariable(String variableID) {
		for (Variable v : variableList.keySet()) {
			String vID = v.getVariableID();
			if((variableList.get(v) == true)) {
				if(variableID.equals(vID)){
					return v;
				}
			}
		}
		return null;
	}

	protected void clearlockTable() {
		lockTable.clear();
	}
	
	protected void clearVariableList() {
		for(Variable v : variableList.keySet()) {
			variableList.replace(v, false);
		}
	}
	
	protected boolean ifSiteContainsTransaction(Transaction transaction) {
		HashMap<Transaction, String> transactionInfoR = new HashMap<>();
		HashMap<Transaction, String> transactionInfoW = new HashMap<>();
		transactionInfoR.put(transaction, "RL");
		transactionInfoW.put(transaction, "WL");
		if(lockTable.containsValue(transactionInfoR) || lockTable.containsValue(transactionInfoW)) {
			return true;
		}
		return false;
	}
	
	protected boolean isVariableExists(String variableID) {
		for (Variable v : variableList.keySet()) {
			if (variableList.get(v)){
				if (variableID.equals(v.getVariableID())) {
					boolean b = variableList.get(v);
					return b;
				}
			}
		}
		return false;
	}
	
	protected boolean isLockedBySelf(Transaction transaction, String variableID) {
		for (Variable v : lockTable.keySet()) {
			if (variableID.equals(v.getVariableID())){
				if (lockTable.get(v) != null && lockTable.get(v).containsKey(transaction)){
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean checkLockState(String variableID, String typeLock) {
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
		
	
		if (isLocked && (lockType.equals(typeLock))) {
			result = true;
		}
		
		
		return result;
		
	}
	
	protected boolean isUp() {
		return isUp;
	}
	
	protected void changeVariableValue(String variableID, int value) {
		for(Variable v:variableList.keySet()){
			if(variableList.get(v)) {
				if(v.getVariableID().equals(variableID)) {
					v.setValue(value);
				}
			}
		}
	}
	
	protected void changeVariableExistence(String variableID, boolean value) {
		for(Variable v : variableList.keySet()) {
			if(v.getVariableID().equals(variableID)) {
				variableList.replace(v, value);
			}
		}
	}
	
	protected void fail() {
		isUp = false;
	}
	
	protected void recover() {
		isUp = true;
		
		for (int i = 1; i <= 20; i++) {
			if (i % 10 + 1== siteIndex) {
				changeVariableExistence("x" + i, true);
			}
			
			for(Variable v : variableList.keySet()) {
				lockTable.put(v, new HashMap<Transaction, String>());
			}
			
		}
	}
}