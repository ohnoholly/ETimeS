package Main;

import java.util.ArrayList;
/**
 * 使用Sort方法：
 * int id ;//ID
 * double point;//計算後的分數
 * ArrayList<String> inputList ;//斷詞List
 * Sort s = new Sort(id, point, inputList);
 * @author finalily
 *
 */

public class Sort {
	int id;
	double point;
	ArrayList<String> inputList = new ArrayList<String>(); // 宣告動態陣列		
	
	public Sort(int id, double point, ArrayList<String> inputList)
	{
		this.id = id ;
		this.point = point ;
		this.inputList = inputList;
	}
}
