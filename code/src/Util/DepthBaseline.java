package Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepthBaseline extends Generator {

	private ArrayList<Candidate> elemList;
	private HashMap<String, Candidate> map = null;

	public DepthBaseline(Concept concept) {
		super(concept);
		this.map = new HashMap<String, Candidate>();
	}

	@Override
	public void genPattern(Sequence sequence) throws ClassNotFoundException, IOException {
		System.out.println("-----------DepthBaseline------genPattern----------");
		HashMap<String, List<HashMap<Integer, BitSet>>> positions = sequence.getPositions();
		this.posNum = sequence.getPosNum();
		this.negNum = sequence.getNegNum();

		// ArrayList<Set<String>> sg = simGroup.getInitSimGroup();
		PatternMap patternMap = new PatternMap();

		// generate the length-1 pattern
		checkSimGroup(patternMap, positions);

		this.elemList = patternMap.get(new PatternKey(null, 0), 0);

		for (Candidate c : elemList) {
			this.map.put(c.cand, c);
		}

		for (Candidate c : elemList) {
			ArrayList<String> pt = new ArrayList<>();
			pt.add(c.cand);
			int len = concept.getNode(c.cand).priority;
			Pattern pattern = new Pattern(pt, c.posBitset, c.negBitset, len);
			depthHelper(pattern);
		}

	}

	private void depthHelper(Pattern pattern) {
		Tool.checkMemory(0);

		// System.out.println(pattern.getPattern().size());
		HashMap<Integer, BitSet> pos = pattern.getPosBitset();
		HashMap<Integer, BitSet> neg = pattern.getNegBitset();
		// BitSet used = new BitSet();
		// for(Candidate c : elemList)
		for (int index = 0; index < elemList.size();) {
			/*
			 * if(used.get(index)) { index ++; continue; }
			 */
			canNum++;
			// new pattern
			Candidate c = elemList.get(index);
			Candidate e = this.map.get(c.cand);
			HashMap<Integer, BitSet> p = e.posBitset;
			// check new pattern
			Set<Integer> inSet = new HashSet<>();
			for (int i : pos.keySet())
				inSet.add(i);
			inSet.retainAll(p.keySet());
			HashMap<Integer, BitSet> posNewBitSet = getNewBitSet(inSet, p, pos);
			double posSup = posNewBitSet.size() / (double) this.posNum;
			if (posSup < Configure.ALPHA) {
				index++;
				/*
				 * for(int j = index; j < elemList.size(); j++) {
				 * if(this.concept.query(c.cand, elemList.get(j).cand))
				 * used.set(j); }
				 */
				continue;
			}

			HashMap<Integer, BitSet> n = e.negBitset;
			inSet.clear();
			for (int i : neg.keySet())
				inSet.add(i);
			inSet.retainAll(n.keySet());
			HashMap<Integer, BitSet> negNewBitSet = getNewBitSet(inSet, n, neg);
			inSet.clear();
			inSet = null;
			double negSup = negNewBitSet.size() / (double) this.negNum;
			int plen = pattern.getLen();
			int clen = concept.getNode(c.cand).priority;
			if (negSup <= Configure.BETA) {
				try {
					List<String> pt = Tool.deepCopy(pattern.getPattern());
					pt.add(c.cand);
//					if (!this.trie.query(pt)) {
						this.trie.insert(pt, posSup, negSup, plen + clen);
//					}
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				index++;
				/*
				 * for(int j = index; j < elemList.size(); j++) {
				 * if(this.concept.query(c.cand, elemList.get(j).cand))
				 * used.set(j); }
				 */
				posNewBitSet.clear();
				negNewBitSet.clear();
				continue;
			} else {
				int len = pattern.getPattern().size();
				pattern.getPattern().add(c.cand);
				pattern.setAtr(posNewBitSet, negNewBitSet, plen + clen);
				depthHelper(pattern);
				pattern.setAtr(pos, neg, plen);
				pattern.getPattern().remove(len);
				posNewBitSet.clear();
				negNewBitSet.clear();
			}
			index++;
		}

	}

	@Override
	public void solution() {
		System.out.println("-----------------solution----------");
		ArrayList<Sdsp> sdsp = this.trie.traver();
		System.out.println("super candidate:" + sdsp.size());
		
		//check by enumerating
		for(int j, i = 0; i < sdsp.size(); i++)
		{
			
			for(j = 0; j < sdsp.size(); j++)
			{
				if(i == j)
					continue;
				if(checkCover(sdsp.get(j), sdsp.get(i)))
				{
					break;
				}
			}
			if(j >= sdsp.size())
				this.dsp.add(sdsp.get(i));
		}
		
		//  Heuristic 
		/*this.trie = new Trie(concept);

		// sort by pattern length then alphabet size
		sdsp.sort(new Comparator<Sdsp>() {
			@Override
			public int compare(Sdsp o1, Sdsp o2) {
				if (o1.pattern.size() == o2.pattern.size())
					return o2.GetLen() - o1.GetLen();
				else
					return o1.pattern.size() - o2.pattern.size();
			}
		});

		// check the minimal
		
		for (Sdsp d : sdsp) {
			if (!this.trie.query(d.pattern)) {
				this.trie.insert(d.pattern, d.posSup, d.negSup, d.len);
			}
		}

		this.dsp = this.trie.traver();*/
	}

	// check whether d1 cover d2
	private boolean checkCover(Sdsp d1, Sdsp d2)
	{
		if(d2.pattern.size() == 0)
			return false;
		if(checkCoverHelp(d1.pattern, d2.pattern))
		{
			return true;
		}

		return false;
	}
	private boolean checkCoverHelp(List<String> d1, List<String> d2)
	{
		if(d1.size() == 0)
			return true;
		for(int j = 0; j < d2.size(); j++)
		{
			if(this.concept.query(d1.get(0), d2.get(j)))
			{
				if(checkCoverHelp(d1.subList(1, d1.size()), d2.subList(j+1, d2.size())))
					return true;
			}
		}
		return false;
	}
}
