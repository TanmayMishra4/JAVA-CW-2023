package edu.uob;

import edu.uob.DBExceptions.DBException;
import edu.uob.DBExceptions.IllegalValueTypeException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static edu.uob.ResponseType.ERROR;

public class Utils {
    private static final Set<String> keyWords = new HashSet<>(Arrays.asList("USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "SELECT", "UPDATE", "DELETE", "FROM", "JOIN", "ADD", "TRUE", "FALSE", "NULL", "AND", "OR"));
    private static final Set<Character> symbols = new HashSet<>(Arrays.asList('!' , '#' , '$' , '%' , '&' , '(' , ')' , '*' , '+' , ',' , '-' , '.' , '/' , ':' , ';' , '>' , '=' , '<' , '?' , '@' , '[' , '\\' , ']' , '^' , '_' , '`' , '{' , '}' , '~'));
    static String generateResponse(ResponseType type, String message){
        if(type == ERROR){
            return "[ERROR]: " + message;
        }
        else{
            return "[OK]: " + message;
        }
    }
    static boolean isNotKeyWord(String word){
        if(keyWords.contains(word.toUpperCase())) return true;
        return false;
    }

    public static boolean isSymbol(char charVal) {
        return symbols.contains(charVal);
    }

    public static ValueLiteral getValueLiteral(String token) throws IllegalValueTypeException {
        try(ValueLiteral<String> val = getStringLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(ValueLiteral<Boolean> val = getBooleanLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(ValueLiteral<Double> val = getFloatLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(ValueLiteral<Integer> val = getIntegerLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        try(ValueLiteral<NULLObject> val = getNullLiteral(token)){
            return val;
        } catch (Exception ignored) {}

        throw new IllegalValueTypeException();
    }

    public static ValueLiteral<Integer> getIntegerLiteral(String token) throws Exception{
        Integer val = null;
        val = Integer.parseInt(token);
        return new ValueLiteral<Integer>(val);
    }
    public static ValueLiteral<Double> getFloatLiteral(String token) throws Exception{
        Double val = null;
        val = Double.parseDouble(token);
        return new ValueLiteral<Double>(val);
    }

    public static ValueLiteral<Boolean> getBooleanLiteral(String token) throws Exception{
        Boolean val = null;
        if(token.equalsIgnoreCase("TRUE")) val = Boolean.valueOf("true");
        else if(token.equalsIgnoreCase("FALSE")) val = Boolean.valueOf("false");
        else{
            throw new Exception("Not a Boolean Literal");
        }
        return new ValueLiteral<Boolean>(val);
    }

    public static ValueLiteral<String> getStringLiteral(String token) throws Exception{
        int stringLength = token.length();
        char closingBracket = token.charAt(stringLength - 1);
        char openingBracket = token.charAt(0);
        String stringVal = null;
        if(openingBracket == '\'' && closingBracket ==  '\''){
            stringVal = token.substring(1, stringLength - 1);
            for(char charVal : stringVal.toCharArray()){
                if(Character.isDigit(charVal) || Character.isLetter(charVal) || charVal == ' ' || Utils.isSymbol(charVal)) continue;
                else throw new DBException("String contains Illegal characters");
            }
        }
        else throw new Exception("Not a String Literal");
        return new ValueLiteral<String>(stringVal);
    }

    public static ValueLiteral<NULLObject> getNullLiteral(String token) throws Exception{
        if(!token.equalsIgnoreCase("NULL")) {
            throw new Exception("Not NULL Value in NULL Object");
        }
        return new ValueLiteral<>(new NULLObject());
    }
}
