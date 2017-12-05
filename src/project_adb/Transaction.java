package project_adb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Hashtable;

public class Transaction {
	private String transactionID;
	private long timeStamp;
	private String transactionType;
	protected HashMap<Integer ,List<String>> locks;
	protected String currentState;
	public Hashtable<String, Integer> tempTable = new Hashtable<>(); // temporarily store changed value
	
	protected Transaction(String id, String transactionType) {
		this.transactionID = id; // such as T1, T2...,etc
		this.transactionType = transactionType; // as in RO and RW
		this.timeStamp = System.currentTimeMillis(); // record the current timestamp
		locks = new HashMap<Integer ,List<String>>();
		for(int i : locks.keySet())
			locks.put(i, new ArrayList<String>());
	}
	
	protected String getTransactionID() {
		return transactionID;
	}
	
	protected long getTimeStamp() {
		return timeStamp;
	}
	
	protected String getType() {
		return transactionType;
	}
}