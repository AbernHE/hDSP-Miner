package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WidthDepths extends Generator{
	private PatternMap patternMap = null;
	private HashMap<String, Candidate> map = null;

	public WidthDepths(Concept concept) {
		super(concept);
		this.map = new HashMap<String, Candidate>();
	}

	@Override
	public void genPattern(Sequence sequence) throws ClassNotFoundException, IOException {
		System.out.println("-----------WidthDepth------genPattern----------");
		HashMap<String, List<HashMap<Integer, BitSet>>> positions = sequence.getPositions();
		this.posNum = sequence.getPosNum();
		this.negNum = sequence.getNegNum();
		
		patternMap = new PatternMap();
		
		// generate the length-1 pattern
		checkSimGroup(patternMap, positions);
		ArrayList<Candidate> elemList = patternMap.get(new PatternKey(null, 0), 0);
		
		for(Candidate c:elemList)
		{
			this.map.put(c.cand, c);
		}
		
		int hi = 0; // pattern length
		boolean depth = false;
		
		while(!patternMap.isEmpty()&&!depth)
		{
//			if(Tool.checkMemory(Configure.MEMORY))
//			{
//				depth = true;
//			}
			if(hi == Configure.LEVEL)
				depth = true;
			Set<PatternKey> keys = patternMap.keySet();
			System.out.println("Level:"+hi);
			ArrayList<PatternKey> keyList = new ArrayList<>();
			for(PatternKey key : keys)
			{
				keyList.add(key);
			}
				
			keyList.sort(new Comparator<PatternKey>() {
				@Override
				public int compare(PatternKey o1, PatternKey o2) {
					return o2.getLen() - o1.getLen();
				}
			});
			
			for(PatternKey key : keyList)
			{
				ArrayList<Candidate> lefts = patternMap.get(key, 0);
				ArrayList<Candidate> rights = patternMap.get(key, 1);
				
				if(rights.size() == 0 || (lefts.size() == 0) && !depth)
				{
					patternMap.remove(key);
					continue;
				}
				for(Candidate left : lefts)
				{
					HashMap<Integer, BitSet> lPos = left.posBitset;
					HashMap<Integer, BitSet> lNeg = left.negBitset;
					BitSet used = new BitSet();
					for(int index = 0 ; index < rights.size(); )
					{
						// check new pattern
						if(used.get(index))
						{
							index ++;
							continue;
						}
						used.set(index);
						
						canNum++;
						Candidate right = rights.get(index);
						
						List<String> pt = new ArrayList<String>();
						pt.add(left.cand);
						List<String> pt2 = new ArrayList<>();
						if(key.getPattern() != null)
							pt2= key.getPattern();
						pt.addAll(pt2);
						List<String> pt0 = new ArrayList<String>();
						pt0.addAll(pt);
						pt0.add(right.cand);
						
						if(this.trie.query(pt0))
						{
							index++;
							continue;
						}
						
						
						HashMap<Integer, BitSet> rPos = right.posBitset;
						Set<Integer> inSet = new HashSet<>();
						for(int i : lPos.keySet())
							inSet.add(i);
						inSet.retainAll(rPos.keySet());  // intersect of left and right patterns
						if(inSet.size() / (double) this.posNum < Configure.ALPHA)
						{
							// if the intersect of two patterns < alpha
							index ++;
							for(int j = index; j < rights.size(); j++)
							{
								if(concept.query(right.cand, rights.get(j).cand))
									used.set(j);
							}
							continue;
						}
						HashMap<Integer, BitSet> posNewBitSet = getNewBitSet(inSet, rPos, lPos);
						double posSup = posNewBitSet.size() / (double) this.posNum;
						if(posSup < Configure.ALPHA)
						{
							// alpha prune
							index ++;
							for(int j = index; j < rights.size(); j++)
							{
								if(concept.query(right.cand, rights.get(j).cand))
									used.set(j);
							}
							continue;
						}
						
						//negative dataset
						HashMap<Integer, BitSet> rNeg = right.negBitset;
						inSet.clear();
						for(int i : lNeg.keySet())
							inSet.add(i);
						inSet.retainAll(rNeg.keySet());
						HashMap<Integer, BitSet> negNewBitSet = getNewBitSet(inSet, rNeg, lNeg);
						double negSup = negNewBitSet.size() / (double) this.negNum;
//						List<Set<String>> pt = new ArrayList<Set<String>>();
//						pt.add(left.cand);
//						List<Set<String>> pt2 = new ArrayList<>();
//						if(key != null)
//							pt2= Tool.deepCopy(key.getPattern());
//						pt.addAll(pt2);
						int llen = this.concept.getNode(left.cand).priority;
						int rlen = this.concept.getNode(right.cand).priority;
						if(negSup <= Configure.BETA)
						{
//							if(!this.trie.query(pt))
							{
								this.trie.insert(pt0, posSup, negSup, key.getLen() + llen + rlen);
							}
							
							index ++;
							for(int j = index; j < rights.size(); j++)
							{
								if(concept.query(right.cand, rights.get(j).cand))
									used.set(j);
							}
							continue;
						}
						else
						{
							if(depth)
							{
								pt.add(right.cand);
								Pattern pattern = new Pattern(pt, posNewBitSet, negNewBitSet, key.getLen() + llen + rlen);
								depthHelper(pattern, hi);
							}
							else
							{
								
								// the candidate pattern
								patternMap.put(new PatternKey(pt, key.getLen() + llen), new Candidate(right.cand, posNewBitSet, negNewBitSet, right.priority), 1);
								List<String> pt3 = new ArrayList<String>();
								pt3.addAll(pt2);
								pt3.add(right.cand);
								patternMap.put(new PatternKey(pt3, key.getLen() + rlen), new Candidate(left.cand, posNewBitSet, negNewBitSet, right.priority), 0);
							}
						}
					}
				}
				Tool.checkMemory(Configure.MEMORY);
				if(!depth)
					patternMap.remove(key); // remove the unused patterns
				else
					patternMap.removeLeft(key);
			}
			if(!depth)
				hi++;
		}
		
		
	}
	
	private void depthHelper(Pattern pattern, int h)
	{
		Tool.checkMemory(0);
		int len = pattern.getPattern().size();
		PatternKey key = new PatternKey(pattern.getPattern().subList(len - h, len), 0);
		List<Candidate> follow = new ArrayList<>();
		if(h == 0)
		{
			key = null;
		}
		if(patternMap.contrainKey(key))
		{
			follow = patternMap.get(key, 1);
		}
		HashMap<Integer, BitSet> pos = pattern.getPosBitset();
		HashMap<Integer, BitSet> neg = pattern.getNegBitset();
		BitSet used = new BitSet();
		Set<Integer> inSet = new HashSet<>();
		HashMap<Integer, BitSet> negNewBitSet = null;
		HashMap<Integer, BitSet> posNewBitSet = null;
//		for(Candidate c:follow)
		for(int index = 0; index < follow.size(); )
		{
			if(used.get(index))
			{
				index ++;
				continue;
			}
			canNum++;
			Candidate c = follow.get(index);
			Candidate e = this.map.get(c.cand);
			HashMap<Integer, BitSet>  p = e.posBitset;
			//check new pattern
			inSet.clear();
			for(int i : pos.keySet())
				inSet.add(i);
			inSet.retainAll(p.keySet());
			posNewBitSet = getNewBitSet(inSet, p, pos);
			double posSup = posNewBitSet.size() / (double) this.posNum;
			if(posSup < Configure.ALPHA)
			{
				index ++;
				for(int j = index; j < follow.size(); j++)
				{
					if(this.concept.query(c.cand, follow.get(j).cand))
						used.set(j);
				}
				continue;
			}
			
			HashMap<Integer, BitSet>  n = e.negBitset;
			inSet.clear();
			for(int i : neg.keySet())
				inSet.add(i);
			inSet.retainAll(n.keySet());
			negNewBitSet = getNewBitSet(inSet, n, neg);
			
			double negSup = negNewBitSet.size() / (double) this.negNum;
			
			int plen = pattern.getLen();
			int clen = concept.getNode(c.cand).priority;
			if(negSup <= Configure.BETA)
			{
				try {
					List<String> pt = Tool.deepCopy(pattern.getPattern());
					pt.add(c.cand);
					if(!this.trie.query(pt))
					{
						this.trie.insert(pt, posSup, negSup, plen+clen);
					}
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				index ++;
				for(int j = index; j < follow.size(); j++)
				{
					if(this.concept.query(c.cand, follow.get(j).cand))
						used.set(j);
				}
				continue;
			}
			else
			{
				int l = pattern.getPattern().size();
				pattern.getPattern().add(c.cand);
				pattern.setAtr(posNewBitSet, negNewBitSet, plen+clen);
				depthHelper(pattern, h);
				pattern.setAtr(pos, neg, plen);
				pattern.getPattern().remove(l);
			}
			negNewBitSet.clear();
			posNewBitSet.clear();
			index ++;
		}
	}

	@Override
	public void solution() {
		System.out.println("-----------------solution----------");
		ArrayList<Sdsp> sdsp = this.trie.traver();
		System.out.println("candidate solutions:" + sdsp.size());
		this.trie = new Trie(concept);
		// sort by pattern length then alphabet size
		sdsp.sort(new Comparator<Sdsp>() {
			@Override
			public int compare(Sdsp o1, Sdsp o2) {
				if(o1.pattern.size() == o2.pattern.size())
					return o2.GetLen() - o1.GetLen();
				else
					return o1.pattern.size() - o2.pattern.size();
			}
		});
		
		// check the minimal
		for(Sdsp d:sdsp)
		{
			if(!this.trie.query(d.pattern))
			{
				this.trie.insert(d.pattern, d.posSup, d.negSup,d.len);
			}
		}
		
		this.dsp = this.trie.traver();
		
	}

}
