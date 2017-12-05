/*
 * transaction begin --> datamanager --> create transaction(type, transactionID, timestamp, etc)
 * transaction 
 * 
 * */

package project_adb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class TM {
	private List<Transaction> runningTransaction;
	private HashMap<Transaction, String> waitingAction;
	
	protected TM() {
		runningTransaction = new ArrayList<Transaction>();
		waitingAction = new HashMap<Transaction, String>();
	}
	
	public Transaction begin(String transactionID, String transactionType) {
		/*
		 * create new transaction
		 * add to running
		 * */
		Transaction transaction = new Transaction(transactionID, transactionType);
		if(transaction.getType().equals("RO")) {
			
		}
		System.out.println(transactionID + " of type " + transactionType + " successfully initialized");
		runningTransaction.add(transaction);

		return transaction;
	}
	
	public void read() {
		/*
		 * execute read action(consistency, acquire readlock)
		 * */
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
			if(DM.checkWriteLock(onChangeVariable) == false) {
				// execute write action
				DM.setWriteLock(transaction, onChangeVariable, "WL");
				// write to tempTable
				transaction.tempTable.put(onChangeVariable, onChangeValue);
				System.out.println("transaction " + transaction.getTransactionID() + " has changed variable " + onChangeVariable + " to " + onChangeValue
						+ " in local copy");
			} else {
				// deadlock detection
				// kill youngest
				if(!waitingAction.containsKey(transaction)) {
					waitingAction.put(transaction, onChangeVariable);
					System.out.println("Write action " + onChangeVariable + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
				// add transaction to waiting list
				// add to waitingTransaction
			}
		} else {
			if(!waitingAction.containsKey(transaction)) {
				waitingAction.put(transaction, onChangeVariable);
				System.out.println("Write action " + onChangeVariable + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
			}
		}
	}
	
	public void updateAllSites(String variable) {
		// update all sites with temptable
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
		// iterate all locks of transaction and set free
	}
	
	public void abort() {

	}
	
	public void fail() {
		
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