package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tool {
	
	public static long totalMemory = 0;
	
	public static ArrayList<String> readSeq(String fname){
		ArrayList<String> sequences = new ArrayList<>();
		String line = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(fname));
			while((line = br.readLine()) != null){
				sequences.add(line);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return sequences;
	}
	
	public static HashMap<String, ArrayList<String>> getSimGroupFromFile(String simGroupFile){
		HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
		BufferedReader br = null;
		String line = null;
		try{
			br = new BufferedReader(new FileReader(simGroupFile));
			while((line = br.readLine())!=null){
				String[] l = line.trim().split(Configure.splitGroup);
				String[] element = l[1].trim().split(Configure.splitSim);
				ArrayList<String> set = new ArrayList<String>();
				for(String e:element){
					if(e != "")
						set.add(e);
				}
				res.put(l[0].trim(), set);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	
	
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {  
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(byteOut);  
        out.writeObject(src);  
  
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
        ObjectInputStream in = new ObjectInputStream(byteIn);  
        @SuppressWarnings("unchecked")  
        List<T> dest = (List<T>) in.readObject();  
        return dest;
    } 
	
	public static <T> Set<T> deepCopySet(Set<T> src) throws IOException, ClassNotFoundException {  
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
        ObjectOutputStream out = new ObjectOutputStream(byteOut);  
        out.writeObject(src);
  
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
        ObjectInputStream in = new ObjectInputStream(byteIn);  
        @SuppressWarnings("unchecked")  
        Set<T> dest = (Set<T>) in.readObject();  
        return dest;  
    } 
	
	public static void printDsp(ArrayList<Sdsp> sdsp)
	{
		
		for(Sdsp dsp : sdsp)
		{
			System.out.print(dsp.pattern);
			System.out.print("\t posSup:"+dsp.posSup);
			System.out.print("\t negSup:"+dsp.negSup);
			System.out.println("\t contrast:"+(dsp.posSup-dsp.negSup));
		}
		System.out.println("sDSP Size:"+sdsp.size());
	}
	
	public static void redirectOut(String file)
	{
		PrintStream ps = null;
		try {
			ps = new PrintStream(new FileOutputStream(file, true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        System.setOut(ps); 
	}
	
	public static void printCandidate(String msg, ArrayList<Candidate> can)
	{
		System.out.print(msg+"=====>");
		for(Candidate c:can)
		{
			System.out.print(c.cand + ", ");
		}
		System.out.println();
	}
	
	public static void printKey(String msg, ArrayList<PatternKey> keyList)
	{
		System.out.print(msg+"=====>");
		for(PatternKey k:keyList)
		{
			if(k == null)
				System.out.print(k + ", ");
			else
				System.out.print(k.getPattern() + ", ");
		}
		System.out.println();
	}
	
	public static void log(String msg, Object obj){
		System.out.println(msg+"===========>"+obj);
	}
	
	public static boolean checkMemory(long memory)
	{
		long m = Runtime.getRuntime().totalMemory()/1024;
		totalMemory = totalMemory > m ? totalMemory : m;
		return  m > memory;
	}
	
	
	
	public static void calSimilarity(double thresh, String simFileName, Set<String> alphabet)
	{
		String[] alpha = alphabet.toArray(new String[0]);
		int size = alphabet.size();
		boolean m[][] = new boolean[size][size];
		for(int i = 0; i < size; i++)
		{
			String s1 = alpha[i];
			if(s1.equals("$"))
			{
				System.out.println();
			}
			for(int j = i; j < size; j++)
			{
				double ss = simScore(s1, alpha[j]);
				if(ss >= thresh){
					m[i][j] = true;
					m[j][i] = true; 
				}
			}
		}
		ArrayList<Set<String>> sim = new ArrayList<>();
		for(int i = 0; i < size; i++)
		{
			Set<String> set = new HashSet<String>();
			for(int j = 0; j < size; j++)
			{
				if(m[i][j])
				{
					set.add(alpha[j]);
				}
			}
			sim.add(set);
		}
		
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(simFileName));
			for(int i = 0; i < sim.size(); i++)
			{
				bw.write(alpha[i]);
				bw.write(":");
				int j = 0;
				for(String s:sim.get(i))
				{
					bw.write(s);
					if(j != sim.get(i).size()-1)
					{
						bw.write(",");
					}
					else
						bw.write("\n");
					j++;
				}
			}
			bw.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	//Á½¸ö×Ö·û´®µÄÏàËÆ¶È
	public static double simScore(String str1, String str2){
		int m = str1.length();
		int n = str2.length();
		int max = m > n ? m : n;
		if(max == 0)
			return 0;
		else
			return  (1 - levenshteinDist(str1, str2) / (double)max);
	}
	
	public static void printParam()
	{
		System.out.println("D+: " + Configure.posFname);
		System.out.println("D-: " + Configure.negFname);
		System.out.println("Gap: " + Configure.GAP[0]+","+Configure.GAP[1]);
		System.out.println("Alpha: " + Configure.ALPHA);
		System.out.println("Beta: " + Configure.BETA);
		System.out.println("Level: " + Configure.LEVEL);
	}
	
	
	//±à¼­¾àÀë
	private static int levenshteinDist(String str1, String str2){
		int m = str1.length();
		int n = str2.length();
		if((m == 1 || n == 1)&& m*n ==0)
			return m > n ? m : n;
		m += 1;
		n += 1;
		int matrix[][] = new int[m][n];
		for(int i = 0; i < m; i++)
			matrix[i][0] = i;
		for(int i = 0; i < n; i++)
			matrix[0][i] = i;
		for(int i = 1; i < m; i ++)
		{
			for(int j = 1; j < n; j ++)
			{
				int a,b;
				a = matrix[i-1][j] + 1;
				b = matrix[i][j-1] + 1;
				int min = a < b ? a : b;
				if(str1.charAt(i-1) == str2.charAt(j-1))
					a = matrix[i-1][j-1];
				else
					a = matrix[i-1][j-1] + 1;
				matrix[i][j] = min < a ? min : a;
			}
		}
		return matrix[m-1][n-1];
	}
	
	public static void readConf(String file)
	{
		BufferedReader br = null;
		String line = null;
		try{
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine())!=null){
				String[] ls = line.trim().split(">");
				String name = ls[0].trim();
				if(ls.length < 2)
					continue;
				String param = ls[1].trim();
				switch(name)
				{
				case "simGroupFile":
					Configure.simGroupFile = param;
					break;
				case "splitGroup":
					Configure.splitGroup = param;
					break;
				case "splitSim":
					Configure.splitSim = param;
					break;
				case "simThresh":
					Configure.simThresh = Double.parseDouble(param);
					break;
				case "outSimFile":
					Configure.outSimFile = param;
					break;
				case "ALPHA":
					Configure.ALPHA = Double.parseDouble(param);
					break;
				case "BETA":
					Configure.BETA = Double.parseDouble(param);
					break;
				case "GAP":
					String p[] = param.split(",");
					Configure.GAP[0] = Integer.parseInt(p[0].trim());
					Configure.GAP[1] = Integer.parseInt(p[1].trim());
					break;
				case "LEVEL":
					Configure.LEVEL = Integer.parseInt(param);
					break;
				case "splittoken":
					Configure.splittoken = param;
					break;
				case "posFname":
					Configure.posFname = param;
					break;
				case "negFname":
					Configure.negFname = param;
					break;
				case "ConceptDir":
					Configure.conceptDir = param.trim();
					break;
				case "conceptFile":
					p = param.trim().split(",");
					for(String f:p)
					{
						Configure.conceptFile.add(f.trim());
					}
					break;
				default:
					System.out.println("error param");
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static List<Map<String, List<String>>> readConcept(String dir, List<String> files)
	{
		BufferedReader br = null;
		String line = null;
		List<Map<String, List<String>>> conceptList = new ArrayList<Map<String, List<String>>>();
		try{
			for(String f:files)
			{
				br = new BufferedReader(new FileReader(dir+f));
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				while((line = br.readLine())!=null){
					List<String> elems = new ArrayList<String>();
					String ls[] = line.trim().split(":");
					String l[] = ls[1].trim().split(",");
					
					for(String s:l)
					{
						elems.add(s.trim());
					}
					map.put(ls[0].trim(), elems);
				}
				conceptList.add(map);
				br.close();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return conceptList;
	}
	
	
}
