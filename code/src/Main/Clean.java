package Main;

import Util.Configure;
import Util.Processor;
import Util.Tool;

public class Clean {
	
	public static void main(String[] args)
	{
		Processor processor = new Processor(Configure.posFname, Configure.negFname);
		Tool.calSimilarity(Configure.simThresh, Configure.outSimFile, processor.getAlphabet());
	}
}