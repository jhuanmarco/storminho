package edu.uffs.storminho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import weka.classifiers.functions.LibSVM;
import weka.core.SelectedTag;

public class Variables {
	
	/* Select Classifier  =====================================================*/
	//Set which classifier will be used  1- J48 / 2- LibSVM
	public static final int CLASSIFIER = 2;   
    //==========================================================================
	
	/* LibSVM Options	=======================================================*/
	//Set LibSVM options
	public static final SelectedTag SVMtype = new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE);
	/*	(Default C-SVC)
		LibSVM.SVMTYPE_C_SVC			 // Multiclass (Default) 
	  	LibSVM.SVMTYPE_NU_SVC 			//  Multiclass 
		LibSVM.SVMTYPE_ONE_CLASS_SVM  // 
		LibSVM.SVMTYPE_EPSILON_SVR	// Regression 
		LibSVM.SVMTYPE_NU_SVR		// Regression 
	*/
	public static final SelectedTag kernelType = new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE);
	/*	(Default RBF)
	 	LibSVM.KERNELTYPE_LINEAR //  (u'*v)
	  	LibSVM.KERNELTYPE_POLYNOMIAL // (gamma*u'*v + coef0)^degree) 
		LibSVM.KERNELTYPE_RBF 		// (exp(-gamma*|u-v|^2))  RBF = Radial (Default) 
		LibSVM.KERNELTYPE_SIGMOID // (tanh(gamma*u'*v + coef0))
	*/	
	public static final double coef0 = 0; //  (Default 0) -- POLY, SIGMOID -- 
	public static final int degree = 0;// (Default 3)  -- POLY -- 
	//public static final int gamma = 1/n; // (Default 1/n atributes) -- POLY, RBF, SIGMOID -- 

    //==========================================================================
	
	/* Auto Process ===========================================================*/
	//Get variables from 'in' folder
	public static final boolean GET_VARIABLES = true;   
    //==========================================================================

	public static final int[] IGNORE_FIELDS = new int[] {};
	
    /* TrainingCreator =======================================================*/
		//Name of the output's file
    public static String TRAININGSET_OUTPUT_FILE = "trainingSet.arff";
    //Sample Size = Essa porcentagem define quantos pares serão selecionado dentro do conjunto de pares positivos
    public static final double SAMPLE_SIZE = 0.30;
    //Duplicate Size = Porcentagem de quantos pares são inseridos duplicados, ex. datasize = 1000 e dulicate size .10 entao o arquivo tera 1100 registros
    public static final double DUPLICATE_SIZE = 0.1;
    //Quantos registros tem ao todo
    public static final int TOTAL_REGISTROS;
    //Quantas duplicatas existem no conjunto de teste
    public static final int TOTAL_DUPLICATAS;
    //Quantos pares tem ao todo
    public static final int TOTAL_PARES = 417247; // need to execute countPairsTopology 407497
    //==========================================================================

    /* .csv related ==========================================================*/
    //Where the tuple is gonna be split
    public static final String SPLIT_CHARS = ",";
    //Which tuple's column holds the id field
    public static final int FIELD_ID = 0;
    //how many columns does the csv have in total
    public final static int ATTRIBUTES_NUMBER = 15;
    //Arquivo que será usado no LineSpout
    public final static String DATASET_INPUT = "teste01"; //407495 407497 407497  408496 408496 417247             411737
    //=========================================================== ===============
    
    /*Header ==========================================================*/ 
    //Verifica se há cabeçalho ou não
    public final static boolean HEADER = true;
    //Get arraylist of headers
    public static ArrayList<String> HEADER_LIST;
    //Where the header is gonna be split
    public static final String HEADER_SPLIT_CHARS = ",";
    //==========================================================================

    /*PairGenerator ==========================================================*/
    //Tamanho máximo de um set pra realizar as combinacoes
    public static final int MAX_SET_SIZE = 50;
    //==========================================================================

    /* General ===============================================================*/
    //Path to project folder
    //Don't forget to define an environment variable called "STORMINHO" or something that you want (if you choose another name, you have to change in here
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    //Path to arff folder
    public static final String ARFF_PATH = PROJECT_PATH + "/arff/";
    //Path to out folder
    public static final String OUT_PATH = PROJECT_PATH + "/out/";
    //Path to in folder
    public static final String IN_PATH = PROJECT_PATH + "/in/";
    //Path to csv folder
    public static final String CSV_PATH = PROJECT_PATH + "/csv/";
    //==========================================================================

    /*PairRanker =============================================================*/
    /*Select the methods that gonna be used. Use this as a sum with the following numbers:
    * 1 - Cosine Similarity
    * 2 - Jaccard  Similarity
    * 4 - Jaro Winkler Similarity
    * 8 - Levenshtein Similarity
    * 16 - Grams
    * Ex: public final static int RANKING_METHODS = 2 + 8; means that Jaccard and Levenshtein gonna be used */
    public final static int RANKING_METHODS = 31; //8 standard
    //Where Id field will be split
    public static final String ID_SPLIT_CHARS = "-";
    //names used in attributes in arff files
    public static final String ARFF_ATTRIBUTES_PREFIX = "att";
    //==========================================================================

    /*Debugs and Preparation =================================================*/
    //if true, will only proccess the necessary to count how many pairs and duplicatas there is in a set
    public static boolean COUNT_MODE;    
    //==========================================================================


    /*Initialization =========================================================*/
    //Init header list and count how many regs have in the dataset
    static {
    	TOTAL_REGISTROS = SharedMethods.fileCountRecords();
		TOTAL_DUPLICATAS = (int)(TOTAL_REGISTROS - (TOTAL_REGISTROS*100)/(100+DUPLICATE_SIZE*100));
		
		// If have header than init header array
		if(HEADER) {
			try {
				HEADER_LIST = SharedMethods.getHeaders();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			HEADER_LIST = null;
		}
		
		
		// Automatic
		if(GET_VARIABLES) {			
			try {
				TRAININGSET_OUTPUT_FILE = SharedMethods.getArff();
				
			} catch (Exception  e) {
				
			}	
		}
		
	}      
    //==========================================================================
    
}
