package edu.uob;

import edu.uob.AllEnums.AlterationType;
import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.Controller.DBController;
import edu.uob.DBExceptions.*;
import edu.uob.Model.NameValuePair;
import edu.uob.Model.Value;
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
    // TODO do tokenizer.previous only on false return
    private DBController dbController;
    private String res;
    private Tokeniser tokeniser;
    private static HashMap<String, SQLComparator> comparatorSymbols = new HashMap<>();
    private static HashMap<String, BoolOperator> boolOperatorSymbols = new HashMap<>();

    SQLParser(String command, DBController dbController) {
        res = "";
        Utils.populateBoolOperatorMap(boolOperatorSymbols);
        Utils.populateComparatorMap(comparatorSymbols);
        this.dbController = dbController;
        this.tokeniser = new Tokeniser(command);
        this.dbController = new DBController();
    }

    public String handleCommand() {// TODO check only one semi colon is present
        res = "";
        String lastToken = tokeniser.getLastToken();
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
        if (!checkUse() && !checkCreate() && !checkDrop() && !checkAlter() && !checkInsert()
                && !checkSelect() && !checkDelete() && !checkUpdate() && !checkJoin()) {
            throw new DBException("Illegal Query");
        }
    }

    private boolean checkJoin() throws DBException {
        int initialIndex = tokeniser.getPos();
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("JOIN")) {
            try {
                String tableName1 = checkTableName();
                String andToken = tokeniser.getCurrentToken();
                tokeniser.next();
                String tableName2 = null;
                if (andToken.equalsIgnoreCase("AND")) tableName2 = checkTableName();
                else {
                    tokeniser.previous();
                    throw new ANDKeywordMissingException();
                }
                String onToken = tokeniser.getCurrentToken();
                tokeniser.next();
                String attributeName1 = null, attributeName2 = null;
                if (onToken.equalsIgnoreCase("ON")) attributeName1 = checkAttributeName();
                else {
                    tokeniser.previous();
                    tokeniser.previous();
                    throw new ONKeywordMissingException();
                }
                andToken = tokeniser.getCurrentToken();
                tokeniser.next();
                if (andToken.equalsIgnoreCase("AND")) attributeName2 = checkAttributeName();
                else {
                    tokeniser.previous();
                    tokeniser.previous();
                    tokeniser.previous();
                    throw new ANDKeywordMissingException();
                }
                dbController.joinTables(tableName1, tableName2, attributeName1, attributeName2);
            } catch (DBException e) {
                tokeniser.setPos(initialIndex);
                throw e;
            }
        } else {
            tokeniser.previous();
            return false;
        }
        return true;
    }

    private boolean checkDelete() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        currentToken = currentToken + " " + tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("DELETE FROM")) {
            String tableName = checkTableName();
            String whereToken = tokeniser.getCurrentToken();
            if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
            tokeniser.next();
            HashSet<Value> valuesToDelete = checkCondition();
            dbController.deleteValuesFromTable(tableName, valuesToDelete);
            return true;
        } else {
            tokeniser.previous();
            tokeniser.previous();
            return false;
        }
    }

    private HashSet<Value> checkCondition() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        int initialPos = tokeniser.getPos();
        tokeniser.next();
        if (currentToken.equals("(")) {
            int initialPos1 = tokeniser.getPos();
            try { // Case : "(" [AttributeName] <Comparator> [Value] ")"
                String attributeName = checkAttributeName();
                SQLComparator sqlComparator = checkComparator();
                Value value = Utils.getValueLiteral(tokeniser.getCurrentToken());
                String closingBracket = tokeniser.get(tokeniser.getSize() - 2);
                if (!closingBracket.equals(")")) throw new BracketMismatchException();
                tokeniser.next();
                return dbController.filter(attributeName, sqlComparator, value);
            } catch (DBException ignored) {
                tokeniser.setPos(initialPos1);
            }
            try { // Case : "(" <Condition> <BoolOperator> <Condition> ")"
                HashSet<Value> condition1Values = checkCondition();
                BoolOperator boolOperator = checkBoolOperator();
                HashSet<Value> condition2Values = checkCondition();
                String closingBracket = tokeniser.getCurrentToken();
                if (!closingBracket.equals(")")) throw new BracketMismatchException();
                tokeniser.next();
                return dbController.filter(condition1Values, boolOperator, condition2Values);
            } catch (DBException ignored) {
                tokeniser.setPos(initialPos1);
            }
        } else {
            tokeniser.previous();
            int initialPos2 = tokeniser.getPos();
            try { // Case : [AttributeName] <Comparator> [Value]
                String attributeName = checkAttributeName();
                SQLComparator sqlComparator = checkComparator();
                Value value = Utils.getValueLiteral(tokeniser.getCurrentToken());
                tokeniser.next();
                return dbController.filter(attributeName, sqlComparator, value);// TODO put this in Where CMD class
            }catch(InvalidIdentifierNameException e){
                throw e;
            }
            catch (DBException ignored) {
                tokeniser.setPos(initialPos);
            }
            try { // Case : <Condition> <BoolOperator> <Condition>
                HashSet<Value> condition1Values = checkCondition();
                BoolOperator boolOperator = checkBoolOperator();
                HashSet<Value> condition2Values = checkCondition();
                return dbController.filter(condition1Values, boolOperator, condition2Values);
            } catch (DBException ignored) {
                tokeniser.setPos(initialPos2);
            }
        }
        tokeniser.setPos(initialPos);
        throw new DBException("Could not Parse Conditions");
    }

    private SQLComparator checkComparator() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (comparatorSymbols.containsKey(currentToken.toUpperCase()))
            return comparatorSymbols.get(currentToken.toUpperCase());

        tokeniser.previous();
        throw new InvalidComparatorException();
    }

    private BoolOperator checkBoolOperator() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (boolOperatorSymbols.containsKey(currentToken.toUpperCase()))
            return boolOperatorSymbols.get(currentToken.toUpperCase());

        tokeniser.previous();
        throw new NotBoolOperatorException();
    }

    private boolean checkUpdate() throws DBException {
        String updateToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (updateToken.equalsIgnoreCase("UPDATE")) {
            int initialPos = tokeniser.getPos();
            try {
                String tableName = checkTableName();
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
                HashSet<Value> resultSet = checkCondition();
                dbController.update(tableName, nameValuePairList, resultSet);
            } catch (DBException e) {
                tokeniser.setPos(initialPos);
                throw e;
            }
        } else {
            tokeniser.previous();
            return false;
        }
        return true;
    }

    private List<NameValuePair> checkNameValueList() throws DBException {
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
        } catch (DBException e) {
            tokeniser.previous();
            if (nameValueList.isEmpty()) throw new EmptyListException("NameValueList");
        }
        return nameValueList;
    }

    private NameValuePair checkNameValue() throws DBException {
        String attributeName = checkAttributeName();
        String equalToken = tokeniser.getCurrentToken();
        tokeniser.next();
        Value value = null;
        if (!equalToken.equals("=")) {
            tokeniser.previous();
            throw new EqualsMissingException();
        }
        value = Utils.getValueLiteral(tokeniser.getCurrentToken());
        tokeniser.next();
        return new NameValuePair(attributeName, value);
    }

    private boolean checkSelect() throws DBException {
        String selectToken = tokeniser.getCurrentToken();
        if (!selectToken.equalsIgnoreCase("SELECT")) return false;
        tokeniser.next();
        try {
            List<String> wildAttributes = checkWildAttributeList();
            String fromToken = tokeniser.getCurrentToken();
            tokeniser.next();
            String tableName = checkTableName();
            if (tokeniser.getCurrentToken().equals(";")) {
                dbController.select(tableName, wildAttributes);
            } else {
                String whereToken = tokeniser.getCurrentToken();
                if (!whereToken.equalsIgnoreCase("WHERE")) throw new WhereKeywordMissingException();
                tokeniser.next();
                HashSet<Value> filteredValues = checkCondition();
                dbController.select(tableName, wildAttributes, filteredValues);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<String> checkWildAttributeList() throws DBException {
        try {
            List<String> attributeList = checkAttributeList();
            return attributeList;
        } catch (DBException ignored) {
            String starToken = tokeniser.getCurrentToken();
            if (!starToken.equals("*")) throw new DBException("Illegal Query");
            tokeniser.next();
            return new ArrayList<>(List.of("*"));
        }
    }

    private boolean checkInsert() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        int initialIndex = tokeniser.getPos();
        tokeniser.next();
        currentToken = currentToken + " " + tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("INSERT INTO")) {
            int initialIndex2 = tokeniser.getPos();
            try {
                String tableName = checkTableName();
                String valuesToken = tokeniser.getCurrentToken();
                tokeniser.next();
                if (!valuesToken.equalsIgnoreCase("VALUES"))
                    throw new DBException("Illegal Query keyword VALUES not found");
                String openingBracket = tokeniser.getCurrentToken();
                tokeniser.next();
                String closingBracket = tokeniser.get(tokeniser.getSize() - 2);
                if (!openingBracket.equals("(") && !closingBracket.equals(")"))
                    throw new BracketMismatchException();
                List<Value> valueList = checkValueList();
                dbController.insertValues(tableName, valueList);
            } catch (DBException e) {
                tokeniser.setPos(initialIndex2);
                throw e;
            }
        } else {
            tokeniser.setPos(initialIndex);
            return false;
        }
        return true;
    }

    private List<Value> checkValueList() throws DBException {
        List<Value> resultList = new ArrayList<>();
        int index = tokeniser.getPos();
        int initialIndex = index;
        try {
            while (!tokeniser.getCurrentToken().equals(")")) {
                Value value = getValue();
                resultList.add(value);
                String comma = tokeniser.getCurrentToken();
                tokeniser.next();
                if (comma.equals(")")) break;
                if (!comma.equals(",")) throw new DBException("Values should be separated by ,(comma)");
                index++;
            }
        } catch (DBException e) {
            tokeniser.setPos(initialIndex);
            throw e;
        }

        if (resultList.size() == 0) throw new DBException("Value List cannot be empty");
        return resultList;
    }

    private Value getValue() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        try (Value val = checkStringLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {
        }

        try (Value val = checkBooleanLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {
        }

        try (Value val = checkFloatLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {
        }

        try (Value val = checkIntegerLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {
        }

        try (Value val = checkNullLiteral()) {
            tokeniser.next();
            return val;
        } catch (Exception ignored) {
        }

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

    private boolean checkAlter() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("ALTER")) {
            try {
                String tableToken = tokeniser.getCurrentToken();
                tokeniser.next();
                String tableName = checkTableName();
                AlterationType alterationType = checkAlterationType();
                String attributeName = checkAttributeName();
                dbController.alterTable(tableName, alterationType);
            } catch (DBException e) {
                tokeniser.previous();
                tokeniser.previous();
                throw e;
            }
        } else {
            tokeniser.previous();
            return false;
        }
        return true;
    }

    private AlterationType checkAlterationType() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        AlterationType result;
        if (currentToken.equalsIgnoreCase("ADD")) result = ADD;
        else if (currentToken.equalsIgnoreCase("DROP")) result = DROP;
        else {
            tokeniser.previous();
            throw new DBException("Invalid AlterationType specified");
        }
        return result;
    }

    private boolean checkDrop() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("DROP")) {
            String operationType = tokeniser.getCurrentToken();
            tokeniser.next();
            if (operationType.equalsIgnoreCase("DATABASE")) {
                String dbName = checkDatabaseName();
                dbController.deleteDB(dbName);
                return true;
            } else if (operationType.equalsIgnoreCase("TABLE")) {
                String tableName = checkTableName();
                dbController.deleteTable(tableName);
                return true;
            } else tokeniser.previous();
        }
        tokeniser.previous();
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
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        String tokenAhead = tokeniser.getCurrentToken();
        tokeniser.next();
        boolean isValid = false;
        if (currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("TABLE")) {
            String tableName = checkTableName();
            if (tokeniser.getPos() == tokeniser.getSize() - 1) { // if no attributes present
//                tokenizer.previous();
//                tokenizer.previous();
                dbController.createTable();
                return true;
            }
//            tokenizer.next();
            String openingBracket = tokeniser.getCurrentToken();
            tokeniser.next();
            String closingBracket = tokeniser.get(tokeniser.getSize() - 2);
            if (openingBracket.equals("(") && closingBracket.equalsIgnoreCase(")")) {
                List<String> attbrList = checkAttributeList();
                isValid = true;
                dbController.createTable(attbrList);
            } else {
//                tokenizer.previous();
                tokeniser.previous();
                return false;
            }
            // TODO check for ")" closing bracket
        }
        tokeniser.previous();
        tokeniser.previous();
        return isValid;
    }

    private List<String> checkAttributeList() throws DBException {
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
            while(comma.equals(",")){
                attributeList.add(attributeName);
                tokeniser.next();
                comma = tokeniser.getCurrentToken();
                attributeName = checkAttributeName();
            }
        }
        catch(DBException e){
            tokeniser.previous();
            throw e;
        }
        if (attributeList.size() == 0) {
            tokeniser.setPos(initialIndex);
            throw new EmptyListException("Attribute List");
        }
        return attributeList;
    }

    private String checkAttributeName() throws DBException {
        return checkDatabaseName();
    }

    private boolean checkCreateDatabase() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        String tokenAhead = tokeniser.getCurrentToken();
        tokeniser.next();
        if (currentToken.equalsIgnoreCase("CREATE") && tokenAhead.equalsIgnoreCase("DATABASE")) {
            String dbName = checkDatabaseName();
            dbController.createDB(dbName);
            return true;
        }
        tokeniser.previous();
        tokeniser.previous();
        return false;
    }

    private boolean checkUse() throws DBException {
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        boolean isValid = false;
        if (currentToken.equalsIgnoreCase("USE")) {
            String dbName = checkDatabaseName();
            dbController.setActiveDB(dbName);
            return true;
        }
        tokeniser.previous();
        return false;
    }

    private String checkDatabaseName() throws DBException {
        // TODO to be completed
        String currentToken = tokeniser.getCurrentToken();
        tokeniser.next();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{1,}$");
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
}
