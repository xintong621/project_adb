/*
 * transaction begin --> datamanager --> create transaction(type, transactionID, timestamp, etc)
 * transaction 
 * 
 * */

package project_adb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TM {
	private List<Transaction> runningTransaction;
	private LinkedHashMap<Transaction, ArrayList<String>> waitingAction;
	private Graph waitingGraph;
	
	protected TM() {
		runningTransaction = new ArrayList<Transaction>();
		waitingAction = new LinkedHashMap<Transaction, ArrayList<String>>();
	}
	
	public Transaction begin(String transactionID, String transactionType) {
		/*
		 * create new transaction
		 * add to running
		 * */
		Transaction transaction = new Transaction(transactionID, transactionType);
		if(transaction.getType().equals("RO")) {
			DM.copyCurrentDB(transaction);
		}
		System.out.println(transactionID + " of type " + transactionType + " successfully initialized");
		runningTransaction.add(transaction);

		return transaction;
	}
	
	public void read(String transactionID, String onReadVariableID) {
		/*
		 * execute read action(consistency, acquire readlock)
		 * */
		Transaction transaction = getTransaction(transactionID);
		if(transaction.getType().equals("RO")) {
			System.out.println(transactionID + " : " + onReadVariableID + " : " + transaction.tempTable.get(onReadVariableID));
		} else { // transaction type of RW
			// add readLock
			if(DM.checkReadState(onReadVariableID) == true) {
				if(DM.checkWriteLock(onReadVariableID) == false) {
					if(DM.checkReadLock(onReadVariableID) == false) {
						DM.setLock(transaction, onReadVariableID, "RL"); // add readlock
					}
					for(Site s : DM.database) {
						if(s.isUp() && s.isVariableExists(onReadVariableID)) {
							Variable readItem = DM.readVariable(s.getSiteIndex(), onReadVariableID);
							System.out.println(transactionID + " : " + onReadVariableID + " : " + readItem.getValue());
							break;
						}
					}
				} else {
					// all sites contains this variable has been write locked
					// add to waiting list
					if(!waitingAction.containsKey(transaction)) {
						addToWaitingAction(onReadVariableID, "R", null, transaction);
						System.out.println("Read action " + onReadVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
					}
					if(deadLockDetection() == false) {
						
					} else {
						System.out.print("deadlocked, ");
						killYoungest(); // kill youngest
					}
				}
			} else {
				if(!waitingAction.containsKey(transaction)) {
					addToWaitingAction(onReadVariableID, "R", null, transaction);
					System.out.println("Read action " + onReadVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
			}
			return;
		}
	}
	
	public Transaction getTransaction(String transactionID) {
		for(Transaction tr : runningTransaction) {
			if(tr.getTransactionID().equals(transactionID)) {
				return tr;
			} else {
				// print exception
			}
		}
		return null;
	}
	
	protected void addToWaitingAction(String VariableID, String actionType, String newValue, Transaction transaction) {
		ArrayList<String> actionInfo = new ArrayList<>();
		if(actionType == "R") {
			actionInfo.add(VariableID);
			actionInfo.add("R");
			actionInfo.add(newValue);
		} else if(actionType == "W") {
			actionInfo.add(VariableID);
			actionInfo.add("W");
			actionInfo.add(newValue);
		}
		waitingAction.put(transaction, actionInfo);
	}
	
	public void write(String transactionID, String onChangeVariableID, Integer onChangeValue) {
		/*
		 * At least one site contains this variable is up
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
		Transaction transaction = getTransaction(transactionID);
		if(DM.checkWriteState(onChangeVariableID) == true) {
			if((DM.checkWriteLock(onChangeVariableID) == false) && DM.checkReadLock(onChangeVariableID) == false) {
				// execute write action
				DM.setLock(transaction, onChangeVariableID, "WL");

				// write to tempTable
				transaction.tempTable.put(onChangeVariableID, onChangeValue);
				System.out.println("transaction " + transaction.getTransactionID() + " has changed variable " + onChangeVariableID + " to " + onChangeValue
						+ " in local copy");
			} else {
				if(!waitingAction.containsKey(transaction)) {
					addToWaitingAction(onChangeVariableID, "W", onChangeValue.toString(), transaction);
					System.out.println("Write action " + onChangeVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
				if(deadLockDetection() == false){
					System.out.println("no deadlocked");
				} else {
					System.out.print("deadlocked, ");
					// remove from waitinglist
					killYoungest();
				}
			}
		} else {
			if(!waitingAction.containsKey(transaction)) {
				addToWaitingAction(onChangeVariableID, "W", onChangeValue.toString(), transaction);
				System.out.println("Write action " + onChangeVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
			}
		}
	}
	
	public boolean deadLockDetection() {
		waitingGraph = new Graph();
		for(Transaction tr : waitingAction.keySet()) {
			waitingGraph.addVertices(tr.getTransactionID());
			String variableID = waitingAction.get(tr).get(0);
			Set<Transaction> holdLockTransactionSet = new HashSet<Transaction>();
			for(Site s : DM.database) {
				if(s.isVariableExists(variableID)) {
					holdLockTransactionSet = s.getLockTransaction(variableID);
					break;
				}
			}
			for(Transaction holdTr : holdLockTransactionSet) {
				waitingGraph.addEdge(tr.getTransactionID(), holdTr.getTransactionID());
				waitingGraph.addVertices(holdTr.getTransactionID());
			}
		}
		return waitingGraph.isCyclic();
	}
	
	public void killYoungest() {
		// compare timestamp of all transaction
		long youngest = Long.MIN_VALUE;
		Transaction youngestTransaction = new Transaction(null, null);
		for(Transaction tr : waitingAction.keySet()) {
			if (tr.getTimeStamp() > youngest) {
				youngest = tr.getTimeStamp();
				youngestTransaction = tr;
			}
		}
		System.out.println("" + youngestTransaction.getTransactionID() + " has been aborted");
		terminate(youngestTransaction);
		
	}
	
	public void end(String transactionID) {
		// for(transaction.tempTable(variable))
		// updateAllSites(variable);
		Transaction transaction = getTransaction(transactionID);
		if(transaction.getType().equals("RW")) {
			for(String changedVariableID : transaction.tempTable.keySet()) {
				int changedValue = transaction.tempTable.get(changedVariableID);
				DM.updateDatabase(changedVariableID, changedValue);
				System.out.println("Transaction " + transaction.getTransactionID() + ": changed the value of " + changedVariableID + " to "
				+ changedValue + " in database.");

			}
		}
		terminate(transaction);
		System.out.println("Transaction " + transaction.getTransactionID() + " has ended.");
	}
	
	public void terminate(Transaction transaction) {
		// clear transactionQueue
		if(runningTransaction.contains(transaction)) {
			runningTransaction.remove(transaction);
		}
		if(waitingAction.containsKey(transaction)) {
			waitingAction.remove(transaction);
		}
		// clear temp table
		transaction.tempTable.clear();
		// unlock all locks
		if(transaction.getType().equals("RW")) {
			DM.unLock(transaction);
		}

		// iterate all locks of transaction and set free
		if(waitingAction.size() != 0) {
			resumeWaitingAction();
		}
	}

	private void resumeWaitingAction() {
		Set<Transaction> set = new LinkedHashSet<Transaction>(waitingAction.keySet());
		for (Transaction tr : set) {
			String variableID = waitingAction.get(tr).get(0);
			String actionInfo = waitingAction.get(tr).get(1);
			String transactionID = tr.getTransactionID();
			switch (actionInfo) {
			case "R":
				read(transactionID, variableID);
				break;
			case "W":
				String valueS = waitingAction.get(tr).get(2);
				int value = Integer.parseInt(valueS);
				write(transactionID, variableID, value);
				break;
			}
			waitingAction.remove(tr);
		}
	}

	public void fail(int siteNum) {
		// transaction abort
	}
	
	public void recover(int siteNum) {
		// empty waitlist
	}

	public void dump() {
		DM.dump();
	}
	
	public void dump(int siteNum) {
		DM.dump(siteNum);
	}
	
	public void dump(String variable) {
		
	}
}