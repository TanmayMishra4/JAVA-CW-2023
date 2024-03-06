package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private int position;
    private List<String> tokenArray;
    Token(String command){
        position = 0;
        tokenArray = new ArrayList<>();
        tokenize(command);
    }

    private void tokenize(String command){
        // TODO write logic to fill tokenArray using tokenizer
    }
    public boolean hasNext(){
        // TODO to be completed
        return position < tokenArray.size();
    }

    public String next(){
        // TODO to be completed
        return "";
    }

    public String currentToken(){
        return tokenArray.get(position);
    }

    public String getFirstToken(){
        return tokenArray.get(0);
    }

    public String getLastToken(){
        return tokenArray.get(tokenArray.size()-1);
    }

    public void increaseIterator(){
        position = Math.min(position+1, tokenArray.size()-1);
    }

    public void decreaseIterator(){
        position = Math.max(0, position-1);
    }

}
