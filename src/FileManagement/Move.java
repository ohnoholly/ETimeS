package FileManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;


public class Move {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new FileReader("160928RandomPickSent.txt"));
        int c = 1;
		while(br.ready()){
        	String content= br.readLine();				
			moveFile(content, "Data\\SentExperiment\\160928Test\\test("+c+").txt");
			c++;
        }
        br.close();

	}
	
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		//delFile(oldPath);

		}
	
	
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //檔存在時
				InputStream inStream = new FileInputStream(oldPath);//讀入原檔
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //位元組數 檔案大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
			}
			catch(Exception e) {
				System.out.println("複製單個檔操作出錯");
				e.printStackTrace();
		 

			}
		 

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
