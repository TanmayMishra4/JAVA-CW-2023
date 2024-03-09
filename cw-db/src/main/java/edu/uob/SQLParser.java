package edu.uob;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uob.ResponseType.ERROR;
import static edu.uob.ResponseType.OK;

public class SQLParser {
    private final String tabSeparatorRegex = "\t";
    private final String spaceSeparatorRegex = "\s";
    private String res;
    Tokenizer tokenizer;
    SQLParser(String command){
		res = new String();
        this.tokenizer = new Tokenizer(command);
    }

    public String handleCommand(){// TODO check only one semi colon is present
        String lastToken = tokenizer.getLastToken();
        if(lastToken.equals(";")) {
            if(checkCommandType())
                return Utils.generateResponse(OK, res);
            else
                return Utils.generateResponse(ERROR, "Wrong Query");
        }
        return Utils.generateResponse(ERROR, "Semicolon not found");

    }

    private boolean checkCommandType() {
        return checkUse() || checkCreate() || checkDrop() || checkAlter() || checkInsert()
                || checkSelect() || checkDelete() || checkUpdate() || checkJoin();
    }

    private boolean checkJoin() {
        return false;
    }

    private boolean checkDelete() {
        return false;
    }

    private boolean checkUpdate() {
        return false;
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
        String currentToken = tokenizer.getCurrentToken();
        boolean isValid = false;
        tokenizer.next();
        if(currentToken.equalsIgnoreCase("DROP")){
            String operationType = tokenizer.getCurrentToken();
            tokenizer.next();
            if(operationType.equalsIgnoreCase("DATABASE"))
                isValid = checkDatabaseName();
            else if(operationType.equalsIgnoreCase("TABLE"))
                isValid = checkTableName();
            tokenizer.previous();
        }
        tokenizer.previous();
        return isValid;
    }

    private boolean checkTableName() {
        // same rules as DB name
        return checkDatabaseName();
    }

    private boolean checkCreate() {
        return checkCreateTable() || checkCreateDatabase();
    }

    private boolean checkCreateTable() { // TODO check for Bounds on tokens, if out of bounds raise and exception
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        String tokenAhead = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("TABLE")){
            isValid = checkTableName();
            tokenizer.next();
            tokenizer.next();
            String additionalToken = tokenizer.getCurrentToken();
            if(additionalToken.equals("(")){
                isValid = checkAttributeList();
            }
            String closingBracket = tokenizer.get(tokenizer.getSize()-2);
            if(closingBracket.equalsIgnoreCase(")")){
                isValid = isValid && true;
            }
            // TODO check for ")" closing bracket
        }
        tokenizer.previous();
        tokenizer.previous();
        return isValid;
    }

    private boolean checkAttributeList() {
        // TODO to be completed
        
        return false;
    }

    private boolean checkCreateDatabase() {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        String tokenAhead = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("DATABASE")){
            isValid = checkDatabaseName();
        }
        tokenizer.previous();
        tokenizer.previous();
        return isValid;
    }

    private boolean checkUse() {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("USE")){
            isValid = checkDatabaseName();
        }
        tokenizer.previous();
        return isValid;
    }

    private boolean checkDatabaseName() {
        // TODO to be completed
        String currentToken = tokenizer.getCurrentToken();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{1,}$");
        Matcher matcher = pattern.matcher(currentToken);
        return matcher.find();
    }
}
