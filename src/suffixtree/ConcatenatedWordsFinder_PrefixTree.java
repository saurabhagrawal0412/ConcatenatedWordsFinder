package suffixtree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * BASIC INFORMATION
 * Date: Mar 17, 2017
 * Author: Saurabh Agrawal (saurabhagrawal0412@gmail.com)
 * This program finds the two longest concatenated words and the total count of concatenated words
 * Takes input from the file: 'E:/Java/TempWS/IBMTest/resources/words for problem.txt'
 * 
 * EXAMPLE
 * For example, if the file contained the words:
 * cat, cats, catsdogcats, dog, dogcatsdog, hippopotamuses, rat, ratcatdogcat
 * ratcatdogcat and catsdogcats will be the longest concatenated words
 * The total number of concatenated words is three
 * 
 * STEPS
 * 1: Build a Prefix tree (Trie) 
 * 2: Add each word to the tree and check if it has prefixes or not 
 * 3: If it has prefixes, add the resulting suffixes to a list
 * 4: After the tree is built, process the list of suffixes
 * 5: For every suffix, check whether it is a valid word or not
 * 6: If it is, add the word to a set and check if it could be added to the output list
 * 7: Print the outputs (the size of the word set is the total count of concatenated words)
 * 
 * OUTPUT
 * Longest concatenated word: ethylenediaminetetraacetates (length = 28)
 * Second longest concatenated word: electroencephalographically (length = 27)
 * Total number of concatenated words: 97107
 * 
 * Time taken: 1901 milliseconds (on my PC: dual core i5, 8 GB RAM, Disk drive)
 * Space: Around 2 MBs
 * 
 * ADVANTAGES OF THIS APPROACH
 * The total memory required for this approach is lesser than the DP approach
 * This approach would have been more space-efficient if we were not to calculate the total number of concatenated words
 * 
 * DISADVANTAGES OF THIS APPROACH
 * This approach is more time-consuming
 * More complex than the DP approach
 * 
 * ALTERNATE APPROACHES
 * 1: Dynamic Programming (Implemented in the other program)
 * 
 * 2: Bloom Filter
 * If the input file is even larger (in the order of GBs), then we can use Bloom Filter instead of using in memory HashSet
 * Bloom Filter would only require 1/4 space
 * But it would decrease the accuracy of the solution (Error rate: 1.0E-8)
 */

/* This class contains the method to find the longest concatenated words 
 * and the count of concatenated words in the file*/
public class ConcatenatedWordsFinder_PrefixTree {
	
	private TrieNode root ;								// Stores the root of the prefix tree (Trie)
	private PriorityQueue<PrefixEntry> suffixQueue ;	// Stores the suffix list with decreasing word-length
	private ArrayList<String> outputList ;				// Stores the output words
	private int outputSize = 2 ;						// The number of longest concatenated words required
	private HashSet<String> concatSet ;
	// This set contains the processed concatenated words. Only had to use it to find the total number of concatenated words 
	
	// Private constructor to make the main class singleton
	private ConcatenatedWordsFinder_PrefixTree() {
		suffixQueue = new PriorityQueue<PrefixEntry>(new PrefixEntryComparator()) ;
		outputList = new ArrayList<>() ;
		concatSet = new HashSet<>() ;
	}
	
	// This method reads the file from the given path and store the words in the dictionary
	private void makeTrie(final String path) {
		root = new TrieNode('*') ;						// The root stores a dummy asterisk character
		
		// Try-with-resources block to automatically close the streams
		// 8 KB buffer provides good disk-read performance on my system
		try(Reader reader = new BufferedReader(new FileReader(path), 8192)) {
			Scanner sc = new Scanner(reader) ;
			while(sc.hasNext()) {
				String word = sc.next() ;
				root.insertWord(word) ;					// This TrieNode's method inserts the word in the Trie
				preparePrefixList(word) ;				// Method call to find and store suffixes of the word
			}
			sc.close() ;
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found") ;
			e.printStackTrace() ;
		}
		catch(IOException e) {
			System.err.println("IO exception") ;
			e.printStackTrace() ;
		}
	}
	
