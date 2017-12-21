package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Manage {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		HashMap<Integer, String> terMap = new HashMap<Integer, String>();
		HashMap<Integer, Float> svmMap = new HashMap<Integer, Float>();
		 
		
		BufferedReader br = new BufferedReader(new FileReader(".\\SVR\\testterm.txt"));
	    int index = 0;    
		while(br.ready()){
	        	String content= br.readLine();				
				terMap.put(index, content);
				index++;
	    }
	    br.close();
	    BufferedReader br1 = new BufferedReader(new FileReader(".\\SVR\\result.txt"));
	    index = 0;
	    while(br1.ready()){
	        	String content= br1.readLine();
	        	Float r = Float.parseFloat(content);
				svmMap.put(index, r);
				index++;
	    }
	    br1.close();
	    
	    System.out.println("term size:"+terMap.size()+" r size:"+svmMap.size());
	    
	    ArrayList<String> svmr = new ArrayList<String>();
	    for(int i=0; i<terMap.size(); i++){
	    	String t = terMap.get(i);
	    	Float s = svmMap.get(i);
	    	svmr.add(t+":"+s);
	    	
	    }
	    
	    Function.bubblesort(svmr);
	    
	    for(int i=0; i< svmr.size(); i++){
	    	 System.out.println(svmr.get(i));
	    }

	}

}
