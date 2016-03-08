import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 *
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws a
	 *             NullPointerException if either argument is null
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 * 
	 */
	private void add(String word, double weight) {
		// TODO: Implement add
		if (word == null){
			throw new NullPointerException("Word is null");
		}
		if (weight == -1){
			throw new IllegalArgumentException("Weight is negative");
		}
		Node current = myRoot;
		char[] myChars = word.toCharArray();
		for (char ch : myChars){
			if (current.mySubtreeMaxWeight < weight){
				current.mySubtreeMaxWeight = weight;
			}
			if (!current.children.containsKey(ch)){
				current.children.put(ch, new Node(ch, current, weight));
			}
			current = current.children.get(ch);
		}
		current.myWeight = weight;
		//set current to be a word
		current.isWord = true;
		//set current's myWord
		current.setWord(word);
		if (current.mySubtreeMaxWeight < weight){
			current.mySubtreeMaxWeight = weight;
		}
		//decreasing the weight???
		if (current.mySubtreeMaxWeight > weight){
			while (current != null){
				current.mySubtreeMaxWeight = current.myWeight;
				for (Node child : current.children.values()){
					if (child.mySubtreeMaxWeight > current.mySubtreeMaxWeight){
						current.mySubtreeMaxWeight = child.mySubtreeMaxWeight;
					}
				}
				current = current.parent;
			}		
		}
	}

	@Override
	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An array of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k
	 *         such words exist, return an array containing all those words If
	 *         no such words exist, return an empty array
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public String[] topKMatches(String prefix, int k) {
		if (prefix == null){
			throw new NullPointerException("Prefix is null");
		}
		PriorityQueue<Node> que = new PriorityQueue<Node>(k, new Node.ReverseSubtreeMaxWeightComparator());
		PriorityQueue<Node> que2 = new PriorityQueue<Node>();
		Node current = myRoot;
		//naviage current to prefix node here
		char[] myChars = prefix.toCharArray();
		for (char ch : myChars){
			if (!current.children.containsKey(ch)){
				System.out.println("empty");
				return new String[0];
			}
			current = current.children.get(ch);
		}
		que.add(current);
		while (!que.isEmpty()){ //while its not null and you haven't found top k matches
			if (que2.size() >= k){	
				if(que2.peek().myWeight < que.peek().mySubtreeMaxWeight){
					que2.poll();
				}
				else{
					
					break;
				}
			}
			current = que.poll();
			if (current.isWord){
				que2.add(current);
			}
			for (Node child: current.children.values()){
				que.add(child);
			}
		}
		
		String[] arr = new String[Math.min(k,que2.size())];
		for (int i = arr.length-1; i>=0; i--){
			arr[i] = que2.poll().myWord;
		}
		
		return arr;
			
	}

	@Override
	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from _terms with the largest weight starting with
	 *         prefix, or an empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 * 
	 */
	public String topMatch(String prefix) {
		if (prefix == null){
			throw new NullPointerException("Prefix is null");
		}
		Node current = myRoot;
		//get to node that represents prefix
		char[] myChars = prefix.toCharArray();
		for (char ch : myChars){
			if (!current.children.containsKey(ch)){
				return "";
			}
			current = current.children.get(ch);
		}
		while (current.mySubtreeMaxWeight != current.myWeight){
			for (Node child: current.children.values()){
				if (child.mySubtreeMaxWeight == current.mySubtreeMaxWeight){
					current = child;
					break;
				}

			}

		}
		return current.myWord;
	}

}
