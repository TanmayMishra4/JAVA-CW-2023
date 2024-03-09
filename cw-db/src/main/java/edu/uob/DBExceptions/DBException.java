package edu.uob.DBExceptions;

public class DBException extends Exception{
    String message;
    DBException(){
        super();
        message = new String();
    }

    public DBException(String message){
        super(message);
        this.message = message;
    }

    public String toString(){
        return message;
    }

}
