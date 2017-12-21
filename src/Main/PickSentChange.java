package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;

import Graphstructure.Graph;
import Graphstructure.Vertex;
import Word2Vec.Writevect;

import com.alibaba.fastjson.parser.Feature;

import tw.cheyingwu.ckip.Term;
import weka.clusterers.SimpleKMeans;
import weka.core.parser.java_cup.internal_error;

public class PickSentChange {
	
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	private static HashMap<Integer, Sentence> sentence = new HashMap<>();
	private static HashMap<String, String> testsent = new HashMap<>();
	private static HashMap<String, String> abands = new HashMap<>();
	private static HashMap<String, String> sentTime = new HashMap<>(); //句子與時間
	private static ArrayList<Sentence> divfact = new ArrayList<Sentence>();
	
	public PickSentChange(String query,ArrayList<String> candidate, ArrayList<String> filter, HashMap<String,String> VerbHash, HashMap<String,String> placeHash,HashMap<String,String> TimeHash, HashMap<String, Float> cent, HashMap<String, String> fakesentTime) throws Exception{
		picking(query,candidate, filter, VerbHash, placeHash, TimeHash, cent, fakesentTime);
		factpicking(query,candidate, filter, VerbHash, placeHash, TimeHash, cent);
	}
	
	public HashMap<Integer, Sentence> getsentence(){
		return sentence;
	}
	
	
	public ArrayList<Sentence> getfactsent(){
		return divfact;
	}
	
	
	public HashMap<String, String> gettest(){
		
		return testsent;
		
	}
	
	public HashMap<String, String> getaband(){
		
		return abands;
		
	}
	
