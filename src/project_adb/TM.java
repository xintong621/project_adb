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
	}
	
	public void read() {
		/*
		 * execute read action(consistency, acquire readlock)
		 * */
	}
	
	public boolean iswritelocked() {
		return false;
	}
	
	public void write(String transactionID, String onChangeVariable, Integer onChangeValue) {
		/*
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
	}
	
	public void end() {
		
	}
}