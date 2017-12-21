package Main;


public class Sentence {
	
	String sentence;
	String terms;
	double score;
	double similarity;
	double pc = 0.0;
	double pcs = 0.0;
	String time = null;
	float[] sentvec;
	int aspectid;
	

	public Sentence(String sentence,String terms) {
		
		this.sentence = sentence;
		this.terms = terms;
		
	}
	
	public void setscore(Double score){
		this.score = score;
	}
	
	public void setpc(Double pc){
		this.pc = pc;
	}
	
	public void setpcs(Double pcs){
		this.pcs = pcs;
	}
	
	public void settime(String time){
		this.time = time;
	}
	
	public void setvec(float[] vector){
		this.sentvec = vector;
	}
	
	public void setaspectid(int aspectid){
		this.aspectid = aspectid;
	}
	
	public void setsim(Double sim){
		this.similarity = sim;
	}
	

}
