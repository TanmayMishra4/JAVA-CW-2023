package edu.uob.AllExceptions.QueryExceptions;

public class SQLQueryException extends Exception{
    String message;
    SQLQueryException(){
        super();
        message = new String();
    }

    public SQLQueryException(String message){
        super(message);
        this.message = message;
    }

    public String toString(){
        return message;
    }

}
