package edu.uffs.storminho.topologies;

import edu.uffs.storminho.bolts.LineSaverBolt;
import edu.uffs.storminho.LineSpout;
import edu.uffs.storminho.SharedMethods;
import edu.uffs.storminho.Variables;
import edu.uffs.storminho.bolts.PairGeneratorBolt;
import edu.uffs.storminho.bolts.PairRankerBolt;
import edu.uffs.storminho.bolts.SplitSentenceBolt;
import edu.uffs.storminho.bolts.TrainingCreatorBolt;
import edu.uffs.storminho.bolts.WordIndexSaveBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import redis.clients.jedis.Jedis;

public class CreateTrainingTopology {
	
  public static Object terminador = new Object();
  public static boolean terminar = false;

  public static void main(String[] args) throws Exception {
	SharedMethods.porc("split.txt");
    TopologyBuilder builder = new TopologyBuilder();
    Jedis jedis = new Jedis("localhost");
    jedis.flushAll();
    Variables.COUNT_MODE = false;
    LocalCluster cluster = null;

    builder.setSpout("line-spout", new LineSpout());
    builder.setBolt("line-saver", new LineSaverBolt(), 1).shuffleGrouping("line-spout");
    builder.setBolt("split-sentence", new SplitSentenceBolt(), 1).shuffleGrouping("line-spout");
    builder.setBolt("index-save", new WordIndexSaveBolt(), 1).shuffleGrouping("split-sentence");
    builder.setBolt("pair-generator", new PairGeneratorBolt(), 10).shuffleGrouping("index-save");
    builder.setBolt("pair-ranker", new PairRankerBolt(), 1).shuffleGrouping("pair-generator");
    builder.setBolt("training-creator", new TrainingCreatorBolt(), 1).shuffleGrouping("pair-ranker");

    Config conf = new Config();
    conf.setDebug(false);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(1);
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(10);
      cluster = new LocalCluster();
      cluster.submitTopology("storminho-topology", conf, builder.createTopology());
      System.out.println("\n\n----------------------------\nFim do processo da topologia\n----------------------------\n");
    }
    
    while(!terminar) {
    	synchronized(CreateTrainingTopology.terminador) {
            CreateTrainingTopology.terminador.wait(); // 6 + 18 = 24
    	}
    }
    
    cluster.shutdown();
    System.out.println("TERMINOULALALA");
  }
}
