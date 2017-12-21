package FileManagement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RandomPick {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		FileWriter fw = new FileWriter("160928RandomPickSent.txt");
		
		int countsent = 0;
		ArrayList<Integer> hole = new ArrayList<>();
		
		
		while(countsent != 1000){
			int randomnum = (int)(Math.random()*6482+1);
			if(hole.contains(randomnum)){
				System.out.println("重複數字");
				continue;
			}else{
				fw.write("F:\\Code\\DisasterKey\\Data\\20160928(Long)\\Sentence\\pttsent ("+randomnum+").txt\r\n");
				fw.flush();
				hole.add(randomnum);
				countsent++;
			}
		}
		
		fw.close();
		
	}
}
