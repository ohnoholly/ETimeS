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

public class PickSent {
	
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	private static HashMap<Integer, Sentence> sentence = new HashMap<>();
	private static HashMap<String, Double> finalentro = new HashMap<>();
	private static ArrayList<Integer> daycount = new ArrayList<>(); //計算每天句子數
	private static HashMap<String, String> testsent = new HashMap<>();
	private static HashMap<String, String> abands = new HashMap<>();
	private static HashMap<String, String> sentTime = new HashMap<>(); //句子與時間
	private static ArrayList<Sentence> divfact = new ArrayList<Sentence>();
	
	public PickSent(ArrayList<String> candidate, ArrayList<String> filter, HashMap<String,String> VerbHash, HashMap<String,String> placeHash,HashMap<String,String> TimeHash, HashMap<String, Float> cent, HashMap<String, String> fakesentTime) throws Exception{
		picking(candidate, filter, VerbHash, placeHash, TimeHash, cent, fakesentTime);
	}
	
	public HashMap<Integer, Sentence> getsentence(){
		return sentence;
	}
	
	
	public ArrayList<Sentence> getfactsent(){
		return divfact;
	}
	
	public HashMap<String, Double> getentropy(){
		return finalentro;
	}
	
	public ArrayList<Integer> getdays(){
		
		return daycount;
		
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

	
	
	
	public void picking(ArrayList<String> candidate, ArrayList<String> filter, HashMap<String,String> VerbHash, HashMap<String,String> placeHash,HashMap<String,String> TimeHash, HashMap<String, Float> cent, HashMap<String, String> fakesentTime) throws Exception{
		
		List<String> filelist = readDirs(".\\Data\\20160116Election(Long)\\Sentence");
		
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
		ArrayList<String> factsents = new ArrayList<>();
		ArrayList<Sentence> facts = new ArrayList<>();
		ArrayList<String> aband = new ArrayList<>();
		HashMap<String, String> files = new HashMap<>(); //裝句子:Term
		
		
		double aveg = 0.0;
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
		        	   if(len<25||len>70){
		        		   if(!aband.contains(conternt[1])){
		        			   aband.add(conternt[1]);
		        		   }
		        		 
		        	   }
		        	   
		        	   if(numbers&&conternt[1].contains("#")&&!aband.contains(conternt[1])){
		        		   aband.add(conternt[1]);
		        	   }
		        	   
		        	   if(numbers&&place&&verb&&entity&&!aband.contains(conternt[1])&&!factsents.contains(conternt[1]+":"+score)){
		        		   	testsent.put(conternt[1], conternt[1]);
		        	   		factsents.add(conternt[1]+":"+score);
		        	   		sentTime.put(conternt[1], conternt[2]);
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
		ArrayList<String> fact = new ArrayList<>();	
		System.out.println("原來有:"+factsents.size()+"句事實");
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
			
    
			if(factsents.contains(sents.get(i))){
				Sentence factsent = new Sentence(c[0], terms);
    	   		factsent.setscore(Double.parseDouble(c[1])/maxscore);
				facts.add(factsent);
    	   		fact.add(sents.get(i));
			}
		}
		
		sents.removeAll(fact);
		
		System.out.println("後來Sent大小:"+sents.size());

		FileWriter fw = new FileWriter(".\\LDA\\PickSents.dat"); //將選出來的句子做LDA準備

		fw.write(sents.size()+"\r\n");
		
		HashMap<String, ArrayList<String>> timeline = new HashMap<String, ArrayList<String>>();
		for(int i =0; i<sents.size(); i++){
			
			String[] c = sents.get(i).split(":");
			String termsplit = files.get(c[0]);
        	String[] term = termsplit.split(" ");
        	String terms = "";
					
			if(sentTime.containsKey(c[0])){   //c[0] 為完整句子
				for(int j = 0; j<term.length;j++){
	        		
	        		if(term[j].length()>=2){ // 將一字單詞去除
	            		fw.write(term[j]+" ");
	                	fw.flush();
	                	terms = terms + term[j]+" ";
	            	}
	        	}
				
				Sentence picked = new Sentence(c[0], terms);
				
				String time = sentTime.get(c[0]);
				String subString = (String) time.subSequence(0, 6);
				
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
        	
        	fw.write("\r\n");
     	   	fw.flush();
        	System.out.println(sents.get(i));
        	
        	
        	
        }
/*		
		HashMap<Integer, HashMap<String, Double>> dateentro = new HashMap<>();
		HashMap<String, Double> temp = null;
		
		
		System.out.println("2015/09/27");
		fw1.write(group1.size()+"\r\n");
		for(int i = 0; i<group1.size();i++){
			
			String[] content = group1.get(i).split(":");
			fw1.write(content[0]);
			fw1.write("\r\n");
			fw1.flush();			
			System.out.println(group1.get(i));
		}
		daycount.add(group1.size());
		temp = new HashMap<>();
		temp = TimeAna.Entropy(group1, files);
		dateentro.put(1, temp);
		
		
		System.out.println("2015/09/28");
		fw2.write(group2.size()+"\r\n");
		for(int i = 0; i<group2.size();i++){
			String[] content = group2.get(i).split(":");
			fw2.write(content[0]);
			fw2.write("\r\n");
			fw2.flush();
			System.out.println(group2.get(i));
		}
		daycount.add(group2.size());
		temp = new HashMap<>();
		temp = TimeAna.Entropy(group2, files);
		dateentro.put(2, temp);
		
		System.out.println("2015/09/29");
		fw3.write(group3.size()+"\r\n");
		for(int i = 0; i<group3.size();i++){
			String[] content = group3.get(i).split(":");
			fw3.write(content[0]);
			fw3.write("\r\n");
			fw3.flush();
			System.out.println(group3.get(i));
		}
		daycount.add(group3.size());
		temp = new HashMap<>();
		temp = TimeAna.Entropy(group3, files);
		dateentro.put(3, temp);
		
		
		System.out.println("2015/09/30");
		fw4.write(group4.size()+"\r\n");
		for(int i = 0; i<group4.size();i++){
			String[] content = group4.get(i).split(":");
			fw4.write(content[0]);
			fw4.write("\r\n");
			fw4.flush();
			System.out.println(group4.get(i));
		}
		daycount.add(group4.size());
		temp = new HashMap<>();
		temp = TimeAna.Entropy(group4, files);
		dateentro.put(4, temp);
		
        
        fw.close();
        
       
        HashMap<String, Double> temp2 = new HashMap<>();
        for(int i = 1; i<=dateentro.size();i++){ //把每組的entropy加總
        	temp2 = dateentro.get(i);
        	
        	 Iterator iter = temp2.entrySet().iterator();  
             while(iter.hasNext()){
                 Map.Entry entry = (Map.Entry)iter.next();
                 String term = entry.getKey().toString();
                 if(finalentro.containsKey(term)){
                	 finalentro.put(term, finalentro.get(term)+temp2.get(term));
                 }else{
                	 finalentro.put(term, temp2.get(term));
                 }
             }
        }
/*        
        ArrayList<String> entropy = new ArrayList<>();
        System.out.println("最後Term Entropy:");
        Iterator iter1 = finalentro.entrySet().iterator();  
        while(iter1.hasNext()){
            Map.Entry entry = (Map.Entry)iter1.next();
            String term = entry.getKey().toString();
            //System.out.println(term+":"+finalentro.get(term));
            entropy.add(term+":"+finalentro.get(term));
        }
        
        Function.bubblesort(entropy);
        for(int i=0; i<entropy.size();i++){
        	System.out.println(entropy.get(i));
        }
*/        
   
        ArrayList<Sort> sortlist = new ArrayList<>();
        HashMap<Integer, Sentence> sentset = new HashMap<Integer, Sentence>();
    /*    
        ArrayList<Sort> sortlist1 = new ArrayList<>();
        ArrayList<Sort> sortlist2 = new ArrayList<>();
        ArrayList<Sort> sortlist3 = new ArrayList<>();
        ArrayList<Sort> sortlist4 = new ArrayList<>();
        HashMap<Integer, String> sentset1 = new HashMap<Integer, String>();
        HashMap<Integer, String> sentset2 = new HashMap<Integer, String>();
        HashMap<Integer, String> sentset3 = new HashMap<Integer, String>();
        HashMap<Integer, String> sentset4 = new HashMap<Integer, String>();
        int co1 = 0, co2 = 0, co3 = 0, co4 = 0;
      */  
        
        System.out.println("事實句子有:"+facts.size()+"句");
        for(int i =0; i<facts.size(); i++){
        	Sentence tempSent = facts.get(i);
        	String time = sentTime.get(tempSent.sentence);
			String subString = (String) time.subSequence(0, 6);			
			tempSent.setvec(Writevect.getvect(facts.get(i).terms, cent));
			tempSent.settime(subString);
	
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
        div.AllDiv(0.3,0.7, cent);
  
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
                    File newfile = new File(filepath + "\\" + flist[i]);
                    if(!newfile.isDirectory())
                    {
                        FileList.add(newfile.getAbsolutePath());
                    }
                    else if(newfile.isDirectory()) //if file is a directory, call ReadDirs
                    {
                        readDirs(filepath + "\\" + flist[i]);
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
