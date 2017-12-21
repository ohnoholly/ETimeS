package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Measure {
	
	private static HashMap<String, Integer> dict = new HashMap<String, Integer>();
    private static ArrayList<Set<String>> filecount = new ArrayList<Set<String>>();
    private static ArrayList<String> FileList;
    private static HashMap<String,HashMap<String, Float>> All_tf;
    private static HashMap<Integer, ArrayList<String>> fileterm = new HashMap<Integer, ArrayList<String>>(); //放每篇文章中的Term 
	
	public Measure(ArrayList<String> fileList, HashMap<String,HashMap<String, Float>> all_tf){
		FileList =fileList;
		All_tf = all_tf;
	}
	
	
	public void process(){
		
	     
		int docNum = FileList.size();
	        
	        for(int i = 0; i < docNum; i++){
	        	ArrayList<String> termtemp = new ArrayList<String>(); //暫存每篇文章中的Term
	            HashMap<String, Float> temp = All_tf.get(FileList.get(i));
	            filecount.add(temp.keySet());
	            Iterator iter = temp.entrySet().iterator();
	            while(iter.hasNext()){
	                Map.Entry entry = (Map.Entry)iter.next();
	                String word = entry.getKey().toString();
	                termtemp.add(word);
	                if(dict.get(word) == null){
	                    dict.put(word, 1);
	                }else {
	                    dict.put(word, dict.get(word) + 1);
	                }
	            }
	            fileterm.put(i, termtemp);
	        }
	        
	        System.out.println("Fileterm:"+fileterm);
	}
	
	
	public static double KLdivergence(String pre, String pos){
		
		
		float count = 0;
		float nocount = 0;
		double kld = 0.0;
		for(int i = 0; i<fileterm.size(); i++){
			ArrayList<String> temp = fileterm.get(i);
			if(temp.contains(pre)&&temp.contains(pos)){
				count++;
			}
			if(temp.contains(pre)&&!temp.contains(pos)){
				nocount++;
			}
		}
		
		if(count == 0){
			kld = 0.0;
		}else if(nocount == 0){
		    kld = 10.0;
		} else{	
			if(dict.containsKey(pre)){
				
				kld = (count/dict.get(pre))*Function.log2((count/dict.get(pre))/(nocount/((double)fileterm.size()-dict.get(pre))));
				
			}else{
				kld = 0.0;
			}
		}
		
		//System.out.println(pre+"的KLD:"+kld);		
		return kld;
		
	}
	
	public static ArrayList<String> getKLD(HashMap<String, String> topterm){
		
		ArrayList<String> KLD = new ArrayList<>();
		
		Iterator iter = topterm.entrySet().iterator();
		while(iter.hasNext()){
			 Map.Entry entry = (Map.Entry)iter.next();
			 String word = entry.getKey().toString();
			 double kldv = KLdivergence(word, "颱風");
			 if(kldv >= 5.0){
				 KLD.add(word+":"+kldv);
			 }
		
		}
		
		 Function.bubblesort(KLD);
	        for(int i =0; i<KLD.size(); i++){
	        	System.out.println(KLD.get(i));
	        }

		return KLD;		
	}
}
