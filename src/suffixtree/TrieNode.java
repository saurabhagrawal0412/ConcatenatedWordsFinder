package suffixtree;

import java.util.ArrayList;
import java.util.HashMap;

/* This class contains the required feature and methods for storing Trie
 * Stores the character at the node */
class TrieNode {

	@SuppressWarnings("unused")
	private final char letter ;								// The node character
	private HashMap<Character, TrieNode> children ;			// Map of children for constant time access
	private boolean isWord ;								// Denotes whether the node represents a word or not
	private static int trieBytes = 0 ;						// Only used for calculating the size of the trie
	
	// Default access constructor for package only access
	TrieNode(char ch) {
		this.letter = ch ;
	}
	
	// Wrapper method for adding a word. Calls the recursive insertWord() method
	void insertWord(String word) {
		StringBuffer buf = new StringBuffer(word) ;
		insertWord(buf) ;
	}
	
	/* This recursive method creates the required nodes in the Trie to insert a word */
	private void insertWord(StringBuffer word) {
		// Base condition: If the word becomes empty, mark the node as a complete word and return
		if(word == null || word.length() <= 0) {
			isWord = true ;
			return ;
		}
		
		if(children == null) {
			children = new HashMap<>() ;
		}
		
		char curr = word.charAt(0) ;						// The zeroth character will be used to find the child
		word.deleteCharAt(0) ;								// The remaining string will be passed to the child
		
		TrieNode child ;
		
		if(children.containsKey(curr)) {					// If the path already exists
			child = children.get(curr) ;
		}
		else {												// If the path does not exist
			child = new TrieNode(curr) ;					// Creating a new node
			children.put(curr, child) ;
		}
		// Calling the child's insert word method and passing the remaining string
		child.insertWord(word) ;
	}
	
	/* Wrapper method for getting the suffix list. Executes after Trie building */
	ArrayList<String> getSuffixes(String word) {
		ArrayList<String> suffixes = new ArrayList<>() ;
		char curr = word.charAt(0) ;
		TrieNode child = children.get(curr) ;				// The child node will be present in the trie
		child.getSuffixes(word, suffixes) ;					// Calling the recursive getSuffixes method
		return suffixes ;
	}
	
	/* This recursive method finds all the suffixes of a word and insert it into the suffix list */
	private void getSuffixes(String word, ArrayList<String> suffixes) {
		word = word.substring(1) ;							// Removing the first character
		
		// Base condition: Skips and exits if word length is zero
		if(word.length() > 0) {
			// If the word is valid, we add it to the list
			if(isWord) {
				suffixes.add(word) ;
			}
			char curr = word.charAt(0) ;					// Using the first character to find the child
			
			// Checking if a valid child exist for the next character
			if(children != null && children.containsKey(curr)) {
				TrieNode child = children.get(curr) ;
				// Calling the child's getSuffixes method and passing the remaining string
				child.getSuffixes(word, suffixes) ;
			}
		}
	}
	
	/* Wrapper method for checking if the Trie contains the given word */
	boolean containsWord(String word) {
		StringBuffer buffer = new StringBuffer(word) ;
		return containsWord(buffer) ;						// Calling the recursive containsWord method
	}
	
	/* This method recursively finds whether the Trie contains the provided word or not
	 * Using string buffer to avoid creating string objects */
	private boolean containsWord(StringBuffer word) {
		
		// Base condition: Empty word means that no more children. Hence, we can simply return this isWord flag
		if(word.length() == 0) {
			return isWord ;
		}
		char ch = word.charAt(0) ;
		word.deleteCharAt(0) ;								// Fetch the first character of the word
		
		// Checks for the child marked by the next character
		if(children != null && children.containsKey(ch)) {
			TrieNode child = children.get(ch) ;
			// Recursively call child's containsWord() method with the remaining string
			return child.containsWord(word) ;
		}
		// If no child was found, return false
		return false ;
	}
	
	// This method calls the calculateTrieSize() method and prints the Trie size
	void printTrieSize() {
		calculateTrieSize() ;
		System.out.println("Trie size = " + trieBytes) ;
	}
	
	// This method recursively calculates the trie size using the Depth-first traversal
	private void calculateTrieSize() {
		trieBytes += 2 ;									// Each node stores a two byte character
		// Base condition: Returns if there are no children
		if(children == null) {
			return ;
		}
		
		// Depth first traversal
		for(char curr : children.keySet()) {
			TrieNode child = children.get(curr) ;
			child.calculateTrieSize() ;
		}
	}
}