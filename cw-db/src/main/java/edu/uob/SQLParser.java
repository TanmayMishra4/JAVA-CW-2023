package edu.uob;

public class SQLParser {
    private final String tabSeparatorRegex = "\t";
    private final String spaceSeparatorRegex = "\s";
    private String res;

	public String getResult(){
		return this.res;
	}
    Tokenizer tokenizer;
    SQLParser(String command){
		res = new String();
		try {
			this.tokenizer = new Tokenizer(command);
		}
		catch(IllegalArgumentException e){
			res = e.toString();
		}

    }

    public String handleCommand(){
//		tokenizer.tokens.stream().forEach(System.out::println);
        return res;
    }

    private boolean checkSelect() {
        return false;
    }

    private boolean checkInsert() {
        return false;
    }

    private boolean checkAlter() {
        return false;
    }

    private boolean checkDrop() {
        return false;
    }

    private boolean checkCreate() {
        return false;
    }

    private boolean checkUse() {
        return false;

    }
}
