package project_adb;

public class Variable {
	private int index;
	private int value;

	public int getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	
	protected Variable(int index) {
		this.index = index;
		this.value = index*10;
	}
}