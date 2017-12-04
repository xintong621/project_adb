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
		this.transactionID = id;
		this.transactionType = transactionType;
		this.timeStamp = System.currentTimeMillis();
	}
	
	protected long getTimeStamp() {
		return timeStamp;
	}
}