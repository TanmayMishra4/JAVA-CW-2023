package edu.uob;

public class Token {
	private String value;
	private int spaces;

	Token(String value){
		setValue(value);
		int spaces = 0;
		int len = value.length();
		for(int i=len-1;i>=0;i--){
			char ch = value.charAt(i);
			if(ch == ' ')
				spaces++;
			else
				break;
		}
		setSpaces(spaces);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getSpaces() {
		return spaces;
	}

	public void strip(){
		value = value.strip();
	}

	public void setSpaces(int spaces) {
		this.spaces = spaces;
	}
}