	// This method finds all the suffixes of a word and insert it in the suffix list
	private void preparePrefixList(final String word) {
		ArrayList<String> suffixes = root.getSuffixes(word) ;
		// Called getSuffixes() method of TrieNode to get the list of suffixes for a given word
		
		PrefixEntry pEntry = new PrefixEntry(word, suffixes) ;
		if(pEntry.getSuffixes().size() > 0) {
			suffixQueue.add(pEntry) ;
		}
	}
	
	/* This method executes after the Trie is built. For each suffix, it finds whether or not it is a word.
	 * If the suffix is a word, the base word is added to the set.
	 * Otherwise, we try to find if it has more prefixes. If it does, we store the smaller suffixes in the queue. */
	private void processPrefixList() {
		// Runs until the suffix priority queue is empty
		while(! suffixQueue.isEmpty()) {
			PrefixEntry pEntry = suffixQueue.remove() ;	// Removes the first element
			String word = pEntry.getWord() ;
			
			// If the word is already in the concatenated word set, we skip the word
			if(concatSet.contains(word)) {
				continue ;
			}
			
			boolean isFound = false ;
			
			for(String suffix : pEntry.getSuffixes()) {
				if(root.containsWord(suffix)) {
					if(isEligibleOutput(word)) {		// This method compares the size of the current word with the output list
						addToOutputList(word) ;			// This adds the word to output list at the correct place
					}
					isFound = true ;					// Only sets isFound, if it the Trie contains the suffix
					concatSet.add(word) ;				// The word is a valid concatenated word, thus adding it to the set
					break ;								// No more processing required
				}
			}
			
			if(! isFound) {								// If none of the suffixes were words, we call add prefixes
				addPrefixes(pEntry, word) ;
			}
		}
	}
	
	// This method finds suffixes for all the suffixes of a word and add it to the suffix list
	private void addPrefixes(PrefixEntry pEntry, final String word) {
		for(String suffix : pEntry.getSuffixes()) {
			ArrayList<String> suffixes = root.getSuffixes(suffix) ;// This method finds all the suffixes of a given word
			PrefixEntry currEntry = new PrefixEntry(word, suffixes) ;
			
			if(currEntry.getSuffixes().size() > 0) {
				suffixQueue.add(currEntry) ;			// Adding the determined suffixes to the queue
			}
		}
	}
	
	// This method only returns false if provided word is shorter than the full output list's last element
	private boolean isEligibleOutput(final String word) {
		if(outputList.size() == outputSize && word.length() < outputList.get(outputSize-1).length()) {
			return false ;
		}
		return true ;
	}
	
	/* This method maintains the output list
	 * The output (Array)list stores the words in decreasing length | list[0] | >= | list[1] |*/
	private void addToOutputList(final String word) {
		if(outputList.size() < outputSize) {
			outputList.add(word) ;
			return ;
		}
		
		// Stores the current word if the length of the current word is greater than ith word in the list
		for(int i=0 ; i<outputList.size() ; i++) {
			if(word.length() > outputList.get(i).length()) {
				outputList.add(i, word) ;
				break ;
			}
		}
		
		// Maintains the size of the output list if it gets larger than the required output size
		if(outputList.size() > outputSize) {
			outputList.remove(outputSize) ;
		}
	}
	
	// This method prints the output list and the count of concatenated words
	private void printOutput() {
		root.printTrieSize() ;
		System.out.println("Count of concatenated words = " + concatSet.size()) ;
		System.out.println("Printing output list") ;
		for(String op : outputList) {
			System.out.println(op);
		}
	}
	
	public static void main(String[] args) {
		long millis1 = System.currentTimeMillis() ;
		ConcatenatedWordsFinder_PrefixTree obj = new ConcatenatedWordsFinder_PrefixTree() ;
		String path = "E:/Java/TempWS/IBMTest/resources/words for problem.txt" ;
		
		obj.makeTrie(path) ;							// Building the Trie
		obj.processPrefixList() ;						// Processing the prefix list
		
		long millis2 = System.currentTimeMillis() ;
		System.out.printf("Time taken = %d milliseconds \n", (millis2 - millis1)) ;
		
		obj.printOutput() ;								// Printing the outputs
	}
}