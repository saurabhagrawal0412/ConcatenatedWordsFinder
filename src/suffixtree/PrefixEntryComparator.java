package suffixtree;

import java.util.Comparator;

/* This comparator compares the sizes of the words in PrefixEntry objects
 * It is used by the Priority queue 
 * It ensures that the largest word is fetched first */
public class PrefixEntryComparator implements Comparator<PrefixEntry> {

	@Override
	public int compare(PrefixEntry o1, PrefixEntry o2) {
		// Returns -1 if the first word is larger than the second
		if(o1.getWord().length() > o2.getWord().length()) {
			return -1 ;
		}
		
		// Returns 1 if the second word is larger than the first
		else if(o1.getWord().length() < o2.getWord().length()) {
			return 1 ;
		}
		
		// Returns 0 if both the words are of equal length
		else {
			return 0 ;
		}
	}
}
