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
import java.util.List;
import java.util.Set;

public class TM {
	private List<Transaction> runningTransaction;
	private LinkedHashMap<Transaction, String> waitingAction;
	private Graph waitingGraph;
	
	protected TM() {
		runningTransaction = new ArrayList<Transaction>();
		waitingAction = new LinkedHashMap<Transaction, String>();
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
						}
					}
				} else {
					// all sites contains this variable has been write locked
					// add to waiting list
					if(deadLockDetection() == false) {
						if(!waitingAction.containsKey(transaction)) {
							waitingAction.put(transaction, onReadVariableID);
							System.out.println("Read action " + onReadVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
						}
					} else {
						killYoungest(); // kill youngest
					}
				}
			} else {
				if(!waitingAction.containsKey(transaction)) {
					waitingAction.put(transaction, onReadVariableID);
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
	
	public void write(String transactionID, String onChangeVariable, Integer onChangeValue) {
		/*
		 * At least one site contains this variable is up
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
		Transaction transaction = getTransaction(transactionID);
		if(DM.checkWriteState(onChangeVariable) == true) {
			if((DM.checkWriteLock(onChangeVariable) == false) && DM.checkReadLock(onChangeVariable) == false) {
				// execute write action
				DM.setLock(transaction, onChangeVariable, "WL");

				// write to tempTable
				transaction.tempTable.put(onChangeVariable, onChangeValue);
				System.out.println("transaction " + transaction.getTransactionID() + " has changed variable " + onChangeVariable + " to " + onChangeValue
						+ " in local copy");
			} else {
				if(!waitingAction.containsKey(transaction)) {
					waitingAction.put(transaction, onChangeVariable);
					System.out.println("Write action " + onChangeVariable + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
				if(deadLockDetection() == false){
					System.out.println("no deadlocked");
				} else {
					System.out.println("deadlocked");
					// remove from waitinglist
					killYoungest();
				}
			}
		} else {
			if(!waitingAction.containsKey(transaction)) {
				waitingAction.put(transaction, onChangeVariable);
				System.out.println("Write action " + onChangeVariable + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
			}
		}
	}
	
	public boolean deadLockDetection() {
		waitingGraph = new Graph();
		for(Transaction tr : waitingAction.keySet()) {
			waitingGraph.addVertices(tr.getTransactionID());
			String variableID = waitingAction.get(tr);
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
		
		System.out.println("Transaction " + transaction.getTransactionID() + " has ended.");
		
		terminate(transaction);
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
		
		DM.unLock(transaction);

		// iterate all locks of transaction and set free
		// dequeueWaitingTransactions();
	}
	
	public void abort() {

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