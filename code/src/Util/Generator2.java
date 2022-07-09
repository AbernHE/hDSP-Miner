package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public abstract class Generator2 {
	protected ArrayList<Sdsp> dsp = null;
	protected Trie trie = null;
	protected int canNum = 0;
	protected int superCanNum = 0;
	protected int posNum;
	protected int negNum;
	protected Concept concept = null;

	public Generator2(Concept concept) {
		dsp = new ArrayList<>();
		this.concept = concept;
		trie = new Trie(concept);
		
	}
	
	
	
	public abstract void genPattern(Sequence sequence) throws ClassNotFoundException, IOException;
	
	public ArrayList<Sdsp> getDsp() {
		return dsp;
	}
	
	// get the solutions
	public abstract void solution();
	
	// sort sdsp by contrast degree
	public void sort()
	{
		this.dsp.sort(new Comparator<Sdsp>() {
			@Override
			public int compare(Sdsp o1, Sdsp o2) {
				double contrast = (o2.posSup - o2.negSup) - (o1.posSup - o1.negSup);
				if(contrast > 0)
					return 1;
				else if(contrast < 0)
					return -1;
				else
					return 0;
			}
		});
	}
	
//	protected void checkSimGroup(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions)
//	{
//		Queue<String> queue = new LinkedList<String>();
//		for(String leaf : positions.keySet())
//		{
//			queue.add(leaf);
//		}
//		System.out.println(positions.keySet());
//		while(!queue.isEmpty())
//		{
//			String elem = queue.poll();
////			if(!elem.contains("e"))
////			{
////				System.out.println(elem);
////			}
//			System.out.println("elem:"+elem);
//			CNode node = concept.getNode(elem);
//			if(node == null)
//				continue;
//			checkSupport(node, patternMap, positions, concept);
//			CNode parent = node.parent;
//			if(parent == null)
//				continue;
//			parent.degree--;
//			if(node.priority == 0)
//				node.priority = 1;
//			parent.priority += node.priority;
//			if(parent.degree == 0)
//			{
//				if(parent.value != null)
//				{
//					queue.offer(parent.value);
//				}
//			}
//		}
//	}
//	
	
	protected void checkSimGroup(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions)
	{
		System.out.println(positions.keySet());
		CNode root = this.concept.getRoot();
		checkSimGroupHelper(patternMap, positions, root);
	}
	
	private void checkSimGroupHelper(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions, CNode root)
	{
//		System.out.println("value:"+root.value);
		if(root.child.isEmpty())
		{
			root.priority = 1;
			checkSupport(root, patternMap, positions, concept);
		}
		else
		{
			int priority = 0;
			for(CNode cn:root.child)
			{
				checkSimGroupHelper(patternMap, positions, cn);
				priority += cn.priority;
			}
			if(root.parent != null)
			{
				root.priority += priority;
				checkSupport(root, patternMap, positions, concept);
			}
		}
	}
	
	
	protected boolean checkSupport(CNode node, PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions, Concept concept)
	{
		canNum++;
		String elem = node.value;
		if(elem == null)
			return false;
//		System.out.print("elem:"+elem+"\t");
		HashMap<Integer, BitSet> posBs = getGroupBitSet(node, positions, 0);
//		System.out.println(posBs.size());
		double posSup = posBs.size() / (double) this.posNum;
		if( posSup < Configure.ALPHA )
		{
			// the support in the D+ < alpha, prune it
			return false;
		}
		HashMap<Integer, BitSet> negBs = getGroupBitSet(node, positions, 1);
		
		double negSup = negBs.size() / (double) this.negNum;
		if( negSup <= Configure.BETA)
		{
			ArrayList<String> pt = new ArrayList<String>();
			pt.add(elem);
			if(!this.trie.query(pt))
			{
				this.trie.insert(pt, posSup, negSup, node.priority);
			}
			// the subset of this set is pruned
			return false;
		}
		List<HashMap<Integer, BitSet>> ls = new ArrayList<HashMap<Integer, BitSet>>();
		ls.add(posBs);
		ls.add(negBs);
		positions.put(elem, ls);
		patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, node.priority), 0);
		patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, node.priority), 1);
		
		return true;
	}
	
	private HashMap<Integer, BitSet> getGroupBitSet(CNode node, HashMap<String, List<HashMap<Integer, BitSet>>> positions, int cls)
	{
		HashMap<Integer, BitSet> bsmap = new HashMap<Integer, BitSet>();
		
		if(node.child.isEmpty())
		{
//			System.out.println(node.value);
			return positions.get(node.value).get(cls);
		}
		for(CNode n : node.child)
		{
			String str = n.value;
			if(positions.containsKey(str))
			{
				HashMap<Integer, BitSet> pos = positions.get(str).get(cls);
				for(int id : pos.keySet())
				{
					BitSet pb = (BitSet) pos.get(id).clone();
					if(bsmap.containsKey(id))
					{
						bsmap.get(id).or(pb);
					}
					else
					{
						bsmap.put(id, pb);
					}
				}
			}
			else
				System.out.println(str);
		}
		return bsmap;
	}
	
	protected HashMap<Integer, BitSet> getNewBitSet(Set<Integer> inSet, HashMap<Integer, BitSet> r, HashMap<Integer, BitSet> l)
	{
		HashMap<Integer, BitSet> newBitSet = new HashMap<Integer, BitSet>();
		for(int id : inSet)
		{
			BitSet lb = l.get(id);
			BitSet rb = r.get(id);
			BitSet b = (BitSet) lb.clone();
			rightShift(b, Configure.GAP);
			b.and(rb);
			if(!b.isEmpty())
			{
				newBitSet.put(id, b);
			}
		}
		return newBitSet;
	}
	
	
	protected void rightShift(BitSet b, int[] gap){
		int count = b.cardinality();
		int length = b.length();
		int index = length-1;
		for (int i = count; i > 0; i--) {
			index = b.previousSetBit(index);
			for(int j = gap[0]; j <= gap[1]; j++)
			{
				b.set(index+j);
			}
			b.clear(index);
		}
	}
	
	public int getCanNum()
	{
		return this.canNum;
	}
}
