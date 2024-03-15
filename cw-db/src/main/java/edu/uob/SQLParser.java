package edu.uob;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.QueryExceptions.*;
import edu.uob.Controller.DBController;
import edu.uob.Model.*;
import edu.uob.Service.Tokeniser;
import edu.uob.Utils.Utils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.uob.AllEnums.AlterationType.ADD;
import static edu.uob.AllEnums.AlterationType.DROP;
import static edu.uob.AllEnums.ResponseType.ERROR;
import static edu.uob.AllEnums.ResponseType.OK;

public class SQLParser {
    private static DBController dbController;
    private String response;
    private final Tokeniser tokeniser;
    private static final HashMap<String, SQLComparator> comparatorSymbols = new HashMap<>();
    private static final HashMap<String, BoolOperator> boolOperatorSymbols = new HashMap<>();

    SQLParser(String command, DBController dbController) {
        response = "";
        SQLParser.dbController = dbController;
        Utils.populateBoolOperatorMap(boolOperatorSymbols);
        Utils.populateComparatorMap(comparatorSymbols);
        this.tokeniser = new Tokeniser(command);
    }

    public String handleCommand() {// TODO check only one semi colon is present
        response = "";
        try{
            if(checkSemiColon()){
                try {
                    checkCommandType();
                    return Utils.generateResponse(OK, response);
                } catch (SQLQueryException e) {
                    return Utils.generateResponse(ERROR, e.toString());
                } catch (Exception e) {
                    return Utils.generateResponse(ERROR, e.getMessage());
                }
            }
            return Utils.generateResponse(ERROR, "error occurred because of number of semi colons");
        }
        catch (Exception e){
            return Utils.generateResponse(ERROR, e.getMessage());
        }
    }

    private boolean checkSemiColon() throws SQLQueryException {
        int countOfSemicolon = 0;
        int size = tokeniser.getSize();
        for(int index=size-1;index>=0;index--){
            String currentToken = tokeniser.get(index);
            if(currentToken.equals(";")) countOfSemicolon++;
            else break;
        }
        return (countOfSemicolon == 1);
    }

    private void checkCommandType() throws SQLQueryException, DBException {
        String commandToken = tokeniser.getCurrentToken();
        switch (commandToken.toUpperCase()) {
            case "SELECT" -> checkSelect();
            case "USE" -> checkUse();
            case "CREATE" -> checkCreate();
            case "DROP" -> checkDrop();
            case "ALTER" -> checkAlter();
            case "INSERT" -> checkInsert();
            case "DELETE" -> checkDelete();
            case "UPDATE" -> checkUpdate();
            case "JOIN" -> checkJoin();
            default -> throw new SQLQueryException("Illegal Query");
        }
    }

    private void checkJoin() throws SQLQueryException, DBException {
        int initialIndex = tokeniser.getPos();
        String currentToken = tokeniser.getCurrentToken();
        if (currentToken.equalsIgnoreCase("JOIN")) {
            try {
                tokeniser.next();
                String tableName1 = checkTableName().toLowerCase();
                String andToken = tokeniser.getCurrentToken();
                if (!andToken.equalsIgnoreCase("AND")) throw new ANDKeywordMissingException();
                tokeniser.next();
                String tableName2 = checkTableName().toLowerCase();
                String onToken = tokeniser.getCurrentToken();
                if(!onToken.equalsIgnoreCase("ON")) throw new ONKeywordMissingException();
                tokeniser.next();
                if (!onToken.equalsIgnoreCase("ON")) throw new ONKeywordMissingException();
                String attributeName1 = checkAttributeName();
                andToken = tokeniser.getCurrentToken();
                if (!andToken.equalsIgnoreCase("AND")) throw new ANDKeywordMissingException();
                tokeniser.next();
                String attributeName2 = checkAttributeName();
                this.response = dbController.joinTables(tableName1, tableName2, attributeName1, attributeName2);
            } catch (SQLQueryException e) {
                tokeniser.setPos(initialIndex);
                throw e;
            }
        }
    }

