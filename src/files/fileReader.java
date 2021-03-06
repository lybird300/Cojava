package files;

import geneConversion.gc;
import historical.histWorker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import recomb.recListMaker;
import simulator.sim;
import coalescent.ArgHandler;
import cosiRand.randomNum;
import demography.demography;
public class fileReader {
	ArgHandler fileSet;
	String[] args;
	boolean seeded = false;
	private boolean debug = true;
	int length;
	demography dem;
	recListMaker recomb;
	sim simulator;
	gc geneConversion;
	histWorker histFactory;
	randomNum random;
	public fileReader(ArgHandler aFileSet,demography adem,recListMaker aRecomb,sim aSim,gc aGeneConverter ,histWorker aHistFactory,randomNum aRNG  ){
		histFactory = aHistFactory;
		simulator = aSim;
		dem = adem;
		fileSet = aFileSet;
		recomb = aRecomb;
		geneConversion = aGeneConverter;
		random = aRNG;
	}
	public void paramFileProcess(){
		try {
			BufferedReader stream = getFileToRead(fileSet.getParamFile().toString());
			String line;
			while((line = stream.readLine())!=null){//reading everything line by line
				char[] charLine = line.toCharArray();
					for(int i =0;i<charLine.length;i++){
						if(charLine[i]=='#' && i == 0){//comment
							i = charLine.length;
							line = "";
							//break;
						}
						else if(charLine[i]=='#' && i!=0){//comment at a point in line kill comment 
							line = line.substring(0, i);
							//processParamBuffer(line);
							i = charLine.length;
							
						}
					
					}
				if(!(line.length()==0)){
					processParamBuffer(line);
				}
			}
		stream.close();
		if(!seeded){
			System.out.println(String.format("coalescent seed: %d\n", -1 * random.seedRNG()));
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error paramFileProcess" + e.getMessage() );
		}
	}
	private void processParamBuffer(String line) {//gonna have to make some objects
		//process the param file
		if(line.contains("length")){//length has to be set before recomb file
			System.out.println(line);
			length = Integer.parseInt(cleanString(line)[1]);
			simulator.simSetLength(length);
		}
		else if (line.contains("recomb_file")){
			System.out.println(line);
			processRecombFile(line);
		}
		else if (line.contains("mutation_rate")){
			System.out.println(line);
			args = cleanString(line);
			double mu =  Double.parseDouble(args[1]);
			simulator.setTheta(mu*length);
			// set mutation rate in sim
		}
		else if (line.contains("infinite_sites")){
			System.out.println(line);
			args = cleanString(line);
			String answer = args[1];
			if(answer.equalsIgnoreCase("yes")){
				//set infinite sites....
			}
		}
		else if (line.contains("gene_conversion_rate")){
			System.out.println(line);
			args = cleanString(line);
			double gcr = Double.parseDouble(args[1]);
			geneConversion.setGCRate(gcr);
		}
		else if (line.contains("number_mutation_sites")){
			System.out.println(line);
			args = cleanString(line);
			int numMut = Integer.parseInt(args[1]);
			simulator.setNumMut(numMut);
			
		}
		else if (line.contains("pop_label")){
			System.out.println(line);
			
		}
		else if (line.contains("pop_size")){
			System.out.println(line);
			args = cleanString(line);
			int popName = Integer.parseInt(args[1]);
			int popSize = Integer.parseInt(args[2]);
			if(dem.setPopSizeByName(0, popName, popSize)!=1)
			System.out.println("parameter file pop Specified does not exist ERROR" + line);
		}
		else if (line.contains("sample_size")){
			//System.out.println(line);
			args = cleanString(line);
			int popName = Integer.parseInt(args[1]);
			int sampleSize = Integer.parseInt(args[2]);
			dem.populateByName(popName, sampleSize, 0);
			if(fileSet.getSegFile()!= null){
				String out = String.format("A %d %d\n", popName,sampleSize);
				//write out to file
			}
		}
		else if (line.contains("pop_define")){
			System.out.println(line);
			args = cleanString(line);
			int popName = Integer.parseInt(args[1]);
			char[] label = args[2].toCharArray();
			//String 
			dem.createPop(popName, label, 0);
		}
		else if (line.contains("pop_event")){
			System.out.println(line);
			histFactory.historicalProcessPopEvent(line);
			
		}
		else if (line.contains("random_seed")){
			System.out.println(line);
			args = cleanString(line);
			if(Double.parseDouble(args[1])>0){
				long rseed = -1*Long.parseLong(args[1]);
				random.setRngSeed(rseed);
				
			}
		}
		else {
			String err = line + "is not a valid parameter \n" +
					"this parameter will be skipped";
			System.out.println(err);
		}
	}
	private void processRecombFile(String line) {
		try{
		BufferedReader aStream;
		String fileType = "recomb_file";
		//line.replace(" ", "");//maybe bad for windows machines with spaces in filename
		line = line.trim();
		line = line.substring(fileType.length());
		aStream = getFileToRead(line.trim());
			while((line = aStream.readLine() )!=null){
				String[] result = line.split("\\s+");
				int start = Integer.parseInt(result[0]);
				double rate = Double.parseDouble(result[1]);
				recomb.addRecombSiteLL(start, rate);
			}
		aStream.close();
		recomb.recomb_calc_r();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private BufferedReader getFileToRead(String aPath){
	 BufferedReader bf;
	try {
		bf = new BufferedReader(new FileReader(aPath));
		return bf;
	} 	catch (FileNotFoundException e) {
		e.printStackTrace();
		return null;
		}
	}
	
	private String[] cleanString(String aLine){
		String[] result = aLine.split("\\s+");
		return result;
	}
	
}

