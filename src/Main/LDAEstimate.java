package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;

import java_cup.terminal;

import javax.xml.bind.attachment.AttachmentMarshaller;

import org.ansj.app.crf.pojo.TempFeature;

import Word2Vec.Writevect;

import weka.core.parser.java_cup.internal_error;


public class LDAEstimate {
	
	private static HashMap<Integer,HashMap<String, Double>> Pcw = new HashMap<>();
	private static HashMap<Integer, HashMap<String, Double>> Pwc = new HashMap<>(); //每一群的PWC
	private static double[] apc = new double[30];
	
	
	public static void LDApro() throws Exception{
		
		
		 	BufferedReader br = new BufferedReader(new FileReader(".\\LDA\\model-final.theta"));
	        
		 	
		 	
		 	while(br.ready()){
	        	String content= br.readLine();
	        	String[] split = content.split(" ");
	        	double[] pc = new double[30];
	        	for(int i = 0; i < split.length; i++){
	        		pc[i] = Double.parseDouble(split[i]);
	        		apc[i] = apc[i] + pc[i];
	        	}
	        	
	        }
	        br.close();
	                
	        BufferedReader br1 = new BufferedReader(new FileReader(".\\LDA\\model-final.twords"));
	        int i = 0;
	        
	        HashMap<String, Double> pwc = null;
	        HashMap<String, HashMap<Integer, Double>> pwcc = new HashMap<>();
	        while(br1.ready()){
	        	String content= br1.readLine();
	        	
	        	if(content.contains(":")){	        		
	        		
	        		pwc = new HashMap<>();
	        		Pwc.put(i, pwc);
	        		i++;
        		
	        	}
	        	while (!content.contains(":")) {
	        		String[] split = content.split(" ");
	        		String[] term = split[0].split("\t");
	        		pwc.put(term[1], Double.parseDouble(split[1])); //取得P(W|C)
	        		
	        		if(pwcc.containsKey(term[1])){
	        			pwcc.get(term[1]).put(i-1, Double.parseDouble(split[1]));
	        		}else{
	        			HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
	        			temp.put(i-1, Double.parseDouble(split[1]));
	        			pwcc.put(term[1], temp);
	        		}
	        		
					break;
				}
	        		        	
	        	
	        }
	        br1.close();
	        
	        HashMap<String, Double> Pw = new HashMap<>();
	        Iterator iter1 = pwcc.entrySet().iterator(); 
	        while(iter1.hasNext()){
	        	
	        	Map.Entry entry = (Map.Entry)iter1.next();
	            String terms = entry.getKey().toString();
	            HashMap<Integer, Double> temp = new HashMap<>();
	            temp = pwcc.get(terms);
	            Iterator iters = temp.entrySet().iterator();
	            Double pw = 0.0;
	            while(iters.hasNext()){
	            	 Map.Entry entrys = (Map.Entry)iters.next();
	 	             Integer top = Integer.parseInt(entrys.getKey().toString());
	 	             double val = temp.get(top);
	 	             pw = pw + (val*apc[top]); //P(W) = P(W|C1)*P(C1)+P(W|C2)*P(C2)+...
	 	            
	            }
	            Pw.put(terms, pw);
	   	           
	        }
	        
	        System.out.println(Pw);
	        
	        
	        for(int j = 0; j<apc.length; j++){	        	
	        	apc[j] = apc[j]/200;	     //取得 P(C)   	
	        	HashMap<String, Double> temp = new HashMap<>();
	        	temp = Pwc.get(j);
	        	ArrayList<String> pcw = new ArrayList<>();
	        	HashMap<String, Double> pcwHashMap = new HashMap<>();
	        	Iterator iter = temp.entrySet().iterator(); 
		        while(iter.hasNext()){
		            Map.Entry entry = (Map.Entry)iter.next();
		            String term = entry.getKey().toString();
		            double pcwtemp = 0.0;
		            
		            pcwtemp = (temp.get(term)*apc[j])/Pw.get(term); //貝氏定理
		            pcw.add(term+":"+pcwtemp);
		            pcwHashMap.put(term, pcwtemp);
		         
		           
		           
		        }
		        
		        Function.bubblesort(pcw);
		        Pcw.put(j, pcwHashMap);
		        
		        System.out.println("第"+j+"群:");
		        for(int k = 0; k<pcw.size();k++){
		        	System.out.println(pcw.get(k));
		        }
	        		        	
	        }
	        
	        HashMap<String, String> pcwgroup = new HashMap<>();
	        for(int k = 0; k<Pcw.size();k++){
	        	HashMap<String, Double> temp = new HashMap<>();
	        	temp = Pcw.get(k);
	        	Iterator iter = temp.entrySet().iterator(); 
	 	        while(iter.hasNext()){
	 	            Map.Entry entry = (Map.Entry)iter.next();
	 	            String term = entry.getKey().toString();	 	            
	 	            double val = temp.get(term); //取得term的pcw
	 	          
	 	            	
	 	            if(pcwgroup.containsKey(term)){
	 	            	String[] maxid = pcwgroup.get(term).split(":"); 
	 	            	double max = Double.parseDouble(maxid[1]);
	 	            	if(val>max){
	 		 	            	max = val;
	 		 	            	pcwgroup.put(term, k+":"+max);
	 	            	}
	 	            }else{
	 	            	double max = val;
	 	            	pcwgroup.put(term, k+":"+max);
	 	            	
	 	            }	 	            
	 	        }	        	
	        }
	        
	        //將PCW依據每個字的群最大機率值，將字分群
	        HashMap<Integer, ArrayList<String>> pcwresult = new HashMap<>();
	        Iterator iter2 = pcwgroup.entrySet().iterator(); 
	        while(iter2.hasNext()){
	        	Map.Entry entry = (Map.Entry)iter2.next();
	        	String term = entry.getKey().toString();
	        	String[] content = entry.getValue().toString().split(":");
	        	int id = Integer.parseInt(content[0]);
	        	if(pcwresult.containsKey(id)){
	        		pcwresult.get(id).add(term+":"+content[1]);
	        	}else{
	        		ArrayList<String> temp = new ArrayList<>();
	        		temp.add(term+":"+content[1]);
	        		pcwresult.put(id, temp);
	        	}
	        }
	        
	        System.out.println("P(C|W)分群結果:");
	        for(int k = 0; k<pcwresult.size();k++){
	        	System.out.println("第"+k+"群");
	        	ArrayList<String> temp = new ArrayList<>();
	        	temp = pcwresult.get(k);
	        	Function.bubblesort(temp);
	        	for(int h = 0; h<temp.size();h++){
	        		System.out.println(pcwresult.get(k).get(h));
	        	}
	        }
	        
	        
	
	
	}
	
