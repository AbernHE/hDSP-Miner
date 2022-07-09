package Util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Processor {
	
	private Set<String> alphabet = null;
	private HashMap<String, List<HashMap<Integer, BitSet>>> positions = null;
	
	private int posNum = 0;
	private int negNum = 0;
	
	public Processor(String posFname, String negFname)
	{
		initPos(posFname, negFname);
	}
	
	
	
	public Set<String> getAlphabet() {
		return alphabet;
	}



	public HashMap<String, List<HashMap<Integer, BitSet>>> getPositions() {
		return positions;
	}



	public int getPosNum() {
		return posNum;
	}



	public int getNegNum() {
		return negNum;
	}



	/**
	 * 
	 * @param posFname: file name of D+
	 * @param negFname: file name of D-
	 * @return a HashMap that has the positions of each element
	 */
	private void initPos(String posFname, String negFname)
	{
		ArrayList<String> posSeq = Tool.readSeq(posFname);
		ArrayList<String> negSeq = Tool.readSeq(negFname);
		
		this.posNum = posSeq.size();
		this.negNum = negSeq.size();
		
		this.positions = new HashMap<String, List<HashMap<Integer, BitSet>>>();
		
		this.alphabet = new HashSet<String>();
		
		for(int i = 0; i < posSeq.size(); i++)
		{
			String[] seqs = posSeq.get(i).trim().split(Configure.splittoken);
			
			for(int j = 0; j < seqs.length; j++)
			{
				String s = seqs[j].trim();
				if(s == "")
					continue;
				this.alphabet.add(s);
				if(!this.positions.containsKey(s))
				{
					List<HashMap<Integer, BitSet>> ls = new ArrayList<HashMap<Integer, BitSet>>();
					HashMap<Integer, BitSet> p = new HashMap<Integer, BitSet>();
					HashMap<Integer, BitSet> n = new HashMap<Integer, BitSet>();
					ls.add(p);
					ls.add(n);
					this.positions.put(s, ls);
				}
				
				if(!this.positions.get(s).get(0).containsKey(i))
				{
					BitSet bs = new BitSet(seqs.length);
					this.positions.get(s).get(0).put(i, bs);
				}
				this.positions.get(s).get(0).get(i).set(j);
			}
		}
		
		for(int i = 0; i < negSeq.size(); i++)
		{
			String[] seqs = negSeq.get(i).trim().split(Configure.splittoken);
			
			for(int j = 0; j < seqs.length; j++)
			{
				String s = seqs[j].trim();
				if(this.positions.containsKey(s))
				{
					if(!this.positions.get(s).get(1).containsKey(i))
					{
						BitSet bs = new BitSet(seqs.length);
						this.positions.get(s).get(1).put(i, bs);
					}
					this.positions.get(s).get(1).get(i).set(j);
				}
			}
		}
	}
	
	
	public static void main(String args[]){
//		BitSet a = new BitSet(100);
//		a.set(7);
//		
//		BitSet b = new BitSet();
//		b.set(2);
//		
//		//b.or(a);
//		System.out.println(a.length());
//		System.out.println(a.size());
//		
//		System.out.println(b);
		
//		String s = "";
//		System.out.println(s.equals(""));
//		System.out.println(s=="");
		
//		ArrayList<String> ls = new ArrayList<>();
//		ls.add("1");
//		change(ls);
//		
//		for(String s : ls)
//		{
//			System.out.println(s);
//		}
		
		
//		HashMap<String, Integer> m = new HashMap<>();
//		m.put(null, 2);
//		
//		HashMap<String, Integer> m1 = m;
//		m1.put("2", 3);
//		m.clear();
//		
//		System.out.println(m.size());
//		System.out.println(m1.size());
		
		ArrayList<Set<String>> pt = new ArrayList<Set<String>>();
		ArrayList<Set<String>> pt1 = new ArrayList<Set<String>>();
		
		Set<String> set = new HashSet<String>();
		set.add("1");
		pt.add(set);
		Set<String> set2 = new HashSet<String>();
		set2.add("3");
		pt.add(set2);
		
		pt1.addAll(pt);
		
		set.add("2");
		set.add("5");
		
		for(Set<String> s:pt1)
		{
			System.out.println(s);
		}
	}
	
}
