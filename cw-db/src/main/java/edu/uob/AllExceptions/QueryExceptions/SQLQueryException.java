package edu.uob.AllExceptions.QueryExceptions;

import java.io.Serial;

public class SQLQueryException extends Exception{
    @Serial
    private static final long serialVersionUID = 62389982;
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
