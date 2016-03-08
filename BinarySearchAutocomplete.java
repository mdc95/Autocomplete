import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * Using a sorted array of Term objects, this implementation uses binary search to find the
 * top term(s).
 * 
 * @author Austin Lu, adapted from Kevin Wayne
 *
 */
public class BinarySearchAutocomplete implements Autocompletor {

	Term[] myTerms;

	/**
	 * Given arrays of words and weights, initialize myTerms to a corresponding
	 * array of Terms sorted lexicographically.
	 * 
	 * This constructor is written for you, but you may make modifications to 
	 * it.
	 * 
	 * @param terms - A list of words to form terms from
	 * @param weights - A corresponding list of weights, such that
	 * terms[i] has weight[i].
	 * @return a BinarySearchAutocomplete whose myTerms object
	 * has myTerms[i] = a Term with word terms[i] and weight weights[i].
	 * @throws a NullPointerException if either argument passed in is
	 * null
	 */
	public BinarySearchAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		myTerms = new Term[terms.length];
		for (int i = 0; i < terms.length; i++) {
			myTerms[i] = new Term(terms[i], weights[i]);
		}
		Arrays.sort(myTerms);
	}

	/**Uses binary search to find the index of the first Term in the passed in 
	 * array which is considered equivalent by a comparator to the given key.
	 * This method should not call comparator.compare() more than 1+log n times,
	 * where n is the size of a.
	 * 
	 * @param a - The array of Terms being searched
	 * @param key - The key being searched for.
	 * @param comparator - A comparator, used to determine equivalency
	 * between the values in a and the key.
	 * @return The first index i for which comparator considers a[i] and key
	 * as being equal. If no such index exists, return -1 instead.
	 */
	public static int firstIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		//TODO: Implement firstIndexOf
		int low = -1;
		int high = a.length-1;
		while (high - low > 1){		
			int mid = (low+high)/2;
			if (comparator.compare(a[mid],key) == 0){
				high = mid;
			}
			else if (comparator.compare(a[mid],key) < 0){
				low = mid;
			}
			else{
				high = mid;
			}

		}
		if (comparator.compare(a[high],key) == 0){
			return high;
		}
		else return -1;
	}

	/**The same as firstIndexOf, but instead finding the index of the
	 * last Term.
	 * 
	 * @param a - The array of Terms being searched
	 * @param key - The key being searched for.
	 * @param comparator - A comparator, used to determine equivalency
	 * between the values in a and the key.
	 * @return The last index i for which comparator considers a[i] and key
	 * as being equal. If no such index exists, return -1 instead.
	 */
	public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		//TODO: Implement lastIndexOf
		int low = 0;
		int high = a.length;
		while (high - low > 1){		
			int mid = (low+high)/2;
			if (comparator.compare(a[mid],key) == 0){
				low = mid;
			}
			else if (comparator.compare(a[mid],key) < 0){
				low = mid;
			}
			else{
				high = mid;
			}

		}
		if (comparator.compare(a[low],key) == 0){
			return low;
		}
		else return -1;
	}


	/**
	 * Required by the Autocompletor interface.
	 * Returns an array containing the k words in myTerms with the largest weight
	 * which match the given prefix, in descending weight order. If less than k
	 * words exist matching the given prefix (including if no words exist),
	 * then the array instead contains all those words.
	 * e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then topKMatches("b", 2)
	 * should return {"bell", "bat"}, but topKMatches("a", 2) should return
	 * {"air"}
	 * 
	 * @param prefix - A prefix which all returned words must start with
	 * @param k - The (maximum) number of words to be returned
	 * @return An array of the k words with the largest weights among all
	 * words starting with prefix, in descending weight order.
	 * 	If less than k such words exist, return an array containing all those 
	 *  words
	 * 	If no such words exist, reutrn an empty array
	 * @throws a NullPointerException if prefix is null
	 */
	public String[] topKMatches(String prefix, int k) {
		//TODO: Implement topKMatches
		if (prefix == null){
			throw new NullPointerException("Prefix is null");
		}
		Term pre = new Term(prefix, 0);
		ArrayList<String> list = new ArrayList<>();
		
		int first = firstIndexOf(myTerms, pre, new Term.PrefixOrder(prefix.length()));
		int last = lastIndexOf(myTerms, pre, new Term.PrefixOrder(prefix.length()));
		
		if (first == -1 || last == -1){
			return new String[0];
		}
		
		if (first != -1 && last != -1){ //if there is a first and last index
			Term[] sub = Arrays.copyOfRange(myTerms, first, last+1);
			List<Term> sublist = Arrays.asList(sub);
			Collections.sort(sublist, new Term.ReverseWeightOrder());

			//if k > last-first, then just use entire sublist rather than just k
			if (sub.length < k){
				for (int i = 0; i<sublist.size(); i++){
					list.add(sublist.get(i).getWord());
				}
			}
			else{

				for (int i = 0; i<k; i++){
					list.add(sublist.get(i).getWord());
				}
			} }
		String[] array = list.toArray(new String[list.size()]);
		return array;
	}

	@Override
	/**
	 * Given a prefix, returns the largest-weight word in myTerms starting with 
	 * that prefix. 
	 * e.g. for {air:3, bat:2, bell:4, boy:1}, topMatch("b") would return "bell".
	 * If no such word exists, return an empty String.
	 * 
	 * @param prefix - the prefix the returned word should start with
	 * @return The word from myTerms with the largest weight starting with 
	 * prefix, or an empty string if none exists
	 * @throws a NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		//TODO: Implement topMatch
		String maxTerm = "*";
		double maxWeight = -1;
		Term pre = new Term(prefix, 0);
		int first = firstIndexOf(myTerms, pre, new Term.PrefixOrder(prefix.length()));
		int last = lastIndexOf(myTerms, pre, new Term.PrefixOrder(prefix.length()));
		
		if (first == -1 || last == -1){
			return "";
		}

		if (first != -1 && last != -1){
			Term[] sub = Arrays.copyOfRange(myTerms, first, last+1);
			for(Term t: sub){
				if (t.getWeight() > maxWeight && t.getWord().startsWith(prefix)) {
					maxTerm = t.getWord();
					maxWeight = t.getWeight();
				}
			}
		}
		
		return maxTerm;
	}

}
