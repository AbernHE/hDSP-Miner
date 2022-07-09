package Util;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class Sequence {
	private HashMap<String, List<HashMap<Integer, BitSet>>> positions;
	private int posNum = 0;
	private int negNum = 0;
	
	public Sequence(HashMap<String, List<HashMap<Integer, BitSet>>> positions, int posNum, int negNum) {
		super();
		this.positions = positions;
		this.posNum = posNum;
		this.negNum = negNum;
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
	
	
}