    private void checkDelete() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        currentToken = currentToken + " " + tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("DELETE FROM")) {
            String tableName = checkTableName().toLowerCase();
            String whereToken = tokeniser.getCurrentToken();
            if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
            tokeniser.next();
            List<Integer> valuesToDelete = checkCondition(tableName);
            dbController.deleteValuesFromTable(tableName, valuesToDelete);
        } else {
            tokeniser.previous();
            tokeniser.previous();
        }
    }

    private List<Integer> checkCondition(String tableName) throws SQLQueryException, DBException {
        Table table = dbController.getTable(tableName);
        int openingBrackets = 0;
        Stack<Object> stack = new Stack<>();
        int index = tokeniser.getPos();
        int size = tokeniser.getSize();
        while(index < size-1){
            String currentToken = tokeniser.getCurrentToken();
            if(currentToken.equals("(")) openingBrackets++;
            else if(currentToken.equals(")")){
                openingBrackets--;
                Condition condition =  getCondition(table, stack);
                stack.push(condition);
            }
            else if(openingBrackets == 0 && stack.size() == 3){
                Condition condition = getCondition(table, stack);
                stack.push(condition);
                stack.push(currentToken);
            }
            else{
                stack.push(currentToken);
            }

            index++;
            tokeniser.next();
        }
        if(openingBrackets != 0) throw new BracketMismatchException();
        if(stack.size() == 3){
            Condition condition = getCondition(table, stack);
            stack.push(condition);
        }
        if(stack.size() != 1) throw new SQLQueryException("Invalid Condition");
        try{
            Condition condition = (Condition) stack.peek();
            return condition.getResultValues();
        }
        catch(Exception e){throw new DBException("Could not solve Condition");}
    }

    private Condition getCondition(Table table, Stack<Object> stack) throws SQLQueryException, DBException{
        if(stack.size() < 3) throw new SQLQueryException("Invalid Condition Specified");
        Object three = stack.pop();
        Object two = stack.pop();
        Object one =  stack.pop();
        if(one instanceof String){
            String columnName = checkColumnName((String) one);
            SQLComparator sqlComparator = getSQLComparator((String) two);
            Value value = Utils.getValueLiteral((String) three);
            return new Condition(table, columnName, value, sqlComparator);
        }
        else if(one instanceof Condition first){
            BoolOperator boolOperator = getBoolOperator((String) two);
            Condition second = (Condition) three;
            return new Condition(table, first, second, boolOperator);
        }
        else{
            throw new DBException("Cannot Solve Condition");
        }
    }

    private BoolOperator getBoolOperator(String token) throws SQLQueryException{
        if (boolOperatorSymbols.containsKey(token.toUpperCase()))
            return boolOperatorSymbols.get(token.toUpperCase());
        throw new NotBoolOperatorException();
    }

    private SQLComparator getSQLComparator(String token) throws SQLQueryException{
        if (comparatorSymbols.containsKey(token.toUpperCase()))
            return comparatorSymbols.get(token.toUpperCase());
        throw new InvalidComparatorException();
    }

//    private SQLComparator checkComparator() throws SQLQueryException {
//        String currentToken = tokeniser.getCurrentToken();
//        tokeniser.next();
//        if (comparatorSymbols.containsKey(currentToken.toUpperCase()))
//            return comparatorSymbols.get(currentToken.toUpperCase());
//
//        tokeniser.previous();
//        throw new InvalidComparatorException();
//    }

