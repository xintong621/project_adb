/*
 * transaction begin --> datamanager --> create transaction(type, transactionID, timestamp, etc)
 * transaction 
 * 
 * */

package project_adb;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public void write(Transaction transaction, String onChangeVariable, Integer onChangeValue) {
		/*
		 * At least one site contains this variable is up
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
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
	
	public void end(Transaction transactionID) {
		// for(transaction.tempTable(variable))
		// updateAllSites(variable);
	}
	
	public void terminate(Transaction transactionID) {
		// runningTransaction.remove this transaction
		// waitingTransaction.remove
		// clear temp table
		// unlock all locks
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