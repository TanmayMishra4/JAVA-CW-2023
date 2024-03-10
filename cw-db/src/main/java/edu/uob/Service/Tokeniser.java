package edu.uob.Service;

import edu.uob.AllEnums.SQLComparator;
import edu.uob.DBExceptions.DBException;
import edu.uob.DBExceptions.TokeniserOutOfBoundsException;
import edu.uob.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
// TODO check for semicolons and tab characters

public class Tokeniser {
	int pos;
	int size;

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getSize() {
		return size;
	}

	String[] specialCharacters = {"(", ")", ",", ";", "==", ">", "<", ">=", "<=", "!="};
	ArrayList<String> tokens;

	public String getCurrentToken() throws DBException{
		try {
			return tokens.get(pos);
		}
		catch(Exception e){
			throw new TokeniserOutOfBoundsException();
		}
	}

	public String get(int index) throws DBException{
		try {
			return tokens.get(index);
		}
		catch(Exception e){
			throw new TokeniserOutOfBoundsException();
		}
	}
	public void next(){
		setPos(pos+1);
	}
	public void previous(){
		setPos(pos-1);
	}
	public String getLastToken(){
		return tokens.get(size-1);
	}

	public Tokeniser(String query) {
		pos = 0;
		tokens = new ArrayList<String>();
		query = query.trim();
		// Split the query on single quotes (to separate out query characters from string literals)
		String[] fragments = query.split("'");
		for (int i=0; i<fragments.length; i++) {
			// Every odd fragment is a string literal, so just append it without any alterations
			if (i%2 != 0) tokens.add("'" + fragments[i] + "'");
				// If it's not a string literal, it must be query characters (which need further processing)
			else {
				// Tokenise the fragments into an array of strings
				String[] nextBatchOfTokens = tokenise(fragments[i]);
				// Then add these to the "result" array list (needs a bit of conversion)
				tokens.addAll(Arrays.asList(nextBatchOfTokens));
			}
		}
		size = tokens.size();
	}

	String[] tokenise(String input) {
		// TODO add checks for dealing with marks>60
		// Add in some extra padding spaces around the "special characters"
		// so we can be sure that they are separated by AT LEAST one space (possibly more)
		for(int i=0; i<specialCharacters.length ;i++) {
			input = input.replace(specialCharacters[i], " " + specialCharacters[i] + " ");
		}
		// Remove all double spaces (the previous replacements may had added some)
		// This is "blind" replacement - replacing if they exist, doing nothing if they don't
		while (input.contains("  ")) input = input.replaceAll("  ", " ");
		// Again, remove any whitespace from the beginning and end that might have been introduced
		input = input.trim();
//		if(hasSymbol(input)){
//
//		}
		// Finally split on the space char (since there will now ALWAYS be a space between tokens)
		return input.split(" ");
	}

//	private boolean hasSymbol(String input) {
//		HashMap<String, SQLComparator> comparatorMap = new HashMap<>();
//		Utils.populateComparatorMap(comparatorMap);
//		StringBuilder sb = new StringBuilder();
//		for(int index=0;index<input.length()-1;index++){
//			String comparator = input.substring(index, index+2);
//			if(comparatorMap.containsKey(comparator)){
//				sb.append();
//			}
//		}
//	}

}
