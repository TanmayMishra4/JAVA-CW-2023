package edu.uob;

import edu.uob.Controller.DBController;
import edu.uob.DBExceptions.DBException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static edu.uob.AlterationType.ADD;
import static edu.uob.AlterationType.DROP;
import static edu.uob.ResponseType.ERROR;
import static edu.uob.ResponseType.OK;

public class SQLParser {
    // TODO do tokenizer.previous only on false return
    private DBController dbController;
    private String res;
    Tokenizer tokenizer;
    SQLParser(String command, DBController dbController){
		res = "";
        this.dbController = dbController;
        this.tokenizer = new Tokenizer(command);
        this.dbController = new DBController();
    }

    public String handleCommand(){// TODO check only one semi colon is present
        res = "";
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
        int initialIndex = tokenizer.getPos();
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        if (currentToken.equalsIgnoreCase("JOIN")) {
            try {
                String tableName1 = checkTableName();
                String andToken = tokenizer.getCurrentToken();
                tokenizer.next();
                String tableName2 = null;
                if (andToken.equalsIgnoreCase("AND")) {
                    tableName2 = checkTableName();
                }
                String onToken = tokenizer.getCurrentToken();
                tokenizer.next();
                String attributeName1 = null, attributeName2 = null;
                if (onToken.equalsIgnoreCase("ON")) {
                    attributeName1 = checkAttributeName();
                }
                andToken = tokenizer.getCurrentToken();
                tokenizer.next();
                if (andToken.equalsIgnoreCase("AND")) {
                    attributeName2 = checkAttributeName();
                }
                dbController.joinTables(tableName1, tableName2, attributeName1, attributeName2);
            }
            catch(DBException e){
                tokenizer.setPos(initialIndex);
                throw e;
            }
        }
        else return false;
        return true;
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

    private boolean checkInsert() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        int initialIndex = tokenizer.getPos();
        tokenizer.next();
        currentToken = currentToken + " " + tokenizer.getCurrentToken();
        tokenizer.next();
        if(currentToken.equalsIgnoreCase("INSERT INTO")){
            int initialIndex2 = tokenizer.getPos();
            try{
                String tableName = checkTableName();
                String valuesToken = tokenizer.getCurrentToken();
                tokenizer.next();
                if(!valuesToken.equalsIgnoreCase("VALUES")) throw new DBException("Illegal Query keyword VALUES not found");
                String openingBracket = tokenizer.getCurrentToken();
                tokenizer.next();
                String closingBracket = tokenizer.get(tokenizer.getSize()-2);
                if(!openingBracket.equals("(") && !closingBracket.equals(")")) throw new DBException("Illegal Query brackets mismatch or not found");
                List<ValueLiteral> valueList = checkValueList();
                dbController.insertValues(tableName, valueList);
            }
            catch (DBException e){
                tokenizer.setPos(initialIndex2);
                throw e;
            }
        }
        else{
            tokenizer.setPos(initialIndex);
            return false;
        }
        return true;
    }

    private List<ValueLiteral> checkValueList() throws DBException {
        List<ValueLiteral> resultList = new ArrayList<>();
        int index = tokenizer.getPos();
        int initialIndex = index;
        try {
            while (!tokenizer.getCurrentToken().equals(")")) {
                ValueLiteral value = checkValue();
                resultList.add(value);
                String comma = tokenizer.getCurrentToken();
                tokenizer.next();
                if(comma.equals(")")) break;
                if(!comma.equals(",")) throw new DBException("Values should be separated by ,(comma)");
                index++;
            }
        }
        catch(DBException e){
            tokenizer.setPos(initialIndex);
            throw e;
        }

        if(resultList.size() == 0) throw new DBException("Value List cannot be empty");
        return resultList;
    }

    private ValueLiteral checkValue() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        try(ValueLiteral<String> val = checkStringLiteral()){
            tokenizer.next();
            return val;
        } catch (Exception e) {}

        try(ValueLiteral<Boolean> val = checkBooleanLiteral()){
            tokenizer.next();
            return val;
        } catch (Exception e) {}

        try(ValueLiteral<Double> val = checkFloatLiteral()){
            tokenizer.next();
            return val;
        } catch (Exception e) {}

        try(ValueLiteral<Integer> val = checkIntegerLiteral()){
            tokenizer.next();
            return val;
        } catch (Exception e) {}

        try(ValueLiteral<NULLObject> val = checkNullLiteral()){
            tokenizer.next();
            return val;
        } catch (Exception e) {}

