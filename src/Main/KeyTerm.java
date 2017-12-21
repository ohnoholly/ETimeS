package Main;

public class KeyTerm {
	
	String term;
	float tfidf;
	float df;
	float centroi;
	
	
	public KeyTerm(String term){
		this.term = term;
	}
	
	public void settfidf(float tfidf){
		this.tfidf = tfidf;
	}
	
	public void setdf(float df){
		this.df = df;
	}
	
	public void setcentroi(float centroi){
		this.centroi = centroi;
	}

}
