package Util;

import java.util.List;
import java.util.Set;

public class Sdsp {
	public List<String> pattern;
	public double posSup;
	public double negSup;
	int len = 0;
	
	public Sdsp(List<String> pattern, double posSup, double negSup, int len) {
		super();
		this.pattern = pattern;
		this.posSup = posSup;
		this.negSup = negSup;
		this.len = len;
	}
	public int GetLen()
	{
		return len;
	}
	
}
