package Word2Vec;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;

import javax.lang.model.element.Element;
import javax.swing.JFrame;

import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;




public class HierarchicalClustering {
	
	
	static HierarchicalClusterer clusterer;
	static Instances data;
	
		public static void main(String[] args) throws Exception {
			// Instantiate clusterer
			clusterer = new HierarchicalClusterer();
			clusterer.setOptions(new String[] {"-N", "5", "-L", "COMPLETE", "-P", "-D", "-B"});
			
			 ArffLoader arffLoader = new ArffLoader();
             arffLoader.setFile(new File("AKLDtoptermVector .arff"));
             Instances newData = arffLoader.getDataSet();
		/*	
			// Build dataset
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			attributes.add(new Attribute("A"));
			attributes.add(new Attribute("B"));
			attributes.add(new Attribute("C"));
			data = new Instances("Weka test", attributes, 3);
			
			//Dataset datas = FileHandler.loadDataset(new File("toptermVector.txt"), 0, ",");
			
			//data = new Instances();
			
			// Add data
			data.add(new DenseInstance(1.0, new double[] { 1.0, 0.0, 1.0 }));
			data.add(new DenseInstance(1.0, new double[] { 0.5, 0.0, 1.0 }));
			data.add(new DenseInstance(1.0, new double[] { 0.0, 1.0, 0.0 }));
			data.add(new DenseInstance(1.0, new double[] { 0.0, 1.0, 0.3 }));
		*/	
			// Cluster network
			clusterer.buildClusterer(newData);
			
			// Print Newick
			System.out.println(clusterer.graph());
				
			System.out.println(clusterer);

		}
		
	
}
