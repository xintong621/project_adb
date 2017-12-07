/**
 * Advanced Database System
 * Replicated Concurrency Control and Recovery
 * 
 * @Author: Xintong Wang(N18322289)
 * @Author: Dailing Zhu(N11754882)
 * 
 * @Date: 07/12/2017
 * 
 * @Class_Description:
 * This is the TransactionManager class of the project, 
 * it implement all the action a transaction will take
 * such as begin, read, write, and end.
**/



/*
 * transaction begin --> 
 *             datamanager --> 
 *             create transaction(type, transactionID, timestamp, etc) 
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
		System.out.println("Begin: " + transactionID + " of type " + transactionType + " successfully initialized");
		runningTransaction.add(transaction);

		return transaction;
	}
	
	public boolean read(String transactionID, String onReadVariableID) {
		/*
		 * execute read action(consistency, acquire readlock)
		 * */
		Transaction transaction = getTransaction(transactionID);
		if(transaction == null) {
			System.err.println("alert: Transaction " + transactionID + " does not exist");
			return true;
		}
		if(transaction.getType().equals("RO")) {
			int readTempValue = transaction.tempTable.get(onReadVariableID);
			if (readTempValue == -1) {
				System.err.println("alert: Couldn't find value of " + onReadVariableID);
			} else {
				System.out.println("Read: transaction " + transactionID + " has read variable " + onReadVariableID + " with value " + readTempValue);
				return true;
			}
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
							if (readItem.getValue() == -1) {
								System.err.println("alert: Couldn't find value of " + readItem.getVariableID());
							} else {
								System.out.println("Read: transaction " + transactionID + " has read variable " + onReadVariableID + "." + s.getSiteIndex() + " with value " + readItem.getValue());
								return true;
							}
							break;
						}
					}
				} else {
					// all sites contains this variable has been write locked
					// add to waiting list
					if(!waitingAction.containsKey(transaction)) {
						addToWaitingAction(onReadVariableID, "R", null, transaction);
						System.out.println("Waitlist: Read action " + onReadVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
					}
					if(deadLockDetection() == false) {
						System.out.println("Deadlock: There is no deadlock");
					} else {
						System.out.print("Deadlock: Deadlock detected, ");
						killYoungest(); // kill youngest
					}
				}
			} else {
				if(!waitingAction.containsKey(transaction)) {
					addToWaitingAction(onReadVariableID, "R", null, transaction);
					System.out.println("Waitlist: Read action " + onReadVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
			}
		}
		return false;
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
	
	protected boolean isExsistingTransaction(String transactionID) {
		for(Transaction tr:runningTransaction) {
			if(tr.getTransactionID().equals(transactionID)) {
					return true;
			}
		}
		return false;
	}
	
	public boolean write(String transactionID, String onChangeVariableID, Integer onChangeValue) {
		/*
		 * At least one site contains this variable is up
		 * iswritelocked == false
		 * execute write action(acquire writelock, write to all copy)
		 * */
		Transaction transaction = getTransaction(transactionID);
		if(transaction == null) {
			System.err.println("alert: Transaction " + transactionID + " does not exist");
			return true;
		}
		if(DM.checkWriteState(onChangeVariableID) == true) {
			if((DM.checkWriteLock(onChangeVariableID) == false) && DM.checkReadLock(onChangeVariableID) == false) {
				// execute write action
				DM.setLock(transaction, onChangeVariableID, "WL");

				// write to tempTable
				transaction.tempTable.put(onChangeVariableID, onChangeValue);
				System.out.println("Write: transaction " + transaction.getTransactionID() + " has changed variable " + onChangeVariableID + " to " + onChangeValue
						+ " in local copy");
				return true;
			} else {
				if(!waitingAction.containsKey(transaction)) {
					addToWaitingAction(onChangeVariableID, "W", onChangeValue.toString(), transaction);
					System.out.println("Waitlist: Write action " + onChangeVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
				}
				if(deadLockDetection() == false){
					System.out.println("Deadlock: there is no deadlock");
				} else {
					System.out.print("Deadlock: deadlock detected, ");
					// remove from waitinglist
					killYoungest();
				}
			}
		} else {
			if(!waitingAction.containsKey(transaction)) {
				addToWaitingAction(onChangeVariableID, "W", onChangeValue.toString(), transaction);
				System.out.println("Waitlist: Write action " + onChangeVariableID + " of " + "Transaction " + transaction.getTransactionID() + " has been added to waiting list");
			}
		}
		return false;
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
		ArrayList<String> arr = waitingGraph.cycleList();
		for(String transactionID : arr) {
			for(Transaction tr : waitingAction.keySet()) {
				if(tr.getTransactionID().equals(transactionID)) {
					if (tr.getTimeStamp() > youngest) {
						youngest = tr.getTimeStamp();
						youngestTransaction = tr;
					}
					break;
				}
			}
		}
		System.out.println("" + youngestTransaction.getTransactionID() + " has been aborted");
		terminate(youngestTransaction);
		
	}
	
	public void end(String transactionID) {
		// for(transaction.tempTable(variable))
		// updateAllSites(variable);
		Transaction transaction = getTransaction(transactionID);
		if(transaction == null) {
			System.err.println("alert: Transaction " + transactionID + " does not exist");
			return;
		}
		if(transaction.getType().equals("RW")) {
			for(String changedVariableID : transaction.tempTable.keySet()) {
				int changedValue = transaction.tempTable.get(changedVariableID);
				DM.updateDatabase(changedVariableID, changedValue);
				System.out.println("Commit: Transaction " + transaction.getTransactionID() + ": changed the value of " + changedVariableID + " to "
				+ changedValue + " in database.");

			}
		}
		System.out.println("Terminate: Transaction " + transaction.getTransactionID() + " has ended.");
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
		boolean couldRemove = false;
		for (Transaction tr : set) {
			String variableID = waitingAction.get(tr).get(0);
			String actionInfo = waitingAction.get(tr).get(1);
			String transactionID = tr.getTransactionID();
			runningTransaction.add(tr);
			if(actionInfo.equals("R")) {
				couldRemove = read(transactionID, variableID);
			} else {
				String valueS = waitingAction.get(tr).get(2);
				int value = Integer.parseInt(valueS);
				couldRemove = write(transactionID, variableID, value);
			}
			if(couldRemove) {
				waitingAction.remove(tr);
			}
			break;
		}
	}

	public void fail(String siteNum) {
		// transaction abort
		int siteID = Integer.parseInt(siteNum);
		boolean siteHasRunning = false;
		for(Site s : DM.database) {
			if(s.getSiteIndex() == siteID) {
				for(Transaction transaction : runningTransaction) {
					if(s.ifSiteContainsTransaction(transaction)) {
						DM.fail(siteID);
						System.err.println("" + transaction.getTransactionID() + " has been aborted because site " + siteID + " is down");
						terminate(transaction);
						siteHasRunning = true;
						break;
					}
				}
				if(siteHasRunning == false) {
					DM.fail(siteID);
				}
				break;
			}
		}
		
		
	}
	
	public void recover(String siteNum) {
		int siteID = Integer.parseInt(siteNum);
		DM.recover(siteID);
		resumeWaitingAction();
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