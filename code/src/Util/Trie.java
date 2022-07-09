package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trie {
	private TrieNode root;
	private Concept concept = null;
	
	public Trie(Concept concept)
	{
		root = new TrieNode();
		this.concept = concept;
	}
	
	public void insert(List<String> pattern, double posSup, double negSup, int len)
	{
		if(pattern == null || pattern.size() == 0)
			return;
		TrieNode root = this.root;
		for(int i = 0; i < pattern.size(); i++)
		{
			String e = pattern.get(i);
			if(root.child.containsKey(e))
			{
				root = root.child.get(e);
			}
			else
			{
				TrieNode node = new TrieNode(e);
				if(i == pattern.size()-1)
				{
					node.setLast(posSup, negSup, len);
				}
				root.child.put(e, node);
				root = node;
			}
		}
	}
	
	public boolean query(List<String> pattern)
	{
//		System.out.println("pattern"+pattern);
		if(pattern == null || pattern.size() == 0)
			return false;
		return queryHelper(pattern, 0, this.root);
	}
	
	private boolean queryHelper(List<String> pattern, int cur, TrieNode node)
	{
		if(node.child.keySet().isEmpty())
			return false;
		for(int i = cur; i < pattern.size(); i++)
		{
			String se = pattern.get(i);
			for(String k : node.child.keySet())
			{
				if(contain(k, se))
				{
					TrieNode tn = node.child.get(k);
					if(tn.last)
						return true;
					if(queryHelper(pattern, i+1, tn))
						return true;
				}
			}
		}
		return false;
	}
	
	private boolean contain(String p, String ch)
	{
		return this.concept.query(p, ch);
	}
	
	// travel the trie and print all pattern
	public ArrayList<Sdsp> traver()
	{
		ArrayList<String> pattern = new ArrayList<>();
		ArrayList<Sdsp> dsps = new ArrayList<>();
		traverHelper(pattern, this.root, dsps);
		return dsps;
	}
	
	private void traverHelper(ArrayList<String> pattern, TrieNode node, ArrayList<Sdsp> dsps)
	{
		if(node.last)
		{
			List<String> pt = null;
			try {
				pt = Tool.deepCopy(pattern);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Sdsp dsp = new Sdsp(pt, node.posSup, node.negSup, node.len);
			dsps.add(dsp);
			return;
		}
		for(String k:node.child.keySet())
		{
			TrieNode n = node.child.get(k);
			pattern.add(n.cand);
			traverHelper(pattern, n, dsps);
			pattern.remove(pattern.size()-1);
		}
	}
	
	
	
	public static void main(String[] args)
	{
		
//		Trie trie = new Trie();
//		
//		ArrayList<String> str = new ArrayList<>();
//		str.add("FY,DE,ALIM,HKR");
//		str.add("FY,DE,ALIM,VLIM");
//		str.add("FY,DE,AVM,AVLIM");
//		str.add("FY,DE,AVLIM,AVIM");
//		str.add("AVLIM,FY,DE,DE,AVM,VLM");
//		
//		for(String s:str)
//		{
//			ArrayList<Set<String>> p = new ArrayList<>();
//			String[] ls = s.split(",");
//			for(String l:ls)
//			{
//				Set<String> set = new HashSet<String>();
//				for(String e:l.split(""))
//				{
//					set.add(e);
//				}
//				p.add(set);
//			}
//			if(trie.query(p))
//			{
//				System.out.println("exist:"+p);
//			}
//			else
//			{
//				System.out.println("insert:"+p);
//				trie.insert(p, 0, 1);
//			}
//		}
		
//		trie.traver();
		
//		Set<String> set1 = new HashSet<String>();
//		set1.add("F");
//		set1.add("Y");
//		
//		Set<String> set2 = new HashSet<String>();
//		set2.add("F");
//		set2.add("Y");
		
		
	}
	
}
