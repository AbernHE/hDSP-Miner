package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Util.Configure.Traval;

public class SimGroup {
	private ArrayList<Set<String>> initSimGroup = null;
	
	public SimGroup(Set<String> alphabet, String simGroupFile)
	{
		if(Configure.TRAVER == Traval.DSP)
			this.initSimGroup = getSingle(alphabet);
		else
			this.initSimGroup = getInitSimGroup(alphabet, simGroupFile);
	}
	
	public ArrayList<Set<String>> getInitSimGroup() {
		return initSimGroup;
	}
	
	

	
	private ArrayList<Set<String>> getInitSimGroup(Set<String> alphabet, String simGroupFile)
	{
		ArrayList<Set<String>> simList = new ArrayList<Set<String>>();
		HashMap<String, ArrayList<String>> sg = Tool.getSimGroupFromFile(simGroupFile);
		
		for(String k : sg.keySet())
		{
			Set<String> l = new HashSet<>();
			ArrayList<String> sls = sg.get(k);
			for(String s : sls){
				if(alphabet.contains(s)){
					l.add(s);
				}
			}
			if(!l.isEmpty())
				simList.add(l);
		}
		
		return simList;
		
	}
	
	
	private ArrayList<Set<String>> getSingle(Set<String> alphabet)
	{
		ArrayList<Set<String>> simList = new ArrayList<Set<String>>();
		
		for(String s : alphabet)
		{
			Set<String> l = new HashSet<>();
			l.add(s);
			if(!l.isEmpty())
				simList.add(l);
		}
		
		return simList;
		
	}
	
}
