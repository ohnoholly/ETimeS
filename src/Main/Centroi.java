package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Centroi {
	 
	private static ArrayList<String> result = new ArrayList<String>();
	
	public static ArrayList<String> getresult(){
		
		return result;
	}
	
	
	public static HashMap<String, Float> centroi(ArrayList<String> FileList, HashMap<String, Float> dflist, HashMap<String,HashMap<String, Float>> all_tf){
	    	
			HashMap <String,Float> FinalTermWeight = new HashMap <String,Float> ();
			
			
			int docNum = FileList.size();
	        
			for(int i = 0; i < docNum ; i++){
	            String filepath = FileList.get(i);
	
	            HashMap<String, Float> temp = all_tf.get(filepath);  //取出每個檔案中term的tf值
	            String [] Term = temp.keySet().toArray(new String[temp.keySet().size()]); //把tf的hash中的key值 轉成陣列
	            
	            
	            for(int k=0;k<Term.length;k++){
	            	Term[k] = Term[k].replaceAll(" ", "");
	            }
	         
	            if (Term.length==1){ //判斷此文件中是不是只有一個詞
 				FinalTermWeight.put(Term[0], (float) 1);
 			}
 			else {

 				ArrayList <float[]> CumRFList = new ArrayList <float[]>();
 				//設定CumRFList的初始値;
 				for (int k=0;k<Term.length;k++){
 					float CumRF [] = new float[Term.length];
 					for (int j=0;j<Term.length;j++){
 						if(k!=j){ //不要計算到自己
 							
 							CumRF[j]=Function.ComputeRF(temp.get(Term[k]),temp.get(Term[j]));
 						}
 	
 					}
 					CumRFList.add(CumRF);
 				}
 				float preTermWeight [] = new float[Term.length];
 				for (int k=0;k<Term.length;k++){
 					preTermWeight[k]=1;
 				}
 				float TermWeight [] = new float[Term.length];
 				boolean convergence=false;
 				int count=0;
 				//計算每個term的權重，直到收斂
 				while(!convergence){
 					count++;
 				
 					for (int k=0;k<Term.length;k++){
 						float weight=0;
 						float CumRF [] = CumRFList.get(k);
 						for (int j=0;j<Term.length;j++){
 							if (k!=j){
 								weight+=CumRF[j]*preTermWeight[j];
 							}
 						}
 						TermWeight[k] = weight;
 					}
 					convergence=Function.IsConvergence(preTermWeight,TermWeight);
 					
 					for(int k=0;k<Term.length;k++){
 						preTermWeight[k]=TermWeight[k];
 						System.out.print(Term[k]+" "+preTermWeight[k]+" ");
 					}
 					System.out.println();
 			
 				}
 				System.out.println("convergence time is "+count);
 				
 				
 				
 				float max=0;
 				for (int k=0;k<Term.length;k++){
 					TermWeight[k]=TermWeight[k]*dflist.get(Term[k]);
 					
 					if (TermWeight[k]>max){
 						max=TermWeight[k];
 					}
 				}
 				//將Final Term Weight加到HashMap中
 				for (int k=0;k<Term.length;k++){
 					if (TermWeight[k]!=0){
 						   					
 						String termK = new String(Term[k]);   					
 						
 						if(FinalTermWeight.containsKey(termK.toString())){
 							FinalTermWeight.put(termK,FinalTermWeight.get(termK)+TermWeight[k]/max);
 							
 						}else{
 							FinalTermWeight.put(termK,TermWeight[k]/max);
 						}
 						
 					}
 				}
// 				System.out.println("centroi:"+FinalTermWeight);
 				
 			
 			}	            
	            
	           
	        }
			
			
			HashMap<String, Float> resultcent = new HashMap<>();
			
			 Iterator iter = FinalTermWeight.entrySet().iterator();
	            while(iter.hasNext()){
	                Map.Entry entry = (Map.Entry)iter.next();
	                String word = entry.getKey().toString();
	                Float centro = FinalTermWeight.get(entry.getKey().toString());
	               // System.out.println("結果:"+word+"|"+centro);
	                result.add(word+":"+centro);
	                resultcent.put(word, centro);
	   
	            }
			
	           result = Function.bubblesort(result);
	           
	           String[] maxsStrings = result.get(0).split(":");
		       Float max = Float.parseFloat(maxsStrings[1]);
	           
		        System.out.println("最後Cetroi score:");
		        for(int i = 0; i<result.size();i++){
		        	String[] temp = result.get(i).split(":");
		        	Float norm = Float.parseFloat(temp[1]);
		        	norm = norm/max;
		        	String normalize = temp[0]+":"+norm;
		        	result.set(i, normalize);
		        	System.out.println(result.get(i));
		        }
				return resultcent;
			
			
	    }
}
