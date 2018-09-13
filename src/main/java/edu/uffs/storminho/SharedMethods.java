/*
SharedMethods
Methods that can be used by more than one bolt
*/

package edu.uffs.storminho;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

import weka.core.Attribute;
import weka.core.Instances;

public class SharedMethods {

    //Receives a id like (rec-XXXX-org or rec-XXXX-dup-X) and tell if it is a duplicate
    public static boolean isDuplicata(String idA, String idB) {
        //split the tuples' indexes to separe the identifier
        String[] aSplit = idA.split(Variables.ID_SPLIT_CHARS);
        String[] bSplit = idB.split(Variables.ID_SPLIT_CHARS);
        return  aSplit[1].equals(bSplit[1]) && (aSplit[2].equals("org") ^ bSplit[2].equals("org"));
    }
    
    //Create a new weka Instances
    public static Instances newInstances(String name) {
        Instances dataRaw;
        ArrayList<Attribute> atts = new ArrayList<Attribute>(SharedMethods.getFieldsNumber() + 1);
        ArrayList<String> classVal = new ArrayList<String>(2);


        classVal.add("nao-duplicata");
        classVal.add("duplicata");
        
        if(Variables.HEADER) {
        	for(int i = 0, j=0; i < SharedMethods.getFieldsNumber(); j++) {
        		if(Variables.IGNORE_FIELDS.contains(j+1)) continue;
        		
        		if ((1 & Variables.RANKING_METHODS) != 0) {
	        		atts.add(new Attribute("COSINE" + Variables.HEADER_LIST.get(j)));
	        		i++;
	        	}
	            if ((2 & Variables.RANKING_METHODS) != 0) {
	            	atts.add(new Attribute("JACCARD" + Variables.HEADER_LIST.get(j)));
	            	i++;
	            }
	            if ((4 & Variables.RANKING_METHODS) != 0) {
	            	atts.add(new Attribute("JARO" + Variables.HEADER_LIST.get(j)));
	            	i++;
	            }
	            if ((8 & Variables.RANKING_METHODS) != 0) {
	            	atts.add(new Attribute("LEVENSHTEIN" + Variables.HEADER_LIST.get(j)));
	            	i++;
	            }
	            if ((16 & Variables.RANKING_METHODS) != 0) {
	            	atts.add(new Attribute("QGRAM" + Variables.HEADER_LIST.get(j)));
	            	i++;
	            }
        	}
        } else {
	        for (int i = 0; i < SharedMethods.getFieldsNumber(); i++) {
	        		atts.add(new Attribute(Variables.ARFF_ATTRIBUTES_PREFIX + i));
	        	}
        }
        
        atts.add(new Attribute("resultado", classVal));
        dataRaw = new Instances(name+"_"+Variables.DATASET_INPUT+"_"+Variables.RANKING_METHODS, atts, 0);
        dataRaw.setClassIndex(SharedMethods.getFieldsNumber());
        return dataRaw;
    }
    
    //Return the list of headers
    public static ArrayList<String> getHeaders() throws Exception{
    	File input = new File(Variables.CSV_PATH + Variables.DATASET_INPUT);
    	BufferedReader reader = new BufferedReader(new FileReader(input));	
    	String header = reader.readLine();
    	
    	if(Variables.HEADER_SPLIT_CHARS != " ") header = header.replaceAll(" ", "_"); 
    	
    	String headers[] = header.split(Variables.HEADER_SPLIT_CHARS);
   
    	ArrayList<String> list = new ArrayList<String>(Variables.ATTRIBUTES_NUMBER);
    	
    	for(int i = 0; i < Variables.ATTRIBUTES_NUMBER; i++) {
    		if (i == Variables.FIELD_ID) continue;
    		list.add(headers[i]);
    	}
    	
    	return list;
    }
    
