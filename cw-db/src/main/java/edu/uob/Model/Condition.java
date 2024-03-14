package edu.uob.Model;

import edu.uob.AllEnums.BoolOperator;
import edu.uob.AllEnums.ConditionType;
import edu.uob.AllEnums.SQLComparator;
import edu.uob.AllExceptions.DBExceptions.ColumnNotFoundException;
import edu.uob.AllExceptions.DBExceptions.DBException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Condition {
    Condition firstCondition;
    Table table;
    Condition secondCondition;
    BoolOperator boolOperator;

    public Value getValue() {
        return value;
    }

    public List<Integer> getResultValues() {
        return resultValues;
    }

    public Condition(Table table, Condition firstCondition, Condition secondCondition, BoolOperator boolOperator) {
        this.firstCondition = firstCondition;
        this.secondCondition = secondCondition;
        this.boolOperator = boolOperator;
        conditionType = ConditionType.BOOL_OPERATOR;
        this.table = table;
        resultValues = new ArrayList<>();
        performBoolOperatorOperation();
    }

    private void performSQLOperatorOperation() throws DBException {
        if(!table.hasColumn(attributeName)) throw new ColumnNotFoundException();
        Column column = table.getColumn(attributeName);
        for(var entry : column.getValues().entrySet()){
            Value val = entry.getValue();
            Integer primaryKey =  entry.getKey();
            if(val.compareFunc(sqlComparator, value)) resultValues.add(primaryKey);
        }
        resultValues.sort(Integer::compareTo);
    }

    public Condition(Table table, String attributeName, Value value, SQLComparator sqlComparator) throws DBException {
        this.attributeName = attributeName;
        this.value = value;
        this.sqlComparator = sqlComparator;
        conditionType = ConditionType.SQL_COMPARATOR;
        this.table = table;
        resultValues = new ArrayList<>();
        performSQLOperatorOperation();
    }

    private void performBoolOperatorOperation() {
        HashSet<Integer> result1 = new HashSet<>(firstCondition.getResultValues());
        HashSet<Integer> result2 = new HashSet<>(secondCondition.getResultValues());
        if(boolOperator == BoolOperator.AND){
            for(int val : result1){
                if(result2.contains(val)) resultValues.add(val);
            }
            resultValues.sort(Integer::compareTo);
        }
        else{
            result1.addAll(result2);
            resultValues = new ArrayList<>(result1);
            resultValues.sort(Integer::compareTo);
        }
    }

    String attributeName;
    Value value;
    SQLComparator sqlComparator;

    List<Integer> resultValues;

    ConditionType conditionType;
}
