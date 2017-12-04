package project_adb;

import java.util.HashMap;
import java.util.List;

public class Transaction {
	private String transactionID;
	private long timeStamp;
	private String transactionType;
	protected HashMap<Integer ,List<String>> locks;
	protected String currentState;
	
	protected Transaction(String id, String transactionType) {
		this.transactionID = id; // such as T1, T2...,etc
		this.transactionType = transactionType; // as in RO and RW
		this.timeStamp = System.currentTimeMillis(); // record the current timestamp
	}
	
	protected long getTimeStamp() {
		return timeStamp;
	}
}