//    private BoolOperator checkBoolOperator() throws SQLQueryException {
//        String currentToken = tokeniser.getCurrentToken();
//        tokeniser.next();
//        if (boolOperatorSymbols.containsKey(currentToken.toUpperCase()))
//            return boolOperatorSymbols.get(currentToken.toUpperCase());
//
//        tokeniser.previous();
//        throw new NotBoolOperatorException();
//    }

    private void checkUpdate() throws SQLQueryException, DBException {
        String updateToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (updateToken.equalsIgnoreCase("UPDATE")) {
            int initialPos = tokeniser.getPos();
            try {
                String tableName = checkTableName().toLowerCase();
                String setToken = tokeniser.getCurrentToken();
                tokeniser.next();
                if (!setToken.equalsIgnoreCase("SET")) {
                    tokeniser.previous();
                    throw new KeywordMissingException("SET");
                }
                List<NameValuePair> nameValuePairList = checkNameValueList();
                initialPos = tokeniser.getPos();
                String whereToken = tokeniser.getCurrentToken();
                if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
                tokeniser.next();
                List<Integer> resultSet = checkCondition(tableName);
                dbController.update(tableName, nameValuePairList, resultSet);
            } catch (SQLQueryException e) {
                tokeniser.setPos(initialPos);
                throw e;
            }
        } else {
            tokeniser.previous();
        }
    }

    private List<NameValuePair> checkNameValueList() throws SQLQueryException {
        List<NameValuePair> nameValueList = new ArrayList<>();
        NameValuePair nameValuePair = checkNameValue();
        nameValueList.add(nameValuePair);

        String comma = tokeniser.getCurrentToken();
        if (!comma.equals(",")) return nameValueList;
        tokeniser.next();
        try {
            nameValuePair = checkNameValue();
            while (nameValuePair != null) {
                nameValueList.add(nameValuePair);
                nameValuePair = checkNameValue();
            }
        } catch (SQLQueryException e) {
            tokeniser.previous();
            if (nameValueList.isEmpty()) throw new EmptyListException("NameValueList");
        }
        return nameValueList;
    }

    private NameValuePair checkNameValue() throws SQLQueryException {
        String attributeName = checkAttributeName();
        String equalToken = tokeniser.getCurrentToken();
        tokeniser.next();
        Value value;
        if (!equalToken.equals("=")) {
            tokeniser.previous();
            throw new EqualsMissingException();
        }
        value = Utils.getValueLiteral(tokeniser.getCurrentToken());
        tokeniser.next();
        return new NameValuePair(attributeName, value);
    }

    private void checkSelect() throws SQLQueryException, DBException {
        String selectToken = tokeniser.getCurrentToken();
        if (!selectToken.equalsIgnoreCase("SELECT")) throw new KeywordMissingException("SELECT");
        tokeniser.next();
        try {
            List<String> wildAttributes = checkWildAttributeList();
            String fromToken = tokeniser.getCurrentToken();
            if(!fromToken.equalsIgnoreCase("FROM")) throw new KeywordMissingException("FROM");
            tokeniser.next();
            String tableName = checkTableName().toLowerCase();
            if (tokeniser.getCurrentToken().equals(";")) {
                this.response = dbController.select(tableName, wildAttributes);
            } else {
                String whereToken = tokeniser.getCurrentToken();
                if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
                tokeniser.next();
                List<Integer> filteredValues = checkCondition(tableName);
                this.response = dbController.select(tableName, wildAttributes, filteredValues);
            }
        }catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    private List<String> checkWildAttributeList() throws SQLQueryException {
        String starToken = tokeniser.getCurrentToken();
        if (starToken.equals("*")) {
            tokeniser.next();
            return new ArrayList<>(List.of("*"));
        } else {
            return checkAttributeList();
        }
    }

    private void checkInsert() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        currentToken = currentToken + " " + tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("INSERT INTO")) {
            int initialIndex2 = tokeniser.getPos();
            try {
                String tableName = checkTableName().toLowerCase();
                String valuesToken = tokeniser.getCurrentToken();
                tokeniser.next();
                if (!valuesToken.equalsIgnoreCase("VALUES"))
                    throw new SQLQueryException("Illegal Query keyword VALUES not found");
                String openingBracket = tokeniser.getCurrentToken();
                tokeniser.next();
                String closingBracket = tokeniser.get(tokeniser.getSize() - 2);
                if (!openingBracket.equals("(") && !closingBracket.equals(")"))
                    throw new BracketMismatchException();
                List<Value> valueList = checkValueList();
                dbController.insertValues(tableName, valueList);
            } catch (SQLQueryException e) {
                tokeniser.setPos(initialIndex2);
                throw e;
            }
        } else {
            throw new DBException("Could not parse Query");
        }
    }

    private List<Value> checkValueList() throws SQLQueryException {
        List<Value> resultList = new ArrayList<>();
        int index = tokeniser.getPos();
        int initialIndex = index;
        try {
            resultList.add(getValue());
            while (!tokeniser.getCurrentToken().equals(")")) {
                String comma = tokeniser.getCurrentToken();
                if (comma.equals(")")) break; // TODO add case to check semicolon to not got into infinite loop
                if (!comma.equals(",")) throw new SQLQueryException("Values should be separated by ,(comma)");
                tokeniser.next();
                Value value = getValue();
                resultList.add(value);
                index++;
            }
        } catch (SQLQueryException e) {
            tokeniser.setPos(initialIndex);
            throw e;
        }

        if (resultList.size() == 0) throw new SQLQueryException("Value List cannot be empty");
        return resultList;
    }

    private Value getValue() throws SQLQueryException {
        try (Value val = checkFloatLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try (Value val = checkIntegerLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try (Value val = checkNullLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try (Value val = checkBooleanLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try (Value val = checkStringLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        tokeniser.previous();
        throw new IllegalValueTypeException();
    }

    private Value checkNullLiteral() throws Exception {
        String currentToken = tokeniser.getCurrentToken();
        return Utils.getNullLiteral(currentToken);
    }

    private Value checkIntegerLiteral() throws Exception {
        String currentToken = tokeniser.getCurrentToken();
        return Utils.getIntegerLiteral(currentToken);
    }

    private Value checkFloatLiteral() throws Exception {
        String currentToken = tokeniser.getCurrentToken();
        return Utils.getFloatLiteral(currentToken);
    }

    private Value checkBooleanLiteral() throws Exception {
        String currentToken = tokeniser.getCurrentToken();
        return Utils.getBooleanLiteral(currentToken);
    }

    private Value checkStringLiteral() throws Exception {
        String currentToken = tokeniser.getCurrentToken();
        return Utils.getStringLiteral(currentToken);
    }

    private void checkAlter() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("ALTER")) {
            try {
                tokeniser.next();
                String tableName = checkTableName().toLowerCase();
                AlterationType alterationType = checkAlterationType();
                String attributeName = checkAttributeName();
                dbController.alterTable(tableName, alterationType, attributeName);
            } catch (SQLQueryException e) {
                tokeniser.previous();
                tokeniser.previous();
                throw e;
            }
        } else {
            tokeniser.previous();
        }
    }

    private AlterationType checkAlterationType() throws SQLQueryException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        AlterationType result;
        if (currentToken.equalsIgnoreCase("ADD")) result = ADD;
        else if (currentToken.equalsIgnoreCase("DROP")) result = DROP;
        else {
            tokeniser.previous();
            throw new SQLQueryException("Invalid AlterationType specified");
        }
        return result;
    }

    private void checkDrop() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("DROP")) {
            String operationType = tokeniser.getCurrentToken();
            tokeniser.next();
            if (operationType.equalsIgnoreCase("DATABASE")) {
                String dbName = checkDatabaseName().toLowerCase();
                dbController.dropDB(dbName);
                return;
            } else if (operationType.equalsIgnoreCase("TABLE")) {
                String tableName = checkTableName().toLowerCase();
                dbController.dropTable(tableName);
                return;
            } else tokeniser.previous();
        }
        tokeniser.previous();
    }

    private String checkTableName() throws SQLQueryException {
        // same rules as DB name
        return checkDatabaseName();
    }

    private void checkCreate() throws SQLQueryException, DBException {
        if (!checkCreateTable()) {
            checkCreateDatabase();
        }
    }

    private boolean checkCreateTable() throws SQLQueryException, DBException {
        // TODO check for Bounds on tokens, if out of bounds raise and exception
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        String tokenAhead = tokeniser.getCurrentToken();
        tokeniser.next();
        boolean isValid = false;
        if (currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("TABLE")) {
            String tableName = checkTableName().toLowerCase();
            if (tokeniser.getPos() == tokeniser.getSize() - 1) { // if no attributes present
                dbController.createTable(tableName);
                return true;
            }
            String openingBracket = tokeniser.getCurrentToken();
            tokeniser.next();
            String closingBracket = tokeniser.get(tokeniser.getSize() - 2);
            if (openingBracket.equals("(") && closingBracket.equalsIgnoreCase(")")) {
                List<String> attbrList = checkAttributeList();
                isValid = true;
                dbController.createTable(tableName, attbrList);
            } else {
                tokeniser.previous();
                return false;
            }
            // TODO check for ")" closing bracket
        }
        tokeniser.previous();
        tokeniser.previous();
        return isValid;
    }

    private List<String> checkAttributeList() throws SQLQueryException {
        // TODO to be completed
        List<String> attributeList = new ArrayList<>();
        int initialIndex = tokeniser.getPos();
        String attributeName = checkAttributeName();
        attributeList.add(attributeName);
        String comma = tokeniser.getCurrentToken();
        if (!comma.equals(",")) return attributeList;
        tokeniser.next();
        try {
            attributeName = checkAttributeName();
            while (comma.equals(",")) {
                attributeList.add(attributeName);
                comma = tokeniser.getCurrentToken();
                if(!comma.equals(",")) break;
                tokeniser.next();
                attributeName = checkAttributeName();
            }
        } catch (SQLQueryException e) {
            tokeniser.previous();
            throw e;
        }
        if (attributeList.size() == 0) {
            tokeniser.setPos(initialIndex);
            throw new EmptyListException("Attribute List");
        }
        return attributeList;
    }

    private String checkAttributeName() throws SQLQueryException {
        return checkDatabaseName();
    }

    private void checkCreateDatabase() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        String tokenAhead = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("DATABASE")) {
            String dbName = checkDatabaseName().toLowerCase();
            dbController.createDB(dbName);
            return;
        }
        tokeniser.previous();
        tokeniser.previous();
        throw new SQLQueryException("Invalid Query");
    }

    private void checkUse() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("USE")) {
            String dbName = checkDatabaseName().toLowerCase();
            dbController.setActiveDB(dbName);
            return;
        }
        tokeniser.previous();
    }

    private String checkDatabaseName() throws SQLQueryException {
        // TODO to be completed
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(currentToken);
        if (!matcher.find()) {
            tokeniser.previous();
            throw new InvalidIdentifierNameException();
        }
        if (Utils.isNotKeyWord(currentToken)) {
            tokeniser.previous();
            throw new KeywordIdentifierException();
        }
        return currentToken;
    }

    private String checkColumnName(String name) throws SQLQueryException{
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.find()) {
            throw new InvalidIdentifierNameException();
        }
        if (Utils.isNotKeyWord(name)) {
            throw new KeywordIdentifierException();
        }
        return name;
    }
}
