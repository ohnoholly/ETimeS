package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weka.core.parser.java_cup.internal_error;

public class TimeAna {
	
	public static HashMap<String, Double> Entropy(ArrayList<String> group, HashMap<String, String> files){
		
		HashMap<String, Double> groupfreq = new HashMap<>();
		double termcount = 0.0;
		for(int i = 0; i<group.size();i++){   //數群中的term frequency
			
			String[] content = group.get(i).split(":");
			String[] terms = content[0].split(" ");
			for(int j = 0; j< terms.length;j++){
				termcount++;
				if(groupfreq.containsKey(terms[j])){
					groupfreq.put(terms[j], groupfreq.get(terms[j])+1);
				}else{
					groupfreq.put(terms[j], 1.0);
				}
			}
		}
			
		HashMap<String, Double> allfreq = new HashMap<>();
		Iterator iter = files.entrySet().iterator();  //數所有句子的term frequency
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            String[] terms = entry.getValue().toString().split(" ");
            for(int i = 0; i<terms.length;i++){
            	
            	if(allfreq.containsKey(terms[i])){
            		allfreq.put(terms[i], allfreq.get(terms[i])+1);
            	}else{
            		allfreq.put(terms[i], 1.0);
            		
            	}
            }
         
        }
       
        HashMap<String, Double> groupentro = new HashMap<>();
       
        double termpro = 0.0;
        double entropy = 0.0;
        Iterator iter2 = groupfreq.entrySet().iterator();  //根據群內的term算機率
        while(iter2.hasNext()){
            Map.Entry entry = (Map.Entry)iter2.next();
            String term = entry.getKey().toString();
            termpro = groupfreq.get(term)/termcount;
            entropy = -(termpro*Function.log2(termpro));
            
            groupentro.put(term, entropy);
        }
		return groupentro;
		
	}
	
	
	public static HashMap<String, Double> SentEntropy(ArrayList<String> sentgroup){
		
		
		
		
		return null;
		
		
	}
	
}