        tokenizer.previous();
        throw new DBException("Illegal Value Type provided");
    }

    private ValueLiteral<NULLObject> checkNullLiteral() throws Exception {
        String currentToken = tokenizer.getCurrentToken();
        if(!currentToken.equalsIgnoreCase("NULL")) {
            throw new Exception("Not NULL Value in NULL Object");
        }
        return new ValueLiteral<>(new NULLObject());
    }

    private ValueLiteral<Integer> checkIntegerLiteral() throws Exception {
        String currentToken = tokenizer.getCurrentToken();
        Integer val = null;
        try{
            val = Integer.parseInt(currentToken);
        }
        catch(Exception e){
            throw e;
        }
        return new ValueLiteral<Integer>(val);
    }

    private ValueLiteral<Double> checkFloatLiteral() throws Exception {
        String currentToken = tokenizer.getCurrentToken();
        Double val = null;
        try{
            val = Double.parseDouble(currentToken);
        }
        catch(Exception e){
            throw e;
        }
        return new ValueLiteral<Double>(val);
    }

    private ValueLiteral<Boolean> checkBooleanLiteral() throws Exception {
        String currentToken = tokenizer.getCurrentToken();
        Boolean val = null;
        if(currentToken.equalsIgnoreCase("TRUE")) val = Boolean.valueOf("true");
        else if(currentToken.equalsIgnoreCase("FALSE")) val = Boolean.valueOf("false");
        else{
            throw new Exception("Not a Boolean Literal");
        }
        return new ValueLiteral<Boolean>(val);
    }

    private ValueLiteral<String> checkStringLiteral() throws Exception {
        String currentToken = tokenizer.getCurrentToken();
        int stringLength = currentToken.length();
        char closingBracket = currentToken.charAt(stringLength - 1);
        char openingBracket = currentToken.charAt(0);
        String stringVal = null;
        if(openingBracket == '\'' && closingBracket ==  '\''){
            stringVal = currentToken.substring(1, stringLength-1);
            for(char charVal : stringVal.toCharArray()){
                if(Character.isDigit(charVal) || Character.isLetter(charVal) || charVal == ' ' || Utils.isSymbol(charVal)) continue;
                else throw new DBException("String contains Illegal characters");
            }
        }
        else throw new Exception("Not a String Literal");

        return new ValueLiteral<String>(stringVal);
    }

    private boolean checkAlter() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        if(currentToken.equalsIgnoreCase("ALTER")) {
            try {
                String tableToken = tokenizer.getCurrentToken();
                tokenizer.next();
                String tableName = checkTableName();
                AlterationType alterationType = checkAlterationType();
                String attributeName = checkAttributeName();
                dbController.alterTable(tableName, alterationType);
            } catch (DBException e) {
                tokenizer.previous();
                tokenizer.previous();
                throw e;
            }
        }
        else{
            tokenizer.previous();
            return false;
        }
        return true;
    }

    private AlterationType checkAlterationType() throws DBException {
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        AlterationType result;
        if(currentToken.equalsIgnoreCase("ADD")) result = ADD;
        else if(currentToken.equalsIgnoreCase("DROP")) result = DROP;
        else {
            tokenizer.previous();
            throw new DBException("Invalid AlterationType specified");
        }
        return result;
    }

    private boolean checkDrop() throws DBException{
        String currentToken = tokenizer.getCurrentToken();
        tokenizer.next();
        if(currentToken.equalsIgnoreCase("DROP")){
            String operationType = tokenizer.getCurrentToken();
            tokenizer.next();
            if(operationType.equalsIgnoreCase("DATABASE")) {
                String dbName = checkDatabaseName();
                dbController.deleteDB(dbName);
                return true;
            }
            else if(operationType.equalsIgnoreCase("TABLE")) {
                String tableName = checkTableName();
                dbController.deleteTable(tableName);
                return true;
            }
            else tokenizer.previous();
        }
        tokenizer.previous();
        return false;
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
            if(tokenizer.getPos() ==  tokenizer.getSize()-1){ // if no attributes present
//                tokenizer.previous();
//                tokenizer.previous();
                dbController.createTable();
                return true;
            }
//            tokenizer.next();
            String openingBracket = tokenizer.getCurrentToken();
            tokenizer.next();
            String closingBracket = tokenizer.get(tokenizer.getSize()-2);
            if(openingBracket.equals("(") && closingBracket.equalsIgnoreCase(")")){
                List<String> attbrList = checkAttributeList();
                isValid = true;
                dbController.createTable(attbrList);
            }
            else {
//                tokenizer.previous();
                tokenizer.previous();
                return false;
            }
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
            String comma = tokenizer.getCurrentToken();
            tokenizer.next();
            if(comma.equals(")")) break;
            if(!comma.equals(",")) {
                tokenizer.setPos(initialIndex);
                throw new DBException("Error parsing Attributes ");
            }
        }
        if(attributeList.size() == 0){
            tokenizer.setPos(initialIndex);
            throw new DBException("Empty Attribute List not allowed");
        }
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
            return true;
        }
        tokenizer.previous();
        tokenizer.previous();
        return false;
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
        tokenizer.next();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{1,}$");
        Matcher matcher = pattern.matcher(currentToken);
        if (!matcher.find()) {
            tokenizer.previous();
            throw new DBException("Identifier Name contains Illegal characters, only alphanumeric allowed");
        }
        if (Utils.isNotKeyWord(currentToken)){
            tokenizer.previous();
            throw new DBException("Identifier Name cannot be a keyword");
        }
        return currentToken;
    }
}
