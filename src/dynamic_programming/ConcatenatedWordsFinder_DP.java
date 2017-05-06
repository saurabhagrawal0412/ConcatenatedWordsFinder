package dynamic_programming;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * BASIC INFORMATION
 * Date: Mar 16, 2017
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
 * 1: Store the words in the dictionary (HashSet<String>)
 * 2: Iterate the dictionary
 * 3: For each word, use Dynamic Programming to find if a word is a concatenation of the other (smaller) words
 * 4: If word is concatenated, increase the concatenatedWordCounter
 * 5: Check whether the word is longer than the words in the output list
 * 6: If it is, add it in the output list
 * 7: Print the output
 * 
 * OUTPUT
 * Longest concatenated word: ethylenediaminetetraacetates (length = 28)
 * Second longest concatenated word: electroencephalographically (length = 27)
 * Total number of concatenated words: 97107
 * 
 * Time taken: 1334 milliseconds (on my PC: dual core i5, 8 GB RAM, Disk drive)
 * Space: Lesser than 3.5 MBs
 * 
 * ADVANTAGES OF THIS APPROACH
 * Takes lesser time than the Prefix tree approach
 * Simpler
 * 
 * DISADVANTAGES OF THIS APPROACH
 * Consumes more time than the DP approach
 * 
 * ALTERNATE APPROACHES
 * 1: Prefix tree (Implemented in the other program)
 * 
 * 2: Bloom Filter
 * If the input file is even larger (in the order of GBs), then we can use Bloom Filter instead of using in memory HashSet
 * Bloom Filter would only require 1/4 space
 * But it would decrease the accuracy of the solution (Error rate: 1.0E-8)
 */

/* This class contains the method to find the longest concatenated words 
 * and the count of concatenated words in the file*/
public class ConcatenatedWordsFinder_DP {

	private HashSet<String> dictionary ;		// Stores all unique words as dictionary for constant time access
	private int concatenatedWordCounter = 0 ;	// Stores the number of concatenated words
	private ArrayList<String> outputList ;		// Stores the output words
	private int outputSize = 2 ;				// The number of longest concatenated words required
	
	// Private constructor to make the main class singleton
	private ConcatenatedWordsFinder_DP() {
		dictionary = new HashSet<>() ;
		outputList = new ArrayList<>() ;
	}
	
	// This method reads the file from the given path and store the words in the dictionary
	private void makeDictionary(final String path) {
		
		// Try-with-resources block to automatically close the streams
		// 8 KB buffer provides good disk-read performance on my system
		try(Reader reader = new BufferedReader(new FileReader(path), 8192)) {
			Scanner sc = new Scanner(reader) ;
			int noOfWords = 0 ;					// Counter to count the total number of words in the file
			int bytes = 0 ;						// Counter to calculate the approximate size of the dictionary
			while(sc.hasNext()) {
				noOfWords++ ;
				String word = sc.next() ;		// Ignores the blank new lines at the end of file
				bytes += (word.length() * 2) ;	// Each character take 2 bytes
				dictionary.add(word) ;
			}
			
			// Printing input and memory statistics
			System.out.println("No of words = " + noOfWords) ;
			System.out.println("Bytes = " + bytes) ;
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
	
	// This method iterates through the dictionary and find the largest concatenated words and the count of concatenated words
	private void getLargestSplittableWord() {
		Iterator<String> it = dictionary.iterator() ;
		
		while(it.hasNext()) {
			String word = it.next() ;

			// If the word is concatenated, we increment the concatenatedWordCounter and call the addToOutputList method
			if(isConcatenated(word)) {
				concatenatedWordCounter++ ;
				if(isEligibleOutput(word)) {
					addToOutputList(word) ;
				}
			}
		}
	}
	
	/* This method uses Dynamic Programming to find whether the given word
	 * could be created by concatenating other words in the dictionary */
	private boolean isConcatenated(final String word) {
		
		boolean[][] table = new boolean[word.length()][word.length()] ;
		/* This table stores whether the substrings are valid dictionary words or not
		 * It gets filled diagonally with the first diagonal being [0][0] to [n-1][n-1]
		 */

		// We increase the substring length from 1 to word length
		for (int substrLength = 1 ; substrLength <= word.length() ; substrLength++) {
			
			//  The start index increments from 0 to maximum possible start index of that pass 
			for (int startIndex = 0 ; startIndex < word.length() - substrLength + 1 ; startIndex++) {
				int endIndex = startIndex + substrLength - 1 ;

				String subStr = word.substring(startIndex, endIndex + 1) ;
				// This is the substring of the word from start index to end index

				/* If the dictionary contains the substring and the substring is not equal to word,
				 * we set the table entry for that substring to true */
				if (dictionary.contains(subStr) && !(subStr.equals(word))) {
					table[startIndex][endIndex] = true ;
				}
				/* Otherwise we check if the substring could be broken into two such that each part is a valid dictionary word */
				else {
					// We split the substring at every point
					for (int splitPoint = startIndex + 1 ; splitPoint <= endIndex ; splitPoint++) {
						/* If we find a point where both sub-substrings are valid words, 
						we break the loop and we set the table entry for that substring to true */
						if (table[startIndex][splitPoint - 1] && table[splitPoint][endIndex]) {
							table[startIndex][endIndex] = true ;
							break ;
						}
					}
				}
			}
		}
		return table[0][word.length() - 1] ;	// The output is obtained at the last column of the first row
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
	private void printOutputs() {
		System.out.println("Printing output list") ;
		for(String word: outputList) {
			System.out.println(word) ;
		}
		
		System.out.println("Count of splittable words = " + concatenatedWordCounter) ;
	}
	
	public static void main(String[] args) {
		ConcatenatedWordsFinder_DP test = new ConcatenatedWordsFinder_DP() ;
		String path = "E:/Java/TempWS/IBMTest/resources/words for problem.txt" ;
		
		long millis1 = System.currentTimeMillis() ;
		
		test.makeDictionary(path) ;
		test.getLargestSplittableWord() ;

		long millis2 = System.currentTimeMillis() ;
		
		System.out.printf("Time taken = %d milliseconds\n", (millis2 - millis1)) ;
		test.printOutputs() ;
	}
}