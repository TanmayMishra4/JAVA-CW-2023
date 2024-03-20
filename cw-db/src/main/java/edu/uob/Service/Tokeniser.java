package edu.uob.Service;

import edu.uob.AllExceptions.DBExceptions.DBException;
import edu.uob.AllExceptions.QueryExceptions.SQLQueryException;
import edu.uob.AllExceptions.QueryExceptions.TokeniserOutOfBoundsException;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokeniser {
	int pos;
	int size;

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) throws SQLQueryException {
		if(this.pos >= tokens.size()) throw new TokeniserOutOfBoundsException();
		this.pos = pos;
	}

	public int getSize() {
		return size;
	}

	String[] specialCharacters = {"(", ")", ",", ";", "==", ">", "<", ">=", "<=", "!="};
	ArrayList<String> tokens;

	public String getCurrentToken() throws SQLQueryException {
		try {
			return tokens.get(pos);
		}
		catch(Exception e){
			throw new TokeniserOutOfBoundsException();
		}
	}

	public String get(int index) throws SQLQueryException {
		try {
			return tokens.get(index);
		}
		catch(Exception e){
			throw new TokeniserOutOfBoundsException();
		}
	}
	public void next() throws SQLQueryException{
		setPos(pos+1);
	}
	public void previous() throws SQLQueryException{
		setPos(pos-1);
	}
	public String getLastToken(){
		return tokens.get(size-1);
	}

	public Tokeniser(String query) {
		pos = 0;
		tokens = new ArrayList<>();
		query = query.trim();
		String[] fragments = query.split("'");
		for (int index=0; index<fragments.length; index++) {
			if (index%2 != 0) tokens.add("'" + fragments[index] + "'");
			else {
				String[] nextBatchOfTokens = tokenise(fragments[index]);
				tokens.addAll(Arrays.asList(nextBatchOfTokens));
			}
		}
		size = tokens.size();
	}

	String[] tokenise(String input) {
		for(int index=0; index<specialCharacters.length ;index++) {
			input = input.replace(specialCharacters[index], " " + specialCharacters[index] + " ");
		}
		while (input.contains("  ")) input = input.replaceAll("  ", " ");
		input = input.trim();
		return input.split(" ");
	}
}
