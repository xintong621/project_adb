/*
 * transaction begin --> datamanager --> create transaction(type, transactionID, timestamp, etc)
 * transaction 
 * 
 * */

package project_adb;

import java.util.List;

public class TM {
	private List<Transaction> running;
	private List<Transaction> waiting;
	
	protected TM() {
		
	}
	
	public void begin() {
		/*
		 * create new transaction
		 * */
	}
	
	public void read() {
		/*
		 * execute read action(consistency, acquire readlock)
		 * */
	}
	
	public void write() {
		/*
		 * execute write action
		 * */
	}
}