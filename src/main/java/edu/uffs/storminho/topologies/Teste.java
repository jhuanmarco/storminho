package edu.uffs.storminho.topologies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import clojure.reflect.Field;
import edu.uffs.storminho.SharedMethods;
import edu.uffs.storminho.Variables;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import sun.java2d.pipe.SpanIterator;
import weka.classifiers.functions.LibSVM;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class Teste {
	
	static double[] toDoubleArray(String arrayS[]) {
		double arrayD[] = new double[arrayS.length];
		
		for(int j = 0; j < arrayS.length; j++) {
			if(j == arrayS.length-1) {
				
			}
			arrayD[j] = Double.parseDouble(arrayS[j]);
			
		} 
		
		return arrayD;
	}
	
	
	static svm_node[] getNode(String tupla) {
		double tuplaD[] = toDoubleArray(tupla.split(","));
		svm_node nodes[] = new svm_node[tuplaD.length];
		
		for(int i = 0; i < tuplaD.length; i++) {
			svm_node node =  new svm_node();
			node.index = i+1;
			node.value = tuplaD[i];
			nodes[i] = node;
			
		}
		return nodes;
		
	}
	
	static void saveFile(svm_problem p) throws FileNotFoundException {
		PrintStream file;
		file = new PrintStream("novo.libsvm");
		String printa;
		
		for(int i = 0; i < p.l; i++) {
			int aqui;
			if(p.y[i] == 0) {
				aqui = 0;
			} else {
				aqui = 1;
			}
			printa = Integer.toString(aqui);
			for(int j = 0; j < p.x[0].length; j++) {
				printa += " " + p.x[i][j].index + ":" + p.x[i][j].value;
			}
			
			printa += "\n";
			file.print(printa);
			file.flush();
		}
		
	}
	
	public static svm_model getModel(LibSVM svm) throws IllegalAccessException, NoSuchFieldException {
		java.lang.reflect.Field modelField = svm.getClass().getDeclaredField("m_Model");
		modelField.setAccessible(true);
		return (svm_model) modelField.get(svm);
	}
	
	public static void main(String[] args) throws Exception {
		boolean aqui = false;
		while(aqui) {
			System.out.println("oi");
			aqui = true;
		}
		/*System.out.println((Variables.ATTRIBUTES_NUMBER -1 -(Variables.IGNORE_FIELDS.size())) * SharedMethods.countSim());
		System.out.println(SharedMethods.countSim());
		System.out.println(Variables.DATASET_PATH + Variables.DATASET_INPUT + Variables.DATASET_INPUT);
		//System.out.println(Variables.IGNORE_FIELDS.size());
		/*for(int i = 0; i < 20 ; i++) System.out.println(Variables.HEADER_LIST.get(14));
		
		for(int i = 0, j=0; i < SharedMethods.getFieldsNumber(); j++) {
			System.out.println(j);
    		if(Variables.IGNORE_FIELDS.contains(j+1)) {
    			System.out.println(Variables.HEADER_LIST.get(j));
    			continue;
    		}
        	if ((1 & Variables.RANKING_METHODS) != 0) {
        		i++;
        	}
            if ((2 & Variables.RANKING_METHODS) != 0) {
            	i++;
            }
            if ((4 & Variables.RANKING_METHODS) != 0) {
            	i++;
            }
            if ((8 & Variables.RANKING_METHODS) != 0) {
            	i++;
            }
            if ((16 & Variables.RANKING_METHODS) != 0) {
            	i++;
            }
    	}
   
		
		
		/*System.out.println(Variables.TOTAL_DUPLICATAS);
		System.out.println(Variables.TOTAL_PARES);
		System.out.println(Variables.TOTAL_REGISTROS);
		System.out.println(Variables.TRAININGSET_OUTPUT_FILE);*/
		/*
		System.out.println(new File(Variables.DATASET_PATH+Variables.DATASET_INPUT+"/control/countPairs").exists());
		
		if(!SharedMethods.dataFolderExists()) {
			SharedMethods.createDataFolder();
		}
		
		System.out.println(SharedMethods.dataFolderExists());
		
		/*Instances data;
	    BufferedReader reader;
		reader = new BufferedReader(new FileReader(Variables.ARFF_PATH + Variables.TRAININGSET_OUTPUT_FILE));
        data = new Instances(reader);
        data.setClassIndex(data.numAttributes() - 1);
		
		LibSVM classifier = new LibSVM();
		classifier = new LibSVM();
    	((LibSVM)classifier).setSVMType(Variables.SVMtype);
    	((LibSVM)classifier).setKernelType(Variables.kernelType);
    	((LibSVM)classifier).setCoef0(Variables.coef0);
    	((LibSVM)classifier).setDegree(Variables.degree);
		
    	classifier.buildClassifier(data); 
    	svm_model model = getModel(classifier);
    	int[] indices = model.sv_indices;
    	
    	for(int i: indices) {
    		Instance supportVector = data.instance(i - 1);
			System.out.println(i + ": " + supportVector);
    	}
    	
    	
		/*
		svm_node node;
		svm SVM = new svm();
		svm_problem problem = new svm_problem();
		
		BufferedReader reader = null;
		Instances data = null;
		String linha;
		double[] tupla;
		int numAtt, numReg;
		int i = 0, j = 0;
		double y[];
		
		try {
			reader = new BufferedReader(new FileReader(Variables.ARFF_PATH + Variables.TRAININGSET_OUTPUT_FILE));
			data = new Instances(reader);
		} catch (Exception e) {
			System.out.println('o');
		}
		
		numAtt = data.numAttributes();
		numReg = data.numInstances();
		
		svm_node [][]tuplas = new svm_node[numReg][numAtt-1];
		y = new double[numReg];
		
		
		while(true) {
			try {
				linha = data.get(i).toString();
				tupla = toDoubleArray(linha.split(","));
			} catch( Exception e) {
				break;
			}
			
			for(j = 0; j < numAtt; j++) {
				if(j == numAtt-1) {
					y[i] = tupla[j];
					continue;
				}
				node = new svm_node();
				node.index = j+1;
				node.value = tupla[j];
				tuplas[i][j] = node;
			}
					
			i++;
	
		}
	
			
		problem.l = i;
		problem.x = tuplas;
		problem.y = y;
		

		svm_parameter parameters = new svm_parameter();

		parameters.svm_type = 0;
		parameters.kernel_type = 1;
		//parameters.coef0 = 5;
		//parameters.gamma = 10;
		parameters.degree = 1;
		
		svm_model model = SVM.svm_train(problem, parameters);
		
		String pre = "0,0,0,0,0,0,0,0,0,0,0,0,0.7,0.5,0.25,0,0,0,0,0,1,1,1,1,1,0,0,0.64,0.2,0.142857,0,0,0,0,0,0,0,0,0,0,0,0,0.602694,0.181818,0,0,0,0.5,0.25,0.166667,1,1,1,1,1,0,0,0.592425,0.235294,0.058824,0,0,0.438131,0.090909,0,0,0,0.548485,0.272727,0";

		svm_node nos[] = getNode(pre);
		
		double predict = SVM.svm_predict(model, nos);
		System.out.println(predict);
		
		SVM.svm_save_model("nome.libsvm", model);
		System.out.println(problem.l);
		System.out.println(SVM.svm_get_nr_sv(model));
		System.out.println(problem.x[0]);
		saveFile(problem);
		
		*/
		/*
		for(i = 0; i < 20; i++) {
			for(j = 0; j < numAtt-1; j++) {
				System.out.print(tuplas[i][j].index + ":" + tuplas[i][j].value + ",");
			}
			System.out.println("");
		}
		
		for(int h = 0; h < nos.length; h++) {
			System.out.println(nos[h].index + ":" + nos[h].value + ",");
		}
		*/	
		
		
		//System.out.println(Variables.ARFF_PATH+Variables.TRAININGSET_OUTPUT_FILE);
		
		
		
		
		/*svm e = new svm();
		svm.svm_train(prob, param)args
		svm.svm_predict_probability(model, x, prob_estimates)
		svn_
		svm_parameter p = new svm_parameter();
	}

	
	public class svm {
		public static final int LIBSVM_VERSION=322; 
		public static svm_model svm_train(svm_probl	em prob, svm_parameter param);
		public static void svm_cross_validation(svm_problem prob, svm_parameter param, int nr_fold, double[] target);
		public static int svm_get_svm_type(svm_model model);
		public static int svm_get_nr_class(svm_model model);
		public static void svm_get_labels(svm_model model, int[] label);
		public static void svm_get_sv_indices(svm_model model, int[] indices);
		public static int svm_get_nr_sv(svm_model model);
		public static double svm_get_svr_probability(svm_model model);
		public static double svm_predict_values(svm_model model, svm_node[] x, double[] dec_values);
		public static double svm_predict(svm_model model, svm_node[] x);
		public static double svm_predict_probability(svm_model model, svm_node[] x, double[] prob_estimates);
		public static void svm_save_model(String model_file_name, svm_model model) throws IOException
		public static svm_model svm_load_model(String model_file_name) throws IOException
		public static String svm_check_parameter(svm_problem prob, svm_parameter param);
		public static int svm_check_probability_model(svm_model model);
		public static void svm_set_print_string_function(svm_print_interface print_func);*/
	}
	
	
}
