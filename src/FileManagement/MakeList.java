package FileManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Main.tfidf;

public class MakeList {
	
	/***
	 * 將讀到的Term寫入單一文字檔中，當作List
	 */

	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		List<String> filelist = readDirs(".\\Data\\Verbtestfiles");
		FileWriter fw = new FileWriter(".\\List\\TyphVerblist.txt",false);
		HashMap<Integer, String> verb = new HashMap<Integer, String>();
		int id = 0;
		
		for(String file : filelist){
           String content = readFile(file);
           String[] verbs = content.split("\t");
           
           for(int i = 0; i<verbs.length; i++){
        	   if(verb.containsValue(verbs[i])){
        		   
        	   }else{
        		   verb.put(id, verbs[i]);
        		   id++;
        	   }
           }
           
        }
		
		for(int i = 0; i<=verb.size(); i++){
			fw.write(verb.get(i)+"\r\n");
			fw.flush();
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
        	
        	String[] linea = line.split(";");
        	if(linea.length !=0){
        		strSb.append(linea[0]);
	        	
        	}
        	
        	
            line = br.readLine();    
        }
        
        return strSb.toString();
    }

}
