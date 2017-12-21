package Word2Vec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weka.core.parser.java_cup.internal_error;

import Main.Function;

import com.ansj.vec.Word2VEC;

public class W2VDistance {
	
    public static ArrayList<String> WordDistance(ArrayList<String> topterm, HashMap<String, Float> dflist) throws IOException {
        Word2VEC w1 = new Word2VEC() ;
        w1.loadGoogleModel("\\Word2Vec\\hanvectors.bin") ;
       
        ArrayList<double[]> seeds = new ArrayList<double[]>();
        
        for(int i = 0; i<topterm.size();i++){  //將選出的的topterm當作Seed
        	String term = topterm.get(i);
        	double[] seed = Function.convertFloatsToDoubles(w1.getWordVector(term));
        	seeds.add(seed);
        }
 
        ArrayList<String> result = new ArrayList<String>();
        Iterator iter1 = dflist.entrySet().iterator();      
        while(iter1.hasNext()){ 
        	Map.Entry entry1 = (Map.Entry)iter1.next();
        	String term = entry1.getKey().toString();
        	double[] first = Function.convertFloatsToDoubles(w1.getWordVector(term));
        	double sim = 0.0;
        	for(int i = 0; i<seeds.size();i++){
        		double cos = Function.cosineSimilarity(first, seeds.get(i));
        		sim = sim + cos;
        	}
        	result.add(term+":"+sim);
        	
        }
        
        Function.bubblesort(result);
        for(int i =99; i<result.size(); i++){
        	result.remove(i);
        }
		return result;
    
        
    }
    
  
}