    //count the file lines(records)
    public static int fileCountRecords() {
		int qtdLinha = 0;
		
		File arquivoLeitura = new File(Variables.CSV_PATH + Variables.DATASET_INPUT);
		LineNumberReader linhaLeitura;
		try {
			linhaLeitura = new LineNumberReader(new FileReader(arquivoLeitura));
			linhaLeitura.skip(arquivoLeitura.length());
			qtdLinha = linhaLeitura.getLineNumber();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		if(Variables.HEADER) --qtdLinha; // se houver header tira o header
		return qtdLinha; //se não houver
	}
    
  //how many fields the weka instances will have
    public static int getFieldsNumber() {
        return ((Variables.ATTRIBUTES_NUMBER -1 -(Variables.IGNORE_FIELDS.size())) * countSim()); // 1 = IDfield
    }

    //count the true bits to count how many methods will be used
    public static int countSim() {
        int rm = Variables.RANKING_METHODS, count = 0;
        for (; rm != 0; rm /= 2) {
            if ((1 & rm) != 0) count++;
        }
        return count;
    }
    
    //get arff file previsouly generated
    public static String getArff() throws IOException {
    	String arff;
    	File file;
    	if(Variables.FOLDER_CONTROL) {
    		file = new File(Variables.DATA_FOLDER + "control/arffFile");;
    	} else {
    		file = new File(Variables.IN_PATH + "arffFile");
    	}
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	arff = reader.readLine();
    	return arff;
    }
    
    public static void setArff(String arff) throws IOException {
    	File file;
    	if(Variables.FOLDER_CONTROL) {
    		file = new File(Variables.DATA_FOLDER + "control/arffFile");;
    	} else {
    		file = new File(Variables.IN_PATH + "arffFile");
    	}
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
    	writer.write(arff);
    	writer.flush();
    	writer.close();
    }
    
  //get training count from in folder
    public static int getCountTraining() throws IOException {
    	String count;
    	File file;
    	if(Variables.FOLDER_CONTROL) {
    		file = new File(Variables.DATA_FOLDER + "control/aux");
    	} else {
    		file = new File(Variables.IN_PATH + "count");
    	}
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	count = reader.readLine();
 
    	return Integer.parseInt(count);
    }
    
    public static void setCountTraining(int i) throws IOException {
    	File file;    	
    	if(Variables.FOLDER_CONTROL) {
    		file = new File(Variables.DATA_FOLDER + "control/aux");
    	} else {
    		file = new  File(Variables.IN_PATH + "count");
    	}
    	
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
    	writer.write(Integer.toString(i));
    	writer.flush();
    }
    
    //Return classifier method 
    public static String getClassifier() {
    	if (Variables.CLASSIFIER == 1) {
    		return "J48";
    	} else if (Variables.CLASSIFIER == 2) {
    		return "SVM";
    	} else {
    		return "NONE";
    	}
    }
    
    //set output file
    public static void setOutputFile(long vp, long vn, long fp, long fn) throws IOException {
    	File f;
    	if(Variables.FOLDER_CONTROL) {
    		f =	new File(Variables.DATA_FOLDER + "out.csv");
    		
    	} else {
    		f =  new File(Variables.OUT_PATH+"out.csv");
    	}
    	
		FileWriter outputfile = new FileWriter(f, true);
		CSVWriter writer = new CSVWriter(outputfile);

		
		
		double precision = 1.0 * vp / (vp + fp);
    	double recall = 1.0 * vp / (vp + fn);
		double f1 = (2*precision*recall)/(precision+recall);
		String classifier = getClassifier();
		
		String[] output = new String[19];
    	
    	output[0] = Variables.TRAININGSET_OUTPUT_FILE;
    	output[1] = Double.toString(precision);
    	output[2] = Double.toString(recall);
    	output[3] = Double.toString(f1);
    	output[4] = Long.toString(vp);
    	output[5] = Long.toString(vn);
    	output[6] = Long.toString(fp);
    	output[7] = Long.toString(fn);
    	
    	String ignoredFields = " ";
    	
    	for(int i : Variables.IGNORE_FIELDS) {
    		ignoredFields += i + " ";
    	}
    	
    	output[8] = ignoredFields;
    	output[9] = getClassifier();
    	
    	if(classifier == "SVM") {
    		String svmType = "";
    		String kernelType = "";
    		
    		switch(Variables.SVMtype.toString()) {
	    		case "0":
	    			svmType = "C_SVC";
	    			break;
	    		case "1":
	    			svmType = "NU_SVC ";
	    			break;
	    		case "2":
	    			svmType = "ONE_CLASS_SVM";
	    			break;
	    		case "3":
	    			svmType = "EPSILON_SVR";
	    			break;
	    		case "4":
	    			svmType = "NU_SVR";
	    			break;
    		}
    		
    		switch(Variables.kernelType.toString()) {
	    		case "0":
	    			kernelType = "LINEAR";
	    			break;
	    		case "1":
	    			kernelType = "POLYNOMIAL "; 
	    			break;
	    		case "2":
	    			kernelType = "RBF";
	    			break;
	    		case "3":
	    			kernelType = "SIGMOID";
	    			break;
    		}
    		
    		output[10] = svmType;
    		output[11] = kernelType;
    	
    		if(Variables.SVM_VARIABLES) {
				output[12] = Double.toString(Variables.coef0);
				output[13] = Integer.toString(Variables.degree);
				if(Variables.SET_GAMMA) {
					output[14] = Double.toString(Variables.gamma);
				} else {
					output[14] = "Default(1/n)";
				}
    		} else {
				output[12] = "Default(0)";
				output[13] = "Default(3)";
				output[14] = "Default(1/n)";
    		}
    	}
    	

    	writer.writeNext(output);
    	writer.close();
    }
    
    //Return new output file name
    public static String newOutputFileName() {
    	int count_training = 0;
    	String name;
    	
    	try {
			count_training = getCountTraining();
		} catch (IOException e) {
		}
    	
    	name = Integer.toString(count_training+1) 
				+ "_"+ Variables.DATASET_INPUT
				+"_SIZE-"+ fileCountRecords()
				+"_TD-" + Variables.TOTAL_DUPLICATAS
				+"_SS-" + Variables.SAMPLE_SIZE
				+"_TP-" + Variables.TOTAL_PARES
				+".arff";	
    	
    	return name;
    }
    public static String[] getOutHeader() {
    	String[] header = new String[] {
	    	"ARFF TRAINING",
	    	"PRECISION",
	    	"RECALL",
	    	"F1",
	    	"VP",
	    	"VN",
	    	"FP",
	    	"FN",
	    	"IGNORED FIELDS",
	    	"CLASS",
	    	"SVM-TYPE",
	    	"KERNEL",
	    	"C",
	    	"DEGREE",
	    	"GAMMA"   	
    	};
    	return header;
    }
    
    public static boolean dataFolderExists() {
    	File folder = new File(Variables.DATA_FOLDER);
    	return folder.exists();
    	
    }
    
    public static void createDataFolder() throws Exception{
    	
    	File folder = new File(Variables.DATA_FOLDER);
    	folder.mkdirs();
    	
    	File training = new File(Variables.DATA_FOLDER+"training/");
    	training.mkdirs();
    	
    	File options = new File(Variables.DATA_FOLDER+"control/");
    	options.mkdirs();
    	

    	File info = new File(Variables.DATA_FOLDER +"info");
    	File aux = new File(Variables.DATA_FOLDER+"control/aux");
    	File arff = new File(Variables.DATA_FOLDER+"control/arffFile");
    	File countP = new File(Variables.DATA_FOLDER+"control/nPairs");
    	File numReg = new File(Variables.DATA_FOLDER+"control/numReg");
    	File numDup = new File(Variables.DATA_FOLDER+"control/numDup");
    	
    	countP.createNewFile();
    	aux.createNewFile();
    	arff.createNewFile();
    	numReg.createNewFile();
    	numDup.createNewFile();
    	info.createNewFile();
    	
    	BufferedWriter writer = new BufferedWriter(new FileWriter(countP, false));
    	writer.write(Integer.toString(0));
    	writer.flush();
    	
    	writer = new BufferedWriter(new FileWriter(aux, false));
    	writer.write(Integer.toString(0));
    	writer.flush();
    	
    	writer = new BufferedWriter(new FileWriter(numReg, false));
    	int numRegistros = SharedMethods.fileCountRecords();
    	writer.write(Integer.toString(numRegistros));
    	writer.flush();
    	
    	int numDuplicatas = (int)(numRegistros - (numRegistros*100)/(100+Variables.DUPLICATE_SIZE*100));
    	writer = new BufferedWriter(new FileWriter(numDup, false));
    	writer.write(Integer.toString(numDuplicatas));
    	writer.flush();
    	
    	writer.close();
    	    	
    	File out = new File(Variables.DATA_FOLDER+"out.csv");
    	out.createNewFile();
    	
    	FileWriter outputfile = new FileWriter(out);
		CSVWriter writerOut = new CSVWriter(outputfile);
    	writerOut.writeNext(getOutHeader());
    	writerOut.close();
    	
    	Files.copy(Paths.get(Variables.CSV_PATH+Variables.DATASET_INPUT), Paths.get(Variables.DATA_FOLDER+Variables.DATASET_INPUT)); 
    }

	public static void getVariablesDataset() throws IOException {
		String path = Variables.DATASET_PATH + Variables.DATASET_INPUT + "/control/";
    	BufferedReader reader = new BufferedReader(new FileReader(new File(path+"arffFile")));
    	Variables.TRAININGSET_OUTPUT_FILE = reader.readLine();
    	reader = new BufferedReader(new FileReader(new File(path+"nPairs")));
    	Variables.TOTAL_PARES = Integer.parseInt(reader.readLine());
    	reader = new BufferedReader(new FileReader(new File(path+"numDup")));
    	Variables.TOTAL_DUPLICATAS = Integer.parseInt(reader.readLine());
    	reader = new BufferedReader(new FileReader(new File(path+"numReg")));
    	Variables.TOTAL_REGISTROS = Integer.parseInt(reader.readLine());
        	
	}
}
