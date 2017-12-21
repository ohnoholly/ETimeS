package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import Word2Vec.*;
import FileManagement.TimePicker;
import Experiment.SentenceEval;

import org.json.JSONException;


public class main {
	
	   private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	    static HashMap<String, Float> dflist = new HashMap<String, Float>();
	    static HashMap<String,String> stopwordHash = new HashMap<String, String>();
	    static HashMap<String,String> summary = new HashMap<String, String>();
	    static String query = "20160206";
	
	public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String file = "Data/"+query+"/Long/PostingData";
        //HashMap<String,String> dwordHash = new HashMap<String, String>();
        HashMap<String,String> VerbHash = new HashMap<String, String>();
        HashMap<String,String> placeHash = new HashMap<String, String>();
        HashMap<String,String> timeHash = new HashMap<String, String>();
        
        HashMap<String, Float> cent = new HashMap<>();
       	        
       
        BufferedReader br = new BufferedReader(new FileReader("./List/Region.txt"));
        while(br.ready()){
        	String content= br.readLine();				
			placeHash.put(content, content);
        }
        br.close();
        BufferedReader br1 = new BufferedReader(new FileReader("./List/Stopword.txt"));
        while(br1.ready()){
        	String content= br1.readLine();
			stopwordHash.put(content, content);
        }
        br1.close();
      
        BufferedReader br3 = new BufferedReader(new FileReader("./List/Verblist.txt"));
        while(br3.ready()){
        	String content= br3.readLine();				
			VerbHash.put(content, content);
        }
        br3.close();
        
        BufferedReader br4 = new BufferedReader(new FileReader("./List/Timelist.txt"));
        while(br4.ready()){
        	String content= br4.readLine();				
			timeHash.put(content, content);
        }
        br4.close();
      
       tfidf ti = new tfidf(FileList, summary, stopwordHash); 

       HashMap<String,HashMap<String, Float>> all_tf = tfidf.tfAllFiles(file); //算tf
       //tfidf.Findplacepost(all_tf, placeHash); //分類地名發文
       HashMap<String, Float> idfs = tfidf.idf(all_tf, dflist); //算tfidf     
       Measure me = new Measure(FileList, all_tf);
       me.process();
       System.out.println("共有多少個Term:"+dflist.size());
       
       cent = Centroi.centroi(FileList,dflist,all_tf);
     
      
  /*     //設定要選出的term數量,將這些term變成向量\    
       int thrshold = 100; 
       for(int j = 0; j<5; j++){
    	   
    	   ArrayList<String> pickterm = new ArrayList<>();
           pickterm = Centroi.getresult();
           System.out.println("pickterm size ="+pickterm.size());
           HashMap<String, String> top = new HashMap<String,String>(); 
           int count = 0;
           for(int i = 0; i<pickterm.size(); i++){
        	   String r = pickterm.get(i);
        	   String[] gterm = r.split(":");
        	   if(!placeHash.containsKey(gterm[0])&&gterm[0].length()!=1&&!timeHash.containsKey(gterm[0])){
        		   top.put(gterm[0], gterm[0]);
        		   //System.out.println(gterm[0]);
        		   count++;
        	   }
        	   
        	   
        	   if(count >=thrshold){
        		   System.out.println(count);
        		   
        		   break;
        	   }
        	   
           }
           System.out.println("Topterm 有:"+top.size()+"個"); 
           
           Writevect.writevect(top, top.size()); //將從Cen選出來的terms算出向量
            
            thrshold = thrshold + 100;
       }
   */    
      