	public static void postclassify(HashMap<Integer, String> sents, HashMap<String, String> Sentime, ArrayList<Integer> daycount,HashMap<String, Float> dflist) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(".\\LDA\\model-final.theta"));
	 	
		HashMap<Integer, ArrayList<String>> result = new HashMap<>(); ; //分類句子
		HashMap<Integer, ArrayList<String>> Pcww = new HashMap<>();
		int j = 0;
		
	 	while(br.ready()){
	 		double pcs = 0.0; //計算每句的機率
	 		String content= br.readLine();
        	String[] split = content.split(" ");
        	double[] pc = new double[30];
        	double max = 0.0;
        	ArrayList<Integer> topic = new ArrayList<>();
        	for(int i = 0; i < split.length; i++){
        		pc[i] = Double.parseDouble(split[i]);
        		if(pc[i]>max){
        			max = pc[i];        			
        		}       		        		
        	}
        	int count = 0;
        	for(int i = 0; i < split.length; i++){
        		pc[i] = Double.parseDouble(split[i]);        		
        		if(pc[i]==max){
        			topic.add(i);
        			count++;
        		}
        		
        	}
        	
        	System.out.println(j+"句重複的最大值:"+count);
        	String all = sents.get(j);
			String[] pair = all.split(":");
			System.out.println(j+"句為:"+pair[0]);
			Sentence newSent = new Sentence(pair[0], pair[1]);
			newSent.setpc(max);
        	if(count<=2){
            	
            	for(int k=0; k<topic.size();k++){
            		if(result.containsKey(topic.get(k))){       		
                		
                		String[] term = pair[1].split(" ");
            			for(int i = 0; i<term.length;i++){
            				Pcww.get(topic.get(k)).add(term[i]);
            				if(Pcw.get(topic.get(k)).get(term[i])!=null){
            					pcs = pcs + Pcw.get(topic.get(k)).get(term[i]); 
            				}
            			}   			
            			//result.get(topic.get(k)).add(pair[0]+":"+pcs); //放句子 後面數值為Pcw加總
            			result.get(topic.get(k)).add(pair[0]+":"+newSent.pc); //放句子 後面數值為Pcw加總
            			
            		}else{
            			ArrayList<String> temp = new ArrayList<>();
            			ArrayList<String> temp2 = new ArrayList<>();
            			
            			//放入單詞
            			String[] term = pair[1].split(" ");   			
            			for(int i = 0; i<term.length;i++){
            				temp2.add(term[i]);
            				if(Pcw.get(topic.get(k)).get(term[i])!=null){
            					pcs = pcs + Pcw.get(topic.get(k)).get(term[i]); 
            				}
            				   				
            			}
            			Pcww.put(topic.get(k), temp2);
            			  			
            			//放入句子
            			temp.add(pair[0]+":"+newSent.pc);
            			result.put(topic.get(k), temp);
            		}
            	}
        	}

			
        	
        	j++;
        	
        }
        br.close();
        
    	
        ArrayList<String> resultentro  = new ArrayList<>(); ; //每群的entro 數
    	for(int i = 0; i<result.size(); i++){  //i 為群數 k為句子數
    		System.out.println("-------////////////////////////////////////////////////-------");
    		ArrayList<String> temp = new ArrayList<String>();
    		if(result.containsKey(i)){
        		temp = result.get(i);
        		Function.bubblesort(temp);
        		ArrayList<String> group1 = new ArrayList<>();
        		ArrayList<String> group2 = new ArrayList<>();
        		ArrayList<String> group3 = new ArrayList<>();
        		ArrayList<String> group4 = new ArrayList<>();
        		
        		
        		System.out.println("第"+i+"群");
        		for(int k = 0; k<temp.size(); k++){
        			String[] pair = temp.get(k).split(":");
        			String time = null;
        			if(Sentime.containsKey(pair[0])){
        				time = Sentime.get(pair[0]);
        			}
        			
        			String subString = (String) time.subSequence(0, 8);
    				if(subString.equals("20150927")){					
    					
    					group1.add(temp.get(k));
    					
    				}else if(subString.equals("20150928")){
    					group2.add(temp.get(k));
    					
    				}else if(subString.equals("20150929")){
    					group3.add(temp.get(k));
    					
    				}else if(subString.equals("20150930")){
    					
    					group4.add(temp.get(k));
    				}
        		
        		}
        		double day1 = 0.0;
        		double day1p = 0.0;
        		System.out.println("2015/09/27");
        		for(int x = 0; x<group1.size();x++){
        			System.out.println(group1.get(x));
        		}
        		if(group1.size()!=0){
        			day1 = -(((double)group1.size()/(double)temp.size())*Function.log2((double)group1.size()/(double)temp.size()));
        			day1p = (((double)group1.size()/(double)temp.size()));
        		}else{
        			day1 = 0.0;
        		}
        		
        		System.out.println("第一天:"+day1p);
        		
        		
        		double day2 = 0.0;
        		double day2p = 0.0;
        		System.out.println("2015/09/28");
        		for(int x = 0; x<group2.size();x++){
        			System.out.println(group2.get(x));
        		}
        		if(group2.size()!=0){
        			day2 = -(((double)group2.size()/(double)temp.size())*Function.log2((double)group2.size()/(double)temp.size()));
        			day2p = (((double)group2.size()/(double)temp.size()));
        		}else{
        			day2 = 0.0;
        		}
        		System.out.println("第二天:"+day2p);
        		
        		
        		double day3 = 0.0;
        		double day3p = 0.0;
        		System.out.println("2015/09/29");
        		for(int x = 0; x<group3.size();x++){
        			System.out.println(group3.get(x));
        		}
        		if(group3.size()!=0){
        			day3 = -(((double)group3.size()/(double)temp.size())*Function.log2((double)group3.size()/(double)temp.size()));
        			day3p = (((double)group3.size()/(double)temp.size()));
        		}else{
        			day3 = 0.0;
        		}
        		System.out.println("第三天:"+day3p);
        	
        		
        		double day4 = 0.0;
        		double day4p = 0.0;
        		System.out.println("2015/09/30");
        		for(int x = 0; x<group4.size();x++){
        			System.out.println(group4.get(x));
        		}
        		if(group4.size()!=0){
        			day4 = -(((double)group4.size()/(double)temp.size())*Function.log2((double)group4.size()/(double)temp.size()));
        			day4p = (((double)group4.size()/(double)temp.size()));
        		}else{
        			day4 = 0.0;
        		}
        		System.out.println("第四天:"+day4p);
        	
        		
        		double sum = day1+day2+day3+day4;
        		System.out.println("句子亂度:"+sum);
        		resultentro.add(i+":"+sum);
    		}

    	
    	}
    	
    		Function.bubblesort(resultentro);
    		for(int i = 0; i<resultentro.size();i++){
    			System.out.println(resultentro.get(i));
    		}
    	
  /* 	
    	for(int i =0; i<Pcww.size(); i++){
    		if(Pcww.containsKey(i)){
    			ArrayList<String> temp = new ArrayList<>();
        		temp = Pcww.get(i);
        		//System.out.println("第"+i+"群: ");
        		Map <String,Double> map = new HashMap<String,Double>();
        		for(String k: temp){
        			if(map.containsKey(k)){
        				map.put(k, map.get(k)+1);
        			}else{
        				map.put(k, 1.0);
        			}
        		}
        		
        		 Iterator iter = map.entrySet().iterator(); 
     	        while(iter.hasNext()){
     	            Map.Entry entry = (Map.Entry)iter.next();
     	            double count = Double.parseDouble(entry.getValue().toString());
     	            count = count/map.size();
     	            map.put(entry.getKey().toString(), count);
     	            
     	        }
        		
        		//System.out.println("結果:"+map);
    		}
    		
    	}
   */ 	
    	
    	
	}
	
	public static void EntroPwc(HashMap<String, Double> entropy){
		 
		for(int i = 0; i<apc.length;i++){
			ArrayList<String> majority = new ArrayList<>();
			System.out.println("第"+i+"群:");
			HashMap<String, Double> pcww = new HashMap<>();
			HashMap<String, Double> temp = new HashMap<>();
        	temp = Pwc.get(i);
        	pcww = Pcw.get(i);
        	Iterator iter = temp.entrySet().iterator(); 
        	while(iter.hasNext()){
	            Map.Entry entry = (Map.Entry)iter.next();
	            String term = entry.getKey().toString();
	            double major = 0.0;
	            major = (temp.get(term)/entropy.get(term))*pcww.get(term);
	            majority.add(term+":"+major);
	        }
        	Function.bubblesort(majority);
        	for(int j = 0; j<majority.size();j++){
        		System.out.println(majority.get(j));
        	}
		
		}
		
		
		
		
		
	}
	
	public static void semanticsim(HashMap<String, Float> dflist, ArrayList<Sentence> factsents) throws IOException{
		
		HashMap<Integer, String> group = new HashMap<>();
		double k1 = 2.0, b = 0.75;
		for(int i = 0; i<apc.length;i++){
			HashMap<String, Double> temp = new HashMap<>();
			temp = Pwc.get(i); //取出每群的Pwc
			
			String represent = "";
			
			Iterator iter = temp.entrySet().iterator();
			double sum = 0.0;
			double averg  = 0.0;
			while(iter.hasNext()){ //算平均
	            Map.Entry entry = (Map.Entry)iter.next();
	            String term = entry.getKey().toString();
	            double pwc = temp.get(term);
	            sum = sum + pwc;
	        }
			averg = sum/temp.size();
			
			Iterator iter1 = temp.entrySet().iterator();
			while(iter1.hasNext()){ 
	            Map.Entry entry = (Map.Entry)iter1.next();
	            String term = entry.getKey().toString();
	            double pwc = temp.get(term);
	    
	            if(pwc>(averg*1.25)){
	            	represent = represent+term+" ";
	            }
	        }
					
			group.put(i, represent);
		}
		
		double sumlen = 0.0;
		double aveglen = 0.0;
		for(int i = 0; i<factsents.size();i++){
			int length = factsents.get(i).sentence.length();
			sumlen = sumlen + length;
		}
		aveglen = sumlen / factsents.size();
	
		
		for(int i = 0; i<group.size();i++){
			String represent = group.get(i);
			
			System.out.println("第"+i+"群:");
			System.out.println("代表關鍵字有:"+represent);
			
			
			for(int k = 0; k<factsents.size();k++){
				
				Sentence tempsent = factsents.get(k);
				String[] terms = tempsent.terms.split(" ");
				
				double sumbm25 = 0.0;
				for(int j = 0; j<terms.length;j++){
					
					String term = terms[j];
					
					
					
					if(dflist.containsKey(term)){
						double idf = Function.log2(1/dflist.get(term));
						double sem = Writevect.Sem(represent, term); //計算得出Sem
						double bm25 = idf*((sem*(k1+1))/(sem+(k1*(1-b+b*(tempsent.sentence.length()/aveglen)))));
					
						if(!Double.isNaN(bm25)){
							sumbm25 = sumbm25 + bm25;
						}
						
					}else{
						double sem = Writevect.Sem(represent, term); //計算得出Sem
						double bm25 = ((sem*(k1+1))/(sem+(k1*(1-b+b*(tempsent.sentence.length()/aveglen)))));
		
						
						if(!Double.isNaN(bm25)){
							sumbm25 = sumbm25 + bm25;
						}
					}
					
				}
			
				System.out.println(tempsent.sentence+"的BM25值:"+sumbm25);
			}
			
				
		}
		
		
	}

	
	
}
