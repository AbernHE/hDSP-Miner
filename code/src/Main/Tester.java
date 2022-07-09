package Main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Tester {
	
	public static <T> ArrayList<T> deepCopy(ArrayList<T> src) throws IOException, ClassNotFoundException {  
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(byteOut);  
        out.writeObject(src);  
  
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
        ObjectInputStream in = new ObjectInputStream(byteIn);  
        @SuppressWarnings("unchecked")  
        ArrayList<T> dest = (ArrayList<T>) in.readObject();  
        return dest;  
    }  

	public static void main(String[] args){
		
		
		
//		ArrayList<String> ls = new ArrayList<String>(2);
//		ls.add("1");
//		ls.add("2");
//		ls.add("3");
//		System.out.println(ls.size());
		
		
//		HashMap<String, Set<String>> map = new HashMap<>();
//		
//		ArrayList<Set<String>> pt = new ArrayList<Set<String>>();
//		
//		Set<String> set = new HashSet<String>();
//		set.add("1");
//		pt.add(set);
//		
//		map.put("1", set);
//		
//		
//		Set<String> set2 = new HashSet<String>();
//		set2.add("1");
//		pt.add(set2);
//		
//		ArrayList<Set<String>> pt1 = new ArrayList<Set<String>>();
//		pt1.add(set);
//		
//		map.remove("1");
//		
////		System.out.println(map.size());
////		System.out.println(pt1);
//		
//		String[] ls = set.toArray(new String[0]);
//		//System.out.println(new String[0]);
//		for(String s: ls)
//			System.out.println(s);
		
//		
//		ArrayList<Set<String>> pt2 = deepCopy(pt);
////		
////		PatternKey pk = new PatternKey(pt);
////		
////		PatternKey pk2 = (PatternKey) pk.clone();
////		
//		set.add("2");
//		
//		Set<String> set2 = new HashSet<String>();
//		set2.add("3");
//		pt2.add(set2);
//		
//		System.out.println(pt2);
//		System.out.println(pt);
		
//		HashMap<String, Integer> m = new HashMap<>();
//		m.put("2", 2);
//		
//		HashMap<String, Integer> m1 = m;
//		m.put("3", 3);
//		
//		Set<String> set = m.keySet();
//		Set<String> s = new HashSet<>();
//		s.addAll(set);
//		System.out.println(s);
//		s.add("4");
//		System.out.println(s);
//		System.out.println(m.keySet());
		
//		System.out.println(pt);
	}

}
