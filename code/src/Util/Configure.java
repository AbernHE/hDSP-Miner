package Util;

import java.util.ArrayList;
import java.util.List;

public class Configure {

	public static final Traval TRAVER = Traval.WIDTHDEPTHSTART;

	public enum Traval {
		DEPTH, SINGLE, DSP, WIDTHSTART, WIDTHDEPTHSTART, BASELINE1, BASELINE2, BASELINE0, DEPTHBASELINE
	}

	public static final boolean dsp = false;

	// similarity group file
	public static String simGroupFile = "./data/protein.txt";
	public static String splitGroup = ":";
	public static String splitSim = ",";
	public static String conceptDir = "./data/concept/";
	public static List<String> conceptFile = new ArrayList<String>();

	// calculate the similarity of textual
	public static double simThresh = 0.80;
	public static String outSimFile = "./data/msg/msg.txt";

	// threshold of alpha and beta
	// public static final double ALPHA = 0.95;
	// public static final double BETA = 0.50;
	// public static final int[] GAP = {0, 3};

	public static double ALPHA = 0.95;
	public static double BETA = 0.50;
	public static int[] GAP = { 0, 3 };

	// memory size
	public static final long MEMORY = 1024 * 1024 * 4;
	public static int LEVEL = 2;

	// the way to split sequences
	public static String splittoken = "";

	// D+ and D- file directory
	// public static final String posFname = "./data/PF02969_1_full.txt";
	// public static final String negFname = "./data/PF04719_1_full.txt";

	// public static final String posFname = "./data/PF14605_full.txt";
	// public static final String negFname = "./data/PF08675_full.txt";

	// public static final String posFname = "./data/PF14250_full.txt";
	// public static final String negFname = "./data/PF15937_full.txt";

	// public static final String simGroupFile = "./data/msg/msg.txt";
	// public static final double ALPHA = 0.045;
	// public static final double BETA = 0.003;
	// public static final int[] GAP = {0, 5};
	// public static final String posFname = "./data/msg/spam.txt";
	// public static final String negFname = "./data/msg/ham.txt";

	// public static final String posFname = "./data/pos";
	// public static final String negFname = "./data/neg";

	// public static final String posFname = "./data/MetallophosN.txt"; //
	// public static final String negFname = "./data/DUF1416.txt";
	// //GramPos_pilinBB

	// public static final String posFname = "./data/SAM_NCD1.txt"; //
	// public static final String negFname = "./data/SAM_IGR.txt";

	// 3
	// public static final String posFname = "./data/Calycin_Calycin_like.txt";
	// //
	// public static final String negFname = "./data/Calycin_MoaF_C.txt";

	// 1
	// public static final String posFname = "./data/DPBB_Ceratp-platanin.txt";
	// //
	// public static final String negFname = "./data/DPBB_Barwin.txt";

	// 2
	public static String posFname = "./data/PKinase_YrbL-PhoP_reg.txt"; //
	public static String negFname = "./data/PKinase_WaaY.txt";

	// public static final String posFname = "./data/FliG_FliG_M.txt"; //
	// public static final String negFname = "./data/FliG_FliG_N.txt";

	// public static final String posFname = "./data/DprA_DUF1273.txt"; //
	// public static final String negFname = "./data/DprA_DUF3412.txt"; //
}
