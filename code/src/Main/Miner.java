package Main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Util.Baseline1;
import Util.Baseline2;
import Util.Concept;
import Util.Configure;
import Util.Configure.Traval;
import Util.Depth;
import Util.DepthBaseline;
import Util.Generator;
import Util.Processor;
import Util.Sdsp;
import Util.Sequence;
import Util.Tool;
import Util.WidthDepths;
import Util.Widths;
import Util.Widths2;

public class Miner {

	public static void main(String[] args) {
		
//		Tool.readConf("conf/configure3_1.txt");
		Tool.readConf("conf/configure_gene_5_1.txt");
//		Tool.readConf("conf/configure3.txt");
//		Tool.readConf("conf/configure_protein_Flic.txt");
//		Tool.readConf("conf/configure_protein_6_3.txt");
//		Tool.readConf("conf/configure_gene_syn.txt");
//		Tool.readConf("conf/configure_cli_fm.txt");
		Tool.printParam();
		
		Tool.redirectOut("out/"+"depthbase.txt");

		System.out.println("test");
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		String current = timeformat.format(System.currentTimeMillis());
		System.out.println("\n\n\n" + current + "\n");
		
		Processor processor = new Processor(Configure.posFname, Configure.negFname);

		System.out.println(processor.getAlphabet().size());
		
		Sequence sequence = new Sequence(processor.getPositions(), processor.getPosNum(), processor.getNegNum());
		
		List<Map<String, List<String>>> conceptList = Tool.readConcept(Configure.conceptDir, Configure.conceptFile);
		
		Concept concept = new Concept(conceptList);
//		concept.printConcept();
		concept.prune(processor.getPositions().keySet());
//		concept.printConcept();
		
		ArrayList<Sdsp> sdsp = null;
		
		Generator generator = null;
		
		if(Configure.TRAVER == Traval.DEPTH)
		{
			generator = new Depth(concept);
		}
		else if(Configure.TRAVER == Traval.WIDTHSTART)
		{
			generator = new Widths(concept);
		}
		else if(Configure.TRAVER == Traval.WIDTHDEPTHSTART)
		{
			generator = new WidthDepths(concept);
		}
		else if(Configure.TRAVER == Traval.BASELINE1)
		{
			generator = new Baseline1(concept);
		}
		else if(Configure.TRAVER == Traval.BASELINE2)
		{
			generator = new Baseline2(concept);
		}
		else if(Configure.TRAVER == Traval.BASELINE0)
		{
			generator = new Widths2(concept);
		}
		else if(Configure.TRAVER == Traval.DEPTHBASELINE)
		{
			generator = new DepthBaseline(concept);
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
		Tool.printDsp(sdsp);
		endMili=System.currentTimeMillis();
		Tool.printParam();
		Tool.log("Finished", endMili - startMili);
		
		System.out.println("TotalMemory: "+Tool.totalMemory);
	}

}
