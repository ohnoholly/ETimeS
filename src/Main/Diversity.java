package Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Word2Vec.Writevect;

/**
 * 使用Diversity方法：
 * int id ;
 * double point ;
 * ArrayList<String> inputList ;
 * ArrayList<Sort> sortList = new ArrayList<Sort>(); 
 * for(int i= 0; i<500 ; ++i){
 * 		Sort s = new Sort(id, point, inputList);
 * 		sortList.add(s);
 * }
 * Diversity div = new Diversity(sortList); 
 * //執行AllDiv(double weight_point, double weight_div);可以做Diversity運算 weight_point是分數的比重,weight_div是差異化的比重
 * div.AllDiv(double weight_point, double weight_div);
 *  //拿到前三名的ID
		 for(int i= 0 ; i<3 ; i++){
			 System.out.println(div.sortByDivList.get(i).id);
		 }
 * 
 * @author finalily
 *
 */
public class Diversity {

	ArrayList<Sort> sortList = new ArrayList<Sort>(); // 宣告動態陣列,還沒排序		
	ArrayList<Sort> sortByPointList = new ArrayList<Sort>(); // 宣告動態陣列,用分數做排序		
	ArrayList<Sort> sortByDivList = new ArrayList<Sort>(); // 宣告動態陣列,把差異化分數加進去做排序		
	
	public Diversity(ArrayList<Sort> sortList)
	{
		this.sortList = sortList;
	}
	
	//算兩兩的div分數值
	public double Div(int id_1, ArrayList<String> inputList_1,int id_2, ArrayList<String> inputList_2 ){
		double small = 0 ;
		double big = 0 ;
		double intersection = 0;
		double union = 0 ;
		double div = 0 ;
		if(inputList_1.size()>inputList_2.size())
		{
			big = inputList_1.size();
			small = inputList_2.size();
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(inputList_2.get(i).equals(inputList_1.get(j))){
						intersection += 1;
					}
				}
			}
		}else
		{
			big = inputList_2.size();
			small = inputList_1.size();
			for(int i=0 ; i<small ; i++)
			{
				for(int j=i ; j<big ;j++)
				{
					if(inputList_1.get(i).equals(inputList_2.get(j))){
						intersection += 1;
					}
				}
			}
		}
		
		union = big + small - intersection;
		
//		System.out.println(big+"+"+small+"-"+intersection + "=" + union);
		div = (intersection/union);
		div = 1-div ;
		return div ;
	}
	
	
	public double cosine(ArrayList<String> inputList_1,ArrayList<String> inputList_2, HashMap<String, Float> cent) throws IOException{
		String terms_1 = "";
		for(int i = 0; i<inputList_1.size();i++){
			
			terms_1 = terms_1 + inputList_1.get(i)+" ";
		}
		
		String terms_2 = "";
		for(int i = 0; i<inputList_2.size();i++){
			
			terms_2 = terms_2 + inputList_2.get(i)+" ";
		}
		
		float[] vector_1 = Writevect.getvect(terms_1, cent);
		float[] vector_2 = Writevect.getvect(terms_2, cent);
		
		double cosine = Function.cosineSimilarity(Function.convertFloatsToDoubles(vector_1),Function.convertFloatsToDoubles(vector_2));
		//System.out.println("cosine:"+cosine);
		
		return  cosine;
		
	}
	
	//計算全部的div,並把最小的存放到sortByDivList
	public void AllDiv(double weight_point, double weight_div, HashMap<String, Float> cent) throws IOException{
		//weight_point = 0.5  ;
		//weight_div = 0.5 ;
		sortByPointList = sortList;
		for(int i =0 ; i<sortByPointList.size()-1 ; i++)
		{
			for(int j=0 ; j<(sortByPointList.size()-i-1) ;  ++j)
			{
			
				Sort temp;
				if(sortByPointList.get(j).point < sortByPointList.get(j+1).point){
					temp = sortByPointList.get(j) ;
					sortByPointList.set(j, sortByPointList.get(j+1));
					sortByPointList.set(j+1, temp);
				}
			}
		}
		
		sortByDivList.add(sortByPointList.get(0));
		sortByPointList.remove(0);

		
		while(!sortByPointList.isEmpty()){
			double smallest_div = 0;//用來存放最小可能的div
			int smallest_index = 10000 ; 
			
			//System.out.println("END\n");
			if(sortByPointList.size() != 1){
				for(int i=0 ; i<sortByPointList.size() ;i++){
					
					double avgdiv = 0.0;
					for(int j= 0 ; j<sortByDivList.size();j++){
						
						double div = Div(j, sortByDivList.get(j).inputList,i, sortByPointList.get(i).inputList );
						//double cosines = cosine(sortByDivList.get(j).inputList, sortByPointList.get(i).inputList, cent);
						
						div = sortByPointList.get(i).point*weight_point+div*weight_div ;
						//double div = (sortByPointList.get(i).point*weight_point) + (weight_div*(1/cosines));
						avgdiv = avgdiv + div;
						
					}
					avgdiv = avgdiv/sortByDivList.size(); //取得平均
					if(avgdiv >= smallest_div){
						System.out.println(avgdiv+">"+smallest_div);
						smallest_div = avgdiv;
						smallest_index = i ;//sortByPointList.get(i).id ;
					}
				}
				
				sortByDivList.add(sortByPointList.get(smallest_index));
				sortByPointList.remove(sortByPointList.get(smallest_index));
			}else{
				sortByDivList.add(sortByPointList.get(0));
				sortByPointList.remove(sortByPointList.get(0));
				
			}
			
			
		}
		
		
		
	}
	

}
