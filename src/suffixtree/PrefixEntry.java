package suffixtree;

import java.util.ArrayList;

/* This is a bean class for combining a word with it's suffixes */
class PrefixEntry {
	private final String word ;								// Stores the base word
	private ArrayList<String> suffixes ;					// Stores the suffixes for the word
	
	// Default access constructor for package only access
	PrefixEntry(String word, ArrayList<String> suffixes) {
		this.word = word ;
		this.suffixes = suffixes ;
	}
	
	// Getter for the word
	String getWord() {
		return word;
	}

	// Getter for the suffix array
	ArrayList<String> getSuffixes() {
		return suffixes;
	}
}