/*
 * transaction begin --> datamanager --> create transaction(type, transactionID, timestamp, etc)
 * transaction 
 * 
 * */

package project_adb;

import java.util.ArrayList;
import java.util.List;

public class TM {
	private List<Transaction> runningTransaction;
	private List<Transaction> waitingTransaction;
	
	protected TM() {
		runningTransaction = new ArrayList<Transaction>();
		waitingTransaction = new ArrayList<Transaction>();
	}
	
	public void begin(String transactionID, String transactionType) {
		/*
		 * create new transaction
		 * add to running
		 * */
		System.out.print(transactionID + " ");
		System.out.println(transactionType);
		Transaction transaction = new Transaction(transactionID, transactionType);
		runningTransaction.add(transaction);
		// cornercase: RO
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
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
		if(DM.checkWriteLock(onChangeVariable) == false) {
			// execute write action
			DM.setWriteLock(transaction, onChangeVariable, "WL");
			// write to tempTable
		} else {
			// add to waitingTransaction
			// if circle then abort
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
		
	}
	
	public void dump(int siteNum) {
		
	}
	
	public void dump(String variable) {
		
	}
}