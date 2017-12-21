package Word2Vec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import weka.core.parser.java_cup.internal_error;

import Main.Function;

import com.ansj.vec.Word2VEC;

public class Writevect {
    
	public static void writevect(HashMap<String, String> topterm, int termsize) throws IOException {
        Word2VEC w1 = new Word2VEC() ;
        w1.loadGoogleModel(".\\Word2Vec\\PTTcorpus.bin") ;
        
        Iterator iter1 = topterm.entrySet().iterator();
        String words[] = new String[termsize];
        int i = 0;
        while(iter1.hasNext()){ 
        	Map.Entry entry1 = (Map.Entry)iter1.next();
        	String term = entry1.getKey().toString();
        	words[i] = term;
        	i++;
        }
        
        Map<String,String> map = new HashMap<String,String>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(".\\Word2Vec\\20160928termVector("+termsize+").txt"));
        
        for(String w:words)
        {
        	if(map.containsKey(w))
        		continue;
        	else
        	{
        		map.put(w, w);
        	}
        	float vec[] = w1.getWordVector(w);
        	if(vec!=null)
        	{
        		
        		String vecString = Arrays.toString(vec);
        		
        		vecString = vecString.replaceAll("\\[","");
        		vecString = vecString.replaceAll("\\]","");
        		vecString = vecString.replaceAll(" ","");
        		
        		writer.append(w+","+vecString);
        		writer.newLine();
        	}
        }
        writer.flush();
        writer.close();
        
    }
	
	
	public static float[] getvect(String terms, HashMap<String, Float> cent) throws IOException{
		
		 Word2VEC w1 = new Word2VEC() ;
	     w1.loadGoogleModel(".\\Word2Vec\\PTTcorpus.bin");
	     
	     String[] term = terms.split(" ");
	     float[] finalvec = new float[200]; //word2vec共有200維
	     ArrayList<float[]> vectors = new ArrayList<>();
	     
	     for(String w:term){
	    	 float[] vec = w1.getWordVector(w);
	    	 float[] sentvect = new float[200];
	    	 if(vec!=null){
	    		 
	    		 if(cent.containsKey(w)){
	    			 for(int i = 0; i<sentvect.length;i++){
	    				 sentvect[i] += vec[i]*cent.get(w); //每個維度乘上Centrality score
	    			 }
	    		 }else{
	    			 for(int i = 0; i<sentvect.length;i++){
	    				 sentvect[i] += vec[i];
	    			 }
	    		 }
	    		 
	    	 }
	    	 vectors.add(sentvect);	    	 
	     }
	     
	     for(int i=0; i<vectors.size();i++){
	    	 float[] temp = vectors.get(i);
	    	 for(int j = 0; j<temp.length;j++){
	    		 finalvec[j] = finalvec[j] + temp[j]; //將每個字的vector維度相加
	    	 }
	     }
	     
	     for(int j = 0; j<finalvec.length;j++){
	    	 finalvec[j] = finalvec[j]/vectors.size(); //每個維度做平均
	     }
				
		return finalvec;
		
	}
	
	
	public static double Sem(String queryterms, String word) throws IOException{
	
		Word2VEC w1 = new Word2VEC() ;
	     w1.loadGoogleModel(".\\Word2Vec\\OPvectors.bin");
	     
	     
	     String[] term = queryterms.split(" ");
	     double[] cosines = new double[term.length]; 
	     double[] wordvec = Function.convertFloatsToDoubles(w1.getWordVector(word));
	     
	     int i = 0;
	     for(String w:term){
	    	 double[] vec = Function.convertFloatsToDoubles(w1.getWordVector(w));
	  
	    	 if(vec!=null){
	    	
	    		 double cosine = Function.cosineSimilarity(wordvec, vec);
	    		 
	    	  	 cosines[i] = cosine;
	    	 }
	    	 
	    	 i++;
	     }
	     
	     double max = 0.0;
	    
	     for(int j = 0; j<cosines.length;j++){
	    	 if(cosines[j]>max){
	    		 max = cosines[j];
	    	 }
	     }
		
		return max;
	}
	
}
