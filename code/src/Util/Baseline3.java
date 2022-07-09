package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Baseline3 extends Generator{
	
	public Baseline3(Concept concept) {
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
		if(Configure.dsp)
		{
			checkElem(patternMap, positions);
			System.out.println("-------------->DSP");
		}
		else
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
				lefts.sort(new Comparator<Candidate>() {
					@Override
					public int compare(Candidate o1, Candidate o2) {
						return o2.priority - o1.priority;
					}
				});
				rights.sort(new Comparator<Candidate>() {
					@Override
					public int compare(Candidate o1, Candidate o2) {
						return o2.priority - o1.priority;
					}
				});
//				for(Candidate c:rights)
//				{
//					System.out.print(c.cand+": "+c.priority+"\t");
//				}
//				System.out.println();
//				System.out.println("key:"+(key == null ? key:key.getPattern()));
//				Tool.printCandidate("lefts:",lefts);
//				Tool.printCandidate("rights:",rights);
				for(Candidate left : lefts)
				{
					HashMap<Integer, BitSet> lPos = left.posBitset;
					HashMap<Integer, BitSet> lNeg = left.negBitset;
					BitSet used = new BitSet();
					for(int index = 0 ; index < rights.size(); )
					{
						// check new pattern
						Candidate right = rights.get(index);
//						System.out.println(""+left.cand+key.getPattern()+right.cand);
						if(used.get(index))
						{
							index ++;
							continue;
						}
						used.set(index);
						Tool.checkMemory(0);
						canNum ++;
						
//						List<String> pt = new ArrayList<String>();
//						pt.add(left.cand);
//						List<String> pt2 = new ArrayList<>();
//						if(key.getPattern() != null)
//							pt2= key.getPattern();
//						pt.addAll(pt2);
//						List<String> pt0 = new ArrayList<String>();
//						pt0.addAll(pt);
//						pt0.add(right.cand);
						
//						if(this.trie.query(pt0))
//						{
//							index++;
//							continue;
//						}
						
						HashMap<Integer, BitSet> rPos = right.posBitset;
						Set<Integer> inSet = new HashSet<>();
						for(int i : lPos.keySet())
							inSet.add(i);
						inSet.retainAll(rPos.keySet());  // intersect of left and right patterns
						if(inSet.size() / (double) this.posNum < Configure.ALPHA)
						{
							// if the intersect of two patterns < alpha
							index ++;
							/*for(int j = index; j < rights.size(); j++)
							{
								if(concept.query(right.cand, rights.get(j).cand))
									used.set(j);
							}*/
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
						List<String> pt = new ArrayList<String>();
						pt.add(left.cand);
						List<String> pt2 = new ArrayList<String>();
						if(key.getPattern() != null)
							pt2= key.getPattern();
						pt.addAll(pt2);
//						System.out.println(left.cand+"--"+right.cand);
						int llen = this.concept.getNode(left.cand).priority;
						int rlen = this.concept.getNode(right.cand).priority;
						
						if(negSup <= Configure.BETA)
						{
							pt.add(right.cand);
//							Sdsp sdsp = new Sdsp(pt, posSup, negSup);
//							dsp.add(sdsp);
							if(!this.trie.query(pt))
							{
								this.trie.insert(pt, posSup, negSup, key.getLen() + llen + rlen);
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
							// the candidate pattern
							patternMap.put(new PatternKey(pt, key.getLen() + llen), new Candidate(right.cand, posNewBitSet, negNewBitSet, right.priority), 1);
							List<String> pt3 = new ArrayList<String>();
							pt3.addAll(pt2);
							pt3.add(right.cand);
							patternMap.put(new PatternKey(pt3, key.getLen() + rlen), new Candidate(left.cand, posNewBitSet, negNewBitSet, right.priority), 0);
						}
						Tool.checkMemory(0);
						index ++;
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
		
		
		/*System.out.println("-----------------solution----------");
		ArrayList<Sdsp> sdsp = this.trie.traver();
		System.out.println("super candidate:"+sdsp.size());
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
				this.trie.insert(d.pattern, d.posSup, d.negSup, d.len);
			}
		}
		
		this.dsp = this.trie.traver();*/
	}
	
	
}
