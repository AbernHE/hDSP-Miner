package Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CNode {
	public List<CNode> child = null;
	public CNode parent = null;
	public String value = null;
	public int degree = 0;
	public int priority = 0;
	
	public CNode(String value)
	{
		this.value = value;
		this.child = new ArrayList<CNode>();
	}
}
