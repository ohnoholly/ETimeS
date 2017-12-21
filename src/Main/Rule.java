package Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Rule {

	public static void supportconf(Integer total, Integer flag, HashMap<String,String> placeHash, ArrayList<String> FileList, HashMap<String,HashMap<String, Float>> all_tf, Integer fthres, Integer lthres, HashMap<String,String> roadHash) {
	
	     HashMap<String, Integer> dict = new HashMap<String, Integer>();
	     ArrayList<Set<String>> filecount = new ArrayList<Set<String>>();
	       int docNum = FileList.size();
	        
	        for(int i = 0; i < docNum; i++){
	            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
	            filecount.add(temp.keySet());
	            Iterator iter = temp.entrySet().iterator();
	            while(iter.hasNext()){
	                Map.Entry entry = (Map.Entry)iter.next();
	                String word = entry.getKey().toString();
	                if(dict.get(word) == null){
	                    dict.put(word, 1);
	                }else {
	                    dict.put(word, dict.get(word) + 1);
	                }
	            }
	        }
	        	Iterator itersup = dict.entrySet().iterator();
	        	ArrayList<String> termset = new ArrayList<String>();
	        	ArrayList<String> suprule = new ArrayList<String>();
	        	ArrayList<String> cofrule = new ArrayList<String>();
	        	ArrayList<String> supcofrule = new ArrayList<String>();
	        	ArrayList<String> liftrule = new ArrayList<String>();
	        	ArrayList<String> mulrule = new ArrayList<String>();
	        	ArrayList<String> kldrule = new ArrayList<String>();
	        	while(itersup.hasNext()){
	        		 Map.Entry entry = (Map.Entry)itersup.next();
	        		 termset.add(entry.getKey().toString());
	        	}
	        	for(int i =1; i<termset.size(); i++){
	        		if(dict.get(termset.get(i))>fthres){
		        		for(int j = 0; j<termset.size();j++){
		        			if(dict.get(termset.get(j))>lthres){
			        			if(!termset.get(i).equals(termset.get(j))){			        				
			        			
			        				String pre = termset.get(i);
			        				String pos = termset.get(j);
			        				
			        				
			        				
			        				if(flag == 1&&placeHash.containsKey(pre)&&roadHash.containsKey(pos)){ //地區
			        					
			        					float count = 0;
			        					float nocount = 0;
				        				for(int k = 0; k<filecount.size(); k++){
				        					Set<String> fileSet = filecount.get(k);
				        					if(fileSet.contains(pre)&&fileSet.contains(pos)){
				        					
				        						 count ++;
				        					}
				        					if(fileSet.contains(pre)&&!fileSet.contains(pos)){
				        						nocount++;
				        					}
				        				}
				        				
				        				float support = count/total;
				        				float confidence = count/dict.get(pre);
				        				float supcof = (support+confidence)/2;
				        				double lift = confidence/(dict.get(pos)/(double)total);				        								   				        
				        				double mul = support*Function.log2(lift);
				        				double kld = (count/dict.get(pre))*Function.log2((count/dict.get(pre))/(nocount/((double)total-dict.get(pre))));
				        				
				        				if(Double.isNaN(mul)){
				        					mul = 0.0;
				        				}if(Double.isNaN(kld)){
				        					kld = 0.0;
				        				}if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
				        				
				        				suprule.add(pre+"=>"+pos+":"+support);
				        				cofrule.add(pre+"=>"+pos+":"+confidence);
				        				supcofrule.add(pre+"=>"+pos+":"+supcof);
				        				liftrule.add(pre+"=>"+pos+":"+lift);
				        				mulrule.add(pre+"=>"+pos+":"+mul);
				        				kldrule.add(pre+"=>"+pos+":"+kld);
				        				
			        				}else if(flag == 2&&!placeHash.containsKey(pre)&&!placeHash.containsKey(pos)&&!pre.equals("颱風")&&!pos.equals("颱風")){ //事件-->事件
			        					float count = 0;
			        							
			        					for(int k = 0; k<filecount.size(); k++){
						        					Set<String> fileSet = filecount.get(k);
						        					if(fileSet.contains(pre)&&fileSet.contains(pos)){
						        					
						        						 count ++;
						        					}
						        					
						        		}
						        		float support = count/total;
						        		float confidence = count/dict.get(pre);
						        		float supcof = (support+confidence)/2;
						        		double lift = confidence/(dict.get(pos)/(double)total);
						        			
						        		if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
						        		
						        		suprule.add(pre+"=>"+pos+":"+support);
						        		cofrule.add(pre+"=>"+pos+":"+confidence);
						        		supcofrule.add(pre+"=>"+pos+":"+supcof);
						        		liftrule.add(pre+"=>"+pos+":"+lift);				
				        								     					        					
			        				}else if(flag == 3&&placeHash.containsKey(pre)&&!placeHash.containsKey(pos)){ //三件keyword規則
			        					
			        					float count = 0;
			        					float cond = 0;
				        							        					
			        						
	        						if(!termset.get(i-1).equals(termset.get(j))&&!termset.get(i-1).equals(termset.get(i))){	
		        						
	        							String mid = termset.get(i-1);
	        							
	        							
	        								for(int k = 0; k<filecount.size(); k++){
					        					Set<String> fileSet = filecount.get(k);
					        					if(fileSet.contains(pre)&&fileSet.contains(pos)&&fileSet.contains(mid)){
					        					
					        						 count ++;
					        					}
					        					
					        					if(fileSet.contains(pre)&&fileSet.contains(mid)){
						        					
					        						 cond ++;
					        					}
					        					
					        				}
	        							
	        							
	        							
				        				float support = count/total;
				        				float confidence = count/cond;
				        				float supcof = support*confidence;
				        				double lift = confidence/(dict.get(pos)/(double)total);
				        			
				        				if(Double.isNaN(support)){
				        					support= 0;
				        				}
				        				if(Double.isNaN(confidence)){
				        					confidence = 0;
				        				}
				        				if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
				        				if(Float.isNaN(supcof)){
				        					supcof = 0;
				        				}
				        				suprule.add(pre+"+"+mid+"=>"+pos+":"+support);
				        				cofrule.add(pre+"+"+mid+"=>"+pos+":"+confidence);
				        				supcofrule.add(pre+"+"+mid+"=>"+pos+":"+supcof);
				        				liftrule.add(pre+"+"+mid+"=>"+pos+":"+lift);				
		        					}
			        						
			        					
			        				}else if(flag == 4&&!roadHash.containsKey(pre)&&!placeHash.containsKey(pre)&&roadHash.containsKey(pos)&&!placeHash.containsKey(pos)){//階層式規則
			        					float count = 0;
			        					float nocount = 0;
				        				for(int k = 0; k<filecount.size(); k++){
				        					Set<String> fileSet = filecount.get(k);
				        					if(fileSet.contains(pre)&&fileSet.contains(pos)){
				        					
				        						 count ++;
				        					}
				        					if(fileSet.contains(pre)&&!fileSet.contains(pos)){
				        						nocount++;
				        					}
				        				}
				        				
				        				float support = count/total;
				        				float confidence = count/dict.get(pre);
				        				float supcof = support*confidence;
				        				double lift = confidence/(dict.get(pos)/(double)total);				        								   				        
				        				double mul = support*Function.log2(lift);
				        				double kld = (count/dict.get(pre))*Function.log2((count/dict.get(pre))/(nocount/((double)total-dict.get(pre))));
				        				
				        				if(Double.isNaN(mul)){
				        					mul = 0.0;
				        				}if(Double.isNaN(kld)){
				        					kld = 0.0;
				        				}if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
				        				
				        				suprule.add(pre+"=>"+pos+":"+support);
				        				cofrule.add(pre+"=>"+pos+":"+confidence);
				        				supcofrule.add(pre+"=>"+pos+":"+supcof);
				        				liftrule.add(pre+"=>"+pos+":"+lift);
				        				mulrule.add(pre+"=>"+pos+":"+mul);
				        				kldrule.add(pre+"=>"+pos+":"+kld);	
			        				}else if(flag == 5&&placeHash.containsKey(pre)&&!placeHash.containsKey(pos)){ //地區有颱風
			        					
			        					float count = 0;
			        					float nocount = 0;
				        				for(int k = 0; k<filecount.size(); k++){
				        					Set<String> fileSet = filecount.get(k);
				        					if(fileSet.contains(pre)&&fileSet.contains(pos)){
				        					
				        						 count ++;
				        					}
				        					if(fileSet.contains(pre)&&!fileSet.contains(pos)){
				        						nocount++;
				        					}
				        				}
				        				
				        				float support = count/total;
				        				float confidence = count/dict.get(pre);
				        				float supcof = support*confidence;
				        				double lift = confidence/(dict.get(pos)/(double)total);				        								   				        
				        				double mul = support*Function.log2(lift);
				        				double kld = (count/dict.get(pre))*Function.log2((count/dict.get(pre))/(nocount/((double)total-dict.get(pre))));
				        				
				        				if(Double.isNaN(mul)){
				        					mul = 0.0;
				        				}if(Double.isNaN(kld)){
				        					kld = 0.0;
				        				}if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
				        				
				        				suprule.add(pre+"=>"+pos+":"+support);
				        				cofrule.add(pre+"=>"+pos+":"+confidence);
				        				supcofrule.add(pre+"=>"+pos+":"+supcof);
				        				liftrule.add(pre+"=>"+pos+":"+lift);
				        				mulrule.add(pre+"=>"+pos+":"+mul);
				        				kldrule.add(pre+"=>"+pos+":"+kld);
				        				
			        				}else if(flag == 6&&!placeHash.containsKey(pre)&&!placeHash.containsKey(pos)){ //事件有颱風
			        					
			        					float count = 0;
			        					float nocount = 0;
				        				for(int k = 0; k<filecount.size(); k++){
				        					Set<String> fileSet = filecount.get(k);
				        					if(fileSet.contains(pre)&&fileSet.contains(pos)){
				        					
				        						 count ++;
				        					}
				        					if(fileSet.contains(pre)&&!fileSet.contains(pos)){
				        						nocount++;
				        					}
				        				}
				        				
				        				float support = count/total;
				        				float confidence = count/dict.get(pre);
				        				float supcof = support*confidence;
				        				double lift = confidence/(dict.get(pos)/(double)total);				        								   				        
				        				double mul = support*Function.log2(lift);
				        				double kld = (count/dict.get(pre))*Function.log2((count/dict.get(pre))/(nocount/((double)total-dict.get(pre))));
				        				
				        				if(Double.isNaN(mul)){
				        					mul = 0.0;
				        				}if(Double.isNaN(kld)){
				        					kld = 0.0;
				        				}if(Double.isNaN(lift)){
				        					lift = 0.0;
				        				}
				        				
				        				suprule.add(pre+"=>"+pos+":"+support);
				        				cofrule.add(pre+"=>"+pos+":"+confidence);
				        				supcofrule.add(pre+"=>"+pos+":"+supcof);
				        				liftrule.add(pre+"=>"+pos+":"+lift);
				        				mulrule.add(pre+"=>"+pos+":"+mul);
				        				kldrule.add(pre+"=>"+pos+":"+kld);
				        				
			        				}
			        				
			        				
			        			}
		        			}
		        		}
	        		}
	        	}		       
	        	suprule = Function.bubblesort(suprule);
	        	cofrule = Function.bubblesort(cofrule);
	        	supcofrule = Function.bubblesort(supcofrule);
	        	liftrule = Function.bubblesort(liftrule);
	        	mulrule = Function.bubblesort(mulrule);
	        	kldrule = Function.bubblesort(kldrule);
	        	
	        System.out.println("--------------Support----------");
	        for(int i = 0; i< suprule.size();i++){
	        	System.out.println(suprule.get(i));
	        }
	        
	        System.out.println("--------------Support----------");
	        System.out.println("--------------Cofidence----------");
	        for(int i = 0; i< cofrule.size();i++){
	        	System.out.println(cofrule.get(i));
	        }
	        
	        System.out.println("--------------Cofidence----------");
	        System.out.println("--------------SupCof----------");
	        for(int i = 0; i< supcofrule.size();i++){
	        	System.out.println(supcofrule.get(i));
	        }
	        
	        System.out.println("--------------SupCof----------");
	        System.out.println("--------------Lift----------");
	        for(int i = 0; i< liftrule.size();i++){
	        	System.out.println(liftrule.get(i));
	        }
	        
	        System.out.println("--------------Lift----------");
	        System.out.println("--------------Mul----------");
	        for(int i = 0; i< mulrule.size();i++){
	        	System.out.println(mulrule.get(i));
	        }
	        
	        System.out.println("--------------Mul----------");
	        System.out.println("--------------KLD----------");
	        for(int i = 0; i< kldrule.size();i++){
	        	System.out.println(kldrule.get(i));
	        }
	        
	        System.out.println("--------------KLD----------");
		
	}

}