  //實驗版本 實驗要選出多少term有較佳的效果
  /*
       int j = 100;
       for(int i = 0; i<5;i++){
    	   ArrayList<String> topterm = new ArrayList<>();
           ArrayList<String> minterm = new ArrayList<>();
           Kmean km = new Kmean(query,j);
           topterm = km.getmaxterm();
           minterm = km.getminterm();
           System.out.println("選出的topterm:");
           System.out.println("Maxterm:"+topterm);
           System.out.println("Minterm:"+minterm);
           
           
                
           HashMap<String, String> fakesentTime = new HashMap<>();
           ArrayList<Sentence> factsents = new ArrayList<>();       
           HashMap<Integer, Sentence> sents = new HashMap<>();
           PickSentChange picker = new PickSentChange(query,topterm,minterm, VerbHash, placeHash,timeHash, cent, fakesentTime);
           sents = picker.getsentence(); //選句子
           factsents = picker.getfactsent(); //取出算過Diversity的句子
           
           HashMap<String, String> testsent = new HashMap<>();
           HashMap<String, String> abandsent = new HashMap<>();
           testsent = picker.gettest();
           abandsent = picker.getaband();
           
           SentenceEval.evaluate(j,testsent, abandsent);
           
           j = j + 100;
           
       }
     */  
     
       
       ArrayList<String> topterm = new ArrayList<>();
       ArrayList<String> minterm = new ArrayList<>();
       Kmean km = new Kmean(query, 100); 
       topterm = km.getmaxterm();
       minterm = km.getminterm();
       System.out.println("選出的topterm:");
       System.out.println("Maxterm:"+topterm);
       System.out.println("Minterm:"+minterm);
      
      
       //System.out.println("DFlist:"+dflist);
       
       
       
      
       //ArrayList<String> dfvalue = ti.getdfvalue();
    
      
  /*     
       ArrayList<String> KLD = new ArrayList<>();
       KLD = Measure.getKLD(top);
       top.clear();
       for(int i = 0; i<KLD.size(); i++){
    	   String r = KLD.get(i);
    	   String[] gterm = r.split(":");
    	   top.put(gterm[0], gterm[0]);    	   
       }
 */     
      
     
       //ArrayList<String> candidate = W2VDistance.WordDistance(topterm, dflist); //擴展
             
                 
       HashMap<String, String> sentTime = new HashMap<>();     
       HashMap<String, String> fakesentTime = new HashMap<>();
       ArrayList<Sentence> factsents = new ArrayList<>();       
       HashMap<Integer, Sentence> sents = new HashMap<>();
       PickSentChange picker = new PickSentChange(query,topterm,minterm, VerbHash, placeHash,timeHash, cent, fakesentTime);
       sents = picker.getsentence(); //選句子
       factsents = picker.getfactsent(); //取出算過Diversity的句子
   
	   //SentenceAspect.SentAspect(dflist, factsents, sents); //BM25
       
	  
        
       /**
	    * 評測挑選句子效果
	    */
 
       
       
       HashMap<String, String> fPContain = new HashMap<>();//存放當初還留下不好的句子 
 /*      
       HashMap<String, String> testsent = new HashMap<>();
       HashMap<String, String> abandsent = new HashMap<>();
       testsent = picker.gettest();
       abandsent = picker.getaband();
       SentenceEval.evaluate(100,testsent, abandsent);
       fPContain = SentenceEval.getFP();
       
/*       //實驗參數
       double ws = 0.1, ss = 0.9;
       for(int i = 0; i<10; i++){
    	   SentenceAspect.SentCosineChange(ws, ss, factsents, sents,cent, fPContain);
    	   ws = ws+0.1;
    	   ss = ss- 0.1;
       
       }
 */      
       
    	   SentenceAspect.SentCosineChange(0.5,0.3, 0.7, factsents, sents,cent, fPContain);
    	   
		
       
       
      
       
       
 /*     
       entropy = picker.getentropy(); //取出每個term的entropy
       daycount = picker.getdays();//取出一天句子數
      
       LDAtest.LDAtrain(30); //做LDA training
       LDAEstimate.LDApro();
       //System.out.println(sents);
       
       
       LDAEstimate.postclassify(sents, fakesentTime, daycount,dflist);
       System.out.println("DFLIST:"+dflist);
       LDAEstimate.semanticsim(dflist, factsents);
       //LDAEstimate.EntroPwc(entropy);
      
   
  /*
   	  //-----算Apriori 關連規則  
       try {
		Apriori.Apriori(summary);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   */
       
       //HashMap<String, Float> avetf = tfidf.Averagetfidf(all_tf); //取得平均之tf值
       
       //HashMap<String, Float> avetfidf = tfidf.tf_idf(all_tf, idfs); //取得平均之tfidf值
       
       //tfidf.features(dwordHash,dflist, cent, avetf, placeHash); //產生features
       //Rule.supportconf(4026,2,placeHash, FileList,all_tf, 20,10, VerbHash); //暴力法關聯規則
       
       //Summary.LocalSum(cent,summary,"宜蘭"); //算摘要
        
    }
	
}
