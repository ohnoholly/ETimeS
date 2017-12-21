package FileManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Delete {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new FileReader("Summary.txt"));
        while(br.ready()){
        	String content= br.readLine();				
			delFile("all//"+content);
        }
        br.close();
	

	}
	
	public static void delFile(String filePathAndName){
		try {
		String filePath = filePathAndName;
		filePath = filePath.toString();
		java.io.File myDelFile = new java.io.File(filePath);
		myDelFile.delete();
		 

		}
		catch(Exception e) {
		System.out.println("刪除檔操作出錯");
		e.printStackTrace();
		 

		}
		 

		}

}
