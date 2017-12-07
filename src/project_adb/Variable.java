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
 * This is the Variable class of the project, 
 * it defines variable structure containning its
 * ID, index, and value.
**/

package project_adb;

public class Variable {
	private String variableID;
	private int value;
	private int index;
	
	// Constructor
	protected Variable(int index) {
		this.index = index;
		this.value = index*10;
		this.variableID = "x" + index;
	}

	public int getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	public String getVariableID() {
		return variableID;
	}
	
	
	protected void setValue(int value) {
		this.value = value;
	}
}