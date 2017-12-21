package Main;

import java.awt.color.ICC_ColorSpace;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.omg.CORBA.PUBLIC_MEMBER;

import weka.core.parser.java_cup.internal_error;

import Word2Vec.W2VDistance;
import Word2Vec.Writevect;


public class SentenceAspect {
	
	public static void SentAspect(HashMap<String, Float> dflist,ArrayList<Sentence> factsents, HashMap<Integer, Sentence> sents) throws IOException{
		
		HashMap<String, ArrayList<Sentence>> result = new HashMap<>();
		
		double k1 = 2.0, b = 0.75;
		
		double sumlen = 0.0;
		double aveglen = 0.0;
		for(int i = 0; i<factsents.size();i++){
			int length = factsents.get(i).sentence.length();
			sumlen = sumlen + length;
		}
		aveglen = sumlen / factsents.size();
		
		
		for(int i = 0; i<sents.size(); i++){
		
			String words = sents.get(i).terms;
			String sentence = sents.get(i).sentence;
			
			System.out.println(sentence+":");
			
			HashMap<Double, Sentence> Maxium = new HashMap<>();
			
			for(int j = 0; j<factsents.size();j++){
				String fact = factsents.get(j).sentence;
				String[] terms = factsents.get(j).terms.split(" ");
				
				double sumbm25 = 0.0;
				for(int k = 0; k<terms.length; k++){
					
					String term = terms[j];
					
					if(dflist.containsKey(term)){
						double idf = Function.log2(1/dflist.get(term));
						double sem = Writevect.Sem(words, term); //計算得出Sem
						double bm25 = idf*((sem*(k1+1))/(sem+(k1*(1-b+b*(fact.length()/aveglen)))));
					
						if(!Double.isNaN(bm25)){
							sumbm25 = sumbm25 + bm25;
						}
						
					}else{
						double sem = Writevect.Sem(words, term); //計算得出Sem
						double bm25 = ((sem*(k1+1))/(sem+(k1*(1-b+b*(fact.length()/aveglen)))));
		
						
						if(!Double.isNaN(bm25)){
							sumbm25 = sumbm25 + bm25;
						}
					}
					
				}
				
				
				System.out.println(fact+"的BM25值:"+sumbm25);
				Maxium.put(sumbm25, factsents.get(j));
				
				
			}
			
			double max = 0.0;
			Iterator iter1 = Maxium.entrySet().iterator();
			while(iter1.hasNext()){ 
	            Map.Entry entry = (Map.Entry)iter1.next();
	            
	            double val = Double.parseDouble(entry.getKey().toString());
	            Sentence temp = Maxium.get(val);
	    
	            if(val>max){
	            	max = val;
	            }
	        }
			
			String maxfact = Maxium.get(max).sentence;
			//System.out.println("最大值的句子:"+maxfact);	
			
			if(result.containsKey(maxfact)){
				result.get(maxfact).add(sents.get(i));
			}else{
				ArrayList<Sentence> issue = new ArrayList<>();
				issue.add(sents.get(i));
				result.put(maxfact, issue);
			}
			
			
		
		}
		
		Iterator iter = result.entrySet().iterator();
		while(iter.hasNext()){ 
            Map.Entry entry = (Map.Entry)iter.next();
            
            String factsentence = entry.getKey().toString();
            System.out.println("事實:"+factsentence);	
            
            ArrayList<Sentence> temp = result.get(factsentence);
            for(int i = 0; i<temp.size();i++){
            	System.out.println(temp.get(i).sentence+":"+temp.get(i).score+"("+temp.get(i).time+")");	
            }
         
        }
		
	}
	
	
	public static void SentCosine(double w1, double w2, ArrayList<Sentence> factsents, HashMap<Integer, Sentence> sents, HashMap<String, Float> cent, HashMap<String, String> fPContain) throws IOException{
		
		
		HashMap<String, ArrayList<Sentence>> result = new HashMap<>();
		
		HashMap<String, Sentence> facts = new HashMap<String, Sentence>();
        for(int j = 0; j<factsents.size();j++){
        	facts.put(factsents.get(j).sentence, factsents.get(j));
        }
        
		for(int k = 0; k<sents.size(); k++){
						
			String terms = sents.get(k).terms;
			String sentence = sents.get(k).sentence;
			float[] vector = Writevect.getvect(terms, cent);
			sents.get(k).setvec(vector);
			double[] sents1 = Function.convertFloatsToDoubles(vector);
			int date = Integer.parseInt(sents.get(k).time);//評論的時間
			
			//System.out.println(sentence+":");
			
			HashMap<Double, Sentence> Maxium = new HashMap<>();
			for(int i = 0; i<factsents.size();i++){
				
				int fatdate = Integer.parseInt(factsents.get(i).time); //事實句發生時間
				
			
					double jaccard = SentsFilter.jaccard(factsents.get(i).terms, terms);
					
					//如果此句沒有跟事實句有類似的詞語將會被過濾掉
					if(jaccard!=0.0){
						float[] vec = factsents.get(i).sentvec;
						double[] sents2 = Function.convertFloatsToDoubles(vec);
						
						double cosine = Function.cosineSimilarity(sents1, sents2);
						double cosjac = cosine*1+jaccard*0.0;
						Maxium.put(cosjac, factsents.get(i));
					}
					
				
	
				
			}
			double max = 0.0;
			Iterator iter1 = Maxium.entrySet().iterator();
			while(iter1.hasNext()){ 
	            Map.Entry entry = (Map.Entry)iter1.next();
	            
	            double val = Double.parseDouble(entry.getKey().toString());
	      
	            if(val>max){
	            	max = val;
	            }
	        }
			
			double average = (w1*sents.get(k).score) + (w2*max); //計算挑選句子的分數
			sents.get(k).setscore(average); //重新設置分數
			String maxfact = "";
			
			if(!Maxium.containsKey(max)){
				continue;
			}else{
				maxfact = Maxium.get(max).sentence;
			}
			
			
			
			if(result.containsKey(maxfact)){
				result.get(maxfact).add(sents.get(k));
			}else{
				ArrayList<Sentence> issue = new ArrayList<>();
				issue.add(sents.get(k));
				result.put(maxfact, issue);
			}
		
		}
		
		int fpcount = 0;
		Iterator iter = result.entrySet().iterator();
		while(iter.hasNext()){ 
            Map.Entry entry = (Map.Entry)iter.next();
            
            String factsentence = entry.getKey().toString();
            System.out.println("事實:"+factsentence);	
            
            ArrayList<Sentence> temp = result.get(factsentence);
           
            //System.out.println("合併前大小:"+temp.size());
            Sentence tmp = facts.get(factsentence);
            
            ArrayList<ArrayList<Sentence>> aspect = new ArrayList<>();
            
           
            
		
            aspect = SentsFilter.HAC(temp, tmp);
            
            
            System.out.println("______________________________________________________________");	
         
            //-------------製作根據時間把句子按照時間分配
            //System.out.println("Aspect size"+aspect.size());
            
            HashMap<String, ArrayList<HashMap<String, ArrayList<Sentence>>>> finaltimeline = new HashMap<>();
            
            HashMap<String, Integer> factissuecount = new HashMap<>();
            for(int i = 0; i<aspect.size();i++){  //將HAC作出來的topic
            	HashMap<String, ArrayList<Sentence>> timeline = new HashMap<String, ArrayList<Sentence>>();
            	ArrayList<Sentence> conment = new ArrayList<>();
            	conment = aspect.get(i);
            	
            	int aspectid = 0;
            	
            	//System.out.println("第"+i+"主題");
            	//System.out.println("主題討論數:"+conment.size());
            	for(int j = 0; j<conment.size();j++){
            		Sentence issue = conment.get(j);
            		aspectid = issue.aspectid;
          		
            		 if(timeline.containsKey(issue.time)){
                   	  timeline.get(issue.time).add(issue);
                     }else{
                   	  ArrayList<Sentence> temorary = new ArrayList<>();
                   	  temorary.add(issue);
                   	  timeline.put(issue.time, temorary);
                     }
            	}
            	          	
            	
            	//-------最後呈現出來  
                Iterator itert = timeline.entrySet().iterator();      		
                double entropy = 0.0;
                while(itert.hasNext()){
                	HashMap<String, ArrayList<Sentence>> group = new HashMap<>();
                    Map.Entry entry2 = (Map.Entry)itert.next();
                    
                    String timedate = entry2.getKey().toString();
                    ArrayList<Sentence> temport = timeline.get(timedate);
                    //System.out.println("時間:"+timedate);
                    
                    if(factissuecount.containsKey(timedate)){
                    	int count = factissuecount.get(timedate);
                    	count = count + temport.size();
                    	factissuecount.put(timedate, count);
                    }else{
                    	factissuecount.put(timedate, temport.size());
                    }
                    
                    Function.bubblesortscore(temport);
                    //System.out.println("當天討論則數:"+temport.size());
              /*      
                    for(int k = 0; k<temport.size();k++){
                    	System.out.println(temport.get(k).sentence+":"+temport.get(k).score);
                    }
                    double entro = -(((double)temport.size()/(double)conment.size())*Function.log2((double)temport.size()/(double)conment.size()));
                    entropy = entropy + entro;
                    /* 
                    for(int i = 0; i<temport.size();i++){
                  	  System.out.println(temport.get(i).sentence+":"+temport.get(i).score);
                  	                    	  
                  	  
                  	  if(group.containsKey(temport.get(i).sentence)){
                  		  ArrayList<Sentence> sameissue = group.get(temport.get(i).sentence);                 		                   		  
                  		  for(int j = 0; j<sameissue.size();j++){
                  			  System.out.println("同樣句子:"+sameissue.get(j).sentence);
                  		  }
                  	  }
                  	  
                  	  
                    }
                    double entro = -(((double)temport.size()/(double)results.size())*Function.log2((double)temport.size()/(double)results.size()));
                    entropy = entropy + entro;
                    */
            
           
            
            
                    group.put("第"+aspectid+"主題", temport);
        		
                    if(finaltimeline.containsKey(timedate)){
                    	ArrayList<HashMap<String, ArrayList<Sentence>>> temporary = finaltimeline.get(timedate);             
                    	temporary.add(group);
                    	
                    
                    }else{
                    	ArrayList<HashMap<String, ArrayList<Sentence>>> temporary = new ArrayList<>();
                    	temporary.add(group);
                    	finaltimeline.put(timedate, temporary);
                    	
                    	
                    }
                
                }
                
                //System.out.println("主題亂度:"+entropy);
                
            	
            }
            double entr = 0.0; //事實亂度
            Iterator iter2 = factissuecount.entrySet().iterator();
			while(iter2.hasNext()){ 
	            Map.Entry entry2 = (Map.Entry)iter2.next();
	            String date = entry2.getKey().toString();
	            int num = factissuecount.get(date);
	            double entro = -(((double)num/(double)temp.size())*Function.log2((double)num/(double)temp.size()));
	            entr = entr + entro;
	        }            
			
            //results = similaritys(temp);    		
    		//System.out.println("合併後:"+results.size());
			
			
			
			Iterator iter3 = finaltimeline.entrySet().iterator();
			while(iter3.hasNext()){ 
	            Map.Entry entry3 = (Map.Entry)iter3.next();
	            String date = entry3.getKey().toString();
	            
	            ArrayList<HashMap<String, ArrayList<Sentence>>> tempory = finaltimeline.get(date);
	            System.out.println("時間:"+date+",討論主題數:"+tempory.size());
	            //取出時間底下各主題
	            for(int k = 0; k<tempory.size();k++){
	            	HashMap<String, ArrayList<Sentence>> topic = tempory.get(k);

	            	Iterator iter4 = topic.entrySet().iterator();
	            	while(iter4.hasNext()){
	            		Map.Entry entry4 = (Map.Entry)iter4.next();
	            		String top = entry4.getKey().toString();
	            		ArrayList<Sentence> sent = topic.get(top);
	            		System.out.println(top+", 主題討論數:"+sent.size());
	            		
	            		
	            		if(sent.size()>3){
	            			for(int j = 0; j<3;j++){
		            			System.out.println(sent.get(j).sentence+":"+sent.get(j).score);
		            			if(fPContain.containsKey(sent.get(j).sentence)){
		            				fpcount++;
		            			}
		            		}
	            		}else{
	            			for(int j = 0; j<sent.size();j++){
		            			System.out.println(sent.get(j).sentence+":"+sent.get(j).score);
		            			if(fPContain.containsKey(sent.get(j).sentence)){
		            				fpcount++;
		            			}
		            		}
	            		}
	            		
	            	}
	            }
	        }
			
			System.out.println("事實亂度:"+entr);
			System.out.println("--------------------------------------------------------------------");
         
        }
		
		System.out.println("不好句子比例:"+fpcount);
		
		
	}
	
public static void SentCosineChange(double thres, double w1, double w2, ArrayList<Sentence> factsents, HashMap<Integer, Sentence> sents, HashMap<String, Float> cent, HashMap<String, String> fPContain) throws IOException{
		
		FileWriter fw = new FileWriter("Finalresult.txt", true); 
		FileWriter fw1 = new FileWriter("Grouprresult.txt", true);
		
		//fw.write("WS:"+w1+"SS:"+w2+"\r\n");
		//System.out.println("WS:"+w1+"SS:"+w2);
		fw.flush();
	
		HashMap<String, ArrayList<Sentence>> result = new HashMap<>();
		
		HashMap<String, Sentence> facts = new HashMap<String, Sentence>();
        for(int j = 0; j<factsents.size();j++){
        	facts.put(factsents.get(j).sentence, factsents.get(j));
        }
        
		for(int k = 0; k<sents.size(); k++){
						
			String terms = sents.get(k).terms;
			String sentence = sents.get(k).sentence;
			float[] vector = Writevect.getvect(terms, cent);
			sents.get(k).setvec(vector);
			double[] sents1 = Function.convertFloatsToDoubles(vector);
			int date = Integer.parseInt(sents.get(k).time);//評論的時間
			
			//System.out.println(sentence+":");
			
			HashMap<Double, Sentence> Maxium = new HashMap<>();
			for(int i = 0; i<factsents.size();i++){
				
				
				
				
					double jaccard = SentsFilter.jaccard(factsents.get(i).terms, terms);
					
					//如果此句沒有跟事實句有類似的詞語將會被過濾掉
					if(jaccard!=0.0){
						float[] vec = factsents.get(i).sentvec;
						double[] sents2 = Function.convertFloatsToDoubles(vec);
						
						double cosine = Function.cosineSimilarity(sents1, sents2);
						double cosjac = (cosine+jaccard)/2;
						
						if(cosjac>thres){
							Maxium.put(cosjac, factsents.get(i));
						}
						
						
					}
					
				
	
				
			}
			double max = 0.0;
			Iterator iter1 = Maxium.entrySet().iterator();
			while(iter1.hasNext()){ 
	            Map.Entry entry = (Map.Entry)iter1.next();
	            
	            double val = Double.parseDouble(entry.getKey().toString());
	      
	            if(val>max){
	            	max = val;
	            }
	        }
			
			double average = (w1*sents.get(k).score) + (w2*max); //計算挑選句子的分數
			sents.get(k).setscore(average); //重新設置分數
			sents.get(k).setsim(max); //設定此句子與事實的相似度
			String maxfact = "";
			
			if(!Maxium.containsKey(max)){
				continue;
			}else{
				maxfact = Maxium.get(max).sentence;
			}
			
			
			
			if(result.containsKey(maxfact)){
				result.get(maxfact).add(sents.get(k));
			}else{
				ArrayList<Sentence> issue = new ArrayList<>();
				issue.add(sents.get(k));
				result.put(maxfact, issue);
			}
		
		}
		
		int fpcount = 0;
		Iterator iter = result.entrySet().iterator(); // result為事實與評論配對的結果 Key為事實句，value為評論句
		while(iter.hasNext()){ 
            Map.Entry entry = (Map.Entry)iter.next();
            
            String factsentence = entry.getKey().toString();
            System.out.println("事實:"+factsentence);	
            fw.write("事實:"+factsentence+"\r\n");
            fw1.write("事實:"+factsentence+"\r\n");
            fw.flush();
            fw1.flush();
            
            ArrayList<Sentence> temp = result.get(factsentence);
           
            //System.out.println("合併前大小:"+temp.size());
            Sentence tmp = facts.get(factsentence);
            
            ArrayList<ArrayList<Sentence>> aspect = new ArrayList<>();
         
          /*  
            for(int i = 0; i<temp.size();i++){
            	System.out.println(temp.get(i).sentence);
            	//fw.write(temp.get(i).sentence+":"+temp.get(i).similarity+"\r\n");
            	//fw.flush();
            }
          /*  
            fw.write("\r\n");
            fw.write("------------------------------------------------------"+"\r\n");
            fw.write("\r\n");
            fw.flush();
		*/
		
		
		//fw.close();
            
           
            aspect = SentsFilter.HAC(temp, tmp);
            
            
            System.out.println("______________________________________________________________");	
         
            //-------------製作根據時間把句子按照時間分配
            //System.out.println("Aspect size"+aspect.size());
            
            HashMap<String, ArrayList<HashMap<String, ArrayList<Sentence>>>> finaltimeline = new HashMap<>();
            
            
            
            for(int i= 0; i<aspect.size();i++){
            	
            	System.out.println("第"+i+"面向：");
            	fw.write("第"+i+"面向："+"\r\n");
            	fw.flush();
            	HashMap<String, ArrayList<Sentence>> timeline = new HashMap<String, ArrayList<Sentence>>(); //Key: Date 
            	ArrayList<Sentence> conmentset = new ArrayList<>();
            	conmentset = aspect.get(i); //取得i個aspect的評論集
            	
            	System.out.println(conmentset.get(0).sentence+":"+conmentset.get(0).score);
            	fw.write(conmentset.get(0).sentence+"\r\n");
            	fw.flush();
            	fw1.write("Representant:"+conmentset.get(0).sentence+"\r\n");
            	fw1.flush();
            	int aspectid = 0;
            	
            	for(int j = 0; j<conmentset.size(); j++){
            		
            		Sentence conment = conmentset.get(j);
            		aspectid = conment.aspectid;
            		fw1.write(conment.sentence+"\r\n");
            		fw1.flush();
            		
            		if(timeline.containsKey(conment.time)){
                     	  timeline.get(conment.time).add(conment);
                       }else{
                     	  ArrayList<Sentence> temorary = new ArrayList<>();
                     	  temorary.add(conment);
                     	  timeline.put(conment.time, temorary);
                       }
            		
            		
            	}
            	
            	Iterator itertime = timeline.entrySet().iterator();
            	while(itertime.hasNext()){
            		Map.Entry entrytime = (Map.Entry)itertime.next();
            		String date = entrytime.getKey().toString();
            		System.out.print("日期:"+date);
            		fw.write("日期:"+date+"\r\n");
            		
            		ArrayList<Sentence> tempcon = timeline.get(date); 
            		System.out.println("討論句子數:"+tempcon.size());
            		fw.write("討論句子數:"+tempcon.size()+"\r\n");
            		fw.flush();
            		Function.bubblesortscore(tempcon);
            		//System.out.println(tempcon.get(0).sentence+":"+tempcon.get(0).score);
            		
            		for(int k = 0; k<tempcon.size();k++){
            			
            			if(fPContain.containsKey(tempcon.get(k).sentence)){
                			fpcount++;
                		}else{
                			continue;
                		}
            		}
          		
            	}
            	
            	
            	fw1.write("-------------------------"+"\r\n");
            	fw1.write("\r\n");
            	fw1.flush();
            }
	
		
		}
		
		System.out.println("不好句子比例:"+fpcount);
		fw.write("不好句子比例:"+fpcount+"\r\n");
		fw.flush();
		fw.close();
		fw1.close();
		
	}
	
	
	public static HashMap<String, Sentence> simfilaritys(ArrayList<Sentence> temp){
		
		HashMap<String, Sentence> results = new HashMap<String, Sentence>();
		ArrayList<Sentence> fix = new ArrayList<>();
		fix.add(temp.get(0)); //放入第一名句子
        temp.remove(0);
        
        HashMap<String, ArrayList<Sentence>> group = new HashMap<>();
        
        while(!temp.isEmpty()){
        	
        	if(temp.size()!=1){
        		
        		for(int i = 0; i<temp.size();i++){
        			Sentence tempsent = temp.get(i);
        			
                	int flag = 0;
        			for(int j =0; j<fix.size();j++){
                        
                        Sentence fixsent = fix.get(j);
                        
                		double inter = Intersection(tempsent.terms, fixsent.terms);
                		
                		
                		if(inter>0.3){
                			if(group.containsKey(fixsent.sentence)){
                				group.get(fixsent.sentence).add(tempsent);
                				
                			}else{
                				ArrayList<Sentence> samesent = new ArrayList<>();
                				samesent.add(tempsent);
                				group.put(fixsent.sentence, samesent);
                			}
                			
                			
                		}else{
                			
                			results.put(tempsent.sentence, tempsent);
                			
                		}
                		
        			}
                	fix.add(temp.get(i));
        			temp.remove(temp.get(i));
                	
                }
        		
        		
        		
        	}else{
        		fix.add(temp.get(0));
        		temp.remove(temp.get(0));
        	}
        	
        }
		
		Iterator iter2 = group.entrySet().iterator();
		while(iter2.hasNext()){ 
            Map.Entry entry2 = (Map.Entry)iter2.next();
            
            String issueString = entry2.getKey().toString();
            
            
            ArrayList<Sentence> temps = group.get(issueString);
            for(int j = 0; j<temps.size();j++){
            	
            	if(results.containsKey(temps.get(j).sentence)){
            		results.remove(temps.get(j).sentence);
            	}
            }              
          
		}
			
		return results;
		
	}
	
	public static double Intersection(String sentterm1, String sentterm2 ){
		double small = 0 ;
		double big = 0 ;
		double intersection = 0;
		double union = 0 ;
		double div = 0 ;
		
		String[] inputList_1 = sentterm1.split(" ");
		String[] inputList_2 = sentterm2.split(" ");
		
		if(inputList_1.length>inputList_2.length)
		{
			big = inputList_1.length;
			small = inputList_2.length;
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(inputList_2[i].equals(inputList_1[j])){
						intersection += 1;
					}
				}
			}
		}else
		{
			big = inputList_2.length;
			small = inputList_1.length;
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(inputList_1[i].equals(inputList_2[j])){
						intersection += 1;
					}
				}
			}
		}
		
		union = big + small - intersection;
		
		div = (intersection/union);

		return div ;
	}

}
