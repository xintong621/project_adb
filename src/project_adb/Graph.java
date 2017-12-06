package project_adb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graph {
	// create an Array of lists for Adjacency List Representation
	private Map<String, ArrayList<String>> adj;
	private HashMap<String, Boolean> vertices;
	ArrayList<String> cycleList;
	
	public Graph() {
		adj = new HashMap<String, ArrayList<String>>();
		vertices = new HashMap<>();
		cycleList = new ArrayList<String>();
		
	}
	
	protected int getVerticeNum() {
		return vertices.size();
	}
	protected boolean isCyclic() {
		Set<String> recStack = new HashSet<>();
		for(String v : vertices.keySet()) {
			if(helper(v, recStack)) {
				return true;
			}
		}
		return false;
	}
	
	protected ArrayList<String> cycleList() {
		return cycleList;
	}
	
	protected boolean helper(String v, Set<String> recStack) {
		if(vertices.get(v) == false) {
			vertices.replace(v, true);
			recStack.add(v);
			cycleList.add(v);
			for(String i : adj.get(v)) {
				if(vertices.get(i) == false) {
					if(helper(i, recStack))
						return true;
				}
				else if(recStack.contains(i)) {
					return true;
				}
			}
		}
		recStack.remove(v);
		return false;
		
	}
	protected void addEdge(String inComingTransaction, String holdLockTransaction) {
		this.adj.get(inComingTransaction).add(holdLockTransaction);
	}
	protected void addVertices(String transactionID) {
		if(!vertices.containsKey(transactionID)) {
			this.vertices.put(transactionID, false);
			this.adj.put(transactionID, new ArrayList<String>());
		}
	}
	
}