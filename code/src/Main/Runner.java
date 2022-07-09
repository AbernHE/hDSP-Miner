package Main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Util.Baseline2;
import Util.Concept;
import Util.Configure;
import Util.Depth;
import Util.DepthBaseline;
import Util.Generator;
import Util.Processor;
import Util.Sdsp;
import Util.Sequence;
import Util.SimGroup;
import Util.Tool;
import Util.WidthDepths;
import Util.Widths;

public class Runner {

	public static void main(String[] args) {
		
		if(args.length < 3)
		{
			System.out.println("input more parameters!");
			return ;
		}
		int way = Integer.parseInt(args[0]);
		
		String resultFile = args[1];
		
		String confName = args[2];
		
		Tool.readConf(confName);
		
		if(args[3].equals("a"))
		{
			Configure.ALPHA = Double.parseDouble(args[4]);
		}
		else if(args[3].equals("b"))
		{
			Configure.BETA = Double.parseDouble(args[4]);
		}
		else if(args[3].equals("g"))
		{
			Configure.GAP[0] = Integer.parseInt(args[4]);
			Configure.GAP[1] = Integer.parseInt(args[5]);
		}
		else if(args[3].equals("l"))
		{
			Configure.LEVEL = Integer.parseInt(args[4]);
		}
		Tool.printParam();
		
		Tool.redirectOut(resultFile);
		
		
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		String current = timeformat.format(System.currentTimeMillis());
		System.out.println("\n\n\n" + current + "\n");
		
		Processor processor = new Processor(Configure.posFname, Configure.negFname);
		
		Sequence sequence = new Sequence(processor.getPositions(), processor.getPosNum(), processor.getNegNum());
		
		List<Map<String, List<String>>> conceptList = Tool.readConcept(Configure.conceptDir, Configure.conceptFile);
		
		Concept concept = new Concept(conceptList);
		
		concept.prune(processor.getPositions().keySet());
		
		ArrayList<Sdsp> sdsp = null;
		
		Generator generator = null;
		
		if(way == 0)
		{
			generator = new Widths(concept);
		}
		else if(way == 1)
		{
			Configure.LEVEL = 2;
			generator = new WidthDepths(concept);
		}
		else if(way == 2)
		{
			generator = new Depth(concept);
		}
		else if(way == 3)
		{
			Configure.LEVEL = 3;
			generator = new WidthDepths(concept);
		}
		else if(way == 4)
		{
			generator = new Baseline2(concept);
		}
		else if(way == 5)
		{
			// depthbaseline, no any prune rules
			generator = new DepthBaseline(concept);
		}
		else if(way == 6)
		{
			// defualt or set
			generator = new WidthDepths(concept);
		}
		
		long startMili=System.currentTimeMillis();
		
		if(generator != null)
		{
			try {
				System.out.println("Generate the patterns.");
				generator.genPattern(sequence);
				generator.solution();
				generator.sort();
				sdsp = generator.getDsp();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long endMili=System.currentTimeMillis();
		Tool.log("Finished", endMili - startMili);
		System.out.println("dsp size:"+sdsp.size());
		Tool.log("candidate size", generator.getCanNum());
//		Tool.printDsp(sdsp);
		endMili=System.currentTimeMillis();
		Tool.printParam();
		Tool.log("Finished", endMili - startMili);
		
		System.out.println("TotalMemory: "+Tool.totalMemory);
	}

}
