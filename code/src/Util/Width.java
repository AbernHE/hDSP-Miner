package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Width extends Generator{
	
	public Width(Concept concept) {
		super(concept);
	}
	
	@Override
	public void genPattern(Sequence sequence) throws ClassNotFoundException, IOException
	{
		System.out.println("-----------Width------genPattern----------");
		HashMap<String, List<HashMap<Integer, BitSet>>> positions = sequence.getPositions();
		this.posNum = sequence.getPosNum();
		this.negNum = sequence.getNegNum();
		
		PatternMap patternMap = new PatternMap();
		
		
		// generate the length-1 pattern
		checkSimGroup(patternMap, positions);
		
		int hi = 0;
		
		// width first enumeration
		while(!patternMap.isEmpty())
		{
			Set<PatternKey> keys = patternMap.keySet();
			System.out.println("Level:"+hi);
			ArrayList<PatternKey> keyList = new ArrayList<>();
			for(PatternKey key : keys)
				keyList.add(key);
			keyList.sort(new Comparator<PatternKey>() {
				@Override
				public int compare(PatternKey o1, PatternKey o2) {
					return o2.getLen() - o1.getLen();
				}
			});
//			System.out.println("keys size:"+keyList.size());
//			Tool.printKey("keys:", keyList);
			for(PatternKey key : keyList)
			{
				
				ArrayList<Candidate> lefts = patternMap.get(key, 0);
				ArrayList<Candidate> rights = patternMap.get(key, 1);
				
				if(lefts.size() == 0 || rights.size() == 0)
				{
					patternMap.remove(key);
					continue;
				}
//				System.out.println("key:"+(key == null ? key:key.getPattern()));
				Tool.printCandidate("lefts:",lefts);
				Tool.printCandidate("rights:",rights);
				for(Candidate left : lefts)
				{
					HashMap<Integer, BitSet> lPos = left.posBitset;
					HashMap<Integer, BitSet> lNeg = left.negBitset;
					for(Candidate right : rights)
					{
						// check new pattern
						HashMap<Integer, BitSet> rPos = right.posBitset;
						Set<Integer> inSet = new HashSet<>();
						for(int i : lPos.keySet())
							inSet.add(i);
						inSet.retainAll(rPos.keySet());  // intersect of left and right patterns
						if(inSet.size() / (double) this.posNum < Configure.ALPHA)
						{
							// if the intersect of two patterns < alpha
							continue;
						}
						HashMap<Integer, BitSet> posNewBitSet = getNewBitSet(inSet, rPos, lPos);
						double posSup = posNewBitSet.size() / (double) this.posNum;
						if(posSup < Configure.ALPHA)
						{
							// alpha prune
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
						List<String> pt = new ArrayList<String>();
						pt.add(left.cand);
						List<String> pt2 = new ArrayList<>();
						if(key.getPattern() != null)
							pt2= Tool.deepCopy(key.getPattern());
						pt.addAll(pt2);
						int llen = this.concept.getNode(left.cand).priority;
						int rlen = this.concept.getNode(right.cand).priority;
						if(negSup <= Configure.BETA)
						{
							pt.add(right.cand);
//							Sdsp sdsp = new Sdsp(pt, posSup, negSup);
//							dsp.add(sdsp);
							if(!this.trie.query(pt))
							{
								this.trie.insert(pt, posSup, negSup, key.getLen() + llen +rlen);
							}
						}
						else
						{
							// the candidate pattern
							patternMap.put(new PatternKey(pt, key.getLen() + llen), new Candidate(right.cand, posNewBitSet, negNewBitSet, right.priority), 1);
							List<String> pt3 = Tool.deepCopy(pt2);
							pt3.add(right.cand);
							patternMap.put(new PatternKey(pt3, key.getLen() + rlen), new Candidate(left.cand, posNewBitSet, negNewBitSet, right.priority), 0);
						}
					}
				}
				patternMap.remove(key); // remove the unused patterns
				
			}
			hi++;
		}
	}

	@Override
	public void solution() {
		System.out.println("-----------------solution----------");
		this.dsp = trie.traver();
		trie = null;
	}
	
	
}
