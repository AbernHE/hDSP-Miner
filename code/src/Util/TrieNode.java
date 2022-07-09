package Util;

import java.util.HashMap;
import java.util.Set;

public class TrieNode {
	public String cand;  // element
	public boolean last; // mark the last element of pattern
	public HashMap<String, TrieNode> child;
	public double posSup;
	public double negSup;
	public int len = 0;
	
	public TrieNode()
	{
		super();
		child = new HashMap<>();
		last = false;
	}

	public TrieNode(String cand) {
		super();
		this.cand = cand;
		this.last = false;
		this.child = new HashMap<>();
	}
	
	public void setLast(double posSup, double negSup, int len)
	{
		this.last = true;
		this.posSup = posSup;
		this.negSup = negSup;
		this.len = len;
	}
	
}
