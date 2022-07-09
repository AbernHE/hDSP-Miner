package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class PatternMap {
	private HashMap<PatternKey, ArrayList<ArrayList<Candidate>>> map = null;
	
	public PatternMap()
	{
		this.map = new HashMap<PatternKey, ArrayList<ArrayList<Candidate>>>();
	}
	
	
	public void put(PatternKey key, Candidate value, int cls)
	{
		if(!map.containsKey(key))
		{
			ArrayList<ArrayList<Candidate>> val = new ArrayList<ArrayList<Candidate>>();
			val.add(new ArrayList<Candidate>());
			val.add(new ArrayList<Candidate>());
			map.put(key, val);
		}
		
		if(cls < 0 || cls > 1)
		{
			System.out.println("PatternMap: The value of d excced the range!");
		}
		else
		{
			map.get(key).get(cls).add(value);
		}
	}
	
	// get a new pattern by key, left, right
	public ArrayList<Candidate> get(PatternKey key, int cls)
	{
		ArrayList<Candidate> candidates = null;
		if(this.map.containsKey(key))
		{
			if(cls >= 0 && cls < 2)
				candidates =  this.map.get(key).get(cls);
		}
		
		return candidates;
	}
	
	public Set<PatternKey> keySet()
	{
		return this.map.keySet();
	}
	
	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}
	
	public ArrayList<ArrayList<Candidate>> remove(PatternKey key)
	{
		return this.map.remove(key);
	}
	
	public void removeLeft(PatternKey key)
	{
		this.map.get(key).get(0).clear();
	}
	
	public boolean contrainKey(PatternKey key)
	{
		return this.map.containsKey(key);
	}
	
}
