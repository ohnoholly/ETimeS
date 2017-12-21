package Main;

import jgibblda.*;

public class LDAtest {

	/**
	 * @param args
	 */
	public static void LDAtrain(int k) {
		// TODO Auto-generated method stub
		
		LDACmdOption ldaOption = new LDACmdOption();   
        ldaOption.est = true;  
        ldaOption.estc = false;  
        ldaOption.dir = ".\\LDA";
        ldaOption.modelName = "model-final";  
        ldaOption.dfile = "PickSents.dat";  
        ldaOption.alpha = 0.5;  
        ldaOption.beta = 0.1;  
        ldaOption.K = k;  
        ldaOption.niters = 1000;  
        //topicNum = ldaOption.K;  
        Estimator estimator = new Estimator();  
        estimator.init(ldaOption);  
        estimator.estimate();  

		
		
	}

}
