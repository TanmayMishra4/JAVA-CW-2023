package edu.uob;

public class SQLParser {
    private final String tabSeparatorRegex = "\t";
    private final String spaceSeparatorRegex = "\s";
    private String res;
    Token tokens;
    SQLParser(String command){
        this.tokens = new Token(command);
        res = new String();
    }

    public String handleCommand(){
        String currentToken = tokens.getLastToken();
        if(!currentToken.equals(";")){ // TODO deal with no semi colon case (maybe raise an exception as well)
            return "[ERROR] No semi colon";
        }
        if(checkUse() || checkCreate() || checkDrop() || checkAlter() || checkInsert() || checkSelect()){
            return res;
        }
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
