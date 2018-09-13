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
import java.util.ArrayList;
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
        return (Variables.ATTRIBUTES_NUMBER - 1 - Variables.IGNORE_FIELDS.length) * countSim(); // 1 = IDfield
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
    	File file = new File(Variables.IN_PATH + "arffFile");
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	arff = reader.readLine();
    	return arff;
    }
    
    public static void setArff(String arff) throws IOException {
    	File file = new File(Variables.IN_PATH + "arffFile");
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
    	writer.write(arff);
    	writer.flush();
    	writer.close();
    }
    
  //get training count from in folder
    public static int getCountTraining() throws IOException {
    	String count;
    	File file = new File(Variables.IN_PATH + "count");
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	count = reader.readLine();
 
    	return Integer.parseInt(count);
    }
    
    public static void setCountTraining(int i) throws IOException {
    	File file = new File(Variables.IN_PATH + "count");
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
    public static void setOutputFile(double precisao, double revocacao, double f1, long vp, long vn, long fp, long fn) throws IOException {
    	File f = new File(Variables.OUT_PATH+"data");
    	BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
    	String classifier = getClassifier();
    	
    	String toAppend = Variables.TRAININGSET_OUTPUT_FILE
    			+ ", PRECISION: " + Double.toString(precisao)
    			+ ", REVOC:" + Double.toString(revocacao)
    			+ ", F1:" + Double.toString(f1)
    			+ ", VP:" + Long.toString(vp) //True Positive
    			+ ", VN:" + Long.toString(vn) //True Negative
    			+ ", FP:" + Long.toString(fp) //False Positive
    			+ ", FN:" + Long.toString(fn) //False Negative
				+", CLASS-" + classifier;
    	
    	if(classifier == "SVM") {
    		toAppend += ", SVMTYPE: " + Variables.SVMtype.toString()
				+ ", KERNELTYPE:" + Variables.kernelType.toString();
    		if(Variables.SVM_VARIABLES) {
				toAppend += ", COEF:" + Double.toString(Variables.coef0)
				+ ", DEGREE:" + Integer.toString(Variables.degree);
    		}
    	}
    	
    	
		toAppend += "\n";
    	writer.append(toAppend);
    	writer.flush();
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
}
