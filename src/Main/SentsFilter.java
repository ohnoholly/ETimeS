package Main;

import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.swing.JFrame;

import main.java.com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import main.java.com.apporiented.algorithm.clustering.Cluster;
import main.java.com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import main.java.com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import main.java.com.apporiented.algorithm.clustering.visualization.DendrogramPanel;



public class SentsFilter {

	public static ArrayList<ArrayList<Sentence>> HAC(ArrayList<Sentence> issues, Sentence factsent) throws IOException {
		// TODO Auto-generated method stub
		
		
		
		HashMap<String, Sentence> conment = new HashMap<String, Sentence>();
        for(int j = 0; j<issues.size();j++){
        	conment.put(issues.get(j).sentence, issues.get(j));
        }
		
		
		String[] names = new String[issues.size()];
		for(int i = 0; i<issues.size();i++){
			names[i] = issues.get(i).sentence;
		}
		
		double[][] distances = new double[issues.size()][issues.size()]; //放入每個點的距離
		
		
		for(int i = 0; i<issues.size();i++){
			double[] sents1 = Function.convertFloatsToDoubles(issues.get(i).sentvec);			
			
			for(int j = issues.size()-1; j>=i;j--){
				
				if(i!=j){
					double[] sents2 = Function.convertFloatsToDoubles(issues.get(j).sentvec);
					double cosine = 1-Function.cosineSimilarity(sents1, sents2);				
					double jaccard = 1-jaccard(issues.get(i).terms, issues.get(j).terms);
					
					double distance = 0.4*cosine + 0.6*jaccard;
					
					distances[i][j] = distance;
					distances[j][i] = distance;
				}else{
					distances[i][j] = 0;
				}				
			}
		}

		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(distances, names,
		    new AverageLinkageStrategy());
		
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
	
		JFrame demo = new JFrame();
		demo.setSize(800,600);
		demo.getContentPane().add(BorderLayout.CENTER,dp);
		demo.setVisible(true);
		
		List<Cluster> topics = new ArrayList<>();
		topics = getTopics(cluster, 0.6f);       //砍樹
		//System.out.println("群數:"+topics.size());
			
		ArrayList<ArrayList<Sentence>> aspects = new ArrayList<>();
		
		for(int i = 0; i<topics.size();i++){
			
			ArrayList<Sentence> temp = new ArrayList<>();
			Stack<Cluster> s = new Stack<>();	
			s.push(topics.get(i));
			while(!s.isEmpty()){
				Cluster c = s.pop();
				if(c.isLeaf()){
					String nameString = c.getName().toString();
					Sentence sents = conment.get(nameString);
					temp.add(sents);
				}else{
					for(int j = 0 ; j < c.getChildren().size() ;j++){
						s.push(c.getChildren().get(j));
					}
				}
			}
			String termsstring = "";
			Function.bubblesortscore(temp);
			if(temp.size()>3){
				
				for(int j = 0; j<temp.size();j++){
					
					termsstring = termsstring + temp.get(j).terms+" ";
				}
				
				
				
					ArrayList<Sentence> goodissue = new ArrayList<>();
					System.out.println("第"+i+"主題:");
					System.out.println("主題討論數:"+temp.size());
					
					for(int j = 0; j<temp.size(); j++){
						System.out.println(temp.get(j).sentence+":"+temp.get(j).score);
						
						temp.get(j).setaspectid(i);
						goodissue.add(temp.get(j));		
					}
					
					//System.out.println("Jaccard:"+sim);
					aspects.add(goodissue);
					
				
				
			}
			
			
			
		}
		
		
		return aspects;		
	
	}
	
	public static List<Cluster> getTopics(Cluster cluster,float threshold){
		List<Cluster> topics = new ArrayList();
		Stack<Cluster> s = new Stack();
		s.push(cluster);
		while(!s.isEmpty()){
			Cluster c = s.pop();
			if(c.getDistanceValue()<=threshold){
				//System.out.println("距離"+c.getDistanceValue());
				topics.add(c);
			
			}else{
				for(int i = 0 ; i < c.getChildren().size() ;i++){
					s.push(c.getChildren().get(i));
				}
			}
		}
		return topics;
}
	
	public static double jaccard(String term1, String term2){
		
		double small = 0 ;
		double big = 0 ;
		double intersection = 0;
		double union = 0 ;
		double div = 0 ;
		String[] terms1 = term1.split(" ");
		String[] terms2 = term2.split(" ");
		
		if(terms1.length>terms2.length)
		{
			big = terms1.length;
			small = terms2.length;
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(terms2[i].equals(terms1[j])){
						intersection += 1;
					}
				}
			}
		}else
		{
			big = terms2.length;
			small = terms1.length;
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(terms1[i].equals(terms2[j])){
						intersection += 1;
					}
				}
			}
		}
		union = big + small - intersection;
		div = (intersection/union);
	
		
		return div;
	}
	
}
