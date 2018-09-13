package edu.uffs.storminho;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class Tree {
	public static void main(String args[]) throws Exception {
	     // train classifier
	     J48 cls = new J48();
	     File f;
	     if(Variables.FOLDER_CONTROL) {
	    	 f = new File(Variables.DATA_FOLDER + "training/" + Variables.TRAININGSET_OUTPUT_FILE);
	     } else {
	    	 f = new File( Variables.ARFF_PATH+Variables.TRAININGSET_OUTPUT_FILE);
	     }
	     
	     System.out.println(f.exists());
	     
	     BufferedReader bf = new BufferedReader(new FileReader(f));
	 
	     Instances data = new Instances(bf);
	     
	     data.setClassIndex(data.numAttributes() - 1);
	     cls.buildClassifier(data);
	 
	     // display classifier
	     final javax.swing.JFrame jf = 
	       new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
	     jf.setSize(1000,800);
	     jf.getContentPane().setLayout(new BorderLayout());
	     TreeVisualizer tv = new TreeVisualizer(null,
	         cls.graph(),
	         new PlaceNode2());
	     jf.getContentPane().add(tv, BorderLayout.CENTER);
	     jf.addWindowListener(new java.awt.event.WindowAdapter() {
	       public void windowClosing(java.awt.event.WindowEvent e) {
	         jf.dispose();
	       }
	     });
	 
	     jf.setVisible(true);
	     tv.fitToScreen();
	   }

}
