package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() 
	{
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> s = new Stack<TagNode>();
		TagNode popped = null;
		while (sc.hasNextLine())
		{
			String temp = sc.nextLine().toString();
			if (!temp.contains(" ") && !temp.contains("/") && temp.substring(0,1).contentEquals("<") && temp.substring(temp.length()-1,temp.length()).contentEquals(">"))
			{
				temp = temp.substring(1, temp.length()-1);
				TagNode t = new TagNode(temp, null, null);
				if (root == null)
				{
					root = t;
					s.push(t);
				}
				else
				{
					if (s.peek().firstChild == null)
					{
						s.peek().firstChild = t;
					}
					else
					{
						if (popped != null)
						{
							popped.sibling = t;
							popped = null;
						}
						else
						{
							TagNode ptr = s.peek().firstChild;
							while (ptr.sibling != null)
							{
								ptr = ptr.sibling;
							}
							ptr.sibling = t;
						}
					}
					s.push(t);
				}
			}
			else if (temp.substring(0,1).contentEquals("<") && temp.substring(temp.length()-1,temp.length()).contentEquals(">") && temp.substring(1,2).contentEquals("/"))
			{
				popped = s.pop();
			}
			else
			{
				TagNode t = new TagNode(temp, null, null);
				if (s.peek().firstChild == null)
				{
					s.peek().firstChild = t;
				}
				else
				{
					if (popped != null)
					{
						popped.sibling = t;
						popped = null;
					}
					else
					{
						TagNode ptr = s.peek().firstChild;
						while (ptr.sibling != null)
						{
							ptr = ptr.sibling;
						}
						ptr.sibling = t;
					}
				}
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	private void replaceTagHelper(TagNode r, String oldTag, String newTag)
	{
		for (TagNode ptr=r; ptr != null;ptr=ptr.sibling) 
		{
			if (ptr.tag.contentEquals(oldTag) && ptr.firstChild != null)
			{
				ptr.tag = newTag;
			}
			if (ptr.firstChild != null) 
			{
				replaceTagHelper(ptr.firstChild, oldTag, newTag);
			}
		}
	}
	public void replaceTag(String oldTag, String newTag) 
	{
		/** COMPLETE THIS METHOD **/
		replaceTagHelper(root, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	private void boldRowHelper(TagNode r, int row, int rowCounter)
	{
		for (TagNode ptr = r; ptr != null; ptr = ptr.sibling)
		{
			if (ptr.tag.contentEquals("tr") && ptr.firstChild != null)
			{
				rowCounter++;
			}
			else if (ptr.tag.contentEquals("td") && rowCounter == row)
			{
				TagNode n = new TagNode("b", ptr.firstChild, null);
				ptr.firstChild = n;
			}
			if (ptr.firstChild != null)
			{
				boldRowHelper(ptr.firstChild, row, rowCounter);
			}
		}
	}
	public void boldRow(int row) 
	{
		/** COMPLETE THIS METHOD **/
		boldRowHelper(root, row, 0);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	private void removeTagHelper1(TagNode r, String tag)
	{
		for (TagNode ptr = r; ptr != null; ptr = ptr.sibling)
		{
			if (ptr.firstChild != null && ptr.firstChild.tag.contentEquals(tag))
			{
				TagNode firstP = ptr.firstChild;
				ptr.firstChild = ptr.firstChild.firstChild;
				TagNode previous = firstP;
				for (TagNode temp = firstP.sibling; temp != null; temp = temp.sibling)
				{
					if (temp.tag.contentEquals(tag) && previous.tag.contentEquals(tag))
					{
						
							TagNode last = null;
							for (TagNode x = previous.firstChild; x != null; x = x.sibling)
							{
								last = x;
							}
							last.sibling = temp.firstChild;
						
					}
					else if (temp.tag.equals(tag) && !previous.tag.contentEquals(tag))
					{
						previous.sibling = temp.firstChild;
					}
					else if (!temp.tag.contentEquals(tag) && previous.tag.contentEquals(tag))
					{
						TagNode last = null;
						for (TagNode x = previous.firstChild; x != null; x = x.sibling)
						{
							last = x;
						}
						last.sibling = temp;
					}
					previous = temp;
				}
			}
			if (ptr.sibling != null && ptr.sibling.tag.equals(tag))
			{
				TagNode temp = ptr.sibling.sibling;
				TagNode last = null;
				for (TagNode x = ptr.sibling.firstChild; x != null; x = x.sibling)
				{
					last = x;
				}
				ptr.sibling = ptr.sibling.firstChild;
				last.sibling = temp;
			}
			if (ptr.firstChild != null)
			{
				removeTagHelper1(ptr.firstChild, tag);
			}
		}
	}
	private void removeTagHelper2(TagNode r, String tag)
	{
		for (TagNode ptr = r; ptr != null; ptr = ptr.sibling)
		{
			if (ptr.firstChild != null && ptr.firstChild.tag.contentEquals(tag))
			{
				TagNode firstP = ptr.firstChild;
				ptr.firstChild = ptr.firstChild.firstChild;
				if (ptr.firstChild.tag.contentEquals("li"))
				{
					for (TagNode pointer = ptr.firstChild; pointer != null; pointer = pointer.sibling)
					{
						if (pointer.tag.contentEquals("li"))
						{
							pointer.tag = "p";
						}
					}
				}
				TagNode previous = firstP;
				for (TagNode temp = firstP.sibling; temp != null; temp = temp.sibling)
				{
					if (temp.tag.contentEquals(tag) && previous.tag.contentEquals(tag))
					{
							for (TagNode pointer = temp.firstChild; pointer != null; pointer = pointer.sibling)
							{
								if (pointer.tag.contentEquals("li"))
								{
									pointer.tag = "p";
								}
							}
							TagNode last = null;
							for (TagNode x = previous.firstChild; x != null; x = x.sibling)
							{
								last = x;
							}
							last.sibling = temp.firstChild;
					}
					else if (temp.tag.equals(tag) && !previous.tag.contentEquals(tag))
					{
						for(TagNode pointer = temp.firstChild; pointer != null; pointer = pointer.sibling)
						{
							if (pointer.tag.equals("li"))
							{
								pointer.tag = "p";
							}
						}
						previous.sibling = temp.firstChild;
					}
					else if (!temp.tag.contentEquals(tag) && previous.tag.contentEquals(tag))
					{
						TagNode last = null;
						for (TagNode x = previous.firstChild; x != null; x = x.sibling)
						{
							last = x;
						}
						last.sibling = temp;
					}
					previous = temp;
				}
			}
			if (ptr.sibling != null && ptr.sibling.tag.equals(tag))
			{
				for (TagNode pointer = ptr.sibling.firstChild; pointer != null; pointer = pointer.sibling)
				{
					if (pointer.tag.contentEquals("li"))
					{
						pointer.tag = "p";
					}
				}
				TagNode temp = ptr.sibling.sibling;
				TagNode last = null;
				for (TagNode x = ptr.sibling.firstChild; x != null; x = x.sibling)
				{
					last = x;
				}
				ptr.sibling = ptr.sibling.firstChild;
				last.sibling = temp;
			}
			if (ptr.firstChild != null)
			{
				removeTagHelper2(ptr.firstChild, tag);
			}
		}
	}
	public void removeTag(String tag) 
	{
		/** COMPLETE THIS METHOD **/
		if (tag.contentEquals("p")||tag.contentEquals("em")||tag.contentEquals("b"))
		{
			removeTagHelper1(root, tag);
		}
		else if (tag.contentEquals("ol") || tag.contentEquals("ul"))
		{
			removeTagHelper2(root, tag);
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	private boolean isLegitPunctuation(String s)
	{
		return (s.contentEquals("!") || s.contentEquals("?") || s.contentEquals(".") || s.contentEquals(";") || s.contentEquals(":"));
	}
	private boolean isTaggableWord(String partOfSentence, String word)
	{
		word = word.toLowerCase();
		partOfSentence = partOfSentence.toLowerCase();
		if (partOfSentence.contains(word))
		{
			if (partOfSentence.indexOf(word) == 0)
			{
				if (word.length() == partOfSentence.length())
				{
					return true;
				}
				else
				{
					if (word.length() == partOfSentence.length() - 1)
					{
						if (isLegitPunctuation(partOfSentence.substring(partOfSentence.length()-1)))
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}
				}
			}
			else
			{
				return false;
			}
		}
		return false;
	}
	private boolean containsTaggableWord(String sentence, String word)
	{
		word = word.toLowerCase();
		sentence = sentence.toLowerCase();
		String [] arr = sentence.split(" ");
		for (String x : arr)
		{
			if (x.contains(word))
			{
				if (x.indexOf(word) == 0)
				{
					if (word.length() == x.length())
					{
						return true;
					}
					else
					{
						if (word.length() == x.length() - 1)
						{
							if (isLegitPunctuation(x.substring(x.length()-1)))
							{
								return true;
							}
							else
							{
								return false;
							}
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}
	private void addTagHelper(TagNode r, String word, String tag)
	{
		for (TagNode ptr = r; ptr != null; ptr = ptr.sibling)
		{
			if (ptr.firstChild != null && ptr.firstChild.firstChild == null && !ptr.tag.contentEquals(tag))
			{
				if (containsTaggableWord(ptr.firstChild.tag, word))
				{
				TagNode sib = ptr.firstChild.sibling;
				String [] arr = getWordList(ptr.firstChild.tag);
				TagNode front = null;
				TagNode back = null;
				for (String x : arr)
				{
					if (isTaggableWord(x, word))
					{
						TagNode taggedWord = new TagNode(x, null, null);
						TagNode newTag = new TagNode(tag, taggedWord, null);
						if (front == null)
						{
							front = newTag;
							back = front;
						}
						else
						{
							back.sibling = newTag;
							back = back.sibling;
						}
					}
					else
					{
						TagNode taggedWord = new TagNode(x, null, null);
						if (front == null)
						{
							front = taggedWord;
							back = front;
						}
						else
						{
							if (back.firstChild != null)
							{
								back.sibling = taggedWord;
								back = back.sibling;
							}
							else
							{
								back.tag = back.tag + x;
							}
						}
					}
				}
				ptr.firstChild = front;
				back.sibling = sib;				
			}
			}
			
			
			if (ptr.sibling != null && ptr.sibling.firstChild == null)
			{
				if (containsTaggableWord(ptr.sibling.tag, word))
				{
				TagNode sib = ptr.sibling.sibling;
				String [] arr = getWordList(ptr.sibling.tag);
				TagNode front = null;
				TagNode back = null;
				for (String x : arr)
				{
					if (isTaggableWord(x, word))
					{
						TagNode taggedWord = new TagNode(x, null, null);
						TagNode newTag = new TagNode(tag, taggedWord, null);
						if (front == null)
						{
							front = newTag;
							back = front;
						}
						else
						{	
							back.sibling = newTag;
							back = back.sibling;
						}
					}
					else
					{
						TagNode taggedWord = new TagNode(x, null, null);
						if (front == null)
						{
							front = taggedWord;
							back = front;
						}
						else
						{
							if (back.firstChild != null)
							{
								back.sibling = taggedWord;
								back = back.sibling;
							}
							else
							{
								back.tag = back.tag + x;
							}
						}
					}
				}
				ptr.sibling = front;
				back.sibling = sib;
			}
			}
			if (ptr.firstChild != null)
			{
				addTagHelper(ptr.firstChild, word, tag);
			}
			
		}
	}
	private String [] getWordList(String w)
	{
		Stack <String> words = new Stack <String>();
		String temp = "";
		for (int i = 0; i < w.length(); i++)
		{
			if (w.substring(i,i+1).contentEquals(" "))
			{
				if (words.isEmpty())
				{
					if (!temp.contentEquals(""))
					{
						words.push(temp);
						temp = "";
					}
					words.push(" ");
				}
				else if (!temp.contentEquals(""))
				{
					words.push(temp);
					words.push(" ");
					temp = "";
				}
				else
				{
					words.push(words.pop() + " ");
				}
			}
			else
			{
				temp += w.substring(i,i+1);
			}
		}
		if (!temp.contentEquals(""))
		{
			words.push(temp);
		}
		String [] arr = new String[words.size()];
		for (int i = arr.length-1; i >= 0; i--)
		{
			arr[i] = words.pop();
		}
		return arr;
	}
	public void addTag(String word, String tag) 
	{
		/** COMPLETE THIS METHOD **/
		if (tag.contentEquals("em") || tag.contentEquals("b"))
		{
		
			addTagHelper(root, word, tag);
		}		
	}
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}