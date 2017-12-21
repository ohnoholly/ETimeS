package Main;

	import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.security.KeyStore.Entry;
import java.util.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.json.JSONException;

	public class tfidf {

	    /**
	     * @param args
	     */    
	    private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file	   
	    private static HashMap<String,String> StopwordHash = new HashMap<String, String>();
	    private static HashMap<String,String> Summary = new HashMap<String, String>();
	    private static ArrayList<String> dfvalue = new ArrayList<String>();
	    
	    public tfidf(ArrayList<String> fileList, HashMap<String,String> summary, HashMap<String,String> stopwordHash){
	    	
	    	FileList = fileList;
	    	StopwordHash = stopwordHash;
	    	Summary = summary;
	    }
	    
	    public HashMap<String,String> getsummary(){
			
	    	return Summary;
	    	
	    }
	    
	    public ArrayList<String> getfileList(){
			return FileList;
	    	
	    }
	    
	    public ArrayList<String> getdfvalue(){
			return dfvalue;
	    	
	    }
	    
	

	    //get list of file for the directory, including sub-directory of it
	    public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException
	    {
	        try
	        {
	            File file = new File(filepath);
	            if(!file.isDirectory())
	            {
	                System.out.println("輸入的[]");
	                System.out.println("filepath:" + file.getAbsolutePath());
	            }
	            else
	            {
	                String[] flist = file.list();
	                for(int i = 0; i < flist.length; i++)
	                {
	                    File newfile = new File(filepath + "/" + flist[i]);
	                    if(!newfile.isDirectory())
	                    {
	                        FileList.add(newfile.getAbsolutePath());
	                    }
	                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
	                    {
	                        readDirs(filepath + "/" + flist[i]);
	                    }                    
	                }
	            }
	        }catch(FileNotFoundException e)
	        {
	            System.out.println(e.getMessage());
	        }
	        return FileList;
	    }
	    
	    //read file
	    public static String readFile(String file) throws FileNotFoundException, IOException
	    {
	        StringBuffer strSb = new StringBuffer(); //String is constant， StringBuffer can be changed.
	        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file)); //byte streams to character streams
	        BufferedReader br = new BufferedReader(inStrR); 
	        String line = br.readLine();
	        
	        while(line != null){
	        	
	        	String[] linea = line.split(";");
	        	if(linea.length !=0){
	        		strSb.append(linea[0]);
		        	Summary.put(linea[0],linea[1]);
	        	}
	        	
	        	
	            line = br.readLine();    
	        }
	        
	        return strSb.toString();
	    }
	    
	    //word segmentation
	    public static ArrayList<String> cutWords(String file) throws IOException{
	    	 ArrayList<String> words = new ArrayList<String>();
	    	
	    	 String text = tfidf.readFile(file);
	    	 System.out.println("原本的字:"+text);
	    	 
	    	 FileWriter fw = new FileWriter("cutword.txt",true);
	    	 
	    	 StringTokenizer tokens = new StringTokenizer(text);

	    	 while(tokens.hasMoreTokens()) {
	    		 String temp = tokens.nextToken().intern();
	    		 if(!temp.equals(StopwordHash.get(temp))){
	    			 words.add(temp.intern());
	    			 fw.write(temp.intern()+" ");
	    		 }	    		 
	    		
	         }
	    	 
	    	 fw.flush();
	    	 fw.close();
	    	 
	    	 System.out.println("切割後的:"+words);
	    	 
	    	 return words;
	    }
	    
	    //term frequency in a file, times for each word
	    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords){
	        HashMap<String, Integer> resTF = new HashMap<String, Integer>();
	        
	        for(String word : cutwords){
	            if(resTF.get(word) == null){
	                resTF.put(word, 1);
	                System.out.println(word);
	            }
	            else{
	                resTF.put(word, resTF.get(word) + 1);
	                System.out.println(word.toString());
	            }
	        }
	        return resTF;
	    }
	    
	    //term frequency in a file, frequency of each word
	    public static HashMap<String, Float> tf(ArrayList<String> cutwords){
	        HashMap<String, Float> resTF = new HashMap<String, Float>();
	        
	        int wordLen = cutwords.size();
	        HashMap<String, Integer> intTF = tfidf.normalTF(cutwords); 
	        
	        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
	        while(iter.hasNext()){
	            Map.Entry entry = (Map.Entry)iter.next();
	            String term = entry.getKey().toString().replaceAll(" ", "");
	            resTF.put(term, Float.parseFloat(entry.getValue().toString()) / wordLen);
	            System.out.println(entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
	        }
	        return resTF;
	    } 
	    
	    //tf times for file
	    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException{
	        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String,Integer>>();
	        
	        List<String> filelist = tfidf.readDirs(dirc);
	        for(String file : filelist){
	            HashMap<String, Integer> dict = new HashMap<String, Integer>();
	            ArrayList<String> cutwords = tfidf.cutWords(file); //get cut word for one file
	            
	            dict = tfidf.normalTF(cutwords);
	            allNormalTF.put(file, dict);
	        }    
	        return allNormalTF;
	    }
	    
	    //tf for all file
	    public static HashMap<String,HashMap<String, Float>> tfAllFiles(String dirc) throws IOException{
	        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
	        List<String> filelist = tfidf.readDirs(dirc);
	        
	        for(String file : filelist){
	            HashMap<String, Float> dict = new HashMap<String, Float>();
	            ArrayList<String> cutwords = tfidf.cutWords(file); //get cut words for one file
	            
	            dict = tfidf.tf(cutwords);
	            allTF.put(file, dict);
	        }
	        return allTF;
	    }
	    public static HashMap<String, Float> idf(HashMap<String,HashMap<String, Float>> all_tf, HashMap<String, Float> dflist){
	        HashMap<String, Float> resIdf = new HashMap<String, Float>();
	        HashMap<String, Integer> dict = new HashMap<String, Integer>();
	        int docNum = FileList.size();
	        
	        for(int i = 0; i < docNum; i++){
	            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
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
	        //System.out.println("IDF for every word is:");
	        Iterator iter_dict = dict.entrySet().iterator();
	        
	        while(iter_dict.hasNext()){
	            Map.Entry entry = (Map.Entry)iter_dict.next();
	            float value = Float.parseFloat(entry.getValue().toString()); //document frequency(df) value
	            float idfvalue = (float)Math.log(docNum / Float.parseFloat(entry.getValue().toString())); 
	            resIdf.put(entry.getKey().toString(), idfvalue);
	            dfvalue.add(entry.getKey().toString()+":"+value/FileList.size());
	            dflist.put(entry.getKey().toString(), value/FileList.size()); //為了之後centroi score
	            //System.out.println(entry.getKey().toString() + " = " + value);
	        }

	        
	        Function.bubblesort(dfvalue);
	        
	        System.out.println("term的df值:");
	        for(int i = 0; i<dfvalue.size();i++){
	        	
	        	System.out.println(dfvalue.get(i));
	        }
	        return resIdf;
	    }
	    
	    public static HashMap<String, Float> tf_idf(HashMap<String,HashMap<String, Float>> all_tf,HashMap<String, Float> idfs){
	        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<String, HashMap<String, Float>>();
	            
	        int docNum = FileList.size();
	        for(int i = 0; i < docNum; i++){
	            String filepath = FileList.get(i);
	            HashMap<String, Float> tfidf = new HashMap<String, Float>();
	            HashMap<String, Float> temp = all_tf.get(filepath);
	            ArrayList<String> temps = new ArrayList<String>();
	            Iterator iter = temp.entrySet().iterator();
	            while(iter.hasNext()){
	                Map.Entry entry = (Map.Entry)iter.next();
	                String word = entry.getKey().toString();
	                Float value = (float)Float.parseFloat(entry.getValue().toString()) * idfs.get(word); 
	                tfidf.put(word, value);
	                temps.add(word+":"+value);
	            }
	            
	            temps = Function.bubblesort(temps);
	            	          
	            System.out.println(filepath+"的TF-IDF排序:\r\n"+temps);
	            System.out.println(" ");
	            resTfIdf.put(filepath, tfidf);
	        }
	        System.out.println("TF-IDF for Every file is :");
	        DisTfIdf(resTfIdf);
	        HashMap<String, Float> average = new HashMap<String, Float>(); 
	        average = Averagetfidf(resTfIdf);
	        
	        return average;
	    }
	    
	    
	    public static void DisTfIdf(HashMap<String, HashMap<String, Float>> tfidf){
	        Iterator iter1 = tfidf.entrySet().iterator();
	        while(iter1.hasNext()){
	            Map.Entry entrys = (Map.Entry)iter1.next();
	            System.out.println("FileName: " + entrys.getKey().toString());
	            System.out.print("{");
	            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
	            Iterator iter2 = temp.entrySet().iterator();
	            
	            while(iter2.hasNext()){
	                Map.Entry entry = (Map.Entry)iter2.next();
	               
	                System.out.print(entry.getKey().toString() + " = " + entry.getValue().toString() + ", ");
	            }
	            System.out.println("}");
	        }
	        
	    }
	    
	    public static HashMap<String, Float> Averagetfidf(HashMap<String, HashMap<String, Float>> tfidf){
	    	
	    	HashMap<String, Float> ave = new HashMap<String, Float>();
	    	HashMap<String, Integer> count = new HashMap<String, Integer>();
	    	
	    	Iterator iter1 = tfidf.entrySet().iterator();
	        while(iter1.hasNext()){
	        	 Map.Entry entrys = (Map.Entry)iter1.next();
	        	 HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue(); //放入每個post的tfidf
	        	 
	        	 Iterator iter2 = temp.entrySet().iterator();
		            
		            while(iter2.hasNext()){
		            	Map.Entry entry = (Map.Entry)iter2.next();
		            	if(ave.containsKey(entry.getKey())){
		            		
		            		Float temvalue = Float.parseFloat(entry.getValue().toString());
		            		ave.put(entry.getKey().toString(), ave.get(entry.getKey().toString())+temvalue);
		            		int tempc = 1 + count.get(entry.getKey().toString());
		            		count.put(entry.getKey().toString(), tempc);
		            	}else{
		            		ave.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()));
		            		count.put(entry.getKey().toString(), 1);
		            	}
		            }
	        }
	        System.out.println(count);
	        Iterator iter3 = ave.entrySet().iterator();
	        ArrayList<String> averageList = new ArrayList<String>();
	        while(iter3.hasNext()){
	        	
	        	 Map.Entry pointer = (Map.Entry)iter3.next();
	        	 float average = ave.get(pointer.getKey().toString())/(float)count.get(pointer.getKey().toString());
	        	 averageList.add(pointer.getKey().toString()+":"+average);
	        }
	        
	        Function.bubblesort(averageList);
	        
	        String[] maxsStrings = averageList.get(0).split(":");
	        Float max = Float.parseFloat(maxsStrings[1]);
	        ave.clear();
	        
	        System.out.println("平均TFIDF:");
	        for(int i=0; i<averageList.size(); i++){
	        	String[] temp = averageList.get(i).split(":");
	        	Float norm = Float.parseFloat(temp[1]);
	        	norm = norm/max;
	        	String normalize = temp[0]+":"+norm;
	        	averageList.set(i, normalize);
	        	ave.put(temp[0], norm);
	        	System.out.println(averageList.get(i));
	        }
			return ave;
	    	
	    }
	    
	    public static void features(HashMap<String, String> dterm,HashMap<String, Float> dfs, ArrayList<String> centroi, HashMap<String, Float> tfidf, HashMap<String,String> placeHash) throws IOException{
	   	 		
	    	 HashMap<String,String> gwordHash = new HashMap<String, String>();
	    	 HashMap<String,String> termterst = new HashMap<String, String>(); 
	    	
	    	
	    	BufferedReader br2 = new BufferedReader(new FileReader(".\\List\\gterm.txt"));
		        while(br2.ready()){
		        	String content= br2.readLine();				
					gwordHash.put(content, content);
		        }
		        br2.close();
		        
		        BufferedReader br3 = new BufferedReader(new FileReader(".\\List\\dttest.txt"));
		        while(br3.ready()){
		        	String content= br3.readLine();				
		        	termterst.put(content, content);
		        }
		        br3.close();
		       	
	    	
	    	
	    	FileWriter fw = new FileWriter("trainfeature.txt",false);
	    	FileWriter fw1 = new FileWriter("trainterm.txt",false);
	    	FileWriter fw2 = new FileWriter("testfeature.txt",false);
	    	FileWriter fw3 = new FileWriter("testterm.txt",false);
	    		
	   	 		for(int i = 0; i< centroi.size(); i++){
	    			
	    			String[] c = centroi.get(i).split(":");
	    			String term = c[0];
	    			if(dfs.containsKey(term)&&tfidf.containsKey(term)&&!placeHash.containsKey(term)){
	    				
	    				if(dterm.containsKey(term)){  //災害詞取training feature
	    					fw.write("1 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
	    					fw2.write("1 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
	    					fw1.write(term+"\r\n");
	    					fw3.write(term+"\r\n");
	    					
	    					
	    				}else{
	    						    					
	    					if(gwordHash.containsKey(term)){ //一般詞取training feature
	    						fw.write("0 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
	    						fw2.write("0 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
	    						fw1.write(term+"\r\n");
	    						fw3.write(term+"\r\n");
	    					}else{  //testing feature	    						
	    						if(termterst.containsKey(term)){
	    							fw2.write("1 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
		    						fw3.write(term+"\r\n");
	    						}else{
	    						fw2.write("0 1:"+dfs.get(term)+" 2:"+c[1]+" 3:"+tfidf.get(term)+"\r\n");
	    						fw3.write(term+"\r\n");
	    						}
	    					}
	    						    					
	    				}
	    				
	    				fw.flush();
	    				fw1.flush();
	    				fw2.flush();
	    				fw3.flush();
	    				
	    			}else{
	    				System.out.println("找不到此term:"+term);
	    			}
	    			
	    		}
	   	 	
	   	 		fw.close();
	   	 		fw1.close();
	   	 		fw2.close();
	   	 		fw3.close();
	    	
	    }
	   
	    
	    //--------------<找地點PO文>--------------
	    public static void Findplacepost(HashMap<String,HashMap<String, Float>> all_tf, HashMap<String,String> placeHash) throws IOException{
	    	FileWriter fw = new FileWriter("Place.txt",false);
	        FileWriter fw1 = new FileWriter("NoPlace.txt",false); 
	        int flag = 0;
	        int count = 0;
	        int noc = 0;
	        Iterator iter = all_tf.entrySet().iterator(); 
	         while(iter.hasNext()){ //檔案佇列
	             flag = 0;
	         	Map.Entry entry = (Map.Entry)iter.next();
	             HashMap<String, Float> termhash = new HashMap<String, Float>();
	             termhash = (HashMap<String, Float>) entry.getValue();
	             Iterator iter1 = termhash.entrySet().iterator();
	             while(iter1.hasNext()){ //單一檔案內算地名
	             	Map.Entry entry1 = (Map.Entry)iter1.next();
	             	String term = entry1.getKey().toString();
	             	
	             	if(placeHash.containsKey(term)){
	             		flag = 1;
	             		
	             	}
	             }
	            if(flag == 1){ 
	             String files = entry.getKey().toString();
	     		fw.write(files+"\r\n");
	     		fw.flush();
	     		count++;
	            }else{
	         	String files = entry.getKey().toString();
	         	fw1.write(files+"\r\n");
	     		fw1.flush();
	     		noc++;
	         	
	            }
	             
	         }
	         System.out.println("共有"+count+"篇有地名");
	         System.out.println(noc+"篇沒有地名");
	    }

	}
