package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Function {
	
	    
    public static boolean IsConvergence(float [] t1,float [] t2){ //判斷是否收斂的方法
		boolean convergence=true;
		ArrayList <float[]> CumRFList = new ArrayList <float[]>();
		
		for (int i=0;i<t1.length;i++){
			float [] temp = {t1[i],t2[i]};
			CumRFList.add(temp);
		}
		Comparator<float[]> comparator = new Comparator<float[]>(){
			public int compare(float[] f1,float[] f2){
				if (f2[0] > f1[0]) return 1;
				else if (f2[0] < f1[0]) return -1;
				else return 0;
			}
		};
		Collections.sort(CumRFList,comparator);
		for (int i=0;i<CumRFList.size()-1;i++){
			
			if (CumRFList.get(i)[1]<CumRFList.get(i+1)[1]){
				convergence=false;
			}
		}
		
		return convergence;
	}

    
    public static float ComputeRF(float a,float b){
    	float CumRF = 0;
		float RF=0;
			
			if (a==0){
				a= (float) 0.01;
			}
			if (b!=0){
				RF=a/b; //a的tf值 / b的tf值
			}
			else{
				RF=a;
			}
			CumRF+=RF;
				
		return CumRF;
    }
    
    public static double log2(double lift) {
		return (float) (Math.log(lift) / Math.log(2));
	}
    
    public static void Swap(ArrayList<String> ds, int indexA, int indexB) 
    {
        String tmp = ds.get(indexA);
        ds.set(indexA, ds.get(indexB));
        ds.set(indexB, tmp);
    }
    
    public static ArrayList<String> bubblesort(ArrayList<String> input){
		
    	for(int k = input.size()-1; k>0; k--){
        	for(int j = 0; j<k; j++){
        		String[] content = input.get(j).split(":");
				String[] content2 = input.get(j+1).split(":");
				double v = Double.parseDouble(content[1]);
				double v2 = Double.parseDouble(content2[1]);
				if (v < v2){
					 Swap(input, j, j + 1);
				 }	
        	}
        }
    	
    	return input;
    	
    }
    
    public static void SwapSentence(ArrayList<Sentence> ds, int indexA, int indexB) 
    {
        Sentence tmp = ds.get(indexA);
        ds.set(indexA, ds.get(indexB));
        ds.set(indexB, tmp);
    }
    
    public static ArrayList<Sentence> bubblesortscore(ArrayList<Sentence> input){
		
    	for(int k = input.size()-1; k>0; k--){
        	for(int j = 0; j<k; j++){
        		
				double v = input.get(j).score;
				double v2 = input.get(j+1).score;
				if (v < v2){
					 SwapSentence(input, j, j + 1);
				 }	
        	}
        }
    	
    	return input;
    	
    }
    
    public static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null; 
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
    
    public static double cosineSimilarity(double[] docVector1, double[] docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        if(docVector1 != null && docVector2 != null){
        	        
	        for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
	        {
	            dotProduct += docVector1[i] * docVector2[i];  
	            magnitude1 += Math.pow(docVector1[i], 2);  //(a^2)
	            magnitude2 += Math.pow(docVector2[i], 2); //(b^2)
	        }
	
	        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
	        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)
	
	        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
	            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
	        } else {
	            return 0.0;
	        }
        }else{
        	cosineSimilarity = 0.0;
        }
        
        return cosineSimilarity;
    }

}
