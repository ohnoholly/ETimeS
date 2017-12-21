package FileManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.cheyingwu.ckip.CKIP;
import tw.cheyingwu.ckip.Term;
import tw.cheyingwu.ckip.WordSegmentationService;

public class TimePicker {

	/**
	 * @param args
	 */
	
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	
	public static HashMap<String,String> getSentTime() throws Exception, IOException {
		// TODO Auto-generated method stub
		
		List<String> filelist = readDirs(".\\Data\\201509Typhoon event\\Tpost");
	
		
		HashMap<String,String> sentstime = new HashMap<>();
		
		for(String file : filelist){ //數PO文
	           String content = readFile(file);
	           String[] col = content.split(";");
	           String time = col[1];
	           
	           if(col.length==3){
	        	   String[] sens = col[2].split(":");
		           	 	          
		           for(int i=0; i<sens.length;i++){ // 數句子
		        	  
		        	   //System.out.println(sens[i]+":"+time);
		        	   sentstime.put(sens[i], time);
		        
		        	   
		           }
	           }
	          
	           
	
	    
		}
		return sentstime;
		

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
