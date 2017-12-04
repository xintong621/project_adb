package project_adb;

import java.util.HashMap;

public class Site {
	private int siteIndex;
	private boolean isUp; // All sites are up in initial state
	private HashMap<Variable, Boolean> variable; // Boolean ====>> exist
	
	protected Site(int index) {
		variable = new HashMap<>();
		isUp = true;
		this.siteIndex = index;
		
		// put all variables to this site along with exist
		for (int i = 1; i <= 20; i++) {
			Variable var = new Variable(i);
			if (i % 2 == 0) {
				variable.put(var, true);
				//isReady.put(var.getVariableID(), true);
			} else if ((i + 1) % 10 == siteIndex) {
				variable.put(var, true);
				//isReady.put(var.getVariableID(), true);
			} else {
				variable.put(var, false);
				//isReady.put(var.getVariableID(), false);
			}
			//locktable.put(var, new HashMap<Transaction, Lock>());
		}
	}
	
	protected void failSite() {
		isUp = false;
	}
}