	public HashMap<String, String> getsentime(){
		
		return sentTime;
		
	}

	
	
	
	public void picking(String query, ArrayList<String> candidate, ArrayList<String> filter, HashMap<String,String> VerbHash, HashMap<String,String> placeHash,HashMap<String,String> TimeHash, HashMap<String, Float> cent, HashMap<String, String> fakesentTime) throws Exception{
		
		List<String> filelist = readDirs("Data/"+query+"/Long/Sentence");
		
		HashMap<String, Double> cands = new HashMap<>();
		HashMap<String, Double> stopword = new HashMap<>();
		
		for(int i=0; i<candidate.size(); i++){
			String[] can = candidate.get(i).split(":");
			cands.put(can[0], Double.parseDouble(can[1]));
		}
		for(int i=0; i<filter.size(); i++){
			String[] stop = filter.get(i).split(":");
			stopword.put(stop[0], Double.parseDouble(stop[1]));
		}
		
		
		ArrayList<String> sents = new ArrayList<>();
		ArrayList<String> aband = new ArrayList<>();
		
		HashMap<String, String> files = new HashMap<>(); //裝句子:Term
		
		
		double aveg = 0.0;
		double maxscore = 0.0;
		
		for(String file : filelist){ //數句子
			   String[] conternt = null;
			   double score = 0.0;
			   
			   String line = readFile(file);
	           conternt = line.split(";"); //切出句子和詞

	           if(conternt.length!=0){
    	  
	        	   files.put(conternt[1], conternt[0]);
	        	  
	        	   String[] Term = null;	
	        	   Term = conternt[0].split(" "); //切出每個詞
		           ArrayList<String> temp = new ArrayList<>(); //為了讓一個單詞只加到一次分數
	        	   	
	        	   if(Term != null &&Term.length !=1){
		        	  
	        		   double badword = 0;
	        		   double goodword = 0;
	        		  
		        	   for(int i = 0; i<Term.length; i++){
			        	  if(!temp.contains(Term[i])){
			        		  temp.add(Term[i]);
			        		  if(cands.containsKey(Term[i])){
			        			  double centerm = cent.get(Term[i]);
				        		  score = score + (cands.get(Term[i]))*centerm;
				        		  goodword++;
				        	
				        	  } //加總候選單字的分數
			        		  
			        		  if(stopword.containsKey(Term[i])){
			        			  score = score - (stopword.get(Term[i]));
			        			  badword ++;
			        		  }//將不好的字加入懲罰
			        	  }
			       
		        	   
		        	   }
		        	   //score = score/goodword;
		        	   
		        	   if(score>maxscore){
		        		   maxscore = score;
		        	   }
		        	   
		        	   
		        	   int len = conternt[1].length();
		        	   String[] commonlength = conternt[1].split("，");
		        	   if(commonlength.length !=0){
		        		   
		        		   double avercom = 0.0;
		        		   for(int j = 0; j<commonlength.length;j++){
		        			   avercom = avercom + commonlength[j].length();
		        		   }
		        		   
		        		   avercom = avercom/commonlength.length;
		        		   
		        		   for(int j = 0; j<commonlength.length;j++){
		        			   if(commonlength.length>avercom){
		        				   aband.add(conternt[1]);
		        			   }
		        		   }
		        		   
		        	   }
		        	   
		        	   if(len<25||len>70){
		        		   if(!aband.contains(conternt[1])){
		        			   aband.add(conternt[1]);
		        		   }
		        		 
		        	   }
		        	   
		        	   if(conternt[1].contains("#")&&!aband.contains(conternt[1])){
		        		   aband.add(conternt[1]);
		        	   }
		
		        	   
		        	   
		        	   if((badword/goodword+0.01)>0.5){
			        		   if(!aband.contains(conternt[1])){
			        			   aband.add(conternt[1]);
			        		   }
		        	   }
		        	   
		        	   if(!sents.contains(conternt[1]+":"+score)&&!aband.contains(conternt[1])){
			        		   sents.add(conternt[1]+":"+score);
			        		   testsent.put(conternt[1], conternt[1]);
			        		   sentTime.put(conternt[1], conternt[2]);
		        	   }
			        	   
		        	   
	        	   	}
	           
	        	   	//選出事實句
	        	   	
	        	   
	        	   	aveg = aveg + conternt[1].length();
	           }
		           
	    		
		}
		
		aveg = aveg/filelist.size();
		
		System.out.println("句子平均長度:"+aveg);
		
		
		Function.bubblesort(sents);
		
		System.out.println("原來Sent大小:"+sents.size());
/*		
		ArrayList<String> lowsent = new ArrayList<>();
		for(int i = 200; i<sents.size();i++){ 		//捨棄後200句
			lowsent.add(sents.get(i));
		}
		sents.removeAll(lowsent);
*/		
		
		System.out.println("刪掉一些後Sent大小:"+sents.size());


		for(int i =0; i<sents.size(); i++){			//把兩百句中的facts挑出來
			String[] c = sents.get(i).split(":");
			String termsplit = files.get(c[0]);
			String[] term = termsplit.split(" ");
        	String terms = "";
        	
				for(int j = 0; j<term.length;j++){
	        		
	        		if(term[j].length()>=2){ // 將一字單詞去除
	            	
	                	terms = terms + term[j]+" ";
	            	}
	        	}
			
		}

		
		System.out.println("後來Sent大小:"+sents.size());

		
		HashMap<String, ArrayList<String>> timeline = new HashMap<String, ArrayList<String>>();
		for(int i =0; i<sents.size(); i++){
			
			String[] c = sents.get(i).split(":");
			String termsplit = files.get(c[0]);
        	String[] term = termsplit.split(" ");
        	String terms = "";
					
			if(sentTime.containsKey(c[0])){   //c[0] 為完整句子
				for(int j = 0; j<term.length;j++){
	        		
	        		if(term[j].length()>=2){ // 將一字單詞去除

	                	terms = terms + term[j]+" ";
	            	}
	        	}
				
				Sentence picked = new Sentence(c[0], terms);
				
				String time = sentTime.get(c[0]);
				String subString = (String) time.subSequence(0, 8);
				
				picked.settime(subString); //句子放入時間
				picked.setscore(Double.parseDouble(c[1])/maxscore);
				
				//-------------製作根據時間把句子按照時間分配
				if(timeline.containsKey(subString)){
              	  timeline.get(subString).add(terms+":"+c[0]+time);
                }else{
              	  ArrayList<String> temorary = new ArrayList<>();
              	  temorary.add(terms+":"+c[0]+time);
              	  timeline.put(subString, temorary);
                }
	
				
				sentence.put(i, picked);
			}
 
        	System.out.println(sents.get(i));
        	
        	
        	
        }

 
        
/*		
        System.out.println("被過濾掉的句子有"+aband.size()+"句");
        for(int i = 0; i<aband.size(); i++){
        	System.out.println(aband.get(i));
        }
        
*/    
        
        
	}
	
	/**
	 * For Picking the fact sentence from short sentence.
	 * @param candidate
	 * @param filter
	 * @param VerbHash
	 * @param placeHash
	 * @param TimeHash
	 * @param cent
	 * @throws Exception
	 */
	

