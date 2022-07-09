package Util;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Set;

public class Candidate {
	public String cand;
	public HashMap<Integer, BitSet> posBitset;
	public HashMap<Integer, BitSet> negBitset;
	public int priority = 0;
	
	public Candidate(String c, HashMap<Integer, BitSet> posBitset, HashMap<Integer, BitSet> negBitset, int priority) {
		super();
		this.cand = c;
		this.posBitset = posBitset;
		this.negBitset = negBitset;
		this.priority = priority;
	}
}
