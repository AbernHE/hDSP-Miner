package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Concept {
	private Map<String, CNode> map = null;
	private CNode root = null;
	
	public Concept(List<Map<String, List<String>>> conceptList)
	{
		map = new HashMap<String, CNode>();
		init(conceptList);
	}
	
	private void init(List<Map<String, List<String>>> conceptList)
	{
		for(Map<String, List<String>> concept:conceptList)
		{
			for(String k:concept.keySet())
			{
				if(!map.containsKey(k))
				{
					CNode cn = new CNode(k);
					map.put(k, cn);
					if(k.equals("0"))
					{
						root = cn;
					}
				}
//				System.out.print(k+":");
				List<String> ls = concept.get(k);
				CNode r = map.get(k);
				for(String s:ls)
				{
//					System.out.print(s+",");
					CNode tmp = new CNode(s);
					tmp.parent = r;
					r.degree ++;
					r.child.add(tmp);
					map.put(s, tmp);
				}
//				System.out.println();
			}
		}
	}
	
	public void prune(Set<String> pos)
	{
		pruneHelp(pos, this.root.child);
	}
	
	private void pruneHelp(Set<String> pos, List<CNode> child)
	{
		for(int i = 0; i < child.size(); i++)
		{
			CNode cn = child.get(i);
			pruneHelp(pos, cn.child);
			if(cn.child.isEmpty() && !pos.contains(cn.value))
			{
				CNode p = cn.parent;
				p.child.remove(cn);
				p.degree --;
				this.map.remove(cn.value);
				i --;
			}
			else if(cn.child.size() == 1 && !pos.contains(cn.value))
			{
				CNode c = cn.child.get(0);
				this.map.remove(cn.value);
				cn.value = c.value;
				cn.degree = c.degree;
				c.parent = null;
				cn.child = c.child;
				this.map.replace(c.value, cn);
			}
		}
		
	}
	
	public boolean query(String parent, String child)
	{
		if(!this.map.containsKey(parent) || !this.map.containsKey(child))
		{
			return false;
		}
		CNode ch = this.map.get(child);
		
		while(ch != null && ch.value != null)
		{
			if(ch.value.equals(parent))
				return true;
			ch = ch.parent;
		}
		return false;
	}
	
	public void printConcept()
	{
		List<Map<String, List<String>>> con = new ArrayList<>();
		printHelp(this.root, con, 0);
		for(Map<String, List<String>> o:con)
		{
			for(String k:o.keySet())
			{
				System.out.print(k+": ");
				List<String> ls = o.get(k);
				for(String s:ls)
				{
					System.out.print(s+",");
				}
				System.out.println();
			}
		}
	}
	
	private void printHelp(CNode root, List<Map<String, List<String>>> con, int d)
	{
		if(!root.child.isEmpty())
		{
			if(d >= con.size())
			{
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				con.add(map);
			}
			Map<String, List<String>> map = con.get(d);
			List<String> ls = new ArrayList<String>();
			for(CNode cn:root.child)
			{
				ls.add(cn.value);
				printHelp(cn, con, d+1);
			}
			map.put(root.value, ls);
			
		}
	}
	
	public CNode getNode(String key)
	{
		return this.map.get(key);
	}
	
	public CNode getRoot()
	{
		return this.root;
	}
	
}
