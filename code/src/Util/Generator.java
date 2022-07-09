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

public abstract class Generator {
	protected ArrayList<Sdsp> dsp = null;
	protected Trie trie = null;
	protected int canNum = 0;
	protected int superCanNum = 0;
	protected int posNum;
	protected int negNum;
	protected Concept concept = null;

	public Generator(Concept concept) {
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
	
	
	protected void checkElem(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions)
	{
		for(String elem:positions.keySet())
		{
			List<String> sim = new ArrayList<String>();
			sim.add(elem);
			HashMap<Integer, BitSet> posBs = getGroupBitSet(sim, positions, 0);
			double posSup = posBs.size() / (double) this.posNum;
			if( posSup < Configure.ALPHA )
			{
				// the support in the D+ < alpha, prune it
				continue;
			}
			HashMap<Integer, BitSet> negBs = getGroupBitSet(sim, positions, 1);
			
			double negSup = negBs.size() / (double) this.negNum;
			if( negSup <= Configure.BETA)
			{
				ArrayList<String> pt = new ArrayList<String>();
				pt.add(elem);
				if(!this.trie.query(pt))
				{
					this.trie.insert(pt, posSup, negSup, 0);
				}
				// the subset of this set is pruned
				continue;
			}
			patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, 0), 0);
			patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, 0), 1);
		}
	}
	
	
	
	protected void checkSimGroup(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions)
	{
		CNode root = this.concept.getRoot();
		checkSimGroupHelper(patternMap, positions, root);
	}
	
	private List<String> checkSimGroupHelper(PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions, CNode root)
	{
		List<String> sim = new ArrayList<String>();
		if(root.child.isEmpty())
		{
			root.priority = 1;
			sim.add(root.value);
			checkSupport(sim, root, patternMap, positions, concept);
			
		}
		else
		{
			int priority = 0;
			for(CNode cn:root.child)
			{
				List<String> ls = checkSimGroupHelper(patternMap, positions, cn);
				priority += cn.priority;
				sim.addAll(ls);
			}
			if(root.parent != null)
			{
				root.priority += priority;
				checkSupport(sim, root, patternMap, positions, concept);
			}
		}
		return sim;
	}
	
	
	protected boolean checkSupport(List<String> sim, CNode node, PatternMap patternMap, HashMap<String, List<HashMap<Integer, BitSet>>> positions, Concept concept)
	{
		canNum++;
		String elem = node.value;
		if(elem == null)
			return false;
		HashMap<Integer, BitSet> posBs = getGroupBitSet(sim, positions, 0);
		double posSup = posBs.size() / (double) this.posNum;
		if( posSup < Configure.ALPHA )
		{
			// the support in the D+ < alpha, prune it
			return false;
		}
		HashMap<Integer, BitSet> negBs = getGroupBitSet(sim, positions, 1);
		
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
		patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, node.priority), 0);
		patternMap.put(new PatternKey(null, 0), new Candidate(elem, posBs, negBs, node.priority), 1);
		
		return true;
	}
	
	private HashMap<Integer, BitSet> getGroupBitSet(List<String> sim, HashMap<String, List<HashMap<Integer, BitSet>>> positions, int cls)
	{
		HashMap<Integer, BitSet> bsmap = new HashMap<Integer, BitSet>();
		for(String str : sim)
		{
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
			{
				System.out.println(sim+"************no key");
			}
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
	
	
	public static void rightShift(BitSet b, int[] gap){
		int count = b.cardinality();
		int length = b.length();
		int index = length-1;
		for (int i = count; i > 0; i--) {
			index = b.previousSetBit(index);
			for(int j = gap[0]; j <= gap[1]; j++)
			{
				b.set(index+j+1);
			}
			b.clear(index);
		}
	}
	
	public int getCanNum()
	{
		return this.canNum;
	}
}
