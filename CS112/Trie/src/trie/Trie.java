package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		//the root is always empty, and initialized first
		int count = 0;
		TrieNode root = new TrieNode(null, null, null); 
		//iterate through the array to start inserting words into the trie
		for(String word: allWords) {
			int prefix = 0;
			int prevPrefix = 0;
			//we initially assume the new word is unique, and will be inserted based on it's array position, first letter and last letter as usual. 
			short start = 0;
			short end = (short)(word.length()-1); 
			
			//assume we have a completely empty Trie, slap on the first item inserted to to root. As of now, it points to nothing else.
			if(root.firstChild == null) {
				//Indexes index = new Indexes(count, start, end);
				root.firstChild = new TrieNode(new Indexes(count, start, end), null, null);
			}
			//there already exists some node attached to the root, so the trie is not empty. 
			//In order to add the next node, first need to compare the prefixes of the new entry and all those already in the trie
			else {
				TrieNode ptr = root.firstChild, prev = null;
				
				while(ptr != null) {
					String ptrWord = allWords[getWord(ptr)].substring(ptr.substr.startIndex, ptr.substr.endIndex + 1);
					prefix += checkPrefix(ptrWord, word.substring(prefix));
					
					if(prefix != 0 && prefix != prevPrefix) {
						if(ptr.substr.startIndex == prefix) {
							prev = ptr;
							ptr = ptr.sibling; 
						}
						if(prefix-1 == ptr.substr.endIndex) {
							prev = ptr;
							ptr = ptr.firstChild;
						}
						else {
							prev = ptr;
							break; 
						}
							
					}
					else {
						prev = ptr;
						ptr = ptr.sibling;
					}
					prevPrefix = prefix;
				}
			
				if(prefix != 0 && prefix != prevPrefix) {
					TrieNode rest = new TrieNode(new Indexes(prev.substr.wordIndex, (short)prefix, (short)prev.substr.endIndex), null, null);
					prev.substr.endIndex = (short)(prefix-1);
					TrieNode temp = prev.firstChild;
					prev.firstChild = rest; 
					rest.sibling = new TrieNode(new Indexes(count, (short)prefix, (short)(word.length()-1)), null, null);
					rest.firstChild = temp;  
				}
				else {
				prev.sibling = new TrieNode(new Indexes(count, (short)prefix, end), null, null);
				}
				
				
			}
			count++;
		}
		
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		return root; 
	}
	
	
	/*
	 * -----------------------------------------------------------------------------------------------------------
	 * PRIVATE HELPER METHODS USED FOR MY TRIE
	 */
	
	private static int checkPrefix(String parent, String next) {
		int range = 0;
		int n = parent.length() < next.length() ? parent.length() : next.length();
		for(int i = 0; i < n; i++) {
			if(parent.charAt(i) == next.charAt(i)) range++;
			else break;
		}
		return range; 
	}
	
	private static int getWord(TrieNode thing) {
		return thing.substr.wordIndex;
	}
	
	private static String wordAtNode(TrieNode node, String[] allWords) {
		return allWords[getWord(node)];
		
	}
	
	/*
	 * PRIVATE HELPER METHODS USED FOR MY TRIE
	 * -----------------------------------------------------------------------------------------------------------
	 */
	
	
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		if(root == null) return null;
		TrieNode ptr = root;
		ArrayList<TrieNode> result = new ArrayList<TrieNode>();
		while(ptr != null) {
			//this is the root node, if the indexes parameter is null we know it's a root so we continue 
			if(ptr.substr == null)ptr = ptr.firstChild;
			
			String currWord = wordAtNode(ptr,allWords);
			
			if(currWord.indexOf(prefix) == 0 || prefix.indexOf(currWord.substring(0,ptr.substr.endIndex+1)) == 0) {
				if(ptr.firstChild != null) {
					result.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
				}
				else {
					result.add(ptr);
					ptr = ptr.sibling;
				}
				 		
			}
			else {
				ptr = ptr.sibling;
			}
			
		}
		
		
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		if(result.isEmpty()) return null;
		
		return result;
	}
	
	
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
