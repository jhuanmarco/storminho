/*
TrainingCreatorBolt
Entrada: Uma Instance da biblioteca weka e 2 linhas originais do arquivo .csv
Saída: Nada
A Instance é a similaridade calculada entre as 2 linhas originais.
O Bolt cria um set de treinamento com uma porcentagem de pares de linhas que são passados para ele.
Esse treinamento é utilizado depois no DecisionTreeBolt.
O treinamento é um arquivo .arff que é salva a cada vez que uma nova Instance é inserida.
*/

package edu.uffs.storminho.bolts;

import edu.uffs.storminho.SharedMethods;
import edu.uffs.storminho.Tree;
import edu.uffs.storminho.Variables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.OutputCollector;

import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Fields;
import redis.clients.jedis.Jedis;
import weka.core.DenseInstance;
import weka.core.Instances;

public class FullCreatorBolt extends BaseRichBolt implements IRichBolt {
    private PrintStream file;
    private int negativeTrainingPairs, allPairs, positivePairs;
    private Instances dataRaw;
    private TreeSet<String> set;


    @Override
    public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        set = new TreeSet<String>();
        negativeTrainingPairs = allPairs = positivePairs = 0;

        //weka
        dataRaw = SharedMethods.newInstances("TrainingInstances");
        
        if(Variables.GET_VARIABLES) {
        	int count_training = 0; 
        	Variables.TRAININGSET_OUTPUT_FILE = "FULL_"+SharedMethods.newOutputFileName();
        	try {
				count_training = SharedMethods.getCountTraining();
				SharedMethods.setCountTraining(count_training+1);
			} catch (IOException e) {
				
			}
			
        }
        
        try {
			file = new PrintStream(Variables.ARFF_PATH + Variables.TRAININGSET_OUTPUT_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        file.print(dataRaw.toString());
        file.flush();
        
    }

    @Override
    public void execute(Tuple tuple) {
        String linha1 = tuple.getString(1), linha2 = tuple.getString(2);
        DenseInstance instance = (DenseInstance)tuple.getValues().get(0);
        String id1 = linha1.split(Variables.SPLIT_CHARS)[Variables.FIELD_ID];
        String id2 = linha2.split(Variables.SPLIT_CHARS)[Variables.FIELD_ID];

        //só vai passar por esse if aqueles que não foram considerados ainda e aqueles que não são exatamente igual (as redundâncias)
        if (set.add(id1 + "_" + id2) && set.add(id2 + "_" + id1) && !id1.equals(id2)) {
        	allPairs++;
         
            if (SharedMethods.isDuplicata(id1, id2)) { //Vai contar quantos desses pares distintos são duplicatas
                positivePairs++;
            } else {
            	negativeTrainingPairs++;
            }
    
			instance.setDataset(dataRaw);
			instance.setClassValue((SharedMethods.isDuplicata(id1, id2) ? "duplicata":"nao-duplicata"));
			
			try {
				file.println(instance.toString());
			    file.flush();
			} catch (Exception e) { System.out.println(e); }
			
			//inform in console / tests
			System.out.println("ENTRARAM NO ARFF:" + allPairs + "\nPositivos: " + positivePairs + " e Negativos: " + negativeTrainingPairs + "\n");
			
			}
     
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields());
    }

    @Override
    public void cleanup() {
    }
}
