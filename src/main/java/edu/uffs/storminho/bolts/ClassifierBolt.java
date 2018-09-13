/*
DecisionTreeBolt
Entrada: Uma Instance da biblioteca Weka
Saída: Classificação dessa Instance
Simples implementação de uma árvore de decisão do tipo J48 sem poda
*/

package edu.uffs.storminho.bolts;


import edu.uffs.storminho.Variables;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.OutputCollector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Values;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.WekaPackageManager;

public class ClassifierBolt extends BaseRichBolt implements IRichBolt {
	OutputCollector _collector;
    Classifier classifier;
    
    Instances data;
    BufferedReader reader;

    @Override
    public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        _collector = collector;

        
        WekaPackageManager.loadPackages( false, true, false );
        
        //weka
        data = null;
        try {
        	if(Variables.FOLDER_CONTROL) {
        		reader = new BufferedReader(new FileReader(Variables.DATA_FOLDER + "training/" + Variables.TRAININGSET_OUTPUT_FILE));
        	} else {
        		reader = new BufferedReader(new FileReader(Variables.ARFF_PATH + Variables.TRAININGSET_OUTPUT_FILE));
        	}
            data = new Instances(reader);
            data.setClassIndex(data.numAttributes() - 1);
           
            
            if(Variables.CLASSIFIER == 1) {
            	classifier = new J48();
            	((J48) classifier).setUnpruned(true);
            	
            } else if (Variables.CLASSIFIER == 2) {
            	classifier = new LibSVM();
            	
            	if(Variables.SVM_VARIABLES) {
	            	((LibSVM)classifier).setSVMType(Variables.SVMtype);
	            	((LibSVM)classifier).setKernelType(Variables.kernelType);
	            	((LibSVM)classifier).setCoef0(Variables.coef0);
	            	((LibSVM)classifier).setDegree(Variables.degree);
	            	if(Variables.SET_GAMMA) {
	            		((LibSVM)classifier).setGamma(Variables.degree);
	            	}
            	}
            	
            } else if (Variables.CLASSIFIER == 3) {
            	classifier = new MultilayerPerceptron();
            	((MultilayerPerceptron)classifier).setGUI(true);
            }
            
            classifier.buildClassifier(data);  
            
            reader.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        DenseInstance instance = (DenseInstance)tuple.getValue(0);
        double result = -1;
        String linha1 = tuple.getString(1), linha2 = tuple.getString(2);
        instance.setDataset(data);
        
        try {
            result = classifier.classifyInstance(instance);
        } catch (Exception ex) {
            System.out.println(ex);
            Logger.getLogger(ClassifierBolt.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("[dt]" + (int)(result + 0.5) + "\n" + linha1 + "\n" + linha2 + "\n");
        _collector.emit(new Values((int)(result + 0.5), linha1, linha2));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("Rª. DecisionTree", "Linha 1", "Linha 2"));
    }

    @Override
    public void cleanup() {
    }
}
