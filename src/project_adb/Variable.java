package project_adb;

public class Variable {
	private String variableID;
	private int value;
	private int index;

	public int getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	public String getVariableID() {
		return variableID;
	}
	
	protected Variable(int index) {
		this.index = index;
		this.value = index*10;
		this.variableID = "x" + index;
	}
}