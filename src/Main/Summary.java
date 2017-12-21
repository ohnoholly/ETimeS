package Main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Summary {
	
	public static void Sum(ArrayList<String> cent, HashMap<String, String> summary) throws IOException{
		
		Iterator iter = summary.entrySet().iterator();
		HashMap<Integer, ArrayList<String>> termset = new HashMap<Integer, ArrayList<String>>();
		HashMap<Integer, String> sentset = new HashMap<Integer, String>();
		int c = 0;
		while (iter.hasNext()) {
			  Map.Entry entry = (Map.Entry)iter.next();
			  ArrayList<String> terms = new ArrayList<String>();
              String word = entry.getKey().toString();
              String[] ter = word.split("\t");
              for(int i = 0; i<ter.length; i++){
            	  terms.add(ter[i]);
              }
              termset.put(c, terms); //放入每篇PO文的term
              String sent = entry.getValue().toString();
              sentset.put(c, sent); //放入每篇PO文的句子
              c++;
              
		}
		HashMap<String, Double> centers = new HashMap<String, Double>();
		centers = centor(cent, 20);
		ArrayList<String> ranks = new ArrayList<String>();
		
		for(int i = 0; i<termset.size(); i++){
			double score = 0.0;
			ArrayList<String> te = termset.get(i);
			for(int j = 0; j<te.size();j++){
				if(centers.containsKey(te.get(j))){
					score = score + centers.get(te.get(j));
				}
				
				
			}
			ranks.add(i+":"+score);
		}
		
		double point ;
		ArrayList<String> inputList ;
		ArrayList<Sort> sortList = new ArrayList<Sort>(); 
		Iterator iters = termset.entrySet().iterator();
		while(iters.hasNext()){
			 Map.Entry entry = (Map.Entry)iters.next();
             int id = Integer.parseInt(entry.getKey().toString());
             String temp = ranks.get(id);
             String[] con = temp.split(":");
             point = Double.parseDouble(con[1]);
             inputList = termset.get(id);
             Sort s = new Sort(id, point, inputList);
             sortList.add(s);
			
		}

		  Diversity div = new Diversity(sortList); 
		  //執行AllDiv(double weight_point, double weight_div);可以做Diversity運算 weight_point是分數的比重,weight_div是差異化的比重
		  //div.AllDiv(1,1);
		  String summarys = null;
				 for(int i= 0 ; i<3 ; i++){
					 int ids = div.sortByDivList.get(i).id;
					 summarys = summarys+"\r\n" + sentset.get(ids);
				 }
		
				 System.out.println(summarys);
				 FileWriter fw = new FileWriter("Summary.txt",false);
				fw.write("Summary:\r\n"+summarys);
				fw.flush();
				fw.close();
		
		
		
	}
	
public static void LocalSum(ArrayList<String> cent, HashMap<String, String> summary, String local) throws IOException{
		
		Iterator iter = summary.entrySet().iterator();
		HashMap<Integer, ArrayList<String>> termset = new HashMap<Integer, ArrayList<String>>();
		HashMap<Integer, String> sentset = new HashMap<Integer, String>();
		int c = 0;
		while (iter.hasNext()) {
			  Map.Entry entry = (Map.Entry)iter.next();
			  ArrayList<String> terms = new ArrayList<String>();
              String word = entry.getKey().toString();
              String[] ter = word.split("\t");
              for(int i = 0; i<ter.length; i++){
            	  terms.add(ter[i]);
              }
              termset.put(c, terms); //放入每篇PO文的term
              String sent = entry.getValue().toString();
              sentset.put(c, sent); //放入每篇PO文的句子
              c++;
              
		}
		HashMap<String, Double> centers = new HashMap<String, Double>();
		centers = centor(cent, 20);
		ArrayList<String> ranks = new ArrayList<String>();
		
		for(int i = 0; i<termset.size(); i++){
			double score = 0.0;
			ArrayList<String> te = termset.get(i);
			for(int j = 0; j<te.size();j++){
				if(centers.containsKey(te.get(j))){
					score = score + centers.get(te.get(j));
				}
				
				if(te.get(j).equals(local)){
					score = score + 2000.0;
				}
				
				
			}
			ranks.add(i+":"+score);
		}
		
		ranks = Function.bubblesort(ranks);
		Swrite(ranks, 3, sentset);
		
		
	}
	
	public static void Swrite(ArrayList<String> score, Integer n, HashMap<Integer, String> sentset) throws IOException{
		String sums = null;
		for(int i = 0; i<n; i++){
			String content = score.get(i);
			String[] post = content.split(":");
			
			sums = sums+"\r\n"+sentset.get(Integer.parseInt(post[0]));
			
		}
		System.out.println("Summary:"+ sums);
		FileWriter fw = new FileWriter("Summary.txt",false);
		fw.write("Summary:\r\n"+sums);
		fw.flush();
		fw.close();
	}
	
	
	public static HashMap<String, Double> centor(ArrayList<String> temp, Integer rank){
		
		HashMap<String, Double> center = new HashMap<String, Double>();
		
		for(int i=0; i<rank; i++){
			String words = temp.get(i);
			String[] word = words.split(":");
			double rankscore = Double.parseDouble(word[1])*(rank-i);
			center.put(word[0], rankscore);
		}
		
		return center;
		
	}
}
