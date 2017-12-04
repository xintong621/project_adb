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
	}
	
	protected void failSite() {
		isUp = false;
	}
}