	public void factpicking(String query,ArrayList<String> candidate, ArrayList<String> filter, HashMap<String,String> VerbHash, HashMap<String,String> placeHash,HashMap<String,String> TimeHash, HashMap<String, Float> cent) throws Exception{
		
		List<String> filelist = readDirs("Data/"+query+"/Short/Sentence");
		
		HashMap<String, Double> cands = new HashMap<>();
		HashMap<String, Double> stopword = new HashMap<>();
		
		for(int i=0; i<candidate.size(); i++){
			String[] can = candidate.get(i).split(":");
			cands.put(can[0], Double.parseDouble(can[1]));
		}
		for(int i=0; i<filter.size(); i++){
			String[] stop = filter.get(i).split(":");
			stopword.put(stop[0], Double.parseDouble(stop[1]));
		}
		
		

		ArrayList<Sentence> factsents = new ArrayList<>();
		ArrayList<String> aband = new ArrayList<>();
		HashMap<String, String> files = new HashMap<>(); //裝句子:Term
		
	
		double maxscore = 0.0;
		
		for(String file : filelist){ //數句子
			   String[] conternt = null;
			   double score = 0.0;
			   
			   String line = readFile(file);
	           conternt = line.split(";"); //切出句子和詞
	           boolean place = false, verb = false, numbers = false, entity = false;
	           if(conternt.length!=0){
    	  
	        	   files.put(conternt[1], conternt[0]);
	        	  
	        	   String[] Term = null;	
	        	   Term = conternt[0].split(" "); //切出每個詞
		           ArrayList<String> temp = new ArrayList<>(); //為了讓一個單詞只加到一次分數
	        	   	
	        	   if(Term != null &&Term.length !=1){
		        	  
	        		   double badword = 0;
	        		   double goodword = 0;
	        		  
		        	   for(int i = 0; i<Term.length; i++){
			        	  if(!temp.contains(Term[i])){
			        		  temp.add(Term[i]);
			        		  if(cands.containsKey(Term[i])){
			        			  double centerm = cent.get(Term[i]);
				        		  score = score + (cands.get(Term[i]))*centerm;
				        		  goodword++;
				        		  entity = true;
				        	  } //加總候選單字的分數
			        	  }
			        	  
			        	  if(stopword.containsKey(Term[i])){
			        		  badword++;
			        	  }
			        	  
			        	  if(placeHash.containsKey(Term[i])){
			        		  place = true;
			        	  }
			        	  if(VerbHash.containsKey(Term[i])){
			        		  verb = true;
			        	  }
			        	  if(TimeHash.containsKey(Term[i])){
			        		  numbers = true;
			        	  }
			        	 
		        	   
		        	   }
		        	   //score = score/goodword;
		        	   
		        	   if(score>maxscore){
		        		   maxscore = score;
		        	   }
		        	   
		        	   
		        	   int len = conternt[1].length();
		        	   if(len<10||len>30){
		        		   if(!aband.contains(conternt[1])){
		        			   aband.add(conternt[1]);
		        		   }
		        		 
		        	   }
		        	   
		        	   if(numbers&&conternt[1].contains("#")&&!aband.contains(conternt[1])){
		        		   aband.add(conternt[1]);
		        	   }
		        	   
		        	   if((badword/goodword+0.01)>0.5){
		        		   if(!aband.contains(conternt[1])){
		        			   aband.add(conternt[1]);
		        		   }
		        	   }
		        	   
		        	   if(numbers&&place&&verb&&entity&&!aband.contains(conternt[1])&&!factsents.contains(conternt[1]+":"+score)){
		        		   	
		        	   		Sentence factsentce = new Sentence(conternt[1], conternt[0]);
		        	   		factsentce.settime(conternt[2]);
		        	   		factsentce.setscore(score);
		        	   		factsents.add(factsentce);
		        	   		
		        	   	}
		        	   
	        	   	}
	           
	        	   	//選出事實句
	        	 
	           }
		           
	    		
		}
		
		
	
		System.out.println("有:"+factsents.size()+"句事實");
	 
        ArrayList<Sort> sortlist = new ArrayList<>();
        HashMap<Integer, Sentence> sentset = new HashMap<Integer, Sentence>();
        
        
        Function.bubblesortscore(factsents);
        
        double maxsentscore = factsents.get(0).score;
        
        
        
        for(int i =0; i<factsents.size(); i++){
        	Sentence tempSent = factsents.get(i);
        	
			String subString = (String) tempSent.time.subSequence(0, 8);			
			tempSent.setvec(Writevect.getvect(factsents.get(i).terms, cent));
			tempSent.settime(subString);
	
			double updatescore = tempSent.score/maxsentscore;
			tempSent.setscore(updatescore);
			
			System.out.println(tempSent.sentence+":"+tempSent.score+"("+subString+")");
			
			String termsString = tempSent.terms;
			String[] termarray = termsString.split(" ");
			//計算Diversity---------
			ArrayList<String> inputList = new ArrayList<>();
			for(int j = 0; j<termarray.length;j++){
				inputList.add(termarray[j]);
			}
			Sort s = new Sort(i, tempSent.score, inputList);
    		sortlist.add(s);
    		sentset.put(i, tempSent);
			
        }
      
        Diversity div = new Diversity(sortlist);
        div.AllDiv(0.1,0.9, cent);
  
        System.out.println("計算Diversity後:");
        for(int i= 0 ; i<5 ; i++){
			 int ids = div.sortByDivList.get(i).id;
			 System.out.println(sentset.get(ids).sentence);
			 //System.out.println(sentset.get(ids).terms);
			 divfact.add(sentset.get(ids));
        }
        
   
        
/*		
        System.out.println("被過濾掉的句子有"+aband.size()+"句");
        for(int i = 0; i<aband.size(); i++){
        	System.out.println(aband.get(i));
        }
        
*/    
        
        
	}
	
	
	public static boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
		}
	
	
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
	
	public static String readFile(String file) throws FileNotFoundException, IOException
    {
        StringBuffer strSb = new StringBuffer(); //String is constant， StringBuffer can be changed.
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file)); //byte streams to character streams
        BufferedReader br = new BufferedReader(inStrR); 
        String line = br.readLine();
        
        while(line != null){
        	
        	
        		strSb.append(line);
	        	

        	
        	
            line = br.readLine();    
        }
        
        return strSb.toString();
    }

}
