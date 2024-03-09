package edu.uob;

import edu.uob.Controller.DBController;
import edu.uob.DBExceptions.DBException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uob.ResponseType.ERROR;
import static edu.uob.ResponseType.OK;

public class SQLParser {
    private DBController dbController;
    private String res;
    Tokenizer tokenizer;
    SQLParser(String command, DBController dbController){
		res = new String();
        this.dbController = dbController;
        this.tokenizer = new Tokenizer(command);
        dbController = new DBController();
    }

    public String handleCommand(){// TODO check only one semi colon is present
        String lastToken = tokenizer.getLastToken();
        if (lastToken.equals(";")) {
            try {
                checkCommandType();
                return Utils.generateResponse(OK, res);
            } catch (DBException e) {
                return Utils.generateResponse(ERROR, e.toString());
            }
        }
        return Utils.generateResponse(ERROR, "Semicolon not found");
    }

    private void checkCommandType() throws DBException {
        if(!checkUse() && !checkCreate() && !checkDrop() && !checkAlter() && !checkInsert()
                && !checkSelect() && !checkDelete() && !checkUpdate() && !checkJoin()){
            throw new DBException("Illegal Query");
        }
    }

    private boolean checkJoin() throws DBException {
//        String currentToken = tokenizer.getCurrentToken();
//        tokenizer.next();
        boolean isValid = false;
//        if(currentToken.equalsIgnoreCase("JOIN")){
//            isValid = true;
//            String tableName1 = tokenizer.getCurrentToken();
//            tokenizer.next();
//            isValid = checkTableName();
//            tokenizer.next();
//            String andToken = tokenizer.getCurrentToken();
//            tokenizer.next();
//            if(andToken.equalsIgnoreCase("AND")){
//                isValid = isValid && checkTableName();
//            }
//            tokenizer.next();
//            String onToken = tokenizer.getCurrentToken();
//            tokenizer.next();
//            if(onToken.equalsIgnoreCase("ON")){
//                isValid = isValid && checkAttributeName();
//            }
//            tokenizer.next();
//            andToken = tokenizer.getCurrentToken();
//            if(andToken.equalsIgnoreCase("AND")){
//                isValid = isValid && checkAttributeName();
//            }
//            tokenizer.previous();
//            tokenizer.previous();
//            tokenizer.previous();
//            tokenizer.previous();
//            tokenizer.previous();
//        }
//        tokenizer.previous();
        return isValid;
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

    private boolean checkDrop() throws DBException{
        String currentToken = tokenizer.getCurrentToken();
        boolean isValid = false;
        tokenizer.next();
        if(currentToken.equalsIgnoreCase("DROP")){
            String operationType = tokenizer.getCurrentToken();
            tokenizer.next();
            if(operationType.equalsIgnoreCase("DATABASE")) {
                String dbName = checkDatabaseName();
                dbController.deleteDB(dbName);
                isValid = true;
            }
            else if(operationType.equalsIgnoreCase("TABLE")) {
                String tableName = checkTableName();
                dbController.deleteTable(tableName);
                isValid = true;
            }
            tokenizer.previous();
        }
        tokenizer.previous();
        return isValid;
    }

    private String checkTableName() throws DBException {
        // same rules as DB name
        return checkDatabaseName();
    }

    private boolean checkCreate() throws DBException {
        return checkCreateTable() || checkCreateDatabase();
    }

    private boolean checkCreateTable() throws DBException { // TODO check for Bounds on tokens, if out of bounds raise and exception
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        String tokenAhead = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("TABLE")){
            String tableName = checkTableName();
            dbController.createTable();
            if(tokenizer.getPos() ==  tokenizer.getSize()-2){ // if no attributes present
                tokenizer.previous();
                tokenizer.previous();
                return true;
            }
            tokenizer.next();
            String openingBracket = tokenizer.getCurrentToken();
            tokenizer.next();
            String closingBracket = tokenizer.get(tokenizer.getSize()-2);
            if(openingBracket.equals("(") && closingBracket.equalsIgnoreCase(")")){
                List<String> attbrList = checkAttributeList();
                isValid = true;
                dbController.createTable(attbrList);
            }
            tokenizer.previous();
            tokenizer.previous();
            // TODO check for ")" closing bracket
        }
        tokenizer.previous();
        tokenizer.previous();
        return isValid;
    }

    private List<String> checkAttributeList() throws DBException {
        // TODO to be completed
        List<String> attributeList = new ArrayList<>();
        int initialIndex = tokenizer.getPos();
        while(!tokenizer.getCurrentToken().equals(")")){
            String attributeName = checkAttributeName();
            attributeList.add(attributeName);
            tokenizer.next();
            String comma = tokenizer.getCurrentToken();
            if(comma.equals(")")) break;
            if(!comma.equals(",")) throw new DBException("Error parsing Attributes ");
            tokenizer.next();
        }
        tokenizer.setPos(initialIndex);
        if(attributeList.size() == 0) throw new DBException("Empty Attribute List not allowed");
        return attributeList;
    }

    private String checkAttributeName() throws DBException{
        return checkDatabaseName();
    }

    private boolean checkCreateDatabase() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        String tokenAhead = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("DATABASE")){
            String dbName = checkDatabaseName();
            dbController.createDB(dbName);
            isValid = true;
        }
        tokenizer.previous();
        tokenizer.previous();
        return isValid;
    }

    private boolean checkUse() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        boolean isValid = false;
        if(currentToken.equalsIgnoreCase("USE")){
            String dbName = checkDatabaseName();
            dbController.setActiveDB(dbName);
            isValid = true;
        }
        tokenizer.previous();
        return isValid;
    }

    private String checkDatabaseName() throws DBException {
        // TODO to be completed
        String currentToken = tokenizer.getCurrentToken();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{1,}$");
        Matcher matcher = pattern.matcher(currentToken);
        if (!matcher.find())
            throw new DBException("Identifier Name contains Illegal characters, only alphanumeric allowed");
        if (Utils.isNotKeyWord(currentToken)) throw new DBException("Identifier Name cannot be a keyword");
        return currentToken;
    }
}
