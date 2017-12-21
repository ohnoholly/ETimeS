package Word2Vec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java_cup.non_terminal;

import Main.Function;
import Main.Measure;
import Main.Sentence;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineDistance;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.XMeans;
import weka.core.Instances;
import weka.core.parser.java_cup.internal_error;
import weka.filters.unsupervised.attribute.AddCluster;

public class Kmean {
	
	static ArrayList<String> minterm = new ArrayList<>();
	static ArrayList<String> maxterm = new ArrayList<>();
	static int termnumber;
	
	public Kmean(String query, int para) throws Exception{
		minterm = null;
		maxterm = null;
		this.termnumber = para;
		kmeans(query,termnumber);
	}
	 
	
	public ArrayList<String> getminterm(){
			
	    	return minterm;
	    	
	    }
	    
	 public ArrayList<String> getmaxterm(){
			
		 	return  maxterm;
	    	
	    }
	
	public void kmeans(String query,int termsn) throws Exception
	{
		Dataset data = FileHandler.loadDataset(new File("./Word2Vec/"+query+"termVector("+termsn+").txt"), 0, ",");
		
		
		//System.out.println(data);
		
		int flag = 0;
		HashMap<Double, ArrayList<String>> klclus = new HashMap<Double, ArrayList<String>>();
		ArrayList<String> addtobad = new ArrayList<>();
		ArrayList<Double> kldrank = new ArrayList<>(); //存放ＫＬＤ值的ranking 順序
		double max = 0.0;//為了比較最大的ＫＬＤ群
		double min = 10.0; //為了比較最小的ＫＬＤ群
		
		int clustern = 3;
		while(flag == 0){
			
			SimpleKMeans skm = new SimpleKMeans(); 
			skm.setNumClusters(clustern); // 設定用 K-Means 要分成 5 群 
			
			double[] rank = new double[clustern]; //為了存放群ＫＬＤ值的陣列
			
			System.out.println("-------現在分成"+clustern+"群-------");
			
			//XMeans xm = new XMeans();

			//xm.setMaxNumClusters(15);
			//xm.setMinNumClusters(5);

			Clusterer jmlxm = new WekaClusterer(skm);
			Dataset[] clusters = jmlxm.cluster(data);
			
			Instances centroids = skm.getClusterCentroids(); //取出每群的群中心  
			
			HashMap<Integer, double[]> centers = new HashMap<>();
			
			for(int i=0; i<clustern; i++){
				double[] center = new double[200];
				for(int j = 0; j<200; j++){
					center[j] = centroids.instance(i).value(j);
				}			
			centers.put(i, center);  //記錄每群的中心點
			}

		
			int j = 1;
					
			for(Dataset d:clusters)
			{
				System.out.println("第"+j+"群: ");
				
				
				
				double klscore = 0.0;
				ArrayList<String> sims = new ArrayList<String>();
				double scoreave = 0.0;
				for(int i = 0 ; i < d.size() ; i++) //計算群內的term
				{	
					if(d.get(i).classValue().toString().length()>=2){
						double[] vectors = new double[200]; //200
						double kld = Measure.KLdivergence(d.get(i).classValue().toString(), "強震");//計算與颱風之KL值
						
						
						
						for(int k = 0; k<200; k++){
							vectors[k] = d.get(i).value(k);
						}
						
						double cosine = Function.cosineSimilarity(centers.get(j-1), vectors); //計算與群中心之相似度
						double avescore = cosine*kld; 
						scoreave = scoreave + avescore;
						klscore = klscore +kld; 
						sims.add(d.get(i).classValue()+":"+avescore);
					
					
					}
									
				}
				scoreave = scoreave/d.size();
				System.out.println("分數平均:"+scoreave);
				
				sims = Function.bubblesort(sims);
				ArrayList<String> temps = new ArrayList<>();
				
				for(int k=1; k<sims.size();k++){
				
					String[] con = sims.get(k).split(":");
					String[] con2 = sims.get(k-1).split(":");
					double sim = Double.parseDouble(con[1]);
					double sim2 = Double.parseDouble(con2[1]);
					double gap = sim2 - sim;
					//System.out.println(gap);
					
					if(sim<scoreave){
						temps.add(sims.get(k));
						addtobad.add(sims.get(k));
					}else{
						System.out.println(sims.get(k));	
					}
					
								
				}
				sims.removeAll(temps);
				
				klscore = klscore/d.size(); //kl值取平均
				rank[j-1] = klscore;
				System.out.println("KLScore:"+klscore);
				System.out.println();
				klclus.put(klscore, sims);
				j++;
			}
			
			for(int k = rank.length-1; k>0; k--){
	        	for(int m = 0; m<k; m++){       	
					double v = rank[m];
					double v2 = rank[m+1];
					if (v < v2){
						double tmp = rank[m];
						rank[m] = rank[m+1];
						rank[m+1] = tmp;
				      
					 }	
	        	}
	        }
			
				
			Iterator iters = klclus.entrySet().iterator();
			
			while(iters.hasNext()){
				 Map.Entry entry = (Map.Entry)iters.next();
				 double klv = Double.parseDouble(entry.getKey().toString());
				 if(klv > max){
					 max = klv;
				 }
				 if(klv<min){
					 min = klv;
				 }
			}
			
			
			/**
			 * 最後評估每群之間的ＫＬＤ是否有一群落單
			 */
			
			double[] gaps = new double[clustern-1];
			double ave = 0.0;
			for(int k = 0; k<rank.length-1;k++){
				kldrank.add(rank[k]);
				double gap = rank[k]-rank[k+1];
				gaps[k] = gap;
				ave = ave + gap;
			}
			ave = ave/rank.length; //為了取gap平均
			if(gaps[gaps.length-1]>ave){
				flag = 1;
			}
			
		clustern++;
		
		}//while 結束
		
		
	
		maxterm = klclus.get(max);
		minterm = klclus.get(min);
		
		minterm.addAll(addtobad);
		//System.out.println("KLDRank size:"+kldrank.size());
		for(int k = 1; k<kldrank.size();k++){
			maxterm.addAll(klclus.get(kldrank.get(k)));
			System.out.println(klclus.get(kldrank.get(k)));
		}
	
		
		
		
	}
}
