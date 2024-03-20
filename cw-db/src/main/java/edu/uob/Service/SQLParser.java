package edu.uob.Service;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.QueryExceptions.*;
import edu.uob.Controller.DBController;
import edu.uob.Model.*;
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

    public SQLParser(String command, DBController dbController) {
        response = "";
        SQLParser.dbController = dbController;
        Utils.populateBoolOperatorMap(boolOperatorSymbols);
        Utils.populateComparatorMap(comparatorSymbols);
        this.tokeniser = new Tokeniser(command);
    }

    public String handleCommand() {
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
        String currentToken = tokeniser.getCurrentToken();
        if (currentToken.equalsIgnoreCase("JOIN")) {
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
        }
        else throw new KeywordMissingException("JOIN");
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
            Condition condition = checkCondition(tableName);
            List<Integer> valuesToDelete = condition.getResultValues();
            dbController.deleteValuesFromTable(tableName, valuesToDelete);
        }
        else throw new KeywordMissingException("DELETE or FROM");
    }

    private Condition checkCondition(String tableName) throws SQLQueryException, DBException {
        Table table = dbController.getTable(tableName);
        int openingBrackets = 0;
        Queue<String> queue = new LinkedList<>();
        int index = tokeniser.getPos();
        int size = tokeniser.getSize();
        while(index < size-1){
            String currentToken = tokeniser.getCurrentToken();
            if(currentToken.equals("(")) openingBrackets++;
            else if(currentToken.equals(")")){
                if(openingBrackets == 0) throw new BracketMismatchException();
                openingBrackets--;
            }
            else queue.add(currentToken);
            index++;
            tokeniser.next();
        }
        if(openingBrackets != 0) throw new BracketMismatchException();
        if(queue.size() < 3) throw new SQLQueryException("Invalid Condition");
        return checkConditionRecursive(table, queue);
    }

    private Condition checkConditionRecursive(Table table, Queue<String> queue) throws SQLQueryException, DBException{
        if(queue.size() < 3) throw new SQLQueryException("Cannot solve condition");
        Condition condition = getCondition(table, queue);
        if(queue.isEmpty()) return condition;
        BoolOperator boolOperator = getBoolOperator(queue.poll());
        return new Condition(table, condition, checkConditionRecursive(table, queue), boolOperator);
    }

    private Condition getCondition(Table table, Queue<String> queue) throws SQLQueryException, DBException{
        if(queue.size() < 3) throw new SQLQueryException("Invalid Condition Specified");
        String one = queue.poll();
        String two = queue.poll();
        String three =  queue.poll();

        String columnName = checkColumnName(one);
        SQLComparator sqlComparator = getSQLComparator(two);
        Value value = Utils.getValueLiteral(three);
        return new Condition(table, columnName, value, sqlComparator);
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

    private void checkUpdate() throws SQLQueryException, DBException {
        String updateToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (updateToken.equalsIgnoreCase("UPDATE")) {
            String tableName = checkTableName().toLowerCase();
            String setToken = tokeniser.getCurrentToken();
            tokeniser.next();
            if (!setToken.equalsIgnoreCase("SET")) {
                tokeniser.previous();
                throw new KeywordMissingException("SET");
            }
            List<NameValuePair> nameValuePairList = checkNameValueList();
            String whereToken = tokeniser.getCurrentToken();
            if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
            tokeniser.next();
            Condition condition = checkCondition(tableName);
            List<Integer> resultSet = condition.getResultValues();
            dbController.update(tableName, nameValuePairList, resultSet);
        } else throw new KeywordMissingException("UPDATE");
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
            if (nameValueList.isEmpty()) throw new EmptyListException("NameValueList");
        }
        return nameValueList;
    }

    private NameValuePair checkNameValue() throws SQLQueryException {
        String attributeName = checkAttributeName();
        String equalToken = tokeniser.getCurrentToken();
        tokeniser.next();
        Value value;
        if (!equalToken.equals("=")) throw new EqualsMissingException();

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
                Condition condition = checkCondition(tableName);
                List<Integer> filteredValues = condition.getResultValues();
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
        } else return checkAttributeList();
    }

    private void checkInsert() throws SQLQueryException, DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        currentToken = currentToken + " " + tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("INSERT INTO")) {
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
        } else throw new KeywordMissingException("INSERT INTO");
    }

    private List<Value> checkValueList() throws SQLQueryException {
        List<Value> resultList = new ArrayList<>();
        int index = tokeniser.getPos();
        int initialIndex = index;
        try {
            resultList.add(getValue());
            while (!tokeniser.getCurrentToken().equals(")")) {
                String comma = tokeniser.getCurrentToken();
                if(comma.equals(";")) throw new SQLQueryException("Invalid Query");
                if (comma.equals(")")) break;
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
        Value val;
        try {
            val = checkIntegerLiteral();
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try {
            val = checkFloatLiteral();
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try {
            val = checkNullLiteral();
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try {
            val = checkBooleanLiteral();
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

        try {
            val = checkStringLiteral();
            tokeniser.next();
            return val;
        } catch (Exception ignored) {}

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
            tokeniser.next();
            String tableName = checkTableName().toLowerCase();
            AlterationType alterationType = checkAlterationType();
            String attributeName = checkAttributeName();
            dbController.alterTable(tableName, alterationType, attributeName);
        }
        else throw new KeywordMissingException("ALTER");
    }

    private AlterationType checkAlterationType() throws SQLQueryException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        AlterationType result;
        if (currentToken.equalsIgnoreCase("ADD")) result = ADD;
        else if (currentToken.equalsIgnoreCase("DROP")) result = DROP;
        else throw new SQLQueryException("Invalid AlterationType specified");

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
            } else if (operationType.equalsIgnoreCase("TABLE")) {
                String tableName = checkTableName().toLowerCase();
                dbController.dropTable(tableName);
            } else throw new SQLQueryException("Invalid DROP command, should include either DATABASE or TABLE");
        }
        else throw new KeywordMissingException("DROP");
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
        }
        tokeniser.previous();
        tokeniser.previous();
        return isValid;
    }

    private List<String> checkAttributeList() throws SQLQueryException {
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
        }
        else throw new KeywordMissingException("USE");
    }

    private String checkDatabaseName() throws SQLQueryException {
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
