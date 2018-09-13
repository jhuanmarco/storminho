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
import edu.uffs.storminho.Variables;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.OutputCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeSet;
import org.apache.storm.task.TopologyContext;
import weka.core.Instances;

public class CountPairsBolt extends BaseRichBolt implements IRichBolt {
    private int negativeTrainingPairs, allPairs, positivePairs;
    private Instances dataRaw;
    private TreeSet<String> set;
    
    @Override
    public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        set = new TreeSet<String>();
        negativeTrainingPairs = allPairs = positivePairs = 0;
    }

    @Override
    public void execute(Tuple tuple) {
        String linha1 = tuple.getString(1), linha2 = tuple.getString(2);
        String id1 = linha1.split(Variables.SPLIT_CHARS)[Variables.FIELD_ID];
        String id2 = linha2.split(Variables.SPLIT_CHARS)[Variables.FIELD_ID];

        //só vai passar por esse if aqueles que não foram considerados ainda e aqueles que não são exatamente igual (as redundâncias)
        if (set.add(id1 + "_" + id2) && set.add(id2 + "_" + id1) && !id1.equals(id2)) {
        	
        	 if (SharedMethods.isDuplicata(id1, id2)) { 
     	        positivePairs++;
     	    } else {
     	        negativeTrainingPairs++; 
     		}
        	 
        	allPairs++;
        	System.out.println("[cp] Total de pares: " + allPairs + "\nPositivos: " + positivePairs + "\nNegativos: " + negativeTrainingPairs + "\n");
            
        	if(Variables.FOLDER_CONTROL) {
            	try {
            		BufferedWriter file = new BufferedWriter(new FileWriter(Variables.DATA_FOLDER + "control/nPairs", false));
					file.write(Integer.toString(allPairs));
					file.flush();
					file.close();
				} catch (Exception e) {
				}
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
       
    }

    @Override
    public void cleanup() {    	
    }
}
