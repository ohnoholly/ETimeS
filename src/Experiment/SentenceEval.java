package Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.spec.ECFieldF2m;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weka.core.parser.java_cup.internal_error;

public class SentenceEval {

	/**
	 * @param args
	 */
	
	private static HashMap<String, String> fPContain = new HashMap<>(); //取出FP與知後的數值比較
	
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	
	
	
	
	public static HashMap<String, String> getFP(){
		
		return fPContain;
	}
	
	
	public static void evaluate(int termn,HashMap<String, String> testsent, HashMap<String, String> aband) throws Exception{
		// TODO Auto-generated method stub
		System.out.println("開始測試");
		 BufferedReader br = new BufferedReader(new FileReader(".\\Data\\ExperimentSent160116.txt"));
	     HashMap<String, Integer> ground = new HashMap<>();  
	     FileWriter fw = new FileWriter("Step1ExperimentResult.txt",true); 
	     
	     
		 while(br.ready()){
	        	String content= br.readLine();
	        	//System.out.println(content);
				String[] split = content.split(";");
				int label = 0;
				if(split.length>1){
					label = Integer.parseInt(split[0]);
					ground.put(split[1], label);
				}
				
	        }
	        
		 	br.close();
		 	System.out.println("共有:"+ground.size());
		 	
		 	
	        HashMap<String, Integer> predict = new HashMap<>();
	        int postive = 0;
	        int negative = 0;
	        int tp = 0;
	        int fp = 0;
	        int tn = 0;
	        int fn = 0;
	        List<String> filelist = readDirs(".\\Data\\SentExperiment\\160116Test");
	        for(String file : filelist){ //數PO文
		           String content = readFile(file);
		           
		           String[] sp = content.split(";");
		           
		           if(sp.length>1){
		        	   //System.out.println(sp[1]);
		        	   if(sp[1].length()>=25&&sp[1].length()<=70){
		        		  
		        		   if(testsent.containsKey(sp[1])){
			        		   
			        		   predict.put(sp[1], 1);
			        		   postive++;
			        		   if(ground.containsKey(sp[1])){
			        			   if(ground.get(sp[1])==1){
				        			   tp++;
				        			   //System.out.println("TP:"+sp[1]);
				        		   }else{
				        			   fp++;
				        			   fPContain.put(sp[1], sp[1]);
				        			   //System.out.println("FP:"+sp[1]);
				        		   }
			        		   }
			        		   
			        	   }else{
			        		   predict.put(sp[1], 0);
			        		   negative++;
			        		   if(ground.containsKey(sp[1])){
			        			   if(ground.get(sp[1])==1){
				        			   fn++;
				        			   System.out.println("FN:"+sp[1]);
				        		   }else{
				        			   tn++;
				        			   //System.out.println("TN:"+sp[1]);
				        		   }
			        		   }
			        		  
			        	   }
		        		   
		        	   }
		        	
		           }
		  
			}
	        System.out.println("Postive有"+postive+", Negative有"+negative);
	        System.out.println("TP:"+tp+",FP:"+fp+",TN:"+tn+",FN:"+fn);
	        fw.write("Term number:"+termn+"\r\n");
	        fw.write("TP:"+tp+",FP:"+fp+",TN:"+tn+",FN:"+fn+"\r\n");
	        fw.flush();
	        fw.close();
		
	}
	
	
	public static void writedata() throws Exception{

		List<String> filelist = readDirs(".\\Data\\Test");
		FileWriter fw = new FileWriter("TestSentence.txt"); 
		 for(String file : filelist){ //數PO文
	           String content = readFile(file);
	           String[] sp = content.split(";");
	
	           if(sp.length>1){
	        	  fw.write(sp[1]+"\r\n");
	        	  fw.flush();
	           }
	  
		}
		 fw.close();
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
