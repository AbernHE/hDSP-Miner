package Util;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Pattern {
	private List<String> pattern = null;
	private HashMap<Integer, BitSet> posBitset;
	private HashMap<Integer, BitSet> negBitset;
	private int len;
	
	public Pattern(List<String> pattern, HashMap<Integer, BitSet> posBitset,
			HashMap<Integer, BitSet> negBitset, int len) {
		super();
		this.pattern = pattern;
		this.posBitset = posBitset;
		this.negBitset = negBitset;
		this.len = len;
	}

	public List<String> getPattern() {
		return pattern;
	}

	public HashMap<Integer, BitSet> getPosBitset() {
		return posBitset;
	}

	public HashMap<Integer, BitSet> getNegBitset() {
		return negBitset;
	}
	
	public void setAtr(HashMap<Integer, BitSet> pos, HashMap<Integer, BitSet> neg, int len)
	{
		this.posBitset = pos;
		this.negBitset = neg;
		this.len = len;
	}
	
//	public void removeLast()
//	{
//		this.pattern.remove(this.pattern.size()-1);
//	}
	
	public int getLen()
	{
		return len;
	}
	
}
